/*
 * diStorm3 JNI wrapper.
 * Gil Dabah, October 2010.
 * Modified by Emiel Tasseel, December 2013.
 */

#include <jdistorm.h>
#include <distorm.h>

#include <string.h>
#include <stdio.h>
#include <stdlib.h>

static struct _CodeInfoIds {
	jclass jCls;
	jfieldID ID_CodeOffset;
	jfieldID ID_Code;
	jfieldID ID_DecodeType;
	jfieldID ID_Features;
} g_CodeInfoIds;

static struct _DecodedResultIds {
	jclass jCls;
	jfieldID ID_Instructions;
	jfieldID ID_MaxInstructions;
} g_DecodedResultIds;

static struct _DecodedInstIds {
	jclass jCls;
	jfieldID ID_Mnemonic;
	jfieldID ID_Operands;
	jfieldID ID_Hex;
	jfieldID ID_Size;
	jfieldID ID_Offset;
} g_DecodedInstIds;

static struct _DecomposedResultIds {
	jclass jCls;
	jfieldID ID_Instructions;
	jfieldID ID_MaxInstructions;
} g_DecomposedResultIds;

static struct _DecomposedInstIds {
	jclass jCls;
	jfieldID ID_Address;
	jfieldID ID_Size;
	jfieldID ID_Flags;
	jfieldID ID_Segment;
	jfieldID ID_Base;
	jfieldID ID_Scale;
	jfieldID ID_Opcode;
	jfieldID ID_Operands;
	jfieldID ID_Disp;
	jfieldID ID_Imm;
	jfieldID ID_UnusedPrefixesMask;
	jfieldID ID_Meta;
	jfieldID ID_RegistersMask;
	jfieldID ID_ModifiedFlagsMask;
	jfieldID ID_TestedFlagsMask;
	jfieldID ID_UndefinedFlagsMask;
} g_DecomposedInstIds;

static struct _OperandIds {
	jclass jCls;
	jfieldID ID_Type;
	jfieldID ID_Index;
	jfieldID ID_Size;
} g_OperandIds;

static struct _ImmIds {
	jclass jCls;
	jfieldID ID_Value;
	jfieldID ID_Size;
} g_ImmIds;

static struct _DispIds {
	jclass jCls;
	jfieldID ID_Displacement;
	jfieldID ID_Size;
} g_DispIds;

void throwByName(JNIEnv* env, const char *name, const char* msg)
{
    jclass cls = (*env)->FindClass(env, name);
    if (cls != NULL) {
        (*env)->ThrowNew(env, cls, msg);
    }
    (*env)->DeleteLocalRef(env, cls);
}

_CodeInfo* acquireCodeInfoStruct(JNIEnv *env, jobject info)
{
	jobject jCodeObj = NULL;
	_CodeInfo* ci = (_CodeInfo*)malloc(sizeof(_CodeInfo));
	if (ci == NULL) {
		throwByName(env, "java/lang/OutOfMemoryError", NULL);
		return NULL;
	}
	memset(ci, 0, sizeof(_CodeInfo));

	ci->codeOffset = (*env)->GetLongField(env, info, g_CodeInfoIds.ID_CodeOffset);

	jCodeObj = (*env)->GetObjectField(env, info, g_CodeInfoIds.ID_Code);
	ci->code = (uint8_t*) (*env)->GetDirectBufferAddress(env, jCodeObj);
	ci->codeLen = (int)(*env)->GetDirectBufferCapacity(env, jCodeObj);

	ci->dt = (*env)->GetIntField(env, info, g_CodeInfoIds.ID_DecodeType);

	ci->features = (*env)->GetIntField(env, info, g_CodeInfoIds.ID_Features);

	return ci;
}

jobject createDecodedInstObj(JNIEnv* env, const _DecodedInst* inst) {
	jobject jInst = (*env)->AllocObject(env, g_DecodedInstIds.jCls);
	if (jInst == NULL) return NULL;
	(*env)->SetObjectField(env, jInst, g_DecodedInstIds.ID_Mnemonic, (*env)->NewStringUTF(env, (const char*)inst->mnemonic.p));
	(*env)->SetObjectField(env, jInst, g_DecodedInstIds.ID_Operands, (*env)->NewStringUTF(env, (const char*)inst->operands.p));
	(*env)->SetObjectField(env, jInst, g_DecodedInstIds.ID_Hex, (*env)->NewStringUTF(env, (const char*)inst->instructionHex.p));
	(*env)->SetIntField(env, jInst, g_DecodedInstIds.ID_Size, inst->size);
	(*env)->SetLongField(env, jInst, g_DecodedInstIds.ID_Offset, inst->offset);
	return jInst;
}

