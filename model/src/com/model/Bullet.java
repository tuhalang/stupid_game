package com.model;

public class Bullet extends FlyingObject {
	private int speed = 10;
	
	public Bullet(int x,int y){
		image = Config.bullet; 
		width = image.getWidth();  
		height = image.getHeight(); 
		this.x = x;
		this.y = y;
	}
	
	public void step(){
		y -= speed; 
	}

	public boolean outOfBounds(){
		return this.y < -this.height; 
	}

    @Override
    public String toString() {
        return x+","+y;
    }
        
        
}
