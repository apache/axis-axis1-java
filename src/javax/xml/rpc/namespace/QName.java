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
 * QName class represents a qualified name based on "Namespaces in XML" specification. 
 * A QName is represented as: 
 * QName ::= (Prefix ':') ? LocalPart 
 *
 * Upgraded the implementation so that the namespaceURI and localPart are
 * always non-null.  This simplifies the implementation, increases performance,
 * and cleans up NullPointerException problems.
 * 
 * Upgraded the implemenation to make QName a final class, changed the 
 * namespaceURI and localPart to final (and interned) Strings, changed equals()
 * to use == comparison on interned Strings.
 * @version 0.1
 */
public final class QName {

    /** Field namespaceURI           */
    private final String namespaceURI;

    /** Field localPart           */
    private final String localPart;

    /**
     * Constructor for the QName.
     *
     * @param localPart - Local part of the QName
     */
    public QName(String localPart) {
        this("", localPart);
    }
    
    /**
     * Constructor for the QName.
     *
     * @param namespaceURI - Namespace URI for the QName
     * @param localPart - Local part of the QName
     */
    public QName(String namespaceURI, String localPart) {
        if (namespaceURI == null) {
            this.namespaceURI = ""; 
        } else {
            this.namespaceURI = namespaceURI.intern();
        }
        if (localPart == null) {
            this.localPart = ""; // This should really be an IllegalArgumentException
        } else {
            this.localPart = localPart.intern();
        }
    }

    /**
     * Gets the Namespace URI for this QName
     *
     * @return namespaceURI
     */
    public String getNamespaceURI() {
        return namespaceURI;
    }

    /**
     * Gets the Local part for this QName
     *
     * @return the Local part for this QName.
     */
    public String getLocalPart() {
        return localPart;
    }

    /**
     * Returns a string representation of this QName
     *
     * @return  a string representation of the QName
     */
    public String toString() {

        if (namespaceURI.equals("")) {
            return localPart;
        } else {
            return (namespaceURI + ":" + localPart);
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
        if (p1 == this) {
            return true;
        }

        if (!(p1 instanceof QName)) {
            return false;
        }
        
        if (namespaceURI == ((QName)p1).namespaceURI &&
            localPart == ((QName) p1).localPart) {
            return true;
        }

        // Since the strings are interned, a direct == of the strings
        // is all that is needed.  Here is the old code.
        //    if (namespaceURI.equals(((QName)p1).namespaceURI) &&
        //        localPart.equals(((QName)p1).localPart)) {
        //        return true;
        //    }
        return false;
    }

    /**
     * Returns a hash code value for this QName object.
     *
     * @return a hash code value for this Qname object
     */
    public int hashCode() {
        return namespaceURI.hashCode() ^ localPart.hashCode();
    }
}


