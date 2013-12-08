package boundless.hook;

import boundless.JNIEnv;
import boundless.Native;
import boundless.natives.NativeReturnType;
import jnr.x86asm.Assembler;

import java.nio.ByteBuffer;

import static java.nio.ByteOrder.nativeOrder;
import static jnr.x86asm.Asm.*;
import static jnr.x86asm.CPU.X86_32;

public class HookWriter {
    public static long writeHook(long offset, long hookId, NativeReturnType returnType) {
        long jvm = Native.getJVMPointer();
        long classPointer = JNIEnv.getClassPtr(HookCallbackNative.class);
        long methodId = JNIEnv.getMethodID(HookCallbackNative.class, "callback" + returnType.typeName(), "(II)" + returnType.identifier());

        ByteBuffer jvmStruct = Native.getByteBuffer(Native.getByteBuffer(jvm, 4).order(nativeOrder()).getInt(), 8 * 4).order(nativeOrder());
        long jvm_AttachCurrentThread = jvmStruct.getInt(4 * 4);

        // Write landing method
        Assembler asm = new Assembler(X86_32);

        asm.push(ebp);          //
        asm.mov(ebp, esp);      // setup stack allocation

        asm.push(ecx);          //
        asm.push(ebx);          //
        asm.push(edx);          // preserve for stdcalls

        asm.sub(esp, imm(4));   // allocate memory for jni env pointer
        asm.mov(ebx, esp);

        // invoke AttachCurrentThread(jvm, &env, 0);
        asm.push(imm(0));
        asm.push(ebx);
        asm.push(imm(jvm));
        asm.call(imm(jvm_AttachCurrentThread));

        // TODO check eax

        asm.mov(ebx, dword_ptr(ebx, 0));
        asm.mov(esp, ebp);

        asm.push(ebp);
        asm.push(uimm(hookId));
        asm.push(uimm(methodId));
        asm.push(uimm(classPointer));
        asm.push(ebx);
        asm.call(JNIEnv.addressOfMethod("CallStaticVoidMethod"));

        asm.pop(edx);
        asm.pop(ebx);
        asm.pop(ecx);

        asm.mov(esp, ebp);
        asm.pop(ebp);

        asm.ret();

        // write landing method
        ByteBuffer lander = Native.allocMemory(asm.codeSize()).order(nativeOrder());
        asm.relocCode(lander, Native.addressOf(lander));

        System.out.println("Lander @ " + Long.toHexString(Native.addressOf(lander)));

        asm = new Assembler(X86_32);
        asm.jmp(imm(Native.addressOf(lander)));
        asm.nop();

        ByteBuffer function = Native.getByteBuffer(offset, asm.codeSize()).order(nativeOrder());
        // write trampoline
        asm.relocCode(function, offset);

        // TODO return original function
        return 0;
    }
}
