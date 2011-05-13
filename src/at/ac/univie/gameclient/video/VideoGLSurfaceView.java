package at.ac.univie.gameclient.video;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class VideoGLSurfaceView extends GLSurfaceView {
    private GLRenderer renderer;
    private String filename;
    
    native void native_start();
    native void native_gl_resize(int w, int h);
    native void native_gl_render();
    native void native_init(String filename);
    native void destroy();
	
    static {
           System.loadLibrary("ffmpeg");
    }
    
    public VideoGLSurfaceView(Context context, String filename) {
        super(context);
        
        this.filename = filename;
        
        (new Thread() {
            @Override
            public void run() {
                    native_start();
            }
        }).start();
        
        setRenderer(new GLRenderer());
    }
    
    public boolean onTouchEvent(final MotionEvent event) {

    	return true;
    }
    
    class GLRenderer implements GLSurfaceView.Renderer {
    	// @Override
    	public void onDrawFrame(GL10 gl) {
    		native_gl_render();
    	}

    	// @Override
    	public void onSurfaceChanged(GL10 gl, int width, int height) {
    		native_gl_resize(width, height);
    	}

    	// @Override
    	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    		native_init(filename);
    	}
    }
}




