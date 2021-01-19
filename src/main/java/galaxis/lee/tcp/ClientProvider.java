package galaxis.lee.tcp;

import java.io.IOException;

public interface ClientProvider {
	
	void sendContent(byte[] content) throws IOException;
	
	byte[] getMessage() throws IOException;
	
	byte[] getMessage(int t) throws IOException;
	
	void close() throws IOException;
	
}

