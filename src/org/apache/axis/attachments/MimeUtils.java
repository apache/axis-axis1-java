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

/**
 * @author Rick Rineholt 
 * @author Wouter Cloetens (wouter@mind.be)
 */

package org.apache.axis.attachments;


import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

/**
 * This class is defines utilities for mime.
 */

public class MimeUtils {

    /**
    * Determine as efficiently as possible the content length for attachments in a mail Multipart.
    * @param mp is the multipart to be serarched.
    * @return the actual length.
    */
    public static long getContentLength(javax.mail.Multipart mp ) throws javax.mail.MessagingException, java.io.IOException {

        int totalParts = mp.getCount();
        long totalContentLength = 0;

        for (int i = 0; i < totalParts; ++i) {
            javax.mail.internet.MimeBodyPart bp = (javax.mail.internet.MimeBodyPart) mp.getBodyPart(i);

            totalContentLength += getContentLength(bp);
        }

        String ctype = mp.getContentType();
        javax.mail.internet.ContentType ct = new javax.mail.internet.ContentType( ctype);
        String boundaryStr = ct.getParameter("boundary");
        int boundaryStrLen = boundaryStr.length() + 4; //must add two for -- prefix and another two for crlf

        return totalContentLength
            + boundaryStrLen * (totalParts + 1) //there is one more boundary than parts
            + 2 * totalParts +  //each parts data must have crlf after it.
            +2;  // last boundary has an additional --
    }

    /**
    * Determine the length for the individual part. 
    * @param mp is the part to be serarched.
    * @return the length in bytes.
    */
    protected  static long  getContentLength(javax.mail.internet.MimeBodyPart bp) {
        long headerLength = -1L;
        long dataSize = -1L;

        try {
            headerLength = getHeaderLength( bp);
            javax.activation.DataHandler dh =  bp.getDataHandler();
            javax.activation.DataSource ds = dh.getDataSource();

            //Do files our selfs since this is costly to read in. Ask the file system.
            // This is 90% of the use of attachments.
            if ( ds instanceof javax.activation.FileDataSource) {
                javax.activation.FileDataSource fdh = (javax.activation.FileDataSource) ds;
                java.io.File df = fdh.getFile();

                if (!df.exists())
                    throw new RuntimeException( "File for dataHandler does not exist" + df.getAbsolutePath());
                dataSize = df.length();
            }
            else {
                dataSize = bp.getSize();
                if (-1 == dataSize ) { //Data size is not known so read it the hard way...
                    dataSize = 0;
                    java.io.InputStream in = ds.getInputStream();
                    byte[] readbuf = new byte[64 * 1024];
                    int bytesread;

                    do {
                        bytesread = in.read(readbuf);
                        if (bytesread > 0) dataSize += bytesread;
                    }
                    while ( bytesread > -1);
                    in.close();
                }
            }
        }
        catch (Exception e) {
            System.err.println(e);
        }
        return dataSize + headerLength;
    }
    /**
     * Gets the header length for any part.
     * @param the part to determine the header length for.
     * @return the length in bytes.
     */
    private static long  getHeaderLength(javax.mail.internet.MimeBodyPart bp) throws javax.mail.MessagingException, java.io.IOException {

        javax.mail.internet.MimeBodyPart headersOnly = new javax.mail.internet.MimeBodyPart(new javax.mail.internet.InternetHeaders(), new byte[0]);

        for ( java.util.Enumeration en = bp.getAllHeaders(); en.hasMoreElements() ; ) {
            javax.mail.Header header = (javax.mail.Header) en.nextElement();

            headersOnly.addHeader( header.getName(), header.getValue());
        }

        java.io.ByteArrayOutputStream bas = new java.io.ByteArrayOutputStream( 1024 * 16);

        headersOnly.writeTo(bas);
        bas.close();

        return (long) bas.size(); //This has header length plus the crlf part that seperates the data
    }

    public static String[] filter = new String[] { "Message-ID", "Mime-Version", "Content-Type" } ;
    
