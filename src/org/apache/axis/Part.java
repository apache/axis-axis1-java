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

package org.apache.axis;

import org.apache.axis.utils.JavaUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

/**
 * A MIME component of an Axis Message.
 * <p>
 * Loosely based on javax.xml.soap.SOAPPart and javax.xml.soap.AttachmentPart.
 * (Why don't <i>they</i> have a Part superclass?)
 * <p>
 * In general, all methods of Part and its subclasses are (or should
 * be) implemented as efficiently as possible.  No buffering is done
 * unless necessary.  (Look-ahead (and hence buffering) is necessary
 * when calculating content length.)
 *
 * @author Rob Jellinghaus (robj@unrealities.com)
 * @author Rick Rineholt
 */

import org.apache.axis.transport.http.HTTPConstants;

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
    public abstract String[] getMimeHeader(String name);

    /**
     * Add the specified MIME header, as per JAXM.
     */
    public void addMimeHeader (String header, String value);

    /**
     * Content location.
     */
    public String getContentLocation();

    /**
     * Set content location.
     */
    public void setContentLocation(String loc);

    /**
     * Sets Content-Id of this part. 
     *  already defined.
     * @param newCid new Content-Id
     * @returns void
     */
    public void setContentId(String newCid);

    /**
     * Content ID.
     */
    public String getContentId();

    /**
     * Get all headers that match
     */
    public java.util.Iterator getMatchingMimeHeaders( final String[] match);

    /**
     * Get all headers that do not match
     */
    public java.util.Iterator getNonMatchingMimeHeaders( final String[] match);

    /**
     * Content type.
     */
    public abstract String getContentType();

    /**
     * Content ID.
     *
     * @return the contentId reference value that should be used directly
     * as an href in a SOAP element to reference this attachment.
     * <B>Not part of JAX-RPC, JAX-M, SAAJ, etc. </B>
     */
    public String getContentIdRef();
}

