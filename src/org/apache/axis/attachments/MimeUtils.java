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

/**
 * @author Rick Rineholt
 * @author Wouter Cloetens (wouter@mind.be)
 */
package org.apache.axis.attachments;

import org.apache.axis.AxisProperties;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.SessionUtils;
import org.apache.commons.logging.Log;

import java.util.Properties;


/**
 * This class is defines utilities for mime.
 */
public class MimeUtils {

    /** Field log           */
    protected static Log log =
        LogFactory.getLog(MimeUtils.class.getName());

    /**
     * Determine as efficiently as possible the content length for attachments in a mail Multipart.
     * @param mp is the multipart to be serarched.
     * @return the actual length.
     *
     * @throws javax.mail.MessagingException
     * @throws java.io.IOException
     */
    public static long getContentLength(javax.mail.Multipart mp)
            throws javax.mail.MessagingException, java.io.IOException {

        int totalParts = mp.getCount();
        long totalContentLength = 0;

        for (int i = 0; i < totalParts; ++i) {
            javax.mail.internet.MimeBodyPart bp =
                    (javax.mail.internet.MimeBodyPart) mp.getBodyPart(i);

            totalContentLength += getContentLength(bp);
        }

        String ctype = mp.getContentType();
        javax.mail.internet.ContentType ct =
                new javax.mail.internet.ContentType(ctype);
        String boundaryStr =
                ct.getParameter("boundary");
        int boundaryStrLen =
                boundaryStr.length()
                + 4;    // must add two for -- prefix and another two for crlf

        // there is one more boundary than parts
        // each parts data must have crlf after it.
        // last boundary has an additional --crlf
        return totalContentLength + boundaryStrLen * (totalParts + 1)
                + 2 * totalParts + +4;
    }

    /**
     * Determine the length for the individual part.
     * @param bp is the part to be searched.
     * @return the length in bytes.
     */
    protected static long getContentLength(
            javax.mail.internet.MimeBodyPart bp) {

        long headerLength = -1L;
        long dataSize = -1L;

        try {
            headerLength = getHeaderLength(bp);

            javax.activation.DataHandler dh = bp.getDataHandler();
            javax.activation.DataSource ds = dh.getDataSource();

            // Do files our selfs since this is costly to read in. Ask the file system.
            // This is 90% of the use of attachments.
            if (ds instanceof javax.activation.FileDataSource) {
                javax.activation.FileDataSource fdh =
                        (javax.activation.FileDataSource) ds;
                java.io.File df = fdh.getFile();

                if (!df.exists()) {
                    throw new RuntimeException(Messages.getMessage("noFile",
                            df.getAbsolutePath()));
                }

                dataSize = df.length();
            } else {
                dataSize = bp.getSize();

                if (-1 == dataSize) {    // Data size is not known so read it the hard way...
                    dataSize = 0;

                    java.io.InputStream in = ds.getInputStream();
                    byte[] readbuf = new byte[64 * 1024];
                    int bytesread;

                    do {
                        bytesread = in.read(readbuf);

                        if (bytesread > 0) {
                            dataSize += bytesread;
                        }
                    } while (bytesread > -1);

                    in.close();
                }
            }
        } catch (Exception e) {
            log.error(Messages.getMessage("exception00"), e);
        }

        return dataSize + headerLength;
    }

    /**
     * Gets the header length for any part.
     * @param bp the part to determine the header length for.
     * @return the length in bytes.
     *
     * @throws javax.mail.MessagingException
     * @throws java.io.IOException
     */
    private static long getHeaderLength(javax.mail.internet.MimeBodyPart bp)
            throws javax.mail.MessagingException, java.io.IOException {

        javax.mail.internet.MimeBodyPart headersOnly =
                new javax.mail.internet.MimeBodyPart(
                        new javax.mail.internet.InternetHeaders(), new byte[0]);

        for (java.util.Enumeration en = bp.getAllHeaders();
             en.hasMoreElements();) {
            javax.mail.Header header = (javax.mail.Header) en.nextElement();

            headersOnly.addHeader(header.getName(), header.getValue());
        }

        java.io.ByteArrayOutputStream bas =
                new java.io.ByteArrayOutputStream(1024 * 16);

        headersOnly.writeTo(bas);
        bas.close();

        return (long) bas.size();    // This has header length plus the crlf part that seperates the data
    }

    // fixme: filter can be replaced as it is not final - is this intended? If
    //  so, document
    // fixme: the fields in filter are not protected - they can be over-written
    /** Field filter           */
    public static String[] filter = new String[]{"Message-ID", "Mime-Version",
                                                 "Content-Type"};

