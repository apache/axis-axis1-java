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

import org.w3c.dom.Element;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.encoding.*;
import org.apache.axis.utils.QName;
import org.apache.axis.utils.QFault;
import java.util.*;
import java.io.*;

/** An Fault body element.
 * 
 * @author Sam Ruby (rubys@us.ibm.com)
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class SOAPFaultElement extends SOAPBodyElement
{
    String currentSubElement;
    Deserializer currentDeser;
    
    public SOAPHandler onStartChild(String namespace,
                                    String name,
                                    Attributes attributes,
                                    DeserializationContext context)
        throws SAXException
    {
        currentSubElement = name;
        currentDeser = context.getTypeMappingRegistry().
                          getDeserializer(SOAPTypeMappingRegistry.XSD_STRING);
        return currentDeser;
    }
    
    public void onEndChild(String localName, Deserializer deserializer)
        throws SAXException
    {
        if (fault == null)
            fault = new AxisFault();
        
        if (currentSubElement.equals("faultcode")) {
            fault.setFaultCode(
                      new QFault(
                         context.getQNameFromString(currentDeser.getValue().toString())
                                )
                              );
        } else if (currentSubElement.equals("faultstring")) {
            fault.setFaultString(currentDeser.getValue().toString());
        } else if (currentSubElement.equals("faultactor")) {
            fault.setFaultActor(currentDeser.getValue().toString());
        } else if (currentSubElement.equals("details")) {
            // !!! Not supported yet
            // fault.setFaultDetails(...);
        }
    }

    ///////////////////////////////////////////////////////////////
    
    protected AxisFault fault;    
    
    public SOAPFaultElement(String namespace, String localName, Attributes attrs,
                      DeserializationContext context)
    {
        super(namespace, localName, attrs, context);
    }
    
    public SOAPFaultElement(AxisFault fault)
    {
        this.fault = fault;
        namespaceURI = Constants.URI_SOAP_ENV;
        name = Constants.ELEM_FAULT;
    }
    
    public void outputImpl(SerializationContext context)
        throws IOException
    {
        context.registerPrefixForURI(prefix, namespaceURI);
        context.startElement(new QName(this.getNamespaceURI(), this.getName()), attributes);

        if (fault.getFaultCode() != null) {
          context.startElement(new QName(Constants.URI_SOAP_ENV, "faultcode"),
                               null);
          QFault code = fault.getFaultCode();
          context.writeSafeString(context.qName2String(code));
          context.endElement();
        }
    
        if (fault.getFaultString() != null) {
          context.startElement(new QName(Constants.URI_SOAP_ENV,
                                         "faultstring"),
                               null);
          context.writeSafeString(fault.getFaultString());
          context.endElement();
        }
    
        if (fault.getFaultActor() != null) {
          context.startElement(new QName(Constants.URI_SOAP_ENV,
                                         "faultactor"),
                               null);
          context.writeSafeString(fault.getFaultActor());
          context.endElement();
        }
    
        Element[] faultDetails = fault.getFaultDetails();
        if (faultDetails != null) {
          //*** TBD ***
        }
    
        context.endElement();
    }
    
    public AxisFault getAxisFault()
    {
        return fault;
    }
    
    public void setAxisFault(AxisFault fault)
    {
        this.fault = fault;
    }
}
