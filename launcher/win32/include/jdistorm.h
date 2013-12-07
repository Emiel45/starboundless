#include <jni.h>

#ifndef JDISTORM_H
#define JDISTORM_H

void JNICALL Java_distorm_Distorm_decompose(JNIEnv *env, jclass clazz, jobject info, jobject result);

void JNICALL Java_distorm_Distorm_decode(JNIEnv *env, jclass clazz, jobject info, jobject result);

jobject JNICALL Java_distorm_Distorm_format(JNIEnv *env, jclass clazz, jobject info, jobject instruction);

void jdistorm_init(JNIEnv *env);

#endif