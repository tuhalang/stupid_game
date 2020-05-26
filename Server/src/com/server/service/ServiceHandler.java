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
import org.apache.log4j.Logger;

/**
 *
 * @author tuhalang
 */
public class ServiceHandler extends Thread {

    private static final Logger LOGGER = Logger.getLogger(ServiceHandler.class);

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
                    } else if (message.startsWith(Config.JOIN_ROOM)) {
                        actionJoinRoom(player, message.substring(1));
                    } else if (message.startsWith(Config.START_GAME)) {
                        actionStartGame(player, message.substring(1));
                    }
                } catch (InterruptedException | IOException ex) {
                    LOGGER.error(ex, ex);
                } catch (Exception ex) {
                    LOGGER.error(ex, ex);
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
                player.setLogin(true);
                Game game = Game.getIntance();
                Room room = game.getEmptyRoom();
                String listRoom = game.getListRoomEmpty();
                // response
                String userMsg = Config.LOGIN_SUCCESS + "|" + listRoom;
                SocketUtil.sendViaTcp(socket, userMsg);
                return;
            }
            SocketUtil.sendViaTcp(socket, Config.LOGIN_FAILED);
        }
        SocketUtil.sendViaTcp(socket, Config.LOGIN_FAILED);
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
                SocketUtil.sendViaTcp(socket, Config.REGISTER_FAILED);
                return;
            }
            // success
            Map<String, String> user = new HashMap<>();
            user.put("username", username);
            user.put("password", password);
            DBObject userObj = new BasicDBObject(user);
            collection.insert(userObj);
            // response success
            String userMsg = "0REGISTER SUCCESSFULLY !";
            SocketUtil.sendViaTcp(socket, Config.REGISTER_SUCCESS);
        }
        // response faild cause duplicate username
        String userMsg = "1REGISTER FAILED !";
        SocketUtil.sendViaTcp(socket, Config.REGISTER_FAILED);
    }

    private void actionJoinRoom(Player player, String idRoom) throws IOException {
        String userMsg;
        if (player.isLogin()) {
            Game game = Game.getIntance();
            Room room = game.getRoom(idRoom);
            if (room.getNumOfMemmber() == 0) {
                player.setAdmin(true);
            }
            try {
                room.setPlayer(player.getUsername(), player);
                player.setCommandsQueue(room.getCommandsQueue());
                game.setRoom(room.getIdRoom(), room);
                userMsg = "0JOIN ROOM SUCCESSFULLY !";
                SocketUtil.sendViaTcp(player.getSocket(), Config.JOIN_ROOM_SUCCESS);
            } catch (Exception ex) {
                userMsg = "1JOIN ROOM FAILED !";
                LOGGER.error(ex, ex);
                SocketUtil.sendViaTcp(player.getSocket(), Config.JOIN_ROOM_FAILED);
            }
        } else {
            userMsg = "1JOIN ROOM FAILED !";
            SocketUtil.sendViaTcp(player.getSocket(), Config.JOIN_ROOM_FAILED);
        }

    }

    private void actionStartGame(Player player, String idRoom) throws IOException {
        String userMsg;
        if (player.isAdmin()) {
            Room room = Game.getIntance().getRoom(idRoom);
            room.startGame();
            Game.getIntance().setRoom(room.getIdRoom(), room);
            userMsg = "0START GAME SUCCESSFULLY !";
            for (Player player1 : room.getPlayers().values()) {
                SocketUtil.sendViaTcp(player1.getSocket(), Config.START_GAME_SUCCESS);
            }
        } else {
            userMsg = "1START GAME FAILED !";
            SocketUtil.sendViaTcp(player.getSocket(), Config.START_GAME_FAILED);
        }
    }
}
