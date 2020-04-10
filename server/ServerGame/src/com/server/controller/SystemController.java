/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.server.controller;

import com.server.model.Command;
import com.server.service.CommonService;
import com.server.service.HandleCommandFactory;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author tuhalang
 */
public class SystemController extends Thread{
    
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(SystemController.class);
    
    private volatile Boolean flag;
    
    private BlockingQueue<Command> systemQueue;
    private static Object mutex = new Object();
    private static SystemController systemController;
    private static final Integer MAX_QUEUE = 1024;
    
    private SystemController(){
        systemQueue = new ArrayBlockingQueue<>(MAX_QUEUE);
        flag = true;
    }
    
    public static SystemController getIntance(){
        SystemController localRef = systemController;
        if (localRef == null) {     
            synchronized (mutex) {
                localRef = systemController;  
                if (localRef == null) {
                    systemController = localRef = new SystemController();
                }
            }
        }
        return localRef;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public BlockingQueue<Command> getSystemQueue() {
        return systemQueue;
    }

    public void setSystemQueue(BlockingQueue<Command> systemQueue) {
        this.systemQueue = systemQueue;
    }
    
    @Override
    public void run() {
        
        HandleCommandFactory commandFactory = new HandleCommandFactory();
        
        while(flag){
            if(!systemQueue.isEmpty()){
                try {
                    Command command = systemQueue.take();
                    String message = command.getMessage();
                    String commandType = message.substring(0, 1);
                    message = message.substring(1);
                    CommonService service = commandFactory.getService(commandType);
                    service.handel(command.getSocket(), message);
                } catch (InterruptedException ex) {
                    LOGGER.error(ex);
                }
                
            }
        }
    }
    
    
}
