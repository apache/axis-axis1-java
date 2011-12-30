/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

