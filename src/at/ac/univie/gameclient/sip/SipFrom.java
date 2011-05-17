package at.ac.univie.gameclient.sip;

public class SipFrom {
	String name;
	SipAddress address;
	String tag = "0"; // TODO: http://tools.ietf.org/html/rfc3261#page-26 and http://tools.ietf.org/html/rfc3261#section-19.3
	
	public SipFrom(String name, SipAddress address) {
		this.name = name;
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SipAddress getAddress() {
		return address;
	}

	public void setAddress(SipAddress address) {
		this.address = address;
	}
	
	public String toString()
	{
		String s = "";
		s += "From: ";
		s += name+" ";
		s += "<"+address+">";
		
		return s;
	}
}
