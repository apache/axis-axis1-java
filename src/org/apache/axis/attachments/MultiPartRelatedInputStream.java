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
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.IOUtils;
import org.apache.commons.logging.Log;

import javax.activation.DataHandler;
import javax.mail.internet.MimeUtility;
import java.io.IOException;

/**
 * This simulates the multipart stream
 *
 * @author Rick Rineholt
 */
public class MultiPartRelatedInputStream extends MultiPartInputStream{

    /** Field log           */
    protected static Log log =
            LogFactory.getLog(MultiPartRelatedInputStream.class.getName());

    /** Field MIME_MULTIPART_RELATED           */
    public static final String MIME_MULTIPART_RELATED = "multipart/related";

    /** Field parts           */
    protected java.util.HashMap parts = new java.util.HashMap();

    /** Field orderedParts           */
    protected java.util.LinkedList orderedParts = new java.util.LinkedList();

    /** Field rootPartLength           */
    protected int rootPartLength = 0;

    /** Field closed           */
    protected boolean closed = false;    // If true the stream has been closed.

    /** Field eos           */
    protected boolean eos =
            false;    // This is set once the SOAP packet has reached the end of stream.

    // protected java.io.InputStream is = null; //The orginal multipart/related stream.
    // This stream controls and manages the  boundary.

    /** Field boundaryDelimitedStream           */
    protected org.apache.axis.attachments.BoundaryDelimitedStream boundaryDelimitedStream =
            null;

    /** Field soapStream           */
    protected java.io.InputStream soapStream =
            null;                            // Set the soap stream once found.

    /** Field soapStreamBDS           */
    protected java.io.InputStream soapStreamBDS =
            null;    // Set to the boundary delimited stream assoc. with soap stream once found.

    /** Field boundary           */
    protected byte[] boundary = null;

    /** Field cachedSOAPEnvelope           */
    protected java.io.ByteArrayInputStream cachedSOAPEnvelope =
            null;                            // Caches the soap stream if it is

    // Still open and a reference to read data in a later attachment occurs.

    /** Field contentLocation           */
    protected String contentLocation = null;

    /** Field contentId           */
    protected String contentId = null;

