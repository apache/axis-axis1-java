/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
