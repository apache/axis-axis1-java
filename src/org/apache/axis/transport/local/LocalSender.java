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

package org.apache.axis.transport.local;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.attachments.Attachments;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPFault;
import org.apache.axis.server.AxisServer;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

import java.net.URL;

/**
 * This is meant to be used on a SOAP Client to call a SOAP server.
 *
 * @author Sam Ruby <rubys@us.ibm.com>
 */
public class LocalSender extends BasicHandler {
    protected static Log log =
        LogFactory.getLog(LocalSender.class.getName());

    private volatile AxisServer server;

    /**
     * Allocate an embedded Axis server to process requests and initialize it.
     */
    public synchronized void init() {
        this.server= new AxisServer();
    }

    public void invoke(MessageContext clientContext) throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug("Enter: LocalSender::invoke");
        }

        AxisServer targetServer = 
            (AxisServer)clientContext.getProperty(LocalTransport.LOCAL_SERVER);

        if (log.isDebugEnabled()) {
            log.debug(Messages.getMessage("usingServer00", 
                "LocalSender", "" + targetServer));
        }

        if (targetServer == null) {
            // This should have already been done, but it doesn't appear to be
            // something that can be relied on.  Oh, well...
            if (server == null) init();
            targetServer = server;
        }

        // Define a new messageContext per request
        MessageContext serverContext = new MessageContext(targetServer);

        // copy the request, and force its format to String in order to
        // exercise the serializers.

// START FIX: http://nagoya.apache.org/bugzilla/show_bug.cgi?id=17161

        Message clientRequest = clientContext.getRequestMessage();
        
        String msgStr = clientRequest.getSOAPPartAsString();

        if (log.isDebugEnabled()) {
            log.debug(Messages.getMessage("sendingXML00", "LocalSender"));
            log.debug(msgStr);
        }
        
        Message serverRequest = new Message(msgStr);

        Attachments serverAttachments = serverRequest.getAttachmentsImpl();
        Attachments clientAttachments = clientRequest.getAttachmentsImpl();

        if (null != clientAttachments && null != serverAttachments) {
            serverAttachments.setAttachmentParts(clientAttachments.getAttachments());
        }

        serverContext.setRequestMessage(serverRequest);

// END FIX: http://nagoya.apache.org/bugzilla/show_bug.cgi?id=17161

        serverContext.setTransportName("local");

        // Also copy authentication info if present
        String user = clientContext.getUsername();
        if (user != null) {
            serverContext.setUsername(user);
            String pass = clientContext.getPassword();
            if (pass != null)
                serverContext.setPassword(pass);
        }

        // set the realpath if possible
        String transURL = clientContext.getStrProp(MessageContext.TRANS_URL);
        if (transURL != null) {
            try {
                URL url = new URL(transURL);
                String file = url.getFile();
                if (file.length()>0 && file.charAt(0)=='/') file = file.substring(1);
                serverContext.setProperty(Constants.MC_REALPATH, file);
                
                // This enables "local:///AdminService" and the like to work.
                serverContext.setTargetService(file);
            } catch (Exception e) {
                throw AxisFault.makeFault(e);
            }
        }

        // If we've been given an explicit "remote" service to invoke,
        // use it. (Note that right now this overrides the setting above;
        // is this the correct precedence?)
        String remoteService = clientContext.getStrProp(LocalTransport.REMOTE_SERVICE);
        if (remoteService != null)
            serverContext.setTargetService(remoteService);

        // invoke the request
        try {
            targetServer.invoke(serverContext);
        } catch (AxisFault fault) {
            Message respMsg = serverContext.getResponseMessage();
            if (respMsg == null) {
                respMsg = new Message(fault);
                serverContext.setResponseMessage(respMsg);
            } else {
                SOAPFault faultEl = new SOAPFault(fault);
                SOAPEnvelope env = respMsg.getSOAPEnvelope();
                env.clearBody();
                env.addBodyElement(faultEl);
            }
        }

        // copy back the response, and force its format to String in order to
        // exercise the deserializers.
        clientContext.setResponseMessage(serverContext.getResponseMessage());
        //clientContext.getResponseMessage().getAsString();

        if (log.isDebugEnabled()) {
            log.debug("Exit: LocalSender::invoke");
        }
    }
}
