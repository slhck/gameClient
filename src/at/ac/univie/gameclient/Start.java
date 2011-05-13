package at.ac.univie.gameclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Start extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);       
	}

	public void onResume() {
		super.onResume();
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), at.ac.univie.gameclient.video.VideoActivity.class);
		startActivity(intent);
	}
}