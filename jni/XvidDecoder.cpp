#include "XvidDecoder.h"


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
	bufferMutex.tryLock();
	memcpy(buffer+alreadyInBuffer, data, count);
	alreadyInBuffer +=count;
	int aib = alreadyInBuffer;
	bufferMutex.unlock();

	return aib;
}

unsigned char * XvidDecoder::decodeOneFrame()
{
	bufferMutex.tryLock();
	int decCount = 0;
	do
	{	
		decCount = decoder->dec_main(buffer, alreadyInBuffer, frame);
		alreadyInBuffer -= decCount;
		memcpy(buffer, buffer+decCount, alreadyInBuffer);
	}
	while(decCount > 0 && alreadyInBuffer > 1);
	bufferMutex.unlock();

	return frame;
}
