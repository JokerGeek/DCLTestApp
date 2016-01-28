/*
 * Copyright 2009 Cedric Priscal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
#include <stdlib.h>
#include <stdio.h>
#include <jni.h>
#include <assert.h>

#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <jni.h>

#include "android/log.h"

const char *TAG = "serial_port";
const char* kClassName = "jjwork/modbus/SerialPort"; //指定要注册的类

static int max485_ctrl_fd = 0;

#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

struct baud_map{
	int baud;
	speed_t speed;
};

//{.baud = 4800, .speed = B4800},
//{.baud = 9600, .speed = B9600},
//{.baud = 19200, .speed = B19200},
//{.baud = 38400, .speed = B38400},
//{.baud = 57600, .speed = B57600},
//{.baud = 115200, .speed = B115200},
//{.baud = 230400, .speed = B230400},
//{.baud = 460800, .speed = B460800},
//{.baud = 500000, .speed = B500000},
//{.baud = 576000, .speed = B576000},
//{.baud = 921600, .speed = B921600},
//{.baud = 1000000, .speed = B1000000},
//{.baud = 1152000, .speed = B1152000},
struct baud_map baud_maps[] = {
		{ 4800, B4800},
		{ 9600, B9600},
		{ 19200, B19200},
		{ 38400, B38400},
		{ 57600, B57600},
		{ 115200, B115200},
		{ 230400, B230400},
		{ 460800, B460800},
		{ 500000, B500000},
		{ 576000, B576000},
		{ 921600, B921600},
		{ 1000000, B1000000},
		{ 1152000, B1152000},
};


static speed_t getBaudrate(jint baudrate) {
	int len = sizeof(baud_maps)/sizeof(baud_maps[0]);
	for(int i = 0; i < len; i++){
		if(baudrate == baud_maps[i].baud)
			return baud_maps[i].speed;
	}
	return -1;
}

/*
 * Class:     cedric_serial_SerialPort
 * Method:    open
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT jobject JNICALL native_open(JNIEnv *env, jobject thiz, jstring path,jint baudrate) {
	int fd;
	speed_t speed;
	jobject mFileDescriptor;


	LOGD("init native Check arguments");
	/* Check arguments */
	{
		speed = getBaudrate(baudrate);
		if (speed == -1) {
			/* TODO: throw an exception */
			LOGE("Invalid baudrate");
			return NULL;
		}
	}

	LOGD("init native Opening device!");
	/* Opening device */
	{
		jboolean iscopy;
		const char *path_utf = env->GetStringUTFChars(path, &iscopy);
		LOGD("Opening serial port %s", path_utf);
	    fd = open(path_utf, O_RDWR | O_NOCTTY | O_NONBLOCK | O_NDELAY);
		LOGD("open() fd = %d", fd);
		env->ReleaseStringUTFChars(path, path_utf);
		if (fd == -1) {
			/* Throw an exception */
			LOGE("Cannot open port %d",baudrate);
			/* TODO: throw an exception */
			return NULL;
		}
	}

	LOGD("init native Configure device!");
	/* Configure device */
	{
		struct termios cfg;
		if (tcgetattr(fd, &cfg)) {
			LOGE("Configure device tcgetattr() failed 1");
			close(fd);
			return NULL;
		}

		cfmakeraw(&cfg);
		cfsetispeed(&cfg, speed);
		cfsetospeed(&cfg, speed);

		if (tcsetattr(fd, TCSANOW, &cfg)) {
			LOGE("Configure device tcsetattr() failed 2");
			close(fd);
			/* TODO: throw an exception */
			return NULL;
		}
	}


	/* Create a corresponding file descriptor */
	{
		jclass cFileDescriptor = env->FindClass("java/io/FileDescriptor");
		jmethodID iFileDescriptor = env->GetMethodID(cFileDescriptor,"<init>", "()V");
		jfieldID descriptorID = env->GetFieldID(cFileDescriptor,"descriptor", "I");
		mFileDescriptor = env->NewObject(cFileDescriptor,iFileDescriptor);
		env->SetIntField(mFileDescriptor, descriptorID, (jint) fd);
	}

	if(max485_ctrl_fd <= 0)
		max485_ctrl_fd = open("/dev/max485_ctl_pin", O_RDWR|O_NDELAY|O_NOCTTY);
	if(max485_ctrl_fd <= 0)
		LOGD("can't open /dev/max485_ctl_pin");
	else{
		LOGD("open /dev/max485_ctl_pin sucess. fd = %d", max485_ctrl_fd);
		ioctl(max485_ctrl_fd, 0, 0);
	}

	return mFileDescriptor;
}

/*
 * Class:     cedric_serial_SerialPort
 * Method:    close
 * Signature: ()V
 */
JNIEXPORT jint JNICALL native_close(JNIEnv * env, jobject thiz)
{
	jclass SerialPortClass = env->GetObjectClass(thiz);
	jclass FileDescriptorClass = env->FindClass("java/io/FileDescriptor");

	jfieldID mFdID = env->GetFieldID(SerialPortClass, "mFd", "Ljava/io/FileDescriptor;");
	jfieldID descriptorID = env->GetFieldID(FileDescriptorClass, "descriptor", "I");

	jobject mFd = env->GetObjectField(thiz, mFdID);
	jint descriptor = env->GetIntField(mFd, descriptorID);

	LOGD("close(fd = %d)", descriptor);
	close(descriptor);

	if(max485_ctrl_fd) {
		close(max485_ctrl_fd);
		max485_ctrl_fd = 0;
	}

	return 1;
}
JNIEXPORT jint JNICALL max485_set_send(JNIEnv * env, jobject thiz){
	if(max485_ctrl_fd)
		ioctl(max485_ctrl_fd, 1, 0);
	return 1;
}

JNIEXPORT jint JNICALL max485_set_recv(JNIEnv * env, jobject thiz){
	if(max485_ctrl_fd)
		ioctl(max485_ctrl_fd, 0, 0);
	return 1;
}
static JNINativeMethod gMethods[] = {
		{ "open", "(Ljava/lang/String;I)Ljava/io/FileDescriptor;",(void*) native_open },
		{ "close", "()I",(void*) native_close },
		{ "setSend", "()I",(void*) max485_set_send },
		{ "setRecv", "()I",(void*) max485_set_recv },
};

/*
 * 为某一个类注册本地方法
 */
static int registerNativeMethods(JNIEnv* env, const char* className,
		JNINativeMethod* gMethods, int numMethods) {
	jclass clazz;
	clazz = env->FindClass(className);
	if (clazz == NULL) {
		return JNI_FALSE;
	}
	if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
		return JNI_FALSE;
	}

	return JNI_TRUE;
}

/*
 * 为所有类注册本地方法
 */
static int registerNatives(JNIEnv* env) {
	return registerNativeMethods(env, kClassName, gMethods,
			sizeof(gMethods) / sizeof(gMethods[0]));
}

/*
 * System.loadLibrary("lib")时调用
 * 如果成功返回JNI版本, 失败返回-1
 */
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
	JNIEnv* env = NULL;
	jint result = -1;

	if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
		return -1;
	}
	assert(env != NULL);

	if (!registerNatives(env)) { //注册
		return -1;
	}
	//成功
	result = JNI_VERSION_1_4;

	return result;
}

