package com.client.shoot;
import java.awt.image.BufferedImage;


public class Hero extends FlyingObject {
	private int life; 
	private int doubleFire; 
	private BufferedImage[] images = {}; 
	private int index; 


	public Hero(){
		image = ShootGame.hero0; 
		width = image.getWidth();   
		height = image.getHeight(); 
		x = 150; 
		y = 400; 
		life = 3; 
		doubleFire = 0; 
		images = new BufferedImage[]{ShootGame.hero0,ShootGame.hero1}; 
		index = 0; 
	}
	
	
	public void step(){ 
		image = images[index++/10%images.length];
		
		/*
		index++;
		int a=index/10;
		int b=a%2;
		image = images[b];
		*/
		/*
		 * 10M  index=1  a=0 b=0
		 * 20M  index=2  a=0 b=0
		 * 30M  index=3  a=0 b=0
		 * ...
		 * 100M index=10 a=1 b=1
		 * 110M index=11 a=1 b=1
		 * ...
		 * 200M index=20 a=2 b=0
		 * 210M index=21 a=2 b=0
		 * ...
		 * 300M index=30 a=3 b=1
		 * ...
		 * 400M index=40 a=4 b=0
		*/
	}


	public Bullet[] shoot(){
		int xStep = this.width/4;
		if(doubleFire > 0){ 
			Bullet[] bullets = new Bullet[2]; 
			bullets[0] = new Bullet(this.x+1*xStep,this.y-20); 
			bullets[1] = new Bullet(this.x+3*xStep,this.y-20); 
			doubleFire -= 2; 
			return bullets;  
		}else{  
			Bullet[] bullets = new Bullet[1]; 
			bullets[0] = new Bullet(this.x+2*xStep,this.y-20); 
			return bullets; 
		}
	}
	
	
	public void moveTo(int x,int y){
		this.x = x - this.width/2; 
		this.y = y - this.height/2;
	}
	
	public void addLife(){
		life++; 
	}
        
	public int getLife(){
		return life;
	}
	
	public void subtractLife(){
		life--; 
	}
	
	
	public void addDoubleFire(){
		doubleFire += 40; 
	}
	
	public void setDoubleFire(int doubleFire){
		this.doubleFire = doubleFire; 
	}
	
	
	public boolean outOfBounds(){
		return false; 
	}

	
	public boolean hit(FlyingObject other){
		int x1 = other.x-this.width/2; 
		int x2 = other.x+other.width+this.width/2;
		int y1 = other.y-this.height/2; 
		int y2 = other.y+other.height+this.height/2;
		int hx = this.x + this.width/2; 
		int hy = this.y + this.height/2;
		
		return hx>x1 && hx<x2
		       &&
		       hy>y1 && hy<y2; 
		
	}

    @Override
    public String toString() {
        return x+","+y;
    }
        
        

}




