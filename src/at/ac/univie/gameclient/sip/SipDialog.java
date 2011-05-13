package at.ac.univie.gameclient.sip;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class SipDialog {
	Socket clientSocket;
	
	public SipDialog(String host, int port) throws UnknownHostException, IOException {
		clientSocket = new Socket(host, port);
	}

	public void send(SipRequest request)
	{
		
	}
	
	public void send(SipResponse response)
	{
		
	}
}
