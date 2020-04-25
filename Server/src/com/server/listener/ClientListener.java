/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.server.listener;

import com.server.bean.Command;
import com.server.common.Config;
import com.server.service.ServiceHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 *
 * @author tuhalang
 */
public class ClientListener extends Thread {

    private Socket socket;
    private ServiceHandler serviceHandler;
    private volatile Boolean isRunning;

    public ClientListener(Socket socket) {
        this.socket = socket;
        this.serviceHandler = ServiceHandler.getIntance();
        this.isRunning = Boolean.TRUE;
    }

    @Override
    public void run() {
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        try {
            while (isRunning) {

                inputStreamReader = new InputStreamReader(this.socket.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);
                String content = bufferedReader.readLine();
                if (content != null) {
                    System.out.println(content);
                    if (content.startsWith(Config.ACCESS_CODE)) {
                        serviceHandler.pushCommand(new Command(socket, content.substring(1)));
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

            if (this.socket != null) {
                try {
                    this.socket.close();

                } catch (IOException e) {
                }
            }
        }
    }

}
