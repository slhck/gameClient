package at.ac.univie.gameclient.gesture;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * A class that logs gestures
 * 
 * @author werner
 * 
 */
public class GestureLogger {

	// server related
	private String serverIp;
	private int serverPort;
	private DatagramSocket sock;

	// gesture related
	private double sensitivity;
	private double amplification;
	private double zeroPitch;
	private double zeroRoll;
	private double centerRoll;

	private double lastPitch;
	private double lastRoll;

	// default modifier for the fluctuation and amplification, leave it at 1,
	// then experiment and see if we need to change them
	private static final int FLUCT = 1;
	private static final int AMP_PITCH = 1;

	// default modifier for the fluctuation and amplification, leave it at 1,
	private static final int FLUCT_ROLL = 1;
	private static final int AMP_ROLL = 1;

	private static final int INDEX_YAW = 0;
	private static final int INDEX_PITCH = 1;
	private static final int INDEX_ROLL = 2;
	
	// debugging related
	private static final String TAG = "GestureLogger";
	public String lastMessage;

	/**
	 * Initializes a new GestureLogger
	 * 
	 * @param ip
	 *            The destination IP of the remote server
	 * @param port
	 *            The destination port
	 * @throws Exception
	 */
	public GestureLogger(String ip, int port) throws Exception {
		Log.d(TAG, "Creating new Gesture Logger for " + ip + ":" + port);

		// set server information
		serverIp = ip;
		serverPort = port;

		// default values
		sensitivity = 1;
		amplification = 1;
		zeroPitch = 3;
		zeroRoll = 10;
		centerRoll = 45;
		lastPitch = 0;

		try {
			sock = new DatagramSocket();
		} catch (SocketException e) {
			Log.e(TAG, "Error opening socket: " + e.getMessage());
		}

	}

	/**
	 * Sets the sensitivity of the GestureLogger The sensitivity is inverted, so
	 * for example: 1 would leave the the fluctuation at +/- 1 degrees 2 would
	 * become 0.5 and change the fluctuation to +/- 0.5 degrees 0.5 would become
	 * 2 and change the fluctuation to +/- 2 degrees 0.25 would become 4 and
	 * change the fluctuation to +/- 4 degrees
	 */
	public void setSensitivity(double sensitivity) {
		this.sensitivity = 1 / sensitivity;
	}

	/**
	 * Sets the amplification of the GestureLogger. It is the value multiplied
	 * with the raw sensor values in order to make movements faster
	 */
	public void setAmplification(double amplification) {
		this.amplification = amplification;
	}

	/**
	 * Sets the zeroPitch boundary, i.e. the boundary in degrees where no
	 * movement should be detected. A higher value results in less sensitivity
	 * and a broader interval. A lower value results in higher sensitivity.
	 * Default value is 3.
	 * 
	 * @param zero
	 */
	public void setZeroPitch(double zero) {
		this.zeroPitch = Math.abs(zero);
	}

	/**
	 * Sets the zeroRoll boundary, i.e. the boundary in degrees where no
	 * movement should be detected. A higher value results in less sensitivity
	 * and a broader interval. A lower value results in higher sensitivity.
	 * Default value is 10.
	 * 
	 * @param zero
	 */
	public void setZeroRoll(double zero) {
		this.zeroRoll = Math.abs(zero);
	}

	/**
	 * Defines the angle in which no up/down movement should be detected. Values
	 * can be anything between 0 (phone lying on table) and 180 (phone upside
	 * down). Default is 45.
	 * 
	 * @param zero
	 */
	public void setCenterRoll(double center) {
		this.centerRoll = Math.abs(center);
	}

	/**
	 * Closes the logger
	 */
	public void close() {
		Log.d(TAG, "Closing Gesture Logger");
		sock.close();
	}

