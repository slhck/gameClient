package at.ac.univie.gameclient.video;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class VideoActivity extends Activity {

	private GLSurfaceView mGLView;
	private String mFilename;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFilename = "/mnt/sdcard/The Naked And Famous - Young Blood.mp4";
        mGLView = new VideoGLSurfaceView(this, mFilename);
        setContentView(mGLView);
    }

	
}
