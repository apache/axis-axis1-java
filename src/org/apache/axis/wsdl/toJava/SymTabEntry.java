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

package org.apache.axis.wsdl.toJava;

import java.util.HashMap;
import java.util.Iterator;

import javax.wsdl.QName;

/**
* SymTabEntry is the base class for all symbol table entries.  It contains four things:
* - a QName
* - space for a Writer-specific name (for example, in Wsdl2java, this will be the Java name)
* - isReferenced flag indicating whether this entry is referenced by other entries
* - dynamicVars; a mechanism for Writers to add additional context information onto entries.
*/
public abstract class SymTabEntry {
    // The QName of this entry is immutable.  There is no setter for  it.
    protected QName qname;

    // The name is Writer implementation dependent.  For example, in Wsdl2java, this will become
    // the Java name.
    protected String name;

    // Is this entry referenced by any other entry?
    private boolean isReferenced = false;

    private HashMap dynamicVars = new HashMap();

    protected SymTabEntry(QName qname) {
        this.qname = qname;
    } // ctor

    protected SymTabEntry(QName qname, String name) {
        this.qname = qname;
        this.name = name;
    } // ctor

    /**
     * Get the QName of this entry.
     */
    public QName getQName() {
        return qname;
    } // getQName

    /**
     * Get the name of this entry.  The name is Writer-implementation-dependent.  For example, in
     * Wsdl2java, this will become the Java name.
     */
    public String getName() {
        return name;
    } // getName

    /**
     * Set the name of this entry.  This method is not called by the framework, it is only called
     * by the Writer implementation.
     */
    public void setName(String name) {
        this.name = name;
    } // setName

    /**
     * Is this entry referenced by any other entry in the symbol table?
     */
    public boolean isReferenced() {
        return isReferenced;
    } // isReferenced

    /**
     * Set the isReferenced variable, default value is true.
     */
    public void setIsReferenced(boolean isReferenced) {
        this.isReferenced = isReferenced;
    } // setIsReferenced

    /**
     * There may be information that does not exist in WSDL4J/DOM structures and does not exist in
     * our additional structures, but that thw Writer implementation will need.  This information is
     * most likely context-relative, so the DynamicVar map is provided for the Writers to store/
     * retrieve their particular information.
     */
    public Object getDynamicVar(Object key) {
        return dynamicVars.get(key);
    } // getDynamicVar

    public void setDynamicVar(Object key, Object value) {
        dynamicVars.put(key, value);
    } // setDynamicVar

    /**
     * Collate the info in this object in string form.
     */
    public String toString() {
        String string =
                "QName:         " + qname + '\n' + 
                "name:          " + name + '\n' + 
                "isReferenced?  " + isReferenced + '\n';
        String prefix = "dynamicVars:   ";
        Iterator keys = dynamicVars.keySet().iterator();
        while (keys.hasNext()) {
            Object key = keys.next();
            string += prefix + key + " = " + dynamicVars.get(key) + '\n';
            prefix = "               ";
        }
        return string;
    } // toString
} // abstract class SymTabEntry
