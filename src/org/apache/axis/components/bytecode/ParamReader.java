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

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * this is the class file reader for adjusting the line number
 * table in the class file.  it can fetch the class name and
 * also fetch method param names. <p>
 *
 * don't get tricky, it's the bare minimum.
 *
 * @author Edwin Smith, Macromedia Inc.
 */
public class ParamReader extends FilterInputStream {
    // constants values that appear in java class files,
    // from jvm spec 2nd ed, section 4.4, pp 103
    static final int CONSTANT_Class = 7;
    static final int CONSTANT_Fieldref = 9;
    static final int CONSTANT_Methodref = 10;
    static final int CONSTANT_InterfaceMethodref = 11;
    static final int CONSTANT_String = 8;
    static final int CONSTANT_Integer = 3;
    static final int CONSTANT_Float = 4;
    static final int CONSTANT_Long = 5;
    static final int CONSTANT_Double = 6;
    static final int CONSTANT_NameAndType = 12;
    static final int CONSTANT_Utf8 = 1;

    private String className;
    private String methodName;
    private Map methods;

    public ParamReader(InputStream in) throws IOException {
        super(in);
    }

    /**
     * @return the next unsigned 16 bit value
     */
    private int readShort() throws IOException {
        return (read() << 8) | read();
    }

    /**
     * @return the next signed 32 bit value
     */
    private int readInt() throws IOException {
        return (read() << 24) | (read() << 16) | (read() << 8) | read();
    }

    /**
     * skip n bytes in the input stream.
     */
    private void skipFully(int n) throws IOException {
        while (n > 0) {
            int c = (int) skip(n);
            if (c < 0)
                throw new EOFException("Unexcepted EOF");
            n -= c;
        }
    }

    /**
     * the constant pool.  constant pool indices in the class file
     * directly index into this array.  The object stored in the
     * array will reflect the type of constant: String, Integer, etc.
     */
    private Object cpool[];

    private void readCpool() throws IOException {
        int count = readShort(); // cpool count
        cpool = new Object[count];
        for (int i = 1; i < count; i++) {
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
                    {
                        int index = readShort(); // string index or class index
                        cpool[i] = new Integer(index);
                    }
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
                    //cpool[i] = new String(buf, pos - len, len, "utf-8");
                    byte[] b = new byte[len];
                    read(b);
                    cpool[i] = new String(b);
                    break;

                default:
                    // corrupt class file
                    throw new IllegalStateException("unexpected constant #" + i + " " + c);
            }
        }
    }

    /**
     * read an attributes array.  the elements of a class file that
     * can contain attributes are: fields, methods, the class itself,
     * and some other types of attributes.
     */
    private void readAttributes(ArrayList lineNumberTable) throws IOException {
        int count = readShort();
        for (int i = 0; i < count; i++) {
            int nameIndex = readShort(); // name index
            int attrLen = readInt();

            String attrName = (String) cpool[nameIndex];

            if ("Code".equals(attrName)) {
                readShort(); // max stack
                readShort(); // max locals
                skipFully(readInt()); // code
                skipFully(8 * readShort()); // exception table

                // read the code attributes (recursive).  This is where
                // we will find the LineNumberTable attribute.
                readAttributes(lineNumberTable);
            } else if ("LocalVariableTable".equals(attrName)) {
                int len = readShort(); // table length
                String[] names = null;
                if (methods != null) {
                    names = new String[len];
                }
                for (int j = 0; j < len; j++) {
                    readShort(); // start pc
                    readShort(); // length
                    int n = readShort(); // name_index
                    readShort(); // descriptor_index
                    int index = readShort(); // index

                    if (names != null) {
                        names[index] = (String) cpool[n];
                    }
                }

                if (methods != null) {
                    methods.put(methodName, names);
                }
            } else {
                // don't care what attribute this is
                skipFully(attrLen);
            }
        }
    }

    private void readBeginning() throws IOException {
        // check the magic number
        if (readInt() != 0xCAFEBABE) {
            // not a class file!
            throw new IOException("this does not appear to be a valid class file");
        }

        readShort(); // minor
        readShort(); // major

        readCpool();

        readShort(); // access flags

        int i = readShort(); // this class
        i = ((Integer) cpool[i]).intValue();
        className = (String) cpool[i];

        readShort(); // super class
    }

    public String getClassName() throws IOException {
        readBeginning();
        return className;
    }

    public String[] getArgNames(String method) throws IOException {
        methods = new HashMap();
        readBeginning();

        int count = readShort(); // ifaces count
        for (int i = 0; i < count; i++) {
            readShort(); // interface index
        }

        count = readShort(); // fields count
        for (int i = 0; i < count; i++) {
            readShort(); // access flags
            readShort(); // name index
            readShort(); // descriptor index
            readAttributes(null); // field attributes
        }

        count = readShort(); // methods count
        for (int i = 0; i < count; i++) {
            readShort(); // access flags
            int m = readShort(); // name index
            this.methodName = (String) cpool[m];
            int d = readShort(); // descriptor index
            readAttributes(null); // method attributes
        }

        // class attributes
        readAttributes(null);

        return (String[]) methods.get(method);
    }
}
