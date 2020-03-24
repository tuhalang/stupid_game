package com.bigwanggang.socket;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServerFrame extends JFrame {
    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 240;
    private JPanel panel;
    private JTextArea area1;
    private JTextArea area2;
    private JButton sentButton;
    private Socket socket;

    public ChatServerFrame() {
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));
        area1 = new JTextArea(6, 20);
        area1.setEnabled(false);
        area1.setFocusable(true);
        area1.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent mouseEvent) {
                area1.setCursor(new Cursor(Cursor.TEXT_CURSOR));   //鼠标进入Text区后变为文本输入指针
            }

            public void mouseExited(MouseEvent mouseEvent) {
                area1.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));   //鼠标离开Text区后恢复默认形态
            }
        });

        area1.getCaret().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                area1.getCaret().setVisible(true);   //使Text区的文本光标显示
            }
        });

        area2 = new JTextArea(4, 16);
        area2.setSelectedTextColor(Color.RED);
        area2.setLineWrap(true);
        area2.setWrapStyleWord(true);
        panel.add(new JScrollPane(area1));
        JPanel p = new JPanel();
        sentButton = new JButton("sent");
        sentButton.addActionListener(new SentAction());
        p.add(new JScrollPane(area2));
        p.add(sentButton);
        panel.add(p);
        add(panel);
        setResizable(false);
        try {
            initSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initSocket() throws IOException {
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

    private class SentAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String text = "server: " + area2.getText();
            System.out.println(text);
            area1.setText(area1.getText() + "\n" + text);
            area2.setText("");
        }
    }
}

