/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.server.model;

import com.server.controller.Listener;
import java.io.Serializable;

/**
 *
 * @author tuhalang
 */
public class Player implements Serializable{
    
    private Listener listener;
    private String username;
    private Boolean status;
    
    public Player(String username, Listener listener){
        this.username = username;
        this.listener = listener;
        this.status = Boolean.FALSE;
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
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

    
    
    
}
