package at.ac.univie.gameclient.sip;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;

import android.util.Log;

public class SipUtility {
	public static String md5(String s)
	{    
	    MessageDigest md5 = null;
	    try {
	        md5 = MessageDigest.getInstance("MD5");
	    }
	    catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }
	 
	    md5.reset();
	    md5.update(s.getBytes());
	    byte[] result = md5.digest();
	 
	    StringBuffer hexString = new StringBuffer();
	    for (int i = 0; i < result.length; i++) {
	        hexString.append(Integer.toHexString(0xFF & result[i]));
	    }
	 
	    return hexString.toString();        
	}
	
	public static String getLocalIpAddress() {
	    try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                if (!inetAddress.isLoopbackAddress()) {
	                    return inetAddress.getHostAddress().toString();
	                }
	            }
	        }
	    } catch (SocketException ex) {
	        Log.e("Sip", ex.toString());
	    }
	    return null;
	}
	
	public static String createId()
	{
		return SipUtility.md5(SipUtility.getLocalIpAddress()+"***"+System.currentTimeMillis());
	}
}
