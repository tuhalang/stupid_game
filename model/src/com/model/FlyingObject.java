package com.model;
import java.awt.image.BufferedImage;

public abstract class FlyingObject {
	public BufferedImage image;
	public int width; 
	public int height;
	public int x;  
	public int y;  
        
	public abstract void step();
	
	public boolean shootBy(Bullet bullet){
		int x1 = this.x; 
		int x2 = this.x + this.width;
		int y1 = this.y; 
		int y2 = this.y + this.height; 
		int bx = bullet.x; 
		int by = bullet.y; 
		
		return bx>x1 && bx<x2
		       &&
		       by>y1 && by<y2; 
	}
	
	public abstract boolean outOfBounds();

    @Override
    public String toString() {
        return x+","+y;
    }
	
        
        
}







