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
import java.util.Map;

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
    
    private Hero myHero = new Hero();
    private String myID;
    private FlyingObject[] flyings = {};
    private Bullet[] bullets = {};
    private LinkedHashMap<String, UserState> userStates = new LinkedHashMap<String, UserState>();
    private LinkedHashMap<String, Hero> heroHashMap = new LinkedHashMap<String, Hero>();
   
    static {
        try {
            background = ImageIO.read(ShootGame.class.getResource("background.jpg"));
            start = ImageIO.read(ShootGame.class.getResource("startGame.png"));
            pause = ImageIO.read(ShootGame.class.getResource("pause.png"));
            gameover = ImageIO.read(ShootGame.class.getResource("gameover.png"));
            //airplane = ImageIO.read(ShootGame.class.getResource("airplane.png"));
//            bee = ImageIO.read(ShootGame.class.getResource("bee.png"));
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
        for (Map.Entry<String, Hero> entry : heroHashMap.entrySet()) {
            String key = entry.getKey();
            Hero hero = entry.getValue();
            hero.step();
        }
        for (int i = 0; i < flyings.length; i++) {
            flyings[i].step();
        }
        for (Map.Entry<String, UserState> entry : userStates.entrySet()) {
            String key = entry.getKey();
            UserState value = entry.getValue();
            Bullet[] bulletList = value.getBullets();
            for (int i = 0; i < bulletList.length; i++) {
                bulletList[i].step();
            }
            
        }
        
    }

    public void shootAction(){
        for (Map.Entry<String, Hero> entry : heroHashMap.entrySet()) {
            String key = entry.getKey();
            shootAction(key);
        }
    }
    
    public void shootAction(String userID) {
        Hero heroToShoot = heroHashMap.get(userID);
        int shootIndex = heroToShoot.getIndexShoot();
        shootIndex++;                           // Increase bullet by 1 for each 10 miliseconds
        if (shootIndex % 30 == 0) {             // 10*30=300 (shoot every 300 miliseconds)
            Bullet[] bs = heroHashMap.get(userID).shoot();
            bullets = Arrays.copyOf(bullets, bullets.length + bs.length);
            System.arraycopy(bs, 0, bullets, bullets.length - bs.length, bs.length);
        }
        heroToShoot.setIndexShoot(shootIndex);
    }

    int score = 0;                              // initialize the score of user

    /**
     * Bullet collide with the enemy
     */
    public void bangAction() {
        for(Map.Entry<String, UserState> entry : userStates.entrySet()){
            String userID = entry.getKey();
            Bullet[] bullets = entry.getValue().getBullets();
            for (int i = 0; i < bullets.length; i++) { 
                bang(userID, bullets[i], i); 
            }
        }
    }

    /**
     * Handle when bullet hit the enemy
     */
    public void bang(String userID, Bullet b, int bulletIndex) {
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
                        heroHashMap.get(userID).addDoubleFire(); 
                        break;
                    case Award.LIFE:    
                        heroHashMap.get(userID).addLife();
                        break;
                }
            }

            FlyingObject t = flyings[index];
            flyings[index] = flyings[flyings.length - 1];
            flyings[flyings.length - 1] = t;
            
            flyings = Arrays.copyOf(flyings, flyings.length - 1);

            // remove bullet if it shoot the plane
            
            Bullet[] bulletList = userStates.get(userID).getBullets();
            Bullet[] newBullets = new Bullet[bulletList.length - 1];
            for (int i = 0; i < bulletList.length; i++) { 
                if(i < bulletIndex)
                    newBullets[i] = bulletList[i];
                else if(i == bulletIndex);
                else
                    newBullets[i - 1] = bulletList[i];
            }
            bulletList = newBullets;
            userStates.get(userID).setBullets(bulletList);
            
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
        
        for(Map.Entry<String, UserState> entry : userStates.entrySet()){
            String key = entry.getKey();
            Bullet[] bulletList = entry.getValue().getBullets();
            Bullet[] bulletLives = new Bullet[bulletList.length];
            for (int i = 0; i < bulletList.length; i++) {
                Bullet b = bulletList[i];
                if (!b.outOfBounds()) {
                    bulletLives[index++] = b;
                }
                bulletList = Arrays.copyOf(bulletLives, index);
                entry.getValue().setBullets(bulletList);
            }
        }
    }

    public void checkGameOverAction() {
        if (isGameOver(myHero)) {
            state = GAME_OVER;
        }
    }

    public boolean isGameOver(Hero hero) {
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
    private int interval = 10;

    public void action() {
        MouseAdapter l = new MouseAdapter() {
            public void mouseMoved(MouseEvent e) {
                if (state == RUNNING) { 
                    int x = e.getX(); 
                    int y = e.getY(); 
                    myHero.moveTo(x, y);
                }
            }

            public void mouseClicked(MouseEvent e) {
                switch (state) { 
                    case START:  
                        state = RUNNING;
                        break;
                    case GAME_OVER: 
                        score = 0;  
                        myHero = new Hero();
                        myID = Communication.ID;
                        flyings = new FlyingObject[0];
                        bullets = new Bullet[0];
                        state = START; 
                        break;
                }
            }

            public void mouseExited(MouseEvent e) {
                if (state == RUNNING) {
                    state = PAUSE;   
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
        }, interval, interval);
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
        g.drawString("SCORE: " + score, 10, 25); 
        g.drawString("LIFE: " + myHero.getLife(), 10, 45); 
    }

    public void paintHero(Graphics g) {
        g.drawImage(myHero.image, myHero.x, myHero.y, null);
        for (Map.Entry<String, Hero> entry : heroHashMap.entrySet()) {
            if(entry.getKey() != myID){
                Hero hero = entry.getValue();
                g.drawImage(hero.image, hero.x, hero.y, null);
            }
        }
    }

    public void paintFlyingObjects(Graphics g) {
        for (int i = 0; i < flyings.length; i++) { 
            FlyingObject f = flyings[i];
            g.drawImage(f.image, f.x, f.y, null);
        }
    }

    public void paintBullets(Graphics g) {
        for(Map.Entry<String, UserState> entry : userStates.entrySet()){
            Bullet[] bulletList = entry.getValue().getBullets();
            for (int i = 0; i < bulletList.length; i++) { 
                Bullet b = bulletList[i];
                g.drawImage(b.image, b.x, b.y, null); 
            }    
        }
    }

    private String getStateGame() {
        String state = "";
        if(!userStates.containsKey(myID)){
            int[] position = {myHero.x, myHero.y};
            UserState u = new UserState(position, bullets);
            userStates.put(myID, u);    
        }
        if(!heroHashMap.containsKey(myID)){
            heroHashMap.put(myID, myHero);
        }
        for (Map.Entry<String, UserState> entry : userStates.entrySet()) {
            String key = entry.getKey();
            Bullet[] bulletList = entry.getValue().getBullets();
            Hero hero = heroHashMap.get(key);
            state += hero.toString() + "|";
            for (Bullet b : bullets) {
                state += b.toString() + ";";
            }
            state += "|";
        }
        for (FlyingObject f : flyings) {
            state += f.toString() + ";";
        }
        return state;
    }
    
    public void setFlyings(FlyingObject[] flyings) {
        this.flyings = flyings;
    }

    public void setBullets(Bullet[] bullets) {
        this.bullets = bullets;
    }
    
    public void setUserStates(LinkedHashMap<String, UserState> userStates) {
        this.userStates = userStates;
    }

    public void setMyHero(Hero myHero) {
        this.myHero = myHero;
    }

    public void setMyID(String myID) {
        this.myID = myID;
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
