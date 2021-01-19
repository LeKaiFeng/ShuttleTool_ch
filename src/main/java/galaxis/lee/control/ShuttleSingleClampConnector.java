package galaxis.lee.control;

import galaxis.lee.control.device.FlashReport;
import galaxis.lee.control.device.ShuttleInsideState;
import galaxis.lee.control.device.FlashInterface;
import galaxis.lee.error.ErrorManager;
import galaxis.lee.graph.PositionManager;
import galaxis.lee.log.LogManager;
import galaxis.lee.tcp.TcpClient;
import galaxis.lee.util.ConsolePrint;

import java.io.IOException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
/**
 * @Author: Lee
 * @Date: Created in 10:43 2020/8/5
 * @Description: TODO 单深夹抱车
 */
public class ShuttleSingleClampConnector implements FlashInterface {
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

	public boolean connectState = false;

	public boolean isConnectState() {
		return connectState;
	}

	public ShuttleSingleClampConnector(String ip, int port, int id) {
		this.m_client = new TcpClient(ip, port);
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
//		this.resend_buffer = new byte[this.send_length];
//		Arrays.fill(resend_buffer, (byte)0);
		int t = 90000;
//		try {
//			t = Integer.valueOf(ConfigureManager.getValue("flash_nettimeout"));
//			if (t < 60000)
//				t = 60000;
//			if (t > 120000)
//				t = 120000;
//		} catch (Exception e) {
//			GISLogManager.getLogger().debug(e.toString());
//			t = 90000;
//		}
		nosending_timeout = t - 30000;
//		this.resend_delay = 10000;
		process_timeout = t;
	}

	@Override
	public Boolean loadUpDown(int para, int width, int pos) {
		//无双深 取放货
		if (para < 0 || para > 6)
			return false;

		if (!isRFW())
			return false;
		
		if (task_id >= 127)
			task_id = 1;
		else
			task_id = task_id + 1;
		LogManager.getLogger().debug("Task id is " + task_id);
		byte[] data = new byte[ShuttleSingleClampConnector.this.send_length];
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
		}
		// else if(para==CLAMP_PICK_UP_LEFT_2){
		// 	task_type = TYPE_LOAD;
		// 	data[5] = 3;
		// 	data[33] = 7;
		// }else if(para==CLAMP_PICK_UP_RIGHT_2){
		// 	task_type = TYPE_LOAD;
		// 	data[5] = 3;
		// 	data[33] = 8;
		// }else if(para==CLAMP_PUT_DOWN_LEFT_2){
		// 	task_type = TYPE_UNLOAD;
		// 	data[5] = 4;
		// 	data[33] = 9;
		// }else if(para==CLAMP_PUT_DOWN_RIGHT_2){
		// 	task_type = TYPE_UNLOAD;
		// 	data[5] = 4;
		// 	data[33] = 10;
		// }
		else{
			LogManager.getLogger().debug("取放货指令异常");
			return false;
		}
		// 向data[30],data[31]添加货物宽度, 为了不改动接口, 使用width(原接口的level)接收宽度
		data[30] = (byte) ((width >> 8) & 0xFF);
		data[31] = (byte) (width & 0xFF);
		System.out.println("宽度为" + width);
		
		while (true) {
			try {
				busy = false;
				LogManager.getLogger().debug("Set Busy Free as start");
				// this.m_client.write(data);
				this.m_client.sendContent(data);
//				for(int n=0;n<this.send_length;n++){
//					this.resend_buffer[n] = data[n];
//				}
//				this.resend = false;
//				start_time = System.currentTimeMillis();
				break;
			} catch (Exception e) {
				LogManager.getLogger().debug(e.toString());
				ErrorManager.addErrorMessage(ErrorManager.NET_FLASH,
						this.flash_id, e.getMessage());
				try {
					Thread.sleep(2000L);
				} catch (InterruptedException e1) {
					LogManager.getLogger().debug(e1.toString());
				}
			}
		}
		process_start = System.currentTimeMillis();
		i_state.setState(ShuttleInsideState.PROCESS_STATE);
		
