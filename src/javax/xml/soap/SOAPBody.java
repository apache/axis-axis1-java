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
package javax.xml.soap;

/**
 * An object that represents the contents of the SOAP body
 * element in a SOAP message. A SOAP body element consists of XML data
 * that affects the way the application-specific content is processed.
 * <P>
 * A <code>SOAPBody</code> object contains <code>SOAPBodyElement</code>
 * objects, which have the content for the SOAP body.
 * A <code>SOAPFaultException</code> object, which carries status and/or
 * error information, is an example of a <code>SOAPBodyElement</code> object.
 * @see javax.xml.rpc.soap.SOAPFaultException SOAPFaultException
 */
public interface SOAPBody extends SOAPElement {

    /**
     * Creates a new <code>SOAPFaultException</code> object and adds it to
     * this <code>SOAPBody</code> object.
     * @return the new <code>SOAPFaultException</code> object
     * @throws  SOAPException if there is a SOAP error
     */
    public abstract SOAPFault addFault() throws SOAPException;

    /**
     * Indicates whether a <code>SOAPFaultException</code> object exists in
     * this <code>SOAPBody</code> object.
     * @return <code>true</code> if a <code>SOAPFaultException</code> object exists in
     *     this <code>SOAPBody</code> object; <code>false</code>
     *     otherwise
     */
    public abstract boolean hasFault();

    /**
     * Returns the <code>SOAPFaultException</code> object in this <code>SOAPBody</code>
     * object.
     * @return the <code>SOAPFaultException</code> object in this <code>SOAPBody</code>
     *    object
     */
    public abstract SOAPFault getFault();

    /**
     * Creates a new <code>SOAPBodyElement</code> object with the
     * specified name and adds it to this <code>SOAPBody</code> object.
     * @param name a <code>Name</code> object with the name for the new
     *   <code>SOAPBodyElement</code> object
     * @return the new <code>SOAPBodyElement</code> object
     * @throws SOAPException  if a SOAP error occurs
     */
    public abstract SOAPBodyElement addBodyElement(Name name)
        throws SOAPException;
}
