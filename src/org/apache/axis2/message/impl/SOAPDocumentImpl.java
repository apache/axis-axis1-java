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
 * 4. The names "Xerces" and "Apache Software Foundation" must
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
import org.apache.axis2.message.SOAPDocument ;
import org.apache.axis2.message.SOAPEnvelope ;
import org.apache.axis2.message.SOAPHeader ;
import org.apache.axis2.message.SOAPHeaderEntry ;
import org.apache.axis2.message.SOAPBody ;
import org.apache.axis2.message.SOAPBodyEntry ;
import org.apache.axis2.message.SOAPFault ;
import org.apache.axis2.message.impl.SOAPEnvelopeImpl ;
import org.apache.axis2.message.impl.SOAPHeaderImpl ;
import org.apache.axis2.message.impl.SOAPHeaderEntryImpl ;
import org.apache.axis2.message.impl.SOAPBodyImpl ;
import org.apache.axis2.message.impl.SOAPBodyEntryImpl ;
import org.apache.axis2.message.impl.SOAPFaultImpl ;
import org.apache.axis2.util.xml.DOMConverter ;
import org.apache.axis2.util.xml.DOMHandler ;

/**
 * SOAPDocumentImpl is an implementation of SOAPDocument.
 * @author Ryo Neyama (neyama@jp.ibm.com)
 * @version $Id$
 */
public class SOAPDocumentImpl
    implements SOAPDocument
{
    Document dom ;

    /**
     * Creates a SOAP document which contains the DOM.
     * @param dom A DOM Document which represents the SOAP Envelope XML.
     */
    public SOAPDocumentImpl(Document dom) { this.dom = dom ; }

    /**
     * Creates a default SOAP document.
     */
    public SOAPDocumentImpl() {
        this(DOMHandler.createDocument()) ;
        setEnvelope(createEnvelope()) ;
    }

    public void setEnvelope(SOAPEnvelope envelope) {
        envelope.ownedBy(dom) ;

        Element old ;
        if ((old = dom.getDocumentElement()) != null)
            dom.replaceChild(envelope.getDOMEntity(), old) ;
        else
            dom.appendChild(envelope.getDOMEntity()) ;
    }

    public SOAPEnvelope getEnvelope() {
        return new SOAPEnvelopeImpl(dom.getDocumentElement()) ;
    }

    public Document getDocument() { return dom ; }

    public void setDocument(Document dom) { this.dom = dom ; }

    public String toXML() { return DOMConverter.toString(dom) ; }

    public String toXML(String encoding) {
        return DOMConverter.toString(dom, encoding) ;
    }

    public String toString() { return toXML() ; }

    /**
     * Not implemented yet.
     */
    public boolean validate() { return true ; }

    public SOAPEnvelope createEnvelope() { return new SOAPEnvelopeImpl(dom) ; }

    public SOAPHeader createHeader() { return new SOAPHeaderImpl(dom) ; }

    public SOAPHeaderEntry createHeaderEntry(Element element) {
        return new SOAPHeaderEntryImpl(element) ;
    }

    public SOAPBody createBody() {
        return new SOAPBodyImpl(dom) ;
    }

    public SOAPBodyEntry createBodyEntry(Element element) {
        return new SOAPBodyEntryImpl(element) ;
    }

    public SOAPFault createFault(String faultCode,
                                 String faultString,
                                 String faultActor) 
    {
        return new SOAPFaultImpl(dom, faultCode, faultString, faultActor) ;
    }
}
