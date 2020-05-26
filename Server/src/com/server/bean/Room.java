/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.server.bean;

import com.model.Airplane;
import com.model.Award;
import com.model.Bee;
import com.model.Bullet;
import com.model.Enemy;
import com.model.FlyingObject;
import com.model.Hero;
import com.server.common.Config;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author tuhalang
 */
public class Room {

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

    public Room() {
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

    public LinkedHashMap<String, Player> getPlayers() {
        return players;
    }

    public void setPlayers(LinkedHashMap<String, Player> players) {
        this.players = players;
    }
    
    
    
    private void initGame(){
        flyings = new FlyingObject[0];
        
        for(Player player : players.values()){
            heros.put(player, new Hero(0, 0));
            bullets.put(player, new Bullet[0]);
        }
    }
    
    private boolean checkFinishGame(){
        for(Player player : players.values()){
            if(player.getStatus()) return false;
        }
        // SAVE DATABASE
        // TODO
        
        isRunning = false;
        initGame();
        return true;
    }

    public void startGame() {
        initGame();
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
        if ((flyEnteredIndex %= Config.FREQ_GEN_FLY) == 0) {
            FlyingObject obj = nextOne();
            flyings = Arrays.copyOf(flyings, flyings.length + 1);
            flyings[flyings.length - 1] = obj;
        }
    }

    public void stepAction(Player player) {
        Hero hero = this.heros.get(player);
        Bullet[] bullets = this.bullets.get(player);
        hero.step();
        for (int i = 0; i < bullets.length; i++) {
            bullets[i].step();
        }
        this.heros.put(player, hero);
        this.bullets.put(player, bullets);
    }

    public void fliesAction() {
        for (int i = 0; i < flyings.length; i++) {
            flyings[i].step();
        }
    }

    public void bangAction(Player player) {
        Bullet[] bullets = this.bullets.get(player);
        Hero hero = this.heros.get(player);
        int score = hero.getScore();
        for (Bullet bullet : bullets) {
            score = bang(bullet, hero, score);
        }
        hero.setScore(score);
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
    
    private void isOver(){
        for(Player player : players.values()){
            if(player.getStatus()){
                return;
            }
        }
        status = false;
        return;
    }

    public void isGameOver(Player player) {
        Hero hero = this.heros.get(player);
        int life = hero.getLife();
        if (life == 0) {
            player.setStatus(Boolean.FALSE);
        }
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
        hero.setLife(life);
        this.heros.put(player, hero);
    }

    public void start() {
        Timer timer = new Timer();
        
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (status) {
                    isOver();
                    int batchSize = 1;
                    if (getNumOfMemmber() > 1) {
                        batchSize = getNumOfMemmber();
                    }
                    try {
                        int index = 1;
                        while (!commandsQueue.isEmpty() && index != 0) {
                            index++;
                            index = index % batchSize;
                            Command command = commandsQueue.take();
                            Player player = command.getPlayer();
                            String message = command.getMessaage();

                            System.out.println("RECIEVE:" + message);

                            String[] items = message.split("\\|");
                            if (items.length > 0) {
                                String[] posStr = items[0].split(",");
                                if (posStr.length == 2) {
                                    Hero hero = heros.get(player);
                                    hero.x = Integer.parseInt(posStr[0]);
                                    hero.y = Integer.parseInt(posStr[1]);
                                }
                            }
                            if (items.length > 1) {
                                String[] bulletStrs = items[1].split(";");
                                int length = bulletStrs.length;
                                Bullet[] bs = new Bullet[length];
                                for (int i = 0; i < length; i++) {
                                    String[] posStr = bulletStrs[i].split(",");
                                    if (posStr.length == 2) {
                                        Bullet bullet = new Bullet(Integer.parseInt(posStr[0]), Integer.parseInt(posStr[1]));
                                        bs[i] = bullet;
                                    }
                                }
                                bullets.put(player, bs);
                            }
                        }
                        enterAction();
                        fliesAction();
                        for (Player p : players.values()) {
                            if (p.getStatus()) {
                                stepAction(p);
                                bangAction(p);
                                outOfBoundsAction(p);
                                isGameOver(p);
                            }
                        }
                        String state = getStateGame();
                        System.out.println("SEND: "+ state);
                        sendStateGame(state);
                    } catch (InterruptedException ex) {
                        LOGGER.error(ex);
                    }
                }
            }

        }, 15, 15);

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
        StringBuilder state = new StringBuilder();
        for (FlyingObject f : flyings) {
            state.append(f.toString()).append(";");
        }
        state.append("|");
        for (Player player : players.values()) {
            //   
            // ID|HERO|SCORE|LIFE|BULLET
            if (player.getStatus()) {
                // ID player
                state.append(player.getUsername()).append("|");

                // major plane
                Hero hero = this.heros.get(player);

                // bullet
                Bullet[] bullets = this.bullets.get(player);

                state.append(hero.getString()).append("|");
                for (Bullet b : bullets) {
                    state.append(b.toString()).append(";");
                }
                state.append("|");
            }
        }

        return state.toString();
    }

    private void sendStateGame(String state) {
        for (Player player : this.players.values()) {
            if (player.getStatus()) {
                Socket socket = player.getSocket();
                try {
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    bw.write(state);
                    bw.newLine();
                    bw.flush();
                } catch (IOException ex) {
                    player.setStatus(Boolean.FALSE);
                    LOGGER.error(ex);
                }
            }
        }
    }

    public Boolean getIsRunning() {
        return isRunning;
    }

    public void setIsRunning(Boolean isRunning) {
        this.isRunning = isRunning;
    }

}
