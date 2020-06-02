package com.client.ui;

import com.client.service.Communication;
import com.model.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Arrays;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.Font;
import java.util.LinkedHashMap;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class ShootGame extends JPanel {

    public static final int WIDTH = 480;
    public static final int HEIGHT = 700;

    public static final int START = 0;
    public static final int RUNNING = 1;
    public static final int PAUSE = 2;
    public static final int GAME_OVER = 3;
    private int state = 0;
    private Boolean isReady = Boolean.FALSE;

    public static BufferedImage background;
    public static BufferedImage start;
    public static BufferedImage pause;
    public static BufferedImage gameover;
    public static BufferedImage airplane;
    public static BufferedImage bee;
    public static BufferedImage bullet;
    public static BufferedImage hero0;
    public static BufferedImage hero1;

    private static JFrame frame;
    private static JPanel mainPanel;

    private final Communication communication;
    private Hero hero = new Hero();

    private LinkedHashMap<String, Hero> guestHero = new LinkedHashMap<>();
    private LinkedHashMap<Hero, Bullet[]> guestBullet = new LinkedHashMap<>();
    private FlyingObject[] flyings = {};
    private Bullet[] bullets = {};

    private static ShootGame game;
    public static String username;
    private static String[] roomIDs;
    private static String playRoomID;

    public static int loginState;

    static {
        try {
            background = ImageIO.read(ShootGame.class.getResource("/image/background.png"));
            start = ImageIO.read(ShootGame.class.getResource("/image/start.png"));
            pause = ImageIO.read(ShootGame.class.getResource("/image/pause.png"));
            gameover = ImageIO.read(ShootGame.class.getResource("/image/gameover.png"));
            //airplane = ImageIO.read(ShootGame.class.getResource("airplane.png"));
            //bee = ImageIO.read(ShootGame.class.getResource("bee.png"));
            //bullet = ImageIO.read(ShootGame.class.getResource("bullet.png"));
            //hero0 = ImageIO.read(ShootGame.class.getResource("hero0.png"));
            //hero1 = ImageIO.read(ShootGame.class.getResource("hero1.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ShootGame() {//169.254.200.91
        communication = Communication.getIntance("127.0.0.1", 8888, this);
        loginState = 0;
    }

    public void checkGameOverAction() {
        if (hero.getLife() == 0) {
            state = GAME_OVER;
        }
    }

    int shootIndex = 0;

    public void shootAction() {
        shootIndex++;
        if ((shootIndex %= 5) == 0) {
            Bullet[] bs = hero.shoot();
            bullets = Arrays.copyOf(bullets, bullets.length + bs.length);
            System.arraycopy(bs, 0, bullets, bullets.length - bs.length, bs.length);
        }
    }

    public void setGuestHero(LinkedHashMap<String, Hero> guestHero) {
        this.guestHero = guestHero;
    }

    public void setGuestBullet(LinkedHashMap<Hero, Bullet[]> guestBullet) {
        this.guestBullet = guestBullet;
    }

    public boolean isIsReady() {
        return isReady;
    }

    public void setIsReady(boolean isReady) {
        this.isReady = isReady;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    private Timer timer;
    private int intervel = 100;

    public void action() {
        MouseAdapter l = new MouseAdapter() {
            public void mouseMoved(MouseEvent e) {
                if (state == RUNNING) {
                    int x = e.getX();
                    int y = e.getY();
                    hero.moveTo(x, y);
                }
            }

            public void mouseClicked(MouseEvent e) {
                switch (state) {
                    case START:
//                        state = RUNNING;
//                        break;
                        if(isJoinedRoom()){
                            startGamePanel();
                        }else if (isChooseRoom()) {
                            createJoinRoom();
                        } else if (isPlaying()) {
                            state = RUNNING;
                        } else {
                            createLoginForm();
                        }
                        break;
                    case GAME_OVER:
                        hero = new Hero();
                        flyings = new FlyingObject[0];
                        bullets = new Bullet[0];
                        state = START;
                        loginState = Config.LOGIN_SUCCESS;
                        break;
                }
            }

//            public void mouseExited(MouseEvent e) {
//                if (state == RUNNING) {
//                    state = PAUSE;   
//                }
//            }

//            public void mouseEntered(MouseEvent e) {
//                if (state == PAUSE) {
//                    state = RUNNING;
//                }
//            }
        };
        this.addMouseListener(l);
        this.addMouseMotionListener(l);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                if (state == RUNNING) {
                    shootAction();
                    checkGameOverAction();
                    String state = getStateGame();
                    communication.send("1" + state);
                }
                repaint();
            }
        }, intervel, intervel);
    }

    public void paint(Graphics g) {
        g.drawImage(background, 0, 0, null);
        paintHero(g);
        paintFlyingObjects(g);
        paintBullets(g);
        paintScore(g);
        paintState(g);
    }

    public void paintState(Graphics g) {
        switch (state) {
            case START:
                g.drawImage(start, 0, 0, null);
                break;
            case PAUSE:
                g.drawImage(pause, 0, 0, null);
                break;
            case GAME_OVER:
                g.drawImage(gameover, 0, 0, null);
                break;
        }
    }

    public void paintScore(Graphics g) {
        g.setColor(new Color(0xFF0000));
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        g.drawString("SCORE: " + hero.getScore(), 10, 25);
        g.drawString("LIFE: " + hero.getLife(), 10, 45);
    }

    public void paintHero(Graphics g) {
        g.drawImage(hero.image, hero.x, hero.y, null);
        for (Hero ghero : this.guestHero.values()) {
            g.drawImage(ghero.image, ghero.x, ghero.y, null);
        }
    }

    public void paintFlyingObjects(Graphics g) {
        for (int i = 0; i < flyings.length; i++) {
            FlyingObject f = flyings[i];
            if (f != null) {
                g.drawImage(f.image, f.x, f.y, null);
            }
        }
    }

    public void paintBullets(Graphics g) {
        for (int i = 0; i < bullets.length; i++) {
            Bullet b = bullets[i];
            g.drawImage(b.image, b.x, b.y, null);
        }
        for (Bullet[] gBullets : guestBullet.values()) {
            for (int i = 0; i < gBullets.length; i++) {
                Bullet b = gBullets[i];
                g.drawImage(b.image, b.x, b.y, null);
            }
        }
    }

    private String getStateGame() {
        StringBuilder stateBuilder = new StringBuilder();
        stateBuilder.append(hero.toString()).append("|");
        for (Bullet b : bullets) {
            stateBuilder.append(b.toString()).append(";");
        }
        return stateBuilder.toString();
    }

    public void setHero(Hero hero) {
        this.hero = hero;
    }

    public void setFlyings(FlyingObject[] flyings) {
        this.flyings = flyings;
    }

    public void setBullets(Bullet[] bullets) {
        this.bullets = bullets;
    }

    public static void setRoomIDs(String[] roomIDs) {
        ShootGame.roomIDs = roomIDs;
    }

    private boolean isJoinedRoom(){
        return (loginState == Config.WAIT_PLAY);
    }
    
    private boolean isChooseRoom() {
        return (loginState == Config.LOGIN_SUCCESS);
    }

    private boolean isPlaying() {
        return (loginState == Config.PLAY);
    }

    public void createJoinRoom() {
        // create JOptionPane to choose room
        
        JComboBox<String> combo = new JComboBox<>(roomIDs);
        String[] options = { "OK", "Cancel" };
        String title = "Bắn nhau đuê";
        int selection = JOptionPane.showOptionDialog(null, combo, title,
        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if(selection == JOptionPane.YES_OPTION){
            playRoomID = (String)combo.getSelectedItem();
            communication.send("02"+ playRoomID);
        } else {
            JOptionPane.showMessageDialog(null, "Không chơi thì thôi");
        }    
    }
    
    public void startGamePanel(){
        String[] options = {"Chiến thôi"};
        int x = JOptionPane.showOptionDialog(null, "Chiến không em ơi",
                "Bấm đi :)))",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        if(x == JOptionPane.YES_OPTION){
            communication.send("03" + playRoomID);
        }
    }

    public static void createLoginForm() {
        // create JOptionPane to Login or Register
        JTextField username = new JTextField();
        JTextField password = new JPasswordField();
        Object[] message = {
            "Username:", username,
            "Password:", password
        };
        int option = JOptionPane.showOptionDialog(null,
                message,
                "Feedback",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"LOGIN", "REGISTER", "CANCEL"}, // this is the array
                "default");
        if (option == JOptionPane.YES_OPTION) {
            // handle if click LOGIN
            String uname = username.getText();
            ShootGame.username = uname;
            String pwd = password.getText();
            if (uname != "" && pwd != "") {
                String mess = "";
                mess += uname + "|" + pwd;
                mess = "00" + mess;
                System.out.println(mess);
                game.communication.send(mess);
            } else {
                JOptionPane.showMessageDialog(null, "Two field must be filled");
            }
        } else if (option == JOptionPane.NO_OPTION) {
            // handle if click REGISTER
            String uname = username.getText();
            String pwd = password.getText();
            if (uname != "" && pwd != "") {
                if (uname.contains("|") || pwd.contains("|")) {
                    JOptionPane.showMessageDialog(null, "Must not include special character!");
                } else {
                    String mess = "";
                    mess += uname + "|" + pwd;
                    mess = "01" + mess;
                    System.out.println(mess);
                    game.communication.send(mess);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Two field must be filled");
            }
            System.out.println("Login canceled");
        }
    }

    public static void main(String[] args) {
        frame = new JFrame("Fly");

        game = new ShootGame();
        frame.add(game);

        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        game.action();

        createLoginForm();
    }
}
