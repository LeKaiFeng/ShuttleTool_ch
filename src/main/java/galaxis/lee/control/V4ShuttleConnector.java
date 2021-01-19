package galaxis.lee.control;

import galaxis.lee.control.device.ShuttleInsideState;
import galaxis.lee.control.device.FlashInterface;
import galaxis.lee.tcp.TcpClient;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @Author: Lee
 * @Date: Created in 16:44 2020/8/4
 * @Description: TODO 正常 原始  老版 稳定
 *                    小工具
 */
public class V4ShuttleConnector implements FlashInterface {

    protected TcpClient m_client;
    protected int flash_id;
    protected int task_id;
    protected int ST_id;
    protected Timer m_timer;
    protected Thread m_thread;

    protected byte error_info;
    public static final byte ERROR_SD_ID = (byte) 140;

    protected static long process_timeout;
    protected static long nosending_timeout;
    protected int send_length = 40;
    protected int receive_length = 10;
    protected int sensor_info = 16;
    protected int error_length = 48;
    protected Object m_object;
    protected ReceiveThread rt;
    protected int task_type;
    protected ShuttleInsideState i_state;
    protected int remote_task_id = 1;

    protected Long process_start;

    protected Boolean busy = false;
//	protected Boolean resend = false;
//	protected byte[] resend_buffer;
//	protected int resend_delay;
//	protected long start_time = 0;

    protected boolean isConnect = false;

    //todo 通过小车运行状态  busy 判断是否重发
    public volatile boolean reSend = true;
    //重发计时  超过30S 停止发送
    protected Long time_start;
    protected Long time_end;
    protected Long reSend_timeout = 30000L;


    public V4ShuttleConnector(String ip, int port, int id) {
        m_client = new TcpClient(ip, port);
        this.flash_id = id;
        this.task_id = 12;
        this.ST_id = 1500;
        this.error_info = 0;
        this.process_start = 0L;
        this.i_state = new ShuttleInsideState(error_length, this.flash_id);
        this.m_object = new Object();
        this.m_timer = new Timer("Timer-FlashConnector-" + id);
        this.rt = new ReceiveThread();
        this.m_thread = new Thread(this.rt);
        this.m_thread.setName("FlashConnector-" + id);
        this.m_timer.schedule(new STSend(), 0L, 3000L);
        this.m_thread.start();
        this.task_type = TYPE_NOTHING;
        int t = 90000;
        nosending_timeout = t - 30000;
        process_timeout = t;
    }


    @Override
    public Boolean loadUpDown(int para, int level, int pos) {
        return true;
    }

    @Override
    public Boolean moveTo(int dir, long dis, int level, int pos, int weight) {
        return true;
    }

    @Override
    public int isTaskFinish() {
        return 1;
    }

    @Override
    public Boolean isLowPower() {
        return null;
    }

    @Override
    public int getErrorInfo(int type) {
        return 0;
    }

    @Override
    public void resetError(int mode) {

    }

    @Override
    public void setAutoSend(Boolean auto) {

    }

    @Override
    public void sysTaskId() {

    }

    @Override
    public Boolean isRFW() {
        return null;
    }

    @Override
    public void clearSendingBuffer() {

    }

    class STSend extends TimerTask {
        @Override
        public void run() {

        }
    }

    class ReceiveThread implements Runnable {

        @Override
        public void run() {

        }
    }
}
