package at.ac.univie.gameclient.sip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

public class SipDialog {
	private SipCallId callId;
	private long commandSequence;
	
	Socket socket;
	PrintWriter out;
	BufferedReader in;
	
	public Thread testThread;
	
	public SipDialog(String host, int port) throws UnknownHostException, IOException {
		Log.v("TCP", "start");
		
		socket = new Socket(host, port);
		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		callId = new SipCallId(SipUtility.getLocalIpAddress(), SipUtility.createId());
		
		/*
		testThread = new Thread() {
			public void run() {
				while(!isInterrupted())
				{
					Log.v("TCP", "send");
					out.println("Das ist ein Test.");
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						interrupt();
					}
				}
			}
		};
		
		testThread.start();
		*/
	}

	void send(SipRequest request)
	{
		out.println(request);
	}
	
	void send(SipResponse response)
	{
		out.println(response);
	}

	public SipCallId getCallId() {
		return callId;
	}

	public long getCommandSequence() {
		return commandSequence;
	}
	
	public void close() throws IOException
	{
		socket.close();
	}
}
