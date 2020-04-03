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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import org.apache.log4j.Logger;

public class Listener extends Thread {
    
    private static final Logger LOGGER = Logger.getLogger(Listener.class);

    private int flag = 1;

    private Socket socket;
    
    private BlockingQueue<String> messagesQueue;

    public Listener(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        
        try {
            while (flag == 1) {
                inputStreamReader = new InputStreamReader(this.socket.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);
                String content = bufferedReader.readLine();
                if (content != null) {
                    if(content.startsWith(GameConfig.CONTROL_GAME_CODE)){
                        if(this.messagesQueue != null){
                            this.messagesQueue.offer(content);
                        }
                    }else if(content.startsWith(GameConfig.LOGIN_CODE)){ 
                        // TODO
                    }else if(content.startsWith(GameConfig.JOIN_ROOM_CODE)){
                        // TODO
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error(e);
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

            if (socket != null) {
                try {
                    socket.close();

                } catch (IOException e) {
                    LOGGER.error(e);
                }
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public BlockingQueue<String> getMessagesQueue() {
        return messagesQueue;
    }

    public void setMessagesQueue(BlockingQueue<String> messagesQueue) {
        this.messagesQueue = messagesQueue;
    }
    
    

}
