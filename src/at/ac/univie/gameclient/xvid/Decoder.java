package at.ac.univie.gameclient.xvid;

public class Decoder {
	static {
		System.loadLibrary("xviddecoder");
		System.loadLibrary("xvidjnidecoder");
	}

	public native void initDecoder(int width, int height);
	public native int copyToDecoderBuffer(byte[] data, int length);
	public native byte[] decodeOneFrame();
	//public native byte[] getFrame();
}
