package at.ac.univie.gameclient.game;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;
import at.ac.univie.gameclient.gesture.GestureLogger;
import at.ac.univie.gameclient.sip.SipDialog;
import at.ac.univie.gameclient.sip.SipRequest;
import at.ac.univie.gameclient.sip.SipRequestType;
import at.ac.univie.gameclient.sip.SipServerRequest;
import at.ac.univie.gameclient.udp.DatagramReceiver;
import at.ac.univie.gameclient.video.Panel;
import at.ac.univie.gameclient.video.VideoStreamDecoder;

public class GameActivity extends Activity implements SensorEventListener {

	
	// General members
	private SharedPreferences mPreferences;
	private static final String TAG = "GameActivity";
	DatagramReceiver datagramReceiver;
	DatagramSocket videoStreamSocket;
	PowerManager.WakeLock wl;
	
	// SIP and Log server members
	private String mServerIp;
	private int mServerPort;
	private int mServerPortLog;
	SipDialog sipDialog;

	// Members related to gesture logging
	private static final int m_matrix_size = 16;
	private static final int SPEED = SensorManager.SENSOR_DELAY_FASTEST;

	SensorManager m_sm = null;
	PowerManager m_pm = null;
	WakeLock m_wl = null;

	TextView m_pitch = null;

	float[] m_R = new float[m_matrix_size];
	float[] m_R_out = new float[m_matrix_size];
	float[] m_I = new float[m_matrix_size];
	float[] m_rotated = new float[3];
	float[] m_magnetic = new float[3];
	float[] m_accel = new float[3];

	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			
		Log.d(TAG, "Reloading preferences");
		
		try {
			mPreferences = PreferenceManager
					.getDefaultSharedPreferences(getBaseContext());
			mServerIp = mPreferences.getString("serverIp", null);
			mServerPort = Integer.parseInt(mPreferences.getString("serverPort", "0"));
			mServerPortLog = Integer.parseInt(mPreferences.getString("serverPortLog", "0"));
		} catch (NumberFormatException e) {
			Log.e(TAG, "Error loading preferences: " + e.getMessage());
			Log.e(TAG, "Exiting video view.");
			finish();
		}
		Log.d(TAG, "Receiving from: " + mServerIp);
		Log.d(TAG, "Port: " + mServerPort);
		Log.d(TAG, "Sending Gestures to: " + mServerPortLog);
		
		// Prepare tools for vibration, sensor management and wakelock
        m_sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        m_pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        m_wl = m_pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "Game Test");
				
		// mGestureLogger =  new GestureLogger(mServerIp, mServerPortLog);
		
		// the rest of initialization is in the onResume method
	}
	
	protected void onResume(Bundle savedInstanceState) {
		
		Log.d(TAG, "onResume called");
		
		m_wl.acquire();
		
        // register this class as a listener for the magnetic field sensor
        m_sm.registerListener(this,
        		m_sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SPEED); 
        // register this class as a listener for the acceleration sensor
        m_sm.registerListener(this,
        		m_sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SPEED); 
		
		VideoStreamDecoder videoStreamDecoder = new VideoStreamDecoder();
        setContentView(new Panel(this, videoStreamDecoder));
       
        try {
        	videoStreamSocket = new DatagramSocket(5444);
			datagramReceiver = new DatagramReceiver(videoStreamSocket, videoStreamDecoder);
			datagramReceiver.start();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			sipDialog = new SipDialog(mServerIp, mServerPort);
			SipRequest sipInviteRequest = new SipServerRequest(sipDialog,
					SipRequestType.INVITE);
			sipInviteRequest.send();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		super.onResume();
	}

	protected void onPause(Bundle savedInstanceState) {
		
		Log.d(TAG, "onPause called");
		
		datagramReceiver.interrupt();
		videoStreamSocket.close();

		SipRequest sipByeRequest = new SipServerRequest(sipDialog, SipRequestType.BYE);
		sipByeRequest.send();
		try {
			sipDialog.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		m_wl.release();

		super.onPause();
	}
	
    @Override
    protected void onStop() {
        // unregister listener and clean up
        if ( m_sm != null )
        	m_sm.unregisterListener(this);
        super.onStop();
    }  

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	public void onSensorChanged(SensorEvent event) {
		// Do not evaluate sensor data if not reliable
		if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
			return;
				
		// Which sensor has changed?
		switch (event.sensor.getType()) {
		case Sensor.TYPE_MAGNETIC_FIELD:
			m_magnetic = event.values.clone();
			break;
		case Sensor.TYPE_ACCELEROMETER:
			m_accel = event.values.clone();
			break;
		}


		// Now calculate azimuth, pitch and roll
		if (m_magnetic != null && m_accel != null) {
			SensorManager.getRotationMatrix(m_R, m_I, m_accel, m_magnetic);
			/*
			// Correct if screen is in Landscape
			SensorManager.remapCoordinateSystem(m_R,
			SensorManager.AXIS_X,
			SensorManager.AXIS_Z, m_R_out);
			*/
			SensorManager.getOrientation(m_R, m_rotated);
			Log.d(TAG, "Pitch: " + Math.toDegrees(m_rotated[1]));
		}	
	}	

}
