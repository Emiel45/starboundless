package boundless.natives;

import com.google.common.collect.Maps;

import java.util.Map;

public enum NativeReturnType {
    BOOLEAN('Z', "Boolean", Boolean.class),
    BYTE('B', "Byte", Byte.class),
    CHAR('C', "Char", Character.class),
    SHORT('S', "Short", Short.class),
    INT('I', "Int", Integer.class),
    LONG('J', "Long", Long.class),
    FLOAT('F', "Float", Float.class),
    DOUBLE('D', "Double", Double.class),
    VOID('V', "Void", Void.class);

    private static final Map<Character, NativeReturnType> identifierToReturnType = Maps.newHashMap();
    private static final Map<String, NativeReturnType> typeNameToReturnType = Maps.newHashMap();
    private static final Map<Class<?>, NativeReturnType> wrappedPrimitiveToReturnType = Maps.newHashMap();
    static {
        for(NativeReturnType returnType : values()) {
            identifierToReturnType.put(returnType.identifier, returnType);
            typeNameToReturnType.put(returnType.typeName, returnType);
            wrappedPrimitiveToReturnType.put(returnType.wrappedPrimitive, returnType);
        }
    }

    public static NativeReturnType fromIdentifier(char identifier) {
        return identifierToReturnType.get(identifier);
    }

    public static NativeReturnType fromTypeName(String typeName) {
        return typeNameToReturnType.get(typeName);
    }

    public static NativeReturnType fromWrappedPrimitive(Class<?> wrappedPrimitive) {
        return wrappedPrimitiveToReturnType.get(wrappedPrimitive);
    }

    private char identifier;
    private String typeName;
    private Class<?> wrappedPrimitive;

    private NativeReturnType(char identifier, String typeName, Class<?> wrappedPrimitive) {
        this.identifier = identifier;
        this.typeName = typeName;
        this.wrappedPrimitive = wrappedPrimitive;
    }

    public char identifier() {
        return identifier;
    }

    public String typeName() {
        return typeName;
    }

    public Class<?> wrappedPrimitive() {
        return wrappedPrimitive;
    }
}
