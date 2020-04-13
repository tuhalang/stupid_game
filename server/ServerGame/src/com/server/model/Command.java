/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.server.model;

import java.net.Socket;

/**
 *
 * @author tuhalang
 */
public class Command {
    private Player player;
    private String message;
    private Long currentTime;

    public Command(Player player, String message, Long currentTime) {
        this.player = player;
        this.message = message;
        this.currentTime = currentTime;
    }
    
    public Command(Player player, String message){
        this.player = player;
        this.message = message;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(Long currentTime) {
        this.currentTime = currentTime;
    }
    
    
            
}
