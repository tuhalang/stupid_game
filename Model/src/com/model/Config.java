/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.model;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 *
 * @author tuhalang
 */
public class Config {

    public static final int WIDTH = 480;
    public static final int HEIGHT = 700;

    public static BufferedImage airplane;
    public static BufferedImage bee;
    public static BufferedImage bullet;
    public static BufferedImage hero0;
    public static BufferedImage hero1;

    static {
        try {
            airplane = ImageIO.read(Config.class.getResource("airplane.png"));
            bee = ImageIO.read(Config.class.getResource("bee.png"));
            bullet = ImageIO.read(Config.class.getResource("bullet.png"));
            hero0 = ImageIO.read(Config.class.getResource("hero0.png"));
            hero1 = ImageIO.read(Config.class.getResource("hero1.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
