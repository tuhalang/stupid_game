/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.server.listener;

import com.server.bean.Command;
import com.server.bean.Player;
import com.server.common.Config;
import com.server.service.ServiceHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author tuhalang
 */
public class ClientListener extends Thread {

    private final Player player;
    private final ServiceHandler serviceHandler;
    private volatile Boolean isRunning;

    public ClientListener(Player player) {
        this.player = player;
        this.serviceHandler = ServiceHandler.getIntance();
        this.isRunning = Boolean.TRUE;
    }

    @Override
    public void run() {
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        try {
            while (isRunning) {
                inputStreamReader = new InputStreamReader(this.player.getSocket().getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);
                String content = bufferedReader.readLine();
                if (content != null) {
                    if (content.startsWith(Config.ACCESS_CODE)) {
                        serviceHandler.pushCommand(new Command(this.player, content.substring(1)));
                    }else if(content.startsWith(Config.CONTROL_CODE)){
                        if(this.player != null){
                            this.player.pushCommand(content.substring(1));
                        }
                    }
                }
            }
        } catch (IOException e) {

        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                }
            }

            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                }
            }

            if (this.player.getSocket() != null) {
                try {
                    this.player.getSocket().close();

                } catch (IOException e) {
                }
            }
        }
    }

}