    /**
     * Multipart stream.
     * @param the string that holds the contentType
     *
     * @param contentType
     * @param is the true input stream from where the source.
     *
     * @throws org.apache.axis.AxisFault
     */
    public MultiPartRelatedInputStream(
            String contentType, java.io.InputStream stream)
            throws org.apache.axis.AxisFault {

        super(null);    // don't cache this stream.

        try {
            
            // First find the start and boundary parameters. There are real weird rules regard what
            // can be in real headers what needs to be escaped etc  let mail parse it.
            javax.mail.internet.ContentType ct =
                    new javax.mail.internet.ContentType(contentType);
            String rootPartContentId =
                    ct.getParameter("start");       // Get the root part content.

            if (rootPartContentId != null) {
                rootPartContentId = rootPartContentId.trim();

                if (rootPartContentId.startsWith("<")) {
                    rootPartContentId = rootPartContentId.substring(1);
                }

                if (rootPartContentId.endsWith(">")) {
                    rootPartContentId = rootPartContentId.substring(0,
                            rootPartContentId.length() - 1);
                }

            }

            if(ct.getParameter("boundary") != null) {
                String boundaryStr =
                        "--"
                        + ct.getParameter(
                                "boundary");    // The boundary with -- add as always the case.


                // if start is null then the first attachment is the rootpart
                // First read the start boundary -- this is done with brute force since the servlet may swallow the crlf between headers.
                // after this we use the more efficient boundarydelimeted stream.  There should never be any data here anyway.
                byte[][] boundaryMarker = new byte[2][boundaryStr.length() + 2];

                IOUtils.readFully(stream, boundaryMarker[0]);

                boundary = (boundaryStr + "\r\n").getBytes("US-ASCII");

                int current = 0;

                // This just goes brute force one byte at a time to find the first boundary.
                // in most cases this just a crlf.
                for (boolean found = false; !found; ++current) {
                    if (!(found =
                            java.util.Arrays.equals(boundaryMarker[current & 0x1],
                                    boundary))) {
                        System.arraycopy(boundaryMarker[current & 0x1], 1,
                                boundaryMarker[(current + 1) & 0x1], 0,
                                boundaryMarker[0].length - 1);

                        if (stream.read(
                                boundaryMarker[(current + 1) & 0x1],
                                boundaryMarker[0].length - 1, 1) < 1) {
                            throw new org.apache.axis.AxisFault(
                                    Messages.getMessage(
                                            "mimeErrorNoBoundary", new String(boundary)));
                        }
                    }
                }

                // after the first boundary each boundary will have a cr lf at the beginning since after the data in any part there
                // is a cr lf added to put the boundary at the begining of a line.
                boundaryStr = "\r\n" + boundaryStr;
                boundary = boundaryStr.getBytes("US-ASCII");
            } else {
                // Since boundary is not specified, we try to find one.
                for (boolean found = false; !found;) {
                    boundary= readLine(stream);
                    if( boundary == null)
                        throw new org.apache.axis.AxisFault(
                                Messages.getMessage(
                                        "mimeErrorNoBoundary", "--"));
                     found = boundary.length >4  && boundary[2] == '-' &&  boundary[3]== '-'; 
                }
              }

            // create the boundary delmited stream.
            boundaryDelimitedStream =
                    new org.apache.axis.attachments.BoundaryDelimitedStream(stream,
                            boundary, 1024);

            // Now read through all potential streams until we have found the root part.
            String contentTransferEncoding = null;

            do {
                contentId = null;
                contentLocation = null;
                contentTransferEncoding = null;

                // Read this attachments headers from the stream.
                javax.mail.internet.InternetHeaders headers =
                        new javax.mail.internet.InternetHeaders(
                                boundaryDelimitedStream);

                // Use java mail utility to read through the headers.
                contentId = headers.getHeader(HTTPConstants.HEADER_CONTENT_ID,
                        null);

                // Clean up the headers and remove any < >
                if (contentId != null) {
                    contentId = contentId.trim();

                    if (contentId.startsWith("<")) {
                        contentId = contentId.substring(1);
                    }

                    if (contentId.endsWith(">")) {
                        contentId = contentId.substring(0, contentId.length()
                                - 1);
                    }

                    contentId = contentId.trim();

                  //  if (!contentId.startsWith("cid:")) {
                  //      contentId =
                  //              "cid:"
                  //              + contentId;        // make sure its identified as cid
                  //  }
                }

                contentLocation =
                        headers.getHeader(HTTPConstants.HEADER_CONTENT_LOCATION,
                                null);

                if (contentLocation != null) {
                    contentLocation = contentLocation.trim();

                    if (contentLocation.startsWith("<")) {
                        contentLocation = contentLocation.substring(1);
                    }

                    if (contentLocation.endsWith(">")) {
                        contentLocation = contentLocation.substring(
                                0, contentLocation.length() - 1);
                    }

                    contentLocation = contentLocation.trim();
                }

                contentType =
                        headers.getHeader(HTTPConstants.HEADER_CONTENT_TYPE, null);

                if (contentType != null) {
                    contentType = contentType.trim();
                }

                contentTransferEncoding = headers.getHeader(
                        HTTPConstants.HEADER_CONTENT_TRANSFER_ENCODING, null);

                if (contentTransferEncoding != null) {
                    contentTransferEncoding = contentTransferEncoding.trim();
                }

                java.io.InputStream decodedStream = boundaryDelimitedStream;

                if ((contentTransferEncoding != null)
                        && (0 != contentTransferEncoding.length())) {
                    decodedStream = MimeUtility.decode(decodedStream,
                            contentTransferEncoding);
                }

                if ((rootPartContentId != null) && !rootPartContentId.equals(
                        contentId)) {    // This is a part that has come in prior to the root part. Need to buffer it up.
                    javax.activation.DataHandler dh =
                            new javax.activation.DataHandler(
                                    new org.apache.axis.attachments.ManagedMemoryDataSource(
                                            decodedStream, 16 * 1024, contentType, true));
                    AttachmentPart ap = new AttachmentPart(dh);

                    if (contentId != null) {
                        ap.setMimeHeader(HTTPConstants.HEADER_CONTENT_ID,
                                contentId);
                    }

                    if (contentLocation != null) {
                        ap.setMimeHeader(HTTPConstants.HEADER_CONTENT_LOCATION,
                                contentLocation);
                    }

                    for (java.util.Enumeration en =
                            headers.getNonMatchingHeaders(new String[]{
                                HTTPConstants.HEADER_CONTENT_ID,
                                HTTPConstants.HEADER_CONTENT_LOCATION,
                                HTTPConstants.HEADER_CONTENT_TYPE}); en.hasMoreElements();) {
                        javax.mail.Header header =
                                (javax.mail.Header) en.nextElement();
                        String name = header.getName();
                        String value = header.getValue();

                        if ((name != null) && (value != null)) {
                            name = name.trim();

                            if (name.length() != 0) {
                                ap.addMimeHeader(name, value);
                            }
                        }
                    }

                    addPart(contentId, contentLocation, ap);

                    boundaryDelimitedStream =
                            boundaryDelimitedStream.getNextStream();    // Gets the next stream.
                }
            } while ((null != boundaryDelimitedStream)
                    && (rootPartContentId != null)
                    && !rootPartContentId.equals(contentId));

            if (boundaryDelimitedStream == null) {
                throw new org.apache.axis.AxisFault(
                        Messages.getMessage("noRoot", rootPartContentId));
            }

            soapStreamBDS = boundaryDelimitedStream;

            if ((contentTransferEncoding != null)
                    && (0 != contentTransferEncoding.length())) {
                soapStream = MimeUtility.decode(boundaryDelimitedStream,
                        contentTransferEncoding);
            } else {
                soapStream =
                        boundaryDelimitedStream;    // This should be the SOAP part
            }

            // Read from the input stream all attachments prior to the root part.
        } catch (javax.mail.internet.ParseException e) {
            throw new org.apache.axis.AxisFault(
                    Messages.getMessage("mimeErrorParsing", e.getMessage()));
        } catch (java.io.IOException e) {
            throw new org.apache.axis.AxisFault(
                    Messages.getMessage("readError", e.getMessage()));
        } catch (javax.mail.MessagingException e) {
            throw new org.apache.axis.AxisFault(
                    Messages.getMessage("readError", e.getMessage()));
        }
    }

