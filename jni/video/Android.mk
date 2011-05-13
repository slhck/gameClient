LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := ffmpeg
# LOCAL_STATIC_LIBRARIES := bambuser-libavcodec bambuser-libavcore bambuser-libavdevice bambuser-libavfilter bambuser-libavformat bambuser-libavutil bambuser-libswscale
LOCAL_SRC_FILES := at_ac_univie_gameclient_video_VideoGLSurfaceView.c
LOCAL_C_INCLUDES := $(LOCAL_PATH)/../bambuser_ffmpeg/
LOCAL_LDLIBS    := -L$(LOCAL_PATH)/../bambuser_ffmpeg -lavcodec -lavcore -lavdevice -lavfilter -lavformat -lavutil -lswscale -llog -lz -lGLESv1_CM

include $(BUILD_SHARED_LIBRARY)
