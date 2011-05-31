#include "decode.h"

static XvidDecoder * decoder;

void initDecoder(int width, int height)
{
	decoder = new XvidDecoder(width, height);
}

void copyToDecoderBuffer(unsigned char * data, int count)
{
	decoder->copyToBuffer(data, count);
}

int decodeOneFrame()
{
	return decoder->decodeOneFrame();
}

unsigned char * getFrame()
{
	return decoder->getFrame();
}
