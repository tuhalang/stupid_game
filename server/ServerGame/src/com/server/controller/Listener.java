/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.server.controller;

/**
 *
 * @author tuhalang
 */
import com.server.common.GameConfig;
import com.server.model.Command;
import com.server.model.Player;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import org.apache.log4j.Logger;

public class Listener extends Thread {
    
    private static final Logger LOGGER = Logger.getLogger(Listener.class);

    private int flag = 1;

    // queue for ingame command
    private BlockingQueue<Command> commandsQueue;
    
    // queue for action system such as login, regsiter, join room
    private BlockingQueue<Command> systemQueue;
    
    private Player player;

    public Listener(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        
        try {
            while (flag == 1) {
                inputStreamReader = new InputStreamReader(this.player.getSocket().getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);
                String content = bufferedReader.readLine();
                if (content != null) {
                   
                    if(content.startsWith(GameConfig.CONTROL_GAME_CODE)){
                        if(this.commandsQueue != null){
                            StackTraceElement[] e = this.getStackTrace();
                            this.commandsQueue.offer(new Command(this.player, content.substring(1), System.currentTimeMillis()));
                        }
                    }else if(content.startsWith(GameConfig.LOGIN_CODE)){ 
                        if(this.systemQueue != null){
                            this.systemQueue.offer(new Command(this.player, content));
                        }
                    }else if(content.startsWith(GameConfig.JOIN_ROOM_CODE)){
                        if(this.systemQueue != null){
                            this.systemQueue.offer(new Command(this.player, content));
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error(e);
            this.player.setStatus(Boolean.FALSE);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    LOGGER.error(e);
                }
            }

            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    LOGGER.error(e);
                }
            }

            if (this.player.getSocket() != null) {
                try {
                    this.player.getSocket().close();

                } catch (IOException e) {
                    LOGGER.error(e);
                }
            }
        }
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
    
    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public BlockingQueue<Command> getCommandsQueue() {
        return commandsQueue;
    }

    public void setCommandsQueue(BlockingQueue<Command> commandsQueue) {
        this.commandsQueue = commandsQueue;
    }

    public BlockingQueue<Command> getSystemQueue() {
        return systemQueue;
    }

    public void setSystemQueue(BlockingQueue<Command> systemQueue) {
        this.systemQueue = systemQueue;
    }

    
    
    

}
