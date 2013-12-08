package boundless.hook;

import boundless.natives.NativeArguments;
import boundless.natives.NativeFunction;

public interface HookCallback<T> {
    public T call(NativeFunction original, NativeArguments arguments);
}