    //when searching for a MIME boundary it MUST be terminated with CR LF. LF alone is NOT sufficient.
    private final byte[] readLine(java.io.InputStream is) throws IOException {

        java.io.ByteArrayOutputStream input = new java.io.ByteArrayOutputStream(1024);
        int c = 0;
        input.write('\r');
        input.write('\n');

        int next = -1;
        for (;c != -1;) {
            c = -1 != next ? next :  is.read();
            next = -1;
            switch (c) {
                case -1:
                break;
                case '\r':
                    next = is.read();
                    if(next == '\n')  //found a line.
                        return input.toByteArray();
                    if(next == -1)  return null;
                    //fall through
                default:
                    input.write((byte)c);
                break;
            }
        }
        //even if there is stuff in buffer if EOF then this can't be a boundary.
        return null; 
    }

    /**
     * Method getAttachmentByReference
     *
     * @param id
     *
     * @return the attachment Part
     *
     * @throws org.apache.axis.AxisFault
     */
    public Part getAttachmentByReference(final String[] id)
            throws org.apache.axis.AxisFault {

        // First see if we have read it in yet.
        Part ret = null;

        for (int i = id.length - 1; (ret == null) && (i > -1); --i) {
            ret = (AttachmentPart) parts.get(id[i]);
        }

        if (null == ret) {
            ret = readTillFound(id);
        }

        log.debug(Messages.getMessage("return02",
                "getAttachmentByReference(\"" + id
                + "\"", ((ret == null)
                ? "null"
                : ret.toString())));

        return ret;
    }

