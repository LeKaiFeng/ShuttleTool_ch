package galaxis.lee.tcp;


import galaxis.lee.log.LogManager;

import java.io.IOException;
import java.net.InetSocketAddress;


public class TcpClient{
	
	//private static int MAX_ERROR_CONUT = 20;
	//private static int RECONNECT_SLEEP_TIME = 10000;
	
	public TcpClient(String host, int port){
		this.host = host;
		this.port = port;
		try {
			this.setTcpClient(this.host, this.port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogManager.getLogger().debug(e.toString());
		}
	}
	
	
	protected boolean setTcpClient(String host, int port) throws IOException{
		this.m_SocketAddress = new InetSocketAddress(host, port);
		try {
			this.close();
			this.m_client = new TcpClientProvider(this.m_SocketAddress);
			//this.m_client.connect();
			this.is_start = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//ErrorManager.addErrorMessage(Error.ERROR_NETWORK, "Set TCP Connection failed");
			this.m_client = null;
			this.is_start = false;
			throw e;
		}
		return true;
	}
	
	public void close() throws IOException{
		if(this.is_start){
			this.m_client.close();
			this.is_start = false;
		}
	}
	
	public void reConnect() throws IOException{
		this.close();
		this.setTcpClient(this.host, this.port);
	}
	
	public void sendContent(byte[] content) throws IOException{
		if(!this.is_start){
			reConnect();
		}
		//continuely reconnect
		this.m_client.sendContent(content);
	}

	public byte[] getMessage() throws IOException {
		// TODO Auto-generated method stub
		if(m_client == null){
			this.reConnect();
			return null;
		}else{
			return m_client.getMessage();
		}
	}
	
	public byte[] getMessage(int t) throws IOException{
		if(m_client == null){
			this.reConnect();
			return null;
		}else{
			return m_client.getMessage(t);
		}
	}
	
	protected String host;
	protected int port;
	protected boolean is_start = false;
	protected InetSocketAddress m_SocketAddress = null;
	protected TcpClientProvider m_client = null;

}
