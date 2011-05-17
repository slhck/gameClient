package at.ac.univie.gameclient.video;

import java.io.IOException;
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
import at.ac.univie.gameclient.SipServerRequest;
import at.ac.univie.gameclient.gesture.GestureLogger;
import at.ac.univie.gameclient.sip.SipDialog;
import at.ac.univie.gameclient.sip.SipRequest;
import at.ac.univie.gameclient.sip.SipRequestType;

public class GameActivity extends Activity implements SensorEventListener {

	// General members
	private GLSurfaceView mGLView;
	private String mFilename;
	private SharedPreferences mPreferences;
	private static final String TAG = "GameActivity";

	// SIP and Log server members
	private String mServerIp;
	private int mServerPort;
	private int mServerPortLog;
	private SipDialog mSipDialog;

	// Members related to gesture logging
	private static final int m_matrix_size = 16;
	float[] m_R = new float[m_matrix_size];
	float[] m_R_out = new float[m_matrix_size];
	float[] m_I = new float[m_matrix_size];
	float[] m_rotated = new float[3];
	float[] m_magnetic = new float[3];
	float[] m_accel = new float[3];
	GestureLogger mGestureLogger;
	private static final int SPEED = SensorManager.SENSOR_DELAY_FASTEST;
	SensorManager m_sm = null;
	PowerManager m_pm = null;
	WakeLock m_wl = null;

	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initPrefs();
		//initSip();
		initGesture();
		initView();
	}
	
	protected void onResume(Bundle savedInstanceState) {
		super.onResume();

		m_wl.acquire();
        
        // register this class as a listener for the magnetic field sensor
        m_sm.registerListener(this,
        		m_sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SPEED); 
        // register this class as a listener for the acceleration sensor
        m_sm.registerListener(this,
        		m_sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SPEED); 
	}

	protected void onPause(Bundle savedInstanceState) {
		super.onPause();
		
        // unregister listener and clean up
        if ( m_sm != null )
        	m_sm.unregisterListener(this);
        if ( m_wl != null )
        	m_wl.release();
        super.onStop();
		
		// sendBye();
	}
	
	private void initView() {
		Log.v(TAG, "Starting video view ... ");
		mFilename = "/mnt/sdcard/videos/test.mp4";
		mGLView = new VideoGLSurfaceView(this, mFilename);
		setContentView(mGLView);
	}

	private void initGesture() {
		m_sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		m_pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		m_wl = m_pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "Video Playback");
		mGestureLogger =  new GestureLogger(mServerIp, mServerPortLog);
	}

	private void initSip() {
		try {
			Log.v(TAG, "Sending SIP Invite");
			mSipDialog = new SipDialog(mServerIp, mServerPort);
			SipRequest sipInviteRequest = new SipServerRequest(mSipDialog,
					SipRequestType.INVITE);
			sipInviteRequest.send();
		} catch (UnknownHostException e) {
			Log.e(TAG, "Could not connect to host: " + e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, "Could not connect to host: " + e.getMessage());
		} catch (Exception e) {
			Log.e(TAG, "Error while sending SIP Invite: " + e.getMessage());
		}
	}

	private void stopSip() {
		Log.v(TAG, "Sending SIP Bye");
		try {
			SipRequest sipByeRequest = new SipServerRequest(mSipDialog,
					SipRequestType.BYE);
			sipByeRequest.send();
			mSipDialog.close();
		} catch (IOException e) {
			Log.e(TAG, "Could not connect to host: " + e.getMessage());
		} catch (Exception e) {
			Log.e(TAG, "Error while sending SIP Bye: " + e.getMessage());
		}
	}

	/**
	 * Updates the preferences when the activity is resumed
	 */
	private void initPrefs() {
		Log.v(TAG, "Reloading preferences");
		try {
			mPreferences = PreferenceManager
					.getDefaultSharedPreferences(getBaseContext());
			mServerIp = mPreferences.getString("serverIp", null);
			mServerPort = Integer.parseInt(mPreferences.getString("serverPort", null));
			mServerPortLog = Integer.parseInt(mPreferences.getString("serverPortLog", null));
		} catch (NumberFormatException e) {
			Log.e(TAG, "Error loading preferences: " + e.getMessage());
			Log.e(TAG, "Exiting video view.");
			finish();
		}
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// shouldn't need to do anything here
	}

	/**
	 * Evaluates sensor changes. Code base from CACMTV, written by Ewald Hotop
	 */
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
			SensorManager.getOrientation(m_R, m_rotated);
			double pitch = Math.toDegrees(m_rotated[1]);
			Log.v(TAG, "Current pitch: " + pitch);
		}
	}

}
