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
import org.apache.axis.MessageContext;
import org.apache.axis.AxisEngine;

import org.apache.axis.client.Transport;
import org.apache.axis.client.Call;

import org.apache.commons.logging.Log;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Messages;

import javax.jms.Destination;

import java.util.HashMap;
import java.util.Iterator;

/**
 * JMSTransport is the JMS-specific implemenation of org.apache.axis.client.Transport.
 *   It implements the setupMessageContextImpl() function to set JMS-specific message
 *   context fields and transport chains. Connector and connection factory
 *   properties are passed in during instantiation and are in turn passed through
 *   when creating a connector.
 *
 * @author Jaime Meritt  (jmeritt@sonicsoftware.com)
 * @author Richard Chung (rchung@sonicsoftware.com)
 * @author Dave Chappell (chappell@sonicsoftware.com)
 */
public class JMSTransport extends Transport
{
    protected static Log log =
            LogFactory.getLog(JMSTransport.class.getName());

    private HashMap connectors;
    private HashMap connectorProps;
    private HashMap connectionFactoryProps;
    private JMSConnector defaultConnector;
    private HashMap passwords;
    private Object connectorLock;

    public JMSTransport(HashMap connectorProps,
                        HashMap connectionFactoryProps)
    {
        transportName = "JMSTransport";
        connectors = new HashMap();
        passwords = new HashMap();
        this.connectorProps = connectorProps;
        this.connectionFactoryProps = connectionFactoryProps;
        connectorLock = new Object();
    }

    /**
     * Set up any transport-specific derived properties in the message context.
     * @param context the context to set up
     * @param message the client service instance
     * @param engine the engine containing the registries
     * @throws AxisFault if service cannot be found
     */
    public void setupMessageContextImpl(MessageContext context,
                                        Call message,
                                        AxisEngine engine)
        throws AxisFault
    {
        String username = message.getUsername();
        JMSConnector connector = null;
        try
        {
            if(username == null)
            {
                initConnectorIfNecessary();
                connector = defaultConnector;
            }
            else
            {
                String password = message.getPassword();
                synchronized(connectorLock)
                {
                    if(connectors.containsKey(username))
                    {
                        String oldPassword = (String)passwords.get(username);
                        if(password.equals(oldPassword))
                            connector = (JMSConnector)connectors.get(username);
                        else
                            throw new AxisFault("badUserPass");
                    }
                    else
                    {
                        connector = createConnector(username, password);
                        connectors.put(username, connector);
                        // I should really md5 hash these
                        passwords.put(username, password);
                    }
                }
            }
        }
        catch(Exception e)
        {
            log.error(Messages.getMessage("cannotConnectError"), e);

            if(e instanceof AxisFault)
                throw (AxisFault)e;
            throw new AxisFault("cannotConnect", e);
        }

        context.setProperty(JMSConstants.CONNECTOR, connector);

        //I would like to use the following, but that requires JMS-URL syntax
        //which I don't have so I will rely on message properties for now
        //String destination = message.getTargetEndpointAddress();

        Object tmp = message.getProperty(JMSConstants.DESTINATION);
        if(tmp != null && (tmp instanceof String || tmp instanceof Destination))
            context.setProperty(JMSConstants.DESTINATION, tmp);
        else
            context.removeProperty(JMSConstants.DESTINATION);

        tmp = message.getProperty(JMSConstants.WAIT_FOR_RESPONSE);
        if(tmp != null && tmp instanceof Boolean)
            context.setProperty(JMSConstants.WAIT_FOR_RESPONSE, tmp);
        else
            context.removeProperty(JMSConstants.WAIT_FOR_RESPONSE);

        tmp = message.getProperty(JMSConstants.DELIVERY_MODE);
        if(tmp != null && tmp instanceof Integer)
            context.setProperty(JMSConstants.DELIVERY_MODE, tmp);
        else
            context.removeProperty(JMSConstants.DELIVERY_MODE);

        tmp = message.getProperty(JMSConstants.PRIORITY);
        if(tmp != null && tmp instanceof Integer)
            context.setProperty(JMSConstants.PRIORITY, tmp);
        else
            context.removeProperty(JMSConstants.PRIORITY);

        tmp = message.getProperty(JMSConstants.TIME_TO_LIVE);
        if(tmp != null && tmp instanceof Long)
            context.setProperty(JMSConstants.TIME_TO_LIVE, tmp);
        else
            context.removeProperty(JMSConstants.TIME_TO_LIVE);

    }

    private void initConnectorIfNecessary()
        throws Exception
    {
        if(defaultConnector != null)
            return;
        synchronized(connectorLock)
        {
            //this is to catch a race issue when n threads do the null check
            //before one of them actually creates the default connector
            if(defaultConnector != null)
                return;
            defaultConnector = createConnector(null, null);
        }
    }

    private JMSConnector createConnector(String username, String password)
        throws Exception
    {
        JMSConnector connector = JMSConnectorFactory.
                createClientConnector(connectorProps, connectionFactoryProps,
                                      username, password);
        connector.start();
        return connector;
    }

    /**
     * Shuts down the connectors managed by this JMSTransport.
     */
    public void shutdown()
    {
        synchronized(connectorLock)
        {
            if(defaultConnector != null)
                defaultConnector.shutdown();

            Iterator connectorIter = connectors.values().iterator();
            while(connectorIter.hasNext())
            {
                ((JMSConnector)connectorIter.next()).shutdown();
            }
        }
    }

}