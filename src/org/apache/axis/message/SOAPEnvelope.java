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

import java.io.*;
import java.util.*;
import org.apache.axis.encoding.*;
import org.apache.axis.Constants;
import org.apache.axis.utils.QName;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.AttributesImpl;

public class SOAPEnvelope
{
    private static boolean DEBUG_LOG = false;
    
    public Vector headers = new Vector();
    public Vector bodyElements = new Vector();
    public Vector independentElements = new Vector();
    public Hashtable idMapping = new Hashtable();
    public String encodingStyleURI = null;
    public Hashtable nsDecls = new Hashtable();
                                            
    public SOAPSAXHandler handler;
    
    // This is a hint to any service description to tell it what
    // "type" of message we are.  This might be "request", "response",
    // or anything else your particular service descripton requires.
    //
    // This gets passed back into the service description during
    // deserialization 
    public String messageType;
    // Our service description, if we know it...
    protected ServiceDescription serviceDesc;
  
    public SOAPEnvelope()
    {
        nsDecls.put(Constants.URI_SOAP_ENV, Constants.NSPREFIX_SOAP_ENV);
        nsDecls.put(Constants.URI_SCHEMA_XSD, Constants.NSPREFIX_SCHEMA_XSD);
        nsDecls.put(Constants.URI_SCHEMA_XSI, Constants.NSPREFIX_SCHEMA_XSI);
        handler = null;
    }
    
    SOAPEnvelope(SOAPSAXHandler handler)
    {
        this.handler = handler;
    }

    public String getMessageType()
    {
        return messageType;
    }
    
    public void setMessageType(String messageType)
    {
        this.messageType = messageType;
    }
    
    public ServiceDescription getServiceDescription()
    {
        return serviceDesc;
    }
    
    public void setServiceDescription(ServiceDescription serviceDesc)
    {
        this.serviceDesc = serviceDesc;
    }
    
    public void setEncodingStyleURI(String uri)
    {
        encodingStyleURI = uri;
    }
    
    public String getAsString()
    {
        // !!! NOT IMPLEMENTED YET
        return null;
    }
    
    public Vector getBodyElements()
    {
        if ((handler != null) && !handler.hasParsedBody()) {
            handler.parseToEnd();
        }
        
        return bodyElements;
    }
    
    public SOAPBodyElement getFirstBody()
    {
        if ((handler != null) && !handler.hasParsedBody()) {
            handler.parseToEnd();
        }
        
        return (SOAPBodyElement)bodyElements.elementAt(0);
    }
    
    public Vector getHeaders()
    {
        if ((handler != null) && !handler.hasParsedHeaders()) {
            handler.parse();
        }
        
        return headers;
    }
    
    void processID(MessageElement element)
    {
        String id = element.getID();
        if (id != null) {
            idMapping.put(id, element);
        }
    }
    
    public void addHeader(SOAPHeader header)
    {
        if (DEBUG_LOG)
            System.out.println("Adding header to message...");
        header.setEnvelope(this);
        headers.addElement(header);
        processID(header); // Can headers have IDs?
    }
    
    public void addBodyElement(SOAPBodyElement element)
    {
        if (DEBUG_LOG)
            System.out.println("Adding body element to message...");
        element.setEnvelope(this);
        bodyElements.addElement(element);
        processID(element); // Can body elements have IDs?
    }
    
    public void addIndependentElement(MessageElement element)
    {
        if (DEBUG_LOG)
            System.out.println("Adding independent element to message...");
        element.setEnvelope(this);
        independentElements.addElement(element);
        processID(element);
    }

    public void parseToEnd()
    {
        if (handler != null)
            handler.parseToEnd();
    }
    
    public MessageElement getElementByID(String id)
    {
        MessageElement el = (MessageElement)idMapping.get(id);
        if ((el != null) || (handler == null))
            return el;  // Got it, or else don't have anything to parse.
        
        // Must find it...
        return handler.parseForID(id);
    }
    
    public SOAPHeader getHeaderByName(String namespace, String localPart)
    {
        SOAPHeader header = (SOAPHeader)findElement(headers, namespace, localPart);
        
        if ((header == null) && (handler != null))
            return handler.parseForHeader(namespace, localPart);
        
        return header;
    }

