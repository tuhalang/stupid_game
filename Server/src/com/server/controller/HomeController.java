/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.server.controller;

import com.server.bean.Game;
import com.server.bean.Player;
import com.server.common.Config;
import com.server.listener.ClientListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author tuhalang
 */
public class HomeController {
    
    
    public static Boolean flag = Boolean.TRUE;

    public static void main(String[] args) {

        // Init Game
        Game game = Game.getIntance();
        
        ExecutorService executorService = Executors.newFixedThreadPool(Config.MAX_POOL);
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(Config.PORT);
            
            while (flag) {
                try {
                    Socket socket = serverSocket.accept();
                    Player player = new Player(socket);
                    ClientListener listener = new ClientListener(player);
                    executorService.execute(listener);
                } catch (IOException e) {
                    
                } catch (Exception ex) {
                    
                }
            }
        } catch (IOException e) {
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    
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
