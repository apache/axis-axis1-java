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
package org.apache.axis.transport.mail;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.apache.commons.net.pop3.POP3Client;
import org.apache.commons.net.pop3.POP3MessageInfo;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;

import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimePart;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

/**
 * This is meant to be used on a SOAP Client to call a SOAP server via SMTP/POP3
 *
 * @author Davanum Srinivas (dims@yahoo.com)
 */
public class MailSender extends BasicHandler {

    protected static Log log = LogFactory.getLog(MailSender.class.getName());
    private UUIDGen uuidGen = UUIDGenFactory.getUUIDGen(null);

    Properties prop = new Properties();
    Session session = Session.getDefaultInstance(prop, null);

    /**
     * invoke creates a socket connection, sends the request SOAP message and then
     * reads the response SOAP message back from the SOAP server
     *
     * @param msgContext the messsage context
     *
     * @throws AxisFault
     */
    public void invoke(MessageContext msgContext) throws AxisFault {

        if (log.isDebugEnabled()) {
            log.debug(Messages.getMessage("enter00", "MailSender::invoke"));
        }
        try {
            // Send the SOAP request to the SMTP server
            String id = writeUsingSMTP(msgContext);
            
            // Read SOAP response from the POP3 Server
            readUsingPOP3(id, msgContext);
        } catch (Exception e) {
            log.debug(e);
            throw AxisFault.makeFault(e);
        }
        if (log.isDebugEnabled()) {
            log.debug(Messages.getMessage("exit00",
                    "HTTPDispatchHandler::invoke"));
        }
    }


    /**
     * Send the soap request message to the server
     *
     * @param msgContext message context
     *
     * @return id for the current message
     * @throws Exception
     */
    private String writeUsingSMTP(MessageContext msgContext)
            throws Exception {
        String id = (new java.rmi.server.UID()).toString();
        String smtpHost = msgContext.getStrProp(MailConstants.SMTP_HOST);

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

        String fromAddress = msgContext.getStrProp(MailConstants.FROM_ADDRESS);
        String toAddress = msgContext.getStrProp(MailConstants.TO_ADDRESS);

        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(fromAddress));
        msg.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(toAddress));

        // Get SOAPAction, default to ""
        String action = msgContext.useSOAPAction()
                ? msgContext.getSOAPActionURI()
                : "";

        if (action == null) {
            action = "";
        }

        Message reqMessage = msgContext.getRequestMessage();

        msg.addHeader(HTTPConstants.HEADER_USER_AGENT, Messages.getMessage("axisUserAgent"));
        msg.addHeader(HTTPConstants.HEADER_SOAP_ACTION, action);
        msg.setDisposition(MimePart.INLINE);
        msg.setSubject(id);

        ByteArrayOutputStream out = new ByteArrayOutputStream(8 * 1024);
        reqMessage.writeTo(out);
        msg.setContent(out.toString(), reqMessage.getContentType(msgContext.getSOAPConstants()));

        ByteArrayOutputStream out2 = new ByteArrayOutputStream(8 * 1024);
        msg.writeTo(out2);

        client.setSender(fromAddress);
        System.out.print(client.getReplyString());
        client.addRecipient(toAddress);
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
        return id;
    }

    /**
     * Read from server using POP3
     * @param msgContext
     * @throws Exception
     */
    private void readUsingPOP3(String id, MessageContext msgContext) throws Exception {
        // Read the response back from the server
        String pop3Host = msgContext.getStrProp(MailConstants.POP3_HOST);
        String pop3User = msgContext.getStrProp(MailConstants.POP3_USERID);
        String pop3passwd = msgContext.getStrProp(MailConstants.POP3_PASSWORD);

        Reader reader;
        POP3MessageInfo[] messages = null;

        MimeMessage mimeMsg = null;
        POP3Client pop3 = new POP3Client();
        // We want to timeout if a response takes longer than 60 seconds
        pop3.setDefaultTimeout(60000);

        for (int i = 0; i < 12; i++) {
            pop3.connect(pop3Host);

            if (!pop3.login(pop3User, pop3passwd)) {
                pop3.disconnect();
                AxisFault fault = new AxisFault("POP3", "( Could not login to server.  Check password. )", null, null);
                throw fault;
            }

            messages = pop3.listMessages();
            if (messages != null && messages.length > 0) {
                StringBuffer buffer = null;
                for (int j = 0; j < messages.length; j++) {
                    reader = pop3.retrieveMessage(messages[j].number);
                    if (reader == null) {
                        AxisFault fault = new AxisFault("POP3", "( Could not retrieve message header. )", null, null);
                        throw fault;
                    }

                    buffer = new StringBuffer();
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    int ch;
                    while ((ch = bufferedReader.read()) != -1) {
                        buffer.append((char) ch);
                    }
                    bufferedReader.close();
                    if (buffer.toString().indexOf(id) != -1) {
                        ByteArrayInputStream bais = new ByteArrayInputStream(buffer.toString().getBytes());
                        Properties prop = new Properties();
                        Session session = Session.getDefaultInstance(prop, null);

                        mimeMsg = new MimeMessage(session, bais);
                        pop3.deleteMessage(messages[j].number);
                        break;
                    }
                    buffer = null;
                }
            }
            pop3.logout();
            pop3.disconnect();
            if (mimeMsg == null) {
                Thread.sleep(5000);
            } else {
                break;
            }
        }

        if (mimeMsg == null) {
            pop3.logout();
            pop3.disconnect();
            AxisFault fault = new AxisFault("POP3", "( Could not retrieve message list. )", null, null);
            throw fault;
        }

        String contentType = mimeMsg.getContentType();
        String contentLocation = mimeMsg.getContentID();
        Message outMsg = new Message(mimeMsg.getInputStream(), false,
                contentType, contentLocation);

        outMsg.setMessageType(Message.RESPONSE);
        msgContext.setResponseMessage(outMsg);
        if (log.isDebugEnabled()) {
            log.debug("\n" + Messages.getMessage("xmlRecd00"));
            log.debug("-----------------------------------------------");
            log.debug(outMsg.getSOAPPartAsString());
        }
    }
}
