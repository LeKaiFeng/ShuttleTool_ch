package galaxis.lee.tcp;


import galaxis.lee.error.ErrorManager;
import galaxis.lee.log.LogManager;

import java.io.IOException;
import java.util.Arrays;

public class DeviceTcpConnector implements Runnable {
	protected byte[] data_send;
	protected byte[] data_receive;
	protected TcpClient m_client;
	protected int l_send;
	protected int l_receive;
	protected Object m_object;
	protected Object n_object;
	protected Long lasttimestamp;
	protected static final Long RESEND_DELAY = 3000L;

	protected String error_type;
	protected int device_id;

	public DeviceTcpConnector(int send_length, int receive_length,
			String server_ip, int port, String error_type, int device_id) {
		this.m_client = new TcpClient(server_ip, port);
		this.data_send = new byte[send_length];
		this.data_receive = new byte[receive_length];
		this.l_send = send_length;
		this.l_receive = receive_length;
		Arrays.fill(this.data_send, (byte) 0);
		Arrays.fill(this.data_receive, (byte) 0);
		this.m_object = new Object();
		this.n_object = new Object();
		this.lasttimestamp = Long.valueOf(System.currentTimeMillis());
		this.error_type = error_type;
		this.device_id = device_id;
	}

	public Boolean setAllData(byte[] s) {
		if (s.length != this.l_send) {
			return Boolean.valueOf(false);
		}
		for (int i = 0; i < this.l_send; i++) {
			this.data_send[i] = s[i];
		}
		return Boolean.valueOf(true);
	}

	public Boolean setByte(byte s, int start) {
		if (start >= this.l_send) {
			return Boolean.valueOf(false);
		}
		this.data_send[start] = s;
		return Boolean.valueOf(true);
	}

	public Boolean setBytes(byte[] s, int start) {
		int l;
		if (start + s.length > this.l_send) {
			l = this.l_send - start;
		} else {
			l = s.length;
		}
		for (int i = 0; i < l; i++) {
			this.data_send[(i + start)] = s[i];
		}
		return Boolean.valueOf(true);
	}

	// public byte[] getReceiveData(int start, int length) {
	// synchronized (this.m_object) {
	// if (start + length > this.l_receive) {
	// return null;
	// }
	// return Arrays.copyOfRange(this.data_receive, start, start + length);
	// }
	// }

	public byte getReceiveByte(int start) {
		synchronized (this.m_object) {
			if (start > this.l_receive) {
				return data_receive[l_receive];
			}
			return data_receive[start];
		}
	}

	public void clearReceiveData() {
		Arrays.fill(this.data_receive, (byte) 0);
	}

//	public void sendBytes(byte[] send) {
//		synchronized (this.n_object) {
//			while (true) {
//				try {
//					this.lasttimestamp = Long.valueOf(System.currentTimeMillis());
//					this.m_client.sendContent(send);
//					break;
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					GISLogManager.getLogger().debug(e.toString());
//					ErrorManager.addErrorMessage(error_type, device_id,
//							e.getMessage());
//					try {
//						Thread.sleep(2000L);
//					} catch (InterruptedException e1) {
//						GISLogManager.getLogger().debug(e1.toString());
//					}
//				}
//			}
//		}
//	}

	public void run() {
		while (true) {
			byte[] message = null;
			try {
				message = this.m_client.getMessage();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LogManager.getLogger().debug(e.toString());
				ErrorManager.addErrorMessage(error_type, device_id,
						e.getMessage());
				try {
					Thread.sleep(2000L);
				} catch (InterruptedException e1) {
					LogManager.getLogger().debug(e1.toString());
				}
			}
			if ((message != null) && (message.length != 0)) {
				synchronized (this.m_object) {
					int l = message.length;
					if (l > this.l_receive) {
						l = this.l_receive;
					}
					for (int i = 0; i < l; i++) {
						this.data_receive[i] = message[i];
					}
				}
			}
			Long now = Long.valueOf(System.currentTimeMillis());
			if (now.longValue() - this.lasttimestamp.longValue() > RESEND_DELAY) {
				send();
			}
			try {
				Thread.sleep(200L);
			} catch (InterruptedException e) {
				LogManager.getLogger().debug(e.toString());
			}
		}
	}

	public void send() {
		synchronized (this.n_object) {
			if (this.data_send[0] == 0) {
				this.data_send[0] = 1;
			} else {
				this.data_send[0] = 0;
			}
			while (true) {
				try {
					this.lasttimestamp = Long.valueOf(System.currentTimeMillis());
					this.m_client.sendContent(this.data_send);
					break;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					LogManager.getLogger().debug(e.toString());
					ErrorManager.addErrorMessage(error_type, device_id,
							e.getMessage());
					try {
						Thread.sleep(2000L);
					} catch (InterruptedException e1) {
						LogManager.getLogger().debug(e1.toString());
					}
				}
			}
		}
	}
}
