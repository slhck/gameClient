#ifndef DECODER_H
#define DECODER_H

#include "XvidDecoder.h"

extern void initDecoder(int width, int height);
extern int copyToDecoderBuffer(unsigned char * data, int count);
extern unsigned char * decodeOneFrame();

#endif /* DECODER_H */
