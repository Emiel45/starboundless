package boundless;

import java.nio.ByteBuffer;

public class Native {
    // TODO: Cleanup
    // Don't use allocMemory but protectMemory instead
    // rename getByteBuffer to fromPointer

    public static native ByteBuffer getByteBuffer(long address, long capacity);
    public static native ByteBuffer allocMemory(long capacity);
    public static native void resumeLaunch();
    public static native long addressOf(ByteBuffer buffer);
    public static native void call(long address);
}
