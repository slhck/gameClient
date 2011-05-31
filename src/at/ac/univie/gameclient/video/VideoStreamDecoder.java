package at.ac.univie.gameclient.video;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.util.Log;
import at.ac.univie.gameclient.rtp.RtpPacket;
import at.ac.univie.gameclient.udp.DatagramHandler;
import at.ac.univie.gameclient.xvid.Decoder;

public class VideoStreamDecoder implements DatagramHandler {
	private Decoder xvidDecoder;
	private int alreadyInBuffer;
	private long thisPacketTimestamp = Long.MIN_VALUE;
	private long lastPacketTimestamp = Long.MIN_VALUE;
	private LinkedBlockingQueue<byte[]> encodedFrameParts = new LinkedBlockingQueue<byte[]>();
	//private LinkedBlockingQueue<DecodedFrame> decodedFrames = new LinkedBlockingQueue<DecodedFrame>();
	private LinkedBlockingQueue<EncodedVideoFrame> encodedFrames = new LinkedBlockingQueue<EncodedVideoFrame>();
	private int i;
	private Object lock = new Object();
	
	int lastSequenceNumber = Integer.MIN_VALUE;
	boolean framePartLost = false;
	boolean firstIteration = true;

	public VideoStreamDecoder() {
		this.xvidDecoder = new Decoder();
		xvidDecoder.initDecoder(800, 480);
	}

	public /*synchronized*/ void handleDatagram(byte[] data) {
		lastPacketTimestamp = thisPacketTimestamp;
		RtpPacket rtpPacket = new RtpPacket(data, data.length); // TODO: we just assume 1 incoming packet carries exactly 1 frame - fix this
		
		Log.v("cgClient_rtp", rtpPacket.SequenceNumber+" "+rtpPacket.TimeStamp);
		byte[] framePart = rtpPacket.payload;
		thisPacketTimestamp = rtpPacket.TimeStamp;
		
		Log.v("IncomingRTP", rtpPacket.SequenceNumber+" "+rtpPacket.TimeStamp+" "+((rtpPacket.Marker == 1) ? "Marker" : ""));
		
		if(firstIteration)
		{
			lastSequenceNumber = rtpPacket.SequenceNumber-1;
			firstIteration = false;
		}
		
		if(lastSequenceNumber != rtpPacket.SequenceNumber-1)
		{
			encodedFrameParts.clear();
			framePartLost = true;
		}
		else if(!framePartLost)
		{
			encodedFrameParts.offer(framePart);
		}

		if(rtpPacket.Marker == 1)
		{
			if(!framePartLost)
			{
				/*byte[] packet;
				while((packet = encodedFrameParts.poll()) != null)
					xvidDecoder.copyToDecoderBuffer(packet, packet.length);
				
				int[] ia = decodeFrame();
				DecodedFrame decodedFrame = new DecodedFrame(ia, rtpPacket.TimeStamp);
				
				//synchronized(lock)
				{
					decodedFrames.offer(decodedFrame);
				}
				
				Log.v("cgClient", "decoded a frame and decodedFrames FIFO Queue has size "+decodedFrames.size());
				*/
				
				Iterator<byte[]> encodedFramePartsIterator = encodedFrameParts.iterator();
				int size = 0;
				while(encodedFramePartsIterator.hasNext())
					size += encodedFramePartsIterator.next().length;
				
				ByteBuffer frame = ByteBuffer.allocate(size);
				byte[] dataPart;
				while((dataPart = encodedFrameParts.poll()) != null)
					frame.put(dataPart);
				
				encodedFrames.offer(new EncodedVideoFrame(rtpPacket.TimeStamp, frame.array()));
				
				Log.v("IncomingRTP", "got frame");
			}
			
			framePartLost = false;
		}
		/*alreadyInBuffer = xvidDecoder.copyToDecoderBuffer(frame, frame.length);
		Log.v("cgClient", (i++)+" copied udp data ("+frame.length+") to decoder buffer (aib = "+alreadyInBuffer+")");
		int[] ia = decodeFrame();
		decodedFrames.offer(ia);*/
		
		lastSequenceNumber = rtpPacket.SequenceNumber;
		
		Log.v("EncodedFrameBuffer", "size: "+encodedFrames.size());
		
	}
	
	/*
	public int[] getFrame()
	{
		DecodedFrame frame;
		//synchronized(lock)
		{
			frame = decodedFrames.poll();
		}
		
		if(frame != null)
		Log.v("cg_CLient", "TIMESTAMP:"+frame.getTimestamp());
		
		if(frame != null)
			return frame.getData();
		
		return null;
	}*/

	public DecodedVideoFrame decodeFrame() {
		EncodedVideoFrame encodedFrame = encodedFrames.poll();
		
		if(encodedFrame != null)
		{
			xvidDecoder.copyToDecoderBuffer(encodedFrame.getData(), encodedFrame.getData().length);
			byte[] ba = xvidDecoder.decodeOneFrame();
			//Log.v("cg_client", "allocating "+ba.length/3);
			int[] ia = new int[ba.length/3];
			
			for(int i = 0, j = 0; i < ba.length; i = i+3, j++)
			{
				ia[j] =
					(int)(0xff000000 |
							((ba[i] & 0xff) << 16) |
							((ba[i+1] & 0xff) << 8) |
							(ba[i+2] & 0xff));
			}

			return new DecodedVideoFrame(encodedFrame.getTimestamp(), ia);
		}
		
		return null;
	}
	
	void dummydecoder()
	{
		EncodedVideoFrame encodedFrame = encodedFrames.poll();
		
		if(encodedFrame != null)
		{
		xvidDecoder.copyToDecoderBuffer(encodedFrame.getData(), encodedFrame.getData().length);
		byte[] ba = xvidDecoder.decodeOneFrame();
		}
	}

	public long getThisPacketTimestamp() {
		return thisPacketTimestamp;
	}

	public long getLastPacketTimestamp() {
		return lastPacketTimestamp;
	}
	

}
