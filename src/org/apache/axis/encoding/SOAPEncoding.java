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

package org.apache.axis.encoding;

import org.apache.axis.utils.XMLUtils;
import org.xml.sax.Attributes;

import javax.xml.rpc.namespace.QName;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Hashtable;

public class SOAPEncoding implements Serializer { 
    private Hashtable typemap = new Hashtable();
    private Hashtable namemap = new Hashtable();
    
    public SOAPEncoding() {
        typemap.put(String.class,  SOAPTypeMappingRegistry.XSD_STRING);
        typemap.put(Boolean.class, SOAPTypeMappingRegistry.XSD_BOOLEAN);
        typemap.put(Double.class,  SOAPTypeMappingRegistry.XSD_DOUBLE);
        typemap.put(Float.class,   SOAPTypeMappingRegistry.XSD_FLOAT);
        typemap.put(Integer.class, SOAPTypeMappingRegistry.XSD_INT);
        typemap.put(Long.class,    SOAPTypeMappingRegistry.XSD_LONG);
        typemap.put(Short.class,   SOAPTypeMappingRegistry.XSD_SHORT);
        typemap.put(BigDecimal.class, SOAPTypeMappingRegistry.XSD_DECIMAL);
        typemap.put(Date.class,    SOAPTypeMappingRegistry.XSD_DATE);
        namemap.put(String.class,  SOAPTypeMappingRegistry.SOAP_STRING);
        namemap.put(Boolean.class, SOAPTypeMappingRegistry.SOAP_BOOLEAN);
        namemap.put(Double.class,  SOAPTypeMappingRegistry.SOAP_DOUBLE);
        namemap.put(Float.class,   SOAPTypeMappingRegistry.SOAP_FLOAT);
        namemap.put(Integer.class, SOAPTypeMappingRegistry.SOAP_INT);
        namemap.put(Long.class,    SOAPTypeMappingRegistry.SOAP_LONG);
        namemap.put(Short.class,   SOAPTypeMappingRegistry.SOAP_SHORT);
        namemap.put(BigDecimal.class, SOAPTypeMappingRegistry.XSD_DECIMAL);
        namemap.put(Date.class,    SOAPTypeMappingRegistry.XSD_DATE);
    }
    
    public void serialize(QName qname, Attributes attributes,
                          Object value, SerializationContext context)
        throws IOException
    {
        context.startElement(qname, attributes);
        if (value != null) {
            if (value instanceof String)
                context.writeString(
                                XMLUtils.xmlEncodeString(value.toString()));
            else
                context.writeString(value.toString());
        }
        context.endElement();
    }
}
