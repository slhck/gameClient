package at.ac.univie.gameclient;

import at.ac.univie.gameclient.sip.SipDialog;
import at.ac.univie.gameclient.sip.SipRequest;
import at.ac.univie.gameclient.sip.SipRequestType;

public class SipServerRequest extends SipRequest {
	
	
	public SipServerRequest(SipDialog dialog, SipRequestType type) {
		super(dialog, type);
		
		setTo(SipConstants.to);
		
		SipConstants.via.renewBranch();
		setVia(SipConstants.via);
		
		setFrom(SipConstants.from);
	}

	
}
