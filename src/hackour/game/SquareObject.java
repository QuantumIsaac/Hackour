package hackour.game;

public class SquareObject extends PhysicalObject{
	//NOTE: Position and size attributes are part of PhysicalObject
	private int side;
	
	private boolean jumping = false;
	private boolean first_jump = false;
	
	private GameCanvas container;
	private Game ga;
	
	private SquareObject( int side, int x, int y, GameCanvas container, Game g ){
		super();
		this.side = side;
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
	
	private boolean doGravity = true;
	
	public void doTick( Game g ){
		if( ( x  + getSide() >= container.getWidth() && velocityX > 0 ) || ( x <= 0 && velocityX < 0 ) ){
			velocityX = 0;
		}else if( (y + getSide() >= container.getHeight() && velocityY > 0 ) || (y <= 0  && velocityY < 0 ) ){
			velocityY = 0;
			if( first_jump ){
				setJumping( false );
				first_jump = false;
			}
		}else{
			if( y + getSide() + velocityY > container.getHeight() ){
				velocityY = ( container.getHeight() - (y + getSide() ) );
			}
			
			// Gravity right here.
			if( doGravity ){
				if( ! ( y + getSide() >= container.getHeight() ) ){
					velocityY = PhysicsModule.calculate_gravity( this );
				}
			}
			
			doGravity = true;
			x += velocityX;
			y += velocityY;
			
		}
	}
	
	public void jump(){
		if( !jumping ){
			if( first_jump )
				setJumping( true );
			else
				first_jump = true;
			setVelocityY( -20 );
		}
		doGravity = true;
	}
	public void setJumping( boolean j ){
		jumping = j;
	}
	public boolean getJumping(){
		return jumping;
	}
	public void goHorizontalDirection( int direction, int velX ){
		for( PhysicalObject obj : ga.GetObjectsOfType( PhysicalObject.TYPE_STATIC ) ){
			if( CollisionDetector.CheckCollision( this.getX(), this.getY(), obj.getX(), obj.getY() ) != direction ){
				velocityX = velX;
			}
		}
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
		System.out.println( direction );
		if( direction == CollisionDetector.BOTTOM ){
			setVelocityY( 0 );
			first_jump = false;
			setJumping( false );
			doGravity = false;
		}else if( direction == CollisionDetector.LEFT || direction == CollisionDetector.RIGHT ){
			setVelocityX( 0 );
		}else if( direction == CollisionDetector.TOP ){
			setVelocityY( 0 );
		}else;
	}
}