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
import org.apache.axis.utils.Mapping;
import org.apache.axis.utils.QName;
import org.apache.axis.AxisFault;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.AttributesImpl;

public class SOAPEnvelope extends MessageElement
{
    private static boolean DEBUG_LOG = false;
    
    public Vector headers = new Vector();
    public Vector bodyElements = new Vector();
    public Vector trailers = new Vector();
    public String encodingStyleURI = null;
    
    // This is a hint to any service description to tell it what
    // "type" of message we are.  This might be "request", "response",
    // or anything else your particular service descripton requires.
    //
    // This gets passed back into the service description during
    // deserialization 
    public String messageType;
    
    public SOAPEnvelope()
    {
        this(true);
    }
  
    public SOAPEnvelope(boolean registerPrefixes)
    {
        if (registerPrefixes) {
            if (namespaces == null)
                namespaces = new ArrayList();
            
            namespaces.add(new Mapping(Constants.URI_SOAP_ENV,
                                       Constants.NSPREFIX_SOAP_ENV));
            namespaces.add(new Mapping(Constants.URI_CURRENT_SCHEMA_XSD,
                                       Constants.NSPREFIX_SCHEMA_XSD));
            namespaces.add(new Mapping(Constants.URI_CURRENT_SCHEMA_XSI,
                                       Constants.NSPREFIX_SCHEMA_XSI));
        }
        
        setDirty(true);
    }
    
    public String getMessageType()
    {
        return messageType;
    }
    
    public void setMessageType(String messageType)
    {
        this.messageType = messageType;
    }
    
    public void setEncodingStyleURI(String uri)
    {
        encodingStyleURI = uri;
    }
    
    public Vector getBodyElements() throws AxisFault
    {
        return bodyElements;
    }
    
    public Vector getTrailers()
    {
        return trailers;
    }
    
    public SOAPBodyElement getFirstBody() throws AxisFault
    {
        if (bodyElements.isEmpty())
            return null;
        
        return (SOAPBodyElement)bodyElements.elementAt(0);
    }
    
    public Vector getHeaders() throws AxisFault
    {
        return headers;
    }
    
    public void addHeader(SOAPHeader header)
    {
        if (DEBUG_LOG)
            System.out.println("Adding header to message...");
        header.setEnvelope(this);
        headers.addElement(header);
        _isDirty = true;
    }
    
    public void addBodyElement(SOAPBodyElement element)
    {
        if (DEBUG_LOG)
            System.out.println("Adding body element to message...");
        element.setEnvelope(this);
        bodyElements.addElement(element);
        _isDirty = true;
    }
    
    public void removeHeader(SOAPHeader header)
    {
        if (DEBUG_LOG)
            System.out.println("Removing header from message...");
        headers.removeElement(header);
        _isDirty = true;
    }
    
    public void removeBodyElement(SOAPBodyElement element)
    {
        if (DEBUG_LOG)
            System.out.println("Removing body element from message...");
        bodyElements.removeElement(element);
        _isDirty = true;
    }
    
    public void removeTrailer(MessageElement element)
    {
        if (DEBUG_LOG)
            System.out.println("Removing trailer from message...");
        trailers.removeElement(element);
        _isDirty = true;
    }
    
    public void clearBody()
    {
        if (!bodyElements.isEmpty())
            bodyElements.removeAllElements();
        _isDirty = true;
    }
    
    public void addTrailer(MessageElement element)
    {
        if (DEBUG_LOG)
            System.out.println("Adding trailer to message...");
        element.setEnvelope(this);
        trailers.addElement(element);
        _isDirty = true;
    }

    public SOAPHeader getHeaderByName(String namespace, String localPart)
        throws AxisFault
    {
        SOAPHeader header = (SOAPHeader)findElement(headers,
                                                    namespace,
                                                    localPart);
        
        return header;
    }

    public SOAPBodyElement getBodyByName(String namespace, String localPart)
        throws AxisFault
    {
        return (SOAPBodyElement)findElement(bodyElements,
                                            namespace,
                                            localPart);
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
        throws AxisFault
    {
        /** This might be optimizable by creating a custom Enumeration
         * which moves through the headers list (parsing on demand, again),
         * returning only the next one each time.... this is Q&D for now.
         */
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
    public void outputImpl(SerializationContext context)
        throws Exception
    {
        boolean oldPretty = context.getPretty();
        context.setPretty(true);

        // Register namespace prefixes.
        if (namespaces != null) {
            for (Iterator i = namespaces.iterator(); i.hasNext(); ) {
                Mapping mapping = (Mapping)i.next();
                context.registerPrefixForURI(mapping.getPrefix(),
                                             mapping.getNamespaceURI());
            }
        }
        
        Enumeration enum;
        AttributesImpl attrs = null;
        if (encodingStyleURI != null) {
            attrs = new AttributesImpl();
            attrs.addAttribute(Constants.URI_SOAP_ENV,
                               Constants.ATTR_ENCODING_STYLE,
                               "SOAP-ENV:" + Constants.ATTR_ENCODING_STYLE,
                               "CDATA", encodingStyleURI);
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
        
        // Output multi-refs
        context.outputMultiRefs();
        
        // Output </SOAP-ENV:Body>
        context.endElement();
        
        // Output trailers
        enum = trailers.elements();
        while (enum.hasMoreElements()) {
            MessageElement element = (MessageElement)enum.nextElement();
            element.output(context);
            // Output this independent element
        }
        
        // Output </SOAP-ENV:Envelope>
        context.endElement();

        context.setPretty(oldPretty);
    }
}