    public SOAPBodyElement getBodyByName(String namespace, String localPart)
    {
        if ((handler != null) && !handler.hasParsedBody()) {
            return handler.parseForBody(namespace, localPart);
        }
        
        return (SOAPBodyElement)findElement(bodyElements, namespace, localPart);
    }
    
    protected MessageElement findElement(Vector vec, String namespace,
                                  String localPart)
    {
        if (vec.isEmpty())
            return null;
        
        Enumeration e = vec.elements();
        MessageElement element;
        while (e.hasMoreElements()) {
            element = (MessageElement)e.nextElement();
            if (element.getNamespaceURI().equals(namespace) &&
                element.getName().equals(localPart))
                return element;
        }
        
        return null;
    }
    
    public Enumeration getHeadersByName(String namespace, String localPart)
    {
        /** This might be optimizable by creating a custom Enumeration
         * which moves through the headers list (parsing on demand, again),
         * returning only the next one each time.... this is Q&D for now.
         */
        if ((handler != null) && !handler.hasParsedHeaders()) {
            handler.parse();
        }
        
        Vector v = new Vector();
        Enumeration e = headers.elements();
        SOAPHeader header;
        while (e.hasMoreElements()) {
            header = (SOAPHeader)e.nextElement();
            if (header.getNamespaceURI().equals(namespace) &&
                header.getName().equals(localPart))
                v.addElement(header);
        }
        
        return v.elements();
    }
    
    /** Should make SOAPSerializationException?
     */
    public void output(SerializationContext context)
        throws Exception
    {
        Enumeration enum;
        
        /** !!! Since we want this as SAX events, we need to
         * finish parsing our input stream.  There should be a way
         * for us to get the input stream itself, though, if we
         * haven't started parsing yet....
         */
        if ((handler != null) && !handler.hasFinished()) {
            handler.parseToEnd();
        }
        
        // Register namespace prefixes.
        enum = nsDecls.keys();
        while (enum.hasMoreElements()) {
            String uri = (String)enum.nextElement();
            context.registerPrefixForURI((String)nsDecls.get(uri), uri);
        }
        
        AttributesImpl attrs = null;
        if (encodingStyleURI != null) {
            attrs = new AttributesImpl();
            attrs.addAttribute(Constants.URI_SOAP_ENV,
                               Constants.ATTR_ENCODING_STYLE,
                               "SOAP-ENV:" + Constants.ATTR_ENCODING_STYLE, "CDATA", encodingStyleURI);
        }
        
        context.startElement(new QName(Constants.URI_SOAP_ENV,
                                       Constants.ELEM_ENVELOPE), attrs);
        
        if (DEBUG_LOG)
            System.out.println(headers.size() + " headers");
        
        if (!headers.isEmpty()) {
            // Output <SOAP-ENV:Header>
            context.startElement(new QName(Constants.URI_SOAP_ENV,
                                           Constants.ELEM_HEADER), null);
            enum = headers.elements();
            while (enum.hasMoreElements()) {
                SOAPHeader header = (SOAPHeader)enum.nextElement();
                header.output(context);
                // Output this header element
            }
            // Output </SOAP-ENV:Header>
            context.endElement();
        }

        if (bodyElements.isEmpty()) {
            // This is a problem.
            throw new Exception("No body elements!");
        }

        // Output <SOAP-ENV:Body>
        context.startElement(new QName(Constants.URI_SOAP_ENV,
                                       Constants.ELEM_BODY), null);
        enum = bodyElements.elements();
        while (enum.hasMoreElements()) {
            SOAPBodyElement body = (SOAPBodyElement)enum.nextElement();
            body.output(context);
            // Output this body element.
        }
        
        // Output </SOAP-ENV:Body>
        context.endElement();
        
        // Output independent elements
        enum = independentElements.elements();
        while (enum.hasMoreElements()) {
            MessageElement element = (MessageElement)enum.nextElement();
            element.output(context);
            // Output this independent element
        }
        
        // Output </SOAP-ENV:Envelope>
        context.endElement();
    }
}
