/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.server.bean;

/**
 *
 * @author tuhalang
 */
public class Command {
    private String messaage;
    private Player player;
    
    public Command(Player player, String message){
        this.player = player;
        this.messaage = message;
    }

    public String getMessaage() {
        return messaage;
    }

    public void setMessaage(String messaage) {
        this.messaage = messaage;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

}
