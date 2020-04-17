/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.server.model;

import com.model.Airplane;
import com.model.Award;
import com.model.Bee;
import com.model.Bullet;
import com.model.Enemy;
import com.model.FlyingObject;
import com.model.Hero;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tuhalang
 */
public class Room extends Thread {

    private volatile LinkedHashMap<String, Player> players;
    private volatile BlockingQueue<Command> commandsQueue;
    private String idRoom;
    private volatile Boolean status;
    private static Object mutex = new Object();
    private static final Integer MAX_PLAYER = 4;
    private static final Integer MAX_QUEUE = 1024;
    private Boolean isRunning = false;

    private FlyingObject[] flyings = {};
    private LinkedHashMap<Player, Hero> heros = new LinkedHashMap<>();
    private LinkedHashMap<Player, Bullet[]> bullets = new LinkedHashMap<>();
    private LinkedHashMap<Player, Integer> scores = new LinkedHashMap<>();
    private LinkedHashMap<Player, Integer> lifes = new LinkedHashMap<>();

    public Room() {
        this.idRoom = UUID.randomUUID().toString();
        players = new LinkedHashMap<>();
        commandsQueue = new ArrayBlockingQueue<>(MAX_QUEUE);
        this.status = true;
    }

    public void setPlayer(String username, Player player) throws Exception {
        if (players.size() < MAX_PLAYER) {
            synchronized (this) {
                players.put(username, player);
                heros.put(player, new Hero());
                Bullet[] bullets = {};
                this.bullets.put(player, bullets);
            }
        } else {
            throw new Exception("The list is full !");
        }
    }

    public synchronized Player getPlayer(String username) {
        return players.get(username);
    }

    public synchronized void removePlayer(String username) {
        players.remove(username);
    }

    public synchronized Boolean isFull() {
        return players.size() >= MAX_PLAYER;
    }

    public synchronized Integer getNumOfMemmber() {
        return this.players.size();
    }

    public synchronized BlockingQueue<Command> getCommandsQueue() {
        return this.commandsQueue;
    }

    public String getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(String idRoom) {
        this.idRoom = idRoom;
    }

    public Boolean getStatus() {
        return status;
    }

    public synchronized void setStatus(Boolean status) {
        this.status = status;
    }

    public void startGame() {
        this.isRunning = Boolean.TRUE;
        this.start();
    }

    public static FlyingObject nextOne() {
        Random rand = new Random();
        int type = rand.nextInt(20);
        if (type == 0) {
            return new Bee();
        } else {
            return new Airplane();
        }
    }

    private int flyEnteredIndex = 0;

    public void enterAction() {
        flyEnteredIndex++;
        if (flyEnteredIndex % 40 == 0) {
            FlyingObject obj = nextOne();
            flyings = Arrays.copyOf(flyings, flyings.length + 1);
            flyings[flyings.length - 1] = obj;
        }
    }

    public void stepAction(Player player) {
        Hero hero = this.heros.get(player);
        Bullet[] bullets = this.bullets.get(player);
        hero.step();
        for (int i = 0; i < flyings.length; i++) {
            flyings[i].step();
        }
        for (int i = 0; i < bullets.length; i++) {
            bullets[i].step();
        }
        this.heros.put(player, hero);
        this.bullets.put(player, bullets);
    }

    int shootIndex = 0;

    public void shootAction(Player player) {
        shootIndex++;
        Hero hero = this.heros.get(player);
        Bullet[] bullets = this.bullets.get(player);
        if (shootIndex % 30 == 0) { //10*30=300
            Bullet[] bs = hero.shoot();
            bullets = Arrays.copyOf(bullets, bullets.length + bs.length);
            System.arraycopy(bs, 0, bullets, bullets.length - bs.length, bs.length);
        }
        this.heros.put(player, hero);
        this.bullets.put(player, bullets);
    }

    int score = 0;

    public void bangAction(Player player) {
        Bullet[] bullets = this.bullets.get(player);
        Hero hero = this.heros.get(player);
        for(int i = 0; i < bullets.length; i++){
            bang(bullets[i], hero, i, player);
        }
        this.bullets.put(player, bullets);
        this.heros.put(player, hero);
    }

