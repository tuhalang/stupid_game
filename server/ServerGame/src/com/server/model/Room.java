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
import org.apache.log4j.Logger;

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
    private static final Integer MAX_QUEUE = 10240;
    private Boolean isRunning = false;

    private static final Logger LOGGER = Logger.getLogger(Room.class);

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
                this.scores.put(player, 0);
                this.lifes.put(player, 3);
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
        if ((flyEnteredIndex %= 20) == 0) {
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

    public void bangAction(Player player) {
        Bullet[] bullets = this.bullets.get(player);
        int score = this.scores.get(player);
        Hero hero = this.heros.get(player);
        for (Bullet bullet : bullets) {
            score = bang(bullet, hero, score);
        }
        this.scores.put(player, score);
        this.bullets.put(player, bullets);
        this.heros.put(player, hero);
    }

    public int bang(Bullet b, Hero hero, int score) {
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

            FlyingObject t = this.flyings[index];
            this.flyings[index] = this.flyings[this.flyings.length - 1];
            this.flyings[this.flyings.length - 1] = t;
            this.flyings = Arrays.copyOf(this.flyings, this.flyings.length - 1);
        }
        return score;
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
        int life = this.lifes.get(player);
        for (int i = 0; i < flyings.length; i++) {
            int index = -1;
            FlyingObject f = flyings[i];
            if (hero.hit(f)) {
                index = i;
                life--;
                hero.setDoubleFire(0);
            }
            if (index != -1) {
                FlyingObject t = flyings[index];
                flyings[index] = flyings[flyings.length - 1];
                flyings[flyings.length - 1] = t;
                flyings = Arrays.copyOf(flyings, flyings.length - 1);
            }
        }
        this.lifes.put(player, life);
        return hero.getLife() <= 0;
    }

    @Override
    public void run() {
        while (this.status) {
            while (!commandsQueue.isEmpty()) {
                try {
                    int t = 0;
                    String state = "";
                    while (!commandsQueue.isEmpty() && t < 5) {
                        Command command = commandsQueue.take();
                        if (command.getMessage().startsWith("1") && this.isRunning) {
                            String[] msg = command.getMessage().substring(1).split("\\|");
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
                            for (Player player : players.values()) {
                                stepAction(command.getPlayer());
                                //shootAction(command.getPlayer());
                                bangAction(command.getPlayer());
                                outOfBoundsAction(command.getPlayer());
                                //isGameOver(command.getPlayer());
                            }
                            state = getStateGame();
                            state = "1" + state;
//                            for (Player player : players.values()) {
//                                sendStateGame(state, player);
//                            }
                        } else if (command.getMessage().startsWith("2")) {
                            command.getPlayer().setStatus(Boolean.TRUE);
                            if (checkStartGame()) {
                                int n = this.getNumOfMemmber();
                                int d = 480 / n;
                                int s = 60;
                                for (Hero hero : heros.values()) {
                                    hero = new Hero(s, 400);
                                    s += d;
                                }
                                String state1 = this.getStateGame();
                                state1 = "1" + state1;
                                for (Player player : players.values()) {
                                    sendStateGame(state1, player);
                                }
                                for (Player player : players.values()) {
                                    sendStateGame("2", player);
                                }

                                this.isRunning = true;
                            }
                        }
                    }

                    if (!state.equals("")) {
                        for (Player player : players.values()) {
                            sendStateGame(state, player);
                        }
                    }

                } catch (InterruptedException ex) {
                    LOGGER.error(ex);
                }
            }
        }
    }

    private Boolean checkStartGame() {
        for (Player player : players.values()) {
            if (!player.getStatus()) {
                return false;
            }
        }
        return true;
    }

    private String getStateGame() {
        // FLIES|PLAYER1|PLAYER2|...

        // state fies
        String state = "";
        for (FlyingObject f : flyings) {
            state += f.toString() + ";";
        }
        state += "|";
        for (Player player : players.values()) {
            //   
            // ID|HERO|BULLET|SCORE|LIFE|

            // ID player
            state += player.getUsername() + "|";

            // major plane
            Hero hero = this.heros.get(player);

            // bullet
            Bullet[] bullets = this.bullets.get(player);

            // score
            Integer score = this.scores.get(player);

            // life
            Integer life = this.lifes.get(player);

            state += hero.toString() + "|";
            for (Bullet b : bullets) {
                state += b.toString() + ";";
            }
            state += "|";
            state += score + "|";
            state += life + "|";
        }

        return state;
    }

    private String getStateGame(String state) {
        String files = "";
        for (FlyingObject f : flyings) {
            files += f.toString() + ";";
        }
        return files + "|" + state;
    }

    private void sendStateGame(String state, Player player) {
        Socket socket = player.getSocket();
        try {
            LOGGER.info("RESPONSE: " + state);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bw.write(state);
            bw.newLine();
            bw.flush();
        } catch (IOException ex) {
            LOGGER.error(ex);
        }
    }

    public Boolean getIsRunning() {
        return isRunning;
    }

    public void setIsRunning(Boolean isRunning) {
        this.isRunning = isRunning;
    }

}
