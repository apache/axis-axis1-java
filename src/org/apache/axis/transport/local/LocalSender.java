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

package org.apache.axis.transport.local;

import org.apache.axis.*;
import org.apache.axis.handlers.*;
import org.apache.axis.server.*;
import org.apache.axis.transport.http.*;
import org.apache.axis.utils.*;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPFaultElement;

import java.net.*;

/**
 * This is meant to be used on a SOAP Client to call a SOAP server.
 *
 * @author Sam Ruby <rubys@us.ibm.com>
 */
public class LocalSender extends BasicHandler {

  private volatile AxisServer server;

  /**
   * Allocate an embedded Axis server to process requests and initialize it.
   */
  public synchronized void init() {
    AxisServer server = new AxisServer();
    server.init();
    this.server=server;
  }

  public void invoke(MessageContext clientContext) throws AxisFault {
    Debug.Print( 1, "Enter: LocalSender::invoke" );

    AxisServer targetServer = (AxisServer)clientContext.
                                     getProperty(LocalTransport.LOCAL_SERVER);
    Debug.Print(3, "LocalSender using server " + targetServer);
    
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
    String msgStr = clientContext.getRequestMessage().getAsString();
    
    Debug.Print(3, "LocalSender sending XML:");
    Debug.Print(3, msgStr);

    serverContext.setRequestMessage(new Message(msgStr));
    serverContext.setTransportName("local");

    // copy soap action if it is present
    String action = clientContext.getStrProp(HTTPConstants.MC_HTTP_SOAPACTION);
    if (action != null) {
       serverContext.setProperty(HTTPConstants.MC_HTTP_SOAPACTION, action);
       serverContext.setTransportName("http");
    }

    // set the realpath if possible
    String transURL = clientContext.getStrProp(MessageContext.TRANS_URL);
    if (transURL != null) {
      try {
        URL url = new URL(transURL);
        if (url.getProtocol().equals("file")) {
          String file = url.getFile();
          if (file.length()>0 && file.charAt(0)=='/') file = file.substring(1);
          serverContext.setProperty(Constants.MC_REALPATH, file);
        }
      } catch (Exception e) {
      }
    }

    // invoke the request
    try {
        targetServer.invoke(serverContext);
    } catch (AxisFault fault) {
        Message respMsg = serverContext.getResponseMessage();
        if (respMsg == null) {
            respMsg = new Message(fault);
            serverContext.setResponseMessage(respMsg);
        } else {
            SOAPFaultElement faultEl = new SOAPFaultElement(fault);
            SOAPEnvelope env = respMsg.getAsSOAPEnvelope();
            env.clearBody();
            env.addBodyElement(faultEl);
        }
    }

    // copy back the response, and force its format to String in order to
    // exercise the deserializers.
    clientContext.setResponseMessage(serverContext.getResponseMessage());
    //clientContext.getResponseMessage().getAsString();

    Debug.Print( 1, "Exit: LocalSender::invoke" );
  }

  public void undo(MessageContext msgContext) {
    Debug.Print( 1, "Enter: LocalSender::undo" );
    Debug.Print( 1, "Exit: LocalSender::undo" );
  }
};
