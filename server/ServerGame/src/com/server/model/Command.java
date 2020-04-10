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
    private Socket socket;
    private String message;
    private Long currentTime;

    public Command(Socket socket, String message, Long currentTime) {
        this.socket = socket;
        this.message = message;
        this.currentTime = currentTime;
    }
    
    public Command(Socket socket, String message){
        this.socket = socket;
        this.message = message;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
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
