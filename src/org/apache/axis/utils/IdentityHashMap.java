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
package org.apache.axis.utils;

import org.apache.axis.AxisFault;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * IdentityHashMap is like HashMap except that it uses the
 * System identityHashCode of the keys to guarantee uniqueness.
 *
 * @author Rich Scheuerle
 */
public class IdentityHashMap
{
    private HashMap keysMap = new HashMap();
    private HashMap valuesMap = new HashMap();

    /** 
     * Default Constructor.  
     */
    public IdentityHashMap() { 
        keysMap = new HashMap();
        valuesMap = new HashMap();        
    }

    /** 
     * Clear the map  
     */
    public void clear() {
        keysMap.clear();
        valuesMap.clear();
    }

    /** 
     * Clone the map
     * @return cloned IdentityHashMap
     */
    public Object clone() {
        IdentityHashMap newMap = new IdentityHashMap();
        newMap.keysMap = (HashMap) keysMap.clone();
        newMap.valuesMap = (HashMap) valuesMap.clone();
        return newMap;
    }

    /** 
     * Query if map contains key
     * @param Object key
     * @return boolean indicating if map contains key
     */
    public boolean containsKey(Object key) {
        return keysMap.containsKey(System.identityHashCode(key) + "");
    }

    /** 
     * Query if map contains value
     * @param Object value
     * @return boolean indicating if map contains value
     */
    public boolean containsValue(Object value) {
        return valuesMap.containsValue(value);
    }

    /**
     * Get value for a particular key
     * @param Object key
     * @return Object value or null
     */
    public Object get(Object key) {
        return valuesMap.get(System.identityHashCode(key) + "");
    }

    /**
     * Query if map is empty
     * @return boolean indicating if map is empty
     */
    public boolean isEmpty() {      
        return valuesMap.isEmpty();
    }

    /**
     * Get the collection of keys.
     * @return Collection of keys
     */
    public Collection keys() {
        return keysMap.values();
    }

    /**
     * Put value for a particular key
     * @param Object key
     * @param Object value
     */
    public void put(Object key, Object value) {
        keysMap.put(System.identityHashCode(key) + "", key);
        valuesMap.put(System.identityHashCode(key) + "", value);
    }

    /**
     * Remove mapping for a particular key
     * @param Object key
     * @return Object previous value or null
     */
    public Object remove(Object key) {
        keysMap.remove(System.identityHashCode(key) + "");
        return valuesMap.remove(System.identityHashCode(key) + "");
    }

    /**
     * Get the size
     * @return Collection of keys
     */
    public int size() {
        return keysMap.size();
    }


    /**
     * Get the collection of values.
     * @return Collection of values
     */
    public Collection values() {
        return valuesMap.values();
    }


}
