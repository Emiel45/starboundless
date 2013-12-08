package boundless.natives.hook;

import boundless.natives.NativeFunction;
import boundless.natives.NativeReturnType;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class HookManager {
    public static final int MAX_HOOK_COUNT = 0xffff;
    private static final HookManager instance = new HookManager();

    public static HookManager instance() {
        return instance;
    }

    private HookManager() {
    }

    private int hookId;

    private NativeFunction[] hooks = new NativeFunction[MAX_HOOK_COUNT];

    private Multimap<NativeFunction, HookCallback<?>> callbacks = HashMultimap.create();
    private Map<NativeFunction, NativeFunction> originals = Maps.newHashMap();

    public <T> void registerHook(NativeFunction function, HookCallback<T> callback) {
        if (!callbacks.containsKey(function)) {
            int id = hookId++;

            // TODO make every type of inheritance work
            Type returnType = ((ParameterizedType) callback.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
            NativeReturnType nativeReturnType = NativeReturnType.fromWrappedPrimitive((Class<?>) returnType);

            long originalAddress = HookWriter.writeHook(function.getAddress(), id, nativeReturnType);
            NativeFunction originalFunction = new NativeFunction(function.getParent(), originalAddress, function.getName() + "_original");

            hooks[id] = function;
            originals.put(function, originalFunction);
        }
        callbacks.put(function, callback);
    }

    public Collection<HookCallback<?>> getCallbacksById(int id) {
        return Collections.unmodifiableCollection(callbacks.get(hooks[id]));
    }

    public NativeFunction getOriginalById(int id) {
        return originals.get(hooks[id]);
    }
}
