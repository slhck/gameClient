package at.ac.univie.gameclient.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import android.util.Log;


public class DatagramReceiver extends Thread {
	DatagramSocket socket;
	DatagramHandler handler;
	
	public DatagramReceiver(DatagramSocket socket, DatagramHandler handler) throws SocketException {
		this.socket = socket;
		this.handler = handler;
	}
	
	public void run()
	{

		
		Log.v("cgClient", "started datagram receiver");
		
		while(!isInterrupted())
		{
			//Log.v("cgClient", "datagram receiver iteration");
			try {
				byte[] buffer = new byte[2048];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				Log.v("cgClient", "got datagram");
				
				byte[] data = new byte[packet.getLength()];
				System.arraycopy(buffer, 0, data, 0, packet.getLength());
				handler.handleDatagram(data);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
