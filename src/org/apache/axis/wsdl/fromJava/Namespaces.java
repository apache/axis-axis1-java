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

    /** Field prefixCount */
    private int prefixCount = 1;

    /** Field namespacePrefixMap */
    private HashMap namespacePrefixMap = null;

    /**
     * Constructor Namespaces
     */
    public Namespaces() {

        super();

        namespacePrefixMap = new HashMap();
    }

    /**
     * Get the namespaace for the given package  If there is no entry in the HashMap for
     * this namespace, create one.
     * 
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
     * 
     * @param key    String representing packagename
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
     * 
     * @param key    packageName String
     * @param value  namespace value
     * @param prefix the prefix to use for the given namespace
     * @return old value for the specified key
     */
    public Object put(Object key, Object value, String prefix) {

        if (prefix != null) {
            namespacePrefixMap.put(value, prefix);
        } else {
            getCreatePrefix((String) value);
        }

        return super.put(key, value);
    }

    /**
     * adds an entry to the packagename/namespace HashMap
     * for each of the entry in the map.  In addition, also add an entries in the
     * auxillary namespace/prefix HashMap
     * 
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
     * 
     * @param namespace namespace
     * @return prefix String
     */
    public String getCreatePrefix(String namespace) {

        if (namespacePrefixMap.get(namespace) == null) {
            namespacePrefixMap.put(namespace, "tns" + prefixCount++);
        }

        return (String) namespacePrefixMap.get(namespace);
    }

    /**
     * put the gine namespace / prefix into the appropriate HashMap
     * 
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
    public static String makeNamespace(String clsName) {
        return makeNamespace(clsName, "http");
    }

    /**
     * Make namespace from a fully qualified class name
     * and the given protocol
     * 
     * @param clsName  fully qualified class name
     * @param protocol protocol String
     * @return namespace namespace String
     */
    public static String makeNamespace(String clsName, String protocol) {

        if (clsName.startsWith("[L")) {
            clsName = clsName.substring(2, clsName.length() - 1);
        }

        if (clsName.lastIndexOf('.') == -1) {
            return protocol + "://" + "DefaultNamespace";
        }

        String packageName = clsName.substring(0, clsName.lastIndexOf('.'));

        return makeNamespaceFromPackageName(packageName, protocol);
    }

    /**
     * Method makeNamespaceFromPackageName
     * 
     * @param packageName 
     * @return 
     */
    private static String makeNamespaceFromPackageName(String packageName) {
        return makeNamespaceFromPackageName(packageName, "http");
    }

    /**
     * Method makeNamespaceFromPackageName
     * 
     * @param packageName 
     * @param protocol    
     * @return 
     */
    private static String makeNamespaceFromPackageName(String packageName,
                                                       String protocol) {

        if ((packageName == null) || packageName.equals("")) {
            return protocol + "://" + "DefaultNamespace";
        }

        StringTokenizer st = new StringTokenizer(packageName, ".");
        String[] words = new String[st.countTokens()];

        for (int i = 0; i < words.length; ++i) {
            words[i] = st.nextToken();
        }

        StringBuffer sb = new StringBuffer(80);

        for (int i = words.length - 1; i >= 0; --i) {
            String word = words[i];

            // seperate with dot
            if (i != words.length - 1) {
                sb.append('.');
            }

            sb.append(word);
        }

        return protocol + "://" + sb.toString();
    }

    /**
     * Get the list of namespaces currently registered
     * 
     * @return iterator
     */
    public Iterator getNamespaces() {
        return namespacePrefixMap.keySet().iterator();
    }
}
