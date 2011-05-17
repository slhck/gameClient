package at.ac.univie.gameclient.sip;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.util.Log;

public class SipRequest {
	private SipDialog dialog;
	private SipRequestType type;
	private SipVia via;
	private SipTo to;
	private SipFrom from;
	private int maxForwards = 70;
	private SipContact contact;

	public SipRequest(SipDialog dialog, SipRequestType type) {
		super();
		
		this.dialog = dialog;
		this.type = type;
	}
	
	public String toString()
	{
		String s = "";
		s += type + " " + to.getAddress() + " SIP/2.0" + "\n";
		s += via + "\n";
		s += "Max-Forwards: " + maxForwards + "\n";
		s += to + "\n";
		s += from + "\n";
		s += dialog.getCallId() + "\n";
		s += "CSeq: " + dialog.getCommandSequence() + " " + type + "\n";
		s += contact + "\n";

		return s;
	}

	public SipRequestType getType() {
		return type;
	}

	public void setType(SipRequestType type) {
		this.type = type;
	}

	public SipVia getVia() {
		return via;
	}

	public void setVia(SipVia via) {
		this.via = via;
	}

	public SipTo getTo() {
		return to;
	}

	public void setTo(SipTo to) {
		this.to = to;
	}

	public SipFrom getFrom() {
		return from;
	}

	public void setFrom(SipFrom from) {
		this.from = from;
	}

	public int getMaxForwards() {
		return maxForwards;
	}

	public void setMaxForwards(int maxForwards) {
		this.maxForwards = maxForwards;
	}
	
	public void send()
	{
		dialog.send(this);
	}
}
