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

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.configuration.NullProvider;
import org.apache.axis.client.AxisClient;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.DeserializationContextImpl;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Mapping;
import org.apache.axis.utils.Messages;

import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.soap.SOAPException;
import javax.xml.namespace.QName;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;


public class SOAPEnvelope extends MessageElement
    implements javax.xml.soap.SOAPEnvelope
{
    protected static Log log =
        LogFactory.getLog(SOAPEnvelope.class.getName());

    private SOAPHeader header;
    private SOAPBody body;

    public Vector trailers = new Vector();
    private SOAPConstants soapConstants;

    // This is a hint to any service description to tell it what
    // "type" of message we are.  This might be "request", "response",
    // or anything else your particular service descripton requires.
    //
    // This gets passed back into the service description during
    // deserialization
    public String messageType;

    public SOAPEnvelope()
    {
        this(true, SOAPConstants.SOAP11_CONSTANTS);
    }

    public SOAPEnvelope(SOAPConstants soapConstants)
    {
        this(true, soapConstants);
    }

    public SOAPEnvelope(boolean registerPrefixes, SOAPConstants soapConstants)
    {
        this.soapConstants = soapConstants;
        header = new SOAPHeader(this, soapConstants);
        body = new SOAPBody(this, soapConstants);

        if (registerPrefixes) {
            if (namespaces == null)
                namespaces = new ArrayList();

            namespaces.add(new Mapping(soapConstants.getEnvelopeURI(),
                                       Constants.NS_PREFIX_SOAP_ENV));
            namespaces.add(new Mapping(Constants.URI_DEFAULT_SCHEMA_XSD,
                                       Constants.NS_PREFIX_SCHEMA_XSD));
            namespaces.add(new Mapping(Constants.URI_DEFAULT_SCHEMA_XSI,
                                       Constants.NS_PREFIX_SCHEMA_XSI));
        }

        setDirty(true);
    }

    public SOAPEnvelope(InputStream input) throws SAXException {
        InputSource is = new InputSource(input);
        header = new SOAPHeader(this, soapConstants); // soapConstants = null!
        DeserializationContext dser = null ;
        AxisClient     tmpEngine = new AxisClient(new NullProvider());
        MessageContext msgContext = new MessageContext(tmpEngine);
        dser = new DeserializationContextImpl(is, msgContext,
                                              Message.REQUEST, this );
        dser.parse();
    }

    public String getMessageType()
    {
        return messageType;
    }

    public void setMessageType(String messageType)
    {
        this.messageType = messageType;
    }

    public Vector getBodyElements() throws AxisFault
    {
        if (body != null) {
            return body.getBodyElements();
        } else {
            return new Vector();
        }
    }

    public Vector getTrailers()
    {
        return trailers;
    }

    public SOAPBodyElement getFirstBody() throws AxisFault
    {
        if (body == null) {
            return null;
        } else {
            return body.getFirstBody();
        }
    }

    public Vector getHeaders() throws AxisFault
    {
        if (header != null) {
            return header.getHeaders();
        } else {
            return new Vector();
        }
    }

    /**
     * Get all the headers targeted at a list of actors.
     */
    public Vector getHeadersByActor(ArrayList actors)
    {
        if (header != null) {
            return header.getHeadersByActor(actors);
        } else {
            return new Vector();
        }
    }

    public void addHeader(SOAPHeaderElement hdr)
    {
        if (header == null) {
            header = new SOAPHeader(this, soapConstants);
        }
        hdr.setEnvelope(this);
        header.addHeader(hdr);
        _isDirty = true;
    }

    public void addBodyElement(SOAPBodyElement element)
    {
        if (body == null) {
            body = new SOAPBody(this, soapConstants);
        }
        element.setEnvelope(this);
        body.addBodyElement(element);

        _isDirty = true;
    }

    public void removeHeaders() {
        header = null;
    }

    public void setHeader(SOAPHeader hdr) {
        header = hdr;
        try {
            header.setParentElement(this);
        } catch (SOAPException ex) {
            // class cast should never fail when parent is a SOAPEnvelope
            log.fatal(Messages.getMessage("exception00"), ex);
        }
    }

    public void removeHeader(SOAPHeaderElement hdr)
    {
        if (header != null) {
            header.removeHeader(hdr);
            _isDirty = true;
        }
    }

    public void removeBody() {
        body = null;
    }

    public void setBody(SOAPBody body) {
        this.body = body;
        try {
            body.setParentElement(this);
        } catch (SOAPException ex) {
            // class cast should never fail when parent is a SOAPEnvelope
            log.fatal(Messages.getMessage("exception00"), ex);
        }
    }

    public void removeBodyElement(SOAPBodyElement element)
    {
        if (body != null) {
            body.removeBodyElement(element);
            _isDirty = true;
        }
    }

    public void removeTrailer(MessageElement element)
    {
        if (log.isDebugEnabled())
            log.debug(Messages.getMessage("removeTrailer00"));
        trailers.removeElement(element);
        _isDirty = true;
    }

    public void clearBody()
    {
        if (body != null) {
            body.clearBody();
            _isDirty = true;
        }
    }

    public void addTrailer(MessageElement element)
    {
        if (log.isDebugEnabled())
            log.debug(Messages.getMessage("removeTrailer00"));
        element.setEnvelope(this);
        trailers.addElement(element);
        _isDirty = true;
    }

    /**
     * Get a header by name (always respecting the currently in-scope
     * actors list)
     */
    public SOAPHeaderElement getHeaderByName(String namespace,
                                             String localPart)
        throws AxisFault
    {
        return getHeaderByName(namespace, localPart, false);
    }

    /**
     * Get a header by name, filtering for headers targeted at this
     * engine depending on the accessAllHeaders parameter.
     */
    public SOAPHeaderElement getHeaderByName(String namespace,
                                             String localPart,
                                             boolean accessAllHeaders)
        throws AxisFault
    {
        if (header != null) {
            return header.getHeaderByName(namespace,
                                          localPart,
                                          accessAllHeaders);
        } else {
            return null;
        }
    }

    public SOAPBodyElement getBodyByName(String namespace, String localPart)
        throws AxisFault
    {
        if (body == null) {
            return null;
        } else {
            return body.getBodyByName(namespace, localPart);
        }
    }

    public Enumeration getHeadersByName(String namespace, String localPart)
        throws AxisFault
    {
        return getHeadersByName(namespace, localPart, false);
    }

    /**
     * Return an Enumeration of headers which match the given namespace
     * and localPart.  Depending on the value of the accessAllHeaders
     * parameter, we will attempt to filter on the current engine's list
     * of actors.
     *
     * !!! NOTE THAT RIGHT NOW WE ALWAYS ASSUME WE'RE THE "ULTIMATE
     * DESTINATION" (i.e. we match on null actor).  IF WE WANT TO FULLY SUPPORT
     * INTERMEDIARIES WE'LL NEED TO FIX THIS.
     */
    public Enumeration getHeadersByName(String namespace, String localPart,
                                        boolean accessAllHeaders)
        throws AxisFault
    {
        if (header != null) {
            return header.getHeadersByName(namespace,
                                           localPart,
                                           accessAllHeaders);
        } else {
            return new Vector().elements();
        }
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

        // Output <SOAP-ENV:Envelope>
        context.startElement(new QName(soapConstants.getEnvelopeURI(),
                                       Constants.ELEM_ENVELOPE), attributes);

        // Output non-SOAPHeader and non-SOAPBody stuff.
        Iterator i = getChildElements();
        while (i.hasNext()) {
            MessageElement element = (MessageElement)i.next();
            if(element instanceof SOAPHeader ||
               element instanceof SOAPBody)
                continue;
            element.output(context);
        }

        // Output headers
        if (header != null) {
            header.outputImpl(context);
        }

        // Output body
        if (body != null) {
            body.outputImpl(context);
        }

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

    SOAPConstants getSOAPConstants() {
        return soapConstants;
    }

    public void setSoapConstants(SOAPConstants soapConstants) {
        this.soapConstants = soapConstants;
    }

    // JAXM methods

    public javax.xml.soap.SOAPBody addBody() throws SOAPException {
        if (body == null) {
            body = new SOAPBody(this, soapConstants);
            return body;
        } else {
            throw new SOAPException(Messages.getMessage("bodyPresent"));
        }
    }

    public javax.xml.soap.SOAPHeader addHeader() throws SOAPException {
        if (header == null) {
            header = new SOAPHeader(this, soapConstants);
            return header;
        } else {
            throw new SOAPException(Messages.getMessage("headerPresent"));
        }
    }

    public javax.xml.soap.Name createName(String localName)
        throws SOAPException {
        // Ok to use the SOAP envelope's namespace URI and prefix?
        return new PrefixedQName(namespaceURI, localName, prefix);
    }

    public javax.xml.soap.Name createName(String localName,
                                          String prefix,
                                          String uri)
        throws SOAPException {
        return new PrefixedQName(uri, localName, prefix);
    }

    public javax.xml.soap.SOAPBody getBody() throws SOAPException {
        return body;
    }

    public javax.xml.soap.SOAPHeader getHeader() throws SOAPException {
        return header;
    }
}
