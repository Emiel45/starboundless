package boundless.natives.call;

import java.nio.ByteBuffer;

public class CallInterface {
    public static native boolean callBoolean(long address, ByteBuffer argsBuffer);
    public static native byte callByte(long address, ByteBuffer argsBuffer);
    public static native char callChar(long address, ByteBuffer argsBuffer);
    public static native short callShort(long address, ByteBuffer argsBuffer);
    public static native int callInt(long address, ByteBuffer argsBuffer);
    public static native long callLong(long address, ByteBuffer argsBuffer);
    public static native float callFloat(long address, ByteBuffer argsBuffer);
    public static native double callDouble(long address, ByteBuffer argsBuffer);
    public static native void callVoid(long address, ByteBuffer argsBuffer);
}
