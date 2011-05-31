package at.ac.univie.gameclient.sip;


public class SipConstants {
	public static SipTo to = new SipTo("Server", new SipAddress("sip:server@192.168.0.86"));
	public static SipFrom from = new SipFrom("Client", new SipAddress("sip:client@192.168.0.9"));
	public static SipVia via = new SipVia(SipUtility.getLocalIpAddress()+":5554");
}
