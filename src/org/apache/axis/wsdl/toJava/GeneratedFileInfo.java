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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * File info available after emit to describe what
 * exactly was created by the Emitter.
 * 
 * @author Tom Jordahl (tomj@macromedia.com)
 */
public class GeneratedFileInfo {

    /** Field list */
    protected ArrayList list = new ArrayList();

    /**
     * Structure to hold entries.
     * There are three public data members:
     * <ul>
     * <li><code>fileName</code> - A relative path of the generated file.</li>
     * <li><code>className</code> - The name of the class in the file.</li>
     * <li><code>type</code> - The type of the file.<br>
     * Valid types are:<br>
     * <code>
     * stub, interface, complexType, enumType, fault, holder, skeleton,
     * skeletonImpl, service, deploy, undeploy, testCase
     * </code></li>
     * </ul>
     */
    public class Entry {

        // relative path of the emitted file

        /** Field fileName */
        public String fileName;

        // name of emitted class

        /** Field className */
        public String className;

        // function of the emitted class

        /** Field type */
        public String type;

        /**
         * Constructor Entry
         * 
         * @param name      
         * @param className 
         * @param type      
         */
        public Entry(String name, String className, String type) {

            this.fileName = name;
            this.className = className;
            this.type = type;
        }

        /**
         * Method toString
         * 
         * @return 
         */
        public String toString() {
            return "Name: " + fileName + " Class: " + className + " Type: "
                    + type;
        }
    }    // Entry

    /**
     * Construct an empty file info list.
     */
    public GeneratedFileInfo() {
    }

    /**
     * Return the entire list of generated files
     * 
     * @return 
     */
    public List getList() {
        return list;
    }

    /**
     * Add an entry
     * 
     * @param name      
     * @param className 
     * @param type      
     */
    public void add(String name, String className, String type) {
        list.add(new Entry(name, className, type));
    }

    /**
     * Lookup an entry by type.
     * <br>
     * Valid type values are:
     * stub, interface, complexType, enumType, fault, holder, skeleton,
     * skeletonImpl, service, deploy, undeploy, testCase
     * 
     * @param type of objects you want info about
     * @return A list of <code>org.apache.axis.wsdl.toJava.GeneratedFileInfo.Entry</code> objects.  Null if no objects found.
     */
    public List findType(String type) {

        // look at each entry for the type we want
        ArrayList ret = null;

        for (Iterator i = list.iterator(); i.hasNext();) {
            Entry e = (Entry) i.next();

            if (e.type.equals(type)) {
                if (ret == null) {
                    ret = new ArrayList();
                }

                ret.add(e);
            }
        }

        return ret;
    }

    /**
     * Lookup an entry by file name
     * 
     * @param file     name you want info about
     * @param fileName 
     * @return The entry for the file name specified.  Null if not found
     */
    public Entry findName(String fileName) {

        // look at each entry for the type we want
        for (Iterator i = list.iterator(); i.hasNext();) {
            Entry e = (Entry) i.next();

            if (e.fileName.equals(fileName)) {
                return e;
            }
        }

        return null;
    }

    /**
     * Lookup an entry by class name
     * 
     * @param class     name you want info about
     * @param className 
     * @return The entry for the class specified.  Null if not found
     */
    public Entry findClass(String className) {

        // look at each entry for the type we want
        for (Iterator i = list.iterator(); i.hasNext();) {
            Entry e = (Entry) i.next();

            if (e.className.equals(className)) {
                return e;
            }
        }

        return null;
    }

    /**
     * Get the list of generated classes
     * 
     * @return 
     */
    public List getClassNames() {

        // is there a better way to do this?
        ArrayList ret = new ArrayList(list.size());

        for (Iterator i = list.iterator(); i.hasNext();) {
            Entry e = (Entry) i.next();

            ret.add(e.className);
        }

        return ret;
    }

    /**
     * Get the list of generated filenames
     * 
     * @return 
     */
    public List getFileNames() {

        // is there a better way to do this?
        ArrayList ret = new ArrayList(list.size());

        for (Iterator i = list.iterator(); i.hasNext();) {
            Entry e = (Entry) i.next();

            ret.add(e.fileName);
        }

        return ret;
    }

    /**
     * Convert all entries in the list to a string
     * 
     * @return 
     */
    public String toString() {

        String s = "";

        for (Iterator i = list.iterator(); i.hasNext();) {
            Entry entry = (Entry) i.next();

            s += entry.toString() + "\n";
        }

        return s;
    }
}
