#ifndef DECODER_H
#define DECODER_H

#include "XvidDecoder.h"

extern void initDecoder(int width, int height);
extern void copyToDecoderBuffer(unsigned char * data, int count);
extern int decodeOneFrame();
extern unsigned char * getFrame();

#endif /* DECODER_H */
