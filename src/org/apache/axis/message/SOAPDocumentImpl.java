/*
 * The Apache Software License, Version 1.1
 * 
 * 
 * Copyright (c) 2001-2003 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowledgment: "This product includes software developed by the Apache
 * Software Foundation (http://www.apache.org/)." Alternately, this
 * acknowledgment may appear in the software itself, if and wherever such
 * third-party acknowledgments normally appear. 4. The names "Axis" and "Apache
 * Software Foundation" must not be used to endorse or promote products derived
 * from this software without prior written permission. For written permission,
 * please contact apache@apache.org. 5. Products derived from this software may
 * not be called "Apache", nor may "Apache" appear in their name, without prior
 * written permission of the Apache Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the Apache Software Foundation. For more information on the
 * Apache Software Foundation, please see <http://www.apache.org/> .
 */

package org.apache.axis.message;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.SOAPPart;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;

/**
 * SOAPDcoumentImpl implements the Document API for SOAPPART. At the moment, it
 * again delgate the XERCES DOM Implementation Here is my argument on it: I
 * guess that there is 3 way to implement this. - fully implement the DOM API
 * here myself. => This is too much and duplicated work. - extends XERCES
 * Implementation => this makes we are fixed to one Implementation - choose
 * delgate depends on the user's parser preference => This is the practically
 * best solution I have now
 * 
 * @author Heejune Ahn (cityboy@tmax.co.kr)
 *  
 */

