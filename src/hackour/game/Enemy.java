package hackour.game;

import java.awt.Graphics;
import java.awt.Color;

public class Enemy extends SquareObject{
	private final int spawn_x;
	private final int spawn_y;
	public Enemy( int x, int y, GameCanvas container, Game g ){
		super( x, y, container, g );
		spawn_x = x;
		spawn_y = y;
		width = HackourGame.UNIT_SIZE;
		height = HackourGame.UNIT_SIZE;
		type = PhysicalObject.TYPE_ENEMY;
		velocityX = HackourGame.NORMAL_VELOCITY;
	}
	public int getSpawnX(){
		return spawn_x;
	}
	public int getSpawnY(){
		return spawn_y;
	}
	@Override
	public void kill(){
		ga.RemoveObject( this );
	}
	@Override
	public void onCollision( PhysicalObject other, int d ){
		super.onCollision( other, d );
		if( other.getType() == PhysicalObject.TYPE_ENTITY || other.getType() == PhysicalObject.TYPE_ENEMY || other.getType() == PhysicalObject.TYPE_BACKGROUND ) return;
		if( d == CollisionDetector.LEFT ) velocityX = -HackourGame.NORMAL_VELOCITY;
		else if( d == CollisionDetector.RIGHT ) velocityX = HackourGame.NORMAL_VELOCITY;
		else;
	}
	@Override
	public void doTick( Game g ){
		super.doTick( g );
	}
	@Override
	protected void checkBounds(boolean base_y_cond){
		boolean left_x = ( x <= 0 );
		boolean right_x = ( ( x + getSide() ) >= HackourGame.EAST_X );
		if( left_x || right_x ){
			if( ( left_x && velocityX < 0 ) || ( right_x && velocityX > 0 ) ) velocityX = 0;
		}
		if( base_y_cond || y <= 0 ){
			if( y <= 0 && velocityY < 0 || base_y_cond && velocityY > 0 ){
				velocityY = 0;
			}
			if( base_y_cond && velocityY > 0 ){
				onGroundHit();
			}
		}else;
		if( x + getSide() >= HackourGame.EAST_X || x <= 0 ){
			if( x + getSide() >= HackourGame.EAST_X ){
				velocityX = -HackourGame.NORMAL_VELOCITY;
			}
			if( x <= 0 ){
				velocityX = HackourGame.NORMAL_VELOCITY;
			}
		}else;
	}
	@Override
	public void paint( Graphics gfx ){
		Color old = gfx.getColor();
		gfx.setColor( Color.RED );
		gfx.fillRect( x, y, width, height );
		gfx.setColor( old );
	}
}