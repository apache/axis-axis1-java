/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001, 2002 The Apache Software Foundation.  All rights
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

package org.apache.axis.transport.jms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.jms.BytesMessage;
import javax.jms.Destination;

import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.AxisFault;

import org.apache.axis.server.AxisServer;

import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.apache.axis.components.logger.LogFactory;

/**
 * SimpleJMSWorker is a worker thread that processes messages that are
 *   received by SimpleJMSListener. It creates a new message context, invokes
 *   the server, and sends back response msg to the replyTo destination.
 *
 * @author Jaime Meritt  (jmeritt@sonicsoftware.com)
 * @author Richard Chung (rchung@sonicsoftware.com)
 * @author Dave Chappell (chappell@sonicsoftware.com)
 */
public class SimpleJMSWorker implements Runnable
{
    protected static Log log =
            LogFactory.getLog(SimpleJMSWorker.class.getName());

    SimpleJMSListener listener;
    BytesMessage message;

    public SimpleJMSWorker(SimpleJMSListener listener, BytesMessage message)
    {
        this.listener = listener;
        this.message = message;
    }

    /**
     * This is where the incoming message is processed.
     */
    public void run()
    {
        InputStream in = null;
        try
        {
            // get the incoming msg content into a byte array
            byte[] buffer = new byte[8 * 1024];
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            for(int bytesRead = message.readBytes(buffer);
                bytesRead != -1; bytesRead = message.readBytes(buffer))
            {
                out.write(buffer, 0, bytesRead);
            }
            in = new ByteArrayInputStream(out.toByteArray());
        }
        catch(Exception e)
        {
            log.error(Messages.getMessage("exception00"), e);
            e.printStackTrace();
            return;
        }

        // create the msg and context and invoke the server
        AxisServer server = listener.getAxisServer();
        Message msg = new Message(in);
        MessageContext  msgContext = new MessageContext(server);
        msgContext.setRequestMessage( msg );
        try
        {
            server.invoke( msgContext );
            msg = msgContext.getResponseMessage();
        }
        catch (AxisFault af)
        {
            msg = new Message(af);
            msg.setMessageContext(msgContext);
        }
        catch (Exception e)
        {
            msg = new Message(new AxisFault(e.toString()));
            msg.setMessageContext(msgContext);
        }

        try
        {
            // now we need to send the response
            Destination destination = message.getJMSReplyTo();
            if(destination == null)
                return;
            JMSEndpoint replyTo = listener.getConnector().createEndpoint(destination);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            msg.writeTo(out);
            replyTo.send(out.toByteArray());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        if (msgContext.getProperty(msgContext.QUIT_REQUESTED) != null)
            // why then, quit!
            try {listener.shutdown();} catch (Exception e) {}
    }
}
