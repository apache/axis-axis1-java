package org.apache.axis.message;

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

import org.apache.axis.AxisFault;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.SOAPTypeMappingRegistry;
import org.apache.axis.encoding.ValueReceiver;
import org.apache.axis.utils.QFault;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.rpc.namespace.QName;
import java.util.HashMap;

/** 
 * Build a Fault body element.
 * 
 * @author Sam Ruby (rubys@us.ibm.com)
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class SOAPFaultBuilder extends SOAPHandler implements ValueReceiver
{
    protected SOAPFaultElement element;
    protected AxisFault fault;
    protected DeserializationContext context;
    static HashMap fields = new HashMap();

    static {
        fields.put("faultcode", SOAPTypeMappingRegistry.XSD_STRING);
        fields.put("faultstring", SOAPTypeMappingRegistry.XSD_STRING);
        fields.put("faultactor", SOAPTypeMappingRegistry.XSD_STRING);
        fields.put("details", null);
    }
    
    public SOAPFaultBuilder(SOAPFaultElement element,
                            DeserializationContext context) {
        this.element = element;
        this.context = context;
        fault = element.getAxisFault();
    }

    public SOAPHandler onStartChild(String namespace,
                                    String name,
                                    String prefix,
                                    Attributes attributes,
                                    DeserializationContext context)
        throws SAXException
    {
        Deserializer currentDeser = null;
        
        QName qName = (QName)fields.get(name);
        
        if (qName != null) {
            currentDeser = context.getTypeMappingRegistry().
                          getDeserializer(qName);
            currentDeser.registerCallback(this, name);
        }
        
        return currentDeser;
    } 
    
    public void valueReady(Object value, Object hint)
    {
        String name = (String)hint;
        if (name.equals("faultcode")) {
            QName qname = context.getQNameFromString((String)value);
            if (qname != null) fault.setFaultCode(new QFault(qname));
        } else if (name.equals("faultstring")) {
            fault.setFaultString((String)value);
        } else if (name.equals("faultactor")) {
            fault.setFaultActor((String)value);
        }
        
    }
}
