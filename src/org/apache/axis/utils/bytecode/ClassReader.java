/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.axis.utils.bytecode;

import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the class file reader for obtaining the parameter names
 * for declared methods in a class.  The class must have debugging
 * attributes for us to obtain this information. <p>
 *
 * This does not work for inherited methods.  To obtain parameter
 * names for inherited methods, you must use a paramReader for the
 * class that originally declared the method. <p>
 *
 * don't get tricky, it's the bare minimum.  Instances of this class
 * are not threadsafe -- don't share them. <p>
 *
 * @author Edwin Smith, Macromedia
 */
public class ClassReader extends ByteArrayInputStream {
    // constants values that appear in java class files,
    // from jvm spec 2nd ed, section 4.4, pp 103
    private static final int CONSTANT_Class = 7;
    private static final int CONSTANT_Fieldref = 9;
    private static final int CONSTANT_Methodref = 10;
    private static final int CONSTANT_InterfaceMethodref = 11;
    private static final int CONSTANT_String = 8;
    private static final int CONSTANT_Integer = 3;
    private static final int CONSTANT_Float = 4;
    private static final int CONSTANT_Long = 5;
    private static final int CONSTANT_Double = 6;
    private static final int CONSTANT_NameAndType = 12;
    private static final int CONSTANT_Utf8 = 1;
    /**
     * the constant pool.  constant pool indices in the class file
     * directly index into this array.  The value stored in this array
     * is the position in the class file where that constant begins.
     */
    private int[] cpoolIndex;
    private Object[] cpool;

    private Map attrMethods;

