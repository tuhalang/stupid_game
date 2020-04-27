/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.server.bean;

import java.io.Serializable;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author tuhalang
 */
public class Player implements Serializable {

    private String username;
    private Boolean status;
    private Socket socket;
    private BlockingQueue<Command> commandsQueue;

    public Player(Socket socket) {
        this.status = Boolean.TRUE;
        this.socket = socket;
    }

    public void pushCommand(String content) {
        if (this.commandsQueue != null) {
            this.commandsQueue.offer(new Command(this, content));
        }
    }

    public BlockingQueue<Command> getCommandsQueue() {
        return commandsQueue;
    }

    public void setCommandsQueue(BlockingQueue<Command> controlQueue) {
        this.commandsQueue = controlQueue;
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
