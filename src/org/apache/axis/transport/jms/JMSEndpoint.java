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

import java.util.HashMap;

import javax.jms.Destination;
import javax.jms.MessageListener;
import javax.jms.Session;

/**
 * JMSEndpoint encapsulates interactions w/ a JMS destination.
 *
 * @author Jaime Meritt  (jmeritt@sonicsoftware.com)
 * @author Richard Chung (rchung@sonicsoftware.com)
 * @author Dave Chappell (chappell@sonicsoftware.com)
 */
public abstract class JMSEndpoint
{
    private JMSConnector m_connector;

    protected JMSEndpoint(JMSConnector connector)
    {
        m_connector = connector;
    }

    abstract Destination getDestination(Session session)
        throws Exception;

    /**
     * Send a message and wait for a response.
     *
     * @param message
     * @param timeout
     * @return
     * @throws JMSException
     */
    public byte[] call(byte[] message, long timeout)throws Exception
    {
        return m_connector.getSendConnection().call(this, message, timeout, null);
    }

    /**
     * Send a message and wait for a response.
     *
     * @param message
     * @param timeout
     * @param properties
     * @return
     * @throws JMSException
     */
    public byte[] call(byte[] message, long timeout, HashMap properties)
        throws Exception
    {
        if(properties != null)
            properties = (HashMap)properties.clone();
        return m_connector.getSendConnection().call(this, message, timeout, properties);
    }

    /**
     * Send a message w/o waiting for a response.
     *
     * @param message
     * @throws JMSException
     */
    public void send(byte[] message)throws Exception
    {
        m_connector.getSendConnection().send(this, message, null);
    }

    /**
     * Send a message w/o waiting for a response.
     *
     * @param message
     * @param properties
     * @throws JMSException
     */
    public void send(byte[] message, HashMap properties)
        throws Exception
    {
        if(properties != null)
            properties = (HashMap)properties.clone();
        m_connector.getSendConnection().send(this, message, properties);
    }

    /**
     * Register a MessageListener.
     *
     * @param listener
     * @throws JMSException
     */
    public void registerListener(MessageListener listener)
        throws Exception
    {
        m_connector.getReceiveConnection().subscribe(createSubscription(listener, null));
    }

    /**
     * Register a MessageListener.
     *
     * @param listener
     * @param properties
     * @throws JMSException
     */
    public void registerListener(MessageListener listener, HashMap properties)
        throws Exception
    {
        if(properties != null)
            properties = (HashMap)properties.clone();
        m_connector.getReceiveConnection().subscribe(createSubscription(listener, properties));
    }

    /**
     * Unregister a message listener.
     *
     * @param listener
     */
    public void unregisterListener(MessageListener listener)
    {
        m_connector.getReceiveConnection().unsubscribe(createSubscription(listener, null));
    }

    /**
     * Unregister a message listener.
     *
     * @param listener
     * @param properties
     */
    public void unregisterListener(MessageListener listener, HashMap properties)
    {
        if(properties != null)
            properties = (HashMap)properties.clone();
        m_connector.getReceiveConnection().unsubscribe(createSubscription(listener, properties));
    }

    protected Subscription createSubscription(MessageListener listener,
                                              HashMap properties)
    {
        return new Subscription(listener, this, properties);
    }

    public int hashCode()
    {
        return toString().hashCode();
    }

    public boolean equals(Object object)
    {
        if(object == null || !(object instanceof JMSEndpoint))
            return false;
        return true;
    }
}
