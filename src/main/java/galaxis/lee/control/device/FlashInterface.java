package galaxis.lee.control.device;

public interface FlashInterface {
	public Boolean loadUpDown(int para, int level, int pos);
	public Boolean moveTo(int dir, long dis, int level, int pos, int weight);
	public int isTaskFinish();
	public Boolean isLowPower();
	public int getErrorInfo(int type);
	public void resetError(int mode);
	public void setAutoSend(Boolean auto);
	public void sysTaskId();
	public Boolean isRFW();
	public void clearSendingBuffer();

	public static final int RECOVERTYPE = 1;
	public static final int NEEDLOCATION = 2;
	public static final int ERRORTYPE = 3;

	public static final int TYPEPOSITION = 1;
	public static final int TYPEOTHERS = 0;
	public static final int TYPEBINFULL = 3;
	public static final int TYPEBINEMPTY = 4;

	public static final int DIRECTION_FORWARD = 1;
	public static final int DIRECTION_BACK = 2;
	public static final int DIRECTION_LEFT = 3;
	public static final int DIRECTION_RIGHT = 4;

	public static final int PICK_UP_LEFT = 1;
	public static final int PICK_UP_RIGHT = 2;
	public static final int PUT_DOWN_LEFT = 3;
	public static final int PUT_DOWN_RIGHT = 4;
	public static final int FORCE_PICKUP_LEFT = 5; //PD左取
	public static final int FORCE_PICKUP_RIGHT = 6;//PD右取

	//TODO 小工具 双深接口  原始版有用到
	public static final int PICK_UP_LEFT_2 = 11;
	public static final int PICK_UP_RIGHT_2 = 12;
	public static final int PUT_DOWN_LEFT_2 = 13;
	public static final int PUT_DOWN_RIGHT_2 = 14;

	//TODO 4.5 双深接口变动  大部分适用
	public static final int CLAMP_PICK_UP_LEFT_2 = 7;
	public static final int CLAMP_PICK_UP_RIGHT_2 = 8;
	public static final int CLAMP_PUT_DOWN_LEFT_2 = 9;
	public static final int CLAMP_PUT_DOWN_RIGHT_2 = 10;
	public static final int CLAMP_FORCE_PICKUP_LEFT_2 = 11;
	public static final int CLAMP_FORCE_PICKUP_RIGHT_2 = 12;
	
	public final static int TYPE_NOTHING = 0;
	public final static int TYPE_MOVE = 1;
	public final static int TYPE_LOAD = 2;
	public final static int TYPE_UNLOAD = 3;

	// public final static String FX_MOVE = "�ƶ�";
	// public final static String FX_FORWARD = "��ǰ";
	// public final static String FX_BACKWARD = "���";
	// public final static String FX_LEFT = "����";
	// public final static String FX_RIGHT = "����";
	//
	// public final static String FX_PICK_UP = "ȡ��";
	// public final static String FX_PICK_UP_LEFT = "��ȡ";
	// public final static String FX_PICK_UP_LEFT_2 = "��ȡ2";
	// public final static String FX_PICK_UP_RIGHT = "��ȡ";
	// public final static String FX_PICK_UP_RIGHT_2 = "��ȡ2";
	//
	//
	// public final static String FX_PUT_DOWN = "�Ż�";
	// public final static String FX_PUT_DOWN_LEFT = "���";
	// public final static String FX_PUT_DOWN_LEFT_2 = "���2";
	// public final static String FX_PUT_DOWN_RIGHT = "�ҷ�";
	// public final static String FX_PUT_DOWN_RIGHT_2 = "�ҷ�2";


}
