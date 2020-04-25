/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.server.service;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.server.bean.Command;
import com.server.common.Config;
import com.server.common.SocketUtil;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author tuhalang
 */
public class ServiceHandler extends Thread {

    protected volatile BlockingQueue<Command> commandQueue;
    private static ServiceHandler serviceHandler;
    private volatile Boolean isRunning;
    private MongoDatasource datasource;
    private DB db;

    private static final Object MUTEX = new Object();

    private ServiceHandler() {
        commandQueue = new ArrayBlockingQueue<>(1024);
        isRunning = Boolean.TRUE;
        datasource = MongoDatasource.getIntance("mongodb://localhost:27017", "game");
        startService();
    }

    private void startService() {
        this.start();
    }
    
    static{
    
    }

    public static ServiceHandler getIntance() {
        ServiceHandler localRef = serviceHandler;
        if (localRef == null) {
            synchronized (MUTEX) {
                localRef = serviceHandler;
                if (localRef == null) {
                    localRef = serviceHandler = new ServiceHandler();
                }
            }
        }
        return localRef;
    }

    public synchronized void pushCommand(Command command) {
        commandQueue.offer(command);
    }

    @Override
    public void run() {
        while (isRunning) {
            if (!commandQueue.isEmpty()) {
                try {
                    Command command = commandQueue.take();
                    String message = command.getMessaage();
                    Socket socket = command.getSocket();
                    if (message.startsWith(Config.LOGIN_CODE)) {
                        actionLogin(socket, message.substring(1));
                    } else if (message.startsWith(Config.REGISTER_CODE)) {
                        actionRegister(socket, message.substring(1));
                    }
                } catch (InterruptedException ex) {
                    // LOGGING
                } catch (IOException ex) {
                    // LOGGING
                }
            }
        }
    }

    //msg = username|password
    private void actionLogin(Socket socket, String msg) throws IOException {
        String[] msgs = msg.split("\\|");
        if (msgs.length == 2) {
            String username = msgs[0];
            String password = msgs[1];

            
            DBCollection collection = datasource.getCollection("users");
            BasicDBObject whereQuery = new BasicDBObject();
            whereQuery.put("username", username);
            whereQuery.put("password", password);
            DBCursor cursor = collection.find(whereQuery);

            // authentication
            if (cursor.hasNext()) {
                // assign room
                // TODO

                // response
                String userMsg = "LOGIN SUCCESSFULLY !";
                SocketUtil.sendViaTcp(socket, userMsg);
                return;
            }
            String userMsg = "LOGIN FAILED !";
            SocketUtil.sendViaTcp(socket, userMsg);
        }
    }

    private void actionRegister(Socket socket, String msg) throws IOException {
        String[] msgs = msg.split("\\|");
        if (msgs.length == 2) {
            String username = msgs[0];
            String password = msgs[1];

            DBCollection collection = datasource.getCollection("users");
            BasicDBObject whereQuery = new BasicDBObject();
            whereQuery.put("username", username);
            DBCursor cursor = collection.find(whereQuery);

            if (cursor.hasNext()) {

                // response faild cause duplicate username
                String userMsg = "REGISTER FAILED !";
                SocketUtil.sendViaTcp(socket, userMsg);
                return;
            }

            // success
            Map<String, String> user = new HashMap<>();
            user.put("username", username);
            user.put("password", password);
            DBObject userObj = new BasicDBObject(user);
            collection.insert(userObj);

            // response success
            String userMsg = "REGISTER SUCCESSFULLY ! PLEASE LOGIN !";
            SocketUtil.sendViaTcp(socket, userMsg);
        }
    }

}
