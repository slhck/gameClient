package at.ac.univie.gameclient.sdp;

import java.net.InetAddress;

public class SdpConnectionData {
	private SdpNetType netType; // IN
	private SdpAddrType addrType; // IP4 or IP6
	private InetAddress connectionAddress; // TODO: IPv4 multicast support, TTL, ...
	
	public SdpConnectionData() {
		super();
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

	public InetAddress getConnectionAddress() {
		return connectionAddress;
	}

	public void setConnectionAddress(InetAddress connectionAddress) {
		this.connectionAddress = connectionAddress;
	}
}
