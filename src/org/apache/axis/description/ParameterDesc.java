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

import org.apache.axis.wsdl.toJava.TypeEntry;
import org.apache.axis.utils.JavaUtils;

import javax.xml.rpc.namespace.QName;
import java.util.Vector;

/**
 * A Parameter descriptor, collecting the interesting info about an
 * operation parameter.
 *
 * (mostly taken from org.apache.axis.wsdl.toJava.Parameter right now)
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class ParameterDesc {

    // constant values for the parameter mode.
    public static final byte IN = 1;
    public static final byte OUT = 2;
    public static final byte INOUT = 3;

    /** The Parameter's XML QName */
    private QName name;
    /** A TypeEntry corresponding to this parameter */
    public TypeEntry typeEntry;
    /** The Parameter mode (in, out, inout) */
    public byte mode = IN;
    /** The XML type of this parameter */
    private QName typeQName;
    /** The Java type of this parameter */
    private Class javaType = null;
    /** The order of this parameter (-1 indicates unordered) */
    private int order = -1;

    public ParameterDesc() {
    }

    /**
     * "Complete" constructor, suitable for usage in skeleton code
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

    public String toString() {
        return "(" + typeEntry + ", " + getName() + ", "
                + (mode == IN ? "IN)" : mode == INOUT ? "INOUT)" : "OUT)" + "position:" + order);
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
                JavaUtils.getMessage("badParameterMode", Byte.toString(mode)));
    }

    public QName getQName() {
        return name;
    }

    public String getName() {
        return name.getLocalPart();
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

    public Class getJavaType() {
        return javaType;
    }

    public void setJavaType(Class javaType) {
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
} // class Parameter
