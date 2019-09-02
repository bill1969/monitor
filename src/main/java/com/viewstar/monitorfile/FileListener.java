package com.viewstar.monitorfile;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

public class FileListener extends FileAlterationListenerAdaptor { // 声明业务服务
     @Autowired
     private ListenerService listenerService; // 采用构造函数注入服务
     public FileListener(ListenerService listenerService) { this.listenerService = listenerService; } // 文件创建执行
     @Override public void onFileCreate(File file) {
          System.out.println("File Create!");
          listenerService.doSomething(file);
     } // 文件创建修改
     @Override public void onFileChange(File file) { // 触发业务
          listenerService.doSomething(file);
           } // 文件创建删除
     @Override public void onFileDelete(File file) { } // 目录创建
     @Override public void onDirectoryCreate(File directory) { } // 目录修改
     @Override public void onDirectoryChange(File directory) { } // 目录删除
     @Override public void onDirectoryDelete(File directory) { } // 轮询开始
     @Override public void onStart(FileAlterationObserver observer) { } // 轮询结束
     @Override public void onStop(FileAlterationObserver observer) { }
}
