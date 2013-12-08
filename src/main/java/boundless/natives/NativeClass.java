package boundless.natives;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Collections;

public class NativeClass {
    private String name;
    private Multimap<String, NativeFunction> functions = HashMultimap.create();

    public NativeClass(String name) {
        this.name = name;
    }

    public Collection<NativeFunction> listFunctionsByName(String name) {
        return Collections.unmodifiableCollection(functions.get(name));
    }

    public Collection<NativeFunction> listFunctions() {
        return Collections.unmodifiableCollection(functions.values());
    }

    public String getName() {
        return name;
    }
}
