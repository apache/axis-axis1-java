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

import java.util.Vector ;
import org.w3c.dom.Document ;
import org.w3c.dom.Element ;
import org.w3c.dom.Node ;
import org.w3c.dom.NodeList ;
import org.apache.axis2.message.SOAPElement ;
import org.apache.axis2.util.xml.DOMConverter ;
import org.apache.axis2.util.xml.DOMHandler ;

/**
 * SOAPElementImpl is an implementation of SOAPElement.
 * @author Ryo Neyama (neyama@jp.ibm.com)
 * @version $Id$
 */
abstract class SOAPElementImpl implements SOAPElement {
    Element entity ;
    SOAPElementImpl(Element entity) { this.entity = entity ; }

    public Element getDOMEntity() { return entity ; }
    public void setDOMEntity(Element entity) { this.entity = entity ; }

    public void setEncodingStyle(String uri) {
        entity.setAttributeNS(Constants.URI_SOAP_ENV, Constants.NSPREFIX_SOAP_ENV+':'+Constants.ATTR_ENCODING_STYLE, uri) ;
    }

    public String getEncodingStyle() {
        String value ;
        if (!"".equals(value = entity.getAttributeNS(Constants.URI_SOAP_ENV, Constants.ATTR_ENCODING_STYLE)))
            return value ;
        return null ;
    }

    public void removeEncodingStyle() {
        entity.removeAttributeNS(Constants.URI_SOAP_ENV, Constants.NSPREFIX_SOAP_ENV+':'+Constants.ATTR_ENCODING_STYLE) ;
    }

    public void declareNamespace(String namespaceURI, String alias) {
        Element entity = getDOMEntity() ;
        entity.setAttribute("xmlns:" + alias, namespaceURI) ;
    }

    public String toXML() { return DOMConverter.toString(entity) ; }

    public String toXML(String encoding) {
        return DOMConverter.toString(entity, encoding) ;
    }

    public String toString() { return toXML() ; }

    public void ownedBy(Element parent) {
        ownedBy(parent.getOwnerDocument()) ;
        if (Constants.URI_SOAP_ENV.equals(entity.getNamespaceURI())) {
            String prefix = DOMHandler.getPrefixForNamespaceURI(parent, Constants.URI_SOAP_ENV) ;
            entity.setPrefix(prefix) ;
        }
    }

    public void ownedBy(Document dom) {
        if (dom != entity.getOwnerDocument())
            entity = (Element)dom.importNode(entity, true) ;
    }

    public int getEntryCount() { return getEntries().length ; }

    NodeList getElementsNamed(String name) {
        return getDOMEntity().getElementsByTagNameNS(Constants.URI_SOAP_ENV, name) ;
    }

    Element[] getEntries() {
        NodeList list = getDOMEntity().getChildNodes() ;
        int length = list.getLength() ;
        Vector buf = new Vector() ;
        Node node ;
        for (int i = 0 ; i < length ; i++)
            if ((node = list.item(i)).getNodeType() == Node.ELEMENT_NODE)
                buf.addElement(node) ;
        Element[] entries = new Element[buf.size()] ;
        for (int i = 0 ; i < entries.length ; i++)
            entries[i] = (Element)buf.elementAt(i) ;
        return entries ;
    }

    public void setID(String id) {
        getDOMEntity().setAttribute(Constants.ATTR_ID, id) ;
    }

    public void removeID() {
        getDOMEntity().removeAttribute(Constants.ATTR_ID) ;
    }
}
