package at.ac.univie.gameclient;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;

public class GestureActivity extends Activity implements SensorEventListener {

	private static final String TAG = "GestureActivity";
	
	private static final int 	m_matrix_size = 16;
	private static final int	SPEED = SensorManager.SENSOR_DELAY_FASTEST;	// fastest reaction rate
	
	SensorManager 	m_sm = null;
	PowerManager 	m_pm = null;
	WakeLock 		m_wl = null;
	
	TextView		m_pitch = null;
	
	float[] 		m_R 		= new float[m_matrix_size];
	float[] 		m_R_out 	= new float[m_matrix_size];
	float[] 		m_I 		= new float[m_matrix_size];
	float[] 		m_rotated 	= new float[3];
	float[]			m_magnetic	= new float[3];
	float[]			m_accel		= new float[3];
		
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Prepare tools for vibration, sensor management and wakelock
        m_sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        m_pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        m_wl = m_pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "Gesture Test");
        
        // Get all Views from the main layout
        setContentView(R.layout.gesture_log);
        
        m_pitch = (TextView) findViewById(R.id.textPitch); 
    }

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
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
			m_pitch.setText("Pitch: " + Math.toDegrees(m_rotated[1]));
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
        // unregister listener and clean up
        if ( m_sm != null )
        	m_sm.unregisterListener(this);
        if ( m_wl != null )
        	m_wl.release();
        super.onStop();
    }    

}