package at.ac.univie.gameclient.gesture;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.util.Log;

public class GestureLogger {

	private String sServerIp;
	private int sServerPort;
	private boolean sInitialized;

	private DatagramSocket sSock;
	
	private final String TAG = "GestureLogger";
	
	
	public GestureLogger(String ip, int port) {
		sServerIp = ip;
		sServerPort = port;
		
		try {
			sSock = new DatagramSocket();
		} catch (SocketException e) {
			Log.e(TAG, "Error opening socket: " + e.getMessage());
		}
		sInitialized = true;
	}
	
	
	public void sendGestureFromSensor() {
		
		// TODO map sensor values to gesture and send it / them
		
	}
	

	
	private void sendSingleGesture(int type, String val) {
		if (!sInitialized) {
			Log.w(TAG, "Gesture Logger wasn't initialized yet! Not trying to send anything.");
			return;
		}
		
		// TODO compose message and send gesture		
		
	}
	
	private void sendMultipleGestures(int[] types, String[] vals) {
		if (!sInitialized) {
			Log.w(TAG, "Gesture Logger wasn't initialized yet! Not trying to send anything.");
			return;
		}
		
		// TODO compose message and send gesture
	}

	private void sendMessage(String message) {
		try {
			Log.v(TAG, "Sending message: " + message);
			InetAddress local = InetAddress.getByName(sServerIp);
			int length = message.length();
			byte[] messageRaw = message.getBytes();
			DatagramPacket p = new DatagramPacket(messageRaw, length, local, sServerPort);
			sSock.send(p);
		} catch (UnknownHostException e) {
			Log.e(TAG, "Host not known: " + e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, "Error while sending: " + e.getMessage());
		}
	}
	
}

/**
 * Trying to come up with some generic gestures to be transmitted
 */
abstract class GestureType {
	private static int TYPE_UP = 0;
	private static int TYPE_RIGHT = 1;
	private static int TYPE_DOWN = 2;
	private static int TYPE_LEFT = 3;
}
