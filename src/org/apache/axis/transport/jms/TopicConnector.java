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

import java.lang.reflect.InvocationTargetException;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

import javax.jms.MessageListener;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TemporaryTopic;
import javax.jms.Destination;
import javax.jms.Session;
import javax.jms.TopicSession;
import javax.jms.TopicPublisher;
import javax.jms.TopicSubscriber;
import javax.jms.Connection;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.ExceptionListener;

import javax.naming.Context;

/**
 * TopicConnector is a concrete JMSConnector subclass that specifically handles
 *   connections to topics (pub-sub domain).
 *
 * @author Jaime Meritt  (jmeritt@sonicsoftware.com)
 * @author Richard Chung (rchung@sonicsoftware.com)
 * @author Dave Chappell (chappell@sonicsoftware.com)
 */
public class TopicConnector extends JMSConnector
{
    public TopicConnector(TopicConnectionFactory factory,
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
              interactRetryInterval, timeoutTime, allowReceive,
              clientID, username, password, context);
    }

    protected Connection internalConnect(ConnectionFactory connectionFactory,
                                         String username, String password)
        throws JMSException
    {
        TopicConnectionFactory tcf = (TopicConnectionFactory)connectionFactory;
        if(username == null)
            return tcf.createTopicConnection();

        return tcf.createTopicConnection(username, password);
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
        return new TopicSyncConnection((TopicConnectionFactory)factory,
                                       (TopicConnection)connection, numSessions,
                                       threadName, clientID, username, password);
    }

    protected AsyncConnection createAsyncConnection(ConnectionFactory factory,
                                                    Connection connection,
                                                    String threadName,
                                                    String clientID,
                                                    String username,
                                                    String password)
        throws JMSException
    {
        return new TopicAsyncConnection((TopicConnectionFactory)factory,
                                        (TopicConnection)connection, threadName,
                                        clientID, username, password);
    }

    protected JMSEndpoint internalCreateEndpoint(String destination)
    {
        return new TopicEndpoint(destination);
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
        if(!(destination instanceof Topic))
            throw new IllegalArgumentException("The input be a topic for this connector");
        return new TopicDestinationEndpoint((Topic)destination);
    }

    private TopicSession createTopicSession(TopicConnection connection, int ackMode)
        throws JMSException
    {
        return connection.createTopicSession(false,
                                             ackMode);
    }

    private Topic createTopic(TopicSession session, String subject)
        throws JMSException
    {
        return session.createTopic(subject);
    }

    private TopicSubscriber createSubscriber(TopicSession session,
                                             TopicSubscription subscription)
        throws JMSException
    {
        if(subscription.isDurable())
            return createDurableSubscriber(session,
                        (Topic)subscription.m_endpoint.getDestination(session),
                        subscription.m_subscriptionName,
                        subscription.m_messageSelector,
                        subscription.m_noLocal);
        else
            return createSubscriber(session,
                        (Topic)subscription.m_endpoint.getDestination(session),
                        subscription.m_messageSelector,
                        subscription.m_noLocal);
    }

    private TopicSubscriber createDurableSubscriber(TopicSession session,
                                                    Topic topic,
                                                    String subscriptionName,
                                                    String messageSelector,
                                                    boolean noLocal)
        throws JMSException
    {
        return session.createDurableSubscriber(topic, subscriptionName,
                                               messageSelector, noLocal);
    }

    private TopicSubscriber createSubscriber(TopicSession session,
                                             Topic topic,
                                             String messageSelector,
                                             boolean noLocal)
        throws JMSException
    {
        return session.createSubscriber(topic, messageSelector, noLocal);
    }




    private final class TopicAsyncConnection extends AsyncConnection
    {

        TopicAsyncConnection(TopicConnectionFactory connectionFactory,
                             TopicConnection connection,
                             String threadName,
                             String clientID,
                             String username,
                             String password)

            throws JMSException
        {
            super(connectionFactory, connection, threadName,
                  clientID, username, password);
        }

        protected ListenerSession createListenerSession(javax.jms.Connection connection,
                                                        Subscription subscription)
            throws JMSException
        {
            TopicSession session = createTopicSession((TopicConnection)connection,
                                                      subscription.m_ackMode);
            TopicSubscriber subscriber = createSubscriber(session,
                                                (TopicSubscription)subscription);
            return new TopicListenerSession(session, subscriber,
                                                (TopicSubscription)subscription);
        }

        private final class TopicListenerSession extends ListenerSession
        {

            TopicListenerSession(TopicSession session,
                                 TopicSubscriber subscriber,
                                 TopicSubscription subscription)
                throws JMSException
            {
                super(session, subscriber, subscription);
            }

            void cleanup()
            {
                try{m_consumer.close();}catch(Exception ignore){}
                try
                {
                    TopicSubscription sub = (TopicSubscription)m_subscription;
                    if(sub.isDurable() && sub.m_unsubscribe)
                    {
                        ((TopicSession)m_session).unsubscribe(sub.m_subscriptionName);
                    }
                }
                catch(Exception ignore){}
                try{m_session.close();}catch(Exception ignore){}

            }
        }
    }

    private final class TopicSyncConnection extends SyncConnection
    {
        TopicSyncConnection(TopicConnectionFactory connectionFactory,
                            TopicConnection connection,
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
            TopicSession session = createTopicSession((TopicConnection)connection,
                                            JMSConstants.DEFAULT_ACKNOWLEDGE_MODE);
            TopicPublisher publisher = session.createPublisher(null);
            return new TopicSendSession(session, publisher);
        }

        private final class TopicSendSession extends SendSession
        {
            TopicSendSession(TopicSession session,
                             TopicPublisher publisher)
                throws JMSException
            {
                super(session, publisher);
            }


            protected MessageConsumer createConsumer(Destination destination)
                throws JMSException
            {
                return createSubscriber((TopicSession)m_session, (Topic)destination,
                                        null, JMSConstants.DEFAULT_NO_LOCAL);
            }

            protected void deleteTemporaryDestination(Destination destination)
                throws JMSException
            {
                ((TemporaryTopic)destination).delete();
            }


            protected Destination createTemporaryDestination()
                throws JMSException
            {
                return ((TopicSession)m_session).createTemporaryTopic();
            }

            protected void send(Destination destination, Message message,
                                int deliveryMode, int priority, long timeToLive)
                throws JMSException
            {
                ((TopicPublisher)m_producer).publish((Topic)destination, message,
                                                deliveryMode, priority, timeToLive);
            }

        }
    }



    private class TopicEndpoint
        extends JMSEndpoint
    {
        String m_topicName;

        TopicEndpoint(String topicName)
        {
            super(TopicConnector.this);
            m_topicName = topicName;
        }

        Destination getDestination(Session session)
            throws JMSException
        {
            return createTopic((TopicSession)session, m_topicName);
        }

        protected Subscription createSubscription(MessageListener listener,
                                                  HashMap properties)
        {
            return new TopicSubscription(listener, this, properties);
        }

        public String toString()
        {
            StringBuffer buffer = new StringBuffer("TopicEndpoint:");
            buffer.append(m_topicName);
            return buffer.toString();
        }

        public boolean equals(Object object)
        {
            if(!super.equals(object))
                return false;

            if(!(object instanceof TopicEndpoint))
                return false;

            return m_topicName.equals(((TopicEndpoint)object).m_topicName);
        }
    }

    private final class TopicSubscription extends Subscription
    {
        String m_subscriptionName;
        boolean m_unsubscribe;
        boolean m_noLocal;

        TopicSubscription(MessageListener listener,
                          JMSEndpoint endpoint,
                          HashMap properties)
        {
            super(listener, endpoint, properties);
            m_subscriptionName = MapUtils.removeStringProperty(properties,
                                                JMSConstants.SUBSCRIPTION_NAME,
                                                null);
            m_unsubscribe = MapUtils.removeBooleanProperty(properties,
                                                JMSConstants.UNSUBSCRIBE,
                                                JMSConstants.DEFAULT_UNSUBSCRIBE);
            m_noLocal = MapUtils.removeBooleanProperty(properties,
                                                JMSConstants.NO_LOCAL,
                                                JMSConstants.DEFAULT_NO_LOCAL);
        }

        boolean isDurable()
        {
            return m_subscriptionName != null;
        }

        public boolean equals(Object obj)
        {
            if(!super.equals(obj))
                return false;
            if(!(obj instanceof TopicSubscription))
                return false;

            TopicSubscription other = (TopicSubscription)obj;
            if(other.m_unsubscribe != m_unsubscribe || other.m_noLocal != m_noLocal)
                return false;

            if(isDurable())
            {
                return other.isDurable() && other.m_subscriptionName.equals(m_subscriptionName);
            }
            else if(other.isDurable())
                return false;
            else
                return true;
        }

        public String toString()
        {
            StringBuffer buffer = new StringBuffer(super.toString());
            buffer.append(":").append(m_noLocal).append(":").append(m_unsubscribe);
            if(isDurable())
            {
                buffer.append(":");
                buffer.append(m_subscriptionName);
            }
            return buffer.toString();
        }

    }

    private final class TopicDestinationEndpoint
        extends TopicEndpoint
    {
        Topic m_topic;

        TopicDestinationEndpoint(Topic topic)
            throws JMSException
        {
            super(topic.getTopicName());
            m_topic = topic;
        }

        Destination getDestination(Session session)
        {
            return m_topic;
        }

    }


}