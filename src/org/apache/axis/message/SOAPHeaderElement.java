/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

import org.apache.axis.Constants;
import org.apache.axis.AxisFault;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.Messages;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

/**
 * A simple header element abstraction.  Extends MessageElement with
 * header-specific stuff like mustUnderstand, actor, and a 'processed' flag.
 *
 * @author Glen Daniels (gdaniels@macromedia.com)
 * @author Glyn Normington (glyn@apache.org)
 */
public class SOAPHeaderElement extends MessageElement
    implements javax.xml.soap.SOAPHeaderElement {
    protected boolean   processed = false;

    protected String    actor = null;
    protected boolean   mustUnderstand = false;
    protected boolean   relay = false;

    public SOAPHeaderElement(String namespace, String localPart)
    {
        super(namespace, localPart);
    }

    public SOAPHeaderElement(Name name)
    {
        super(name);
    }

    public SOAPHeaderElement(String namespace, String localPart, Object value)
    {
        super(namespace, localPart, value);
    }

    public SOAPHeaderElement(Element elem)
    {
        super(elem);

        // FIXME : This needs to come from someplace reasonable, perhaps
        // TLS (SOAPConstants.getCurrentVersion() ?)
        SOAPConstants soapConstants = SOAPConstants.SOAP11_CONSTANTS;

        if (getNamespaceURI().equals(SOAPConstants.SOAP12_CONSTANTS.getEnvelopeURI()))
            soapConstants = SOAPConstants.SOAP12_CONSTANTS;

        String val = elem.getAttributeNS(soapConstants.getEnvelopeURI(),
                                         Constants.ATTR_MUST_UNDERSTAND);

        if (soapConstants == SOAPConstants.SOAP12_CONSTANTS)
            mustUnderstand = ((val != null) && (val.equals("true") || val.equals("1"))) ? true : false;
        else
            mustUnderstand = ((val != null) && val.equals("1")) ? true : false;

        QName roleQName = soapConstants.getRoleAttributeQName();
        actor = elem.getAttributeNS(roleQName.getNamespaceURI(),
                                    roleQName.getLocalPart());
//        if (actor == null) {
//            actor = "";
//        }
        
        if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
            String relayVal = elem.getAttributeNS(soapConstants.getEnvelopeURI(),
                                                  Constants.ATTR_RELAY);
            relay = ((relayVal != null) && (relayVal.equals("true") || relayVal.equals("1"))) ? true : false;
        }
    }

    public void setParentElement(SOAPElement parent) throws SOAPException {
        if(parent == null)
            throw new IllegalArgumentException(Messages.getMessage("nullParent00"));
        if(!(parent instanceof SOAPHeader))
            throw new IllegalArgumentException(Messages.getMessage("illegalArgumentException00"));
        try {
            super.setParentElement(parent);
        } catch (Throwable t) {
            throw new SOAPException(t);
        }
    }

    public void detachNode() {
        ((SOAPHeader)parent).removeHeader(this);
        super.detachNode();
    }

    public SOAPHeaderElement(String namespace,
                             String localPart,
                             String prefix,
                             Attributes attributes,
                             DeserializationContext context)
            throws AxisFault
    {
        super(namespace, localPart, prefix, attributes, context);

        SOAPConstants soapConstants = context.getMessageContext() == null ?
                                        SOAPConstants.SOAP11_CONSTANTS :
                                        context.getMessageContext().getSOAPConstants();

        // Check for mustUnderstand
        String val = attributes.getValue(soapConstants.getEnvelopeURI(),
                                         Constants.ATTR_MUST_UNDERSTAND);

        if (soapConstants == SOAPConstants.SOAP12_CONSTANTS)
            mustUnderstand = ((val != null) && (val.equals("true") || val.equals("1"))) ? true : false;
        else
            mustUnderstand = ((val != null) && val.equals("1")) ? true : false;

        QName roleQName = soapConstants.getRoleAttributeQName();
        actor = attributes.getValue(roleQName.getNamespaceURI(),
                                    roleQName.getLocalPart());
//        if (actor == null) {
//            actor = "";
//        }

        if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
            String relayVal = attributes.getValue(soapConstants.getEnvelopeURI(),
                                                  Constants.ATTR_RELAY);
            relay = ((relayVal != null) && (relayVal.equals("true") || relayVal.equals("1"))) ? true : false;
        }

        processed = false;
        alreadySerialized = true;
    }

    public boolean getMustUnderstand() { return( mustUnderstand ); }
    public void setMustUnderstand(boolean b) {
        mustUnderstand = b ;
    }

    public String getActor() { return( actor ); }
    public void setActor(String a) {
        actor = a ;
    }
    
    public String getRole() { return( actor ); }
    public void setRole(String a) {
        actor = a ;
    }

    public boolean getRelay() {
        return relay;
    }
    public void setRelay(boolean relay) {
        this.relay = relay;
    }

    public void setProcessed(boolean value) {
        processed = value ;
    }

    public boolean isProcessed() {
        return( processed );
    }

    boolean alreadySerialized = false;

    /** Subclasses can override
     */
    protected void outputImpl(SerializationContext context) throws Exception {
        if (!alreadySerialized) {
            SOAPConstants soapVer = getEnvelope().getSOAPConstants();
            QName roleQName = soapVer.getRoleAttributeQName();

            if (actor != null) {
                setAttribute(roleQName.getNamespaceURI(),
                             roleQName.getLocalPart(), actor);
            }
            
            String val;
            if (context.getMessageContext() != null && context.getMessageContext().getSOAPConstants() == SOAPConstants.SOAP12_CONSTANTS)
                val = mustUnderstand ? "true" : "false";
            else
                val = mustUnderstand ? "1" : "0";

            setAttribute(soapVer.getEnvelopeURI(),
                         Constants.ATTR_MUST_UNDERSTAND,
                         val);
            
            if (soapVer == SOAPConstants.SOAP12_CONSTANTS && relay) {
                setAttribute(soapVer.getEnvelopeURI(), Constants.ATTR_RELAY,
                             "true");
            }
        }

        super.outputImpl(context);
    }
}
