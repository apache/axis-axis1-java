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

package org.apache.axis;


/**
 * A part of a MIME message. Typically, in a MIME message there will be one
 * <code>SOAPPart</code> containing the SOAP message, and 0 or more
 * <code>AttachmentParts</code> instances containing each of the attachments.
 */
public interface Part extends java.io.Serializable
{
    /**
     * Gets all the values of the <CODE>MimeHeader</CODE> object
     * in this <CODE>SOAPPart</CODE> object that is identified by
     * the given <CODE>String</CODE>.
     * @param   name  the name of the header; example:
     *     "Content-Type"
     * @return a <CODE>String</CODE> array giving all the values for
     *     the specified header
     * @see #setMimeHeader(java.lang.String, java.lang.String) setMimeHeader(java.lang.String, java.lang.String)
     */
    public String[] getMimeHeader(String name);

    // fixme: no explicit method to get the value associated with a header e.g.
    //  String getMimeHeader(header)
    /**
     * Add the specified MIME header, as per JAXM.
     *
     * @param header  the MIME header name
     * @param value   the value associated with the header
     */
    public void addMimeHeader (String header, String value);

    // fixme: what do we mean by location? Is this a URL, a locator in a stream,
    //  a place in the xml? something else?
    /**
     * Get the content location.
     *
     * @return a <code>String</code> giving the location
     */
    public String getContentLocation();

    /**
     * Set content location.
     *
     * @param loc  the new location
     */
    public void setContentLocation(String loc);

    // fixme: confusing docs - what's going on here?
    /**
     * Sets Content-Id of this part.
     *  already defined.
     * @param newCid new Content-Id
     */
    public void setContentId(String newCid);

    /**
     * Get the content ID.
     *
     * @return the content ID
     */
    public String getContentId();

    // for these 2 methods...
    // fixme: is this an iterator over mime header names or values?
    // fixme: why this API rather than just exposing the header names, and
    //  a method to fetch the value for a name?
    /**
     * Get an <code>Iterator</code> over all headers that match any item in
     * <code>match</code>.
     */
    public java.util.Iterator getMatchingMimeHeaders( final String[] match);

    /**
     * Get all headers that do not match.
     */
    public java.util.Iterator getNonMatchingMimeHeaders( final String[] match);

    // fixke: are content types MIME types or something else, or what?
    /**
     * Get the content type.
     *
     * @return the content type <code>String</code>
     */
    public String getContentType();

    /**
     * Content ID.
     *
     * @return the contentId reference value that should be used directly
     * as an href in a SOAP element to reference this attachment.
     * <B>Not part of JAX-RPC, JAX-M, SAAJ, etc. </B>
     */
    public String getContentIdRef();
}

