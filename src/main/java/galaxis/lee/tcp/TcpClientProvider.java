package galaxis.lee.tcp;


import galaxis.lee.log.LogManager;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

//import cn.aigret.Main;

public class TcpClientProvider implements ClientProvider {

	protected Socket socket;

	protected PrintWriter writer = null;

	protected BufferedReader reader = null;

	protected DataInputStream isr = null;
	
	protected DataOutputStream osr = null;

	protected InetSocketAddress address;

	protected byte[] cbuf;

	protected Object m_object;

	public TcpClientProvider(final InetSocketAddress address)
			throws IOException {
		this(address, true);
	}

	public TcpClientProvider(final InetSocketAddress address,
			final boolean initialConnect) throws IOException {

		this.address = address;
		if (initialConnect) {
			connect();
		}
	}

	public synchronized void sendContent(byte[] content) throws IOException {
		//synchronized (m_object) {
			if (socket == null || socket.isClosed() || !socket.isConnected()) {
				try {
					connect();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					LogManager.getLogger().debug(e.toString());
				}
			}
			try {
				osr.write(content);
				osr.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//GISLogManager.getLogger().debug(e.toString());
				try {
					socket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					LogManager.getLogger().debug(e1.toString());
				}
				throw e;
			}
			//writer.println(content);
			//writer.flush();
//			String result;
//			try {
//				result = reader.readLine();
//				LogManager.getLogger().debug(result);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				LogManager.getLogger().debug(e.toString());
//			}
//		//}

	}

	@Override
	public byte[] getMessage() {
		//synchronized (m_object) {
			// TODO Auto-generated method stub
			// String result = "";
			// String s = null;
			Arrays.fill(cbuf, (byte) 0);
			int l = 0;
			try {
				l = isr.read(cbuf);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//LogManager.getLogger().debug(e.toString());
			}
			if (l > 0) {
				//LogManager.getLogger().debug(cbuf);
				return Arrays.copyOf(cbuf, l);
				//return cbuf;
			} else
				return null;
		//}
		// return result;
	}
	
	@Override
	public byte[] getMessage(int t) throws IOException {
		// TODO Auto-generated method stub
		//Arrays.fill(cbuf, (byte) 0);
		byte[] xbuf = new byte[t];
		Arrays.fill(xbuf, (byte) 0);
		int l = 0;
		try {
			//l = isr.read(cbuf);
			l = isr.read(xbuf,0,t);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//LogManager.getLogger().debug(e.toString());
		}
		if (l > 0) {
			//LogManager.getLogger().debug(cbuf);
			return xbuf;
		} else
			return null;
	}

	@Override
	public void close() throws IOException {
		if (socket != null) {
			socket.close();
		}
	}


	protected ThreadPoolExecutor pool;
	public void connect()  throws IOException {


		if (socket != null && socket.isConnected() && !socket.isClosed()) {
			return;
		}
		this.socket = new Socket();
		socket.setSoTimeout(10 * 1000);
		socket.connect(address);
		osr = new DataOutputStream(socket.getOutputStream());
		writer = new PrintWriter(new OutputStreamWriter(
				socket.getOutputStream()));
		isr = new DataInputStream(socket.getInputStream());
		reader = new BufferedReader(new InputStreamReader(
				socket.getInputStream(), "UTF-8"));
		cbuf = new byte[1000];
		m_object = new Object();
		// System.out.println("Connecting success");
		LogManager.getLogger().debug("Connecting success!");
	}

}
