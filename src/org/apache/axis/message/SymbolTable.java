/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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

package org.apache.axis.message;

import org.apache.axis.AxisInternalServices;
import org.apache.commons.logging.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This was swiped from xerces2, I stripped the comments
 * out to make it easier for me to see what was going on,
 * I'll add them back in later
 */
public class SymbolTable
{
    protected static Log log =
        AxisInternalServices.getLog(SymbolTable.class.getName());

    protected static final int TABLE_SIZE = 101;
    protected Entry[] fBuckets = new Entry[TABLE_SIZE];
    public SymbolTable() {}
    
    private ArrayList list = new ArrayList();
    private HashMap hash = new HashMap();
    
    public String getSymbol(int bucket) {
        return (String)list.get(bucket);
        /*
        return fBuckets[bucket].symbol;
        */
    }
    
    public int addSymbol(String symbol) {
        Integer i = (Integer)hash.get(symbol);
        int ret;
        if (i == null) {
            list.add(symbol);
            ret = list.size() - 1;
            hash.put(symbol, new Integer(ret));
        } else {
            ret = i.intValue();
        }
        return ret;
        /*
        int bucket = hash(symbol) % TABLE_SIZE;
        int n = 0;
        OUTER: for (Entry entry = fBuckets[bucket]; entry != null; entry = entry.next) {
            n++;
            int length = symbol.length();
            if (length == entry.characters.length) {
                for (int i = 0; i < length; i++) {
                    if (symbol.charAt(i) != entry.characters[i]) {
                        continue OUTER;
                    }
                }
                return bucket;
            }
        }
        Entry entry = new Entry(symbol, fBuckets[bucket]);
        fBuckets[bucket] = entry;
        log.debug("Symbol '" + symbol + "' is index " + bucket);
        return bucket;
        */
    }
    public int addSymbol(char[] buffer, int offset, int length) {
        return addSymbol(new String(buffer, offset, length));
        /*
        int bucket = hash(buffer, offset, length) % TABLE_SIZE;
        int n = 0;
        OUTER: for (Entry entry = fBuckets[bucket]; entry != null; entry = entry.next) {
            n++;
            if (length == entry.characters.length) {
                for (int i = 0; i < length; i++) {
                    if (buffer[offset + i] != entry.characters[i]) {
                        continue OUTER;
                    }
                }
                return bucket;
            }
        }
        Entry entry = new Entry(buffer, offset, length, fBuckets[bucket]);
        fBuckets[bucket] = entry;
        log.debug("Symbol '" + entry.symbol + "' is index " + bucket);
        return bucket;
        */
    }
    public int hash(String symbol) {
        int code = 0;
        int length = symbol.length();
        for (int i = 0; i < length; i++) {
            code = code * 37 + symbol.charAt(i);
        }
        return code & 0x7FFFFFF;
    }
    public int hash(char[] buffer, int offset, int length) {
        int code = 0;
        for (int i = 0; i < length; i++) {
            code = code * 37 + buffer[offset + i];
        }
        return code & 0x7FFFFFF;
    }
    protected static final class Entry {
        public String symbol;
        public char[] characters;
        public Entry next;
        public Entry(String symbol, Entry next) {
            this.symbol = symbol;
            characters = new char[symbol.length()];
            symbol.getChars(0, characters.length, characters, 0);
            this.next = next;
        }
        public Entry(char[] ch, int offset, int length, Entry next) {
            characters = new char[length];
            System.arraycopy(ch, offset, characters, 0, length);
            symbol = new String(characters);
            this.next = next;
        }
    }
}