public class SOAPDocumentImpl
implements org.w3c.dom.Document, java.io.Serializable {

    // Depending on the user's parser preference
    org.w3c.dom.Document delegate = null;
    SOAPPart soapPart = null;

    /**
     * Construct the Document
     * 
     * @param soapPart
     */
    public SOAPDocumentImpl(SOAPPart sp) {
        try {
            delegate = XMLUtils.newDocument();
        } catch (ParserConfigurationException e) {
            // Do nothing
        }
        soapPart = sp;
    }

    /**
     * @todo : link with SOAP
     * 
     * @return
     */
    public DocumentType getDoctype() {
        return delegate.getDoctype();
    }

    public DOMImplementation getImplementation() {
        return delegate.getImplementation();
    }

    /**
     * should not be called, the method will be handled in SOAPPart
     * 
     * @return
     */
    public Element getDocumentElement() {
        return soapPart.getDocumentElement();
    }

    /**
     * based on the tagName, we will make different kind SOAP Elements Instance
     * Is really we can determine the Type by the Tagname???
     * 
     * @todo : verify this method
     * 
     * @param tagName
     * @return @throws
     *         DOMException
     */

    public org.w3c.dom.Element createElement(String tagName)
    throws DOMException {
        int index = tagName.indexOf(":");
        String prefix, localname;
        if (index < 0) {
            prefix = "";
            localname = tagName;
        } else {
            prefix = tagName.substring(0, index);
            localname = tagName.substring(index + 1);
        }

        try {
            SOAPEnvelope soapenv =
                (org.apache.axis.message.SOAPEnvelope) soapPart.getEnvelope();
            if (soapenv != null) {
                if (tagName.equalsIgnoreCase(Constants.ELEM_ENVELOPE))
                    new SOAPEnvelope();
                if (tagName.equalsIgnoreCase(Constants.ELEM_HEADER))
                    return new SOAPHeader(soapenv, soapenv.getSOAPConstants());
                if (tagName.equalsIgnoreCase(Constants.ELEM_BODY))
                    return new SOAPBody(soapenv, soapenv.getSOAPConstants());
                if (tagName.equalsIgnoreCase(Constants.ELEM_FAULT))
                    return new SOAPEnvelope();
                if (tagName.equalsIgnoreCase(Constants.ELEM_FAULT_DETAIL))
                    return new SOAPFault(new AxisFault(tagName));
                else {
                    return new MessageElement("", prefix, localname);
                }
            } else {
                return new MessageElement("", prefix, localname);
            }

        } catch (SOAPException se) {
            throw new DOMException(DOMException.INVALID_STATE_ERR, "");
        }
    }

    /**
     * 
     * Creates an empty <code>DocumentFragment</code> object. @todo not
     * implemented yet
     * 
     * @return A new <code>DocumentFragment</code>.
     */
    public DocumentFragment createDocumentFragment() {
        return delegate.createDocumentFragment();
    }
    /**
     * Creates a <code>Text</code> node given the specified string.
     * 
     * @param data
     *            The data for the node.
     * @return The new <code>Text</code> object.
     */
    public org.w3c.dom.Text createTextNode(String data) {
        org.apache.axis.message.Text me =
            new org.apache.axis.message.Text(data);
        me.setOwnerDocument(soapPart);
        return me;

    }

    /**
     * Creates a <code>Comment</code> node given the specified string.
     * 
     * @param data
     *            The data for the node.
     * @return The new <code>Comment</code> object.
     */
    public Comment createComment(String data) {
        return new org.apache.axis.message.CommentImpl(data);
    }

    /**
     * Creates a <code>CDATASection</code> node whose value is the specified
     * string.
     * 
     * @param data
     *            The data for the <code>CDATASection</code> contents.
     * @return The new <code>CDATASection</code> object.
     * @exception DOMException
     *                NOT_SUPPORTED_ERR: Raised if this document is an HTML
     *                document.
     */
    public CDATASection createCDATASection(String data) throws DOMException {
        return new CDATAImpl(data);
    }

    /**
     * Creates a <code>ProcessingInstruction</code> node given the specified
     * name and data strings.
     * 
     * @param target
     *            The target part of the processing instruction.
     * @param data
     *            The data for the node.
     * @return The new <code>ProcessingInstruction</code> object.
     * @exception DOMException
     *                INVALID_CHARACTER_ERR: Raised if the specified target
     *                contains an illegal character. <br>NOT_SUPPORTED_ERR:
     *                Raised if this document is an HTML document.
     */
    public ProcessingInstruction createProcessingInstruction(
            String target,
            String data)
    throws DOMException {
        throw new java.lang.UnsupportedOperationException(
        "createProcessingInstruction");
    }

    /**
     * @todo: How Axis will maintain the Attribute representation ?
     */
    public Attr createAttribute(String name) throws DOMException {
        return delegate.createAttribute(name);
    }

    /**
     * @param name
     * @return @throws
     *         DOMException
     */
    public EntityReference createEntityReference(String name)
    throws DOMException {
        throw new java.lang.UnsupportedOperationException(
        "createEntityReference");
    }

    public Node importNode(Node importedNode, boolean deep)
    throws DOMException {
        throw new java.lang.UnsupportedOperationException("importNode");
    }

    /**
     * Return SOAPElements (what if they want SOAPEnvelope or Header/Body?)
     * 
     * @param namespaceURI
     * @param qualifiedName
     * @return @throws
     *         DOMException
     */
    public Element createElementNS(String namespaceURI, String qualifiedName)
    throws DOMException {
        org.apache.axis.soap.SOAPConstants soapConstants = null;
        if (Constants.URI_SOAP11_ENV.equals(namespaceURI)) {
            soapConstants = org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS;
        } else if (Constants.URI_SOAP12_ENV.equals(namespaceURI)) {
            soapConstants = org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS;
        }

        // For special SOAP Element
        MessageElement me = null;
        if (soapConstants != null) {
            if (qualifiedName.equals(Constants.ELEM_ENVELOPE)) {
                // TODO: confirm SOAP 1.1!
                me = new SOAPEnvelope(soapConstants); 
            } else if (qualifiedName.equals(Constants.ELEM_HEADER)) {
                me = new SOAPHeader(null, soapConstants);
                // Dummy SOAPEnv required?
            } else if (qualifiedName.equals(Constants.ELEM_BODY)) {
                me = new SOAPBody(null, soapConstants);
            } else if (qualifiedName.equals(Constants.ELEM_FAULT)) {
                me = null;
            } else if (qualifiedName.equals(Constants.ELEM_FAULT_DETAIL)) {
                // TODO:
                me = null;
            } else {
                throw new DOMException(
                        DOMException.INVALID_STATE_ERR,
                "No such Localname for SOAP URI");
            }
            // TODO:
            return null;
            // general Elements
        } else {
            me = new MessageElement(namespaceURI, qualifiedName);
        }

        if (me != null)
            me.setOwnerDocument(soapPart);

        return me;

    }

    /**
     * Attribute is not particularly dealt with in SAAJ.
     *  
     */
    public Attr createAttributeNS(String namespaceURI, String qualifiedName)
    throws DOMException {
        return delegate.createAttributeNS(namespaceURI, qualifiedName);
    }

    /**
     * search the SOAPPart in order of SOAPHeader and SOAPBody for the
     * requested Element name
     *  
     */
    public NodeList getElementsByTagNameNS(
            String namespaceURI,
            String localName) {
        try {
            if (soapPart != null) {
                SOAPEnvelope soapEnv =
                    (org.apache.axis.message.SOAPEnvelope) soapPart
                    .getEnvelope();
                SOAPHeader header =
                    (org.apache.axis.message.SOAPHeader) soapEnv.getHeader();
                if (header != null) {
                    return header.getElementsByTagNameNS(
                            namespaceURI,
                            localName);
                }
                SOAPBody body =
                    (org.apache.axis.message.SOAPBody) soapEnv.getHeader();
                if (body != null) {
                    return header.getElementsByTagNameNS(
                            namespaceURI,
                            localName);
                }
            }
            return null;
        } catch (SOAPException se) {
            throw new DOMException(DOMException.INVALID_STATE_ERR, "");
        }
    }

    /**
     * search the SOAPPart in order of SOAPHeader and SOAPBody for the
     * requested Element name
     *  
     */
    public NodeList getElementsByTagName(String localName) {

        try {
            if (soapPart != null) {
                SOAPEnvelope soapEnv =
                    (org.apache.axis.message.SOAPEnvelope) soapPart
                    .getEnvelope();
                SOAPHeader header =
                    (org.apache.axis.message.SOAPHeader) soapEnv.getHeader();
                if (header != null) {
                    return header.getElementsByTagName(localName);
                }
                SOAPBody body =
                    (org.apache.axis.message.SOAPBody) soapEnv.getHeader();
                if (body != null) {
                    return header.getElementsByTagName(localName);
                }
            }
            return null;
        } catch (SOAPException se) {
            throw new DOMException(DOMException.INVALID_STATE_ERR, "");
        }
    }
    /**
     * Returns the <code>Element</code> whose <code>ID</code> is given by
     * <code>elementId</code>. If no such element exists, returns <code>null</code>.
     * Behavior is not defined if more than one element has this <code>ID</code>.
     * The DOM implementation must have information that says which attributes
     * are of type ID. Attributes with the name "ID" are not of type ID unless
     * so defined. Implementations that do not know whether attributes are of
     * type ID or not are expected to return <code>null</code>.
     * 
     * @param elementId
     *            The unique <code>id</code> value for an element.
     * @return The matching element.
     * @since DOM Level 2
     */
    public Element getElementById(String elementId) {
        return delegate.getElementById(elementId);
    }

    /**
     * Node Implementation
     *  
     */

    public String getNodeName() {
        return null;
    }

    public String getNodeValue() throws DOMException {
        throw new DOMException(
                DOMException.NO_DATA_ALLOWED_ERR,
                "Cannot use TextNode.get in " + this);
    }

    public void setNodeValue(String nodeValue) throws DOMException {
        throw new DOMException(
                DOMException.NO_DATA_ALLOWED_ERR,
                "Cannot use TextNode.set in " + this);
    }

    /**
     * override it in sub-classes
     * 
     * @return
     */
    public short getNodeType() {
        return Node.DOCUMENT_NODE;
    }

    public Node getParentNode() {
        return null;
    }

    public NodeList getChildNodes() {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
    }

    /**
     * Do we have to count the Attributes as node ????
     * 
     * @return
     */
    public Node getFirstChild() {
        try {
            if (soapPart != null)
                return (org.apache.axis.message.SOAPEnvelope) soapPart
                .getEnvelope();
            else
                return null;
        } catch (SOAPException se) {
            throw new DOMException(DOMException.INVALID_STATE_ERR, "");
        }

    }

    /**
     * @return
     */
    public Node getLastChild() {
        try {
            if (soapPart != null)
                return (org.apache.axis.message.SOAPEnvelope) soapPart
                .getEnvelope();
            else
                return null;
        } catch (SOAPException se) {
            throw new DOMException(DOMException.INVALID_STATE_ERR, "");
        }

    }

    public Node getPreviousSibling() {
        return null;
    }
    public Node getNextSibling() {

        return null;
    }

    public NamedNodeMap getAttributes() {
        return null;
    }

    /**
     * 
     * we have to have a link to them...
     */
    public Document getOwnerDocument() {
        return null;
    }

    /**
     */
    public Node insertBefore(Node newChild, Node refChild)
    throws DOMException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
    }

    public Node replaceChild(Node newChild, Node oldChild)
    throws DOMException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
    }

    public Node removeChild(Node oldChild) throws DOMException {
        try {
            Node envNode;
            if (soapPart != null) {
                envNode = soapPart.getEnvelope();
                if (envNode.equals(oldChild)) {
                    return envNode;
                }
            }
            throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
        } catch (SOAPException se) {
            throw new DOMException(DOMException.INVALID_STATE_ERR, "");
        }
    }

    public Node appendChild(Node newChild) throws DOMException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
    }

    public boolean hasChildNodes() {
        try {
            if (soapPart != null) {
                if (soapPart.getEnvelope() != null) {
                    return true;
                }
            }
            return false;
        } catch (SOAPException se) {
            throw new DOMException(DOMException.INVALID_STATE_ERR, "");
        }

    }

    /**
     * @todo: Study it more.... to implement the deep mode correctly.
     *  
     */
    public Node cloneNode(boolean deep) {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
    }

    /**
     * @todo: is it OK to simply call the superclass?
     *  
     */
    public void normalize() {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
    }

    // TODO: fill appropriate features
    private String[] features = { "foo", "bar" };
    private String version = "version 2.0";

    public boolean isSupported(String feature, String version) {
        if (!version.equalsIgnoreCase(version))
            return false;
        else
            return true;
    }

    public String getPrefix() {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
    }
    public void setPrefix(String prefix) {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
    }

    public String getNamespaceURI() {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
    }
    public void setNamespaceURI(String nsURI) {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
    }

    public String getLocalName() {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
    }

    public boolean hasAttributes() {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
    }
}
