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

import org.apache.axis2.message.SOAPBody ;
import org.apache.axis2.message.SOAPBodyEntry ;
import org.apache.axis2.message.SOAPFault ;
import org.apache.axis2.util.xml.DOMHandler ;

/**
 * SOAPBodyImpl is an implementation of SOAPBody.
 * @author Ryo Neyama (neyama@jp.ibm.com)
 * @version $Id$
 */
final public class SOAPBodyImpl extends SOAPElementImpl implements SOAPBody {
    /**
     * Creates a wrapper for the specified SOAP Body element.
     */
    SOAPBodyImpl(Element entity) { super(entity) ; }

    /**
     * Creates a wrapper for the default SOAP Body element.
     */
    public SOAPBodyImpl(Document dom) {
        super(dom.createElementNS(Constants.URI_SOAP_ENV, Constants.NSPREFIX_SOAP_ENV+':'+Constants.ELEM_BODY)) ;
    }

    public void setFault(SOAPFault fault) {
        Element body = getDOMEntity() ;
        removeFault() ;
        fault.ownedBy(body) ;
        body.appendChild(fault.getDOMEntity()) ;
    }

    public SOAPFault getFault() {
        NodeList list = getElementsNamed(Constants.ELEM_FAULT) ;
        if (list.getLength() == 0)
            return null ;
        else
            return new SOAPFaultImpl((Element)list.item(0)) ;
    }

    public void removeFault() {
        Element body = getDOMEntity() ;
        NodeList list = getElementsNamed(Constants.ELEM_FAULT) ;
        int length = list.getLength() ;
        for (int i = 0 ; i < length ; i++)
            body.removeChild(list.item(i)) ;
    }

    public boolean containsFault() {
        return getElementsNamed(Constants.ELEM_FAULT).getLength() != 0;
    }

    public int getBodyEntryCount() { return getEntryCount() ; }

    public SOAPBodyEntry getBodyEntry(int index) {
        return createBodyEntry(getEntries()[index]) ;
    }

    public SOAPBodyEntry[] getBodyEntries() {
        Element[] entities = getEntries() ;
        SOAPBodyEntry[] entries = new SOAPBodyEntry[entities.length] ;
        for (int i = 0 ; i < entries.length ; i++)
            entries[i] = createBodyEntry(entities[i]) ;
        return entries ;
    }

    public void addBodyEntry(SOAPBodyEntry bodyEntry) {
        Element body ;
        bodyEntry.ownedBy(body = getDOMEntity()) ;
        body.appendChild(bodyEntry.getDOMEntity()) ;
    }

    public void removeBodyEntry(SOAPBodyEntry bodyEntry) {
        getDOMEntity().removeChild(bodyEntry.getDOMEntity()) ;
    }

    private SOAPBodyEntry createBodyEntry(Element elem) {
        if (DOMHandler.isNodeNamedNS(elem,
                                     Constants.URI_SOAP_ENV,
                                     Constants.ELEM_FAULT))
            return new SOAPFaultImpl(elem) ;
        else
            return new SOAPBodyEntryImpl(elem) ;
    }
}
