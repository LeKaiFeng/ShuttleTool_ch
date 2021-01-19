package galaxis.lee.control.device;

public class shuttleAction {
	public int type;//1 move;2 load/unload;//0 sleep
	public int action;//1-4 for both move/action
	public int length;//length or time for sleep
	
	public shuttleAction(int t, int a, int l){
		type = t;
		action = a;
		length = l;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}
}
