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

import javax.jms.QueueConnectionFactory;
import javax.jms.QueueConnection;
import javax.jms.Queue;
import javax.jms.TemporaryQueue;
import javax.jms.Session;
import javax.jms.QueueSession;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.JMSException;
import javax.jms.Destination;

import javax.naming.Context;

import java.util.HashMap;

/**
 * QueueConnector is a concrete JMSConnector subclass that specifically handles
 *   connections to queues (ptp domain).
 *
 * @author Jaime Meritt  (jmeritt@sonicsoftware.com)
 * @author Richard Chung (rchung@sonicsoftware.com)
 * @author Dave Chappell (chappell@sonicsoftware.com)
 */
public class QueueConnector extends JMSConnector
{

    public QueueConnector(ConnectionFactory factory,
                          int numRetries,
                          int numSessions,
                          long connectRetryInterval,
                          long interactRetryInterval,
                          long timeoutTime,
                          boolean allowReceive,
                          String clientID,
                          String username,
                          String password,
                          Context context)
        throws JMSException
    {
        super(factory, numRetries, numSessions, connectRetryInterval,
              interactRetryInterval, timeoutTime, allowReceive, clientID,
              username, password, context);
    }

    protected JMSEndpoint internalCreateEndpoint(String destination)
    {
        return new QueueEndpoint(destination);
    }

    /**
     * Create an endpoint for a queue destination.
     *
     * @param destination
     * @return
     * @throws JMSException
     */
    public JMSEndpoint createEndpoint(Destination destination)
        throws JMSException
    {
        if(!(destination instanceof Queue))
            throw new IllegalArgumentException("The input must be a queue for this connector");
        return new QueueDestinationEndpoint((Queue)destination);
    }

    protected Connection internalConnect(ConnectionFactory connectionFactory,
                                         String username,
                                         String password)
        throws JMSException
    {
        QueueConnectionFactory qcf = (QueueConnectionFactory)connectionFactory;
        if(username == null)
            return qcf.createQueueConnection();

        return qcf.createQueueConnection(username, password);
    }


    protected SyncConnection createSyncConnection(ConnectionFactory factory,
                                                  Connection connection,
                                                  int numSessions,
                                                  String threadName,
                                                  String clientID,
                                                  String username,
                                                  String password)

        throws JMSException
    {
        return new QueueSyncConnection((QueueConnectionFactory)factory,
                                       (QueueConnection)connection, numSessions,
                                       threadName, clientID, username, password);
    }

    private QueueSession createQueueSession(QueueConnection connection, int ackMode)
        throws JMSException
    {
        return connection.createQueueSession(false, ackMode);
    }

    private Queue createQueue(QueueSession session, String subject)
        throws JMSException
    {
        return session.createQueue(subject);
    }

    private QueueReceiver createReceiver(QueueSession session,
                                         Queue queue,
                                         String messageSelector)
        throws JMSException
    {
        return session.createReceiver(queue, messageSelector);
    }

    private final class QueueSyncConnection extends SyncConnection
    {
        QueueSyncConnection(QueueConnectionFactory connectionFactory,
                            QueueConnection connection,
                            int numSessions,
                            String threadName,
                            String clientID,
                            String username,
                            String password)
            throws JMSException
        {
            super(connectionFactory, connection, numSessions, threadName,
                  clientID, username, password);
        }

        protected SendSession createSendSession(javax.jms.Connection connection)
            throws JMSException
        {
            QueueSession session = createQueueSession((QueueConnection)connection,
                                        JMSConstants.DEFAULT_ACKNOWLEDGE_MODE);
            QueueSender sender = session.createSender(null);
            return new QueueSendSession(session, sender);
        }

        private final class QueueSendSession extends SendSession
        {
            QueueSendSession(QueueSession session,
                             QueueSender  sender)
                throws JMSException
            {
                super(session, sender);
            }

            protected MessageConsumer createConsumer(Destination destination)
                throws JMSException
            {
                return createReceiver((QueueSession)m_session, (Queue)destination, null);
            }


            protected Destination createTemporaryDestination()
                throws JMSException
            {
                return ((QueueSession)m_session).createTemporaryQueue();
            }

            protected void deleteTemporaryDestination(Destination destination)
                throws JMSException
            {
                ((TemporaryQueue)destination).delete();
            }

            protected void send(Destination destination, Message message,
                                int deliveryMode, int priority, long timeToLive)
                throws JMSException
            {
                ((QueueSender)m_producer).send((Queue)destination, message,
                                                deliveryMode, priority, timeToLive);
            }

        }
    }

    private class QueueEndpoint
        extends JMSEndpoint
    {
        String m_queueName;

        QueueEndpoint(String queueName)
        {
            super(QueueConnector.this);
            m_queueName = queueName;
        }

        Destination getDestination(Session session)
            throws JMSException
        {
            return createQueue((QueueSession)session, m_queueName);
        }

        public String toString()
        {
            StringBuffer buffer = new StringBuffer("QueueEndpoint:");
            buffer.append(m_queueName);
            return buffer.toString();
        }

        public boolean equals(Object object)
        {
            if(!super.equals(object))
                return false;

            if(!(object instanceof QueueEndpoint))
                return false;

            return m_queueName.equals(((QueueEndpoint)object).m_queueName);
        }
    }


    private final class QueueDestinationEndpoint
        extends QueueEndpoint
    {
        Queue m_queue;

        QueueDestinationEndpoint(Queue queue)
            throws JMSException
        {
            super(queue.getQueueName());
            m_queue = queue;
        }

        Destination getDestination(Session session)
        {
            return m_queue;
        }

    }

    protected AsyncConnection createAsyncConnection(ConnectionFactory factory,
                                                    Connection connection,
                                                    String threadName,
                                                    String clientID,
                                                    String username,
                                                    String password)
        throws JMSException
    {
        return new QueueAsyncConnection((QueueConnectionFactory)factory,
                                        (QueueConnection)connection, threadName,
                                        clientID, username, password);
    }

    private final class QueueAsyncConnection extends AsyncConnection
    {

        QueueAsyncConnection(QueueConnectionFactory connectionFactory,
                             QueueConnection connection,
                             String threadName,
                             String clientID,
                             String username,
                             String password)
            throws JMSException
        {
            super(connectionFactory, connection, threadName, clientID, username, password);
        }

        protected ListenerSession createListenerSession(javax.jms.Connection connection,
                                                        Subscription subscription)
            throws JMSException
        {
            QueueSession session = createQueueSession((QueueConnection)connection,
                                                      subscription.m_ackMode);
            QueueReceiver receiver = createReceiver(session,
                        (Queue)subscription.m_endpoint.getDestination(session),
                        subscription.m_messageSelector);
            return new ListenerSession(session, receiver, subscription);
        }

    }

}