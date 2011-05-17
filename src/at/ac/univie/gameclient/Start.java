package at.ac.univie.gameclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import at.ac.univie.gameclient.video.GameActivity;

public class Start extends Activity {

	private static final int DIALOG_ABOUT_ID = 0;
	private static final String TAG = "Start";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		Button buttonStart = (Button) findViewById(R.id.button_start);
		buttonStart.setOnClickListener(mButtonStartListener);

		Log.v(TAG, "Started Game Client");
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
			 Log.v(TAG, "Preference Menu called");
			 Intent prefIntent = new Intent();
			 prefIntent.setClass(getApplicationContext(),
					 PreferenceActivity.class);
			 startActivity(prefIntent);
		 return true;
		
		 // Display the about dialog
		 case R.id.menu_about:
			 showDialog(DIALOG_ABOUT_ID);
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
			Log.v(TAG, "Start Button called");
			Intent sessionIntent = new Intent();
			sessionIntent.setClass(getApplicationContext(),
					GameActivity.class);
			startActivity(sessionIntent);
		}
	};

}