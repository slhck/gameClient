package at.ac.univie.gameclient.sip;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SipContact {
	String displayName;
	SipAddress address;
	// TODO: header parameters
	
	public SipContact(String displayName, SipAddress address) {
		this.displayName = displayName;
		this.address = address;
	}
	
	@Override
	public String toString() {
		String returnString = "Contact: ";
		returnString += "\""+displayName+"\" ";
		returnString += "<"+address+">";
		
		return super.toString();
	}
	
	// input is expected as a single line
	// abbreviation of "Contact" to "m" will not be recognized
	// only display name and sip uri are supported and must be present! - other fields will cause errors
	public static SipContact parse(String data)
	{
		Pattern pattern = Pattern.compile("(.*) \"(.*)\" <(.*)>");
		Matcher matcher = pattern.matcher(data);
		if(matcher.matches())
		{
			System.out.println(matcher.group(1));
			System.out.println(matcher.group(2));
			System.out.println(matcher.group(3));
		}
		
		return new SipContact("blub", new SipAddress(""));
	}
}