package galaxis.lee.graph;

import java.util.ArrayList;

public class PositionManager {

	// public PositionManager(){
	// positionService=new PositionService();
	// }
	// public List<Position> getPositions(){
	// return positionService.getPositions();
	// }

	public static Boolean initPositions() {
		return true;
	}

	public static Position GetPositionFromPLCInput(int plc_location) {
		return null;
	}

	public static int getPositionID(int level, int pos) {
		return level * 1000000 + pos;
	}

	public static Position getPositionFromId(int p_id) {
		return null;
	}

	public static ArrayList<Path> getPathInSameLevel(Position start,
			Position end) {
		return  null;
	}


}
