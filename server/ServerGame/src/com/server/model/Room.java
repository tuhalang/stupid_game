/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.server.model;

import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author tuhalang
 */
public class Room extends Thread{
    
    private volatile LinkedHashMap<String, Player> players;
    private volatile BlockingQueue<String> messagesQueue;
    private String idRoom;
    private volatile Boolean status;
    private static Object mutex = new Object();
    private static final Integer MAX_PLAYER = 4;
    private static final Integer MAX_QUEUE = 1024;
    
    public Room(){
        this.idRoom = UUID.randomUUID().toString();
        players = new LinkedHashMap<>();
        messagesQueue = new ArrayBlockingQueue<>(MAX_QUEUE);
        this.status = true;
    }
    
    public void setPlayer(String username, Player player) throws Exception{
        if(players.size() < MAX_PLAYER)
            
            synchronized(this){
                players.put(username, player);
            }
        else
            throw new Exception("The list is full !");
    }
    
    public synchronized Player getPlayer(String username){
        return players.get(username);
    }
    
    public synchronized void removePlayer(String username){
        players.remove(username);
    }
    
    public synchronized Boolean isFull(){
        return players.size() >= MAX_PLAYER;
    }
    
    public synchronized Integer getNumOfMemmber(){
        return this.players.size();
    }
    
    public synchronized BlockingQueue<String> getMessagesQueue(){
        return this.messagesQueue;
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
    
    public void startGame(){
        this.start();
    }

    @Override
    public void run() {
        while(this.status){
            
        }
    }
    
    
}
