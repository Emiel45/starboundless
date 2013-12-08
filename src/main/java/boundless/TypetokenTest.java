package boundless;

import boundless.hook.HookCallback;
import boundless.natives.NativeArguments;
import boundless.natives.NativeFunction;
import com.google.common.reflect.TypeToken;

import java.lang.reflect.ParameterizedType;

public class TypeTokenTest {
    public static void main(String[] args) {
        HookCallback<?> stuff = new HookCallback<Void>() {

            @Override
            public Void call(NativeFunction original, NativeArguments arguments) {
                return null;
            }
        };
        System.out.println(((ParameterizedType)stuff.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0]);
    }
}
