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

    /** The documentation for the parameter */
	private String documentation = null;


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

    /**
     * @deprecated
     * @param name the parameter's fully qualified XML name
     * @param mode IN, OUT, INOUT
     * @param typeQName the parameter's XML type QName
     * @param javaType the parameter's javaType
     */
    public ParameterDesc(QName name, byte mode, QName typeQName, Class javaType) {
      this(name,mode,typeQName,javaType,false,false);
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
                                         "INOUT" : "OUT") + "\n"; 
        text+=indent + "position:   " + order + "\n";
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

    /**
     * get the documentation for the parameter
     */
	public String getDocumentation() {
    	return documentation; 
    }

    /**
     * set the documentation for the parameter
     */
	public void setDocumentation(String documentation) {
    	this.documentation = documentation;
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