		int level = 0;
		i_state.setActionForErrorRecord(level, pos, 0, 0, para);
		return true;
	}

	@Override
	public Boolean moveTo(int dir, long dis, int level, int pos, int weight) {

		if (dir < 0 || dir > 4)
			return false;

		if (!isRFW())
			return false;
		
		if (task_id >= 127)
			task_id = 1;
		else
			task_id = task_id + 1;
		// LogManager.getLogger().debug("Task id is " + task_id);
		byte[] data = new byte[ShuttleSingleClampConnector.this.send_length];
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
		while (true) {
			try {
				busy = false;
				LogManager.getLogger().debug("Set Busy Free as start");
				this.m_client.sendContent(data);
//				for(int n=0;n<this.send_length;n++){
//					this.resend_buffer[n] = data[n];
//				}
//				this.resend = false;
//				start_time = System.currentTimeMillis();
				break;
			} catch (Exception e) {
				LogManager.getLogger().debug(e.toString());
				ErrorManager.addErrorMessage(ErrorManager.NET_FLASH,
						this.flash_id, e.getMessage());
				try {
					Thread.sleep(2000L);
				} catch (InterruptedException e1) {
					LogManager.getLogger().debug(e1.toString());
				}
			}
		}
		process_start = System.currentTimeMillis();
		i_state.setState(ShuttleInsideState.PROCESS_STATE);
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
		// TODO Auto-generated method stub
		if(auto == true){
			this.sysTaskId();
		}
	}

	@Override
	public void sysTaskId() {
		// TODO Auto-generated method stub
		this.task_id = this.remote_task_id + 1;
		LogManager.getLogger().debug("Remote Task id is " + this.remote_task_id);
	}

	@Override
	public Boolean isRFW() {
		// TODO Auto-generated method stub
		if (i_state.getWorkmode() == ShuttleInsideState.AUTOMODE
				&& !i_state.getIs_error()
				&& i_state.getErrorMessage().equals("")) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void clearSendingBuffer() {
		// TODO Auto-generated method stub

	}

	class STSend extends TimerTask {

		public void run() {
//			if(i_state.getState() == ShuttleInsideState.PROCESS_STATE){
//				if(busy = false){
//				}
//			}
			
			if (process_start != 0L
					&& System.currentTimeMillis() - process_start < nosending_timeout) {
				return;
			}

			// if(Shuttle2Connector.this.ST_id >13){
			// return;
			// }
			if (ShuttleSingleClampConnector.this.ST_id >= 2000) {
				ShuttleSingleClampConnector.this.ST_id = 1500;
			} else {
				ShuttleSingleClampConnector.this.ST_id += 1;
			}
			byte[] data = new byte[ShuttleSingleClampConnector.this.send_length];
			Arrays.fill(data, (byte) 0);
			data[0] = ((byte) (ShuttleSingleClampConnector.this.ST_id / 256));
			data[1] = ((byte) (ShuttleSingleClampConnector.this.ST_id % 256));
			data[2] = ((byte) (ShuttleSingleClampConnector.this.flash_id / 256));
			data[3] = ((byte) (ShuttleSingleClampConnector.this.flash_id % 256));
			// data[4] = 'S';
			data[5] = 5;// Detail data request
			while (true) {
				try {
					ShuttleSingleClampConnector.this.m_client.sendContent(data);
					break;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					LogManager.getLogger().debug(e.toString());
					ErrorManager.addErrorMessage(ErrorManager.NET_FLASH,
							ShuttleSingleClampConnector.this.flash_id, e.getMessage());
					try {
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
					//receive = Shuttle2Connector.this.m_client.getMessage();
					receive = ShuttleSingleClampConnector.this.m_client.getMessage(receive_length);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					LogManager.getLogger().error(e.toString());
					ErrorManager.addErrorMessage(ErrorManager.NET_FLASH,
							ShuttleSingleClampConnector.this.flash_id, e.getMessage());
					try {
						Thread.sleep(5000L);
					} catch (InterruptedException e1) {
						LogManager.getLogger().error(e1.toString());
					}
				}
				if (receive != null) {
					connectState = true;
					ConsolePrint.print1(receive);
					if ((receive[4] == 'S')
							&& ((receive[5] == 'D') || (receive[5] == 'T'))) {
						if(receive[5] == 'D'){
							try {
								byte[] info = ShuttleSingleClampConnector.this.m_client.getMessage(sensor_info+error_length);
								byte[] old_receive = Arrays.copyOf(receive, receive_length);
								receive = new byte[receive_length+sensor_info+error_length];
								if(info == null){
									try {
										LogManager.getLogger().debug("Clear all buffer in case");
										ShuttleSingleClampConnector.this.m_client.getMessage();
										continue;
									} catch (IOException e1) {
										// TODO Auto-generated catch block
										LogManager.getLogger().error(e1.toString());
									}
								}
								System.arraycopy(old_receive, 0, receive, 0, old_receive.length);
								System.arraycopy(info, 0, receive, old_receive.length, info.length);
								ConsolePrint.print1(receive);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								LogManager.getLogger().error(e.toString());
								try {
									LogManager.getLogger().debug("Clear all buffer in case");
									ShuttleSingleClampConnector.this.m_client.getMessage();
									continue;
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									LogManager.getLogger().error(e1.toString());
								}
							}
							//receive = receive + Shuttle2Connector.this.m_client.getMessage(receive_length+sensor_info+error_length);
						}
						
						
						ShuttleSingleClampConnector.this.error_info = receive[8];
						if (i_state.getState() == ShuttleInsideState.PROCESS_STATE){
							if((ShuttleSingleClampConnector.this.error_info & 0x08) != 0){
								if(busy == false){
									busy = true;
									LogManager.getLogger().debug("Set busy");
								}
							}else{
								if(busy == true){
									busy = false;
									i_state.setState(ShuttleInsideState.FINISH_STATE);
									process_start = 0L;
									LogManager.getLogger().debug("Set busy free as received flash free signal, change into finish");
								}
							}
						}
						if ((i_state.getState() == ShuttleInsideState.PROCESS_STATE)
								&& ((ShuttleSingleClampConnector.this.error_info & 0x01) != 0)) {
							if (receive[5] == 'D') {
								i_state.setState(ShuttleInsideState.ERROR_STATE);
								process_start = 0L;
								i_state.setIs_error(true);
								busy = false;
								if (task_type == TYPE_MOVE) {
									i_state.setError_type(ShuttleInsideState.ERROR_NEED_LOCATION);
								} else if (task_type == TYPE_LOAD) {
									i_state.setError_type(ShuttleInsideState.ERROR_POSSIABLE_EMPTY);
								} else if (task_type == TYPE_UNLOAD) {
									i_state.setError_type(ShuttleInsideState.ERROR_POSSIABLE_FULL);
								} else {
									i_state.setError_type(ShuttleInsideState.ERROR_OTHERS);
								}
								LogManager.getLogger().debug(
										"Change state into error");
							} else {
								byte[] data = new byte[ShuttleSingleClampConnector.this.send_length];
								Arrays.fill(data, (byte) 0);
								data[0] = (byte) 0;
								data[1] = (byte) ERROR_SD_ID;
								data[2] = ((byte) (ShuttleSingleClampConnector.this.flash_id / 256));
								data[3] = ((byte) (ShuttleSingleClampConnector.this.flash_id % 256));
								// data[4] = 'S';
								data[5] = 5;// Detail data request
								while (true) {
									try {
										ShuttleSingleClampConnector.this.m_client
												.sendContent(data);
										break;
									} catch (Exception e) {
										// TODO Auto-generated catch block
										LogManager.getLogger().debug(
												e.toString());
										ErrorManager
												.addErrorMessage(
														ErrorManager.NET_FLASH,
														ShuttleSingleClampConnector.this.flash_id,
														e.getMessage());
										try {
											Thread.sleep(5000L);
										} catch (InterruptedException e1) {
											LogManager.getLogger().debug(
													e1.toString());
										}
									}
								}
								LogManager.getLogger().debug(
										"ST shows error, ask SD for details");
							}
						} else if ((ShuttleSingleClampConnector.this.error_info & 0x01) == 0) {
							i_state.setIs_error(false);
						} else {
							i_state.setIs_error(true);
						}

						if ((ShuttleSingleClampConnector.this.error_info & 0x02) != 0) {
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
							if (receive.length >= sensor_info + error_length
									+ receive_length) {
								i_state.setErrormap(receive, sensor_info
										+ receive_length);
								remote_task_id = receive[18] * 256
										+ receive[19];
							}
						}

					} else {
						int t_id = receive[0] * 256 + receive[1];
						if ((t_id == ShuttleSingleClampConnector.this.task_id)
								&& (i_state.getState() == ShuttleInsideState.PROCESS_STATE)) {
							if (receive[7] == 3) {// Finish
								i_state.setState(ShuttleInsideState.FINISH_STATE);
								process_start = 0L;
								LogManager.getLogger().debug("Set Busy Free as receive finish data");
								busy = false;
								LogManager.getLogger().debug(
										"Change state into finish");
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
								LogManager.getLogger().debug(
										"Change state into error");

								// send SD request
								byte[] data = new byte[ShuttleSingleClampConnector.this.send_length];
								Arrays.fill(data, (byte) 0);
								data[0] = (byte) 0;
								data[1] = ERROR_SD_ID;
								data[2] = ((byte) (ShuttleSingleClampConnector.this.flash_id / 256));
								data[3] = ((byte) (ShuttleSingleClampConnector.this.flash_id % 256));
								// data[4] = 'S';
								data[5] = 5;// Detail data request
								while (true) {
									try {
										ShuttleSingleClampConnector.this.m_client
												.sendContent(data);
										break;
									} catch (IOException e) {
										// TODO Auto-generated catch block
										LogManager.getLogger().debug(
												e.toString());
										ErrorManager
												.addErrorMessage(
														ErrorManager.NET_FLASH,
														ShuttleSingleClampConnector.this.flash_id,
														e.toString());
										try {
											Thread.sleep(2000L);
										} catch (InterruptedException e1) {
											LogManager.getLogger().debug(
													e1.toString());
										}
									}
								}
//								LogManager.getLogger().debug(
//										"Task feedback shows error, ask SD for details");
							}
						} else {
							if(i_state.getState() == ShuttleInsideState.PROCESS_STATE){
							LogManager.getLogger().debug(
									"task_id is "
											+ ShuttleSingleClampConnector.this.task_id
											+ " and t_id is " + t_id);
							if (t_id == 0
									&& i_state.getState() == ShuttleInsideState.PROCESS_STATE) {
								ConsolePrint.print1(receive);
								LogManager
										.getLogger()
										.debug("As received Unknown package, set this task as finished in case");
								i_state.setState(ShuttleInsideState.FINISH_STATE);
								LogManager.getLogger().debug(
										"Change state into finish");
							}
							}else{
								// Not in process state, better clear all buffer
								try {
									LogManager.getLogger().debug(
											"Clear all buffer in case");
									ShuttleSingleClampConnector.this.m_client.getMessage();
									continue;
								} catch (IOException e) {
									// TODO Auto-generated catch block
									LogManager.getLogger().error(
											e.toString());
								}
							}
						}
					}
				}else{
					connectState = false;
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
