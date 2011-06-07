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

	private double lastPitch = 0;
	private static final int FLUCT_PITCH = 5;

	private DatagramSocket sSock;

	private final String TAG = "GestureLogger";

	/**
	 * Initializes a new GestureLogger
	 * 
	 * @param ip
	 * @param port
	 * @throws Exception
	 */
	public GestureLogger(String ip, int port) throws Exception {
		Log.d(TAG, "Creating new Gesture Logger for " + ip + ":" + port);

		if (sInitialized)
			throw new Exception(
					"Can't initialize two gesture loggers at the same time!");

		sServerIp = ip;
		sServerPort = port;

		try {
			sSock = new DatagramSocket();
		} catch (SocketException e) {
			Log.e(TAG, "Error opening socket: " + e.getMessage());
		}

		sInitialized = true;

	}

	/**
	 * Closes the logger
	 */
	public void close() {
		Log.d(TAG, "Closing Gesture Logger");
		sSock.close();
		sInitialized = false;
	}

	/**
	 * Converts the raw sensor values into a message and sends it, unless the
	 * minimum delay for sending a message isn't reached yet
	 * 
	 * @param yaw
	 * @param pitch
	 * @param roll
	 */
	public void sendGestureFromSensor(double yaw, double pitch, double roll) {
		Log.d(TAG, "sendGestureFromSensor Called");
		
		if (pitch > lastPitch + FLUCT_PITCH) {
			lastPitch = pitch;
			sendGesture(GestureType.TYPE_RIGHT, pitch);
		} else if (pitch < (lastPitch - FLUCT_PITCH * -1)) {
			lastPitch = pitch;
			sendGesture(GestureType.TYPE_LEFT, pitch);
		}
		
	}

	/**
	 * Converts a gesture into a proper UDP message
	 * 
	 * @param type
	 *            The gesture type
	 * @param val
	 *            The value of the gesture
	 */
	private void sendGesture(int type, double val) {
		Log.d(TAG, "sendGesture called");
		String message = "" + type + (int) val;
		Log.d(TAG, "sending message: " + message);
		
		// sendMessage(message);
	}

	/**
	 * Sends a message to the UDP server
	 * 
	 * @param message
	 */
	private void sendMessage(String message) {
		try {
			Log.v(TAG, "Sending message: " + message);
			InetAddress local = InetAddress.getByName(sServerIp);
			int length = message.length();
			byte[] messageRaw = message.getBytes();
			DatagramPacket p = new DatagramPacket(messageRaw, length, local,
					sServerPort);
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
	public static int TYPE_UP = 0;
	public static int TYPE_RIGHT = 1;
	public static int TYPE_DOWN = 2;
	public static int TYPE_LEFT = 3;
}
