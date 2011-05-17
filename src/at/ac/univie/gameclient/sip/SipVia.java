package at.ac.univie.gameclient.sip;

public class SipVia {
	public SipVia(String responseAddress) {
		this.responseAddress = responseAddress;
		renewBranch();
	}
	
	String responseAddress;
	String branch; // TODO: http://tools.ietf.org/html/rfc3261#page-39

	public String getResponseAddress() {
		return responseAddress;
	}
	public void setResponseAddress(String responseAddress) {
		this.responseAddress = responseAddress;
	}
	public String getBranch() {
		return branch;
	}
	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String toString()
	{
		String s = "";
		s += "Via: ";
		s += responseAddress+";";
		s += "branch="+branch;
		
		return s;
	}
	public void renewBranch() {
		branch = SipUtility.createId();
	}
}