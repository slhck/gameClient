package at.ac.univie.gameclient.video;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Panel extends SurfaceView implements SurfaceHolder.Callback {
    private VideoPresenter _thread;
    private VideoStreamDecoder videoStreamDecoder;
	private long latestTimestamp;
	private int frameCounter = 0;

    public Panel(Context context, VideoStreamDecoder videoStreamDecoder) {
        super(context);
        getHolder().addCallback(this);
        _thread = new VideoPresenter(getHolder(), this);
        this.videoStreamDecoder = videoStreamDecoder;
    }

    @Override
    public void onDraw(Canvas canvas) {
    	int decCount;
    	if(videoStreamDecoder.getAlreadyInBuffer() > 1)
    	{
    		int[] ia = videoStreamDecoder.decodeFrame();
	    	//if(ia.length > 0)
	    	{
		    	Bitmap bitmap = Bitmap.createBitmap(ia, 800, 600, Bitmap.Config.ARGB_8888);
		        //Bitmap _scratch = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
		        canvas.drawColor(Color.WHITE);
		        canvas.drawBitmap(bitmap, 0, 0, null);
	    	}
	    	
	    	//Log.v("cgClient", "length "+ia.length);
	        Log.v("cgClient", "FRAC"+(++frameCounter)+" decoded a frame");
	        
	        /*if(videoStreamDecoder.getLastPacketTimestamp() != Long.MIN_VALUE)
	        {
	        	long sleeptime = videoStreamDecoder.getThisPacketTimestamp()-videoStreamDecoder.getLastPacketTimestamp();
		        long realtime = System.currentTimeMillis()-latestTimestamp;
	        	if(realtime < sleeptime)
	        	try {
					Thread.sleep(sleeptime - realtime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }*/
	        
	        latestTimestamp = System.currentTimeMillis();
    	}
    }

    public void surfaceCreated(SurfaceHolder holder) {
        _thread.setRunning(true);
        _thread.start();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // simply copied from sample application LunarLander:
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        boolean retry = true;
        _thread.setRunning(false);
        while (retry) {
            try {
                _thread.join();
                retry = false;
            } catch (InterruptedException e) {
                // we will try it again and again...
            }
        }
    }

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}
}
