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
 */

public abstract class Part {

    private Message msg;
    private Hashtable headers = new Hashtable();
    private String contentId;
    private String contentLocation;
    
    private static String CONTENT_ID_HEADER = "Content-Id";
    private static String CONTENT_LOCATION_HEADER = "Content-Location";
    
    /**
     * Fill in the Message field.  (Of course this can only be called by
     * subclass constructors since Part itself is abstract.)
     */
    public Part (Message parent) {
        msg = parent;
    }

    /**
     * Add the specified MIME header, as per JAXM.
     */
    public void addMimeHeader (String header, String value) {
        headers.put(header, value);
    }

    /**
     * Get the specified MIME header.
     */
    public String getMimeHeader (String header) {
        return (String) headers.get(header);
    }
    
    /**
     * Get the Message for this Part.
     */
    public Message getMessage () {
        return msg;
    }
    
    /**
     * Add getAllMimeHeaders later....
     */ 
    
    /**
     * Content length (length in bytes of the encoded content only, no headers).
     */
    public abstract int getContentLength();

    /**
     * Total size in bytes (of all content and headers, as encoded).
     */
    public abstract int getSize();

    /**
     * Content location.
     */
    public String getContentLocation() {
        return getMimeHeader(CONTENT_LOCATION_HEADER);
    }

    /**
     * Set content location.
     */
    public void setContentLocation(String loc) {
        addMimeHeader(CONTENT_LOCATION_HEADER, loc);
    }

    /**
     * Content ID.
     */
    public String getContentId() {
        return getMimeHeader(CONTENT_ID_HEADER);
    }

    /**
     * Set content ID.
     */
    public void setContentId(String id) {
        addMimeHeader(CONTENT_ID_HEADER, id);
    }

    /**
     * Content type.
     */
    public abstract String getContentType();

    /**
     * Writing.  Writes all bytes, including headers (i.e. writes
     * getSize() bytes).
     * 
     * ROBJDO: make this package method?
     */
    public abstract void writeTo(OutputStream out) throws IOException;
}

