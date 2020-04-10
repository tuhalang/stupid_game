/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.client.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tuhalang
 */
public class Communication {

    private volatile Socket socket;
    private volatile BlockingQueue<String> messagesQueue;

    private static Communication communication;
    private static Object mutex = new Object();

    private Communication(String ip, int port) {
        try {
            this.socket = new Socket(ip, port);
            this.messagesQueue = new ArrayBlockingQueue<>(1024);
        } catch (IOException ex) {
            Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Communication getIntance(String ip, int port) {
        Communication localRef = communication;
        if (localRef == null) {
            synchronized (mutex) {
                localRef = communication;
                if (localRef == null) {
                    communication = localRef = new Communication(ip, port);
                }
            }
        }
        return localRef;
    }

    public void send(String message) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
            bw.write(message);
            bw.newLine();
            bw.flush();
        } catch (IOException ex) {
            Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void receive() {
        Runnable run = new Runnable() {

            @Override
            public void run() {
                System.out.println("IS LISTENING MESSAGE");
                while (socket.isConnected()) {

                    try {
                        InputStreamReader isr = new InputStreamReader(socket.getInputStream());
                        BufferedReader br = new BufferedReader(isr);
                        String message = br.readLine();
                        messagesQueue.offer(message);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }

                }
                System.out.println("socket is closed");
            }

        };
        Thread thread = new Thread(run);
        thread.start();
    }

}
