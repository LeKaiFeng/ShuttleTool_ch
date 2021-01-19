package galaxis.lee.error;

import java.sql.Timestamp;

public class Error {
//	int status;
//	String error_msg;
	
//	static public String getJSONReport(int level, int pos, String task){
//		return null;
//	}
	public static final int ERROR_NETWORK = 1;
	public static final int ERROR_FLASH = 2;
	public static final int ERROR_DB = 3;
	public static final int ERROR_WCS = 4;
	
	private int id;
	private String type;
	private int device_id;
	private String content;
	private Timestamp start;
	private Timestamp end;
	private int level = 0;
	private int pos = 0;
	private int dir = 0;
	private int dis = 0;
	private int action = 0;

	public int getId() {
		return id;
	}
	public String getType() {
		return type;
	}
	public int getDevice_id() {
		return device_id;
	}
	public String getContent() {
		return content;
	}
	public Timestamp getStart() {
		return start;
	}
	public Timestamp getEnd() {
		return end;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setDevice_id(int device_id) {
		this.device_id = device_id;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public void setStart(Timestamp start) {
		this.start = start;
	}
	public void setEnd(Timestamp end) {
		this.end = end;
	}
	public int getLevel() {
		return level;
	}
	public int getPos() {
		return pos;
	}
	public int getDir() {
		return dir;
	}
	public int getDis() {
		return dis;
	}
	public int getAction() {
		return action;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public void setPos(int pos) {
		this.pos = pos;
	}
	public void setDir(int dir) {
		this.dir = dir;
	}
	public void setDis(int dis) {
		this.dis = dis;
	}
	public void setAction(int action) {
		this.action = action;
	}
	
	
}
