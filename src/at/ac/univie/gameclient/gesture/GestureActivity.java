package at.ac.univie.gameclient.gesture;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;
import at.ac.univie.gameclient.R;
import at.ac.univie.gameclient.R.id;
import at.ac.univie.gameclient.R.layout;

public class GestureActivity extends Activity implements SensorEventListener {

	private static final String TAG = "GestureActivity";
	
	private static final int 	m_matrix_size = 16;
	private static final int	SPEED = SensorManager.SENSOR_DELAY_FASTEST;	// fastest reaction rate
	
	SensorManager 	m_sm = null;
	PowerManager 	m_pm = null;
	WakeLock 		m_wl = null;
	
	TextView		m_yaw = null;
	TextView		m_pitch = null;
	TextView		m_roll = null;
	TextView		m_message = null;
	
	float[] 		m_R 		= new float[m_matrix_size];
	float[] 		m_R_out 	= new float[m_matrix_size];
	float[] 		m_I 		= new float[m_matrix_size];
	float[] 		m_rotated 	= new float[3];
	float[]			m_magnetic	= new float[3];
	float[]			m_accel		= new float[3];

	private SharedPreferences mPreferences;

	private String mServerIp;
	private int mServerPort;
	private int mServerPortLog;

	private GestureLogger mGestureLogger;
	private long lastLog = 0;
	private static final int DELAY = 100;
			
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Prepare tools for vibration, sensor management and wakelock
        m_sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        m_pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        m_wl = m_pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "Gesture Test");
        
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
		
		// create a gesture logger
		try {
			mGestureLogger = new GestureLogger(mServerIp, mServerPort);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "Could not create Gesture Logger: " + e.toString());
		}
		
		// set its preferences
		try {
			mGestureLogger.setPreferences(PreferenceManager.getDefaultSharedPreferences(getBaseContext()));			
		} catch (Exception e) {
			Log.e(TAG, "Could not set Logger's preferences: " + e.toString());
		}
        
        // Get all Views from the main layout
        setContentView(R.layout.gesture_log);
        
        m_yaw = (TextView) findViewById(R.id.textYaw);
        m_pitch = (TextView) findViewById(R.id.textPitch);
        m_roll = (TextView) findViewById(R.id.textRoll);
        m_message = (TextView) findViewById(R.id.textMessage);
    }

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	public void onSensorChanged(SensorEvent event) {

		// Do not evaluate if too soon
		long currentTime = System.currentTimeMillis();
		if (currentTime <= (lastLog + DELAY)) {
			return;
		}
		lastLog = currentTime;
		
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
			
			// Correct if screen is in Landscape
			SensorManager.remapCoordinateSystem(m_R,
			SensorManager.AXIS_X,
			SensorManager.AXIS_Z, m_R_out);
			
			SensorManager.getOrientation(m_R, m_rotated);
			double yaw = Math.toDegrees(m_rotated[0]);
			double pitch = Math.toDegrees(m_rotated[1]);
			double roll = Math.toDegrees(m_rotated[2]);
			
			m_yaw.setText("Yaw:    " + (int) yaw);
			m_pitch.setText("Pitch: " + (int) pitch);
			m_roll.setText("Roll:  " + (int) roll);
			
			mGestureLogger.sendGestureFromSensor(yaw, pitch, roll);
			
			m_message.setText(mGestureLogger.lastMessage);
		}
	}

	
	@Override
    protected void onResume() {
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
    
    @Override
    protected void onStop() {
    	super.onStop();
    	
        // unregister listener and clean up
        if ( m_sm != null )
        	m_sm.unregisterListener(this);
        if ( m_wl != null )
        	m_wl.release();
    }    

}