package at.ac.univie.gameclient.sdp;

import java.net.InetAddress;

public class SdpOrigin {
	private String username; // must not contain spaces! "-" if host does not support usernames
	private String sessId; // NTP timestamp recommended
	private String sessVersion; // NTP timestamp recommended
	private SdpNetType netType; // IN
	private SdpAddrType addrType; // IP4 or IP6
	private InetAddress unicastAddress;
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		if(username.contains(" "))
			throw new RuntimeException("Username must not contain spaces!");
		
		this.username = username;
	}
	
	public String getSessId() {
		return sessId;
	}
	
	public void setSessId(String sessId) {
		this.sessId = sessId;
	}
	
	public String getSessVersion() {
		return sessVersion;
	}
	
	public void setSessVersion(String sessVersion) {
		this.sessVersion = sessVersion;
	}
	
	public SdpNetType getNetType() {
		return netType;
	}
	
	public void setNetType(SdpNetType netType) {
		this.netType = netType;
	}
	
	public SdpAddrType getAddrType() {
		return addrType;
	}
	
	public void setAddrType(SdpAddrType addrType) {
		this.addrType = addrType;
	}
	
	public InetAddress getUnicastAddress() {
		return unicastAddress;
	}
	
	public void setUnicastAddress(InetAddress unicastAddress) {
		if(unicastAddress.isMulticastAddress())
			throw new RuntimeException("Address must be unicast!");
		
		this.unicastAddress = unicastAddress;
	}
}