    /**
     * This routine will the multi part type and write it out to a stream.
     *
     * <p>Note that is does *NOT* pass <code>AxisProperties</code>
     * to <code>javax.mail.Session.getInstance</code>, but instead
     * the System properties.
     * </p>
     * @param os is the output stream to write to.
     * @param mp the multipart that needs to be written to the stream.
     */
    public static void writeToMultiPartStream(
            java.io.OutputStream os, javax.mail.internet.MimeMultipart mp) {

        try {
            Properties props = AxisProperties.getProperties();

            props.setProperty(
                    "mail.smtp.host",
                    "localhost");    // this is a bogus since we will never mail it.

            javax.mail.Session session =
                    javax.mail.Session.getInstance(props, null);
            javax.mail.internet.MimeMessage message =
                    new javax.mail.internet.MimeMessage(session);

            message.setContent(mp);
            message.saveChanges();
            message.writeTo(os, filter);
        } catch (javax.mail.MessagingException e) {
            log.error(Messages.getMessage("javaxMailMessagingException00"), e);
        } catch (java.io.IOException e) {
            log.error(Messages.getMessage("javaIOException00"), e);
        }
    }

    /**
     * This routine will get the content type from a mulit-part mime message.
     *
     * @param mp the MimeMultipart
     * @return the content type
     */
    public static String getContentType(javax.mail.internet.MimeMultipart mp) {
        StringBuffer contentType = new StringBuffer(mp.getContentType());
        // TODO (dims): Commons HttpClient croaks if we don't do this.
        //              Need to get Commons HttpClient fixed.
        for(int i=0;i<contentType.length();){
            char ch = contentType.charAt(i);
            if(ch=='\r'||ch=='\n')
                contentType.deleteCharAt(i);
            else
                i++;
        }
        return contentType.toString();
    }

    /**
     * This routine will create a multipart object from the parts and the SOAP content.
     * @param env should be the text for the main root part.
     * @param parts contain a collection of the message parts.
     *
     * @return a new MimeMultipart object
     *
     * @throws org.apache.axis.AxisFault
     */
    public static javax.mail.internet.MimeMultipart createMP(
            String env, java.util.Collection parts)
            throws org.apache.axis.AxisFault {

        javax.mail.internet.MimeMultipart multipart = null;

        try {
            String rootCID = SessionUtils.generateSessionId();

            multipart = new javax.mail.internet.MimeMultipart(
                    "related; type=\"text/xml\"; start=\"<" + rootCID + ">\"");

            javax.mail.internet.MimeBodyPart messageBodyPart =
                    new javax.mail.internet.MimeBodyPart();

            messageBodyPart.setText(env, "UTF-8");
            messageBodyPart.setHeader("Content-Type",
                    "text/xml; charset=UTF-8");
            messageBodyPart.setHeader("Content-Id", "<" + rootCID + ">");
            messageBodyPart.setHeader(
                    HTTPConstants.HEADER_CONTENT_TRANSFER_ENCODING, "binary");
            multipart.addBodyPart(messageBodyPart);

            for (java.util.Iterator it = parts.iterator(); it.hasNext();) {
                org.apache.axis.Part part =
                        (org.apache.axis.Part) it.next();
                javax.activation.DataHandler dh =
                        org.apache.axis.attachments.AttachmentUtils.getActivationDataHandler(
                                part);
                String contentID = part.getContentId();

                messageBodyPart = new javax.mail.internet.MimeBodyPart();

                messageBodyPart.setDataHandler(dh);

                String contentType = dh.getContentType();

                if ((contentType == null)
                        || (contentType.trim().length() == 0)) {
                    contentType = "application/octet-stream";
                }

                messageBodyPart.setHeader(HTTPConstants.HEADER_CONTENT_TYPE,
                        contentType);
                messageBodyPart.setHeader(HTTPConstants.HEADER_CONTENT_ID,
                        "<" + contentID + ">");
                messageBodyPart.setHeader(
                        HTTPConstants.HEADER_CONTENT_TRANSFER_ENCODING,
                        "binary");    // Safe and fastest for anything other than mail;

                for (java.util.Iterator i =
                        part.getNonMatchingMimeHeaders(new String[]{
                            HTTPConstants.HEADER_CONTENT_TYPE,
                            HTTPConstants.HEADER_CONTENT_ID,
                            HTTPConstants.HEADER_CONTENT_TRANSFER_ENCODING}); i.hasNext();) {
                    javax.xml.soap.MimeHeader header = (javax.xml.soap.MimeHeader) i.next();

                     messageBodyPart.setHeader(header.getName(), header.getValue());
                }

                multipart.addBodyPart(messageBodyPart);
            }
        } catch (javax.mail.MessagingException e) {
            log.error(Messages.getMessage("javaxMailMessagingException00"), e);
        }

        return multipart;
    }
}
