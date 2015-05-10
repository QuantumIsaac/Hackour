package hackour.game;

import java.awt.Graphics;

public abstract class PhysicalObject implements GObject{
	public static final int TYPE_ENTITY = 50;
	public static final int TYPE_STATIC = 51;
	
	protected int type = PhysicalObject.TYPE_ENTITY;
	
	public int getType(){
		return type;
	}
	
	public PhysicalObject( int type ){
		this.type = type;
	}
	
	public PhysicalObject(){
		this( PhysicalObject.TYPE_ENTITY );
	}
	
	protected int x;
	protected int y;
	
	protected int width;
	protected int height;
	
	protected int velocityX;
	protected int velocityY;
	
	public int getVelocityX(){
		return velocityX;
	}
	public int getVelocityY(){
		return velocityY;
	}
	
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	
	public int getWidth(){
		return width;
	}
	public int getHeight(){
		return height;
	}
	
	public abstract void onCollision( PhysicalObject other, int direction );
	public abstract void paint( Graphics gfx );
	
	public abstract void interact();
}