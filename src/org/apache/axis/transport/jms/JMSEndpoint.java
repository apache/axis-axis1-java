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

import javax.jms.Destination;
import javax.jms.Session;
import javax.jms.JMSException;
import javax.jms.MessageListener;

import java.util.HashMap;

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
        throws JMSException;

    /**
     * Send a message and wait for a response.
     *
     * @param message
     * @param timeout
     * @return
     * @throws JMSException
     */
    public byte[] call(byte[] message, long timeout)throws JMSException
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
        throws JMSException
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
    public void send(byte[] message)throws JMSException
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
        throws JMSException
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
        throws JMSException
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
        throws JMSException
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