    /**
     * Method addPart
     *
     * @param contentId
     * @param locationId
     * @param ap
     */
    protected void addPart(String contentId, String locationId,
                           AttachmentPart ap) {

        if ((contentId != null) && (contentId.trim().length() != 0)) {
            parts.put(contentId, ap);
        }

        if ((locationId != null) && (locationId.trim().length() != 0)) {
            parts.put(locationId, ap);
        }

        orderedParts.add(ap);
    }

    /** Field READ_ALL           */
    protected static final String[] READ_ALL = {
        " * \0 ".intern()};    // Shouldn't never match

    /**
     * Method readAll
     *
     * @throws org.apache.axis.AxisFault
     */
    protected void readAll() throws org.apache.axis.AxisFault {
        readTillFound(READ_ALL);
    }

    /**
     * Method getAttachments
     *
     * @return the collection of attachments
     *
     * @throws org.apache.axis.AxisFault
     */
    public java.util.Collection getAttachments()
            throws org.apache.axis.AxisFault {

        readAll();

        return orderedParts;
    }

    /**
     * This will read streams in till the one that is needed is found.
     *
     * @param id id is the stream being sought.
     *
     * @return the part for the id
     *
     * @throws org.apache.axis.AxisFault
     */
    protected Part readTillFound(final String[] id)
            throws org.apache.axis.AxisFault {

        if (boundaryDelimitedStream == null) {
            return null;    // The whole stream has been consumed already
        }

        Part ret = null;

        try {
            if (soapStreamBDS
                    == boundaryDelimitedStream) {    // Still on the SOAP stream.
                if (!eos) {    // The SOAP packet has not been fully read yet. Need to store it away.
                    java.io.ByteArrayOutputStream soapdata =
                            new java.io.ByteArrayOutputStream(1024 * 8);
                    byte[] buf =
                            new byte[1024 * 16];
                    int byteread = 0;

                    do {
                        byteread = soapStream.read(buf);

                        if (byteread > 0) {
                            soapdata.write(buf, 0, byteread);
                        }
                    } while (byteread > -1);

                    soapdata.close();

                    soapStream = new java.io.ByteArrayInputStream(
                            soapdata.toByteArray());
                }

                boundaryDelimitedStream =
                        boundaryDelimitedStream.getNextStream();
            }

            // Now start searching for the data.
            if (null != boundaryDelimitedStream) {
                do {
                    String contentType = null;
                    String contentId = null;
                    String contentTransferEncoding = null;
                    String contentLocation = null;

                    // Read this attachments headers from the stream.
                    javax.mail.internet.InternetHeaders headers =
                            new javax.mail.internet.InternetHeaders(
                                    boundaryDelimitedStream);

                    contentId = headers.getHeader("Content-Id", null);

                    if (contentId != null) {
                        contentId = contentId.trim();

                        if (contentId.startsWith("<")) {
                            contentId = contentId.substring(1);
                        }

                        if (contentId.endsWith(">")) {
                            contentId =
                                    contentId.substring(0, contentId.length() - 1);
                        }

                     //   if (!contentId.startsWith("cid:")) {
                     //       contentId = "cid:" + contentId;
                     //   }

                        contentId = contentId.trim();
                    }

                    contentType =
                            headers.getHeader(HTTPConstants.HEADER_CONTENT_TYPE,
                                    null);

                    if (contentType != null) {
                        contentType = contentType.trim();
                    }

                    contentLocation =
                            headers.getHeader(HTTPConstants.HEADER_CONTENT_LOCATION,
                                    null);

                    if (contentLocation != null) {
                        contentLocation = contentLocation.trim();
                    }

                    contentTransferEncoding = headers.getHeader(
                            HTTPConstants.HEADER_CONTENT_TRANSFER_ENCODING, null);

                    if (contentTransferEncoding != null) {
                        contentTransferEncoding =
                                contentTransferEncoding.trim();
                    }

                    java.io.InputStream decodedStream = boundaryDelimitedStream;

                    if ((contentTransferEncoding != null)
                            && (0 != contentTransferEncoding.length())) {
                        decodedStream =
                                MimeUtility.decode(decodedStream,
                                        contentTransferEncoding);
                    }

                    ManagedMemoryDataSource source = new ManagedMemoryDataSource(
                                                        decodedStream, ManagedMemoryDataSource.MAX_MEMORY_DISK_CACHED, contentType, true);
                    DataHandler dh = new DataHandler(source);
                    AttachmentPart ap = new AttachmentPart(dh);

                    if (contentId != null) {
                        ap.setMimeHeader(HTTPConstants.HEADER_CONTENT_ID,
                                contentId);
                    }

                    if (contentLocation != null) {
                        ap.setMimeHeader(HTTPConstants.HEADER_CONTENT_LOCATION,
                                contentLocation);
                    }

                    for (java.util.Enumeration en =
                            headers.getNonMatchingHeaders(new String[]{
                                HTTPConstants.HEADER_CONTENT_ID,
                                HTTPConstants.HEADER_CONTENT_LOCATION,
                                HTTPConstants.HEADER_CONTENT_TYPE}); en.hasMoreElements();) {
                        javax.mail.Header header =
                                (javax.mail.Header) en.nextElement();
                        String name = header.getName();
                        String value = header.getValue();

                        if ((name != null) && (value != null)) {
                            name = name.trim();

                            if (name.length() != 0) {
                                ap.addMimeHeader(name, value);
                            }
                        }
                    }

                    addPart(contentId, contentLocation, ap);

                    for (int i = id.length - 1; (ret == null) && (i > -1);
                         --i) {
                        if ((contentId != null) && id[i].equals(
                                contentId)) {    // This is the part being sought
                            ret = ap;
                        } else if ((contentLocation != null)
                                && id[i].equals(contentLocation)) {
                            ret = ap;
                        }
                    }

                    boundaryDelimitedStream =
                            boundaryDelimitedStream.getNextStream();
                } while ((null == ret) && (null != boundaryDelimitedStream));
            }
        } catch (Exception e) {
            throw org.apache.axis.AxisFault.makeFault(e);
        }

        return ret;
    }

