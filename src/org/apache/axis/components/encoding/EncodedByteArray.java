/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
package org.apache.axis.components.encoding;

import java.io.UnsupportedEncodingException;

/**
 *
 * Simple byte array with variable array length, used within the
 * XMLEncoder.
 *
 * It is used as a ByteArrayOutputStream replacement to limit
 * the size of created temporary objects
 *
 *
 * @author <a href="mailto:jens@void.fm">Jens Schumann</a>
 * @version $Id$
 *
 */
class EncodedByteArray {
    private byte[] array = null;
    private int pointer;
    
    private final double PADDING = 1.5;

    public EncodedByteArray(byte[] bytes, int startPos, int length) {
        // string length will be at least the size of the byte array
        array = new byte[(int) (bytes.length * PADDING)];
        System.arraycopy(bytes, startPos, array, 0, length);
        pointer = length;
    }

    public EncodedByteArray(int size) {
        array = new byte[size];
    }

    public void append(int aByte) {
        if (pointer + 1 >= array.length) {
            byte[] newArray = new byte[(int) (array.length * PADDING)];
            System.arraycopy(array, 0, newArray, 0, pointer);
            array = newArray;
        }
        array[pointer] = (byte) aByte;
        pointer++;
    }

    public void append(byte[] byteArray) {
        if (pointer + byteArray.length >= array.length) {
            byte[] newArray = new byte[((int)(array.length * PADDING)) + byteArray.length];
            System.arraycopy(array, 0, newArray, 0, pointer);
            array = newArray;
        }

        System.arraycopy(byteArray, 0, array, pointer, byteArray.length);
        pointer += byteArray.length;
    }

    public void append(byte[] byteArray, int pos, int length) {
        if (pointer + length >= array.length) {
            byte[] newArray = new byte[((int) (array.length * PADDING)) + byteArray.length];
            System.arraycopy(array, 0, newArray, 0, pointer);
            array = newArray;
        }
        System.arraycopy(byteArray, pos, array, pointer, length);
        pointer += length;
    }

    /**
     * convert to a string using the platform's default charset 
     * @return string
     */ 
    public String toString() {
        return new String(array, 0, pointer);
    }

    /**
     * convert the encoded byte array to a string according to the given charset 
     * @param charsetName
     * @return string
     * @throws UnsupportedEncodingException
     */ 
    public String toString(String charsetName) throws UnsupportedEncodingException {
        return new String(array, 0, pointer, charsetName);
    }
}
