# gameClient

This is a client for cloud gaming applications written for the Android platform that is able to display video and send gestures from/to a remote server. It relies on an MPEG-4 Visual video bitstream, encoded by XviD.

## Video Decoding

### Building Native Libraries

The Android Java media framework itself can't handle every type of bitstream properly. Therefore it is necessary to wrap some native code into the project. XviD (supposedly) is very fast and we can wrap its functionality into a native library that will be accessed using Java Native Interface.

`libxvidcore` is bundled into this software. You can find the source files within `jni/libxvidcore`. In order to be built for Android, it has to be compiled against the [NDK](http://developer.android.com/sdk/ndk/index.html). We therefore write an `Android.mk` makefile that compiles all the XviD sources into a static library.

This static library, `libxvidcore.a` is then used by a wrapper class named `DecodeVideoXVID`, which helps us call the XviD methods (like `init`). It will be built as a shared library `libxviddecoder.so`.

Finally, the real implementation is found in `decode.cpp`, which is built into another shared library called `libxvidjnidecoder.so`

### Calling the Native Libraries

The `libxvidjnidecoder` supplies us with native methods that can be accessed within the Android application. The video itself is played on a `Panel`.

## Networking

When the application starts, no packets are exchanged with the server yet. Instead, a SIP `INVITE` message is sent when the user presses the "Start" button. The server shall then begin encoding the video using the same version of XviD, encapsulate the bitstream in RTP and send it over UDP to the client's address.

The client will then decode the bitstream by unpacking the UDP/RTP packets.

When the client is exited, it sends a message to the server to stop encoding.

## Gesture Recognition

Basic gesture recognition is built into the application. During the gaming activity, the phone's sensors are read in short intervals. The pitch, yaw and roll values can be extracted and will be sent to the server as a gesture. The server shall then interpret the gestures.