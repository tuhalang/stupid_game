package com.client.shoot;

import com.client.service.Communication;
import com.model.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Arrays;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.Font;
import java.util.LinkedHashMap;

public class ShootGame extends JPanel {

    public static final int WIDTH = 480;
    public static final int HEIGHT = 700;

    public static final int START = 0;
    public static final int RUNNING = 1;
    public static final int PAUSE = 2;
    public static final int GAME_OVER = 3;
    private int state = 0;

    public static BufferedImage background;         // the background image
    public static BufferedImage start;                   
    public static BufferedImage pause;
    public static BufferedImage gameover;
    public static BufferedImage airplane;
    public static BufferedImage bee;
    public static BufferedImage bullet;
    public static BufferedImage hero0;
    public static BufferedImage hero1;

    private Communication communication;
    private Hero hero = new Hero();
    
    private Hero[] guestHero = {};
    private LinkedHashMap<Hero, Bullet[]> guestBullet = new LinkedHashMap<>();
    private FlyingObject[] flyings = {};
    private Bullet[] bullets = {};

    static {
        try {
            background = ImageIO.read(ShootGame.class.getResource("background.jpg"));
            start = ImageIO.read(ShootGame.class.getResource("startGame.png"));
            pause = ImageIO.read(ShootGame.class.getResource("pause.png"));
            gameover = ImageIO.read(ShootGame.class.getResource("gameover.png"));
            //airplane = ImageIO.read(ShootGame.class.getResource("airplane.png"));
            bee = ImageIO.read(ShootGame.class.getResource("bee.png"));
            //bullet = ImageIO.read(ShootGame.class.getResource("bullet.png"));
            //hero0 = ImageIO.read(ShootGame.class.getResource("hero0.png"));
            //hero1 = ImageIO.read(ShootGame.class.getResource("hero1.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ShootGame() {
        communication = Communication.getIntance("127.0.0.1", 8888, this);
    }

    public static FlyingObject nextOne() {
        Random rand = new Random();
        int type = rand.nextInt(20);
        if (type == 0) {
            return new Bee();
        } else {
            return new Airplane();
        }
    }

    private int flyEnteredIndex = 0;

    public void enterAction() {
        flyEnteredIndex++;
        if (flyEnteredIndex % 40 == 0) {
            FlyingObject obj = nextOne();
            flyings = Arrays.copyOf(flyings, flyings.length + 1);
            flyings[flyings.length - 1] = obj;
        }
    }

    /* All the element make a step */
    public void stepAction() {
        hero.step();
        for (int i = 0; i < flyings.length; i++) {
            flyings[i].step();
        }
        for (int i = 0; i < bullets.length; i++) {
            bullets[i].step();
        }
    }

    int shootIndex = 0;

    public void shootAction() {
        shootIndex++;                           // Increase bullet by 1 for each 10 miliseconds
        if (shootIndex % 30 == 0) {             // 10*30=300 (shoot every 300 miliseconds)
            Bullet[] bs = hero.shoot();
            bullets = Arrays.copyOf(bullets, bullets.length + bs.length);
            System.arraycopy(bs, 0, bullets, bullets.length - bs.length, bs.length);
        }
    }

    int score = 0; //�÷�

    /**
     * Bullet collide with the enemy
     */
    public void bangAction() {
        for (int i = 0; i < bullets.length; i++) { 
            bang(bullets[i]); 
        }
    }

    /**
     * Handle when bullet hit the enemy
     */
    public void bang(Bullet b) {
        int index = -1; // mark the enemy which is shoot
        for (int i = 0; i < flyings.length; i++) { 
            FlyingObject f = flyings[i]; 
            if (f.shootBy(b)) { 
                index = i; 
                break; 
            }
        }
        if (index != -1) {
            // check if find the hit enemy
            FlyingObject one = flyings[index];      // get the hit object
            if (one instanceof Enemy) {             
                Enemy e = (Enemy) one; 
                score += e.getScore();              // achieve score
            }
            if (one instanceof Award) {  
                Award a = (Award) one; 
                int type = a.getType(); 
                switch (type) {           
                    case Award.DOUBLE_FIRE: 
                        hero.addDoubleFire(); 
                        break;
                    case Award.LIFE:    
                        hero.addLife();
                        break;
                }
            }

            FlyingObject t = flyings[index];
            flyings[index] = flyings[flyings.length - 1];
            flyings[flyings.length - 1] = t;
            
            flyings = Arrays.copyOf(flyings, flyings.length - 1);

        }
    }

    /**
     * remove the Flying Object and the Bullet out of vision
     */
    public void outOfBoundsAction() {
        int index = 0;                      // index will show the number of object within frame
        FlyingObject[] flyingLives = new FlyingObject[flyings.length]; 
        for (int i = 0; i < flyings.length; i++) { 
            FlyingObject f = flyings[i]; 
            if (!f.outOfBounds()) {  
                flyingLives[index++] = f;   // save the eneny within frame to array
                                            
            }
        }
        flyings = Arrays.copyOf(flyingLives, index);

        index = 0;
        Bullet[] bulletLives = new Bullet[bullets.length];
        for (int i = 0; i < bullets.length; i++) {
            Bullet b = bullets[i];
            if (!b.outOfBounds()) {
                bulletLives[index++] = b;
            }
        }
        bullets = Arrays.copyOf(bulletLives, index);
    }

    public void checkGameOverAction() {
        if (isGameOver()) {
            state = GAME_OVER;
        }
    }

    public boolean isGameOver() {
        for (int i = 0; i < flyings.length; i++) {
            int index = -1;
            FlyingObject f = flyings[i];
            if (hero.hit(f)) {
                index = i;
                hero.subtractLife();
                hero.setDoubleFire(0);
            }
            if (index != -1) { 
                FlyingObject t = flyings[index];
                flyings[index] = flyings[flyings.length - 1];
                flyings[flyings.length - 1] = t;
                flyings = Arrays.copyOf(flyings, flyings.length - 1);
            }
        }
        return hero.getLife() <= 0;
    }

    private Timer timer;
    private int intervel = 10;

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
                        state = RUNNING;
                        break;
                    case GAME_OVER: 
                        score = 0;  
                        hero = new Hero();
                        flyings = new FlyingObject[0];
                        bullets = new Bullet[0];
                        state = START; 
                        break;
                }
            }

            public void mouseExited(MouseEvent e) {
//                if (state == RUNNING) {
//                    state = PAUSE;   
//                }
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
            public void run() { //10
                if (state == RUNNING) { //
//                    enterAction(); //tao vat the bay
//                    stepAction();  //di chuyen
//                    shootAction(); //
//                    bangAction();  //
//                    outOfBoundsAction(); //
//                    checkGameOverAction(); //
                    String state = getStateGame();
                    communication.send("4" + state);
                }
                repaint();
            }
        }, intervel, intervel);
    }

    public void paint(Graphics g) {
        g.drawImage(background, 0, 0, null); //������ͼ
        paintHero(g); //��Ӣ�ۻ�����
        paintFlyingObjects(g); //�����˶���
        paintBullets(g); //���ӵ�����
        paintScore(g); //���ֺͻ���
        paintState(g); //��״̬
    }

    /**
     * ��״̬
     */
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

    /**
     * ���ֺͻ���
     */
    public void paintScore(Graphics g) {
        g.setColor(new Color(0xFF0000)); //������ɫ(0xFF0000Ϊ����)
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20)); //��������(Font.SANS_SERIFΪ����,Font.BOLDΪ������ʽ,20Ϊ�ֺ�)
        g.drawString("SCORE: " + score, 10, 25); //����
        g.drawString("LIFE: " + hero.getLife(), 10, 45); //����
    }

    /**
     * ��Ӣ�ۻ�
     */
    public void paintHero(Graphics g) {
        g.drawImage(hero.image, hero.x, hero.y, null);
        for (Hero ghero : this.guestHero) {
            g.drawImage(ghero.image, ghero.x, ghero.y, null);
        }
    }

    /**
     * ������
     */
    public void paintFlyingObjects(Graphics g) {
        for (int i = 0; i < flyings.length; i++) { //�������е���
            FlyingObject f = flyings[i]; //��ȡÿһ������
            g.drawImage(f.image, f.x, f.y, null); //�����˶���
        }
    }

    /**
     * ���ӵ�
     */
    public void paintBullets(Graphics g) {
        for (int i = 0; i < bullets.length; i++) { //���������ӵ�
            Bullet b = bullets[i]; //��ȡÿһ���ӵ�
            g.drawImage(b.image, b.x, b.y, null); //���ӵ�����
        }
//        for (Bullet gBullets : guestBullet) {
//            for (int i = 0; i < gBullets.length; i++) { //���������ӵ�
//                Bullet b = gBullets[i]; //��ȡÿһ���ӵ�
//                g.drawImage(b.image, b.x, b.y, null); //���ӵ�����
//            }
//        }
    }

    private String getStateGame() {
        String state = "";
        state += hero.toString() + "|";
        for (Bullet b : bullets) {
            state += b.toString() + ";";
        }
        state += "|";
        for (FlyingObject f : flyings) {
            state += f.toString() + ";";
        }
        return state;
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

    public static void main(String[] args) {
        JFrame frame = new JFrame("Fly"); 
        ShootGame game = new ShootGame(); 
        frame.add(game); 

        frame.setSize(WIDTH, HEIGHT); 
        frame.setAlwaysOnTop(true); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        frame.setLocationRelativeTo(null); 
        frame.setVisible(true); 

        game.action(); 

    }
}
