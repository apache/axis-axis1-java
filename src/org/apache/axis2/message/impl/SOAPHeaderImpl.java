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

import org.apache.axis2.message.SOAPHeader ;
import org.apache.axis2.message.SOAPHeaderEntry ;

/**
 * SOAPHeaderImpl is an implementation of SOAPHeader.
 * @author Ryo Neyama (neyama@jp.ibm.com)
 * @version $Id$
 */
final public class SOAPHeaderImpl
    extends SOAPElementImpl
    implements SOAPHeader
{
    /**
     * Creates a wrapper for the specified SOAP Header element.
     */
    public SOAPHeaderImpl(Element entity) { super(entity) ; }

    /**
     * Creates a wrapper for the default SOAP Header element.
     */
    SOAPHeaderImpl(Document dom) {
        super(dom.createElementNS(Constants.URI_SOAP_ENV, Constants.NSPREFIX_SOAP_ENV+':'+Constants.ELEM_HEADER)) ;
    }

    public int getHeaderEntryCount() { return getEntryCount() ; }

    public SOAPHeaderEntry getHeaderEntry(int index) {
        return new SOAPHeaderEntryImpl(getEntries()[index]) ;
    }

    public SOAPHeaderEntry[] getHeaderEntries() {
        Element[] entities = getEntries() ;
        SOAPHeaderEntry[] entries = new SOAPHeaderEntry[entities.length] ;
        for (int i = 0 ; i < entries.length ; i++)
            entries[i] = new SOAPHeaderEntryImpl(entities[i]) ;
        return entries ;
    }

    public void addHeaderEntry(SOAPHeaderEntry headerEntry) {
        Element header ;
        headerEntry.ownedBy(header = getDOMEntity()) ;
        header.appendChild(headerEntry.getDOMEntity()) ;
    }

    public void removeHeaderEntry(SOAPHeaderEntry headerEntry) {
        getDOMEntity().removeChild(headerEntry.getDOMEntity()) ;
    }
}
