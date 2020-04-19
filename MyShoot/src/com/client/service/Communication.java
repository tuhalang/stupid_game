/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.client.service;

import com.client.shoot.ShootGame;
import com.client.shoot.UserState;
import com.model.Airplane;
import com.model.Bullet;
import com.model.FlyingObject;
import com.model.Hero;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tuhalang
 */
public class Communication {

    private volatile Socket socket;
    private volatile BlockingQueue<String> messagesQueue;
    public static final String ID = UUID.randomUUID().toString();
    private ShootGame shootGame;

    private static Communication communication;
    private static Object mutex = new Object();

    private Communication(String ip, int port, ShootGame shootGame) {
        try {
            this.socket = new Socket(ip, port);
            this.messagesQueue = new ArrayBlockingQueue<>(1024);
            this.shootGame = shootGame;
            send(ID);
            receive();
            handle();
        } catch (IOException ex) {
            Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Communication getIntance(String ip, int port, ShootGame shootGame) {
        Communication localRef = communication;
        if (localRef == null) {
            synchronized (mutex) {
                localRef = communication;
                if (localRef == null) {
                    communication = localRef = new Communication(ip, port, shootGame);
                }
            }
        }
        return localRef;
    }

    public void send(String message) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
            bw.write(message);
            bw.newLine();
            bw.flush();
        } catch (IOException ex) {
            Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void receive() {
        Runnable run = new Runnable() {

            @Override
            public void run() {
                System.out.println("IS LISTENING MESSAGE");
                while (socket.isConnected()) {

                    try {
                        InputStreamReader isr = new InputStreamReader(socket.getInputStream());
                        BufferedReader br = new BufferedReader(isr);
                        String message = br.readLine();
                        if (message != null) {
                            messagesQueue.offer(message);
                        }
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }

                }
                System.out.println("socket is closed");
            }

        };
        Thread thread = new Thread(run);
        thread.start();
    }

    private void handle() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                while (!messagesQueue.isEmpty()) {
                    // structure: <number of user> | user1_name | position 1 | bullet 1 | user2_name | position 2 | bullet 2 | ... | userN_name | position n | bullet n | enemy_position
                    String message;                    
                    try {
                        message = messagesQueue.take();
                        System.out.println(message);
                        String[] objs = message.split("\\|");
                        int numberOfplayer = Integer.valueOf(objs[0]);                                              // get the number of player in the room
                        LinkedHashMap<String, UserState> userStates = new LinkedHashMap<String, UserState>();       // get the state of all user
                        for (int i = 0; i < numberOfplayer; i++) {
                            String userName = objs[3 * i + 1];
                            String[] positionStrs = objs[3 * i + 2].split(",");
                            int[] position = {Integer.parseInt(positionStrs[0]), Integer.parseInt(positionStrs[1])};
                            UserState uState = null;
                            if (!objs[3 * i + 3].equals("")) {
                                String[] bulletStrs = objs[3 * i + 3].split(";");
                                Bullet[] bullets = new Bullet[bulletStrs.length];
                                for (int j = 0; j < bulletStrs.length; j++) {
                                    String[] bulletPos = bulletStrs[j].split(",");
                                    Bullet bullet = new Bullet(Integer.parseInt(bulletPos[0]), Integer.parseInt(bulletPos[1]));
                                    bullets[j] = bullet;
                                }
                                uState = new UserState(position, bullets);
                            }
                            userStates.put(userName, uState);
                            if(userName.equals(ID)){
                                shootGame.setMyID(userName);
                            }
                        }
                        shootGame.setUserStates(userStates);
                        if (objs.length > 2 && !objs[2].equals("")) {
                            String[] flyStrs = objs[2].split(";");
                            FlyingObject[] flies = new FlyingObject[flyStrs.length];
                            for (int i = 0; i < flies.length; i++) {
                                if (!flyStrs[i].equals("")) {
                                    String[] bulletPos = flyStrs[i].split(",");
                                    FlyingObject fly = new Airplane(Integer.parseInt(bulletPos[0]), Integer.parseInt(bulletPos[1]));
                                    flies[i] = fly;
                                }
                            }
                            shootGame.setFlyings(flies);
                        }

                    } catch (InterruptedException ex) {
                        Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        }, 5, 5);

    }
}
