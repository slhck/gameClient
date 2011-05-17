package at.ac.univie.gameclient.sip;

public class SipCallId {
	String caller;
	String random;

	public SipCallId(String caller, String random) {;
		this.caller = caller;
		this.random = random;
	}

	public String getCaller() {
		return caller;
	}

	public void setCaller(String caller) {
		this.caller = caller;
	}

	public String getRandom() {
		return random;
	}

	public void setRandom(String random) {
		this.random = random;
	}
	
	public String toString()
	{
		String s = "";
		s += "Call-ID: ";
		s += random + "@" + caller;
		
		return s;
	}
}