void JNICALL Java_distorm_Distorm_decode(JNIEnv *env, jclass clazz, jobject info, jobject result) {
	jarray jInsts = NULL;
	jobject jInst = NULL;
	_CodeInfo* ci = NULL;
	_DecodedInst* insts = NULL;
	jint maxInstructions = 0;
	unsigned int usedInstructionsCount = 0, i = 0;

	ci = acquireCodeInfoStruct(env, info);
	if (ci == NULL) {
		throwByName(env, "java/lang/OutOfMemoryError", NULL);
		return;
	}

	maxInstructions = (*env)->GetIntField(env, result, g_DecodedResultIds.ID_MaxInstructions);

	insts = (_DecodedInst*)malloc(maxInstructions * sizeof(_DecodedInst));
	if (insts == NULL) goto Cleanup;

	distorm_decode(ci->codeOffset, ci->code, ci->codeLen, ci->dt, insts, maxInstructions, &usedInstructionsCount);

	jInsts = (*env)->NewObjectArray(env, usedInstructionsCount, g_DecodedInstIds.jCls, NULL);
	if (jInsts == NULL) goto Cleanup;

	for (i = 0; i < usedInstructionsCount; i++) {
		jInst = createDecodedInstObj(env, &insts[i]);
		if (jInst == NULL) goto Cleanup;
		(*env)->SetObjectArrayElement(env, jInsts, i, jInst);
	}

	(*env)->SetObjectField(env, result, g_DecodedResultIds.ID_Instructions, jInsts);

Cleanup:
	/* In case of an error, jInsts will get cleaned automatically. */
	if (ci != NULL) free(ci);
	if (insts != NULL) free(insts);
}

#include <windows.h>

