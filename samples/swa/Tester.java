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

package samples.swa;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

/**
 * Class Tester
 * 
 * @version %I%, %G%
 */
public class Tester {

    /** Field HEADER_CONTENT_TYPE */
    public static final String HEADER_CONTENT_TYPE = "Content-Type";

    /** Field HEADER_CONTENT_TRANSFER_ENCODING */
    public static final String HEADER_CONTENT_TRANSFER_ENCODING =
            "Content-Transfer-Encoding";

    /** Field address */
    private static final java.lang.String address =
            "http://localhost:8080/axis/services/SwaHttp";

    /**
     * Method main
     * 
     * @param args 
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {

        /*
         * Start to prepare service call. Once this is done, several
         * calls can be made on the port (see below)
         *
         * Fist: get the service locator. This implements the functionality
         * to get a client stub (aka port).
         */
        SwaServiceLocator service = new SwaServiceLocator();

        /*
         * Here we use an Axis specific call that allows to override the
         * port address (service endpoint address) with an own URL. Comes
         * in handy for testing.
         */
        java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(address);
        } catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }

        SwaPort port = (SwaPort) service.getSwaHttp(endpoint);

        /*
         * At this point all preparations are done. Using the port we can
         * now perform as many calls as necessary.
         */

        /*
         * Prepare the Multipart attachment. It consists of several data files. The
         * multipart container is of type "multipart/mixed"
         */
        MimeMultipart mpRoot = new MimeMultipart();
        System.out.println("MimeMultipart content: " + mpRoot.getContentType());
        DataHandler dh = new DataHandler(new FileDataSource("duke.gif"));
        addBodyPart(mpRoot, dh);
        dh = new DataHandler(new FileDataSource("pivots.jpg"));
        addBodyPart(mpRoot, dh);
        // perform call
        port.swaSend("AppName", mpRoot);
    }

    /**
     * Method addBodyPart
     * 
     * @param mp 
     * @param dh 
     */
    private static void addBodyPart(MimeMultipart mp, DataHandler dh) {
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        try {
            messageBodyPart.setDataHandler(dh);
            String contentType = dh.getContentType();
            if ((contentType == null) || (contentType.trim().length() == 0)) {
                contentType = "application/octet-stream";
            }
            System.out.println("Content type: " + contentType);
            messageBodyPart.setHeader(HEADER_CONTENT_TYPE, contentType);
            messageBodyPart.setHeader(
                    HEADER_CONTENT_TRANSFER_ENCODING,
                    "binary");    // Safe and fastest for anything other than mail
            mp.addBodyPart(messageBodyPart);
        } catch (javax.mail.MessagingException e) {
        }
    }
}
