#pragma once

#include "BMutex.hpp"
#include <utility>
#include "DecodeVideoXVID.h"

class XvidDecoder
{
public:
	XvidDecoder(int width, int height);
	~XvidDecoder(void);
	int copyToBuffer(unsigned char * data, int count);
	unsigned char * decodeOneFrame();
private:
	DecodeVideoXVID* decoder;
	BMutex bufferMutex;
	unsigned char *buffer;
	unsigned char *frame;
	int alreadyInBuffer;
};
