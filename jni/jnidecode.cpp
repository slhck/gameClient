#include "decode.h"
#include <jni.h>
#include <android/log.h>

extern "C" {

static int gwidth;
static int gheight;

JNIEXPORT void JNICALL
Java_at_ac_univie_gameclient_xvid_Decoder_initDecoder( JNIEnv*  env,
                                      jobject  thiz,
                                      jint     width,
                                      jint     height )
{
	initDecoder(width, height);
	gwidth = width;
	gheight = height;
}

JNIEXPORT jint JNICALL
Java_at_ac_univie_gameclient_xvid_Decoder_copyToDecoderBuffer(JNIEnv*  env,
                                      			jobject  thiz,
							jbyteArray data,
							jsize length)
{
	jbyteArray body = env->NewByteArray(length);
	env->GetByteArrayRegion(data, 0, length, (jbyte*)body);
	jint aib = copyToDecoderBuffer((unsigned char*)body, length);
	__android_log_write(2, "cgClient_native", "copied to decoder buffer");

	return aib;
}

JNIEXPORT jbyteArray JNICALL
Java_at_ac_univie_gameclient_xvid_Decoder_decodeOneFrame(JNIEnv*  env,
                                      			jobject  thiz)
{
	jbyteArray jb;
	jint size = gwidth*gheight*3;
	jb=env->NewByteArray(size);
	env->SetByteArrayRegion(jb, 0, size, (jbyte *)decodeOneFrame());
	__android_log_write(2, "cgClient_native", "retrieved one frame");
	return jb;

	/*if(decCount < 1)
		__android_log_write(2, "cgClient_native", "tried to decode one frame");
	else
		__android_log_write(2, "cgClient_native", "decoded one frame");*/
}

/*JNIEXPORT jbyteArray JNICALL
Java_at_ac_univie_gameclient_xvid_Decoder_getFrame(JNIEnv*  env,
                                      			jobject  thiz)
{
	jbyteArray jb;
	jint size = gwidth*gheight*3;
	jb=env->NewByteArray(size);
	env->SetByteArrayRegion(jb, 0, size, (jbyte *)getFrame());
	__android_log_write(2, "cgClient_native", "retrieved one frame");
	return jb;
}*/

}
