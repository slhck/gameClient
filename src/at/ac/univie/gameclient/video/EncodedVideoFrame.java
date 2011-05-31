package at.ac.univie.gameclient.video;

public class EncodedVideoFrame {

	private long timestamp;
	private byte[] data;

	public EncodedVideoFrame(long timestamp, byte[] data) {
		this.timestamp = timestamp;
		this.data = data;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public byte[] getData() {
		return data;
	}

}
