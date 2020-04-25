/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.server.bean;

import java.net.Socket;

/**
 *
 * @author tuhalang
 */
public class Command {
    private String messaage;
    private Socket socket;
    
    public Command(Socket socket, String message){
        this.socket = socket;
        this.messaage = message;
    }

    public String getMessaage() {
        return messaage;
    }

    public void setMessaage(String messaage) {
        this.messaage = messaage;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    
    
    
    
}
