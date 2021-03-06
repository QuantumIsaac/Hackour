package hackour.game;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

public class SquareObject extends PhysicalObject{
	//NOTE: Position and size attributes are part of PhysicalObject
	private int side;
	
	protected boolean jumping = false;
	protected boolean first_jump = false;
	
	protected boolean adjusted = false;
	
	protected boolean edit_mode = false;
	protected boolean noClip = false;
	
	protected GameCanvas container;
	protected Game ga;
	
	private SquareObject( int side, int x, int y, GameCanvas container, Game g ){
		super();
		this.side = side;
		width = side;
		height = side;
		this.x = x;
		this.y = y;
		
		velocityX = 0;
		velocityY = 0;
		
		this.container = container;
		ga = g;
	}
	public SquareObject( int x, int y, GameCanvas container, Game g ){
		this( HackourGame.UNIT_SIZE, x, y, container, g );
	}
	public SquareObject( GameCanvas container, Game g ){
		this(0,0, container, g);
	}
	
	@Override
	public void paint( Graphics gfx ){
		Color c = gfx.getColor();
		gfx.setColor( Color.BLACK );
		gfx.fillRect( x, y, side, side );
		gfx.setColor( c );
	}
	
	private boolean doGravity = true;
	private boolean onPlatform = false;
	public boolean isOnPlatform(){
		return onPlatform;
	}
	
	public int[] CenterUnit(){
		int[] coords = new int[2];
		int usex = x + (int)Math.ceil(HackourGame.UNIT_SIZE / 2);
		int usey = y + (int)Math.ceil(HackourGame.UNIT_SIZE / 2);
		coords[0] = ( ( usex - (usex % HackourGame.UNIT_SIZE) ) / HackourGame.UNIT_SIZE) ;
		coords[1] = ( ( usey - (usey % HackourGame.UNIT_SIZE) ) / HackourGame.UNIT_SIZE);
		return coords;
	}
	
	public void doTick( Game g ){
		ArrayList<PhysicalObject> stats = ga.getStatics();
		boolean base_y_cond = (y + getSide() >= HackourGame.SOUTH_Y );
		if( !edit_mode ){
			int[] coords = CenterUnit();
			if( !base_y_cond && ( !CollisionDetector.OnBlock( coords[0], coords[1], ga) ) ){
				onPlatform = false;
			}
			
			doGravity = !onPlatform;
			
			// Gravity right here.
			if( doGravity ){
				velocityY = PhysicsModule.calculate_gravity( this );
			}
		}
		checkBounds(base_y_cond);
		
		if( !adjusted ){
			x += velocityX;
			y += velocityY;
		}else adjusted = false;
		
		if( !noClip ){
			CollisionDetector.CheckCollisionsFor( this, stats );
			CollisionDetector.CheckCollisionsFor( this, ga.getEntities() );
			if( !onPlatform ) Lookahead();
		}
		
		adjustBounds();
	}
	
	private void adjustBounds(){
		if( y + side + velocityY > HackourGame.SOUTH_Y ){
			y = HackourGame.SOUTH_Y - side;
			if( !PhysicsModule.INVERT_GRAVITY ) onGroundHit();
		}
		if( y + velocityY < HackourGame.NORTH_Y ){
			y = 0;
			if( PhysicsModule.INVERT_GRAVITY ) onGroundHit();
		}                                                      
		
		if( x + side + velocityX > HackourGame.EAST_X ){
			x = HackourGame.EAST_X - width;
		}
		if( x + velocityX < HackourGame.WEST_X ){
			x = 0;
		}
	}
	
	protected void checkBounds(boolean base_y_cond){
		boolean left_x = ( x <= 0 );
		boolean right_x = ( ( x + getSide() ) >= HackourGame.EAST_X );
		if( left_x || right_x ){
			if( ( left_x && velocityX < 0 ) || ( right_x && velocityX > 0 ) ) velocityX = 0;
		}
		if( base_y_cond || y < 0 ){
			if( y < 0 && velocityY < 0 || base_y_cond && velocityY > 0 ){
				velocityY = 0;
			}
			if( base_y_cond && velocityY > 0 ){
				onGroundHit();
			}
		}else;
	}
	
	public void EditMode(){
		edit_mode = true;
		doGravity = false;
		noClip = true;
	}
	public void PlayMode(){
		edit_mode = false;
		noClip = false;
	}
	public void ToggleMode(){
		if( !edit_mode ){
			EditMode();
		}else{
			PlayMode();
		}
	}
	
	public boolean IsEditMode(){
		return edit_mode;	
	}
	
	protected void onGroundHit(){
		if( first_jump ){
			setJumping( false );
			first_jump = false;
		}
		onPlatform = true;
		velocityY = 0;
	}
	
