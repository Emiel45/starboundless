package boundless.hook;

import boundless.natives.NativeFunction;
import boundless.natives.NativeReturnType;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class HookManager {
    private static final HookManager instance = new HookManager();

    public static HookManager instance() {
        return instance;
    }

    private HookManager() {
    }

    private int hookId;

    private Multimap<NativeFunction, HookCallback<?>> hooks = HashMultimap.create();
    private Multimap<NativeFunction, NativeFunction> originals = HashMultimap.create();

    public <T> void registerHook(NativeFunction function, HookCallback<T> callback) {
        if (!hooks.containsKey(function)) {
            // TODO make every type of inheritance work
            Type returnType = ((ParameterizedType) callback.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
            NativeReturnType nativeReturnType = NativeReturnType.fromWrappedPrimitive((Class<?>) returnType);

            long originalAddress = HookWriter.writeHook(function.getAddress(), hookId++, nativeReturnType);
            NativeFunction originalFunction = new NativeFunction(function.getParent(), originalAddress, function.getName() + "_original");

            originals.put(function, originalFunction);
        }
        hooks.put(function, callback);
    }
}
