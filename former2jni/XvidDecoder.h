#pragma once

#include "BMutex.hpp"
#include <utility>
#include "DecodeVideoXVID.h"

class XvidDecoder
{
public:
	XvidDecoder(int width, int height);
	~XvidDecoder(void);
	void copyToBuffer(unsigned char * data, int count);
	int decodeOneFrame();
	unsigned char * getFrame();
private:
	DecodeVideoXVID* decoder;
	BMutex bufferMutex;
	unsigned char *buffer;
	unsigned char *frame;
	int alreadyInBuffer;
};
