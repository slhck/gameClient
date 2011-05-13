#include "at_ac_univie_gameclient_video_VideoGLSurfaceView.h"

#include <string.h>
#include <pthread.h>
#include <android/log.h>
#include <GLES/gl.h>
#include <GLES/glext.h>

#include <stdio.h>
#include <time.h>

#include "libavcodec/avcodec.h"
#include "libavformat/avformat.h"
#include "libswscale/swscale.h"

// #include "ffmpeg/libavcodec/avcodec.h"
// #include "ffmpeg/libavformat/avformat.h"
// #include "ffmpeg/libswscale/swscale.h"


#define FRAME_X 128
#define FRAME_Y 128
#define FPS_TRIGGER 5

static pthread_cond_t s_vsync_cond;
static pthread_mutex_t s_vsync_mutex;

AVFormatContext *pFormatCtx;
AVCodecContext *pCodecCtx;
AVCodec *pCodec;
AVFrame *pFrame;
AVFrame *pFrameRGB;
AVPacket packet;
uint8_t *buffer;
AVFrame *frames[100];
int videoStream;
int frameFinished;
int numBytes;
int i;
int height, width;
float color = 0.0f;
int frameNumber = 0;
GLuint textures[1];
GLuint textureId;

int frame_counter = 0;
time_t last_time;

static const GLfloat vertices[] = {
	-0.5f, 1.0f,
	0.5f, 1.0f,
	-0.5f, -0.5f,
	0.5f, -0.5f
};

static const GLfloat texCoords[] = {
	0.0f, 0.0f,
	1.0f, 0.0f,
	0.0f, 1.0f,
	1.0f, 1.0f
};

static void wait_vsync()
{
       pthread_mutex_lock(&s_vsync_mutex);
       pthread_cond_wait(&s_vsync_cond, &s_vsync_mutex);
       pthread_mutex_unlock(&s_vsync_mutex);
}

JNIEXPORT void JNICALL Java_at_ac_univie_gameclient_video_VideoGLSurfaceView_native_1start
	(JNIEnv * env, jclass clazz)
{
	/* init conditions */
	pthread_cond_init(&s_vsync_cond, NULL);
	pthread_mutex_init(&s_vsync_mutex, NULL);

	while (1) {
		   /* game code goes here */
		    wait_vsync();
	}
}

JNIEXPORT void JNICALL Java_at_ac_univie_gameclient_video_VideoGLSurfaceView_native_1gl_1resize
  (JNIEnv * env, jclass clazz, jint w, jint h)
{
	//height = h;
	//width = w;
}

JNIEXPORT void JNICALL Java_at_ac_univie_gameclient_video_VideoGLSurfaceView_native_1gl_1render
  (JNIEnv * env, jclass clazz)
{
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
	//glOrthof(0.0f, 1.0f, 0.0f, 1.0f, -1.0f, 1.0f);
	glEnableClientState(GL_VERTEX_ARRAY);
	glEnableClientState(GL_TEXTURE_COORD_ARRAY);
	glVertexPointer(2, GL_FLOAT, 0, vertices);
	glTexCoordPointer(2, GL_FLOAT, 0, texCoords);
	glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
	glDisable(GL_DEPTH_TEST);
	
	grabFrame();

	glDisableClientState(GL_VERTEX_ARRAY);
	glDisableClientState(GL_TEXTURE_COORD_ARRAY);

	printFramesPerSecond();

	pthread_cond_signal(&s_vsync_cond);
}

void printFramesPerSecond() {
	frame_counter++;
	if(time(NULL)-last_time >= FPS_TRIGGER) {
		__android_log_print(ANDROID_LOG_DEBUG,"FPS #: ", "%.2f", (float)(frame_counter/(time(NULL)-last_time)));
		frame_counter = 0;
		last_time = time(NULL);
	}
}

void grabFrame() {
	if(av_read_frame(pFormatCtx, &packet) >= 0) {
		// Is this a packet from the video stream?
		if(packet.stream_index == videoStream) {
			// Decode video frame
			//clock_t time1 = clock();
			avcodec_decode_video2(pCodecCtx, pFrame, &frameFinished, &packet);
			//clock_t time2 = clock();
			//__android_log_print(ANDROID_LOG_DEBUG,"avcodec_decode_video2", "%f", ((double)(time2-time1))/1000);

			if(frameFinished) {
				struct SwsContext *pSWSContext = sws_getContext(pCodecCtx->width, pCodecCtx->height,
					pCodecCtx->pix_fmt, FRAME_X, FRAME_Y, PIX_FMT_RGBA, SWS_BILINEAR, 0, 0, 0);

				sws_scale(pSWSContext, (const uint8_t * const*)pFrame->data, pFrame->linesize, 0,
					pCodecCtx->height, pFrameRGB->data, pFrameRGB->linesize);

				glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, FRAME_X, FRAME_Y, 0, GL_RGBA, GL_UNSIGNED_BYTE, pFrameRGB->data[0]);
			}
		}

	  	// Free the packet that was allocated by av_read_frame
		av_free_packet(&packet);	
	}
}

