#include "XvidDecoder.h"
#include <android/log.h>

XvidDecoder::XvidDecoder(int width, int height)
{
	alreadyInBuffer = 0;

	decoder = new DecodeVideoXVID();
	decoder->global_init(true);
	decoder->dec_init(width, height);

	frame = new unsigned char[width*height*3];
	buffer = (unsigned char *) malloc(1024*1024*2);
}


XvidDecoder::~XvidDecoder(void)
{
	delete decoder;
	delete[] buffer;
	delete[] frame;
}

int XvidDecoder::copyToBuffer(unsigned char * data, int count)
{
	//__android_log_write(2, "cgClient_native", "XvidDecoder.cpp copyToBuffer begin");
	//bufferMutex.tryLock();
	memcpy(buffer+alreadyInBuffer, data, count);
	alreadyInBuffer +=count;
	int aib = alreadyInBuffer;
	//bufferMutex.unlock();
	//__android_log_write(2, "cgClient_native", "XvidDecoder.cpp copyToBuffer end");

	return aib;
}

unsigned char * XvidDecoder::decodeOneFrame()
{
	//__android_log_write(2, "cgClient_native", "XvidDecoder.cpp decodeOneFrame begin");
	//bufferMutex.tryLock();
	int decCount = 0;
	do
	{	
		decCount = decoder->dec_main(buffer, alreadyInBuffer, frame);
		alreadyInBuffer -= decCount;
		memcpy(buffer, buffer+decCount, alreadyInBuffer);
	}
	while(decCount > 0 && alreadyInBuffer > 1);
	//bufferMutex.unlock();
	//__android_log_write(2, "cgClient_native", "XvidDecoder.cpp decodeOneFrame end");

	return frame;
}
