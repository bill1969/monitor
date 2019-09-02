package com.viewstar.monitorfile;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class FileListenerRunner implements CommandLineRunner {
    @Autowired
    private FileListenerFactory fileListenerFactory;
    @Override
    public void run(String... args) throws Exception { // 创建监听者
        FileAlterationMonitor fileAlterationMonitor = fileListenerFactory.getMonitor();

        try { // do not stop this thread
            System.out.println("Monitor Start...");
            fileAlterationMonitor.start();

        } catch (Exception e) {
            System.out.println("Monitor Start Failed...");
            e.printStackTrace(); }
    }
}
