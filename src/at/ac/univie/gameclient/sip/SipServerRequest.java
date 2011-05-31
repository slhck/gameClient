package at.ac.univie.gameclient.sip;


public class SipServerRequest extends SipRequest {
	
	
	public SipServerRequest(SipDialog dialog, SipRequestType type) {
		super(dialog, type);
		
		setTo(SipConstants.to);
		
		SipConstants.via.renewBranch();
		setVia(SipConstants.via);
		
		setFrom(SipConstants.from);
	}

	
}
