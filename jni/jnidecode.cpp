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
	//__android_log_write(2, "cgClient_native", "jnidecode.cpp initDecoder begin");
	initDecoder(width, height);
	gwidth = width;
	gheight = height;
	//__android_log_write(2, "cgClient_native", "jnidecode.cpp initDecoder end");
}

JNIEXPORT jint JNICALL
Java_at_ac_univie_gameclient_xvid_Decoder_copyToDecoderBuffer(JNIEnv*  env,
                                      			jobject  thiz,
							jbyteArray data,
							jsize length)
{
	//__android_log_write(2, "cgClient_native", "jnidecode.cpp copyToDecoderBuffer begin");
	jbyteArray body = env->NewByteArray(length);
	env->GetByteArrayRegion(data, 0, length, (jbyte*)body);
	jint aib = copyToDecoderBuffer((unsigned char*)body, length);
	//__android_log_write(2, "cgClient_native", "jnidecode.cpp copyToDecoderBuffer end");

	return aib;
}

JNIEXPORT jbyteArray JNICALL
Java_at_ac_univie_gameclient_xvid_Decoder_decodeOneFrame(JNIEnv*  env,
                                      			jobject  thiz)
{
	//__android_log_write(2, "cgClient_native", "jnidecode.cpp decodeOneFrame begin");
	jbyteArray jb;
	jint size = gwidth*gheight*3;
	jb=env->NewByteArray(size);
	env->SetByteArrayRegion(jb, 0, size, (jbyte *)decodeOneFrame());
	//__android_log_write(2, "cgClient_native", "jnidecode.cpp decodeOneFrame end");

	return jb;
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
