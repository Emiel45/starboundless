package boundless.natives;

public class NativeArguments {
//    For reference:
//
//    public static long readPointer(long pointer) {
//        return Native.getByteBuffer(pointer, 4).order(nativeOrder()).getInt(0);
//    }
//
//    public static String readCString(long offset) {
//        StringBuilder stringBuilder = new StringBuilder();
//        ByteBuffer stringBuffer = Native.getByteBuffer(offset, 0xffff);
//        char c;
//        while(true) {
//            c = (char) stringBuffer.get();
//            if(c != '\0') {
//                stringBuilder.append(c);
//            } else {
//                break;
//            }
//        }
//        return stringBuilder.toString();
//    }
//
//    public static void callback(int id, int stackPointer) {
//        ByteBuffer stack = Native.getByteBuffer(stackPointer + 8, 4 * 6).order(nativeOrder());
//
//        int clientId = stack.getInt(0);
//        String channel = readCString(readPointer(stack.getInt(4)));
//        String message = readCString(readPointer(stack.getInt(8)));
//
//        System.out.println("[" + clientId + "][" + channel + "]: " + message);
//    }
//
//
}
