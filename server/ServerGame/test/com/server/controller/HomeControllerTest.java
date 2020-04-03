/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.server.controller;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author tuhalang
 */
public class HomeControllerTest {

    public static void main(String[] args) throws InterruptedException {
        BufferedWriter bw = null;
        try {
            Socket socket = new Socket("127.0.0.1", 8888);

            // gửi username để làm key trên HashTable server
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            
            
            for(int i=0; i<10000; i++){
                bw.write(i + ": HELLO");
                System.out.println("message: " + i + ": HELLO");
                bw.newLine();
                bw.flush();
                Thread.sleep(30);
            }

        } catch (UnknownHostException e) {
        } catch (IOException e) {
        }
    }
}