void JNICALL Java_distorm_Distorm_decompose(JNIEnv *env, jclass clazz, jobject info, jobject result) {
	jarray jInsts = NULL, jOperands = NULL;
	jobject jInst = NULL, jOperand = NULL, jImm = NULL, jDisp = NULL;
	_CodeInfo* ci = NULL;
	_DInst* insts = NULL;
	jint maxInstructions = 0;
	unsigned int usedInstructionsCount = 0, i = 0, j = 0, operandsNo = 0;
	int success = 0;

	ci = acquireCodeInfoStruct(env, info);
	if (ci == NULL) {
		throwByName(env, "java/lang/OutOfMemoryError", NULL);
		return;
	}

	maxInstructions = (*env)->GetIntField(env, result, g_DecomposedResultIds.ID_MaxInstructions);

	insts = (_DInst*)malloc(maxInstructions * sizeof(_DInst));
	if (insts == NULL) goto Cleanup;

	distorm_decompose(ci, insts, maxInstructions, &usedInstructionsCount);

	jInsts = (*env)->NewObjectArray(env, usedInstructionsCount, g_DecomposedInstIds.jCls, NULL);
	if (jInsts == NULL) goto Cleanup;

	for (i = 0; i < usedInstructionsCount; i++) {
		jInst = (*env)->AllocObject(env, g_DecomposedInstIds.jCls);
		if (jInst == NULL) goto Cleanup;

		/* Simple fields: */
		(*env)->SetLongField(env, jInst, g_DecomposedInstIds.ID_Address, insts[i].addr);
		(*env)->SetIntField(env, jInst, g_DecomposedInstIds.ID_Flags, insts[i].flags);
		(*env)->SetIntField(env, jInst, g_DecomposedInstIds.ID_Size, insts[i].size);
		(*env)->SetIntField(env, jInst, g_DecomposedInstIds.ID_Segment, insts[i].segment);
		(*env)->SetIntField(env, jInst, g_DecomposedInstIds.ID_Base, insts[i].base);
		(*env)->SetIntField(env, jInst, g_DecomposedInstIds.ID_Scale, insts[i].scale);
		(*env)->SetIntField(env, jInst, g_DecomposedInstIds.ID_Opcode, insts[i].opcode);
		(*env)->SetIntField(env, jInst, g_DecomposedInstIds.ID_UnusedPrefixesMask, insts[i].unusedPrefixesMask);
		(*env)->SetIntField(env, jInst, g_DecomposedInstIds.ID_Meta, insts[i].meta);
		(*env)->SetIntField(env, jInst, g_DecomposedInstIds.ID_RegistersMask, insts[i].usedRegistersMask);
		(*env)->SetIntField(env, jInst, g_DecomposedInstIds.ID_ModifiedFlagsMask, insts[i].modifiedFlagsMask);
		(*env)->SetIntField(env, jInst, g_DecomposedInstIds.ID_TestedFlagsMask, insts[i].testedFlagsMask);
		(*env)->SetIntField(env, jInst, g_DecomposedInstIds.ID_UndefinedFlagsMask, insts[i].undefinedFlagsMask);

		/* Immediate variant. */
		jImm = (*env)->AllocObject(env, g_ImmIds.jCls);
		if (jImm == NULL) goto Cleanup;
		(*env)->SetLongField(env, jImm, g_ImmIds.ID_Value, insts[i].imm.qword);
		/* The size of the immediate is in one of the operands, if at all. Look for it below. Zero by default. */
		(*env)->SetIntField(env, jImm, g_ImmIds.ID_Size, 0);

		/* Count operands. */
		for (operandsNo = 0; operandsNo < OPERANDS_NO; operandsNo++) {
			if (insts[i].ops[operandsNo].type == O_NONE) break;
		}

		jOperands = (*env)->NewObjectArray(env, operandsNo, g_OperandIds.jCls, NULL);
		if (jOperands == NULL) goto Cleanup;

		for (j = 0; j < operandsNo; j++) {
			if (insts[i].ops[j].type == O_IMM) {
				/* Set the size of the immediate operand. */
				(*env)->SetIntField(env, jImm, g_ImmIds.ID_Size, insts[i].ops[j].size);
			}

			jOperand = (*env)->AllocObject(env, g_OperandIds.jCls);
			if (jOperand == NULL) goto Cleanup;
			(*env)->SetIntField(env, jOperand, g_OperandIds.ID_Type, insts[i].ops[j].type);
			(*env)->SetIntField(env, jOperand, g_OperandIds.ID_Index, insts[i].ops[j].index);
			(*env)->SetIntField(env, jOperand, g_OperandIds.ID_Size, insts[i].ops[j].size);
			(*env)->SetObjectArrayElement(env, jOperands, j, jOperand);
		}
		(*env)->SetObjectField(env, jInst, g_DecomposedInstIds.ID_Operands, jOperands);

		/* Attach the immediate variant. */
		(*env)->SetObjectField(env, jInst, g_DecomposedInstIds.ID_Imm, jImm);

		/* Displacement variant. */
		jDisp = (*env)->AllocObject(env, g_DispIds.jCls);
		if (jDisp == NULL) goto Cleanup;
		(*env)->SetLongField(env, jDisp, g_DispIds.ID_Displacement, insts[i].disp);
		(*env)->SetIntField(env, jDisp, g_DispIds.ID_Size, insts[i].dispSize);
		(*env)->SetObjectField(env, jInst, g_DecomposedInstIds.ID_Disp, jDisp);

		(*env)->SetObjectArrayElement(env, jInsts, i, jInst);
	}

	(*env)->SetObjectField(env, result, g_DecodedResultIds.ID_Instructions, jInsts);

Cleanup:
	/* In case of an error, jInsts will get cleaned automatically. */
	if (ci != NULL) free(ci);
	if (insts != NULL) free(insts);
}

