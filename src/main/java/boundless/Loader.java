package boundless;

import boundless.hook.HookWriter;
import distorm.*;

import javax.swing.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static java.nio.ByteOrder.nativeOrder;

public class Loader {
    public static long readPointer(long pointer) {
        return Native.getByteBuffer(pointer, 4).order(nativeOrder()).getInt(0);
    }

    public static String readCString(long offset) {
        StringBuilder stringBuilder = new StringBuilder();
        ByteBuffer stringBuffer = Native.getByteBuffer(offset, 0xffff);
        char c;
        while(true) {
            c = (char) stringBuffer.get();
            if(c != '\0') {
                stringBuilder.append(c);
            } else {
                break;
            }
        }
        return stringBuilder.toString();
    }

    public static void callback(int id, int stackPointer) {
        ByteBuffer stack = Native.getByteBuffer(stackPointer + 8, 4 * 6).order(nativeOrder());

        int clientId = stack.getInt(0);
        String channel = readCString(readPointer(stack.getInt(4)));
        String message = readCString(readPointer(stack.getInt(8)));

        System.out.println("[" + clientId + "][" + channel + "]: " + message);
    }
    public static void main() {
        JOptionPane.showMessageDialog(null, "Press ok to launch.");
        long classPtr = JNIEnv.getClassPtr(Loader.class);
        long methodId = JNIEnv.getStaticMethodID(Loader.class, "callback", "(II)V");

        System.out.println("Loader classPtr: " + Long.toHexString(classPtr));
        System.out.println("Callback id: " + Long.toHexString(methodId));

        long hooked_function = 0x43c600;

        System.out.println("Starboundless: Reading hooked_function");
        ByteBuffer chatProcessor_joinChannel = Native.getByteBuffer(hooked_function, 100);

        CodeInfo info = new CodeInfo(hooked_function, chatProcessor_joinChannel, DecodeType.Decode32Bits, 0);
        DecomposedResult result = new DecomposedResult(100);

        System.out.println("Starboundless: Decomposing Reading hooked_function");
        Distorm.decompose(info, result);

        System.out.println("Starboundless: Iterating instructions...");
        for(DecomposedInst inst : result.instructions) {
            if(inst == null) break;
            DecodedInst decodedInst = Distorm.format(info, inst);
            System.out.println("0x" + Long.toHexString(decodedInst.getOffset()) + ": " +decodedInst.getHex() + " \t" + decodedInst.getMnemonic() + " \t" + decodedInst.getOperands());
        }

        HookWriter.writeHook(hooked_function, classPtr, methodId, 1);

        info = new CodeInfo(hooked_function, chatProcessor_joinChannel, DecodeType.Decode32Bits, 0);
        result = new DecomposedResult(100);

        System.out.println("Starboundless: Decomposing Reading hooked_function");
        Distorm.decompose(info, result);

        System.out.println("Starboundless: Iterating instructions...");
        for(DecomposedInst inst : result.instructions) {
            if(inst == null) break;
            DecodedInst decodedInst = Distorm.format(info, inst);
            System.out.println("0x" + Long.toHexString(decodedInst.getOffset()) + ": " +decodedInst.getHex() + " \t" + decodedInst.getMnemonic() + " \t" + decodedInst.getOperands());
        }

        System.out.println("Starboundless: Resuming launch...");
        Native.resumeLaunch();
    }
}
