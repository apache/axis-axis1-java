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