jobject JNICALL Java_distorm_Distorm_format(JNIEnv *env, jclass clazz, jobject info, jobject instruction) {
	_CodeInfo* ci = NULL;
	_DInst input = {0};
	_DecodedInst output = {0};
	jobject ret = NULL, jOperands = NULL, jOp = NULL, jTmp = NULL;
	jsize i, opsCount;

	ci = acquireCodeInfoStruct(env, info);
	if (ci == NULL) {
		throwByName(env, "java/lang/OutOfMemoryError", NULL);
		return NULL;
	}

	input.addr = (*env)->GetLongField(env, instruction, g_DecomposedInstIds.ID_Address);
	input.flags = (uint16_t) (*env)->GetIntField(env, instruction, g_DecomposedInstIds.ID_Flags);
	input.size = (uint8_t) (*env)->GetIntField(env, instruction, g_DecomposedInstIds.ID_Size);
	input.segment = (uint8_t) (*env)->GetIntField(env, instruction, g_DecomposedInstIds.ID_Segment);
	input.base = (uint8_t) (*env)->GetIntField(env, instruction, g_DecomposedInstIds.ID_Base);
	input.scale = (uint8_t) (*env)->GetIntField(env, instruction, g_DecomposedInstIds.ID_Scale);
	input.opcode = (uint16_t) (*env)->GetIntField(env, instruction, g_DecomposedInstIds.ID_Opcode);
	/* unusedPrefixesMask is unused indeed, lol. */
	input.meta = (uint8_t) (*env)->GetIntField(env, instruction, g_DecomposedInstIds.ID_Meta);
	/* Nor usedRegistersMask. */

	jOperands = (*env)->GetObjectField(env, instruction, g_DecomposedInstIds.ID_Operands);
	if (jOperands != NULL) {
		opsCount = (*env)->GetArrayLength(env, jOperands);
		for (i = 0; i < opsCount; i++) {
			jOp = (*env)->GetObjectArrayElement(env, jOperands, i);
			if (jOp != NULL) {
				input.ops[i].index = (uint8_t) (*env)->GetIntField(env, jOp, g_OperandIds.ID_Index);
				input.ops[i].type = (uint8_t) (*env)->GetIntField(env, jOp, g_OperandIds.ID_Type);
				input.ops[i].size = (uint16_t) (*env)->GetIntField(env, jOp, g_OperandIds.ID_Size);
			}
		}
	}

	jTmp = (*env)->GetObjectField(env, instruction, g_DecomposedInstIds.ID_Imm);
	if (jTmp != NULL) {
		input.imm.qword = (uint64_t) (*env)->GetLongField(env, jTmp, g_ImmIds.ID_Value);
	}

	jTmp = (*env)->GetObjectField(env, instruction, g_DecomposedInstIds.ID_Disp);
	if (jTmp != NULL) {
		input.disp = (uint64_t) (*env)->GetLongField(env, jTmp, g_DispIds.ID_Displacement);
		input.dispSize = (uint8_t) (*env)->GetIntField(env, jTmp, g_DispIds.ID_Size);
	}

	distorm_format(ci, &input, &output);

	ret = createDecodedInstObj(env, &output);

	if (ci != NULL) free(ci);
	return ret;
}

