#include "decode.h"
#include <jni.h>

//extern "C" {

JNIEXPORT void JNICALL
Java_at_ac_univie_gameclient_Start_init( JNIEnv*  env,
                                      jobject  thiz,
                                      jint     width,
                                      jint     height )
{
	initDecoder(width, height);
}

JNIEXPORT void JNICALL
Java_at_ac_univie_gameclient_Start_copyToDecoderBuffer(JNIEnv*  env,
                                      			jobject  thiz,
							jbyteArray data)
{
	jsize length = env->GetArrayLength(data);
	jbyte *body = env->GetByteArrayElements(data, 0);
	copyToDecoderBuffer((unsigned char*)body, length);
	env->ReleaseByteArrayElements(data, body, 0);
}

JNIEXPORT jbyteArray JNICALL
Java_at_ac_univie_gameclient_Start_decodeOneFrame(JNIEnv*  env,
                                      			jobject  thiz)
{
	jbyteArray jb;
	jint size = decodeOneFrame();
	unsigned char * frame = getFrame();
	jb=env->NewByteArray(size);
	env->SetByteArrayRegion(jb, 0, size, (jbyte *)frame);
	return jb;
}

//}
