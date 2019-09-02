package com.viewstar.monitorfile;//import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Thread.sleep;
import static jdk.nashorn.internal.objects.NativeString.indexOf;

@Service
public class ListenerService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private ActionLogDao actionLogDao;
    @Value("${monitor.dir}")
    private  String monitorDir ;
    @Value("${monitor.file}")
    private  String monitorFile;
    @Value("${monitor.backup}")
    private  String monitorBackup;
    @Value("${syncUrl}")
    private String syncUrl;
    @Value("${carrier}")
    private String carrier;
    @Value("${interval}")
    private int interval;
    @Value("${saveInDB}")
    private boolean saveInDB;

    public void doSomething(File file){
        readFileByLines(file.toString());
    }
    public void readFileByLines(String fileName) {

        File file = new File(fileName);
        BufferedReader reader = null;
        String prefix="SyncUserinfo";
        try {
            //System.out.println("以行为单位读取文件内容，一次读一整行：");
            InputStreamReader ir = new InputStreamReader(new FileInputStream(fileName), "GBK");
            reader = new BufferedReader(ir);
            String tempString = null;
            JSONObject str =null;
            JSONObject params = new JSONObject();

            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                System.out.println(tempString);
                String[] fields =null;
                if (tempString.contains(","))
                    fields=tempString.split("\\,");
                if (tempString.contains("|"))
                    fields=tempString.split("\\|");
                if (fields.length<6) {
                    System.out.println("fields is less!");
                    return;
                }

                params.put("spid",carrier);
                params.put("userid",fields[0]);
                SimpleDateFormat sDateFormat=new SimpleDateFormat("yyyyMMddHHmmss");
                params.put("activeTime",sDateFormat.parse(fields[1]));
                params.put("carrier",1);
                params.put("region",fields[2]);
                params.put("epgGroup",fields[3]);
                params.put("updateTime",sDateFormat.parse(fields[4]));
                if ("F0A".equals(fields[5]))
                    params.put("state",1);
                else if ("F0B,F0J,F0K,5".contains(fields[5]))
                    params.put("state",2);
                else if ("F0U,F0X,99".contains(fields[5]))
                    params.put("state",4);
                else if ("0".contains(fields[5]))
                    params.put("state",0);

                str = doPost(syncUrl, params);
                System.out.println(str);
                User user= (User) userDao.getUserByUserID(params.get("userid").toString());
                if (user==null)
                    user = new User();
                user.setUserid(fields[0]);
                user.setEpgGroup(fields[3]);
                user.setRegion(fields[2]);
                if ("F0A".equals(fields[5]))
                    user.setState(1);
                else if ("F0B,F0J,F0K".contains(fields[5]))
                    user.setState(2);
                else if ("F0U,F0X".contains(fields[5]))
                    user.setState(4);
                user.setSPID(carrier);
                user.setUpdateTime(sDateFormat.parse(fields[4]));
                user.setActiveTime(sDateFormat.parse(fields[1]));
                userDao.save(user);
                if (saveInDB){
                    ActionLog log = new ActionLog();
                    log.setActionID(0);  //10-regist 11-update employee info 12-delete whitelist
                    log.setUserID(fields[0]);
                    log.setResult(str.get("status").toString());
                    log.setOptime(new java.sql.Date(new Date().getTime()));
                    log.setOperator("huawei");
                    actionLogDao.save(log);
                }
                line++;
                if ((interval>1000)||(interval<100))
                    sleep(300);
                else
                    sleep(interval);
                //System.out.println("sleep 1 second");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        Date date=new Date();
        SimpleDateFormat df=new SimpleDateFormat("yyyyMMddhhmmss");

        File newFile = new File(monitorBackup+file.getName());
        File fileParent = newFile.getParentFile();
        if ( !fileParent.exists()){//若此目录不存在，则创建之
            System.out.println(fileParent.mkdirs());
            System.out.println("创建文件夹路径为："+ monitorBackup);
        }
        if (!file.renameTo(newFile))
            System.out.println("备份文件失败!"+newFile.toString());

    }
    public static JSONObject doPost(String url, JSONObject json){  //, String infoname, Class cl

        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        JSONObject response = null;
        try {
            StringEntity s = new StringEntity(json.toString());
            s.setContentEncoding("UTF-8");
            s.setContentType("application/json");//发送json数据需要设置contentType
            post.setEntity(s);
            HttpResponse res = httpclient.execute(post);
            String result = EntityUtils.toString(res.getEntity());// 返回json格式：
            response = JSONObject.parseObject(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return response;
    }
}
