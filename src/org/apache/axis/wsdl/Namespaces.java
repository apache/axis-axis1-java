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
package org.apache.axis.wsdl;

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
    private String root;

    /**
     * Instantiate a Namespaces object whose packages will all reside under root.
     */
    public Namespaces(String root) {
        super();
        this.root = root;
    } // ctor

    /**
     * Instantiate a clone of an existing Namespaces object.
     */
    private Namespaces(Namespaces clone) {
        super(clone);
        this.root = clone.root;
    } // ctor

    /**
     * Instantiate a clone of this Namespaces object.
     */
    public Object clone() {
        return new Namespaces(this);
    } // clone

    /**
     * Get the package name for the given namespace.  If there is no entry in the HashMap for
     * this namespace, create one.
     */
    public String getCreate(String key) {
        Object value = super.get(key);
        if (value == null) {
            value = Utils.makePackageName((String) key);
            put(key, value);
        }
        return (String) value;
    } // getCreate

    /**
     * Get the package name in directory format (dots replaced by slashes).  If the package name
     * doesn't exist in the HashMap, return "".
     */
    public String getAsDir(String key) {
        String pkg = (String) get(key);
        return toDir(pkg);
    } // getAsDir

    /**
     * Return the given package name in directory format (dots replaced by slashes).  If pkg is null,
     * "" is returned.
     */
    public String toDir(String pkg) {
        String dir = null;
        if (root == null) {
            dir = pkg;
        }
        else {
            dir = root + File.separatorChar + pkg;
        }

        if (dir == null || dir.equals("")) {
            return "";
        }
        else {
            return dir.replace('.', File.separatorChar) + File.separatorChar;
        }
    } // toDir

    /**
     * Like HashMap's putAll, this replaces the existing contents with the new contents.  But it
     * also makes a directory for each package name.
     */
    public void putAll(Map map) {
        super.clear();
        Iterator i = map.keySet().iterator();
        while (i.hasNext()) {
            Object key = i.next();
            String pkg = (String) map.get(key);
            pkg = javify(pkg);
            put(key, pkg);
        }
    } // putAll

    /**
     * Make sure each package name doesn't conflict with a Java keyword.
     * Ie., org.apache.import.test becomes org.apache.import_.test.
     */
    private String javify(String pkg) {
        StringTokenizer st = new StringTokenizer(pkg, ".");
        pkg = "";
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (Utils.isJavaKeyword(token)) {
                token = Utils.makeNonJavaKeyword(token);
            }
            pkg = pkg + token;
            if (st.hasMoreTokens()) {
                pkg = pkg + '.';
            }
        }
        return pkg;
    } // javify

    /**
     * Make a directory for the given package under root.
     */
    public void mkdir(String pkg) {
        String pkgDirString = toDir(pkg);
        File packageDir = new File(pkgDirString);
        packageDir.mkdirs();
    } // mkdir
} // class Namespaces
