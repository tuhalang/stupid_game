package com.client.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;

public class ShotPlaneFrame extends JFrame {
    private static final int DEFAULT_WIDTH = 600;
    private static final int DEFAULT_HEIGHT = 450;
    private Plane plane = new Plane(6, 5, 6, 8);
    private ShotPlaneDisplayConponent gameDisplayComponent;
    private JPanel controlPanel;
    private JTextField ipField;
    private JTextField portField;
    private JTextArea chatDisplayArea;
    private JTextArea chatInputArea;
    private JScrollPane chatDisplayWindow;
    private JScrollPane chatInputWindow;
    private GridBagConstraints constraints;
    private PrintWriter pw;
    private boolean serverIsOn = false;
    private boolean clientIsReady = false;
    private JButton connectButton;
    private JButton statusButton;

    public ShotPlaneFrame() {
        // setup properties  for app
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setLocationRelativeTo(null);
        
        // setup properties for element inside the app
        controlPanel = new JPanel();
        chatDisplayArea = new JTextArea(6, 6);
        chatDisplayArea.setEnabled(false);
        chatInputArea = new JTextArea(6, 3);
        chatDisplayWindow = new JScrollPane();
        chatInputWindow = new JScrollPane();
        chatDisplayWindow.getViewport().add(chatDisplayArea);
        chatInputWindow.getViewport().add(chatInputArea);
        chatInputArea.setLineWrap(true);
        chatDisplayArea.setLineWrap(true);
        chatDisplayArea.setDisabledTextColor(Color.BLACK);

        chatDisplayArea.setFont(new java.awt.Font("Dialog", 1, 14));
        gameDisplayComponent = new ShotPlaneDisplayConponent(plane);
        constraints = new GridBagConstraints();
        ipField = new JTextField(10);
        InetAddress localhostAddress = null;
        try {
            localhostAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        if (localhostAddress != null) {
            ipField.setText(localhostAddress.getHostAddress());
            ipField.setEnabled(false);
            ipField.setDisabledTextColor(Color.BLACK);

            ipField.setEditable(true);
        }
        portField = new JTextField(10);

        JSplitPane up_down = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        up_down.setDividerSize(2);
        up_down.setDividerLocation(300);
        up_down.setEnabled(false);
        getContentPane().add(up_down, BorderLayout.CENTER);

        //separator of game display and infomation display
        JSplitPane left_right = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        left_right.setDividerSize(1);
        left_right.setDividerLocation(300);
        left_right.setEnabled(false);
        up_down.setLeftComponent(left_right);
        up_down.setRightComponent(controlPanel);

        //left of separator is game diaplay panel
        left_right.setLeftComponent(gameDisplayComponent);

        //separator of information diaplay and information input palen
        JSplitPane dis_input_split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        dis_input_split.setDividerLocation(200);
        dis_input_split.setLeftComponent(chatDisplayWindow);
        left_right.setRightComponent(dis_input_split);

        //separator of information input and sent information
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(200);
        dis_input_split.setRightComponent(split);
        split.setLeftComponent(chatInputWindow);
        initSendButton(split);

        controlPanelInit();
        setTitle("SMART GAME by tuhalang ft nhanbka");
//        setIconImage(new ImageIcon("./images/sg.png").getImage());
        setResizable(false);
    }

    private void initSendButton(JSplitPane split) {
        JButton sendButton = new JButton("SEND");
        split.setRightComponent(sendButton);
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String info = chatInputArea.getText();
                chatDisplayArea.append("Server:" + info + "\r\n");
                pw.println(info);
                chatInputArea.setText("");
            }
        });
    }

    private void controlPanelInit() {
        controlPanel.setLayout(new GridLayout(1, 2));

        initDirectPanel();
        initIpPortInputPanel();

        setResizable(false);
    }

    private void initIpPortInputPanel() {
        GridBagLayout layout = new GridBagLayout();

        JPanel ipPortPanel = new JPanel();
        ipPortPanel.setLayout(layout);

        JLabel ipLabel = new JLabel("IP:");
        JLabel portLabel = new JLabel("Port:");
        connectButton = new JButton("connect");
        connectButton.addActionListener(new ConnectAction());

        constraints.anchor = GridBagConstraints.SOUTH;
        add(ipPortPanel, ipLabel, constraints, 0, 0, 1, 1);
        add(ipPortPanel, ipField, constraints, 1, 0, 5, 1);
        add(ipPortPanel, portLabel, constraints, 0, 1, 1, 1);
        add(ipPortPanel, portField, constraints, 1, 1, 5, 1);
        add(ipPortPanel, connectButton, constraints, 2, 2, 1, 1);

        controlPanel.add(ipPortPanel);
    }

    private void initDirectPanel() {
        JPanel directionPanel = new JPanel();
        JButton leftButton = new JButton("Left");
        statusButton = new JButton("");
        statusButton.setEnabled(false);
        statusButton.setBackground(Color.WHITE);

        JButton upButton = new JButton("Up");
        JButton downButton = new JButton("Down");
        JButton rightButton = new JButton("Right");
        JButton okButton = new JButton("OK");
        ActionListener moveListener = new ButtonAction();
        leftButton.addActionListener(moveListener);
        upButton.addActionListener(moveListener);
        downButton.addActionListener(moveListener);
        rightButton.addActionListener(moveListener);
        okButton.addActionListener(moveListener);

        JButton rotate1 = new JButton("<<");
        JButton rotate2 = new JButton(">>");
        rotate1.addActionListener(moveListener);
        rotate2.addActionListener(moveListener);

        GridBagLayout layout = new GridBagLayout();

        directionPanel.setLayout(layout);
        constraints.anchor = GridBagConstraints.NORTH;
        add(directionPanel, statusButton, constraints, 0, 0, 1, 2);
        add(directionPanel, leftButton, constraints, 2, 1, 1, 1);
        add(directionPanel, upButton, constraints, 3, 0, 1, 1);
        add(directionPanel, downButton, constraints, 3, 2, 1, 1);
        add(directionPanel, rightButton, constraints, 4, 1, 1, 1);
        add(directionPanel, okButton, constraints, 3, 1, 1, 1);

        add(directionPanel, rotate1, constraints, 6, 0, 1, 1);
        add(directionPanel, rotate2, constraints, 6, 2, 1, 1);
        controlPanel.add(directionPanel);
    }

    public void add(JPanel panel, Component c, GridBagConstraints constraints, int x, int y, int w, int h) {
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.gridwidth = w;
        constraints.gridheight = h;
        panel.add(c, constraints);
    }

    private class ButtonAction implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            String input = event.getActionCommand();
            System.out.println(input);
            if ("Left".equals(input)) {
                Point head = plane.getHead();
                Point tail = plane.getTail();
                boolean e1 = head.getX() == tail.getX() && head.getX() > 2;
                boolean e2 = head.getY() == tail.getY() && head.getX() > 0 && tail.getX() > 0;
                if (e1 | e2) {
                    plane = new Plane(head.getX() - 1, head.getY(), tail.getX() - 1, tail.getY());
                    gameDisplayComponent.setPlane(plane);
                    gameDisplayComponent.repaint();
                }
            }
            if ("Right".equals(input)) {
                Point head = plane.getHead();
                Point tail = plane.getTail();
                boolean e1 = head.getX() == tail.getX() && head.getX() < 11;
                boolean e2 = head.getY() == tail.getY() && head.getX() < 13 && tail.getX() < 13;
                if (e1 | e2) {
                    plane = new Plane(head.getX() + 1, head.getY(), tail.getX() + 1, tail.getY());
                    gameDisplayComponent.setPlane(plane);
                    gameDisplayComponent.repaint();
                }
            }
            if ("Up".equals(input)) {
                Point head = plane.getHead();
                Point tail = plane.getTail();
                boolean e1 = head.getX() == tail.getX() && head.getY() > 0 && tail.getY() > 0;
                boolean e2 = head.getY() == tail.getY() && head.getY() > 2;
                if (e1 | e2) {
                    plane = new Plane(head.getX(), head.getY() - 1, tail.getX(), tail.getY() - 1);
                    gameDisplayComponent.setPlane(plane);
                    gameDisplayComponent.repaint();
                }
            }
            if ("Down".equals(input)) {
                Point head = plane.getHead();
                Point tail = plane.getTail();
                boolean e1 = head.getX() == tail.getX() && head.getY() < 13 && tail.getY() < 13;
                boolean e2 = head.getY() == tail.getY() && head.getY() < 11;
                if (e1 | e2) {
                    plane = new Plane(head.getX(), head.getY() + 1, tail.getX(), tail.getY() + 1);
                    gameDisplayComponent.setPlane(plane);
                    gameDisplayComponent.repaint();
                }
            }
            if ("<<".equals(input)) {
                Point head = plane.getHead();
                Point tail = plane.getTail();
                if (head.getX() == tail.getX() && head.getY() < tail.getY()) {
                    double x1 = head.getX() - 1;
                    double y = head.getY() + 1;
                    double x2 = tail.getX() + 2;
                    if (y == 1) y++;
                    plane = new Plane(x1, y, x2, y);
                } else if (head.getX() == tail.getX() && head.getY() > tail.getY()) {
                    double x1 = head.getX() + 1;
                    double y = head.getY() - 1;
                    double x2 = tail.getX() - 2;
                    if (y == 12) y--;
                    plane = new Plane(x1, y, x2, y);
                } else if (head.getY() == tail.getY() && head.getX() > tail.getX()) {
                    double x = head.getX() - 1;
                    double y1 = head.getY() - 1;
                    double y2 = tail.getY() + 2;
                    if (x == 12) x--;
                    plane = new Plane(x, y1, x, y2);
                } else if (head.getY() == tail.getY() && head.getX() < tail.getX()) {
                    System.out.println("hh");
                    double x = head.getX() + 1;
                    double y1 = head.getY() + 1;
                    double y2 = tail.getY() - 2;
                    if (x == 1) x++;
                    plane = new Plane(x, y1, x, y2);
                }
                gameDisplayComponent.setPlane(plane);
                gameDisplayComponent.repaint();
            }
            if (">>".equals(input)) {
                Point head = plane.getHead();
                Point tail = plane.getTail();
                if (head.getX() == tail.getX() && head.getY() > tail.getY()) {
                    double x1 = head.getX() - 1;
                    double y = head.getY() - 1;
                    double x2 = tail.getX() + 2;
                    if (y == 12) y--;
                    plane = new Plane(x1, y, x2, y);
                } else if (head.getX() == tail.getX() && head.getY() < tail.getY()) {
                    double x1 = head.getX() + 1;
                    double y = head.getY() + 1;
                    double x2 = tail.getX() - 2;
                    if (y == 1) y++;
                    plane = new Plane(x1, y, x2, y);
                } else if (head.getY() == tail.getY() && head.getX() < tail.getX()) {
                    double x = head.getX() + 1;
                    double y1 = head.getY() - 1;
                    double y2 = tail.getY() + 2;
                    if (x == 1) x++;
                    plane = new Plane(x, y1, x, y2);
                } else if (head.getY() == tail.getY() && head.getX() > tail.getX()) {
                    double x = head.getX() - 1;
                    double y1 = head.getY() + 1;
                    double y2 = tail.getY() - 2;
                    if (x == 12) x--;
                    plane = new Plane(x, y1, x, y2);
                }
                gameDisplayComponent.setPlane(plane);
                gameDisplayComponent.repaint();
            }
            if ("OK".equals(input)) {
                for (int i = 0; i < 14; i++) {
                    for (int j = 0; j < 14; j++) {
                        Rectangle2D rectangle2D = new Rectangle(10 + i * 20, 10 + j * 20, 20, 20);
                        gameDisplayComponent.addSquare(rectangle2D, new Color(54, 63, 61));
                    }
                }

                gameDisplayComponent.disablePlane();
                gameDisplayComponent.repaint();
                gameDisplayComponent.addPrintWirter(pw);
                gameDisplayComponent.disableComponent();
                if (clientIsReady) {
                    pw.println("game begin");
                    chatDisplayArea.append("game begin\n");
                    chatDisplayArea.setCaretPosition(chatDisplayArea.getDocument().getLength());
                } else {
                    pw.println("server is ready");
                }
            }
        }
    }

    private class ConnectAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            System.out.println(event.getActionCommand());
            int port = Integer.valueOf(portField.getText());

            if (!serverIsOn) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            ServerSocket ss = new ServerSocket(port);
                            InetAddress localhostAddress = InetAddress.getLocalHost();
                            chatDisplayArea.append("server start up, waiting client to connect\n");
                            chatDisplayArea.append("server ip: " + localhostAddress.getHostAddress() + ", port: " + port + "\n");
                            Socket s = ss.accept();
                            System.out.println("client connect");
                            chatDisplayArea.append("client connect!!\n");
                            chatDisplayArea.append("put the plane and press OK Button to begin game\n");
                            InputStreamReader isr = new InputStreamReader(s.getInputStream());
                            BufferedReader br = new BufferedReader(isr);
                            pw = new PrintWriter(s.getOutputStream(), true);

                            while (true) {
                                String info = br.readLine();
                                if ("client is ready".equals(info)) {
                                    clientIsReady = true;
                                    chatDisplayArea.append("Opponent is ready\n");
                                } else if ("game begin".equals(info)) {
                                    chatDisplayArea.append("Game begin!\n");
                                } else if (Util.isHitAction(info)) {
                                    Matcher m = Util.HITPATTER.matcher(info);
                                    int x = -1;
                                    int y = -1;
                                    if (m.find()) {
                                        x = Integer.parseInt(m.group(1));
                                        y = Integer.parseInt(m.group(2));
                                    }
                                    Point p = new Point(x, y);
                                    if (Util.ifHitDownPlane(plane, p)) {
                                        pw.println("hitResponse:" + x + ":" + y + ":2");
                                    } else if (Util.ifHitPlane(plane, p)) {
                                        pw.println("hitResponse:" + x + ":" + y + ":1");
                                    } else {
                                        pw.println("hitResponse:" + x + ":" + y + ":0");
                                    }
                                    gameDisplayComponent.enableComponent();
                                    statusButton.setBackground(Color.GREEN);
                                } else if (Util.isHitResponseAction(info)) {
                                    Matcher m = Util.RESPONSEPATTERN.matcher(info);
                                    int x = -1, y = -1, result = -1;
                                    if (m.find()) {
                                        x = Integer.parseInt(m.group(1));
                                        y = Integer.parseInt(m.group(2));
                                        result = Integer.parseInt(m.group(3));
                                    }
                                    Rectangle2D rectangle2D = new Rectangle2D.Double(x * 20 + 10, y * 20 + 10, 20, 20);
                                    switch (result) {
                                        case 0: {
                                            gameDisplayComponent.putRectangle(rectangle2D, Color.WHITE);
                                            chatDisplayArea.append("does not hit the plane\n");
                                            break;
                                        }
                                        case 1: {
                                            gameDisplayComponent.putRectangle(rectangle2D, Color.BLUE);
                                            chatDisplayArea.append("hit the body of the plane\n");
                                            break;
                                        }
                                        case 2: {
                                            gameDisplayComponent.putRectangle(rectangle2D, Color.BLUE);
                                            chatDisplayArea.append("hit down the plane, you win\n");
                                            break;
                                        }
                                        default:
                                            break;
                                    }
                                    gameDisplayComponent.repaint();
                                    gameDisplayComponent.disableComponent();
                                    statusButton.setBackground(Color.WHITE);
                                } else
                                    chatDisplayArea.append("client:" + info + "\r\n");
                                chatDisplayArea.setCaretPosition(chatDisplayArea.getDocument().getLength());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            pw = null;
                        }
                    }
                }).start();
                serverIsOn = true;
                portField.setEnabled(false);
                portField.setDisabledTextColor(Color.BLACK);
                connectButton.setEnabled(false);
            }
        }
    }
}
