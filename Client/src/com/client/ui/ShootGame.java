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
import javax.swing.Box;
import javax.swing.JLabel;
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
    private static JPanel loginPanel;
    private static LoginForm login;
    private static JPanel mainPanel;

    private final Communication communication;
    private Hero hero = new Hero();

    private LinkedHashMap<String, Hero> guestHero = new LinkedHashMap<>();
    private LinkedHashMap<Hero, Bullet[]> guestBullet = new LinkedHashMap<>();
    private FlyingObject[] flyings = {};
    private Bullet[] bullets = {};

    private static ShootGame game;
    private String username;

    public static int loginState;  // 0 - start | 1 - login success | 2 - login failed | -1 - register success | -2 register failed

    static {
        try {
            background = ImageIO.read(ShootGame.class.getResource("background.png"));
            start = ImageIO.read(ShootGame.class.getResource("start.png"));
            pause = ImageIO.read(ShootGame.class.getResource("pause.png"));
            gameover = ImageIO.read(ShootGame.class.getResource("gameover.png"));
            //airplane = ImageIO.read(ShootGame.class.getResource("airplane.png"));
            //bee = ImageIO.read(ShootGame.class.getResource("bee.png"));
            //bullet = ImageIO.read(ShootGame.class.getResource("bullet.png"));
            //hero0 = ImageIO.read(ShootGame.class.getResource("hero0.png"));
            //hero1 = ImageIO.read(ShootGame.class.getResource("hero1.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ShootGame() {
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
        if ((shootIndex %= 10) == 0) {
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
    private int intervel = 50;

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
                        if (isLogin()) {
                            state = RUNNING;
                            break;
                        } else {
                            createLoginForm();
                        }
                    case GAME_OVER:
                        hero = new Hero();
                        flyings = new FlyingObject[0];
                        bullets = new Bullet[0];
                        state = START;
                        break;
                }
            }

            public void mouseExited(MouseEvent e) {
                if (state == RUNNING) {
                    //state = PAUSE;   
                }
            }

            public void mouseEntered(MouseEvent e) {
                if (state == PAUSE) {
                    state = RUNNING;
                }
            }
        };
        this.addMouseListener(l);
        this.addMouseMotionListener(l);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                if (state == RUNNING) {
                    shootAction();
                    //checkGameOverAction();
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

    private boolean isLogin() {
        return (loginState == 1);
    }

    public static void createLoginForm() {
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
        if (option == JOptionPane.OK_OPTION) {
            String uname = username.getText();
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
            game.communication.send("00" + username.getText() + "|" + password.getText());
        } else {
            System.out.println("Login canceled");
        }
    }

    public static void main(String[] args) {
        frame = new JFrame("Fly");

        game = new ShootGame();
//        mainPanel = new JPanel();
        frame.add(game);
//        login = new LoginForm(game.communication);

        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        game.action();

        createLoginForm();
    }

    public void loginStartGame() {
        game = new ShootGame();
        mainPanel.remove(login);
        mainPanel.add(game);
        mainPanel.setSize(WIDTH, HEIGHT);
        frame.setSize(WIDTH, HEIGHT);
        frame.setAlwaysOnTop(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        game.action();

    }
}
