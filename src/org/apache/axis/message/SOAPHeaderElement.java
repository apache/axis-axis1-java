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

import org.apache.axis.Constants;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.utils.JavaUtils;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.Name;
import javax.xml.namespace.QName;

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

    protected String    actor = "";
    protected boolean   mustUnderstand = false;

    public SOAPHeaderElement() {
        super();
    }

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

        String val = elem.getAttributeNS(soapConstants.getEnvelopeURI(),
                                         Constants.ATTR_MUST_UNDERSTAND);
        mustUnderstand = ((val != null) && val.equals("1")) ? true : false;

        QName roleQName = soapConstants.getRoleAttributeQName();
        actor = elem.getAttributeNS(roleQName.getNamespaceURI(),
                                    roleQName.getLocalPart());
        if (actor == null) {
            actor = "";
        }
    }

    public void setParentElement(SOAPElement parent) throws SOAPException {
        if(parent == null)
            throw new IllegalArgumentException(JavaUtils.getMessage("nullParent00")); 
        if(!(parent instanceof SOAPHeader))
            throw new IllegalArgumentException(JavaUtils.getMessage("illegalArgumentException00")); 
        try {
            super.setParentElement((SOAPHeader)parent);
        } catch (Throwable t) {
            throw new SOAPException(t);
        }
    }

    public void detachNode() {
        ((SOAPHeader)parent).removeHeader(this);
        super.detachNode();
    }

    public SOAPHeaderElement(String namespace, String localPart, String prefix,
                      Attributes attributes, DeserializationContext context) {
        super(namespace, localPart, prefix, attributes, context);

        SOAPConstants soapConstants = context.getMessageContext().
                                                          getSOAPConstants();

        // Check for mustUnderstand
        String val = attributes.getValue(soapConstants.getEnvelopeURI(),
                                         Constants.ATTR_MUST_UNDERSTAND);
        mustUnderstand = ((val != null) && val.equals("1")) ? true : false;

        QName roleQName = soapConstants.getRoleAttributeQName();
        actor = attributes.getValue(roleQName.getNamespaceURI(),
                                    roleQName.getLocalPart());
        if (actor == null) {
            actor = "";
        }

        processed = false;
    }

    public boolean getMustUnderstand() { return( mustUnderstand ); }
    public void setMustUnderstand(boolean b) {
        mustUnderstand = b ;
        String val = b ? "1" : "0";

        // Instead of doing this can we hang out until serialization time
        // and do it there, so that we can then resolve SOAP version?
        setAttribute(Constants.URI_SOAP11_ENV,
                     Constants.ATTR_MUST_UNDERSTAND,
                     val);
    }

    public String getActor() { return( actor ); }
    public void setActor(String a) {
        actor = a ;

        // FIXME
        // Instead of doing this can we hang out until serialization time
        // and do it there, so that we can then resolve SOAP version?
        setAttribute(Constants.URI_SOAP11_ENV, Constants.ATTR_ACTOR, a);
    }

    public void setProcessed(boolean value) {
        processed = value ;
    }

    public boolean isProcessed() {
        return( processed );
    }
}
