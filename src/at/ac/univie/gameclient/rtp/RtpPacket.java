package at.ac.univie.gameclient.rtp;

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
	public int TimeStamp;
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
		PayloadType = header[1] & 127;
		SequenceNumber = (int)(header[3]) + 256*(int)(header[2]);
		TimeStamp = (int)(header[7]) + 256*(int)(header[6]) + 65536*(int)(header[5]) + 16777216*(int)(header[4]);
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
	
	public int gettimestamp() {
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

}
