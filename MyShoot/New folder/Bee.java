package com.client.shoot;
import java.util.Random;

/** �۷�: ���Ƿ����Ҳ�ǽ��� */
public class Bee extends FlyingObject implements Award {
	private int xSpeed = 1; //x�߲��Ĳ���
	private int ySpeed = 2; //y�߲��Ĳ���
	private int awardType;  //��������(0Ϊ����ֵ��1Ϊ��)
	
	/** Bee���췽�� */
	public Bee(){
		image = ShootGame.bee; //ͼƬ
		width = image.getWidth();   //��
		height = image.getHeight(); //��
		Random rand = new Random(); //���������
		x = rand.nextInt(ShootGame.WIDTH-this.width); //0����Ļ����۷��֮��������
		y = -this.height; //�����۷�ĸ�
		awardType = rand.nextInt(2); //0��1֮����������0Ϊ����ֵ��1Ϊ��
	}
	
	/** ��дgetType() */
	public int getType(){
		return awardType; //���ؽ�������
	}
	
	/** ��дstep() */
	public void step(){
		x += xSpeed; //x��
		y += ySpeed; //y��
		if(x > ShootGame.WIDTH-this.width){ //��x������Ļ����۷����x��
			xSpeed = -1;
		}
		if(x < 0){  //��xС��0��x��
			xSpeed = 1;
		}
	}

	/** ��дoutOfBounds() */
	public boolean outOfBounds(){
		return this.y > ShootGame.HEIGHT; //�۷��y������Ļ�ĸ�ΪԽ��
	}
}





