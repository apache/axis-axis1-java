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
package org.apache.axis.wsdl.fromJava;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * <p>Description: A HashMap of packageNames and namespaces with some helper methods </p>
 *
 * @author rkumar@borland.com
 */
public class Namespaces extends HashMap {
    private int prefixCount = 1;
    private HashMap namespacePrefixMap = null;

    public Namespaces() {
        super();
        namespacePrefixMap  = new HashMap();
    }

    /**
     * Get the namespaace for the given package  If there is no entry in the HashMap for
     * this namespace, create one.
     * @param key String representing packagename
     * @return the namespace either created or existing
     */
    public String getCreate(String key) {
        Object value = super.get(key);
        if (value == null) {
            value = makeNamespaceFromPackageName(key);
            put(key, value, null);
        }
        return (String) value;
    }

    /**
     * Get the namespaace for the given package  If there is no entry in the HashMap for
     * this namespace, create one.
     * @param key String representing packagename
     * @param prefix the prefix to use for the generated namespace
     * @return the namespace either created or existing
     */
    public String getCreate(String key, String prefix) {
        Object value = super.get(key);
        if (value == null) {
            value = makeNamespaceFromPackageName(key);
            put(key, value, prefix);
        }
        return (String) value;
    }

    /**
     * adds an entry to the packagename/namespace HashMap.  In addition,
     * also makes an entry in the auxillary namespace/prefix HashMap if an
     * entry doesn't already exists
     * @param key packageName String
     * @param value namespace value
     * @param prefix the prefix to use for the given namespace
     * @return old value for the specified key
     */
    public Object put(Object key, Object value, String prefix) {
        if (prefix != null)
            namespacePrefixMap.put(value, prefix);
        else
            getCreatePrefix((String)value);
        return super.put(key, value);
    }

    /**
     * adds an entry to the packagename/namespace HashMap
     * for each of the entry in the map.  In addition, also add an entries in the
     * auxillary namespace/prefix HashMap
     * @param map packageName/namespace map
     */
    public void putAll(Map map) {
        Iterator i = map.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            put(entry.getKey(), entry.getValue(), null);
        }
    }

    /**
     * Get the prefix for the given namespace. If one exists, create one
     * @param namespace namespace
     * @return prefix String
     */
    public String getCreatePrefix(String namespace) {
        if (namespacePrefixMap.get(namespace) == null) {
            namespacePrefixMap.put(namespace, "tns" + prefixCount++);
        }
        return (String)namespacePrefixMap.get(namespace);
    }

    /**
     * put the gine namespace / prefix into the appropriate HashMap
     * @param namespace
     * @param prefix
     */
    public void putPrefix(String namespace, String prefix) {
        namespacePrefixMap.put(namespace, prefix);
    }

    /**
     * adds an entry to the namespace / prefix HashMap
     * for each of the entry in the map.
     *
     * @param map packageName/namespace map
     */
    public void putAllPrefix(Map map) {
        Iterator i = map.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Make namespace from a fully qualified class name
     * use the default protocol for the namespace
     *
     * @param clsName fully qualified class name
     * @return namespace namespace String
     */
    public static String makeNamespace (String clsName) {
        return makeNamespace(clsName, "http");
    }

    /**
     * Make namespace from a fully qualified class name
     * and the given protocol
     *
     * @param clsName fully qualified class name
     * @param protocol protocol String
     * @return namespace namespace String
     */
    public static String makeNamespace (String clsName, String protocol) {
        if ( clsName.startsWith("[L") )
            clsName = clsName.substring( 2, clsName.length() - 1 );
            
        if (clsName.lastIndexOf('.') == -1)
            return protocol + "://" + "DefaultNamespace";
        String packageName = clsName.substring(0, clsName.lastIndexOf('.'));
        return makeNamespaceFromPackageName(packageName, protocol);       
    }

    private static String makeNamespaceFromPackageName(String packageName) {
      return makeNamespaceFromPackageName(packageName, "http");       
    }
    
    
    private static String makeNamespaceFromPackageName(String packageName, String protocol) {
        if (packageName == null || packageName.equals(""))
            return protocol + "://" + "DefaultNamespace";
        StringTokenizer st = new StringTokenizer( packageName, "." );
        String[] words = new String[ st.countTokens() ];
        for(int i = 0; i < words.length; ++i)
            words[i] = st.nextToken();

        StringBuffer sb = new StringBuffer(80);
        for(int i = words.length-1; i >= 0; --i) {
            String word = words[i];
            // seperate with dot
            if( i != words.length-1 )
                sb.append('.');
            sb.append( word );
        }
        return protocol + "://" + sb.toString();
    }
    
    /**
     * Get the list of namespaces currently registered
     * @return iterator
     */ 
    public Iterator getNamespaces() {
        return namespacePrefixMap.keySet().iterator();            
    }
}
