/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.ibm.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.axis2.message.impl ;

import org.w3c.dom.Document ;
import org.w3c.dom.Element ;
import org.w3c.dom.NodeList ;

import org.apache.axis2.message.SOAPEnvelope ;
import org.apache.axis2.message.SOAPHeader ;
import org.apache.axis2.message.SOAPBody ;

/**
 * SOAPEnvelopeImpl is an implementation of SOAPEnvelope.
 * @author Ryo Neyama (neyama@jp.ibm.com)
 * @version $Id$
 */
final public class SOAPEnvelopeImpl
    extends SOAPElementImpl
    implements SOAPEnvelope
{
    /**
     * Creates a SOAP Envelope which contains the DOM Element.
     * @param entity A DOM Element which represents the SOAP Envelope.
     */
    public SOAPEnvelopeImpl(Element entity) { super(entity) ; }

    /**
     * Creates a default SOAP Envelope which contains the DOM Document.
     * @param dom A DOM Document. A SOAP Envelope element will be created by this constructor. If the Document already has a document element, a DOM exception will be thrown.
     */
    SOAPEnvelopeImpl(Document dom) {
        super(dom.createElementNS(Constants.URI_SOAP_ENV, Constants.NSPREFIX_SOAP_ENV+':'+Constants.ELEM_ENVELOPE)) ;
        declareNamespace(Constants.URI_SOAP_ENV,
                         Constants.NSPREFIX_SOAP_ENV) ;
        declareNamespace(Constants.URI_SCHEMA_XSI,
                         Constants.NSPREFIX_SCHEMA_XSI) ;
        declareNamespace(Constants.URI_SCHEMA_XSD,
                         Constants.NSPREFIX_SCHEMA_XSD) ;
        setHeader(new SOAPHeaderImpl(dom)) ;
        setBody(new SOAPBodyImpl(dom)) ;
    }

    public boolean hasHeader() {
        return getElementsNamed(Constants.ELEM_HEADER).getLength() > 0 ;
    }

    public SOAPHeader getHeader() {
        NodeList list = getElementsNamed(Constants.ELEM_HEADER) ;
        if (list.getLength() == 0) {
            SOAPHeader header ;
            setHeader(header = new SOAPHeaderImpl(getDOMEntity().getOwnerDocument())) ;
            return header ;
        } else
            return new SOAPHeaderImpl((Element)list.item(0)) ;
    }

    public void setHeader(SOAPHeader header) {
        Element env = getDOMEntity() ;
        header.ownedBy(env) ;
        NodeList list = getElementsNamed(Constants.ELEM_HEADER) ;
        if (list.getLength() > 0)
            env.replaceChild(header.getDOMEntity(), list.item(0)) ;
        else
            env.insertBefore(header.getDOMEntity(), env.getFirstChild()) ;
    }

    public void removeHeader() {
        getDOMEntity().removeChild(getHeader().getDOMEntity()) ;
    }

    public SOAPBody getBody() {
        NodeList list = getElementsNamed(Constants.ELEM_BODY) ;
        if (list.getLength() == 0)
            return null ;
        else
            return new SOAPBodyImpl((Element)list.item(0)) ;
    }

    public void removeBody() {
        getDOMEntity().removeChild(getBody().getDOMEntity()) ;
    }

    public void setBody(SOAPBody body) {
        Element env = getDOMEntity() ;
        body.ownedBy(env) ;
        NodeList list = getElementsNamed(Constants.ELEM_BODY) ;
        if (list.getLength() > 0)
            env.replaceChild(body.getDOMEntity(), list.item(0)) ;
        else
            env.appendChild(body.getDOMEntity()) ;
    }
}
