//
// Created by cniek on 25/07/2020.
//

#include <jni.h>
#include <cstring>
#include <android/log.h>
#include <dlfcn.h>
#include <sys/mman.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/system_properties.h>
//#include <filesystem>
#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include <sys/stat.h>
#include <zlib.h>
#include <linux/fcntl.h>
#include <pthread.h>
#include <stdio.h>
#include <malloc.h>
#include <vector>
#include <string>
#include <stdlib.h>

#define LOG_TAG "JNI contour.cpp"
#define ALOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" {

    jbyteArray e(JNIEnv *env, jclass, jbyteArray plaintext)
    {
        jbyte a[] = {1,2,3,4,5,6};
        jbyteArray ret = env->NewByteArray(6);
        env->SetByteArrayRegion (ret, 0, 6, a);
        return ret;
    }

    static JNINativeMethod methods[] = {
            {"e", "([B)[B", (void *)e}
    };

    jint JNICALL JNI_OnLoad(JavaVM *vm, void *)
    {
        JNIEnv *env;
        if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_4) != JNI_OK)
            return JNI_FALSE;

        jclass clazz = env->FindClass("com/cniekirk/traintimes/utils/Sign");

        if (clazz == 0)
        {
            ALOGE("ERROR: Cannot FindClass");
            return -1;
        }

        if (env->RegisterNatives(clazz, methods, sizeof(methods) / sizeof(methods[0])) < 0)
        {
            ALOGE("DEBUG: methods: %d", sizeof(methods));
            ALOGE("DEBUG: methods[0]: %d", sizeof(methods[0]));
            ALOGE("ERROR: Register natives not working!");
        }

        return JNI_VERSION_1_4;
    }

}
