package boundless.natives.hook;

import boundless.JNIEnv;
import boundless.Native;
import boundless.natives.NativeReturnType;
import distorm.*;
import jnr.x86asm.Assembler;

import java.nio.ByteBuffer;

import static java.nio.ByteOrder.nativeOrder;
import static jnr.x86asm.Asm.*;
import static jnr.x86asm.CPU.X86_32;

public class HookWriter {
    public static long writeHook(long offset, long hookId, NativeReturnType returnType) {
        long jvm = Native.getJVMPointer();
        long classPointer = JNIEnv.getClassPtr(HookCallbackNative.class);
        long methodId = JNIEnv.getStaticMethodID(HookCallbackNative.class, "callback" + returnType.typeName(), "(III)" + returnType.identifier());

        ByteBuffer jvmStruct = Native.getByteBuffer(Native.getByteBuffer(jvm, 4).order(nativeOrder()).getInt(), 8 * 4).order(nativeOrder());
        long jvm_AttachCurrentThread = jvmStruct.getInt(4 * 4);

        // Write landing method
        Assembler asm = new Assembler(X86_32);

        asm.int3();

        if(!returnType.usesEax) asm.push(eax);
        else asm.push(imm(0));
        asm.push(ebx);          //
        asm.push(ecx);          //
        asm.push(edx);          // preserve for stdcalls

        asm.push(ebp);          //
        asm.mov(ebp, esp);      // setup stack allocation

        asm.sub(esp, imm(4));   // allocate memory for jni env pointer
        asm.mov(ebx, esp);

        // invoke AttachCurrentThread(jvm, &env, 0);
        asm.push(imm(0));
        asm.push(ebx);
        asm.push(imm(jvm));
        asm.call(imm(jvm_AttachCurrentThread));

        // TODO check eax

        asm.mov(ebx, dword_ptr(ebx, 0));

        asm.push(dword_ptr(ebp, 8));
        asm.push(ebp);
        asm.push(uimm(hookId));
        asm.push(uimm(methodId));
        asm.push(uimm(classPointer));
        asm.push(ebx);
        asm.call(JNIEnv.addressOfMethod("CallStatic" + returnType.typeName() + "Method"));

        asm.mov(esp, ebp);
        asm.pop(ebp);

        asm.pop(edx);
        asm.pop(ecx);
        asm.pop(ebx);
        if(!returnType.usesEax) asm.pop(eax);
        else asm.add(esp, imm(4));

        asm.ret();

        // write landing method
        ByteBuffer lander = Native.allocMemory(asm.codeSize()).order(nativeOrder());
        asm.relocCode(lander, Native.addressOf(lander));

        asm = new Assembler(X86_32);
        asm.jmp(imm(Native.addressOf(lander)));

        ByteBuffer trampoline = Native.getByteBuffer(offset, asm.codeSize() + 8).order(nativeOrder());

        DecodedResult decodedTrampoline = new DecodedResult(0xff);
        Distorm.decode(new CodeInfo(offset, trampoline, DecodeType.Decode32Bits, 0), decodedTrampoline);

        int trampolineSize = asm.codeSize();

        int originalSize = 0;
        for(int i = 0; i < decodedTrampoline.instructions.length; i++) {
            DecodedInst inst = decodedTrampoline.instructions[i];
            originalSize += inst.getSize();
            if(originalSize > trampolineSize) {
                break;
            }
        }

        while(asm.codeSize() < originalSize) asm.nop();

        Assembler asm2 = new Assembler(X86_32);
        asm2.jmp(uimm(Native.addressOf(trampoline) + originalSize));

        ByteBuffer original = Native.allocMemory(originalSize + asm.codeSize()).order(nativeOrder());
        for(int i = 0; i < originalSize; i++) original.put(i, trampoline.get(i));

        ByteBuffer originalJmp = Native.getByteBuffer(Native.addressOf(original) + originalSize, asm.codeSize()).order(nativeOrder());
        asm2.relocCode(originalJmp, Native.addressOf(originalJmp));

        asm.relocCode(trampoline, offset);

        // TODO return original function
        return Native.addressOf(original);
    }
}
