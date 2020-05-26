/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.server.bean;

import com.server.common.Config;
import java.util.LinkedHashMap;

/**
 *
 * @author tuhalang
 */
public class Game {
    
    private volatile LinkedHashMap<String, Room> rooms;
    private final static Integer MAX_ROOM = 4;
    private static volatile Game game;
    private static Object mutex = new Object();
    
    private Game(){
        rooms = new LinkedHashMap<>();
        if(!Config.AUTO_ASSIGN_ROOM){
            for(int i=0; i<MAX_ROOM; i++){
                Room room = new Room();
                room.setIdRoom(String.valueOf(i+1));
                rooms.put(room.getIdRoom(), room);
            }
        }
    }
    
    public static Game getIntance(){
        Game localRef = game;
        if (localRef == null) {     
            synchronized (mutex) {
                localRef = game;  
                if (localRef == null) {
                    game = localRef = new Game();
                }
            }
        }
        return localRef;
    }
    
    public synchronized void setRoom(String idRoom, Room room){
        rooms.put(idRoom, room);
    }
    
    public synchronized Room getRoom(String idRoom){
        return rooms.get(idRoom);
    }
    
    public synchronized void removeRoom(String idRoom){
        Room room = new Room();
        room.setIdRoom(idRoom);
        rooms.put(idRoom, room);
    }
    
    public synchronized Room getEmptyRoom(){
        for(Room room : rooms.values()){
            if(!room.isFull() && !room.getIsRunning()){
                return room;
            }
        }
        if(rooms.size() < MAX_ROOM){
            return new Room();
        }
        
        return null;
    }
    
    public synchronized String getListRoomEmpty(){
        String result="";
        for(Room room : rooms.values()){
            if(!room.isFull()){
                result+=room.getIdRoom()+",";
            }
        }
        return result;
    }
    
}
