#pragma once

extern "C"
{
#include <stdint.h>
#include <stdlib.h>
#include <string.h>
#include "./libxvidcore/src/xvid.h"
}

class DecodeVideoXVID {
public:
	DecodeVideoXVID(){ dec_handle = 0; };
	
	static void global_init( int use_assembler );
	int dec_init( int width, int height );
	int dec_main( void *istream, int istream_size, void *outbuffer );
	int dec_stop();
private:	
	int width, height;
	void *dec_handle;
};


