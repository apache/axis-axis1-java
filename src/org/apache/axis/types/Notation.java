/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
package org.apache.axis.types;

import org.apache.axis.Constants;
import org.apache.axis.description.AttributeDesc;
import org.apache.axis.description.ElementDesc;
import org.apache.axis.description.FieldDesc;
import org.apache.axis.description.TypeDesc;

/**
 * Custom class for supporting XSD data type NOTATION.
 *
 * @author Davanum Srinivas <dims@yahoo.com>
 * @see <a href="http://www.w3.org/TR/xmlschema-1/#element-notation">XML Schema Part 1: 3.12 Notation Declarations</a>
 */

public class Notation {
    NCName name;
    URI publicURI;
    URI systemURI;

    public Notation() {
    }

    public Notation(NCName name, URI publicURI, URI systemURI) {
        this.name = name;
        this.publicURI = publicURI;
        this.systemURI = systemURI;
    }

    public NCName getName() {
        return name;
    }

    public void setName(NCName name) {
        this.name = name;
    }

    public URI getPublic() {
        return publicURI;
    }

    public void setPublic(URI publicURI) {
        this.publicURI = publicURI;
    }

    public URI getSystem() {
        return systemURI;
    }

    public void setSystem(URI systemURI) {
        this.systemURI = systemURI;
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Notation))
            return false;
        Notation other = (Notation) obj;
        if (name == null) {
            if (other.getName() != null) {
                return false;
            }
        } else if (!name.equals(other.getName())) {
            return false;
        }
        if (publicURI == null) {
            if (other.getPublic() != null) {
                return false;
            }
        } else if (!publicURI.equals(other.getPublic())) {
            return false;
        }
        if (systemURI == null) {
            if (other.getSystem() != null) {
                return false;
            }
        } else if (!systemURI.equals(other.getSystem())) {
            return false;
        }
        return true;
    }

    // Type metadata
    private static TypeDesc typeDesc;

    static {
        typeDesc = new TypeDesc(Notation.class);
        FieldDesc field;

        // An attribute with a specified QName
        field = new AttributeDesc();
        field.setFieldName("name");
        field.setXmlName(Constants.XSD_NCNAME);
        typeDesc.addFieldDesc(field);

        // An attribute with a default QName
        field = new AttributeDesc();
        field.setFieldName("public");
        field.setXmlName(Constants.XSD_ANYURI);
        typeDesc.addFieldDesc(field);

        // An element with a specified QName
        field = new ElementDesc();
        field.setFieldName("system");
        field.setXmlName(Constants.XSD_ANYURI);
        typeDesc.addFieldDesc(field);
    }

    public static TypeDesc getTypeDesc() {
        return typeDesc;
    }
}