    public void bang(Bullet b, Hero hero, int bulletIndex, Player player) {
        int index = -1;
        for (int i = 0; i < this.flyings.length; i++) {
            FlyingObject f = this.flyings[i];
            if (f.shootBy(b)) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            FlyingObject one = this.flyings[index];
            if (one instanceof Enemy) {
                Enemy e = (Enemy) one;
                score += e.getScore();
            }
            if (one instanceof Award) {
                Award a = (Award) one;
                int type = a.getType();
                switch (type) {
                    case Award.DOUBLE_FIRE:
                        hero.addDoubleFire();
                        break;
                    case Award.LIFE:
                        hero.addLife();
                        break;
                }
            }
                
            // remove the FlyingObject was shot
            FlyingObject t = this.flyings[index];
            this.flyings[index] = this.flyings[this.flyings.length - 1];
            this.flyings[this.flyings.length - 1] = t;
            this.flyings = Arrays.copyOf(this.flyings, this.flyings.length - 1);
            
            // remove bullet if it shoot the plane
            Bullet[] oldBullets = this.bullets.get(player);
            Bullet bulletToRemove = oldBullets[bulletIndex];
            this.bullets.get(player)[oldBullets.length - 1] = bulletToRemove;
            this.bullets.put(player, Arrays.copyOf(oldBullets, oldBullets.length - 1));
        }
    }

    public void outOfBoundsAction(Player player) {
        int index = 0;
        FlyingObject[] flyingLives = new FlyingObject[flyings.length];
        for (int i = 0; i < flyings.length; i++) {
            FlyingObject f = flyings[i];
            if (!f.outOfBounds()) {
                flyingLives[index++] = f;
            }
        }
        flyings = Arrays.copyOf(flyingLives, index);

        Bullet[] bullets = this.bullets.get(player);
        index = 0;
        Bullet[] bulletLives = new Bullet[bullets.length];
        for (Bullet b : bullets) {
            if (!b.outOfBounds()) {
                bulletLives[index++] = b;
            }
        }
        bullets = Arrays.copyOf(bulletLives, index);
        this.bullets.put(player, bullets);
    }

    public boolean isGameOver(Player player) {
        Hero hero = this.heros.get(player);
        for (int i = 0; i < flyings.length; i++) {
            int index = -1;
            FlyingObject f = flyings[i];
            if (hero.hit(f)) {
                index = i;
                hero.subtractLife();
                hero.setDoubleFire(0);
            }
            if (index != -1) {
                FlyingObject t = flyings[index];
                flyings[index] = flyings[flyings.length - 1];
                flyings[flyings.length - 1] = t;
                flyings = Arrays.copyOf(flyings, flyings.length - 1);
            }
        }
        return hero.getLife() <= 0;
    }

    @Override
    public void run() {
        while (this.status) {
            while (!commandsQueue.isEmpty()) {
                try {
                    Command command = commandsQueue.take();
                    System.out.println(command.getMessage());
                    String[] msg = command.getMessage().split("\\|");
                    System.out.println(command.getMessage());
                    enterAction();
                    String[] heroPos = msg[0].split(",");
                    Hero hero = new Hero(Integer.parseInt(heroPos[0]), Integer.parseInt(heroPos[1]));
                    this.heros.put(command.getPlayer(), hero);
                    if (msg.length > 1 && !msg[1].equals("")) {
                        String[] bulletStrs = msg[1].split(";");
                        Bullet[] bullets = new Bullet[bulletStrs.length];
                        for (int i = 0; i < bulletStrs.length; i++) {
                            if (!bulletStrs[i].equals("")) {
                                String[] bulletPos = bulletStrs[i].split(",");
                                Bullet bullet = new Bullet(Integer.parseInt(bulletPos[0]), Integer.parseInt(bulletPos[1]));
                                bullets[i] = bullet;
                            }
                        }
                        this.bullets.put(command.getPlayer(), bullets);
                    }
                    enterAction();
                    stepAction(command.getPlayer());
                    shootAction(command.getPlayer());
                    bangAction(command.getPlayer());
                    outOfBoundsAction(command.getPlayer());
                    isGameOver(command.getPlayer());
                    String state = getStateGame(command.getPlayer());
                    sendStateGame(state, command.getPlayer());
                } catch (InterruptedException ex) {
                    Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private String getStateGame(Player player) {
        Hero hero = this.heros.get(player);
        Bullet[] bullets = this.bullets.get(player);
        String state = "";
        state += hero.toString() + "|";
        for (Bullet b : bullets) {
            state += b.toString() + ";";
        }
        state += "|";
        for (FlyingObject f : flyings) {
            state += f.toString() + ";";
        }
        return state;
    }

    private void sendStateGame(String state, Player player) {
        Socket socket = player.getSocket();
        try {
            System.out.println("Send");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bw.write(state);
            bw.newLine();
            bw.flush();
        } catch (IOException ex) {
            Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Boolean getIsRunning() {
        return isRunning;
    }

    public void setIsRunning(Boolean isRunning) {
        this.isRunning = isRunning;
    }

}