void jdistorm_init(JNIEnv *env) {
	jclass jCls = NULL;

	jCls = (*env)->FindClass(env, "distorm/CodeInfo");
	g_CodeInfoIds.jCls = (*env)->NewWeakGlobalRef(env, jCls);
	g_CodeInfoIds.ID_CodeOffset = (*env)->GetFieldID(env, jCls, "codeOffset", "J");
	g_CodeInfoIds.ID_Code = (*env)->GetFieldID(env, jCls, "code", "Ljava/nio/ByteBuffer;");
	g_CodeInfoIds.ID_DecodeType = (*env)->GetFieldID(env, jCls, "decodeType", "I");
	g_CodeInfoIds.ID_Features = (*env)->GetFieldID(env, jCls, "features", "I");

	jCls = (*env)->FindClass(env, "distorm/DecodedResult");
	g_DecodedResultIds.jCls = (*env)->NewWeakGlobalRef(env, jCls);
	g_DecodedResultIds.ID_MaxInstructions = (*env)->GetFieldID(env, jCls, "maxInstructions", "I");
	g_DecodedResultIds.ID_Instructions = (*env)->GetFieldID(env, jCls, "instructions", "[Ldistorm/DecodedInst;");

	jCls = (*env)->FindClass(env, "distorm/DecodedInst");
	g_DecodedInstIds.jCls = (*env)->NewWeakGlobalRef(env, jCls);
	g_DecodedInstIds.ID_Mnemonic = (*env)->GetFieldID(env, jCls, "mnemonic", "Ljava/lang/String;");
	g_DecodedInstIds.ID_Operands = (*env)->GetFieldID(env, jCls, "operands", "Ljava/lang/String;");
	g_DecodedInstIds.ID_Hex = (*env)->GetFieldID(env, jCls, "hex", "Ljava/lang/String;");
	g_DecodedInstIds.ID_Size = (*env)->GetFieldID(env, jCls, "size", "I");
	g_DecodedInstIds.ID_Offset = (*env)->GetFieldID(env, jCls, "offset", "J");

	jCls = (*env)->FindClass(env, "distorm/DecomposedResult");
	g_DecomposedResultIds.jCls = (*env)->NewWeakGlobalRef(env, jCls);
	g_DecomposedResultIds.ID_Instructions = (*env)->GetFieldID(env, jCls, "instructions", "[Ldistorm/DecomposedInst;");
	g_DecomposedResultIds.ID_MaxInstructions = (*env)->GetFieldID(env, jCls, "maxInstructions", "I");

	jCls = (*env)->FindClass(env, "distorm/DecomposedInst");
	g_DecomposedInstIds.jCls = (*env)->NewWeakGlobalRef(env, jCls);
	g_DecomposedInstIds.ID_Address = (*env)->GetFieldID(env, jCls, "addr", "J");
	g_DecomposedInstIds.ID_Size = (*env)->GetFieldID(env, jCls, "size", "I");
	g_DecomposedInstIds.ID_Flags = (*env)->GetFieldID(env, jCls, "flags", "I");
	g_DecomposedInstIds.ID_Segment = (*env)->GetFieldID(env, jCls, "segment", "I");
	g_DecomposedInstIds.ID_Base = (*env)->GetFieldID(env, jCls, "base", "I");
	g_DecomposedInstIds.ID_Scale = (*env)->GetFieldID(env, jCls, "scale", "I");
	g_DecomposedInstIds.ID_Opcode = (*env)->GetFieldID(env, jCls, "opcode", "I");
	g_DecomposedInstIds.ID_Operands = (*env)->GetFieldID(env, jCls, "operands", "[Ldistorm/Operand;");
	g_DecomposedInstIds.ID_Disp = (*env)->GetFieldID(env, jCls, "disp", "Ldistorm/DecomposedInst$DispVariant;");
	g_DecomposedInstIds.ID_Imm = (*env)->GetFieldID(env, jCls, "imm", "Ldistorm/DecomposedInst$ImmVariant;");
	g_DecomposedInstIds.ID_UnusedPrefixesMask = (*env)->GetFieldID(env, jCls, "unusedPrefixesMask", "I");
	g_DecomposedInstIds.ID_Meta = (*env)->GetFieldID(env, jCls, "meta", "I");
	g_DecomposedInstIds.ID_RegistersMask = (*env)->GetFieldID(env, jCls, "registersMask", "I");
	g_DecomposedInstIds.ID_ModifiedFlagsMask = (*env)->GetFieldID(env, jCls, "modifiedFlagsMask", "I");
	g_DecomposedInstIds.ID_TestedFlagsMask = (*env)->GetFieldID(env, jCls, "testedFlagsMask", "I");
	g_DecomposedInstIds.ID_UndefinedFlagsMask = (*env)->GetFieldID(env, jCls, "undefinedFlagsMask", "I");

	jCls = (*env)->FindClass(env, "distorm/Operand");
	g_OperandIds.jCls = (*env)->NewWeakGlobalRef(env, jCls);
	g_OperandIds.ID_Type = (*env)->GetFieldID(env, jCls, "type", "I");
	g_OperandIds.ID_Index = (*env)->GetFieldID(env, jCls, "index", "I");
	g_OperandIds.ID_Size = (*env)->GetFieldID(env, jCls, "size", "I");

	jCls = (*env)->FindClass(env, "distorm/DecomposedInst$ImmVariant");
	g_ImmIds.jCls = (*env)->NewWeakGlobalRef(env, jCls);
	g_ImmIds.ID_Value = (*env)->GetFieldID(env, jCls, "value", "J");
	g_ImmIds.ID_Size = (*env)->GetFieldID(env, jCls, "size", "I");

	jCls = (*env)->FindClass(env, "distorm/DecomposedInst$DispVariant");
	g_DispIds.jCls = (*env)->NewWeakGlobalRef(env, jCls);
	g_DispIds.ID_Displacement = (*env)->GetFieldID(env, jCls, "displacement", "J");
	g_DispIds.ID_Size = (*env)->GetFieldID(env, jCls, "size", "I");
}