	private void Lookahead(){
		SquareObject character = this;
		SquareObject lookahead = new SquareObject( x + velocityX, y + velocityY, container, ga ){
			public void onCollision( PhysicalObject other, int direction){
				if( other.getType() != PhysicalObject.TYPE_STATIC ) return;
				switch(direction){
				case CollisionDetector.TOP:
					if( CollisionDetector.CheckHiddenEdge( other.getX(), other.getY(), CollisionDetector.TOP ) ) return;
					y = ( ( other.getY() * HackourGame.UNIT_SIZE ) - character.getHeight() );
					break;
				case CollisionDetector.BOTTOM:
					if( CollisionDetector.CheckHiddenEdge( other.getX(), other.getY(), CollisionDetector.BOTTOM ) ) return;
					y = ( ( other.getY() * HackourGame.UNIT_SIZE ) + other.getHeight() );
					break;
				case CollisionDetector.LEFT:
					if( CollisionDetector.CheckHiddenEdge( other.getX(), other.getY(), CollisionDetector.LEFT ) ) return;
					x = ( ( other.getX() * HackourGame.UNIT_SIZE ) - character.getWidth() );
					break;
				case CollisionDetector.RIGHT:
					if( CollisionDetector.CheckHiddenEdge( other.getX(), other.getY(), CollisionDetector.RIGHT ) ) return;
					x = ( (other.getX() * HackourGame.UNIT_SIZE) + other.getWidth() );
					break;
				default:
					return;
				}
				character.setX( x );
				character.setY( y );
				character.onCollision( other, direction );
			}
		};
		
		CollisionDetector.CheckCollisionsFor( lookahead, ga.getStatics() );
	}
	private String getDirection( int d ){
		switch(d){
		case CollisionDetector.RIGHT:
			return "RIGHT";
		case CollisionDetector.LEFT:
			return "LEFT";
		case CollisionDetector.TOP:
			return "TOP";
		case CollisionDetector.BOTTOM:
			return "BOTTOM";
		}
		return "NONE";
	}
	protected boolean dead = false;
	public boolean isDead(){
		return dead;
	}
	public void setDead( boolean d ){
		dead = d;
	}
	public void kill(){
		dead = true;
		ga.repaint();
		try{
			Thread.sleep( 750 );
		}catch(Exception e){}
		dead = false;
		HackourGame.RUNNING_HOST.respawn();
	}
	public void jump(){
		if( !jumping ){
			if( first_jump )
				setJumping( true );
			else
				first_jump = true;
			if( !PhysicsModule.INVERT_GRAVITY ) setVelocityY( -HackourGame.JUMP_VELOCITY );
			else{
				setVelocityY( HackourGame.JUMP_VELOCITY );
			}
		}
		doGravity = true;
		onPlatform = false;
	}
	public void setJumping( boolean j ){
		jumping = j;
	}
	public boolean getJumping(){
		return jumping;
	}
	public void goHorizontalDirection( int direction, int velX ){
		//direction = (direction == CollisionDetector.RIGHT) ? CollisionDetector.LEFT : CollisionDetector.RIGHT;
		int ex_x = 1;
		if( direction == CollisionDetector.LEFT ) ex_x = -1;
		PhysicalObject obj = ga.get_unit( x + ex_x, y );
		if( obj != null && x + velocityX >= obj.getX() ){
			return;
		}
		velocityX = velX;
	}
	
	public void setVelocityX( int vel ){
		velocityX = vel;
	}
	public void setVelocityY( int vel ){
		velocityY = vel;
	}

	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public int getSide(){
		return side;
	}
	
	public void setX( int x ){
		this.x = x;
	}
	public void setY( int y ){
		this.y = y;
	}
	public void setSide( int s ){
		this.side = s;
	}
	
	public void onCollision( PhysicalObject other, int direction ){
		if( other.getType() == PhysicalObject.TYPE_STATIC ){
			if( direction == CollisionDetector.TOP ){
				y = (other.getY() * HackourGame.UNIT_SIZE) - height;
				if( !onPlatform && !PhysicsModule.INVERT_GRAVITY ) onGroundHit();
				else setVelocityY(0);
			}else if( direction == CollisionDetector.LEFT || direction == CollisionDetector.RIGHT ){
				if( direction == CollisionDetector.LEFT ){
					x = (other.getX() * HackourGame.UNIT_SIZE) - HackourGame.UNIT_SIZE;
				}else{
					x = (other.getX() * HackourGame.UNIT_SIZE ) + HackourGame.UNIT_SIZE;
				}
				setVelocityX( 0 );
			}else if( direction == CollisionDetector.BOTTOM ){
				if( PhysicsModule.INVERT_GRAVITY ) onGroundHit();
				else setVelocityY(0);
			}else;
		}else if( other.getType() == PhysicalObject.TYPE_ENEMY && type != PhysicalObject.TYPE_ENEMY && !IsEditMode() ){
			if( direction != 0 && direction != CollisionDetector.TOP ){
				kill();
			}
			if( direction == CollisionDetector.TOP ){
				if( other instanceof Enemy ){
					( (Enemy) other ).kill();
					if( jumping ) jumping = false;
					jump();
				}
			}
		}
	}
	
	public void interact(){}
}