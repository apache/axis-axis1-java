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
package org.apache.axis.description;

import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.symbolTable.TypeEntry;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * A Parameter descriptor, collecting the interesting info about an
 * operation parameter.
 *
 * (mostly taken from org.apache.axis.wsdl.toJava.Parameter right now)
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class ParameterDesc implements Serializable {

    // constant values for the parameter mode.
    public static final byte IN = 1;
    public static final byte OUT = 2;
    public static final byte INOUT = 3;

    /** The Parameter's XML QName */
    private transient QName name;
    /** A TypeEntry corresponding to this parameter */
    public TypeEntry typeEntry;
    /** The Parameter mode (in, out, inout) */
    private byte mode = IN;
    /** The XML type of this parameter */
    private QName typeQName;
    /** The Java type of this parameter */
    private Class javaType = null;
    /** The order of this parameter (-1 indicates unordered) */
    private int order = -1;
    /** Indicates if this ParameterDesc represents a return or normal parameter **/
    private boolean isReturn = false;
    /** MIME type for this parameter, if there is one */
    private String mimeType = null;

    /** Indicates whether input/output values are stored in the header */
    private boolean inHeader = false;
    private boolean outHeader = false; 

    public ParameterDesc() {
    }

    /**
     * Constructor-copy
     *
     * @param copy the copy 
     */
    public ParameterDesc(ParameterDesc copy) {
        name = copy.name;
        typeEntry = copy.typeEntry;
        mode = copy.mode;
        typeQName = copy.typeQName;
        javaType = copy.javaType;
        order = copy.order;
        isReturn = copy.isReturn;
        mimeType = copy.mimeType;
        inHeader = copy.inHeader;
        outHeader = copy.outHeader;
    }

    /**
     * Constructor
     *
     * @param name the parameter's fully qualified XML name
     * @param mode IN, OUT, INOUT
     * @param typeQName the parameter's XML type QName
     */
    public ParameterDesc(QName name, byte mode, QName typeQName) {
        this.name = name;
        this.mode = mode;
        this.typeQName = typeQName;
    }

    /**
     * "Complete" constructor, suitable for usage in skeleton code
     *
     * @param name the parameter's fully qualified XML name
     * @param mode IN, OUT, INOUT
     * @param typeQName the parameter's XML type QName
     * @param javaType the parameter's javaType
     * @param inHeader does this parameter go into the input message header?
     * @param outHeader does this parameter go into the output message header?
     */
    public ParameterDesc(QName name, byte mode, QName typeQName,
            Class javaType, boolean inHeader, boolean outHeader) {
        this(name,mode,typeQName);
        this.javaType = javaType;
        this.inHeader = inHeader;
        this.outHeader = outHeader;
    }

    public String toString() {
        return toString("");
    }
    public String toString(String indent) {
        String text="";
        text+=indent + "name:       " + name + "\n";
        text+=indent + "typeEntry:  " + typeEntry + "\n";
        text+=indent + "mode:       " + (mode == IN ? 
                                         "IN" : mode == INOUT ? 
                                         "INOUT" : "OUT:  " 
                                         + "position:" + order) + "\n";
        text+=indent + "isReturn:   " + isReturn + "\n";
        text+=indent + "typeQName:  " + typeQName + "\n";
        text+=indent + "javaType:   " + javaType + "\n";
        text+=indent + "inHeader:   " + inHeader + "\n";
        text+=indent + "outHeader:  " + outHeader+ "\n";
        return text;
    } // toString
    
    /**
     * Get a mode constant from a string.  Defaults to IN, and returns
     * OUT or INOUT if the string matches (ignoring case).
     */ 
    public static byte modeFromString(String modeStr)
    {
        byte ret = IN;
        if (modeStr == null) { 
            return IN;
        } else if (modeStr.equalsIgnoreCase("out")) {
            ret = OUT;
        } else if (modeStr.equalsIgnoreCase("inout")) {
            ret = INOUT;
        }
        return ret;
    }
    
    public static String getModeAsString(byte mode)
    {
        if (mode == INOUT) {
            return "inout";
        } else if (mode == OUT) {
            return "out";
        } else if (mode == IN) {
            return "in";
        }
        
        throw new IllegalArgumentException(
                Messages.getMessage("badParameterMode", Byte.toString(mode)));
    }

    public QName getQName() {
        return name;
    }

    public String getName() {
        if (name == null) {
            return null;
        }
        else {
            return name.getLocalPart();
        }
    }

    public void setName(String name) {
        this.name = new QName("", name);
    }

    public void setQName(QName name) {
        this.name = name;
    }

    public QName getTypeQName() {
        return typeQName;
    }

    public void setTypeQName(QName typeQName) {
        this.typeQName = typeQName;
    }

    /** 
     * Get the java type (note that this is javaType in the signature.)
     * @return Class javaType
     */
    public Class getJavaType() {
        return javaType;
    }

    /** 
     * Set the java type (note that this is javaType in the signature.) 
     */
    public void setJavaType(Class javaType) {
        // The javaType must match the mode.  A Holder is expected for OUT/INOUT
        // parameters that don't represent the return type.
        if (javaType != null) {
            if ((mode == IN || isReturn) &&
                javax.xml.rpc.holders.Holder.class.isAssignableFrom(javaType) ||
                mode != IN && !isReturn &&
                !javax.xml.rpc.holders.Holder.class.isAssignableFrom(javaType)) {
                throw new IllegalArgumentException(
                     Messages.getMessage("setJavaTypeErr00", 
                                          javaType.getName(),
                                          getModeAsString(mode)));
            }             
        }

        this.javaType = javaType;
    }

    public byte getMode() {
        return mode;
    }

    public void setMode(byte mode) {
        this.mode = mode;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setInHeader(boolean value) {
        this.inHeader = value;
    }

    public boolean isInHeader() {
        return this.inHeader;
    }

    public void setOutHeader(boolean value) {
        this.outHeader = value;
    }

    public boolean isOutHeader() {
        return this.outHeader;
    }

    /**
     * Indicates ParameterDesc represents return of OperationDesc
     * @return true if return parameter of OperationDesc
     */
    public boolean getIsReturn() {
        return isReturn;
    }
    /**
     * Set to true to indicate return parameter of OperationDesc
     * @param value boolean that indicates if return parameter of OperationDesc
     */
    public void setIsReturn(boolean value) {
        isReturn = value;
    }

    private void writeObject(ObjectOutputStream out)
        throws IOException {
        if (name == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            out.writeObject(name.getNamespaceURI());
            out.writeObject(name.getLocalPart());
        }
        if (typeQName == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            out.writeObject(typeQName.getNamespaceURI());
            out.writeObject(typeQName.getLocalPart());
        }
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {
        if (in.readBoolean()) {
            name = new QName((String)in.readObject(),
                             (String)in.readObject());
        } else {
            name = null;
        }
        if (in.readBoolean()) {
            typeQName = new QName((String)in.readObject(),
                                  (String)in.readObject());
        } else {
            typeQName = null;
        }
        in.defaultReadObject();
    }

} // class ParameterDesc