JNIEXPORT void JNICALL Java_at_ac_univie_gameclient_video_VideoGLSurfaceView_native_1init
	(JNIEnv * env, jobject obj, jstring filename)
{
	// TURN ON 2D TEXTURE
	glEnable(GL_TEXTURE_2D);
	glDisable(GL_BLEND);

	// allocate a texture name
	glGenTextures(1, &textureId);
	i=0;

	// BIND THE TEXTURE
	glBindTexture(GL_TEXTURE_2D, textureId);

	// SET TEXTURE PARAMS
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

	// REGISTER ALL THE CODECS AVAILABLE IN FFmpeg FOR USE
	av_register_all();

	jboolean isCopy;
	const char * path = (*env)->GetStringUTFChars(env, filename, &isCopy);

	// LOAD FILE HEADERS
	int open = av_open_input_file(&pFormatCtx, path, NULL, 0, NULL);

	if(open != 0) {
		// IO ERROR
		__android_log_write(ANDROID_LOG_ERROR,"ERROR: ", "could not open file.");
	}

	if(av_find_stream_info(pFormatCtx) < 0) {
		// STREAM INFO ERROR
		__android_log_write(ANDROID_LOG_ERROR,"ERROR: ", "could not find stream info.");
	}

	// FIND THE FIRST VIDEO STREAM
	videoStream = -1;
	for(i=0; i<pFormatCtx->nb_streams; i++) {
		if(pFormatCtx->streams[i]->codec->codec_type == CODEC_TYPE_VIDEO) {
			videoStream = i;
			break;
		}
	}
	if(videoStream == -1) {
		__android_log_write(ANDROID_LOG_ERROR,"ERROR: ", "didn't find a video stream.");
	}

	// POINTER TO CODEC FOR VIDEO STREAM
	pCodecCtx = pFormatCtx->streams[videoStream]->codec;

	// FIND VIDEO STREAM DECODER
	pCodec = avcodec_find_decoder(pCodecCtx->codec_id);
	if(pCodec == NULL) {
		// CODEC NOT FOUND
		__android_log_write(ANDROID_LOG_ERROR,"ERROR: ", "could not find codec.");
	}

	// OPEN CODEC
	if(avcodec_open(pCodecCtx, pCodec) < 0) {
		// OPEN CODEC ERROR
		__android_log_write(ANDROID_LOG_ERROR,"ERROR: ", "could not open codec.");
	}

	// Allocate video frame
	pFrame = avcodec_alloc_frame();

	// Allocate an AVFrame structure
	pFrameRGB = avcodec_alloc_frame();
	if(pFrameRGB == NULL) {
		__android_log_write(ANDROID_LOG_ERROR,"ERROR: ", "null pFrameRGB.");
	}

	// Determine required buffer size and allocate buffer
	numBytes = avpicture_get_size(PIX_FMT_RGBA, FRAME_X, FRAME_Y);
	buffer = (uint8_t *)av_malloc(numBytes*sizeof(uint8_t));

	// Assign appropriate parts of buffer to image planes in pFrameRGB
	// Note that pFrameRGB is an AVFrame, but AVFrame is a superset
	// of AVPicture
	avpicture_fill((AVPicture *)pFrameRGB, buffer, PIX_FMT_RGBA, FRAME_X, FRAME_Y);

	// RELEASE MEMORY ALLOCATED BY JAVA VM
	(*env)->ReleaseStringUTFChars(env, filename, path);
}

JNIEXPORT void JNICALL Java_at_ac_univie_gameclient_video_VideoGLSurfaceView_native_1destroy
  (JNIEnv * env, jobject obj)
{
	// Free the RGB image
	av_free(buffer);
	av_free(pFrameRGB);

	// Free the YUV frame
	av_free(pFrame);

	// Close the codec
	avcodec_close(pCodecCtx);

	// Close the video file
	av_close_input_file(pFormatCtx);
}

