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
    static class FaultElementFactory implements ElementFactory
    {
        public MessageElement createElement(String namespace, String localName,
                                        Attributes attributes, DeserializationContext context)
        {
            return new SOAPFaultElement(namespace, localName, attributes, context);
        }
    }
    
    public static ElementFactory getFactory()
    {
        return new FaultElementFactory();
    }
    
    String currentSubElement;
    StringBuffer currentValue;
    
    public void onStartChild(String namespace, String name, String qName,
                             Attributes attributes)
        throws SAXException
    {
        currentSubElement = name;
        currentValue = new StringBuffer();
    }
    
    public void characters(char [] chars, int start, int end)
    {
        // Only capture characters between StartChild and EndChild (the
        // rest, presumably, is ignorable whitespace).
        if (currentValue != null)
            currentValue.append(chars, start, end);
    }

    public void onEndChild(String localName, DeserializerBase deserializer)
        throws SAXException
    {
        if (fault == null)
            fault = new AxisFault();
        
        if (currentSubElement.equals("faultcode")) {
            fault.setFaultCode(
                      new QFault(
                         context.getQNameFromString(currentValue.toString())
                                )
                              );
        } else if (currentSubElement.equals("faultstring")) {
            fault.setFaultString(currentValue.toString());
        } else if (currentSubElement.equals("faultactor")) {
            fault.setFaultActor(currentValue.toString());
        } else if (currentSubElement.equals("details")) {
            // !!! Not supported yet
            // fault.setFaultDetails(...);
        }

        currentValue = null;
    }

    public DeserializerBase getContentHandler() { return this; }
    
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
    }
    
    public void output(SerializationContext context)
        throws IOException
    {
        context.registerPrefixForURI(prefix, namespaceURI);
        context.startElement(new QName(this.getNamespaceURI(), this.getName()), attributes);

        if (fault.getFaultCode() != null) {
          MessageElement element = new 
            MessageElement(Constants.URI_SOAP_ENV, "faultcode");
          QFault code = fault.getFaultCode();
          String prefix = context.getPrefixForURI(code.getNamespaceURI());
          element.setValue(prefix + ":" + code.getLocalPart());
          element.output(context);
        }
    
        if (fault.getFaultString() != null) {
          MessageElement element = new 
            MessageElement(Constants.URI_SOAP_ENV, "faultstring");
          element.setValue(fault.getFaultString());
          element.output(context);
        }
    
        if (fault.getFaultActor() != null) {
          MessageElement element = new 
            MessageElement(Constants.URI_SOAP_ENV, "faultactor");
          element.setValue(fault.getFaultActor());
          element.output(context);
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
