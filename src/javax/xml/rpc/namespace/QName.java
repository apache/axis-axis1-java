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

package javax.xml.rpc.namespace;

/**
 * QName class represents a qualified name based on "Namespaces in XML" specification. A QName is represented as: 
 * QName ::= (Prefix ':') ? LocalPart 
 *
 * @version 0.1
 */
public class QName {

    /** Field namespaceURI           */
    private String namespaceURI = null;

    /** Field localPart           */
    private String localPart = null;

    /**
     * Constructor for the QName.
     */
    public QName() {}

    /**
     * Constructor for the QName.
     *
     * @param localPart - Local part of the QName
     */
    public QName(String localPart) {
        setLocalPart(localPart);
    }
    
    /**
     * Constructor for the QName.
     *
     * @param namespaceURI - Namespace URI for the QName
     * @param localPart - Local part of the QName
     */
    public QName(String namespaceURI, String localPart) {
        setNamespaceURI(namespaceURI);
        setLocalPart(localPart);
    }

    /**
     * Sets the Namespace URI for this QName
     *
     * @param namespaceURI
     */
    public void setNamespaceURI(String namespaceURI) {
        if (namespaceURI == null)
            namespaceURI = "";
        this.namespaceURI = namespaceURI;
    }

    /**
     * Gets the Namespace URI for this QName
     *
     * @return namespaceURI
     */
    public String getNamespaceURI() {
        return (namespaceURI);
    }

    /**
     * Sets the Local part for this QName
     *
     * @param localPart
     */
    public void setLocalPart(String localPart) {
        this.localPart = localPart;
    }

    /**
     * Gets the Local part for this QName
     *
     * @return the Local part for this QName.
     */
    public String getLocalPart() {
        return (localPart);
    }

    /**
     * Returns a string representation of this QName
     *
     * @return  a string representation of the QName
     */
    public String toString() {

        if (namespaceURI == null || namespaceURI.equals("")) {
            return localPart;
        } else {
            return namespaceURI + ":" + localPart;
        }
    }

    /**
     * Indicates whether some other object is "equal to" this QName object.
     *
     * @param p1 - the reference object with which to compare
     *
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object p1) {

        if (!(p1 instanceof QName)) {
            return false;
        }

        if (namespaceURI == null) {
            if (((QName) p1).namespaceURI != null) {
                return false;
            }
        } else {
            if (!namespaceURI.equals(((QName) p1).namespaceURI)) {
                return false;
            }
        }

        return localPart.equals(((QName) p1).localPart);
    }

    /**
     * Returns a hash code value for this QName object.
     *
     * @return a hash code value for this Qname object
     */
    public int hashCode() {
        return namespaceURI.hashCode() ^ localPart.hashCode();
    }

    // temporary!!

    /**
     * Constructor for the QName.
     *
     * @param qName 
     * @param element
     */
    public QName(String qName, org.w3c.dom.Element element) {

        if (qName != null) {
            int i = qName.indexOf(":");

            if (i < 0) {
                setLocalPart(qName);
                setNamespaceURI(null);
            } else {
                String prefix = qName.substring(0, i);
                String local  = qName.substring(i + 1);

                setLocalPart(local);
                setNamespaceURI(org.apache.axis.utils.XMLUtils
                    .getNamespace(prefix, element));
            }
        }
    }
}


