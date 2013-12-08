package boundless.hook;

public class HookCallbackNative {
    public static Object callback(int id, int stackPointer) {
        return null;
    }

    public static boolean callbackBoolean(int id, int stackPointer) {
        return (Boolean) callback(id, stackPointer);
    }

    public static byte callbackByte(int id, int stackPointer) {
        return (Byte) callback(id, stackPointer);
    }

    public static char callbackChar(int id, int stackPointer) {
        return (Character) callback(id, stackPointer);
    }

    public static short callbackShort(int id, int stackPointer) {
        return (Short) callback(id, stackPointer);
    }

    public static int callbackInt(int id, int stackPointer) {
        return (Integer) callback(id, stackPointer);
    }

    public static long callbackLong(int id, int stackPointer) {
        return (Long) callback(id, stackPointer);
    }

    public static float callbackFloat(int id, int stackPointer) {
        return (Float) callback(id, stackPointer);
    }

    public static double callbackDouble(int id, int stackPointer) {
        return (Double) callback(id, stackPointer);
    }

    public static void callbackVoid(int id, int stackPointer) {
        callback(id, stackPointer);
    }
}
