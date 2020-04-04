package com.client.test;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

public class ChatFrameServer {

    private PrintWriter pw;
    private JFrame frame;
    private JPanel pane_buttom;
    private JSplitPane pane_center;

    private JScrollPane pane_showWindow;
    private JScrollPane pane_inputWindow;
    private JTextArea area_showWindow;
    private JTextArea area_inputWindow;

    private JButton btn_send;

    private Dimension dimension;

    public ChatFrameServer() {
        frame = new JFrame();
        pane_buttom = new JPanel();
        pane_showWindow = new JScrollPane();
        pane_inputWindow = new JScrollPane();
        area_showWindow = new JTextArea();
        area_inputWindow = new JTextArea();
        pane_center = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, pane_showWindow, pane_inputWindow);
        btn_send = new JButton("send");

        dimension = new Dimension(50, 300);

    }

    public void showFrame() {
        initFrame();
        initChatTextArea();
        initButton();
        btn_send();
        socket();
    }

    public void initFrame() {
        frame.setTitle("server");
        int width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        int height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        frame.setBounds(width / 2, height / 2, 400, 450);
        frame.setVisible(true);
    }

    private void initChatTextArea() {
        pane_showWindow.getViewport().add(area_showWindow);
        pane_inputWindow.getViewport().add(area_inputWindow);
        area_showWindow.setEditable(false);
        pane_showWindow.setMinimumSize(dimension);
        frame.add(pane_center, BorderLayout.CENTER);
    }

    public void initButton() {
        pane_buttom.add(btn_send);
        frame.add(pane_buttom, BorderLayout.SOUTH);
    }


    private void btn_send() {
        btn_send.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String info = area_inputWindow.getText();
                area_showWindow.append("server: " + info + "\r\n");
                pw.println(info);
                area_inputWindow.setText("");
            }
        });
    }

    private void socket() {
        ServerSocket ss;
        try {
            ss = new ServerSocket(9988);
            Socket s = ss.accept();
            InputStreamReader isr = new InputStreamReader(s.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            pw = new PrintWriter(s.getOutputStream(), true);

            while (true) {
                String info = br.readLine();
                area_showWindow.append("client" + info + "\r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ChatFrameServer chat = new ChatFrameServer();
        chat.showFrame();
    }
}

