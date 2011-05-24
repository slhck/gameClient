LOCAL_PATH:= $(call my-dir)

############
#
#   libxvidcore.a

include $(CLEAR_VARS)

LOCAL_MODULE    := libxvidcore
#LOCAL_CFLAGS = -DFIXED_POINT -DEXPORT=”" -UHAVE_CONFIG_H -I$(LOCAL_PATH)/include
LOCAL_CFLAGS := -DARCH_IS_32BIT -DARCH_IS_GENERIC -I$(LOCAL_PATH)/libxvidcore/src -lc

LOCAL_SRC_FILES :=  \
./libxvidcore/src/decoder.c \
./libxvidcore/src/encoder.c \
./libxvidcore/src/xvid.c \
./libxvidcore/src/bitstream/bitstream.c \
./libxvidcore/src/bitstream/cbp.c \
./libxvidcore/src/bitstream/mbcoding.c \
./libxvidcore/src/dct/fdct.c \
./libxvidcore/src/dct/idct.c \
./libxvidcore/src/dct/simple_idct.c \
./libxvidcore/src/image/colorspace.c \
./libxvidcore/src/image/image.c \
./libxvidcore/src/image/interpolate8x8.c \
./libxvidcore/src/image/font.c \
./libxvidcore/src/image/postprocessing.c \
./libxvidcore/src/image/qpel.c \
./libxvidcore/src/image/reduced.c \
./libxvidcore/src/motion/estimation_bvop.c \
./libxvidcore/src/motion/estimation_common.c \
./libxvidcore/src/motion/estimation_gmc.c \
./libxvidcore/src/motion/estimation_pvop.c \
./libxvidcore/src/motion/estimation_rd_based.c \
./libxvidcore/src/motion/estimation_rd_based_bvop.c \
./libxvidcore/src/motion/gmc.c \
./libxvidcore/src/motion/motion_comp.c \
./libxvidcore/src/motion/vop_type_decision.c \
./libxvidcore/src/motion/sad.c \
./libxvidcore/src/prediction/mbprediction.c \
./libxvidcore/src/plugins/plugin_single.c \
./libxvidcore/src/plugins/plugin_2pass1.c \
./libxvidcore/src/plugins/plugin_2pass2.c \
./libxvidcore/src/plugins/plugin_lumimasking.c \
./libxvidcore/src/plugins/plugin_dump.c \
./libxvidcore/src/plugins/plugin_psnr.c \
./libxvidcore/src/plugins/plugin_ssim.c \
./libxvidcore/src/quant/quant_h263.c \
./libxvidcore/src/quant/quant_matrix.c \
./libxvidcore/src/quant/quant_mpeg.c \
./libxvidcore/src/utils/emms.c \
./libxvidcore/src/utils/mbtransquant.c \
./libxvidcore/src/utils/mem_align.c \
./libxvidcore/src/utils/mem_transfer.c \
./libxvidcore/src/utils/timer.c

include $(BUILD_STATIC_LIBRARY)

############
#
#   libxviddecoder.a

include $(CLEAR_VARS)

LOCAL_MODULE    := libxviddecoder
LOCAL_CFLAGS := -lc
LOCAL_C_INCLUDES := $(LOCAL_PATH)/libxvidcore/src
LOCAL_STATIC_LIBRARIES := libxvidcore
LOCAL_SRC_FILES := \
DecodeVideoXVID.cpp \
XvidDecoder.cpp \
decode.cpp

include $(BUILD_SHARED_LIBRARY)

############
#
#   libxvidjnidecoder.so

include $(CLEAR_VARS)

LOCAL_MODULE    := libxvidjnidecoder
LOCAL_CFLAGS := -lc
LOCAL_SRC_FILES := jnidecode.cpp


LOCAL_STATIC_LIBRARIES := libxvidcore \
libxviddecoder

LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)
