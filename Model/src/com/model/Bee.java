package com.model;

import java.util.Random;

public class Bee extends FlyingObject implements Award {

    private int xSpeed = 1;
    private int ySpeed = 2;
    private int awardType;

    public Bee() {
        image = Config.bee;
        width = image.getWidth();
        height = image.getHeight();
        Random rand = new Random();
        x = rand.nextInt(Config.WIDTH - this.width);
        y = -this.height;
        awardType = rand.nextInt(2);
    }
    
    public Bee(int k) {
        image = Config.bee;
        width = image.getWidth();
        height = image.getHeight();
        Random rand = new Random();
        x = rand.nextInt(Config.WIDTH - this.width);
        y = -this.height;
        xSpeed += k;
        ySpeed += k;
        awardType = rand.nextInt(2);
    }

    public Bee(int x, int y){
        image = Config.bee;
        width = image.getWidth();
        height = image.getHeight();
        Random rand = new Random();
        this.x = x;
        this.y = y;
        awardType = rand.nextInt(2);
    }
    
    public int getType() {
        return awardType;
    }

    public void step() {
        x += xSpeed;
        y += ySpeed;
        if (x > Config.WIDTH - this.width) {
            xSpeed = -1;
        }
        if (x < 0) {
            xSpeed = 1;
        }
    }

    public boolean outOfBounds() {
        return this.y > Config.HEIGHT;
    }
}
