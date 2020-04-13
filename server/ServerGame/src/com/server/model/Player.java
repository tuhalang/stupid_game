/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.server.model;

import java.io.Serializable;
import java.net.Socket;

/**
 *
 * @author tuhalang
 */
public class Player implements Serializable {

    private String username;
    private Boolean status;
    private Socket socket;

    public Player(String username, Socket socket) {
        this.username = username;
        this.status = Boolean.FALSE;
        this.socket = socket;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    
    

}
