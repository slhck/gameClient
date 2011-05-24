/*
 *  DecodeVideo.cpp
 *  MyCapture
 *
 *  Created by Helmut Hlavacs on 30.11.10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include "DecodeVideoXVID.h"



void DecodeVideoXVID::global_init( int use_assembler )
{
	xvid_gbl_init_t  xvid_gbl_init;
	
	/* Reset the structure with zeros */
	memset(&xvid_gbl_init, 0, sizeof(xvid_gbl_init_t));
	
	/*------------------------------------------------------------------------
	 * XviD core initialization
	 *----------------------------------------------------------------------*/
	
	/* Version */
	xvid_gbl_init.version = XVID_VERSION;
	
	/* Assembly setting */
	if(use_assembler)
#ifdef ARCH_IS_IA64
		xvid_gbl_init.cpu_flags = XVID_CPU_FORCE | XVID_CPU_IA64;
#else
	xvid_gbl_init.cpu_flags = 0;
#endif
	else
		xvid_gbl_init.cpu_flags = XVID_CPU_FORCE;
	
	//xvid_gbl_init.debug = debug_level;
	
	xvid_global(NULL, 0, &xvid_gbl_init, NULL);
}
	
	
int DecodeVideoXVID::dec_init( int width, int height ) {
	int ret;
	
	xvid_dec_create_t xvid_dec_create;

	memset(&xvid_dec_create, 0, sizeof(xvid_dec_create_t));

	/*------------------------------------------------------------------------
	 * XviD encoder initialization
	 *----------------------------------------------------------------------*/
	
	this->width = width;
	this->height = height;
	
	/* Version */
	xvid_dec_create.version = XVID_VERSION;
	
	/*
	 * Image dimensions -- set to 0, xvidcore will resize when ever it is
	 * needed
	 */
	xvid_dec_create.width = width;
	xvid_dec_create.height = height;
	
	ret = xvid_decore(NULL, XVID_DEC_CREATE, &xvid_dec_create, NULL);
	
	dec_handle = xvid_dec_create.handle;
	
	return(ret);
}



int DecodeVideoXVID::dec_main( void *istream, int istream_size, void *outbuffer )
{
	
	int ret = -1;
	
	xvid_dec_frame_t xvid_dec_frame;
	xvid_dec_stats_t xvid_dec_stats;
	
	/* Reset all structures */
	memset(&xvid_dec_frame, 0, sizeof(xvid_dec_frame_t));
	memset(&xvid_dec_stats, 0, sizeof(xvid_dec_stats_t));
	
	/* Set version */
	xvid_dec_frame.version = XVID_VERSION;
	xvid_dec_stats.version = XVID_VERSION;
	
	/* No general flags to set */
	xvid_dec_frame.general          = 0;
	
	/* Input stream */
	xvid_dec_frame.bitstream        = istream;
	xvid_dec_frame.length           = istream_size;
	
	/* Output frame structure */
	xvid_dec_frame.output.plane[0]  = outbuffer;
	xvid_dec_frame.output.stride[0] = width*3;
	xvid_dec_frame.output.csp = XVID_CSP_RGB;
	
	//xvid_dec_frame.output.stride[0] = width*2;
	//xvid_dec_frame.output.csp = XVID_CSP_YVYU;
	ret = xvid_decore(dec_handle, XVID_DEC_DECODE, &xvid_dec_frame, &xvid_dec_stats);
	
	return(ret);
}


/* close decoder to release resources */
int DecodeVideoXVID::dec_stop()
{
	int ret;
	
	ret = xvid_decore(dec_handle, XVID_DEC_DESTROY, NULL, NULL);
	
	return(ret);
}








