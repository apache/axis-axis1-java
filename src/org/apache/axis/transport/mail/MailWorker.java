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

package org.apache.axis.transport.mail;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPFault;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;

import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimePart;
import java.io.ByteArrayOutputStream;
import java.io.Writer;
import java.util.Properties;


public class MailWorker implements Runnable {
    protected static Log log =
            LogFactory.getLog(MailWorker.class.getName());

    // Server 
    private MailServer server;

    // Current message
    private MimeMessage mimeMessage;

    // Axis specific constants
    private static String transportName = "Mail";

    private Properties prop = new Properties();
    private Session session = Session.getDefaultInstance(prop, null);

    /**
     * Constructor for MailWorker
     * @param server
     * @param mimeMessage
     */
    public MailWorker(MailServer server, MimeMessage mimeMessage) {
        this.server = server;
        this.mimeMessage = mimeMessage;
    }

    /**
     * The main workhorse method.
     */
    public void run() {
        // create an Axis server
        AxisServer engine = server.getAxisServer();

        // create and initialize a message context
        MessageContext msgContext = new MessageContext(engine);
        Message requestMsg;

        // buffers for the headers we care about
        StringBuffer soapAction = new StringBuffer();
        StringBuffer fileName = new StringBuffer();
        StringBuffer contentType = new StringBuffer();
        StringBuffer contentLocation = new StringBuffer();

        Message responseMsg = null;

        // prepare request (do as much as possible while waiting for the
        // next connection).  
        try {
            msgContext.setTargetService(null);
        } catch (AxisFault fault) {
        }
        msgContext.setResponseMessage(null);
        msgContext.reset();
        msgContext.setTransportName(transportName);

        responseMsg = null;

        try {
            try {
                // parse all headers into hashtable
                parseHeaders(mimeMessage, contentType,
                        contentLocation, soapAction);

                // Real and relative paths are the same for the
                // MailServer
                msgContext.setProperty(Constants.MC_REALPATH,
                        fileName.toString());
                msgContext.setProperty(Constants.MC_RELATIVE_PATH,
                        fileName.toString());
                msgContext.setProperty(Constants.MC_JWS_CLASSDIR,
                        "jwsClasses");

                // this may be "" if either SOAPAction: "" or if no SOAPAction at all.
                // for now, do not complain if no SOAPAction at all
                String soapActionString = soapAction.toString();
                if (soapActionString != null) {
                    msgContext.setUseSOAPAction(true);
                    msgContext.setSOAPActionURI(soapActionString);
                }
                requestMsg = new Message(mimeMessage.getInputStream(), false,
                        contentType.toString(), contentLocation.toString());
                msgContext.setRequestMessage(requestMsg);

                // invoke the Axis engine
                engine.invoke(msgContext);

                // Retrieve the response from Axis
                responseMsg = msgContext.getResponseMessage();
                if (responseMsg == null) {
                    throw new AxisFault(Messages.getMessage("nullResponse00"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                AxisFault af;
                if (e instanceof AxisFault) {
                    af = (AxisFault) e;
                    log.debug(Messages.getMessage("serverFault00"), af);
                } else {
                    af = AxisFault.makeFault(e);
                }

                // There may be headers we want to preserve in the
                // response message - so if it's there, just add the
                // FaultElement to it.  Otherwise, make a new one.
                responseMsg = msgContext.getResponseMessage();
                if (responseMsg == null) {
                    responseMsg = new Message(af);
                } else {
                    try {
                        SOAPEnvelope env = responseMsg.getSOAPEnvelope();
                        env.clearBody();
                        env.addBodyElement(new SOAPFault((AxisFault) e));
                    } catch (AxisFault fault) {
                        // Should never reach here!
                    }
                }
            }

            String replyTo = ((InternetAddress) mimeMessage.getReplyTo()[0]).getAddress();
            String sendFrom = ((InternetAddress) mimeMessage.getAllRecipients()[0]).getAddress();
            String subject = "Re: " + mimeMessage.getSubject();
            writeUsingSMTP(msgContext, server.getHost(), sendFrom, replyTo, subject, responseMsg);
        } catch (Exception e) {
            e.printStackTrace();
            log.debug(Messages.getMessage("exception00"), e);
        }
        if (msgContext.getProperty(msgContext.QUIT_REQUESTED) != null) {
            // why then, quit!
            try {
                server.stop();
            } catch (Exception e) {
            }
        }

    }

    /**
     * Send the soap request message to the server
     * 
     * @param msgContext
     * @param smtpHost
     * @param sendFrom
     * @param replyTo
     * @param output
     * @throws Exception
     */
    private void writeUsingSMTP(MessageContext msgContext,
                                String smtpHost,
                                String sendFrom,
                                String replyTo,
                                String subject,
                                Message output)
            throws Exception {
        SMTPClient client = new SMTPClient();
        client.connect(smtpHost);
        
        // After connection attempt, you should check the reply code to verify
        // success.
        System.out.print(client.getReplyString());
        int reply = client.getReplyCode();
        if (!SMTPReply.isPositiveCompletion(reply)) {
            client.disconnect();
            AxisFault fault = new AxisFault("SMTP", "( SMTP server refused connection )", null, null);
            throw fault;
        }

        client.login(smtpHost);
        System.out.print(client.getReplyString());
        reply = client.getReplyCode();
        if (!SMTPReply.isPositiveCompletion(reply)) {
            client.disconnect();
            AxisFault fault = new AxisFault("SMTP", "( SMTP server refused connection )", null, null);
            throw fault;
        }

        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(sendFrom));
        msg.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(replyTo));
        msg.setDisposition(MimePart.INLINE);
        msg.setSubject(subject);

        ByteArrayOutputStream out = new ByteArrayOutputStream(8 * 1024);
        output.writeTo(out);
        msg.setContent(out.toString(), output.getContentType(msgContext.getSOAPConstants()));

        ByteArrayOutputStream out2 = new ByteArrayOutputStream(8 * 1024);
        msg.writeTo(out2);

        client.setSender(sendFrom);
        System.out.print(client.getReplyString());
        client.addRecipient(replyTo);
        System.out.print(client.getReplyString());

        Writer writer = client.sendMessageData();
        System.out.print(client.getReplyString());
        writer.write(out2.toString());
        writer.flush();
        writer.close();

        System.out.print(client.getReplyString());
        if (!client.completePendingCommand()) {
            System.out.print(client.getReplyString());
            AxisFault fault = new AxisFault("SMTP", "( Failed to send email )", null, null);
            throw fault;
        }
        System.out.print(client.getReplyString());
        client.logout();
        client.disconnect();
    }

    /**
     * Read all mime headers, returning the value of Content-Length and
     * SOAPAction.
     * @param mimeMessage         InputStream to read from
     * @param contentType The content type.
     * @param contentLocation The content location
     * @param soapAction StringBuffer to return the soapAction into
     */
    private void parseHeaders(MimeMessage mimeMessage,
                              StringBuffer contentType,
                              StringBuffer contentLocation,
                              StringBuffer soapAction)
            throws Exception {
        contentType.append(mimeMessage.getContentType());
        contentLocation.append(mimeMessage.getContentID());
        String values[] = mimeMessage.getHeader(HTTPConstants.HEADER_SOAP_ACTION);
        if (values != null)
            soapAction.append(values[0]);
    }
}
