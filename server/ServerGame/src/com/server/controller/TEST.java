/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.server.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tuhalang
 */
public class TEST {

    public static void main(String[] args) throws IOException {

        Socket socket = new Socket("127.0.0.1", 8888);
        receive(socket);
        while (true) {
            
            Scanner scanner = new Scanner(System.in);
            String msg = scanner.nextLine();
            send(socket, msg);


        }
    }
    
    public static void receive(Socket socket) {
        Runnable run = new Runnable() {

            @Override
            public void run() {
                System.out.println("IS LISTENING MESSAGE");
                while (socket.isConnected()) {

                    try {
                        InputStreamReader isr = new InputStreamReader(socket.getInputStream());
                        BufferedReader br = new BufferedReader(isr);
                        String message = br.readLine();
                        if (message != null) {
                            System.out.println(message);
                        }
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

    public static void send(Socket socket, String message) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bw.write(message);
            bw.newLine();
            bw.flush();
        } catch (IOException ex) {

        }
    }
}
