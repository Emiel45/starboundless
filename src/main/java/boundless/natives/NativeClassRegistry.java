package boundless.natives;

import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class NativeClassRegistry {
    private static final NativeClassRegistry instance = new NativeClassRegistry();

    public static NativeClassRegistry instance() {
        return instance;
    }

    private Map<String, NativeClass> classes = Maps.newHashMap();

    private NativeClassRegistry() {
    }

    public NativeClass getClassByName(String name) {
        return classes.get(name);
    }

    public Collection<NativeClass> listClasses() {
        return Collections.unmodifiableCollection(classes.values());
    }
}
