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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.rpc.namespace.QName;
import java.io.IOException;
import java.lang.reflect.Field;

/** An RPC parameter
 *
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class RPCParam
{
    protected static Log log =
        LogFactory.getLog(RPCParam.class.getName());

    // Who's your daddy?
    RPCElement myCall;
    
    private QName qname;
    public Object value;
    
    private static Field valueField;
    static {
        Class cls = RPCParam.class;
        try {
            valueField = cls.getField("value");
        } catch (NoSuchFieldException e) {
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
    
    public String getName()
    {
        return this.qname.getLocalPart();
    }
    
    public QName getQName()
    {
        return this.qname;
    }
    
    public static Field getValueField()
    {
        return valueField;
    }
    
    public void serialize(SerializationContext context)
        throws IOException
    {
        if (value != null) {
            context.serialize(qname, null, value, value.getClass());
        } else {
            context.serialize(qname, null, value, null);
        }
    }
}
