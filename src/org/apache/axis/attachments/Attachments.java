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
package org.apache.axis.attachments;

import org.apache.axis.Part;

/**
 * Access the Attachments of a Message.  This interface essentially
 * firewalls the rest of Axis from any dependencies on javax.activation.
 * <p>
 * If javax.activation is not available, this is the *only* class that
 * will be compiled in org.apache.axis.attachments.
 *
 * @author Rob Jellinghaus (robj@unrealities.com)
 * @author Rick Rineholt
 */
public interface Attachments extends java.io.Serializable {

    /**
     * Adds an existing attachment to this list.
     * Note: Passed part will be bound to this message.
     * @param newPart new part to add
     * @return Part old attachment with the same Content-ID, or null.
     * @throws org.apache.axis.AxisFault
     */
    public Part addAttachmentPart(Part newPart)
            throws org.apache.axis.AxisFault;

    /**
     * This method uses getAttacmentByReference() to look for attachment.
     * If attachment has been found, it will be removed from the list, and
     * returned to the user.
     *
     * @param reference The reference that referers to an attachment.
     * @return The part associated with the removed attachment, or null.
     *
     * @throws org.apache.axis.AxisFault
     */
    public Part removeAttachmentPart(String reference)
            throws org.apache.axis.AxisFault;

    /**
     * Removes all <CODE>AttachmentPart</CODE> objects that have
     *   been added to this <CODE>SOAPMessage</CODE> object.
     *
     *   <P>This method does not touch the SOAP part.</P>
     */
    public void removeAllAttachments();

    /**
     * This method should look at a refernce and determine if it is a CID: or url
     * to look for attachment.
     *
     * @param reference The reference in the xml that referers to an attachment.
     * @return The part associated with the attachment.
     *
     * @throws org.apache.axis.AxisFault
     */
    public Part getAttachmentByReference(String reference)
            throws org.apache.axis.AxisFault;

    /**
     * This method will return all attachments as a collection.
     *
     * @return A collection of attachments.
     *
     * @throws org.apache.axis.AxisFault
     */
    public java.util.Collection getAttachments()
            throws org.apache.axis.AxisFault;

    /**
     * Retrieves all the <CODE>AttachmentPart</CODE> objects
     * that have header entries that match the specified headers.
     * Note that a returned attachment could have headers in
     * addition to those specified.
     * @param   headers a <CODE>MimeHeaders</CODE>
     *     object containing the MIME headers for which to
     *     search
     * @return an iterator over all attachments that have a header
     *     that matches one of the given headers
     */
    public java.util.Iterator getAttachments(
            javax.xml.soap.MimeHeaders headers);

    /**
     * Create a new attachment Part in this Message.
     * Will actually, and always, return an AttachmentPart.
     *
     * @param part The part that is referenced
     *
     * @return a new attachment part
     *
     * @throws org.apache.axis.AxisFault
     */
    public Part createAttachmentPart(Object part)
            throws org.apache.axis.AxisFault;

    /**
     * Create a new attachment Part in this Message.
     * Will actually, and always, return an AttachmentPart.
     *
     * @return a new attachment part
     *
     * @throws org.apache.axis.AxisFault
     */
    public Part createAttachmentPart() throws org.apache.axis.AxisFault;

    /**
     *  Will the attachments of this message to that of the colleciton.
     *
     * @param parts
     *
     * @throws org.apache.axis.AxisFault
     */
    public void setAttachmentParts(java.util.Collection parts)
            throws org.apache.axis.AxisFault;

    /**
     * From the complex stream return the SOAP part.
     * @return will return the root part if the stream is supported,
     *         otherwise null.
     */
    public Part getRootPart();

    /**
     * Sets the root part of this multipart block
     *
     * @param newRoot  the new root <code>Part</code>
     */
    public void setRootPart(Part newRoot);

    /**
     * Get the content length of the stream.
     *
     * @return the content length of
     *
     * @throws org.apache.axis.AxisFault
     */
    public long getContentLength() throws org.apache.axis.AxisFault;

    /**
     * Write the content to the stream.
     *
     * @param os the stream
     *
     * @throws org.apache.axis.AxisFault
     */
    public void writeContentToStream(java.io.OutputStream os)
            throws org.apache.axis.AxisFault;

    /**
     * Write the content to the stream.
     *
     * @return the content type
     *
     * @throws org.apache.axis.AxisFault
     */
    public String getContentType() throws org.apache.axis.AxisFault;

    /**
     * This is the number of attachments.
     *
     * @return the number of attachments
     */
    public int getAttachmentCount();

    /**
     * Determine if an object is to be treated as an attchment.
     *
     * @param value the value that is to be determined if
     * its an attachment.
     *
     * @return True if value should be treated as an attchment.
     */
    public boolean isAttachment(Object value);


    /** Use the default attatchment send type. */
    public final int SEND_TYPE_NOTSET = 1;

    /** Use the SOAP with MIME attatchment send type. */
    public final int SEND_TYPE_MIME = 2; //use mime

    /** Use the DIME attatchment type. */
    public final int  SEND_TYPE_DIME= 3; //use dime;

    /** Use the DIME attatchment type. */
    public final int  SEND_TYPE_NONE= 4; //don't send as attachments

    final int SEND_TYPE_MAX = 4;

    /** The default attatchment type. MIME */
    final int SEND_TYPE_DEFAULT = SEND_TYPE_MIME;

    /** The prefix used to assoc. attachments as content-id */
    public final String CIDprefix= "cid:";

    /**
     * Set the format for attachments.
     *
     * @param sendtype the format to send.
     *      SEND_TYPE_MIME for Multipart Releated Mail type attachments.
     *      SEND_TYPE_DIME for DIME type attachments.
     */

    public void setSendType( int sendtype);

    /**
     * Determine if an object is to be treated as an attchment.
     *
     *
     * @return SEND_TYPE_MIME, SEND_TYPE_DIME,  SEND_TYPE_NOTSET
     */

    public int getSendType();

    /**
     * dispose of the attachments and their files; do not use the object
     * after making this call.
     */

    public void dispose();
}
