package at.ac.univie.gameclient.video;

import android.util.Log;
import at.ac.univie.gameclient.rtp.RtpPacket;
import at.ac.univie.gameclient.udp.DatagramHandler;
import at.ac.univie.gameclient.xvid.Decoder;

public class VideoStreamDecoder implements DatagramHandler {
	private Decoder xvidDecoder;
	private int alreadyInBuffer;
	private long thisPacketTimestamp = Long.MIN_VALUE;
	private long lastPacketTimestamp = Long.MIN_VALUE;

	public VideoStreamDecoder() {
		this.xvidDecoder = new Decoder();
		xvidDecoder.initDecoder(800, 600);
	}

	public synchronized void handleDatagram(byte[] data) {
		lastPacketTimestamp = thisPacketTimestamp;
		RtpPacket rtpPacket = new RtpPacket(data, data.length); // TODO: we just assume 1 incoming packet carries exactly 1 frame - fix this
		byte[] frame = rtpPacket.payload;
		thisPacketTimestamp = rtpPacket.TimeStamp;
		
		alreadyInBuffer = xvidDecoder.copyToDecoderBuffer(frame, frame.length);
		Log.v("cgClient", "copied udp data ("+frame.length+") to decoder buffer (aib = "+alreadyInBuffer+")");
	}

	public synchronized int[] decodeFrame() {
		byte[] ba = xvidDecoder.decodeOneFrame();
		int[] ia = new int[ba.length/3];
		
		for(int i = 0, j = 0; i < ba.length; i = i+3, j++)
		{
			ia[j] =
				(int)(0xff000000 |
						((ba[i] & 0xff) << 16) |
						((ba[i+1] & 0xff) << 8) |
						(ba[i+2] & 0xff));
		}
		
		return ia;
	}

	public synchronized int getAlreadyInBuffer() {
		return alreadyInBuffer;
	}

	public int convertRgbTtoArgb(int rgb) {

		int r = (rgb >> 16) & 0xFF;

		int g = (rgb >> 8) & 0xFF;

		int b = (rgb >> 0) & 0xFF;

		return 0xff000000 | (r << 16) | (g << 8) | b;
	}

	public long getThisPacketTimestamp() {
		return thisPacketTimestamp;
	}

	public long getLastPacketTimestamp() {
		return lastPacketTimestamp;
	}
	

}
