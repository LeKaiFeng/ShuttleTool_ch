package galaxis.lee.control;

import galaxis.lee.control.device.FlashReport;
import galaxis.lee.control.device.ShuttleInsideState;
import galaxis.lee.control.device.FlashInterface;
import galaxis.lee.error.ErrorManager;
import galaxis.lee.graph.PositionManager;
import galaxis.lee.log.LogManager;
import galaxis.lee.tcp.TcpClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Author: Lee
 * @Date: Created in 16:44 2020/8/4
 * @Description: TODO 变动  增加重连机制
 *                   V shuttle4.5
 */
public class V4ShuttleConnector2 implements FlashInterface {

    public TcpClient m_client;
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
    protected Boolean positioning = false;
    // protected flashScannerInterface m_flash_scanner = null;
    protected final int safty_time = 3000;


    //通过收到的任务 TaskID 判断是否重发
    // public volatile byte[] reSendBt = new byte[receive_length];
    //todo 通过小车运行状态  busy 判断是否重发
    public volatile boolean reSend = true;
    //重发计时  超过30S 停止发送
    protected Long time_start;
    protected Long time_end;
    protected Long reSend_timeout = 30000L;
    protected Thread m_tcp;

    // protected Boolean resend = false;
    // protected byte[] resend_buffer;
    // protected int resend_delay;
    // protected long start_time = 0;
    public volatile boolean connectState = false;


    public boolean isConnectState() {
        return connectState;
    }

    public V4ShuttleConnector2(String ip, int port, int id) {
        m_tcp = new Thread(Thread.currentThread().getThreadGroup(), () -> {
            this.m_client = new TcpClient(ip, port);
        }, Thread.currentThread().getName());
        m_tcp.start();

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
        // this.m_timer.schedule(new STSend(), 0L, Integer.valueOf(ConfigureManager.getValue("Timer_times")));
        this.m_timer.schedule(new STSend(), 0L, 3000L);
        this.m_thread.start();
        this.task_type = TYPE_NOTHING;
        // this.m_flash_scanner = fcp;
//		this.resend_buffer = new byte[this.send_length];
//		Arrays.fill(resend_buffer, (byte)0);
        // int t = 0;
        // try {
        //     t = Integer.valueOf(ConfigureManager.getValue("flash_nettimeout"));
        //     if (t < 60000)
        //         t = 60000;
        //     if (t > 240000)
        //         t = 240000;
        // } catch (Exception e) {
        //     LogManager.getLogger().debug(e.toString());
        //     t = 90000;
        // }
        int t = 90000;
        nosending_timeout = t - 30000;
//		this.resend_delay = 10000;
        process_timeout = t;
    }


