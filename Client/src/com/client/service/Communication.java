/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.client.service;

import com.client.ui.Config;
import com.client.ui.ShootGame;
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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author tuhalang
 */
public final class Communication {

    private volatile Socket socket;
    private volatile BlockingQueue<String> messagesQueue;
    private ShootGame shootGame;

    private static volatile Communication communication;
    private static final Object mutex = new Object();

    private Communication(String ip, int port, ShootGame shootGame) {
        try {
            this.socket = new Socket(ip, port);
            this.messagesQueue = new ArrayBlockingQueue<>(10240);
            this.shootGame = shootGame;
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
                        System.out.println(message);
                        if (message != null) {
                            String code = message.substring(0, 3);
                            switch (code) {
                                case Config.RES_LOGIN_SUCCESS:
                                    shootGame.loginState = Config.LOGIN_SUCCESS;
                                    System.out.println("Login Succesfull, now need to choose room");
                                    shootGame.setRoomIDs(message.split("\\|")[1].split(","));
                                    break;
                                case Config.RES_LOGIN_FAILED:
                                    shootGame.loginState = Config.LOGIN_FAIL;
                                        JOptionPane.showMessageDialog(null, "Login failed, please try again");
                                    break;
                                case Config.RES_JOIN_ROOM_SUCCESS:
                                    shootGame.loginState = Config.WAIT_PLAY;
                                        System.out.println("Join room successful");
                                    break;
                                case Config.RES_JOIN_ROOM_FAILED:
                                    JOptionPane.showMessageDialog(null, "Join room failed, please try again");
                                    break;
                                case Config.RES_START_GAME_SUCCESS:
                                    shootGame.loginState = Config.PLAY;
                                        System.out.println("Let's play");
                                    break;
                                case Config.RES_START_GAME_FAILED:
                                    JOptionPane.showMessageDialog(null, "You are not admin");
                                    break;
                                case Config.RES_REGISTER_SUCCESS:
                                    shootGame.loginState = Config.REGISTER_SUCCESS;
                                        System.out.println("Register Succesfull");
                                    break;
                                case Config.RES_REGISTER_FAILED:
                                    shootGame.loginState = Config.REGISTER_FAIL;
                                    JOptionPane.showMessageDialog(null, "Register failed, please try again");
                                    break;
                                case Config.RES_FINISH_GAME:
                                    message = message.substring(3);
                                    String[] result = message.split("\\|");
                                    int numOfPlayer = result.length;
//                                    String[] allUserName = new String[numOfPlayer];
//                                    String[] allScore = new String[numOfPlayer];
//                                    String[] deadOrAlive = new String[numOfPlayer];
                                    String mess = "";
                                    for(int i = 0; i < numOfPlayer; i++){
                                        String[] info = result[i].split(",");
//                                        allUserName[i] = info[0];
//                                        allScore[i] = info[1];
//                                        deadOrAlive[i] = info[2];
                                        mess += i + 1 + ". " + info[0] + ": " + info[1];
                                        if(info[2].equals("0")){
                                            mess += "is alive";
                                        }
                                        mess += "\n";
                                    }
                                    JOptionPane.showMessageDialog(null, mess);
                                    break;
                                default:
                                    messagesQueue.offer(message);
                            }
                        }
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }

                }

                System.out.println(
                        "socket is closed");
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
                    String message;
                    try {
                        message = messagesQueue.take();
                        System.out.println(message);

                        String[] items = message.split("\\|");
                        if (items.length > 0) {
                            // Get the state of the flying objects
                            if (!items[0].equals("")) {
                                String[] flyingStrs = items[0].split(";");
                                int length = flyingStrs.length;
                                FlyingObject[] flyings = new FlyingObject[length];
                                for (int i = 0; i < length; i++) {
                                    if (!flyingStrs[i].equals("")) {
                                        String[] posStr = flyingStrs[i].split(",");
                                        if (posStr.length == 2) {
                                            FlyingObject flying = new Airplane(Integer.parseInt(posStr[0]), Integer.parseInt(posStr[1]));
                                            flyings[i] = flying;
                                        }
                                    }
                                }
                                shootGame.setFlyings(flyings);
                            }
                        }

                        LinkedHashMap<String, Hero> guestHero = new LinkedHashMap<>();
                        LinkedHashMap<Hero, Bullet[]> guestBullet = new LinkedHashMap<>();
                        for (int i = 1; i < items.length;) {
                            // get the state of the different users
                            if (ShootGame.username.equals(items[i])) {
                                String uname = null;
                                String[] posStr = null;
                                String scoreStr = null;
                                String lifeStr = null;
                                String[] bulletStrs = null;

                                uname = items[i];
                                i++;
                                posStr = items[i].split(",");
                                i++;
                                scoreStr = items[i];
                                i++;
                                lifeStr = items[i];
                                i++;
                                if (i < items.length && !items[i].equals("")) {
                                    bulletStrs = items[i].split(";");
                                }
                                i++;

                                Hero hero = new Hero(Integer.parseInt(posStr[0]),
                                        Integer.parseInt(posStr[1]),
                                        Integer.parseInt(scoreStr),
                                        Integer.parseInt(lifeStr));
                                shootGame.setHero(hero);

                                if (bulletStrs != null) {
                                    int length = bulletStrs.length;
                                    Bullet[] bullets = new Bullet[length];
                                    for (int j = 0; j < length; j++) {
                                        if (!bulletStrs[j].equals("")) {
                                            String[] posBStr = bulletStrs[j].split(",");
                                            if (posStr.length == 2) {
                                                Bullet bullet = new Bullet(Integer.parseInt(posBStr[0]),
                                                        Integer.parseInt(posBStr[1]));
                                                bullets[j] = bullet;
                                            }
                                        }
                                    }
                                    shootGame.setBullets(bullets);
                                }

                            } else {
                                String uname = null;
                                String[] posStr = null;
                                String scoreStr = null;
                                String lifeStr = null;
                                String[] bulletStrs = null;

                                uname = items[i];
                                i++;
                                posStr = items[i].split(",");
                                i++;
                                scoreStr = items[i];
                                i++;
                                lifeStr = items[i];
                                i++;
                                if (i < items.length && !items[i].equals("")) {
                                    bulletStrs = items[i].split(";");
                                }
                                i++;

                                Hero hero = new Hero(Integer.parseInt(posStr[0]),
                                        Integer.parseInt(posStr[1]),
                                        Integer.parseInt(scoreStr),
                                        Integer.parseInt(lifeStr));
                                guestHero.put(uname, hero);

                                if (bulletStrs != null) {
                                    int length = bulletStrs.length;
                                    Bullet[] bullets = new Bullet[length];
                                    for (int j = 0; j < length; j++) {
                                        if (!bulletStrs[j].equals("")) {
                                            String[] posBStr = bulletStrs[j].split(",");
                                            if (posStr.length == 2) {
                                                Bullet bullet = new Bullet(Integer.parseInt(posBStr[0]),
                                                        Integer.parseInt(posBStr[1]));
                                                bullets[j] = bullet;
                                            }
                                        }
                                    }
                                    guestBullet.put(hero, bullets);
                                }
                            }
                        }

                        shootGame.setGuestHero(guestHero);
                        shootGame.setGuestBullet(guestBullet);

                    } catch (InterruptedException ex) {
                        Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        }, 15, 15);

    }
}
