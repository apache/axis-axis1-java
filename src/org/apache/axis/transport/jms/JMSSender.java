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

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

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
        try
        {
            Object destination = msgContext.getProperty(JMSConstants.DESTINATION);
            if(destination == null)
                throw new AxisFault("noDestination");

            JMSConnector connector = (JMSConnector)msgContext.getProperty(
                                                    JMSConstants.CONNECTOR);
            JMSEndpoint endpoint = null;
            if(destination instanceof String)
                endpoint = connector.createEndpoint((String)destination);
            else
                endpoint = connector.createEndpoint((Destination)destination);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            msgContext.getRequestMessage().writeTo(out);

            HashMap props = createSendProperties(msgContext);
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
    }

    private HashMap createSendProperties(MessageContext context)
    {

        if(!context.containsProperty(JMSConstants.PRIORITY) &&
           !context.containsProperty(JMSConstants.DELIVERY_MODE) &&
           !context.containsProperty(JMSConstants.TIME_TO_LIVE))
        {
            return null;
        }

        HashMap props = new HashMap();
        if(context.containsProperty(JMSConstants.PRIORITY))
            props.put(JMSConstants.PRIORITY,
            context.getProperty(JMSConstants.PRIORITY));
        if(context.containsProperty(JMSConstants.DELIVERY_MODE))
            props.put(JMSConstants.DELIVERY_MODE,
            context.getProperty(JMSConstants.DELIVERY_MODE));
        if(context.containsProperty(JMSConstants.TIME_TO_LIVE))
            props.put(JMSConstants.TIME_TO_LIVE,
            context.getProperty(JMSConstants.TIME_TO_LIVE));
        return props;
    }


}