    @Override
    public Boolean loadUpDown(int para, int level, int pos) {
        reSend = true; //初始化 每个任务的发送状态
        if (para < 0 || para > 12)
            return false;

        if (!isRFW())
            return false;

        if (task_id >= 127)
            task_id = 1;
        else
            task_id = task_id + 1;
        LogManager.getLogger().debug("Task id is " + task_id);
        byte[] data = new byte[V4ShuttleConnector2.this.send_length];
        Arrays.fill(data, (byte) 0);
        data[0] = ((byte) (this.task_id / 256));
        data[1] = ((byte) (this.task_id % 256));
        data[2] = ((byte) (this.flash_id / 256));
        data[3] = ((byte) (this.flash_id % 256));

        if (para == PICK_UP_LEFT) {
            task_type = TYPE_LOAD;
            data[5] = 3;// load
            data[33] = 1;// left
        } else if (para == PICK_UP_RIGHT) {
            task_type = TYPE_LOAD;
            data[5] = 3;// load
            data[33] = 2;// right
        } else if (para == PUT_DOWN_LEFT) {
            task_type = TYPE_UNLOAD;
            data[5] = 4;// unload
            data[33] = 3;// left
        } else if (para == PUT_DOWN_RIGHT) {
            task_type = TYPE_UNLOAD;
            data[5] = 4;// unload
            data[33] = 4;// right
        } else if (para == FORCE_PICKUP_LEFT) {
            task_type = TYPE_LOAD;
            data[5] = 3;// load
            data[33] = 5;// left
        } else if (para == FORCE_PICKUP_RIGHT) {
            task_type = TYPE_LOAD;
            data[5] = 3;// load
            data[33] = 6;// right
        } else if (para == CLAMP_PICK_UP_LEFT_2) {
            task_type = TYPE_LOAD;
            data[5] = 3;// load
            data[33] = 7;// left
        } else if (para == CLAMP_PICK_UP_RIGHT_2) {
            task_type = TYPE_LOAD;
            data[5] = 3;// load
            data[33] = 8;// right
        } else if (para == CLAMP_PUT_DOWN_LEFT_2) {
            task_type = TYPE_UNLOAD;
            data[5] = 4;// unload
            data[33] = 9;// left
        } else if (para == CLAMP_PUT_DOWN_RIGHT_2) {
            task_type = TYPE_UNLOAD;
            data[5] = 4;// unload
            data[33] = 10;// right
        } else if (para == CLAMP_FORCE_PICKUP_LEFT_2) {
            task_type = TYPE_UNLOAD;
            data[5] = 4;// unload
            data[33] = 11;// right
        } else if (para == CLAMP_FORCE_PICKUP_RIGHT_2) {
            task_type = TYPE_UNLOAD;
            data[5] = 4;// unload
            data[33] = 12;// right
        } else
            return false;
        i_state.setState(ShuttleInsideState.PROCESS_STATE);
        time_start = System.currentTimeMillis();
        int times_load = 0;
        while (true) {
            if (connectState) {
                try {
                    // if ("Y".equals(ConfigureManager.getValue("Cartest"))) {
                    if (true) {
                        String testMessage = "";
                        for (byte t : data) {
                            // message = message + " " + t;
                            //System.out.print(t + " ");
                            int m = t & 0xFF;
                            testMessage = testMessage + " " + Integer.toHexString(m);
                        }
                        LogManager.getLogger().debug("send loadUpDown " + testMessage);
                    }
                    busy = false;
                    LogManager.getLogger().debug("Set Busy Free as start");
                    this.m_client.sendContent(data);
                    LogManager.getLogger().debug("send success");
                    time_end = System.currentTimeMillis();
                    //				for(int n=0;n<this.send_length;n++){
                    //					this.resend_buffer[n] = data[n];
                    //				}
                    //				this.resend = false;
                    //				start_time = System.currentTimeMillis();
                    //TODO 任务重发
                    Thread.sleep(500);
                    if (reSend) {

                        if (time_end - time_start > reSend_timeout) {  //重发超过30S 当前任务就停止发送 ，并将小车置为异常状态，中断下次任务的发送
                            // i_state.setState(ShuttleInsideState.ERROR_STATE);
                            LogManager.getLogger().debug("reSend >>> time out , break");
                            break;
                        }
                        times_load++;
                        LogManager.getLogger().debug("reSend >>> " + "times:" + times_load);
                    } else {
                        LogManager.getLogger().debug("next task >>>");
                        break;
                    }
                    // break;
                } catch (IOException | InterruptedException e) {

                    LogManager.getLogger().debug(e.toString());
                    ErrorManager.addErrorMessage(ErrorManager.NET_FLASH, this.flash_id, e.getMessage());
                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException e1) {
                        LogManager.getLogger().debug(e1.toString());
                    }
                }
            } else {
                LogManager.getLogger().debug("the loadupdown not send ");
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        process_start = System.currentTimeMillis();
        // i_state.setState(ShuttleInsideState.PROCESS_STATE);
        i_state.setActionForErrorRecord(level, pos, 0, 0, para);
        return true;
    }

    @Override
    public Boolean moveTo(int dir, long dis, int level, int pos, int weight) {
        reSend = true; //初始化 每个任务的发送状态
        if (dir < 0 || dir > 4)
            return false;

        if (!isRFW())
            return false;

        if (task_id >= 127)
            task_id = 1;
        else
            task_id = task_id + 1;
        // LogManager.getLogger().debug("Task id is " + task_id);

        long real_pos = dis;
        if (weight > 0 && dis > 2000) {// more than 2 meter
            real_pos = (int) (real_pos * (1.0 - (double) weight / 20000.0));// 10Kg->0.0005
            if (dis - real_pos > 20) {
                real_pos = dis - 20;// no more than 20mm
            }
        }
        dis = real_pos;

        byte[] data = new byte[V4ShuttleConnector2.this.send_length];
        Arrays.fill(data, (byte) 0);
        data[0] = ((byte) (this.task_id / 256));
        data[1] = ((byte) (this.task_id % 256));
        data[2] = ((byte) (this.flash_id / 256));
        data[3] = ((byte) (this.flash_id % 256));

        long dis_data;
        if (dir == DIRECTION_BACK || dir == DIRECTION_RIGHT)
            dis_data = -dis;
        else
            dis_data = dis;

        if (dir == DIRECTION_BACK || dir == DIRECTION_FORWARD) {
            //barcode check
            // data[31] = (byte) (barcode & 0xFF);
            // data[30] = (byte) ((barcode >> 8) & 0xFF);
            // data[29] = (byte) ((barcode >> 16) & 0xFF);
            // data[28] = (byte) ((barcode >> 24) & 0xFF);
            data[19] = (byte) (dis_data & 0xFF);
            data[18] = (byte) ((dis_data >> 8) & 0xFF);
            data[17] = (byte) ((dis_data >> 16) & 0xFF);
            data[16] = (byte) ((dis_data >> 24) & 0xFF);
            data[15] = (byte) (dis_data >> 32 & 0xFF);
            data[14] = (byte) ((dis_data >> 40) & 0xFF);
            data[13] = (byte) ((dis_data >> 48) & 0xFF);
            data[12] = (byte) ((dis_data >> 56) & 0xFF);
            data[5] = 1;// Move straight
        } else {
            //barcode check
            // data[31] = (byte) (barcode & 0xFF);
            // data[30] = (byte) ((barcode >> 8) & 0xFF);
            // data[29] = (byte) ((barcode >> 16) & 0xFF);
            // data[28] = (byte) ((barcode >> 24) & 0xFF);
            data[27] = (byte) (dis_data & 0xFF);
            data[26] = (byte) ((dis_data >> 8) & 0xFF);
            data[25] = (byte) ((dis_data >> 16) & 0xFF);
            data[24] = (byte) ((dis_data >> 24) & 0xFF);
            data[23] = (byte) (dis_data >> 32 & 0xFF);
            data[22] = (byte) ((dis_data >> 40) & 0xFF);
            data[21] = (byte) ((dis_data >> 48) & 0xFF);
            data[20] = (byte) ((dis_data >> 56) & 0xFF);
            data[5] = 2;// Move cross
        }
        i_state.setState(ShuttleInsideState.PROCESS_STATE);
        int times_move = 0;
        time_start = System.currentTimeMillis();
        while (true) {
            if (connectState) {
                try {
                    // if ("Y".equals(ConfigureManager.getValue("Cartest"))) {
                    if (true) {
                        String testMessage = "";
                        for (byte t : data) {
                            // message = message + " " + t;
                            //System.out.print(t + " ");
                            int m = t & 0xFF;
                            testMessage = testMessage + " " + Integer.toHexString(m);
                        }
                        LogManager.getLogger().debug("send moveTo" + testMessage);
                    }
                    busy = false;
                    LogManager.getLogger().debug("Set Busy Free as start");
                    this.m_client.sendContent(data);
                    LogManager.getLogger().debug("send success!");
                    time_end = System.currentTimeMillis();
                    // connectState = false;
//				for(int n=0;n<this.send_length;n++){
//					this.resend_buffer[n] = data[n];
//				}
//				this.resend = false;
//				start_time = System.currentTimeMillis();
                    //TODO 任务重发
                    Thread.sleep(500);

                 /* //TODO 一、根据任务id
                   int reSendTaskId = reSendBt[0] * 256 + reSendBt[1];
                    // LogManager.getLogger().debug("reSendId:"+reSendTaskId+" , taskId:"+task_id+" ,reSendBt "+Arrays.toString(reSendBt));
                    if (reSendTaskId == task_id || reSendBt[9] == 2) {
                        LogManager.getLogger().debug("next task >>>");
                        break;
                    } else {
                        times2 += 500;
                        if (times2 >= 90000) {
                            LogManager.getLogger().debug("reSend >>> time out , break  ");
                            break;
                        }
                        LogManager.getLogger().debug("reSend >>>  " + "times: " + times2 / 500 + " , " + Arrays.toString(data));
                    }*/
                    //todo 二、根据小车的动作状态
                    if (reSend) {
                        if (time_end - time_start > reSend_timeout) {  //重发超过30S 当前任务就停止发送 ，并将小车置为异常状态，中断下次任务的发送
                            // i_state.setState(ShuttleInsideState.ERROR_STATE);
                            LogManager.getLogger().debug("reSend >>> time out , break");
                            break;
                            // return false;
                        }
                        times_move++;
                        LogManager.getLogger().debug("reSend >>> " + "times:" + times_move);

                    } else {
                        LogManager.getLogger().debug("next task >>>");

                        break;
                    }

                    // break;
                } catch (IOException | InterruptedException e) {

                    LogManager.getLogger().debug(e.toString());
                    ErrorManager.addErrorMessage(ErrorManager.NET_FLASH, this.flash_id, e.getMessage());
                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException e1) {
                        LogManager.getLogger().debug(e1.toString());
                    }
                }
            } else {
                LogManager.getLogger().debug("Invalid connect state");
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e1) {
                    LogManager.getLogger().debug(e1.toString());
                }
            }
        }
        process_start = System.currentTimeMillis();
        LogManager.getLogger().debug("process_start" + process_start);
        // i_state.setState(ShuttleInsideState.PROCESS_STATE);
        i_state.setActionForErrorRecord(level, pos, dir, (int) dis, 0);
        return true;
    }

    @Override
    public int isTaskFinish() {
        long start = System.currentTimeMillis();
        long now = System.currentTimeMillis();
        while (true) {
            if (i_state.getState() == ShuttleInsideState.FINISH_STATE) {
                process_start = 0L;
                task_type = TYPE_NOTHING;
                return FlashReport.SHUTTLE_FINISH;
            }
            if (i_state.getState() == ShuttleInsideState.ERROR_STATE) {
                process_start = 0L;
                return FlashReport.PROCESS_ERROR;
            }
            now = System.currentTimeMillis();
            if (now - start > process_timeout) {
                // return FlashReport.PROCESS_TIMEOUT;
                LogManager.getLogger().debug(
                        "As hard timeout, Change state into error");
                i_state.setState(ShuttleInsideState.ERROR_STATE);

                reSend = false;
                LogManager.getLogger().debug("change reSend state false by timeout");

                if (task_type == TYPE_MOVE) {
                    i_state.setError_type(ShuttleInsideState.ERROR_NEED_LOCATION);
                } else if (task_type == TYPE_LOAD) {
                    i_state.setError_type(ShuttleInsideState.ERROR_POSSIABLE_EMPTY);
                } else if (task_type == TYPE_UNLOAD) {
                    i_state.setError_type(ShuttleInsideState.ERROR_POSSIABLE_FULL);
                } else {
                    i_state.setError_type(ShuttleInsideState.ERROR_OTHERS);
                }
                process_start = 0L;
                return FlashReport.PROCESS_ERROR;
            }
            try {
                Thread.sleep(200L);
            } catch (InterruptedException e) {
                LogManager.getLogger().debug(e.toString());
            }
        }
    }

    @Override
    public Boolean isLowPower() {
        return i_state.getIs_lowPower();
    }

    @Override
    public int getErrorInfo(int type) {
        if (type == FlashInterface.RECOVERTYPE) {
            return i_state.getRecover_mode();
        } else if (type == FlashInterface.NEEDLOCATION) {
            i_state.refreshPosition();
            int level = i_state.getResume_level();
            int pos = i_state.getResume_pos();
            int p_id = PositionManager.getPositionID(level, pos);
            LogManager.getLogger().debug("Relocation id is " + p_id);
            return (int) p_id;
        } else if (type == FlashInterface.ERRORTYPE) {
            int e_type = i_state.getError_type();
            if (e_type == ShuttleInsideState.ERROR_NEED_LOCATION) {
                LogManager.getLogger().debug(
                        "Return error type as relocation");
                return FlashInterface.TYPEPOSITION;
            } else if (e_type == ShuttleInsideState.ERROR_BINEMPTY) {
                LogManager.getLogger().debug(
                        "Return error type as bin empty");
                return FlashInterface.TYPEBINEMPTY;
            } else if (e_type == ShuttleInsideState.ERROR_BINFULL) {
                LogManager.getLogger()
                        .debug("Return error type as bin full");
                return FlashInterface.TYPEBINFULL;
            } else {
                LogManager.getLogger().debug("Return error type as others");
                return FlashInterface.TYPEOTHERS;
            }
        } else {
            return 0;
        }
    }

    @Override
    public void resetError(int mode) {
        if (mode == 0) {
            i_state.setError_type(ShuttleInsideState.ERROR_NOERROR);
        }
        i_state.resetResume();
    }

    @Override
    public void setAutoSend(Boolean auto) {
        if (auto == true) {
            this.sysTaskId();
        }
    }

    @Override
    public void sysTaskId() {
        this.task_id = this.remote_task_id + 1;
        LogManager.getLogger().debug("Remote Task id is " + this.remote_task_id);
    }

    @Override
    public Boolean isRFW() {
        if (i_state.getWorkmode() == ShuttleInsideState.AUTOMODE
                && !i_state.getIs_error()
                && i_state.getErrorMessage().equals("")) {
            return true;
        } else {
            LogManager.getLogger().debug("RFW is false");
            return false;
        }
    }

    @Override
    public void clearSendingBuffer() {

    }

    class STSend extends TimerTask {
        public void run() {
//			if(i_state.getState() == ShuttleInsideState.PROCESS_STATE){
//				if(busy = false){
//				}
//			}
            // GISLogManager.getLogger().debug("process_start"+process_start);
		/*	if (process_start != 0L && System.currentTimeMillis() - process_start < nosending_timeout) {
				return;
			}*/
            // if(Shuttle2Connector.this.ST_id >13){
            // return;
            // }
            if (!connectState) {
                return;
            }
            if (V4ShuttleConnector2.this.ST_id >= 2000) {
                V4ShuttleConnector2.this.ST_id = 1500;
            } else {
                V4ShuttleConnector2.this.ST_id += 1;
            }
            LogManager.getLogger().debug("ST-----------------------" + ST_id);
            byte[] data = new byte[V4ShuttleConnector2.this.send_length];
            Arrays.fill(data, (byte) 0);
            data[0] = ((byte) (V4ShuttleConnector2.this.ST_id / 256));
            data[1] = ((byte) (V4ShuttleConnector2.this.ST_id % 256));
            data[2] = ((byte) (V4ShuttleConnector2.this.flash_id / 256));
            data[3] = ((byte) (V4ShuttleConnector2.this.flash_id % 256));
            // data[4] = 'S';
            data[5] = 5;// Detail data request
            while (true) {
                try {
                    // if ("Y".equals(ConfigureManager.getValue("Cartest"))) {
                    if (connectState) {
                        String testMessage = "";
                        for (byte t : data) {
                            // message = message + " " + t;
                            //System.out.print(t + " ");
                            int m = t & 0xFF;
                            testMessage = testMessage + " " + Integer.toHexString(m);
                        }
                        LogManager.getLogger().debug("STsend " + testMessage);
                        V4ShuttleConnector2.this.m_client.sendContent(data);
                    }
                    // connectState = false;
                    break;
                } catch (IOException e) {

                    connectState = false;
                    // try {
                    // V4ShuttleConnector2.this.m_client.reConnect();
                    // } catch (IOException e2) {
                    // // TODO Auto-generated catch block
                    // e2.printStackTrace();
                    // }
                    // TODO Auto-generated catch block
                    LogManager.getLogger().debug(e.toString());
                    ErrorManager.addErrorMessage(ErrorManager.NET_FLASH, V4ShuttleConnector2.this.flash_id,
                            e.getMessage());
                    try {
                        // Thread.sleep(1000L);
                        Thread.sleep(5000L);
                    } catch (InterruptedException e1) {
                        LogManager.getLogger().debug(e1.toString());
                    }
                }
            }
        }
    }

    class ReceiveThread implements Runnable {

        private Boolean is_break = Boolean.valueOf(false);

        public void setBreak() {
            this.is_break = Boolean.valueOf(true);
        }

        public void run() {
            do {
                byte[] receive = null;
                try {
                    if (m_client != null) {
                        receive = V4ShuttleConnector2.this.m_client.getMessage(receive_length);
                    }
                    // receive = Shuttle2Connector.this.m_client.getMessage();
                    String message = "";
                    if (receive != null) {
                        for (byte t : receive) {
                            // message = message + " " + t;
                            //System.out.print(t + " ");
                            int m = t & 0xFF;
                            message = message + " " + Integer.toHexString(m);
                        }
                        LogManager.getLogger().debug(message);
                    }
                } catch (IOException e) {
                    LogManager.getLogger().error(e.toString());
                    ErrorManager.addErrorMessage(ErrorManager.NET_FLASH, V4ShuttleConnector2.this.flash_id,
                            e.getMessage());
                    try {
                        V4ShuttleConnector2.this.m_client.reConnect();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                    try {
                        Thread.sleep(5000L);
                    } catch (InterruptedException e1) {
                        LogManager.getLogger().error(e1.toString());
                    }
                }
                if (receive != null) {
                    // 仅获取小车动作响应数据,判断重发依据  {0 e 0 8 0 1 0 3 0 0}
                  /*  if (receive[4] != 'S') {
                        reSendBt = receive;
                        System.out.println("reSendBt : " + Arrays.toString(reSendBt));
                    }*/

                    connectState = true;
                    // String message = "";
                    for (byte t : receive) {
                        // message = message + " " + t;
                        System.out.print(t + " ");
                    }
                    // LogManager.getLogger().debug(message);
                    System.out.println();
                    if (receive[4] == 'R' && receive[5] == 'B') {
                        LogManager.getLogger().debug(" RB ");
                       /* long flash_scanner = (receive[6] & 0xFF) * 256 * 256 * 256 + (receive[7] & 0xFF) * 256 * 256
                                + (receive[8] & 0xFF) * 256 + (receive[9] & 0xFF);
                        LogManager.getLogger().debug("flash_scanner" + flash_scanner);
                        Flashscanner fs = FlashscannerManager.getPositionFromFlashBarcode(flash_scanner);
                        if (fs != null) {
                            LogManager.getLogger().debug("ReBirth to " + fs.getLevel() + "/" + fs.getPos());
                            m_flash_scanner.forceChangePosition(fs.getLevel(), fs.getPos());
                        } else {
                            // chenc 20190718 start
                            m_flash_scanner.forceChangePosition(Position.NOTFOUND_LEVEL, Position.NOTFOUND_POS);
                            LogManager.getLogger()
                                    .debug("ReBirth to " + Position.NOTFOUND_LEVEL + "/" + Position.NOTFOUND_POS);
                            // chenc 20190718 end
                            LogManager.getLogger().warn("UnKnown barcode position " + flash_scanner);
                        }*/

                    } else if ((receive[4] == 'S') && ((receive[5] == 'D') || (receive[5] == 'T'))) {
                        if (receive[5] == 'D') {
                            // LogManager.getLogger().debug("st receive");
                            try {
                                byte[] info = V4ShuttleConnector2.this.m_client.getMessage(sensor_info + error_length);
                                byte[] old_receive = Arrays.copyOf(receive, receive_length);
                                receive = new byte[receive_length + sensor_info + error_length];
                                if (info == null) {
                                    try {
                                        LogManager.getLogger().debug("Clear all buffer in case");
                                        V4ShuttleConnector2.this.m_client.getMessage();
                                        continue;
                                    } catch (IOException e1) {
                                        // TODO Auto-generated catch block
                                        LogManager.getLogger().error(e1.toString());
                                    }
                                }
                                System.arraycopy(old_receive, 0, receive, 0, old_receive.length);
                                System.arraycopy(info, 0, receive, old_receive.length, info.length);
                                for (byte t : receive) {
                                    // message = message + " " + t;
                                    System.out.print(t + " ");
                                }
                                System.out.println();
                                // if ("Y".equals(ConfigureManager.getValue("Cartest"))) {
                                if (true) {
                                    String testMessage = "";
                                    if (receive != null) {
                                        for (byte t : receive) {
                                            // message = message + " " + t;
                                            //System.out.print(t + " ");
                                            int m = t & 0xFF;
                                            testMessage = testMessage + " " + Integer.toHexString(m);
                                        }
                                        LogManager.getLogger().debug("receive" + testMessage);
                                    }
                                }
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                LogManager.getLogger().error(e.toString());
                                try {
                                    LogManager.getLogger().debug("Clear all buffer in case");
                                    V4ShuttleConnector2.this.m_client.getMessage();
                                    continue;
                                } catch (IOException e1) {
                                    // TODO Auto-generated catch block
                                    LogManager.getLogger().error(e1.toString());
                                }
                            }
                            // receive = receive +
                            // Shuttle2Connector.this.m_client.getMessage(receive_length+sensor_info+error_length);
                        }
                        if (receive[7] == 1 || receive[7] == 2 || receive[7] == 3) {
                            if (reSend != false) {
                                reSend = false;
                                LogManager.getLogger().debug("reSend false ");
                            }
                        }
                        // LogManager.getLogger().debug("car state: " + i_state.getState());
                        V4ShuttleConnector2.this.error_info = receive[8];
                        if (i_state.getState() == ShuttleInsideState.PROCESS_STATE) {
                            if ((V4ShuttleConnector2.this.error_info & 0x08) != 0) {
                                if (busy == false) {
                                    busy = true;
                                    LogManager.getLogger().debug("Set busy");
                                    if (reSend != false) {
                                        reSend = false;
                                        LogManager.getLogger().debug("change reSend state false by busy");
                                    }
                                }
                            } else {
                                //TODO 重发>>>>>>>
                                reSend = true;
                                LogManager.getLogger().debug("change resend state true ");
                                if (busy == true) {
                                    busy = false;

                                    i_state.setState(ShuttleInsideState.FINISH_STATE);
                                    process_start = 0L;
                                    LogManager.getLogger()
                                            .debug("Set busy free as received flash free signal, change into finish");
                                }
                            }
                        }

                        if ((V4ShuttleConnector2.this.error_info & 0x20) != 0) {
                            V4ShuttleConnector2.this.positioning = true;
                        } else {
                            V4ShuttleConnector2.this.positioning = false;
                        }

                        if ((i_state.getState() == ShuttleInsideState.PROCESS_STATE)
                                && ((V4ShuttleConnector2.this.error_info & 0x01) != 0)) {
                            if (receive[5] == 'D') {
                                i_state.setState(ShuttleInsideState.ERROR_STATE);
                                process_start = 0L;
                                i_state.setIs_error(true);
                                busy = false;
                                reSend = false;
                                LogManager.getLogger().debug("Change reSend state false by error");

                                if (task_type == TYPE_MOVE) {
                                    i_state.setError_type(ShuttleInsideState.ERROR_NEED_LOCATION);
                                } else if (task_type == TYPE_LOAD) {
                                    i_state.setError_type(ShuttleInsideState.ERROR_POSSIABLE_EMPTY);
                                } else if (task_type == TYPE_UNLOAD) {
                                    i_state.setError_type(ShuttleInsideState.ERROR_POSSIABLE_FULL);
                                } else {
                                    i_state.setError_type(ShuttleInsideState.ERROR_OTHERS);
                                }

                                LogManager.getLogger().debug("Change state into error");
                            } else {
                                byte[] data = new byte[V4ShuttleConnector2.this.send_length];
                                Arrays.fill(data, (byte) 0);
                                data[0] = (byte) 0;
                                data[1] = (byte) ERROR_SD_ID;
                                data[2] = ((byte) (V4ShuttleConnector2.this.flash_id / 256));
                                data[3] = ((byte) (V4ShuttleConnector2.this.flash_id % 256));
                                // data[4] = 'S';
                                data[5] = 5;// Detail data request
                                while (true) {
                                    try {
                                        // if ("Y".equals(ConfigureManager.getValue("Cartest"))) {
                                        if (true) {
                                            String testMessage = "";
                                            if (receive != null) {
                                                for (byte t : data) {
                                                    // message = message + " " + t;
                                                    //System.out.print(t + " ");
                                                    int m = t & 0xFF;
                                                    testMessage = testMessage + " " + Integer.toHexString(m);
                                                }
                                                LogManager.getLogger().debug("send:" + testMessage);
                                            }
                                        }
                                        V4ShuttleConnector2.this.m_client.sendContent(data);
                                        break;
                                    } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                        LogManager.getLogger().debug(e.toString());
                                        ErrorManager.addErrorMessage(ErrorManager.NET_FLASH,
                                                V4ShuttleConnector2.this.flash_id, e.getMessage());
                                        try {
                                            Thread.sleep(5000L);
                                        } catch (InterruptedException e1) {
                                            LogManager.getLogger().debug(e1.toString());
                                        }
                                    }
                                }
                                LogManager.getLogger().debug("ST shows error, ask SD for details");
                            }
                        } else if ((V4ShuttleConnector2.this.error_info & 0x01) == 0) {
                            i_state.setIs_error(false);
                        } else {
                            i_state.setIs_error(true);
                        }

                        if ((V4ShuttleConnector2.this.error_info & 0x02) != 0) {
                            i_state.setIs_lowPower(true);
                        } else {
                            i_state.setIs_lowPower(false);
                        }

                        if ((receive[7] == 2)) {
                            i_state.setWorkmode(ShuttleInsideState.AUTOMODE);
                        } else if ((receive[7] == 1)) {
                            i_state.setWorkmode(ShuttleInsideState.MANUALMODE);
                        }

                        if (receive[5] == 'D') {
                            if (receive.length >= sensor_info + error_length + receive_length) {
                                i_state.setErrormap(receive, sensor_info + receive_length);
                                remote_task_id = receive[18] * 256 + receive[19];
                            }
                        }

                    } else {
                        int t_id = receive[0] * 256 + receive[1];
                        if ((t_id == V4ShuttleConnector2.this.task_id)
                                && (i_state.getState() == ShuttleInsideState.PROCESS_STATE)) {
                            if (receive[7] == 3) {// Finish
                                i_state.setState(ShuttleInsideState.FINISH_STATE);
                                process_start = 0L;
                                LogManager.getLogger().debug("Set Busy Free as receive finish data");
                                busy = false;
                                LogManager.getLogger().debug("Change state into finish");
                                if (reSend != false) {
                                    reSend = false;
                                    LogManager.getLogger().debug("Change reSend state false by finish ");
                                }

                            } else if (receive[7] == 2) {// Error
                                i_state.setState(ShuttleInsideState.ERROR_STATE);
                                process_start = 0L;
                                if (task_type == TYPE_MOVE) {
                                    i_state.setError_type(ShuttleInsideState.ERROR_NEED_LOCATION);
                                } else if (task_type == TYPE_LOAD) {
                                    i_state.setError_type(ShuttleInsideState.ERROR_POSSIABLE_EMPTY);
                                } else if (task_type == TYPE_UNLOAD) {
                                    i_state.setError_type(ShuttleInsideState.ERROR_POSSIABLE_FULL);
                                } else {
                                    i_state.setError_type(ShuttleInsideState.ERROR_OTHERS);
                                }
                                busy = false;
                                LogManager.getLogger().debug("Change  state into error");
                                reSend = false;
                                LogManager.getLogger().debug("Change reSend state false by error");


                                // send SD request
                                byte[] data = new byte[V4ShuttleConnector2.this.send_length];
                                Arrays.fill(data, (byte) 0);
                                data[0] = (byte) 0;
                                data[1] = ERROR_SD_ID;
                                data[2] = ((byte) (V4ShuttleConnector2.this.flash_id / 256));
                                data[3] = ((byte) (V4ShuttleConnector2.this.flash_id % 256));
                                // data[4] = 'S';
                                data[5] = 5;// Detail data request
                                while (true) {
                                    try {
                                        // if ("Y".equals(ConfigureManager.getValue("Cartest"))) {
                                        if (true) {
                                            String testMessage = "";
                                            if (receive != null) {
                                                for (byte t : data) {
                                                    // message = message + " " + t;
                                                    //System.out.print(t + " ");
                                                    int m = t & 0xFF;
                                                    testMessage = testMessage + " " + Integer.toHexString(m);
                                                }
                                                LogManager.getLogger().debug("send:" + testMessage);
                                            }
                                        }
                                        V4ShuttleConnector2.this.m_client.sendContent(data);
                                        break;
                                    } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                        LogManager.getLogger().debug(e.toString());
                                        ErrorManager.addErrorMessage(ErrorManager.NET_FLASH,
                                                V4ShuttleConnector2.this.flash_id, e.toString());
                                        try {
                                            Thread.sleep(2000L);
                                        } catch (InterruptedException e1) {
                                            LogManager.getLogger().debug(e1.toString());
                                        }
                                    }
                                }
//								LogManager.getLogger().debug(
//										"Task feedback shows error, ask SD for details");
                            }
                        } else {
                            if (i_state.getState() == ShuttleInsideState.PROCESS_STATE) {
//								LogManager.getLogger()
//										.debug("task_id is " + V4ShuttleConnector2.this.task_id + " and t_id is " + t_id);
//								if (t_id == 0 && i_state.getState() == ShuttleInsideState.PROCESS_STATE) {
//									String s = "";
//									for (int i = 0; i < receive.length; i++) {
//										s = s + receive[i];
//									}
//									LogManager.getLogger().debug(s);
//									LogManager.getLogger()
//											.debug("As received Unknown package, set this task as finished in case");
//									i_state.setState(ShuttleInsideState.FINISH_STATE);
//									LogManager.getLogger().debug("Change state into finish");
//								}
                            } else {
                                // Not in process state, better clear all buffer
                                try {
                                    LogManager.getLogger().debug("Clear all buffer in case");
                                    V4ShuttleConnector2.this.m_client.getMessage();
                                    continue;
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    LogManager.getLogger().error(e.toString());
                                }
                            }
                        }
                    }
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    LogManager.getLogger().debug(e.toString());
                }
            } while (!this.is_break.booleanValue());
        }
    }
}
