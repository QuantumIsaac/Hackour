package hackour.levels;

import hackour.game.*;

import java.io.*;

public class LevelIO{
	public static final int CODE_BLOCK = 0;
	public static final int CODE_SWITCH = 1;
	public static final int CODE_WIRE = 2;
	public static final int CODE_DOOR = 3;
	public static final int CODE_AND = 4;
	public static final int CODE_OR = 5;
	public static final int CODE_XOR = 6;
	public static final int CODE_BLOCKWIRE = 7;
	public static final int CODE_CLOCK = 8;
	public static final int CODE_GOAL = 9;
	public static final int CODE_PLUSPORTAL = 10;
	public static final int CODE_MINUSPORTAL = 11;
	public static final int CODE_TELEPORTER = 12;
	public static final int CODE_ENEMY = 13;
	public static final int CODE_TEXT = 14;
	
	public static final int LAYER_END = 1600;
	public static final int FILE_END  = 1601;
	
	private static DataInputStream dis;
	private static DataOutputStream dos;
	
	public static Level readLevel(String file){
		try{
			dis = new DataInputStream( new FileInputStream(file) );
		}catch(IOException ioe){
			ioe.printStackTrace();
			return null;
		}
		Level out = new Level();
		Layer cur;
		
		while( true ){
			try{
				int in_int = dis.readInt();
				
				if( in_int == FILE_END ){
					break;
				}
				
				int x = in_int;
				int y = dis.readInt();
				
				cur = new Layer(x,y);
				
				int cur_val = 0;
				while( (cur_val = dis.readInt()) != LAYER_END && cur_val != FILE_END ){
					int bx = dis.readInt();
					int by = dis.readInt();
					switch(cur_val){
					case CODE_BLOCK:
						Block b = new Block( bx, by );
						cur.AddObject(b);
						break;
					case CODE_SWITCH:
						Switch s = new Switch( bx, by );
						cur.AddObject(s);
						break;
					case CODE_WIRE:
						Wire w = new Wire( bx, by );
						cur.AddObject(w);
						break;
					case CODE_DOOR:
						Door d = new Door( bx, by );
						cur.AddObject(d);
						break;
					case CODE_AND:
						ANDGate a = new ANDGate( bx, by );
						cur.AddObject(a);
						break;
					case CODE_OR:
						ORGate o = new ORGate( bx, by );
						cur.AddObject(o);
						break;
					case CODE_XOR:
						XORGate xor = new XORGate( bx, by );
						cur.AddObject(xor);
						break;
					case CODE_BLOCKWIRE:
						BlockWire bw = new BlockWire( bx, by );
						cur.AddObject(bw);
						break;
					case CODE_CLOCK:
						Clock c = new Clock( bx, by );
						cur.AddObject(c);
						break;
					case CODE_GOAL:
						Goal g = new Goal( bx, by );
						cur.AddObject(g);
						break;
					case CODE_PLUSPORTAL:
						PlusPortal pp = new PlusPortal( bx, by );
						cur.AddObject( pp );
						break;
					case CODE_MINUSPORTAL:
						MinusPortal mp = new MinusPortal( bx, by );
						cur.AddObject( mp );
						break;
					case CODE_TELEPORTER:
						int dx = dis.readInt();
						int dy = dis.readInt();
						Teleporter tp = new Teleporter( bx, by, dx, dy );
						cur.AddObject( tp );
						break;
					case CODE_ENEMY:
						Enemy e = HackourGame.RUNNING_HOST.createEnemy( bx, by );
						cur.AddObject( e );
						break;
					case CODE_TEXT:
						String st = dis.readUTF();
						TextComponent tc = new TextComponent( bx, by, st );
						cur.AddObject( tc );
						break;
					default:
						break;
					}
				}
				out.AddLayer(cur);
			}catch(IOException ioe){
				break;
			}
		}
		return out;
	}
	
	public static void writeLevel(Level l, String file){
		try{
			dos = new DataOutputStream( new FileOutputStream(file) );
			for( Layer la : l.GetLayers() ){
				write_spawn_data(la);
				for( PhysicalObject po : la.GetObjects() ){
					int code = 0;
					if( po instanceof Block ){
						code = CODE_BLOCK;
					}else if( po instanceof Switch ){
						code = CODE_SWITCH;
					}else if( po instanceof Wire ){
						code = CODE_WIRE;
					}else if( po instanceof Door ){
						code = CODE_DOOR;
					}else if( po instanceof ANDGate ){
						code = CODE_AND;
					}else if( po instanceof ORGate ){
						code = CODE_OR;
					}else if( po instanceof XORGate ){
						code = CODE_XOR;
					}else if( po instanceof BlockWire ){
						code = CODE_BLOCKWIRE;
					}else if( po instanceof Clock ){
						code = CODE_CLOCK;
					}else if( po instanceof Goal ){
						code = CODE_GOAL;
					}else if( po instanceof PlusPortal ){
						code = CODE_PLUSPORTAL;
					}else if( po instanceof MinusPortal ){
						code = CODE_MINUSPORTAL;
					}else if( po instanceof Teleporter ){
						write_teleporter( ( Teleporter ) po );
						continue;
					}else if( po instanceof Enemy ){
						code = CODE_ENEMY;
					}else if( po instanceof TextComponent){
						write_text_component( (TextComponent) po );
						continue;
					}else continue;
					
					write_object( code, po );
				}
				end_layer();
			}
			
			end_file();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}
	
	private static void write_object( int code, PhysicalObject po ) throws IOException{
		dos.writeInt(code);
		dos.writeInt(po.getX());
		dos.writeInt(po.getY());
	}
	private static void write_text_component( TextComponent tc ) throws IOException{
		write_object( CODE_TEXT, tc );
		dos.writeUTF( tc.getText() );
	}
	private static void write_teleporter( Teleporter t ) throws IOException{
		write_object( CODE_TELEPORTER, t );
		dos.writeInt(t.get_dx());
		dos.writeInt(t.get_dy());
	}
	private static void end_layer() throws IOException{
		dos.writeInt( LAYER_END );
	}
	private static void end_file() throws IOException{
		dos.writeInt( FILE_END );
		dos.close();
	}
	private static void write_spawn_data(Layer data) throws IOException{
		dos.writeInt( data.GetSpawnX() );
		dos.writeInt( data.GetSpawnY() );
	}
}