	/**
	 * Converts the raw sensor values into a message and sends it, unless the
	 * minimum delay for sending a message isn't reached yet. For the correct
	 * mapping of Yaw/Pitch/Roll, imagine holding the phone in landscape and
	 * looking at a plane from the side, while it is flying from right to left:
	 * http://upload.wikimedia.org/wikipedia/commons/7/7e/Rollpitchyawplain.png
	 * 
	 * @param yaw
	 *            Currently not implemented
	 * @param pitch
	 *            The pitch (left/right) in degrees between 90 (left) -90
	 *            (right)
	 * @param roll
	 *            The roll (up/down) in degrees. Its real meaning depends on the
	 *            centerRoll setting
	 */
	public void sendGestureFromSensor(double yaw, double pitch, double roll) {

		
		try {
			// declare return members (an array of the size 3)
			int[] type = new int[3];
			double[] val = new double[3];
			double[] interval = new double[3];

			// -----------------------------------------------------------
			// Precalculations
			
			// the boundary is the amount in degrees that the raw values will
			// have to change before a real change is recognized
			double boundary = FLUCT * sensitivity;

			// the pitch is amplified according to the settings
			pitch = pitch * AMP_PITCH * amplification;
			roll = roll * AMP_ROLL * amplification;

			// Recalculate the roll value so that the new center is at 0. Then
			// the values behave just like the pitch.
			// for example: -45 would become 0, -90 would become -45, etc.
			roll += centerRoll;

			// -----------------------------------------------------------
			// Pitch calculations

			// if the pitch is within the zeroPitch range, just set it to 0
			if ((pitch > (-1 * zeroPitch)) && (pitch < (zeroPitch))) {
				pitch = 0;
			}
			// if not, correct it by subtracting/adding the zeroPitch value to
			// get 0 again
			else {
				if (pitch > 0)
					pitch -= zeroPitch;
				else
					pitch += zeroPitch;
			}

			// default type means: no movement was detected
			// this happens when the pitch only changes within the fluctuation
			// bounds
			type[INDEX_PITCH] = GestureType.TYPE_NO_MOVEMENT;

			// if the pitch is positive, the phone was moved to the left
			if (pitch > lastPitch + boundary) {
				type[INDEX_PITCH] = GestureType.TYPE_LEFT;

				// if the pitch is negative, the phone was moved to the right
			} else if (pitch < (lastPitch - boundary)) {
				type[INDEX_PITCH] = GestureType.TYPE_RIGHT;
			}

			// send the gesture and mark the pitch to compare it in the
			// next round
			val[INDEX_PITCH] = pitch;
			interval[INDEX_PITCH] = pitch - lastPitch;
			lastPitch = pitch;

			// -----------------------------------------------------------
			// Roll calculations
			// if the pitch is within the zeroPitch range, just set it to 0
			if ((roll > (-1 * zeroRoll)) && (roll < (zeroRoll))) {
				roll = 0;
			}
			// if not, correct it by subtracting/adding the zeroPitch value to
			// get 0 again
			else {
				if (roll > 0)
					roll -= zeroRoll;
				else
					roll += zeroRoll;
			}

			// default type means: no movement was detected
			// this happens when the roll only changes within the fluctuation
			// bounds
			type[INDEX_ROLL] = GestureType.TYPE_NO_MOVEMENT;

			// if the roll is positive, the phone was moved downwards
			if (roll > lastRoll + boundary) {
				type[INDEX_ROLL] = GestureType.TYPE_DOWN;

			// if the roll is negative, the phone was moved upwards
			} else if (roll < (lastRoll - boundary)) {
				type[INDEX_ROLL] = GestureType.TYPE_UP;
			}
			
			// send the gesture and mark the pitch to compare it in the
			// next round
			val[INDEX_ROLL] = roll;
			interval[INDEX_ROLL] = roll - lastRoll;
			lastRoll = roll;
			
			// -----------------------------------------------------------
			// Finally send gesture, which is composed of the arrays defined before
			sendGesture(type, val, interval);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "Error while converting gesture: " + e.toString());
		}
	}

	/**
	 * Converts a gesture into a proper UDP message
	 * 
	 * @param type
	 *            The gesture types
	 * @param val
	 *            The values of the gestures
	 * @param interval
	 *            The intervals of the gestures
	 */
	private void sendGesture(int type[], double val[], double interval[]) {
		// TODO the message is composed of integers only at the moment
		
		String message = "";
		message += "LR/" + type[INDEX_PITCH] + "/" + (int) val[INDEX_PITCH] + "/" + (int) Math.abs(interval[INDEX_PITCH]) + "#";
		message += "UD/" + type[INDEX_ROLL] + "/" + (int) val[INDEX_ROLL] + "/" + (int) Math.abs(interval[INDEX_ROLL]) + "#";
		
		// set the last message sent so the activity can display it
		lastMessage = message;
		
		sendMessage(message);
	}

	/**
	 * Sends a message to the UDP server
	 * 
	 * @param message
	 */
	private void sendMessage(String message) {
		try {
			InetAddress local = InetAddress.getByName(serverIp);
			byte[] messageRaw = message.getBytes();
			DatagramPacket p = new DatagramPacket(messageRaw,
					messageRaw.length, local, serverPort);
			sock.send(p);
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
	public static final int TYPE_UP = 0;
	public static final int TYPE_RIGHT = 1;
	public static final int TYPE_DOWN = 2;
	public static final int TYPE_LEFT = 3;
	public static final int TYPE_NO_MOVEMENT = 4;
}
