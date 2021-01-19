package galaxis.lee.graph;

public class Path {
	public final int type;
	public final int direction;
	public final int distance;
	public final Position start;
	public final Position end;
	
	public Path(int t, int dir, int dis, Position s, Position e){
		type=t;
		direction=dir;
		distance=dis;
		start = s;
		end = e;
	}
}
