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
package org.apache.axis.attachments;

import org.apache.axis.Part;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.SessionUtils;
import org.apache.commons.logging.Log;

import javax.activation.DataHandler;
import javax.xml.soap.SOAPException;
import java.util.Iterator;
import java.io.ByteArrayOutputStream;

/**
 * Class AttachmentPart
 *
 */
public class AttachmentPart extends javax.xml.soap.AttachmentPart
        implements Part {

    /** Field log           */
    protected static Log log =
            LogFactory.getLog(AttachmentPart.class.getName());

    /** Field datahandler           */
    javax.activation.DataHandler datahandler = null;

    /** Field mimeHeaders           */
    private javax.xml.soap.MimeHeaders mimeHeaders =
            new javax.xml.soap.MimeHeaders();

    /** Field contentObject */
    private Object contentObject;

    /**
     * Constructor AttachmentPart
     */
    public AttachmentPart() {
        setMimeHeader(HTTPConstants.HEADER_CONTENT_ID, SessionUtils.generateSessionId());
    }

    /**
     * Constructor AttachmentPart
     *
     * @param dh
     */
    public AttachmentPart(javax.activation.DataHandler dh) {
        setMimeHeader(HTTPConstants.HEADER_CONTENT_ID,
                SessionUtils.generateSessionId());
        datahandler = dh;
        if(dh != null)
            setMimeHeader(HTTPConstants.HEADER_CONTENT_TYPE, dh.getContentType());
    }

    /**
     * Method getActivationDataHandler
     *
     * @return
     */
    public javax.activation.DataHandler getActivationDataHandler() {
        return datahandler;
    }

    /**
     * getContentType
     *
     * @return content type
     */
    public String getContentType() {
        return getFirstMimeHeader(HTTPConstants.HEADER_CONTENT_TYPE);
    }

    /**
     * Add the specified MIME header, as per JAXM.
     *
     * @param header
     * @param value
     */
    public void addMimeHeader(String header, String value) {
        mimeHeaders.addHeader(header, value);
    }

    /**
     * Get the specified MIME header.
     *
     * @param header
     *
     * @return
     */
    public String getFirstMimeHeader(String header) {
        String[] values = mimeHeaders.getHeader(header.toLowerCase());
        if ((values != null) && (values.length > 0)) {
            return values[0];
        }
        return null;
    }

    /**
     * check if this Part's mimeheaders matches the one passed in.
     * TODO: Am not sure about the logic.
     */
    public boolean matches(javax.xml.soap.MimeHeaders headers) {
        for (Iterator i = headers.getAllHeaders(); i.hasNext();) {
            javax.xml.soap.MimeHeader hdr = (javax.xml.soap.MimeHeader) i.next();
            String values[] = mimeHeaders.getHeader(hdr.getName());
            boolean found = false;
            if (values != null) {
                for (int j = 0; j < values.length; j++) {
                    if (!hdr.getValue().equalsIgnoreCase(values[j]))
                        continue;
                    found = true;
                    break;
                }
            }
            if (!found)
                return false;
        }
        return true;
    }

    /**
     * Content location.
     */
    public String getContentLocation() {
        return getFirstMimeHeader(HTTPConstants.HEADER_CONTENT_LOCATION);
    }

    /**
     * Set content location.
     *
     * @param loc
     */
    public void setContentLocation(String loc) {
        setMimeHeader(HTTPConstants.HEADER_CONTENT_LOCATION, loc);
    }

    /**
     *     Sets Content-Id of this part.
     *      already defined.
     *     @param newCid new Content-Id
     */
    public void setContentId(String newCid) {
        setMimeHeader(HTTPConstants.HEADER_CONTENT_ID, newCid);
    }

    /**
     * Content ID.
     *
     * @return
     */
    public String getContentId() {
        return getFirstMimeHeader(HTTPConstants.HEADER_CONTENT_ID);
    }

    /**
     * Get all headers that match
     *
     * @param match
     *
     * @return
     */
    public java.util.Iterator getMatchingMimeHeaders(final String[] match) {
        return mimeHeaders.getMatchingHeaders(match);
    }

    /**
     * Get all headers that do not match
     *
     * @param match
     *
     * @return
     */
    public java.util.Iterator getNonMatchingMimeHeaders(final String[] match) {
        return mimeHeaders.getNonMatchingHeaders(match);
    }

    /**
     * Retrieves all the headers for this <CODE>
     * AttachmentPart</CODE> object as an iterator over the <CODE>
     * MimeHeader</CODE> objects.
     * @return  an <CODE>Iterator</CODE> object with all of the Mime
     *     headers for this <CODE>AttachmentPart</CODE> object
     */
    public Iterator getAllMimeHeaders() {
        return mimeHeaders.getAllHeaders();
    }

    /**
     * Changes the first header entry that matches the given name
     *   to the given value, adding a new header if no existing
     *   header matches. This method also removes all matching
     *   headers but the first.
     *
     *   <P>Note that RFC822 headers can only contain US-ASCII
     *   characters.</P>
     * @param  name   a <CODE>String</CODE> giving the
     *     name of the header for which to search
     * @param  value  a <CODE>String</CODE> giving the
     *     value to be set for the header whose name matches the
     *     given name
     * @throws java.lang.IllegalArgumentException if
     *     there was a problem with the specified mime header name
     *     or value
     */
    public void setMimeHeader(String name, String value) {
        mimeHeaders.setHeader(name, value);
    }

    /** Removes all the MIME header entries. */
    public void removeAllMimeHeaders() {
        mimeHeaders.removeAllHeaders();
    }

    /**
     * Removes all MIME headers that match the given name.
     * @param  header - the string name of the MIME
     *     header/s to be removed
     */
    public void removeMimeHeader(String header) {
        mimeHeaders.removeHeader(header);
    }

    /**
     * Gets the <CODE>DataHandler</CODE> object for this <CODE>
     * AttachmentPart</CODE> object.
     * @return the <CODE>DataHandler</CODE> object associated with
     *     this <CODE>AttachmentPart</CODE> object
     * @throws  SOAPException  if there is
     *     no data in this <CODE>AttachmentPart</CODE> object
     */
    public DataHandler getDataHandler() throws SOAPException {
        if(datahandler == null) {
            throw new SOAPException(Messages.getMessage("noContent"));
        }
        return datahandler;
    }

    /**
     * Sets the given <CODE>DataHandler</CODE> object as the
     * data handler for this <CODE>AttachmentPart</CODE> object.
     * Typically, on an incoming message, the data handler is
     * automatically set. When a message is being created and
     * populated with content, the <CODE>setDataHandler</CODE>
     * method can be used to get data from various data sources into
     * the message.
     * @param  datahandler  <CODE>DataHandler</CODE> object to
     *     be set
     * @throws java.lang.IllegalArgumentException if
     *     there was a problem with the specified <CODE>
     *     DataHandler</CODE> object
     */
    public void setDataHandler(DataHandler datahandler) {
        if(datahandler == null)
            throw new java.lang.IllegalArgumentException(
                Messages.getMessage("illegalArgumentException00"));
        this.datahandler = datahandler;
        setMimeHeader(HTTPConstants.HEADER_CONTENT_TYPE, datahandler.getContentType());
    }

    /**
     * Gets the content of this <CODE>AttachmentPart</CODE> object
     *   as a Java object. The type of the returned Java object
     *   depends on (1) the <CODE>DataContentHandler</CODE> object
     *   that is used to interpret the bytes and (2) the <CODE>
     *   Content-Type</CODE> given in the header.
     *
     *   <P>For the MIME content types "text/plain", "text/html" and
     *   "text/xml", the <CODE>DataContentHandler</CODE> object does
     *   the conversions to and from the Java types corresponding to
     *   the MIME types. For other MIME types,the <CODE>
     *   DataContentHandler</CODE> object can return an <CODE>
     *   InputStream</CODE> object that contains the content data as
     *   raw bytes.</P>
     *
     *   <P>A JAXM-compliant implementation must, as a minimum,
     *   return a <CODE>java.lang.String</CODE> object corresponding
     *   to any content stream with a <CODE>Content-Type</CODE>
     *   value of <CODE>text/plain</CODE> and a <CODE>
     *   javax.xml.transform.StreamSource</CODE> object
     *   corresponding to a content stream with a <CODE>
     *   Content-Type</CODE> value of <CODE>text/xml</CODE>. For
     *   those content types that an installed <CODE>
     *   DataContentHandler</CODE> object does not understand, the
     *   <CODE>DataContentHandler</CODE> object is required to
     *   return a <CODE>java.io.InputStream</CODE> object with the
     *   raw bytes.</P>
     * @return a Java object with the content of this <CODE>
     *     AttachmentPart</CODE> object
     * @throws  SOAPException  if there is no content set
     *     into this <CODE>AttachmentPart</CODE> object or if there
     *     was a data transformation error
     */
    public Object getContent() throws SOAPException {
        if(contentObject != null)
            return contentObject;

        if(datahandler == null) {
            throw new SOAPException(Messages.getMessage("noContent"));
        }

        javax.activation.DataSource ds = datahandler.getDataSource();
        if (ds instanceof ManagedMemoryDataSource) {
            ManagedMemoryDataSource mds = (ManagedMemoryDataSource) ds;

            if (ds.getContentType().equals("text/plain")) {
                try {
                    java.io.InputStream is = ds.getInputStream();
                    byte[] bytes = new byte[is.available()];

                    is.read(bytes);

                    return new String(bytes);
                } catch (java.io.IOException io) {
                    log.error(Messages.getMessage("javaIOException00"), io);
                }
            }
        }
        return null;
    }

    /**
     * Sets the content of this attachment part to that of the
     * given <CODE>Object</CODE> and sets the value of the <CODE>
     * Content-Type</CODE> header to the given type. The type of the
     * <CODE>Object</CODE> should correspond to the value given for
     * the <CODE>Content-Type</CODE>. This depends on the particular
     * set of <CODE>DataContentHandler</CODE> objects in use.
     * @param  object  the Java object that makes up
     *     the content for this attachment part
     * @param  contentType the MIME string that
     *     specifies the type of the content
     * @throws java.lang.IllegalArgumentException if
     *     the contentType does not match the type of the content
     *     object, or if there was no <CODE>
     *     DataContentHandler</CODE> object for this content
     *     object
     * @see #getContent() getContent()
     */
    public void setContent(Object object, String contentType) {
        if (object instanceof String) {
            try {
                String s = (String) object;
                java.io.ByteArrayInputStream bais =
                        new java.io.ByteArrayInputStream(s.getBytes());
                datahandler = new DataHandler(new ManagedMemoryDataSource(bais,
                        ManagedMemoryDataSource.MAX_MEMORY_DISK_CACHED, contentType, true));
                contentObject = object;
                return;
            } catch (java.io.IOException io) {
                log.error(Messages.getMessage("javaIOException00"), io);
                throw new java.lang.IllegalArgumentException(
                        Messages.getMessage("illegalArgumentException00"));
            }
        } else if (object instanceof java.io.InputStream) {
                try {
                    datahandler = new DataHandler(new ManagedMemoryDataSource((java.io.InputStream)object,
                            ManagedMemoryDataSource.MAX_MEMORY_DISK_CACHED, contentType, true));
                    contentObject = object;
                    return;
                } catch (java.io.IOException io) {
                    log.error(Messages.getMessage("javaIOException00"), io);
                    throw new java.lang.IllegalArgumentException(
                            Messages.getMessage("illegalArgumentException00"));
                }
        } else {
            throw new java.lang.IllegalArgumentException(
                    Messages.getMessage("illegalArgumentException00"));
        }
    }

    /**
     * Clears out the content of this <CODE>
     * AttachmentPart</CODE> object. The MIME header portion is left
     * untouched.
     */
    public void clearContent() {
        datahandler = null;
        contentObject = null;
    }

    /**
     * Returns the number of bytes in this <CODE>
     * AttachmentPart</CODE> object.
     * @return the size of this <CODE>AttachmentPart</CODE> object
     *     in bytes or -1 if the size cannot be determined
     * @throws  SOAPException  if the content of this
     *     attachment is corrupted of if there was an exception
     *     while trying to determine the size.
     */
    public int getSize() throws SOAPException {
        if (datahandler == null)
            return 0;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            datahandler.writeTo(bout);
        } catch (java.io.IOException ex) {
            log.error(Messages.getMessage("javaIOException00"), ex);
            throw new SOAPException(Messages.getMessage("javaIOException01", ex.getMessage()), ex);
        }
        return bout.size();
    }

    /**
     * Gets all the values of the header identified by the given
     * <CODE>String</CODE>.
     * @param   name  the name of the header; example:
     *     "Content-Type"
     * @return a <CODE>String</CODE> array giving the value for the
     *     specified header
     * @see #setMimeHeader(java.lang.String, java.lang.String) setMimeHeader(java.lang.String, java.lang.String)
     */
    public String[] getMimeHeader(String name) {
        return mimeHeaders.getHeader(name);
    }

    /**
     * Content ID.
     *
     * @return the contentId reference value that should be used directly
     * as an href in a SOAP element to reference this attachment.
     * <B>Not part of JAX-RPC, JAX-M, SAAJ, etc. </B>
     */
    public String getContentIdRef() {
      return Attachments.CIDprefix + getContentId();
    }
}
