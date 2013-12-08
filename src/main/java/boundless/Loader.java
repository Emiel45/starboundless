package boundless;

import boundless.natives.NativeArguments;
import boundless.natives.NativeClass;
import boundless.natives.NativeFunction;
import boundless.natives.call.CallInterface;
import boundless.natives.call.CallWriter;
import boundless.natives.hook.HookCallback;
import boundless.natives.hook.HookManager;

import java.nio.ByteBuffer;

import static java.nio.ByteOrder.nativeOrder;

public class Loader {
    public static void main() {
//        JOptionPane.showMessageDialog(null, "Press ok to resume.");

//        int i = 0;
//        ByteBuffer cstring = ByteBuffer.allocateDirect(0xff).order(nativeOrder());
//        for (char c : "Hello World from StarBound's logger!".toCharArray()) {
//            cstring.put(i++, (byte) c);
//        }
//        cstring.put(i++, (byte) 0);
//
//
//        final ByteBuffer arguments = ByteBuffer.allocateDirect(4).order(nativeOrder());
//        arguments.putInt(0, (int) Native.addressOf(cstring));
//
//        final long callerAddress = CallWriter.writeCall(0x6afaf0, 1);
//
//        new Thread() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                CallInterface.callVoid(callerAddress, arguments);
//            }
//        }.start();

        System.out.println("Instantiating Class...");
        NativeClass loggerClass = new NativeClass("ChatProcessor");
        System.out.println("Instantiating Function...");
        NativeFunction logger_info = new NativeFunction(loggerClass, 0x47c790, "handleCommand");

        System.out.println("Hooking: " + logger_info);
        HookManager.instance().registerHook(logger_info, new HookCallback<Integer>() {
            @Override
            public Integer call(NativeFunction original, NativeArguments arguments, int ecx) {

                System.out.println("hai command handler");
                ByteBuffer returnBufferInside = Native.allocMemory(4).order(nativeOrder());

                ByteBuffer returnBuffer = Native.allocMemory(4).order(nativeOrder());
                returnBuffer.putInt(0, (int) Native.addressOf(returnBufferInside));

                ByteBuffer argumentsBuffer = Native.allocMemory(5 * 4).order(nativeOrder());
                for(int i = 0; i < argumentsBuffer.capacity(); i++) argumentsBuffer.put(i, arguments.buffer().get(i));

                argumentsBuffer.putInt(4 * 4, ecx);

                System.out.println("returnBuffer @ " + Long.toHexString(Native.addressOf(returnBuffer)));

                long caller = CallWriter.writeCall(original.getAddress(), 5);
                int ret = CallInterface.callInt(caller, argumentsBuffer);

                System.out.println("ret: " + (ret == ecx));

                return ret;
            }
        });

        System.out.println("Starboundless: Resuming launch...");
        Native.resumeLaunch();
    }
}
