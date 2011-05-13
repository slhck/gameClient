package at.ac.univie.gameclient.sdp;

public class SdpMediaDescription {
	public class RtpMap {
		String payloadType;
		String encodingName;
		int clockRate;
		
		public String getPayloadType() {
			return payloadType;
		}
		
		public void setPayloadType(String payloadType) {
			this.payloadType = payloadType;
		}
		
		public String getEncodingName() {
			return encodingName;
		}
		
		public void setEncodingName(String encodingName) {
			this.encodingName = encodingName;
		}
		
		public int getClockRate() {
			return clockRate;
		}
		
		public void setClockRate(int clockRate) {
			this.clockRate = clockRate;
		}
	}

	public enum Media {
		audio, video, text, application, message
	}

	Media media;
	String port;
	int numberOfPorts;
	String proto; // z.B: RTP/AVP
	String fmt; // e.g. payload type
	
	RtpMap rtpMap;
	
	public SdpMediaDescription() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Media getMedia() {
		return media;
	}

	public void setMedia(Media media) {
		this.media = media;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public int getNumberOfPorts() {
		return numberOfPorts;
	}

	public void setNumberOfPorts(int numberOfPorts) {
		this.numberOfPorts = numberOfPorts;
	}

	public String getProto() {
		return proto;
	}

	public void setProto(String proto) {
		this.proto = proto;
	}

	public String getFmt() {
		return fmt;
	}

	public void setFmt(String fmt) {
		this.fmt = fmt;
	}
}
