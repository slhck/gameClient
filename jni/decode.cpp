#include "decode.h"

static XvidDecoder * decoder;

void initDecoder(int width, int height)
{
	decoder = new XvidDecoder(width, height);
}

int copyToDecoderBuffer(unsigned char * data, int count)
{
	return decoder->copyToBuffer(data, count);
}

unsigned char * decodeOneFrame()
{
	decoder->decodeOneFrame();
}
