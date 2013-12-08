package boundless.natives.hook;

import boundless.Native;
import boundless.natives.NativeArguments;
import boundless.natives.NativeFunction;

import static java.nio.ByteOrder.nativeOrder;

public class HookCallbackNative {
    public static Object callback(int id, int stackPointer, int ecx) {
        // TODO use arguments size instead of 0xff
        NativeArguments arguments = new NativeArguments(Native.getByteBuffer(stackPointer + 24, 0xff).order(nativeOrder()));
        NativeFunction originalFunction = HookManager.instance().getOriginalById(id);
        for(HookCallback<?> callback : HookManager.instance().getCallbacksById(id)) {
            callback.call(originalFunction, arguments, 0);
        }
        return null;
    }

    public static boolean callbackBoolean(int id, int stackPointer) {
        return (Boolean) callback(id, stackPointer, 0);
    }

    public static byte callbackByte(int id, int stackPointer) {
        return (Byte) callback(id, stackPointer, 0);
    }

    public static char callbackChar(int id, int stackPointer) {
        return (Character) callback(id, stackPointer, 0);
    }

    public static short callbackShort(int id, int stackPointer) {
        return (Short) callback(id, stackPointer, 0);
    }

    public static int callbackInt(int id, int stackPointer, int ecx) {
        return (Integer) callback(id, stackPointer, ecx);
    }

    public static long callbackLong(int id, int stackPointer) {
        return (Long) callback(id, stackPointer, 0);
    }

    public static float callbackFloat(int id, int stackPointer) {
        return (Float) callback(id, stackPointer, 0);
    }

    public static double callbackDouble(int id, int stackPointer) {
        return (Double) callback(id, stackPointer, 0);
    }

    public static void callbackVoid(int id, int stackPointer) {
        callback(id, stackPointer, 0);
    }
}