    /**
     * Return the content location.
     * @return the Content-Location of the stream.
     *   Null if no content-location specified.
     */
    public String getContentLocation() {
        return contentLocation;
    }

    /**
     * Return the content id of the stream
     * @return the Content-Location of the stream.
     *   Null if no content-location specified.
     */
    public String getContentId() {
        return contentId;
    }

    /**
     * Read the root stream.
     *
     * @param b
     * @param off
     * @param len
     *
     * @return
     *
     * @throws java.io.IOException
     */
    public int read(byte[] b, int off, int len) throws java.io.IOException {

        if (closed) {
            throw new java.io.IOException(Messages.getMessage("streamClosed"));
        }

        if (eos) {
            return -1;
        }

        int read = soapStream.read(b, off, len);

        if (read < 0) {
            eos = true;
        }

        return read;
    }

    /**
     * Method read
     *
     * @param b
     *
     * @return
     *
     * @throws java.io.IOException
     */
    public int read(byte[] b) throws java.io.IOException {
        return read(b, 0, b.length);
    }

    /**
     * Method read
     *
     * @return
     *
     * @throws java.io.IOException
     */
    public int read() throws java.io.IOException {

        if (closed) {
            throw new java.io.IOException(Messages.getMessage("streamClosed"));
        }

        if (eos) {
            return -1;
        }

        int ret = soapStream.read();

        if (ret < 0) {
            eos = true;
        }

        return ret;
    }

    /**
     * Method close
     *
     * @throws java.io.IOException
     */
    public void close() throws java.io.IOException {

        closed = true;

        soapStream.close();
    }

    /**
     * Available test is used by Oracle XML parser
     * @since Axis1.2
     * @return true if there is data; false if we are closed or at the end of the stream
     * @throws java.io.IOException
     */
    public int available() throws java.io.IOException {
        return (closed || eos) ? 0 : soapStream.available();
    }

}
