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
package org.apache.axis.components.bytecode;

import org.apache.axis.utils.JavaUtils;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * this is the class file reader for obtaining the parameter names
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
public class ParamReader extends ByteArrayInputStream {
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

    private String methodName;
    private Map methods = new HashMap();

    /**
     * load the bytecode for a given class, by using the class's defining
     * classloader and assuming that for a class named P.C, the bytecodes are
     * in a resource named /P/C.class.
     * @param c the class of interest
     * @return a byte array containing the bytecode
     * @throws IOException
     */
    private static byte[] getBytes(Class c) throws IOException {
        InputStream fin = c.getResourceAsStream('/' + c.getName().replace('.', '/') + ".class");
        try {
            int length = fin.available();
            byte[] b = new byte[length];
            fin.read(b);
            return b;
        } finally {
            fin.close();
        }
    }

    /**
     * process a class file, given it's class.  We'll use the defining
     * classloader to locate the bytecode.
     * @param c
     * @throws IOException
     */
    public ParamReader(Class c) throws IOException {
        this(getBytes(c));
    }

    /**
     * process the given class bytes directly.
     * @param b
     * @throws IOException
     */
    public ParamReader(byte[] b) throws IOException {
        super(b);

        // check the magic number
        if (readInt() != 0xCAFEBABE) {
            // not a class file!
            throw new IOException(JavaUtils.getMessage("badClassFile00"));
        }

        readShort(); // minor version
        readShort(); // major version

        readCpool(); // slurp in the constant pool

        readShort(); // access flags
        readShort(); // this class name
        readShort(); // super class name

        int count = readShort(); // ifaces count
        for (int i = 0; i < count; i++) {
            readShort(); // interface index
        }

        count = readShort(); // fields count
        for (int i = 0; i < count; i++) {
            readShort(); // access flags
            readShort(); // name index
            readShort(); // descriptor index
            skipAttributes(); // field attributes
        }

        count = readShort(); // methods count
        for (int i = 0; i < count; i++) {
            readShort(); // access flags
            int m = readShort(); // name index
            String name = getUtf8(m);
            int d = readShort(); // descriptor index
            this.methodName = name + getUtf8(d);
            readAttributes(); // method attributes
        }

    }

    /**
     * @return the next unsigned 16 bit value
     */
    private int readShort() {
        return (read() << 8) | read();
    }

    /**
     * @return the next signed 32 bit value
     */
    private int readInt() {
        return (read() << 24) | (read() << 16) | (read() << 8) | read();
    }

    /**
     * skip n bytes in the input stream.
     */
    private void skipFully(int n) throws IOException {
        while (n > 0) {
            int c = (int) skip(n);
            if (c < 0)
                throw new EOFException(JavaUtils.getMessage("unexpectedEOF00"));
            n -= c;
        }
    }

    /**
     * the constant pool.  constant pool indices in the class file
     * directly index into this array.  The value stored in this array
     * is the position in the class file where that constant begins.
     */
    private int cpoolIndex[];

    private String getUtf8(int i) throws IOException {
        int oldPos = pos;
        try {
            pos = cpoolIndex[i];
            read(); //  CONSTANT_Utf8
            int len = readShort();
            skipFully(len);
            return new String(buf, pos - len, len, "utf-8");
        } finally {
            pos = oldPos;
        }
    }

    private void readCpool() throws IOException {
        int count = readShort(); // cpool count
        cpoolIndex = new int[count];
        for (int i = 1; i < count; i++) {
            cpoolIndex[i] = super.pos;

            int c = read();
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
                            JavaUtils.getMessage("unexpectedBytes00"));
            }
        }
    }

    private void skipAttributes() throws IOException {
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
    private void readAttributes() throws IOException {
        int count = readShort();
        for (int i = 0; i < count; i++) {
            int nameIndex = readShort(); // name index
            int attrLen = readInt();

            String attrName = getUtf8(nameIndex);

            if ("Code".equals(attrName)) {
                readShort(); // max stack
                readShort(); // max locals
                skipFully(readInt()); // code
                skipFully(8 * readShort()); // exception table

                // read the code attributes (recursive).  This is where
                // we will find the LocalVariableTable attribute.
                readAttributes();
            } else if ("LocalVariableTable".equals(attrName)) {
                int len = readShort(); // table length
                String[] names = null;
                if (methods != null && methodName != null) {
                    names = new String[len];
                    methods.put(methodName, names);
                }
                for (int j = 0; j < len; j++) {
                    readShort(); // start pc
                    readShort(); // length
                    int n = readShort(); // name_index
                    readShort(); // descriptor_index
                    int index = readShort(); // index

                    if (names != null) {
                        names[index] = getUtf8(n);
                    }
                }
            } else {
                // don't care what attribute this is
                skipFully(attrLen);
            }
        }
    }

    private void addDescriptor(StringBuffer b, Class c) {
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
     * return the names of the declared parameters for the given method.
     * If we cannot determine the names, return null.  The returned array will
     * have one name per parameter.  The length of the array will be the same
     * as the length of the Class[] array returned by Method.getParameterTypes().
     * @param method
     * @return String[] array of names, one per parameter, or null
     */
    public String[] getParameterNames(Method method) {
        Class[] paramTypes = method.getParameterTypes();

        // compute the method descriptor

        StringBuffer b = new StringBuffer(method.getName());
        b.append('(');

        for (int i = 0; i < paramTypes.length; i++) {
            addDescriptor(b, paramTypes[i]);
        }

        b.append(')');
        addDescriptor(b, method.getReturnType());

        // look up the names for this method
        String[] localNames = (String[]) methods.get(b.toString());

        // we know all the local variable names, but we only need to return
        // the names of the parameters.

        if (localNames != null) {
            String[] paramNames = new String[paramTypes.length];
            int j = Modifier.isStatic(method.getModifiers()) ? 0 : 1;

            for (int i = 0; i < paramNames.length; i++) {
                paramNames[i] = localNames[j++];
            }

            return paramNames;
        } else {
            return null;
        }
    }

}
