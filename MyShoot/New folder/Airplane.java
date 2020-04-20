package com.client.shoot;
/** �л�: ���Ƿ����Ҳ�ǵ��� */
public class Airplane extends FlyingObject implements Enemy  {
	private int speed = 2; //�߲��Ĳ���
	
	/** Airplane���췽�� */
	public Airplane(){
		image = ShootGame.airplane; //ͼƬ
		width = image.getWidth();   //��
		height = image.getHeight(); //��
		x = (int)(Math.random()*(ShootGame.WIDTH-this.width)); //0����Ļ����л���֮��������
		y = -this.height; //���ĵл��ĸ�
	}
	
	/** ��дgetScore() */
	public int getScore(){
		return 5; //���һ���л���5��
	}
	/** ��дstep() */
	public void step(){
		y += speed;  //y��
	}
	/** ��дoutOfBounds() */
	public boolean outOfBounds(){
		return this.y > ShootGame.HEIGHT; //�л���y������Ļ�ĸ�ΪԽ��
	}
}