    /**
     * This routine will the multi part type and write it out to a stream.
     * @param os is the output stream to write to.
     * @param the multipart that needs to be written to the stream.
     */
    public static void writeToMultiPartStream(java.io.OutputStream os, javax.mail.internet.MimeMultipart  mp) {
        try {
            java.util.Properties props = System.getProperties();

            props.put("mail.smtp.host", "localhost"); //this is a bogus since we will never mail it.
            javax.mail.Session session = javax.mail.Session.getInstance(props, null);
            javax.mail.internet.MimeMessage message = new javax.mail.internet.MimeMessage(session);

            message.setContent(mp);
            message.saveChanges();
            message.writeTo(os, filter);

        }
        catch (javax.mail.MessagingException e) {
            System.err.println(e);
            e.printStackTrace();
        }
        catch (java.io.IOException e) {
            System.err.println(e);
            e.printStackTrace();
        }
    }
    /**
     * This routine will get the content type.
     */
    public static String getContentType(javax.mail.internet.MimeMultipart mp) {
        return  mp.getContentType();
    }
    /**
     * This routine will create a multipar object from the parts and the SOAP content.
     * @param the env should be the text for the main root part.
     * @param the parts contain a collection of  mappings of cids to the message parts.
     */

    public static javax.mail.internet.MimeMultipart createMP(String env, java.util.Map parts ) throws org.apache.axis.AxisFault {
        javax.mail.internet.MimeMultipart multipart = null;

        try {
            String rootCID = getNewContentIdValue();

            if (rootCID.startsWith("cid:")) rootCID = rootCID.substring(4);
            multipart = new javax.mail.internet.MimeMultipart("related; start=\"<" + rootCID + ">\"" );

            javax.mail.internet.MimeBodyPart messageBodyPart = new javax.mail.internet.MimeBodyPart();

            messageBodyPart.setText(env);
            messageBodyPart.setHeader("Content-Type", "text/xml; charset=utf-8" );
            messageBodyPart.setHeader("Content-ID", "<" + rootCID  + ">" );
            messageBodyPart.setHeader("Content-Transfer-Encoding", "8bit");

            multipart.addBodyPart(messageBodyPart);
            java.util.Set pe = parts.entrySet();

            for (java.util.Iterator it = pe.iterator(); it.hasNext(); ) {
                java.util.Map.Entry es = (java.util.Map.Entry) it.next();
                javax.activation.DataHandler dh =
                    org.apache.axis.attachments.AttachmentUtils.getActiviationDataHandler((org.apache.axis.Part) es.getValue());
                String contentID = (String) es.getKey();

                if (contentID.startsWith("cid:")) contentID = contentID.substring(4);

                messageBodyPart = new javax.mail.internet.MimeBodyPart();

                messageBodyPart.setDataHandler(dh);
                String contentType = dh.getContentType();

                if (contentType == null || contentType.trim().length() == 0) {
                    contentType = "application/octet-stream";
                }
                messageBodyPart.setHeader("Content-Type", contentType );
                messageBodyPart.setHeader("Content-ID", "<" + contentID  + ">" );
                messageBodyPart.setHeader("Content-Transfer-Encoding", "binary"); //Safe and fastest for anything other than mail;
                multipart.addBodyPart(messageBodyPart);
            }
        }
        catch (javax.mail.MessagingException e) {
            System.err.println(e);
            e.printStackTrace();
        }
        return multipart ;
    }

    static String thisHost = null;

    private static int count = (int) (Math.random() * 100);

    public static String getNewContentIdValue() {
        int lcount;

        synchronized (org.apache.axis.Message.class  ) {
            lcount = ++count;
        }
        if (null == thisHost) {
            try {
                thisHost = java.net.InetAddress.getLocalHost().getHostName();
            } 
            catch (java.net.UnknownHostException e) {
                System.err.println("exception:" + e);
                thisHost = "localhost";
                e.printStackTrace();
            }
        }

        StringBuffer s = new StringBuffer();

        // Unique string is <hashcode>.<currentTime>.apache-soap.<hostname>
        s.append("cid:").append( lcount).append(s.hashCode()).append('.').append(System.currentTimeMillis()).append(".AXIS@").append(thisHost);
        return s.toString();
    }
}
