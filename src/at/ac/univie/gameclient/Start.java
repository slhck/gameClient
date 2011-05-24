package at.ac.univie.gameclient;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import at.ac.univie.gameclient.sip.SipDialog;
import at.ac.univie.gameclient.sip.SipRequest;
import at.ac.univie.gameclient.sip.SipRequestType;
import at.ac.univie.gameclient.sip.SipServerRequest;
import at.ac.univie.gameclient.udp.DatagramReceiver;
import at.ac.univie.gameclient.video.Panel;
import at.ac.univie.gameclient.video.VideoActivity;
import at.ac.univie.gameclient.video.VideoStreamDecoder;

public class Start extends Activity {
	private static final int DIALOG_ABOUT_ID = 0;
	private static final String TAG = "Start";
	
	private String mServerIp = "192.168.0.87";
	private int mServerPort = 20248;
	private SharedPreferences mPreferences;

	SipDialog sipDialog;
	
	DatagramReceiver datagramReceiver;
	DatagramSocket videoStreamSocket;
	
	PowerManager.WakeLock wl;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");

/*
		try {
			mPreferences = this.getPreferences(MODE_WORLD_READABLE);
			mServerIp = mPreferences.getString("serverIp", null);
			mServerPort = Integer.parseInt(mPreferences.getString("serverPort",
					null));
		} catch (Exception e) {
			Log.e(TAG, "Failed to load preferences: " + e.getMessage());
		}*/
		
		/*Button buttonStart = (Button) findViewById(R.id.button_start);
		buttonStart.setOnClickListener(mButtonStartListener);


        TextView  tv = new TextView(this);
        int       x  = 1000;
        int       y  = 42;
*/
        // here, we dynamically load the library at runtime
        // before calling the native method.
        //


        

        //tv.setText( "init");
        //setContentView(tv);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		// sipDialog.testThread.interrupt();
		
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
		
		wl.release();

		super.onPause();
	}

	@Override
	protected void onResume() {
		wl.acquire();
		
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

		super.onResume();
	}

	/**
	 * Populates the options menu
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	/**
	 * Handles the option menu selections
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		//	change to the preferences activity
		 case R.id.menu_preferences:
			 Intent prefIntent = new Intent();
			 prefIntent.setClass(getApplicationContext(),
					 PreferenceActivity.class);
			 startActivity(prefIntent);
		 return true;
		
		 // Display the about dialog
		 case R.id.menu_about:
			 // showDialog(DIALOG_ABOUT_ID);
			 return true;
		
		 // Exit the application
		 case R.id.menu_exit:
			 this.finish();
			 return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	/**
	 * Handles the dialogs shown in the application
	 */
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {

		// display the about dialog
		case DIALOG_ABOUT_ID:
			AlertDialog.Builder builderAbout = new AlertDialog.Builder(this);
			builderAbout.setTitle("About").setMessage(
					R.string.about).setCancelable(false)
					.setPositiveButton("Close",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.dismiss();
								}
							});
			dialog = (AlertDialog) builderAbout.create();
			break;

		default:
			dialog = null;
		}
		return dialog;
	}

	/**
	 * Click listener for the start button
	 */
	private OnClickListener mButtonStartListener = new OnClickListener() {
		public void onClick(View v) {
			
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
			
			Intent sessionIntent = new Intent();
			sessionIntent.setClass(getApplicationContext(),
					VideoActivity.class);
			startActivity(sessionIntent);
		}
	};
}