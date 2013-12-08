package boundless.natives.call;

import boundless.Native;
import jnr.x86asm.Assembler;
import jnr.x86asm.CPU;

import java.nio.ByteBuffer;

import static java.nio.ByteOrder.nativeOrder;
import static jnr.x86asm.Asm.*;

public class CallWriter {
    public static long writeCall(long address, int argCount) {
        Assembler asm = new Assembler(CPU.X86_32);

        asm.int3();
        asm.push(ebp);
        asm.mov(ebp, esp);

        asm.mov(eax, dword_ptr(ebp, 8));
        asm.mov(ecx, dword_ptr(eax, (argCount - 1) * 4));

        asm.sub(esp, uimm(argCount * 4));

        for(int i = 0; i < argCount; i++) {
            asm.mov(ebx, dword_ptr(eax, i * 4));
            asm.mov(dword_ptr(esp, i * 4), ebx);
        }
        asm.call(uimm(address));

        asm.mov(esp, ebp);
        asm.pop(ebp);
        asm.ret();

        ByteBuffer caller = Native.allocMemory(asm.codeSize()).order(nativeOrder());
        asm.relocCode(caller, Native.addressOf(caller));

        return Native.addressOf(caller);
    }

}
