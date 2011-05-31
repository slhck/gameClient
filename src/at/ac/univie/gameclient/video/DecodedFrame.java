package at.ac.univie.gameclient.video;

public class DecodedFrame {

	private int[] data;
	private long timestamp;

	public DecodedFrame(int[] data, long timestamp) {
		this.data = data;
		this.timestamp = timestamp;
	}

	public int[] getData() {
		return data;
	}

	public long getTimestamp() {
		return timestamp;
	}

}
