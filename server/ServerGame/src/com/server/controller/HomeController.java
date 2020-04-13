/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.server.controller;

import com.server.common.GameConfig;
import com.server.model.Game;
import com.server.model.Player;
import com.server.model.Room;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;

/**
 *
 * @author tuhalang
 */
public class HomeController {
    
    private static final Logger LOGGER = Logger.getLogger(HomeController.class);
    
    private static Game game;

    public static Boolean flag = Boolean.TRUE;

    public static void main(String[] args) {

        SystemController systemController = SystemController.getIntance();
        systemController.start();
        
        //create Game
        game = Game.getIntance();

        // create NUM_OF_THREAD thread pool
        ExecutorService executorService = Executors.newFixedThreadPool(GameConfig.NUM_OF_THREAD);
        ServerSocket serverSocket = null;

        try {
            LOGGER.info("Binding to port: " + GameConfig.SERVER_PORT);
            serverSocket = new ServerSocket(GameConfig.SERVER_PORT);
            LOGGER.info("Server started: " + serverSocket);
            LOGGER.info("Waiting for client ...");
            
            while (flag) {
                try {

                    Socket socket = serverSocket.accept();

                    // get username
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String username = bufferedReader.readLine();

                    
                    

                    Player player = new Player(username, socket);
                    
                    // create WorkerThread to handle request for each client
                    Listener listener = new Listener(player);
                    listener.setSystemQueue(systemController.getSystemQueue());
                    
                    if(GameConfig.AUTO_ASSIGN_ROOM){
                        
                        LOGGER.info("AUTO ASSIGN ROOM FOR USER: " + username);
                        Room room = game.getEmptyRoom();
                        if(room != null){
                            room.setPlayer(username, player);
                            listener.setCommandsQueue(room.getCommandsQueue());
                            game.setRoom(room.getIdRoom(), room);
                            LOGGER.info("USER: " + username + " JOINED ROOM " + room.getIdRoom());
                            if(room.getNumOfMemmber()==1){
                                room.startGame();
                            }
                            LOGGER.info("NUM OF MEMMBER: " + room.getNumOfMemmber());
                        }else{
                            LOGGER.error("================= MAX ZOOM ==================");
                        }
                    }
                    
                    executorService.execute(listener);

                } catch (IOException e) {
                    LOGGER.error(e);
                } catch (Exception ex) {
                    LOGGER.error(ex);
                }
            }
        } catch (IOException e) {
            LOGGER.error(e);
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                    LOGGER.info("server socket is cloesed");
                } catch (IOException e) {
                    LOGGER.error(e);
                }
            }
        }
    }

//    private static void checkOffline() {
//        Timer timer = new Timer();
//        Queue<String> listOffline = new LinkedList<String>();
//        timer.schedule(new TimerTask() {
//
//            @Override
//            public void run() {
//                BufferedWriter bufferedWriter = null;
//                for (String username : users.keySet()) {
//                    WorkerThread workerThread = users.get(username);
//                    Socket socket = workerThread.getSocket();
//                    System.out.println(username);
//                    if (!socket.isConnected() || socket.isClosed()) {
//                        listOffline.add(username);
//                    }
//                }
//                while (!listOffline.isEmpty()) {
//                    users.remove(listOffline.poll());
//                }
//
//            }
//        }, 0, 5000);
//
//    }
}
