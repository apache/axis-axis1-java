package org.apache.axis.rpc.util;

import java.util.Hashtable;

final public class JavaType {
    private JavaType() {}

    public static final byte _void = 1;
    public static final byte _boolean = 2;
	public static final byte _byte = 3;
	public static final byte _short = 4;
	public static final byte _char = 5;
	public static final byte _int = 6;
	public static final byte _long = 7;
	public static final byte _float = 8;
	public static final byte _double = 9;
	public static final byte _string = 10;
	public static final byte _object = 11;
    static final Hashtable types = new Hashtable();
    static {
        types.put(void.class, new Integer(_void));
        types.put(boolean.class, new Integer(_boolean));
        types.put(byte.class, new Integer(_byte));
        types.put(short.class, new Integer(_short));
        types.put(char.class, new Integer(_char));
        types.put(int.class, new Integer(_int));
        types.put(long.class, new Integer(_long));
        types.put(float.class, new Integer(_float));
        types.put(double.class, new Integer(_double));
        types.put(String.class, new Integer(_string));
        types.put(Object.class, new Integer(_object));
    }

    public static int getTypeCode(Class type) {
        Integer i;
        if ((i = (Integer)types.get(type)) == null)
            return -1;
        return i.intValue();
    }

    static final Hashtable wrapper2primitive = new Hashtable();
    static {
        wrapper2primitive.put(Void.class, void.class);
        wrapper2primitive.put(Boolean.class, boolean.class);
        wrapper2primitive.put(Byte.class, byte.class);
        wrapper2primitive.put(Short.class, short.class);
        wrapper2primitive.put(Character.class, char.class);
        wrapper2primitive.put(Integer.class, int.class);
        wrapper2primitive.put(Long.class, long.class);
        wrapper2primitive.put(Float.class, float.class);
        wrapper2primitive.put(Double.class, double.class);
    }
    public static Class toPrimitiveType(Class wrapperType) {
        return (Class)wrapper2primitive.get(wrapperType);
    }

    static final Hashtable sig2type = new Hashtable();
    static {
        sig2type.put("V", void.class);
        sig2type.put("Z", boolean.class);
        sig2type.put("B", byte.class);
        sig2type.put("S", short.class);
        sig2type.put("C", char.class);
        sig2type.put("I", int.class);
        sig2type.put("J", long.class);
        sig2type.put("F", float.class);
        sig2type.put("D", double.class);
    }
    public static Class toPrimitiveType(String sig) {
        return (Class)sig2type.get(sig);
    }

    static final Hashtable type2sig = new Hashtable();
    static {
        type2sig.put(void.class, "V");
        type2sig.put(boolean.class, "Z");
        type2sig.put(byte.class, "B");
        type2sig.put(short.class, "S");
        type2sig.put(char.class, "C");
        type2sig.put(int.class, "I");
        type2sig.put(long.class, "J");
        type2sig.put(float.class, "F");
        type2sig.put(double.class, "D");
    }
    public static String toSignature(Class type) {
        String sig;
        if ((sig = (String)type2sig.get(type)) != null)
            return sig;
        return type.getName();
    }
}
