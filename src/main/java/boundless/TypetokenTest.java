package boundless;

import boundless.natives.hook.HookCallback;
import boundless.natives.NativeArguments;
import boundless.natives.NativeFunction;

import java.lang.reflect.ParameterizedType;

public class TypeTokenTest {
    public static void main(String[] args) {
        HookCallback<?> stuff = new HookCallback<Void>() {

            @Override
            public Void call(NativeFunction original, NativeArguments arguments, int ecx) {
                return null;
            }
        };
        System.out.println(((ParameterizedType)stuff.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0]);
    }
}