    /**
     * load the bytecode for a given class, by using the class's defining
     * classloader and assuming that for a class named P.C, the bytecodes are
     * in a resource named /P/C.class.
     * @param c the class of interest
     * @return a byte array containing the bytecode
     * @throws IOException
     */
    protected static byte[] getBytes(Class c) throws IOException {
        InputStream fin = c.getResourceAsStream('/' + c.getName().replace('.', '/') + ".class");
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int actual;
            do {
                actual = fin.read(buf);
                if (actual > 0) {
                    out.write(buf, 0, actual);
                }
            } while (actual > 0);
            return out.toByteArray();
        } finally {
            fin.close();
        }
    }

    static String classDescriptorToName(String desc) {
        return desc.replace('/', '.');
    }

    protected static Map findAttributeReaders(Class c) {
        HashMap map = new HashMap();
        Method[] methods = c.getMethods();

        for (int i = 0; i < methods.length; i++) {
            String name = methods[i].getName();
            if (name.startsWith("read") && methods[i].getReturnType() == void.class) {
                map.put(name.substring(4), methods[i]);
            }
        }

        return map;
    }


    protected static String getSignature(Member method, Class[] paramTypes) {
        // compute the method descriptor

        StringBuffer b = new StringBuffer((method instanceof Method) ? method.getName() : "<init>");
        b.append('(');

        for (int i = 0; i < paramTypes.length; i++) {
            addDescriptor(b, paramTypes[i]);
        }

        b.append(')');
        if (method instanceof Method) {
            addDescriptor(b, ((Method) method).getReturnType());
        } else if (method instanceof Constructor) {
            addDescriptor(b, void.class);
        }

        return b.toString();
    }

    private static void addDescriptor(StringBuffer b, Class c) {
        if (c.isPrimitive()) {
            if (c == void.class)
                b.append('V');
            else if (c == int.class)
                b.append('I');
            else if (c == boolean.class)
                b.append('Z');
            else if (c == byte.class)
                b.append('B');
            else if (c == short.class)
                b.append('S');
            else if (c == long.class)
                b.append('J');
            else if (c == char.class)
                b.append('C');
            else if (c == float.class)
                b.append('F');
            else if (c == double.class) b.append('D');
        } else if (c.isArray()) {
            b.append('[');
            addDescriptor(b, c.getComponentType());
        } else {
            b.append('L').append(c.getName().replace('.', '/')).append(';');
        }
    }


    /**
     * @return the next unsigned 16 bit value
     */
    protected final int readShort() {
        return (read() << 8) | read();
    }

    /**
     * @return the next signed 32 bit value
     */
    protected final int readInt() {
        return (read() << 24) | (read() << 16) | (read() << 8) | read();
    }

    /**
     * skip n bytes in the input stream.
     */
    protected void skipFully(int n) throws IOException {
        while (n > 0) {
            int c = (int) skip(n);
            if (c <= 0)
                throw new EOFException(Messages.getMessage("unexpectedEOF00"));
            n -= c;
        }
    }

    protected final Member resolveMethod(int index) throws IOException, ClassNotFoundException, NoSuchMethodException {
        int oldPos = pos;
        try {
            Member m = (Member) cpool[index];
            if (m == null) {
                pos = cpoolIndex[index];
                Class owner = resolveClass(readShort());
                NameAndType nt = resolveNameAndType(readShort());
                String signature = nt.name + nt.type;
                if (nt.name.equals("<init>")) {
                    Constructor[] ctors = owner.getConstructors();
                    for (int i = 0; i < ctors.length; i++) {
                        String sig = getSignature(ctors[i], ctors[i].getParameterTypes());
                        if (sig.equals(signature)) {
                            cpool[index] = m = ctors[i];
                            return m;
                        }
                    }
                } else {
                    Method[] methods = owner.getDeclaredMethods();
                    for (int i = 0; i < methods.length; i++) {
                        String sig = getSignature(methods[i], methods[i].getParameterTypes());
                        if (sig.equals(signature)) {
                            cpool[index] = m = methods[i];
                            return m;
                        }
                    }
                }
                throw new NoSuchMethodException(signature);
            }
            return m;
        } finally {
            pos = oldPos;
        }

    }

    protected final Field resolveField(int i) throws IOException, ClassNotFoundException, NoSuchFieldException {
        int oldPos = pos;
        try {
            Field f = (Field) cpool[i];
            if (f == null) {
                pos = cpoolIndex[i];
                Class owner = resolveClass(readShort());
                NameAndType nt = resolveNameAndType(readShort());
                cpool[i] = f = owner.getDeclaredField(nt.name);
            }
            return f;
        } finally {
            pos = oldPos;
        }
    }

    private static class NameAndType {
        String name;
        String type;

        public NameAndType(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }

    protected final NameAndType resolveNameAndType(int i) throws IOException {
        int oldPos = pos;
        try {
            NameAndType nt = (NameAndType) cpool[i];
            if (nt == null) {
                pos = cpoolIndex[i];
                String name = resolveUtf8(readShort());
                String type = resolveUtf8(readShort());
                cpool[i] = nt = new NameAndType(name, type);
            }
            return nt;
        } finally {
            pos = oldPos;
        }
    }


    protected final Class resolveClass(int i) throws IOException, ClassNotFoundException {
        int oldPos = pos;
        try {
            Class c = (Class) cpool[i];
            if (c == null) {
                pos = cpoolIndex[i];
                String name = resolveUtf8(readShort());
                cpool[i] = c = Class.forName(classDescriptorToName(name));
            }
            return c;
        } finally {
            pos = oldPos;
        }
    }

    protected final String resolveUtf8(int i) throws IOException {
        int oldPos = pos;
        try {
            String s = (String) cpool[i];
            if (s == null) {
                pos = cpoolIndex[i];
                int len = readShort();
                skipFully(len);
                cpool[i] = s = new String(buf, pos - len, len, "utf-8");
            }
            return s;
        } finally {
            pos = oldPos;
        }
    }

    protected final void readCpool() throws IOException {
        int count = readShort(); // cpool count
        cpoolIndex = new int[count];
        cpool = new Object[count];
        for (int i = 1; i < count; i++) {
            int c = read();
            cpoolIndex[i] = super.pos;
            switch (c) // constant pool tag
            {
                case CONSTANT_Fieldref:
                case CONSTANT_Methodref:
                case CONSTANT_InterfaceMethodref:
                case CONSTANT_NameAndType:

                    readShort(); // class index or (12) name index
                    // fall through

                case CONSTANT_Class:
                case CONSTANT_String:

                    readShort(); // string index or class index
                    break;

                case CONSTANT_Long:
                case CONSTANT_Double:

                    readInt(); // hi-value

                    // see jvm spec section 4.4.5 - double and long cpool
                    // entries occupy two "slots" in the cpool table.
                    i++;
                    // fall through

                case CONSTANT_Integer:
                case CONSTANT_Float:

                    readInt(); // value
                    break;

                case CONSTANT_Utf8:

                    int len = readShort();
                    skipFully(len);
                    break;

                default:
                    // corrupt class file
                    throw new IllegalStateException(
                            Messages.getMessage("unexpectedBytes00"));
            }
        }
    }

    protected final void skipAttributes() throws IOException {
        int count = readShort();
        for (int i = 0; i < count; i++) {
            readShort(); // name index
            skipFully(readInt());
        }
    }

    /**
     * read an attributes array.  the elements of a class file that
     * can contain attributes are: fields, methods, the class itself,
     * and some other types of attributes.
     */
    protected final void readAttributes() throws IOException {
        int count = readShort();
        for (int i = 0; i < count; i++) {
            int nameIndex = readShort(); // name index
            int attrLen = readInt();
            int curPos = pos;

            String attrName = resolveUtf8(nameIndex);

            Method m = (Method) attrMethods.get(attrName);

            if (m != null) {
                try {
                    m.invoke(this, new Object[]{});
                } catch (IllegalAccessException e) {
                    pos = curPos;
                    skipFully(attrLen);
                } catch (InvocationTargetException e) {
                    try {
                        throw e.getTargetException();
                    } catch (Error ex) {
                        throw ex;
                    } catch (RuntimeException ex) {
                        throw ex;
                    } catch (IOException ex) {
                        throw ex;
                    } catch (Throwable ex) {
                        pos = curPos;
                        skipFully(attrLen);
                    }
                }
            } else {
                // don't care what attribute this is
                skipFully(attrLen);
            }
        }
    }

    /**
     * read a code attribute
     * @throws IOException
     */
    public void readCode() throws IOException {
        readShort(); // max stack
        readShort(); // max locals
        skipFully(readInt()); // code
        skipFully(8 * readShort()); // exception table

        // read the code attributes (recursive).  This is where
        // we will find the LocalVariableTable attribute.
        readAttributes();
    }

    protected ClassReader(byte buf[], Map attrMethods) {
        super(buf);

        this.attrMethods = attrMethods;
    }
}

