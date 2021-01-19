package galaxis.lee.graph;


public class Position {
	private int level;
	private int pos;
	private int junction;
	private int direction;
	private int distance;
	private int type;//0,pos;1,junction;2,charging;3,lift
	
	static public final int TPYEPOS=4;
	static public final int TYPEJUNCTION=1;
	static public final int TYPECHARGING=5;
	static public final int TPYELIFT=2;
	
	static public final int DIRECTION_FORWARD = 1;
	static public final int DIRECTION_BACK = 2;
	static public final int DIRECTION_LEFT = 3;
	static public final int DIRECTION_RIGHT = 4;
	
	public Position(){}
//	public Position(int l, int p, int j, int dir, int dis, int t){
//		level=l;
//		pos=p;
//		junction=j;
//		direction=dir;
//		distance=dis;
//		type=t;
//	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getPos() {
		return pos;
	}
	public void setPos(int pos) {
		this.pos = pos;
	}
	public int getJunction() {
		return junction;
	}
	public void setJunction(int junction) {
		this.junction = junction;
	}
	public int getDirection() {
		return direction;
	}
	public void setDirection(int direction) {
		this.direction = direction;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
}
