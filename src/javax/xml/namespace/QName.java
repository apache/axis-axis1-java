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

package javax.xml.namespace;

import java.io.Serializable;

/**
 * QName class represents a qualified name based on "Namespaces in XML"
 * specification.  A QName is represented as: 
 *
 *     QName ::= (Prefix ':') ? LocalPart 
 *
 * Upgraded the implementation so that the namespaceURI and localPart are
 * always non-null.  This simplifies the implementation, increases performance,
 * and cleans up NullPointerException problems.
 * 
 * Upgraded the implemenation to make QName a final class, changed the 
 * namespaceURI and localPart to final (and interned) Strings, changed equals()
 * to use == comparison on interned Strings.
 *
 * Updated to optimize use of empty string, and remove erroneous
 * (or at least (possibly?) optimizer-dependent) equivalence checks.
 *
 * @version 0.1
 */
public class QName implements Serializable {
    /** comment/shared empty string     */
    private static final String emptyString = "".intern();

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
        this.namespaceURI = emptyString;
        this.localPart= (localPart == null) ? emptyString : localPart.intern();
    }
    
    /**
     * Constructor for the QName.
     *
     * @param namespaceURI - Namespace URI for the QName
     * @param localPart - Local part of the QName.
     */
    public QName(String namespaceURI, String localPart) {
        this.namespaceURI =
            (namespaceURI == null) ? emptyString : namespaceURI.intern();

        this.localPart =
            (localPart == null) ? emptyString : localPart.intern();
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
        return ((namespaceURI == emptyString)
                ? localPart
                : namespaceURI + ":" + localPart);
    }

    /**
     * Indicates whether some other object is "equal to" this QName object.
     *
     * @param p1 - the reference object with which to compare
     *
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public final boolean equals(Object p1) {
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
     * Returns a QName holding the value of the specified String. The string must be in the form returned by the
     * QName.toString() method, i.e. "{namespaceURI}localPart", with the "{namespaceURI}" part being optional.
     * This method doesn't do a full validation of the resulting QName. In particular, it doesn't check that the
     * resulting namespace URI is a legal URI (per RFC 2396 and RFC 2732), nor that the resulting local part is a
     * legal NCName per the XML Namespaces specification.
     *
     * @param s the string to be parsed
     * @throws java.lang.IllegalArgumentException If the specified String cannot be parsed as a QName
     * @return QName corresponding to the given String
     */
    public static QName valueOf(String s) {
        if ((s == null) || s.equals("")) {
            throw new IllegalArgumentException("invalid QName literal");
        }
        if (s.charAt(0) == '{') {
            int i = s.indexOf('}');
            if (i == -1) {
                throw new IllegalArgumentException("invalid QName literal");
            }
            if (i == s.length() - 1) {
                throw new IllegalArgumentException("invalid QName literal");
            } else {
                return new QName(s.substring(1, i), s.substring(i + 1));
            }
        } else {
            return new QName(s);
        }
    }

    /**
     * Returns a hash code value for this QName object.
     *
     * @return a hash code value for this Qname object
     */
    public final int hashCode() {
        return namespaceURI.hashCode() ^ localPart.hashCode();
    }
}


