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
import com.server.bean.Game;
import com.server.bean.Player;
import com.server.bean.Room;
import com.server.common.Config;
import com.server.common.SocketUtil;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    static {

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
                    Player player = command.getPlayer();
                    if (message.startsWith(Config.LOGIN_CODE)) {
                        actionLogin(player, message.substring(1));
                    } else if (message.startsWith(Config.REGISTER_CODE)) {
                        actionRegister(player, message.substring(1));
                    }
                } catch (InterruptedException | IOException ex) {
                    // LOGGING
                } catch (Exception ex) {
                    Logger.getLogger(ServiceHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    //msg = username|password
    private void actionLogin(Player player, String msg) throws IOException, Exception {
        String[] msgs = msg.split("\\|");
        Socket socket = player.getSocket();
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
                player.setUsername(username);
                Game game = Game.getIntance();
                Room room = game.getEmptyRoom();
                room.setPlayer(username, player);
                player.setCommandsQueue(room.getCommandsQueue());
                game.setRoom(room.getIdRoom(), room);
                System.out.println("AUTO ASSIGN ROOM " + room.getIdRoom());
                if (room.getNumOfMemmber() == 1) {
                    room.startGame();
                }

                // response
                String userMsg = "0LOGIN SUCCESSFULLY !";
                SocketUtil.sendViaTcp(socket, userMsg);
                return;
            }
            String userMsg = "1LOGIN FAILED !";
            SocketUtil.sendViaTcp(socket, userMsg);
        }
        String userMsg = "1LOGIN FAILED !";
        SocketUtil.sendViaTcp(socket, userMsg);
    }

    private void actionRegister(Player player, String msg) throws IOException {
        String[] msgs = msg.split("\\|");
        Socket socket = player.getSocket();
        if (msgs.length == 2) {
            String username = msgs[0];
            String password = msgs[1];

            DBCollection collection = datasource.getCollection("users");
            BasicDBObject whereQuery = new BasicDBObject();
            whereQuery.put("username", username);
            DBCursor cursor = collection.find(whereQuery);

            if (cursor.hasNext()) {

                // response faild cause duplicate username
                String userMsg = "1REGISTER FAILED !";
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
            String userMsg = "0REGISTER SUCCESSFULLY ! PLEASE LOGIN !";
            SocketUtil.sendViaTcp(socket, userMsg);
        }
        // response faild cause duplicate username
        String userMsg = "1REGISTER FAILED !";
        SocketUtil.sendViaTcp(socket, userMsg);
    }

}
