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
package org.apache.axis.message;

import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.description.ParameterDesc;

import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

import javax.xml.namespace.QName;

import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/** An RPC parameter
 *
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class RPCParam implements Serializable
{
    protected static Log log =
        LogFactory.getLog(RPCParam.class.getName());

    // Who's your daddy?
    RPCElement myCall;
    
    private transient QName qname;
    private Object value = null;
    private int countSetCalls = 0; // counts number of calls to set

    private ParameterDesc paramDesc;

    /**
     * Do we definitely want (or don't want) to send xsi:types?  If null
     * (the default), just do whatever our SerializationContext is configured
     * to do.  If TRUE or FALSE, the SerializationContext will do what we
     * want.
     */
    private Boolean wantXSIType = null;

    private static Method valueSetMethod;
    static {
        Class cls = RPCParam.class;
        try {
            valueSetMethod = cls.getMethod("set", new Class[] {Object.class});
        } catch (NoSuchMethodException e) {
            log.error(JavaUtils.getMessage("noValue00", "" + e));
            System.exit(-1);
        }
    }

    /** Constructor for building up messages.
     */
    public RPCParam(String name, Object value)
    {
        this.qname = new QName("", name);
        this.value = value;
    }

    public RPCParam(QName qname, Object value)
    {
        this.qname = qname;
        this.value = value;
    }

    public RPCParam(String namespace, String name, Object value)
    {
        this.qname = new QName(namespace, name);
        this.value = value;
    }
    
    public void setRPCCall(RPCElement call)
    {
        myCall = call;
    }
    
    public Object getValue()
    {
        return value;
    }
    
    public void setValue(Object value)
    {
        this.value = value;
    }

    /**
     * This set method is registered during deserialization
     * to set the deserialized value.
     * If the method is called multiple times, the 
     * value is automatically changed into a container to 
     * hold all of the values.
     * @param newValue is the deserialized object
     */
    public void set(Object newValue) {
        countSetCalls++;
        // If this is the first call,
        // simply set the value.
        if (countSetCalls==1) {
            this.value = newValue;
            return;
        }
        // If this is the second call, create an
        // ArrayList to hold all the values
        else if (countSetCalls==2) {
            ArrayList list = new ArrayList();
            list.add(this.value);
            this.value = list;
        } 
        // Add the new value to the list
        ((ArrayList) this.value).add(newValue);
    }

    public String getName()
    {
        return this.qname.getLocalPart();
    }
    
    public QName getQName()
    {
        return this.qname;
    }
    
    public static Method getValueSetMethod()
    {
        return valueSetMethod;
    }

    public ParameterDesc getParamDesc() {
        return paramDesc;
    }

    public void setParamDesc(ParameterDesc paramDesc) {
        this.paramDesc = paramDesc;
    }

    public void setXSITypeGeneration(Boolean value) {
        this.wantXSIType = value; 
    }

    public Boolean getXSITypeGeneration() {
        return this.wantXSIType;
    }

    public void serialize(SerializationContext context)
        throws IOException
    {
        // Set the javaType to value's class unless 
        // parameter description information exists.
        // Set the xmlType using the parameter description
        // information.  (an xmlType=null causes the
        // serialize method to search for a compatible xmlType)
        Class javaType = value == null ? null: value.getClass();
        QName xmlType = null;
        if (paramDesc != null) {
            if (javaType == null) {
                javaType = paramDesc.getJavaType() != null ?
                    paramDesc.getJavaType(): javaType;
            } else if (!(javaType.equals(paramDesc.getJavaType()))) {
                // This must (assumedly) be a polymorphic type - in ALL
                // such cases, we must send an xsi:type attribute.
                wantXSIType = Boolean.TRUE;
            }
            xmlType = paramDesc.getTypeQName();
        }
        context.serialize(qname,  // element qname
                          null,   // no extra attrs
                          value,  // value
                          javaType, xmlType, // java/xml type
                          true, wantXSIType); 
    }

    private void writeObject(ObjectOutputStream out)
        throws IOException {
        if (qname == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            out.writeObject(qname.getNamespaceURI());
            out.writeObject(qname.getLocalPart());
        }
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) 
        throws IOException, ClassNotFoundException {
        if (in.readBoolean()) {
            qname = new QName((String)in.readObject(),
                              (String)in.readObject());
        } else {
            qname = null;
        }
        in.defaultReadObject();
    }
}
