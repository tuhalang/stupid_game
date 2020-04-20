package com.model;

public class Airplane extends FlyingObject implements Enemy {

    private int speed = 2;

    public Airplane() {
        image = Config.airplane;
        width = image.getWidth();
        height = image.getHeight();
        x = (int) (Math.random() * (Config.WIDTH - this.width));
        y = -this.height;
    }

    public Airplane(int x, int y) {
        image = Config.airplane;
        width = image.getWidth();
        height = image.getHeight();
        this.x = x;
        this.y = y;
    }

    public int getScore() {
        return 5;
    }

    public void step() {
        y += speed;
    }

    public boolean outOfBounds() {
        return this.y > Config.HEIGHT;
    }
}
