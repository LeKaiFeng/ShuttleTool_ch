package galaxis.lee.error;


public class ErrorManager {

	public static String NET_FLASH = "NETWORK-FLASH";
	public static String NET_LIFT = "NETWORK-LIFT";
	public static String DB = "DB";
	public static String FLASH = "FLASH";
	public static String WCS = "WCS";

	static public void addErrorMessage(String type, int device_id,
			String content) {
		addErrorMessage(type, device_id, content, 0, 0, 0, 0, 0);
	}
	
	static synchronized public void addErrorMessage(String type, int device_id,
			String content, int level, int pos, int dir, int dis, int action) {

	}
}
