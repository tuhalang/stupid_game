package com.bigwanggang.socket;

import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String args[]) throws Exception {
        Socket socket = new Socket("localhost", 8888);
        OutputStream os = socket.getOutputStream();
        PrintWriter pw = new PrintWriter(os);
        pw.write("user:admin:password:123");
        pw.flush();
        socket.shutdownOutput();
        InputStream is = socket.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String info = null;
        while ((info = br.readLine()) != null) {
            System.out.println("i am client, server say: " + info);
        }

        br.close();
        is.close();
        pw.close();
        os.close();
        socket.close();
    }
}