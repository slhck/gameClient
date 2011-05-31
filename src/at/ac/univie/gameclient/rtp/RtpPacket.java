package at.ac.univie.gameclient.rtp;

import android.util.Log;

// http://webcache.googleusercontent.com/search?q=cache:hWBgr2ZCXzkJ:www.cs.umbc.edu/~pmundur/courses/CMSC691C/RTPpacket.html+rtp+header+java&cd=2&hl=en&ct=clnk&client=ubuntu&source=www.google.com
public class RtpPacket{

	//size of the RTP header:
	static int HEADER_SIZE = 12;
	
	//Fields that compose the RTP header
	public int Version;
	public int Padding;
	public int Extension;
	public int CC;
	public int Marker;
	public int PayloadType;
	public int SequenceNumber;
	public long TimeStamp;
	public int Ssrc;
	
	//Bitstream of the RTP header
	public byte[] header;
	
	//size of the RTP payload
	public int payload_size;
	//Bitstream of the RTP payload
	public byte[] payload;
	
	
	
	//--------------------------
	//Constructor of an RTPpacket object from header fields and payload bitstream
	//--------------------------
	public RtpPacket(int PType, int Framenb, int Time, byte[] data, int data_length){
	  //fill by default header fields:
	  Version = 2;
	  Padding = 0;
	  Extension = 0;
	  CC = 0;
	  Marker = 0;
	  Ssrc = 0;
	
	  //fill changing header fields:
	  SequenceNumber = Framenb;
	  TimeStamp = Time;
	  PayloadType = PType;
	  
	  //build the header bistream:
	  //--------------------------
	  header = new byte[HEADER_SIZE];
	
	  //.............
	  //TO COMPLETE
	  //.............
	  //fill the header array of byte with RTP header fields
	
	  //header[0] = ...
	  // .....
	
	
	  //fill the payload bitstream:
	  //--------------------------
	  payload_size = data_length;
	  payload = new byte[data_length];
	
	  //fill payload array of byte from data (given in parameter of the constructor)
	  //......
	
	  // ! Do not forget to uncomment method printheader() below !
	
	}
	  
	//--------------------------
	//Constructor of an RTPpacket object from the packet bistream 
	//--------------------------
	public RtpPacket(byte[] packet, int packet_size)
	{
	  //fill default fields:
	  Version = 2;
	  Padding = 0;
	  Extension = 0;
	  CC = 0;
	  Marker = 0;
	  Ssrc = 0;
	
	  //check if total packet size is lower than the header size
	  if (packet_size >= HEADER_SIZE) 
	    {
		//get the header bitsream:
		header = new byte[HEADER_SIZE];
		for (int i=0; i < HEADER_SIZE; i++)
		  header[i] = packet[i];
	
		//get the payload bitstream:
		payload_size = packet_size - HEADER_SIZE;
		payload = new byte[payload_size];
		for (int i=HEADER_SIZE; i < packet_size; i++)
		  payload[i-HEADER_SIZE] = packet[i];
	
		//interpret the changing fields of the header:
		Marker = ((header[1] & 0x80) >> 7);
		PayloadType = header[1] & 127;
		//SequenceNumber = (int)(header[3]) + 256*(int)(header[2]);
		//TimeStamp = (int)(header[7]) + 256*(int)(header[6]) + 65536*(int)(header[5]) + 16777216*(int)(header[4]);
	    //Log.v("TIMESTAMP", TimeStamp+"");
		TimeStamp = bytesToUIntLong(header, 4);
		SequenceNumber = bytesToUIntInt(header, 2);
	    }
	}
	
	//--------------------------
	//getpayload: return the payload bistream of the RTPpacket and its size
	//--------------------------
	public int getpayload(byte[] data) {
	
	  for (int i=0; i < payload_size; i++)
	    data[i] = payload[i];
	
	  return(payload_size);
	}
	
	//--------------------------
	//getpayload_length: return the length of the payload
	//--------------------------
	public int getpayload_length() {
	  return(payload_size);
	}
	
	//--------------------------
	//getlength: return the total length of the RTP packet
	//--------------------------
	public int getlength() {
	  return(payload_size + HEADER_SIZE);
	}
	
	//--------------------------
	//getpacket: returns the packet bitstream and its length
	//--------------------------
	public int getpacket(byte[] packet)
	{
	  //construct the packet = header + payload
	  for (int i=0; i < HEADER_SIZE; i++)
		packet[i] = header[i];
	  for (int i=0; i < payload_size; i++)
		packet[i+HEADER_SIZE] = payload[i];
	
	  //return total size of the packet
	  return(payload_size + HEADER_SIZE);
	}
	
	//--------------------------
	//gettimestamp
	//--------------------------
	
	public long gettimestamp() {
	  return(TimeStamp);
	}
	
	//--------------------------
	//getsequencenumber
	//--------------------------
	public int getsequencenumber() {
	  return(SequenceNumber);
	}
	
	//--------------------------
	//getpayloadtype
	//--------------------------
	public int getpayloadtype() {
	  return(PayloadType);
	}

	/** 
	 * Combines four bytes (most significant bit first) into a 32 bit unsigned integer.
	 * 
	 * @param bytes
	 * @param index of most significant byte
	 * @return long with the 32 bit unsigned integer
	 */
	public static long bytesToUIntLong(byte[] bytes, int index) {
		long accum = 0;
		int i = 3;
		for (int shiftBy = 0; shiftBy < 32; shiftBy += 8 ) {
			accum |= ( (long)( bytes[index + i] & 0xff ) ) << shiftBy;
			i--;
		}
		return accum;
	}

	/** 
	 * Combines two bytes (most significant bit first) into a 16 bit unsigned integer.
	 * 
	 * @param index of most significant byte
	 * @return int with the 16 bit unsigned integer
	 */
	public static int bytesToUIntInt(byte[] bytes, int index) {
		int accum = 0;
		int i = 1;
		for (int shiftBy = 0; shiftBy < 16; shiftBy += 8 ) {
			accum |= ( (long)( bytes[index + i] & 0xff ) ) << shiftBy;
			i--;
		}
		return accum;
	}

}
