package hackour.game;

import java.awt.Graphics;
import java.awt.Color;

public class TextComponent extends PhysicalObject{
	private String text = "";
	public TextComponent( int x, int y, String msg ){
		super( PhysicalObject.TYPE_BACKGROUND );
		this.x = x;
		this.y = y;
		text = msg;
	}
	public TextComponent( int x, int y ){
		this( x, y, "");
	}
	public String getText(){
		return text;
	}
	public void setText( String txt ){
		text = txt;
	}
	@Override
	public void paint( Graphics gfx ){
		Color old = gfx.getColor();
		gfx.setColor( Color.BLACK );
		gfx.drawString(text, ( x * HackourGame.UNIT_SIZE ) + (HackourGame.UNIT_SIZE - ( int )( (HackourGame.UNIT_SIZE / 2 ) )), ( y * HackourGame.UNIT_SIZE ) + (HackourGame.UNIT_SIZE - ( int )( (HackourGame.UNIT_SIZE / 4 ) )) );
		gfx.setColor( old );
	}
	@Override
	public void interact(){}
	@Override
	public void onCollision( PhysicalObject po, int d ){}
	@Override
	public void doTick( Game g ){}
}