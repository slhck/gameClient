package at.ac.univie.gameclient.video;

public class DecodedVideoFrame {

	private long timestamp;
	private int[] data;

	public DecodedVideoFrame(long timestamp, int[] data) {
		this.timestamp = timestamp;
		this.data = data;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public int[] getData() {
		return data;
	}

}
