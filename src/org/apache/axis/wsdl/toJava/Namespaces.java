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
package org.apache.axis.wsdl.toJava;

import org.apache.axis.utils.JavaUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * This class is essentially a HashMap of <namespace, package name> pairs with
 * a few extra wizzbangs.
 */
public class Namespaces extends HashMap {

    /** Field root */
    private String root;

    /** Field defaultPackage */
    private String defaultPackage = null;

    /** Toknens in a namespace that are treated as package name part separators. */
    private static final char[] pkgSeparators = {'.', ':'};

    /** Field javaPkgSeparator */
    private static final char javaPkgSeparator = pkgSeparators[0];

    /**
     * Method normalizePackageName
     * 
     * @param pkg       
     * @param separator 
     * @return 
     */
    private static String normalizePackageName(String pkg, char separator) {

        for (int i = 0; i < pkgSeparators.length; i++) {
            pkg = pkg.replace(pkgSeparators[i], separator);
        }

        return pkg;
    }

    /**
     * Instantiate a Namespaces object whose packages will all reside under root.
     * 
     * @param root 
     */
    public Namespaces(String root) {

        super();

        this.root = root;
    }    // ctor

    /**
     * Instantiate a clone of an existing Namespaces object.
     * 
     * @param clone 
     */
    private Namespaces(Namespaces clone) {

        super(clone);

        this.root = clone.root;
        this.defaultPackage = clone.defaultPackage;
    }    // ctor

    /**
     * Instantiate a clone of this Namespaces object.
     * 
     * @return 
     */
    public Object clone() {
        return new Namespaces(this);
    }    // clone

    /**
     * Get the package name for the given namespace.  If there is no entry in the HashMap for
     * this namespace, create one.
     * 
     * @param key 
     * @return 
     */
    public String getCreate(String key) {
        return getCreate(key, true);
    }    // getCreate

    /**
     * Get the package name for the given namespace.  If there is no entry in the HashMap for
     * this namespace, create one if create flag is on, return <tt>null</tt> otherwise.
     * 
     * @param key    
     * @param create 
     * @return 
     */
    String getCreate(String key, boolean create) {

        if (defaultPackage != null) {
            return defaultPackage;
        }

        String value = (String) super.get(key);

        if ((value == null) && create) {
            value = normalizePackageName((String) Utils.makePackageName(key),
                    javaPkgSeparator);

            put(key, value);
        }

        return (String) value;
    }    // getCreate

    /**
     * Get the package name in directory format (dots replaced by slashes).  If the package name
     * doesn't exist in the HashMap, return "".
     * 
     * @param key 
     * @return 
     */
    public String getAsDir(String key) {

        if (defaultPackage != null) {
            return toDir(defaultPackage);
        }

        String pkg = (String) get(key);

        return toDir(pkg);
    }    // getAsDir

    /**
     * Return the given package name in directory format (dots replaced by slashes).  If pkg is null,
     * "" is returned.
     * 
     * @param pkg 
     * @return 
     */
    public String toDir(String pkg) {

        String dir = null;

        if (pkg != null) {
            pkg = normalizePackageName(pkg, File.separatorChar);
        }

        if (root == null) {
            dir = pkg;
        } else {
            dir = root + File.separatorChar + pkg;
        }

        return (dir == null)
                ? ""
                : dir + File.separatorChar;
    }    // toDir

    /**
     * Like HashMap's putAll, this adds the given map's contents to this map.  But it
     * also makes sure the value strings are javified.
     * 
     * @param map 
     */
    public void putAll(Map map) {

        Iterator i = map.entrySet().iterator();

        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            Object key = entry.getKey();
            String pkg = (String) entry.getValue();

            pkg = javify(pkg);

            put(key, pkg);
        }
    }    // putAll

    /**
     * Make sure each package name doesn't conflict with a Java keyword.
     * Ie., org.apache.import.test becomes org.apache.import_.test.
     * 
     * @param pkg 
     * @return 
     */
    private String javify(String pkg) {

        StringTokenizer st = new StringTokenizer(pkg, ".");

        pkg = "";

        while (st.hasMoreTokens()) {
            String token = st.nextToken();

            if (JavaUtils.isJavaKeyword(token)) {
                token = JavaUtils.makeNonJavaKeyword(token);
            }

            pkg = pkg + token;

            if (st.hasMoreTokens()) {
                pkg = pkg + '.';
            }
        }

        return pkg;
    }    // javify

    /**
     * Make a directory for the given package under root.
     * 
     * @param pkg 
     */
    public void mkdir(String pkg) {

        String pkgDirString = toDir(pkg);
        File packageDir = new File(pkgDirString);

        packageDir.mkdirs();
    }    // mkdir

    /**
     * Set a package name that overrides the namespace map
     * 
     * @param defaultPackage a java package name (e.g. com.foo)
     */
    public void setDefaultPackage(String defaultPackage) {
        this.defaultPackage = defaultPackage;
    }
}    // class Namespaces
