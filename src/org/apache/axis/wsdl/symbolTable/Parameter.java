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
package org.apache.axis.wsdl.symbolTable;

import javax.xml.namespace.QName;

/**
  * This class simply collects
  */
public class Parameter {

    // constant values for the parameter mode.
    public static final byte IN = 1;
    public static final byte OUT = 2;
    public static final byte INOUT = 3;

    // The QName of the element associated with this param.  Defaults to
    // null, which means it'll be new QName("", name)
    private QName qname;
    
    // The part name of this parameter, just a string.
    private String name;

    // The MIME type of this parameter, null if it isn't a MIME type.
    private MimeInfo mimeInfo = null;
    
    private TypeEntry type;
    private byte mode = IN;

    // Flags indicating whether the parameter goes into the soap message as
    // a header.
    private boolean inHeader = false;
    private boolean outHeader = false;

    public String toString() {
        return "(" + type
                + (mimeInfo == null ? "" : "(" + mimeInfo + ")")
                + ", " + getName() + ", "
                + (mode == IN ? "IN)" : mode == INOUT ? "INOUT)" : "OUT)"
                + (inHeader ? "(IN soap:header)" : "")
                + (outHeader ? "(OUT soap:header)" : ""));
    } // toString

    /**
     * Get the fully qualified name of this parameter.
     */
    public QName getQName() {
        return qname;
    }

    /**
     * Get the name of this parameter.  This call is equivalent to
     * getQName().getLocalPart().
     */
    public String getName() {
        if (name == null && qname != null) {
            return qname.getLocalPart();
        }
        return name;
    }

    /**
     * Set the name of the parameter.  This replaces both the
     * name and the QName (the namespaces becomes "").
     */
    public void setName(String name) {
        this.name = name;
        if (qname == null)
            this.qname = new QName("", name);
    }

    /**
     * Set the QName of the parameter.
     */
    public void setQName(QName qname) {
        this.qname = qname;
    }

    /**
     * Get the MIME type of the parameter.
     */
    public MimeInfo getMIMEInfo() {
        return mimeInfo;
    } // getMIMEType

    /**
     * Set the MIME type of the parameter.
     */
    public void setMIMEInfo(MimeInfo mimeInfo) {
        this.mimeInfo = mimeInfo;
    } // setMIMEType

    /**
     * Get the TypeEntry of the parameter.
     */
    public TypeEntry getType() {
        return type;
    }

    /**
     * Set the TypeEntry of the parameter.
     */
    public void setType(TypeEntry type) {
        this.type = type;
    }

    /**
     * Get the mode (IN, INOUT, OUT) of the parameter.
     */
    public byte getMode() {
        return mode;
    }

    /**
     * Set the mode (IN, INOUT, OUT) of the parameter.  If the input
     * to this method is not one of IN, INOUT, OUT, then the value
     * remains unchanged.
     */
    public void setMode(byte mode) {
        if (mode <= INOUT & mode >= IN) {
            this.mode = mode;
        }
    }

    /**
     * Is this parameter in the input message header?
     */
    public boolean isInHeader() {
        return inHeader;
    } // isInHeader

    /**
     * Set the inHeader flag for this parameter.
     */
    public void setInHeader(boolean inHeader) {
        this.inHeader = inHeader;
    } // setInHeader

    /**
     * Is this parameter in the output message header?
     */
    public boolean isOutHeader() {
        return outHeader;
    } // isOutHeader

    /**
     * Set the outHeader flag for this parameter.
     */
    public void setOutHeader(boolean outHeader) {
        this.outHeader = outHeader;
    } // setOutHeader
} // class Parameter
