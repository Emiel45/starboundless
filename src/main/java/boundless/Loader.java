package boundless;

import boundless.hook.HookManager;

public class Loader {
    public static void main() {
        // HookManager.instance().registerHook();

        System.out.println("Starboundless: Resuming launch...");
        Native.resumeLaunch();
    }
}
