package at.ac.univie.gameclient.sip;

import java.net.InetAddress;

public class SipRequest {
	public enum Type {
		INVITE
	}

	public class Via {
		InetAddress responseAddress;
		String branch; // TODO: http://tools.ietf.org/html/rfc3261#page-39
	}
	
	public class To {
		String name;
		SipAddress address;
	}
	
	public class From {
		String name;
		SipAddress address;
		String tag; // TODO: http://tools.ietf.org/html/rfc3261#page-26
	}
	
	private Type type;
	private Via via;
	private To to;
	private From from;
	private String callId; // callId@myHostname unique across dialog so TODO: move it to dialog
	private int commandSequence; //increment it TODO:move to dialog
	private int maxForwards = 70;

	public SipRequest(Type type) {
		super();
		
		this.type = type;
	}
}
