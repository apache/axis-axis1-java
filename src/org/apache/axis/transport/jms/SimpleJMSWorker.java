/*
 * Copyright 2001, 2002,2004 The Apache Software Foundation.
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

package org.apache.axis.transport.jms;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.server.AxisServer;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

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
        AxisServer server = SimpleJMSListener.getAxisServer();

        // if the incoming message has a contentType set,
        // pass it to my new Message
        String contentType = null;
        try 
        { 
            contentType = message.getStringProperty("contentType");
        } 
        catch(Exception e) 
        { 
            e.printStackTrace();
        }

        Message msg = null;
        if(contentType != null && !contentType.trim().equals("")) 
        {
            msg = new Message(in, true, contentType, null);
        } 
        else 
        {
            msg = new Message(in);
        }

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

        if (msgContext.getProperty(MessageContext.QUIT_REQUESTED) != null)
            // why then, quit!
            try {listener.shutdown();} catch (Exception e) {}
    }
}
