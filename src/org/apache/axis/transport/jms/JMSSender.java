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
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.attachments.Attachments;

import javax.jms.Destination;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * This is meant to be used on a SOAP Client to call a SOAP server.
 *
 * @author Jaime Meritt  (jmeritt@sonicsoftware.com)
 * @author Richard Chung (rchung@sonicsoftware.com)
 * @author Dave Chappell (chappell@sonicsoftware.com)
 */
public class JMSSender extends BasicHandler
{
    public JMSSender()
    {
    }

    /**
     * invoke() creates an endpoint, sends the request SOAP message, and then
     *   either reads the response SOAP message or simply returns.
     *
     * @todo hash on something much better than the connection factory
     *  something like domain:url:username:password would be adequate
     * @param msgContext
     * @throws AxisFault
     */
    public void invoke(MessageContext msgContext) throws AxisFault
    {
        JMSConnector connector = null;
        try
        {
            Object destination = msgContext.getProperty(JMSConstants.DESTINATION);
            if(destination == null)
                throw new AxisFault("noDestination");

            connector = (JMSConnector)msgContext.getProperty(JMSConstants.CONNECTOR);

            JMSEndpoint endpoint = null;
            if(destination instanceof String)
                endpoint = connector.createEndpoint((String)destination);
            else
                endpoint = connector.createEndpoint((Destination)destination);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            msgContext.getRequestMessage().writeTo(out);

            HashMap props = createSendProperties(msgContext);

            // If the request message contains attachments, set
            // a contentType property to go in the outgoing message header
            String ret = null;
            Message message = msgContext.getRequestMessage();
            Attachments mAttachments = message.getAttachmentsImpl();
            if (mAttachments != null && 0 != mAttachments.getAttachmentCount()) 
            {
                String contentType = mAttachments.getContentType();
                if(contentType != null && !contentType.trim().equals("")) 
                {
                    props.put("contentType", contentType);
                }
            }

            boolean waitForResponse = true;
            if(msgContext.containsProperty(JMSConstants.WAIT_FOR_RESPONSE))
                waitForResponse =
                    ((Boolean)msgContext.getProperty(
                        JMSConstants.WAIT_FOR_RESPONSE)).booleanValue();
            if(waitForResponse)
            {
                long timeout = (long) msgContext.getTimeout();
                byte[] response = endpoint.call(out.toByteArray(), timeout, props);
                Message msg = new Message(response);
                msgContext.setResponseMessage(msg);
            }
            else
            {
                endpoint.send(out.toByteArray(), props);
            }
        }
        catch(Exception e)
        {
            throw new AxisFault("failedSend", e);
        }
        finally
        {
            if (connector != null)
                JMSConnectorManager.getInstance().release(connector);
        }
    }

    private HashMap createSendProperties(MessageContext context)
    {
        //I'm not sure why this helper method is private, but 
        //we need to delegate to factory method that can build the
        //application-specific map of properties so make a change to
        //delegate here. 
        HashMap props = createApplicationProperties(context);

        if(context.containsProperty(JMSConstants.PRIORITY))
            props.put(JMSConstants.PRIORITY,
            context.getProperty(JMSConstants.PRIORITY));
        if(context.containsProperty(JMSConstants.DELIVERY_MODE))
            props.put(JMSConstants.DELIVERY_MODE,
            context.getProperty(JMSConstants.DELIVERY_MODE));
        if(context.containsProperty(JMSConstants.TIME_TO_LIVE))
            props.put(JMSConstants.TIME_TO_LIVE,
            context.getProperty(JMSConstants.TIME_TO_LIVE));
        if(context.containsProperty(JMSConstants.JMS_CORRELATION_ID))
            props.put(JMSConstants.JMS_CORRELATION_ID,
            context.getProperty(JMSConstants.JMS_CORRELATION_ID));
        return props;
    }

    /** Return a map of properties that makeup the application-specific
        for the JMS Messages.
     */
    protected HashMap createApplicationProperties(MessageContext context) {
        HashMap props = null;
        if (context.containsProperty(
            JMSConstants.JMS_APPLICATION_MSG_PROPS)) {
            props = new HashMap();
            props.putAll((Map)context.getProperty(
                JMSConstants.JMS_APPLICATION_MSG_PROPS));
        }
        return props;
    }
}