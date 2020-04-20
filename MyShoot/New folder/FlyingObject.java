package com.client.shoot;
import java.awt.image.BufferedImage;

/** ������ */
public abstract class FlyingObject {
	protected BufferedImage image; //ͼƬ
	protected int width; //��
	protected int height; //��
	protected int x;  //x����
	protected int y;  //y����

	/** ��������һ�� */
	public abstract void step();
	
	/** ���˱��ӵ�ײ this:���� bullet:�ӵ� */
	public boolean shootBy(Bullet bullet){
		int x1 = this.x;  //x1:���˵�x
		int x2 = this.x + this.width; //y1:���˵�x+���˵Ŀ�
		int y1 = this.y;  //y1:���˵�y
		int y2 = this.y + this.height; //y2:���˵�y+���˵ĸ�
		int bx = bullet.x; //bx:�ӵ���x
		int by = bullet.y; //by:�ӵ���y
		
		return bx>x1 && bx<x2
		       &&
		       by>y1 && by<y2; //bx��x1��x2֮�䣬���ң�by��y1��y2֮�������ײ����
	}
	
	/** ���������Ƿ�Խ�� */
	public abstract boolean outOfBounds();

    @Override
    public String toString() {
        return x+","+y;
    }
	
        
        
}







