package at.ac.univie.gameclient.sip;

import java.net.InetAddress;

public class SipTo {
	private String name;
	private SipAddress address;
	
	public SipTo(String name, SipAddress address) {
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
		s += "To: ";
		s += name+" ";
		s += "<"+address+">";
		
		return s;
	}
}