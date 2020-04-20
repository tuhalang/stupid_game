/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.client.service;

import com.client.shoot.ShootGame;
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
public final class Communication {

    private volatile Socket socket;
    private volatile BlockingQueue<String> messagesQueue;
    public static final String ID = UUID.randomUUID().toString();
    private ShootGame shootGame;

    private static volatile Communication communication;
    private static final Object mutex = new Object();

    private Communication(String ip, int port, ShootGame shootGame) {
        try {
            this.socket = new Socket(ip, port);
            this.messagesQueue = new ArrayBlockingQueue<>(10240);
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
                    String message;
                    try {
                        message = messagesQueue.take();
                        
                        if (message.startsWith("1")) {
                            message = message.substring(1);
                            String[] objs = message.split("\\|");

                            if (!objs[0].equals("")) {
                                String[] flyStrs = objs[0].split(";");
                                FlyingObject[] flies = new FlyingObject[flyStrs.length];
                                for (int i = 0; i < flies.length; i++) {
                                    if (!flyStrs[i].equals("")) {
                                        String[] bulletPos = flyStrs[i].split(",");
                                        if (!bulletPos[0].equals("") && !bulletPos[1].equals("")) {
                                            FlyingObject fly = new Airplane(Integer.parseInt(bulletPos[0]), Integer.parseInt(bulletPos[1]));
                                            flies[i] = fly;
                                        }
                                    }
                                }
                                shootGame.setFlyings(flies);
                            }
                            LinkedHashMap<String, Hero> guestHero = new LinkedHashMap<>();
                            LinkedHashMap<Hero, Bullet[]> guestBullet = new LinkedHashMap<>();

                            for (int i = 0; i < (objs.length - 1) / 5; i++) {
                                String ID = objs[5 * i + 1];
                                String[] heroPos = objs[5 * i + 2].split(",");
                                Hero hero = new Hero(Integer.parseInt(heroPos[0]), Integer.parseInt(heroPos[1]));
                                if (ID.endsWith(Communication.ID)) {
                                    shootGame.setHero(hero);
                                } else {
                                    guestHero.put(ID, hero);
                                }
                                if (!objs[5 * i + 3].equals("")) {
                                    String[] bulletStrs = objs[5 * i + 3].split(";");
                                    Bullet[] bullets = new Bullet[bulletStrs.length];
                                    for (int j = 0; j < bulletStrs.length; j++) {
                                        String[] bulletPos = bulletStrs[j].split(",");
                                        if (!bulletPos[0].equals("") && !bulletPos[1].equals("")) {
                                            Bullet bullet = new Bullet(Integer.parseInt(bulletPos[0]), Integer.parseInt(bulletPos[1]));
                                            bullets[j] = bullet;
                                        }
                                    }
                                    int score = Integer.parseInt(objs[5 * i + 4]);
                                    int life = Integer.parseInt(objs[5 * i + 5]);
                                    if (ID.endsWith(Communication.ID)) {
                                        shootGame.setBullets(bullets);
                                        shootGame.setScore(score);
                                        hero.setLife(life);
                                        shootGame.setHero(hero);
                                    } else {
                                        guestBullet.put(hero, bullets);
                                    }
                                }
                            }
                            shootGame.setGuestHero(guestHero);
                            shootGame.setGuestBullet(guestBullet);
                        } else if (message.startsWith("2")) {
                            shootGame.setState(ShootGame.RUNNING);
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        }, 20, 20);

    }
}
