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


import org.w3c.dom.Node;

import javax.wsdl.QName;

/**
 * This class represents a type that is supported by the WSDL2Java emitter.
 * A Type has a QName representing its XML name and a Java Name, which
 * is its full java name.  The Type may also have a Node, which locates
 * the definition of the emit type in the xml.  
 * An Type is created by the TypeFactory object.
 *
 * @author Rich Scheuerle  (scheu@us.ibm.com)
 */
public class Type {

    private QName  qName;        // QName of the element type
    private String jName;        // Name of the Java Mapping (Class or primitive)
    private Node   node;         // Element node  
    private boolean isBaseType;  // Indicates if represented by a java primitive or util class

    /**
     * Create an Emit Type with 
     */
    public Type(QName pqName, String pjName, Node pnode) {
        qName = pqName;
        node  = pnode;
        // If the qName represents a base type, we override the 
        // java name with the name of the base java name.
        jName = initBaseType();
        if (jName == null) {
            jName = pjName;
            isBaseType = false;
        } else {
            isBaseType = true;
        }
    }

    /**
     * Query QName
     */
    public QName getQName() {
        return qName;
    }

    /**
     * Query Java Mapping Name
     */
    public String getJavaName() {
        return jName;
    }

    /**
     * Query Java Local Name
     */
    public String getJavaLocalName() {
        return jName.substring(jName.lastIndexOf('.')+1);
    }

    /**
     * Query Java Package Name
     */
    public String getJavaPackageName() {
        if (jName.lastIndexOf('.') > 0) {
            return jName.substring(0, jName.lastIndexOf('.'));
        }
        else {
            return "";
        }
    }

    /**
     * Query Java Mapping Name
     */
    public Node getNode() {
        return node;
    }

    /**
     * Query whether a Node defining the type exists.
     */
    public boolean isDefined() {
        return (node != null);
    }

    /**
     * Returns the Java Base Type Name.
     * For example if the Type represents a schema integer, "int" is returned.
     * If this is a user defined type, null is returned.
     */
    public String getBaseType() {
        if (isBaseType) {
            return jName;
        }
        else {
            return null;
        }
    }

    /**
     * Private method which returns the appropriate java type for each base type supported.
     */
    private String initBaseType() {
        String localName = qName.getLocalPart();
        if (Utils.isSchemaNS(qName.getNamespaceURI())) {
            if (localName.equals("string"))
                return "java.lang.String";
            else if (localName.equals("integer"))
                return "int";
            else if (localName.equals("int"))
                return "int";
            else if (localName.equals("long"))
                return "long";
            else if (localName.equals("short"))
                return "short";
            else if (localName.equals("decimal"))
                return "java.math.BigDecimal";
            else if (localName.equals("float"))
                return "float";
            else if (localName.equals("double"))
                return "double";
            else if (localName.equals("boolean"))
                return "boolean";
            else if (localName.equals("byte"))
                return "byte";
            else if (localName.equals("QName"))
                return "org.apache.axis.rpc.namespace.QName";
            else if (localName.equals("dateTime"))
                return "java.util.Date";
            else if (localName.equals("base64Binary"))
                return "byte[]";
            else if (localName.equals("date"))
                return "java.util.Date";
            else if (localName.equals("void"))
                return "void";
        }
        else if (Utils.isSoapEncodingNS(qName.getNamespaceURI())) {
            if (localName.equals("string"))
                return "java.lang.String";
            else if (localName.equals("int"))
                return "int";
            else if (localName.equals("short"))
                return "short";
            else if (localName.equals("decimal"))
                return "java.math.BigDecimal";
            else if (localName.equals("float"))
                return "float";
            else if (localName.equals("double"))
                return "double";
            else if (localName.equals("boolean"))
                return "boolean";
        }
        return null;
    }
}




