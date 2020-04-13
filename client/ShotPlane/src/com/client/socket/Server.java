package com.client.socket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) throws IOException {
        Server server = new Server(8888);
    }

    public Server(int port) throws IOException {

        ServerSocket serverSocket = new ServerSocket(10086);
        Socket socket = serverSocket.accept();
        InputStream is = socket.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String info = null;
        while ((info = br.readLine()) != null) {
            System.out.println("server to client:" + info);
        }
        socket.shutdownInput();
        OutputStream os = socket.getOutputStream();
        PrintWriter pw = new PrintWriter(os);
        pw.write("welcome.");
        pw.flush();


        pw.close();
        os.close();
        br.close();
        isr.close();
        is.close();
        socket.close();
        serverSocket.close();
    }
}
