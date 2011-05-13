package at.ac.univie.gameclient.sdp;

public class SdpMessage {
	/* v */
	String protocolVersion = "0";
	
	/* o */
	SdpOrigin origin;
	
	/* s */
	String sessionName = " "; // TODO: only ISO 10646 chars allowed
	
	/* c */
	SdpConnectionData connectionData;
	
	/* m */
	SdpMediaDescription mediaDescription;
	
	/*
	 * Ignored fields of sdp: (TODO: Implement these fields)
	 * a (for sessionName)
	 * i
	 * u
	 * e
	 * p
	 * b
	 * t is assumed permanent (TODO: allow timings)
	 * r
	 * z
	 * k
	 * a (at session level)
	 */

	public String getProtocolVersion() {
		return protocolVersion;
	}

	public void setProtocolVersion(String protocolVersion) {
		this.protocolVersion = protocolVersion;
	}

	public SdpOrigin getOrigin() {	
		return origin;
	}

	public void setOrigin(SdpOrigin origin) {
		this.origin = origin;
	}

	public String getSessionName() {
		return sessionName;
	}

	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}
}
