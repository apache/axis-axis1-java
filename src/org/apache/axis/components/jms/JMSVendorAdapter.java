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

package org.apache.axis.components.jms;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jms.InvalidDestinationException;
import javax.jms.JMSException;
import javax.jms.JMSSecurityException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;

import org.apache.axis.MessageContext;
import org.apache.axis.client.Call;
import org.apache.axis.transport.jms.JMSConstants;
import org.apache.axis.transport.jms.JMSURLHelper;

/**
 * SPI Interface that all JMSVendorAdaptors must implement.  Allows for
 * ConnectionFactory creation and Destination lookup
 *
 * @author Jaime Meritt (jmeritt@sonicsoftware.com)
 * @author Ray Chun (rchun@sonicsoftware.com)
 */
public abstract class JMSVendorAdapter
{
    public static final int SEND_ACTION = 0;
    public static final int CONNECT_ACTION = 1;
    public static final int SUBSCRIBE_ACTION = 2;
    public static final int RECEIVE_ACTION = 3;
    public static final int ON_EXCEPTION_ACTION = 4;

    public abstract QueueConnectionFactory getQueueConnectionFactory(HashMap cfProps)
        throws Exception;
    public abstract TopicConnectionFactory getTopicConnectionFactory(HashMap cfProps)
        throws Exception;

    // let adapters add vendor-specific properties or override standard ones
    public abstract void addVendorConnectionFactoryProperties(JMSURLHelper jmsurl, HashMap cfProps);

    // let adapters match connectors using vendor-specific connection factory properties
    public abstract boolean isMatchingConnectionFactory(javax.jms.ConnectionFactory cf, JMSURLHelper jmsurl, HashMap cfProps);

    // returns <adapter> in 'org.apache.axis.components.jms.<adapter>VendorAdapter'
    public String getVendorId()
    {
        String name = this.getClass().getName();

        // cut off the trailing 'VendorAdapter'
        if (name.endsWith(JMSConstants.ADAPTER_POSTFIX))
        {
            int index = name.lastIndexOf(JMSConstants.ADAPTER_POSTFIX);
            name = name.substring(0,index);
        }

        // cut off the leading 'org.apache.axis.components.jms.'
        int index = name.lastIndexOf(".");
        if (index > 0)
            name = name.substring(index+1);

        return name;
    }

    /**
     * Creates a JMS connector property table using values supplied in
     * the endpoint address.  Properties are translated from the short form
     * in the endpoint address to the long form (prefixed by "transport.jms.")
     *
     * @param jmsurl the endpoint address
     * @return the set of properties to be used for instantiating the JMS connector
     */
    public HashMap getJMSConnectorProperties(JMSURLHelper jmsurl)
    {
        HashMap connectorProps = new HashMap();

        // the JMS URL may be useful when matching connectors
        connectorProps.put(JMSConstants.JMS_URL, jmsurl);

        // JMSConstants.CLIENT_ID,
        String clientID = jmsurl.getPropertyValue(JMSConstants._CLIENT_ID);
        if (clientID != null)
            connectorProps.put(JMSConstants.CLIENT_ID, clientID);

        // JMSConstants.CONNECT_RETRY_INTERVAL,
        String connectRetryInterval = jmsurl.getPropertyValue(JMSConstants._CONNECT_RETRY_INTERVAL);
        if (connectRetryInterval != null)
            connectorProps.put(JMSConstants.CONNECT_RETRY_INTERVAL, connectRetryInterval);

        // JMSConstants.INTERACT_RETRY_INTERVAL,
        String interactRetryInterval = jmsurl.getPropertyValue(JMSConstants._INTERACT_RETRY_INTERVAL);
        if (interactRetryInterval != null)
            connectorProps.put(JMSConstants.INTERACT_RETRY_INTERVAL, interactRetryInterval);

        // JMSConstants.DOMAIN
        String domain = jmsurl.getPropertyValue(JMSConstants._DOMAIN);
        if (domain != null)
            connectorProps.put(JMSConstants.DOMAIN, domain);

        // JMSConstants.NUM_RETRIES
        String numRetries = jmsurl.getPropertyValue(JMSConstants._NUM_RETRIES);
        if (numRetries != null)
            connectorProps.put(JMSConstants.NUM_RETRIES, numRetries);

        // JMSConstants.NUM_SESSIONS
        String numSessions = jmsurl.getPropertyValue(JMSConstants._NUM_SESSIONS);
        if (numSessions != null)
            connectorProps.put(JMSConstants.NUM_SESSIONS, numSessions);

        // JMSConstants.TIMEOUT_TIME,
        String timeoutTime = jmsurl.getPropertyValue(JMSConstants._TIMEOUT_TIME);
        if (timeoutTime != null)
            connectorProps.put(JMSConstants.TIMEOUT_TIME, timeoutTime);

        return connectorProps;
    }

    /**
     *
     * Creates a connection factory property table using values supplied in
     * the endpoint address
     *
     * @param jmsurl  the endpoint address
     * @return the set of properties to be used for instantiating the connection factory
     */
    public HashMap getJMSConnectionFactoryProperties(JMSURLHelper jmsurl)
    {
        HashMap cfProps = new HashMap();

        // hold on to the original address (this will be useful when the JNDI vendor adapter
        // matches connectors)
        cfProps.put(JMSConstants.JMS_URL, jmsurl);

        // JMSConstants.DOMAIN
        String domain = jmsurl.getPropertyValue(JMSConstants._DOMAIN);
        if (domain != null)
            cfProps.put(JMSConstants.DOMAIN, domain);

        // allow vendors to customize the cf properties table
        addVendorConnectionFactoryProperties(jmsurl, cfProps);

        return cfProps;
    }

    public Queue getQueue(QueueSession session, String name)
        throws Exception
    {
        return session.createQueue(name);
    }

    public Topic getTopic(TopicSession session, String name)
        throws Exception
    {
        return session.createTopic(name);
    }

    public boolean isRecoverable(Throwable thrown, int action)
    {
        if(thrown instanceof RuntimeException ||
           thrown instanceof Error ||
           thrown instanceof JMSSecurityException ||
           thrown instanceof InvalidDestinationException)
            return false;
        if(action == ON_EXCEPTION_ACTION)
            return false;
        return true;
    }

    public void setProperties(Message message, HashMap props)
        throws JMSException
    {
        Iterator iter = props.keySet().iterator();
        while (iter.hasNext())
        {
            String key = (String)iter.next();
            String value = (String)props.get(key);

            message.setStringProperty(key, value);
        }
    }

    /**
     * Set JMS properties in the message context.
     *
     * TODO: just copy all properties that are not used for the JMS connector
     * or connection factory
     */
    public void setupMessageContext(MessageContext context,
                                    Call call,
                                    JMSURLHelper jmsurl)
    {
        Object tmp = null;

        String jmsurlDestination = null;
        if (jmsurl != null)
            jmsurlDestination = jmsurl.getDestination();
        if (jmsurlDestination != null)
            context.setProperty(JMSConstants.DESTINATION, jmsurlDestination);
        else
        {
            tmp = call.getProperty(JMSConstants.DESTINATION);
            if (tmp != null && tmp instanceof String)
                context.setProperty(JMSConstants.DESTINATION, tmp);
            else
                context.removeProperty(JMSConstants.DESTINATION);
        }

        String delivMode = null;
        if (jmsurl != null)
            delivMode = jmsurl.getPropertyValue(JMSConstants._DELIVERY_MODE);
        if (delivMode != null)
        {
            int mode = JMSConstants.DEFAULT_DELIVERY_MODE;
            if (delivMode.equalsIgnoreCase(JMSConstants.DELIVERY_MODE_PERSISTENT))
                mode = javax.jms.DeliveryMode.PERSISTENT;
            else if (delivMode.equalsIgnoreCase(JMSConstants.DELIVERY_MODE_NONPERSISTENT))
                mode = javax.jms.DeliveryMode.NON_PERSISTENT;
            context.setProperty(JMSConstants.DELIVERY_MODE, new Integer(mode));
        }
        else
        {
            tmp = call.getProperty(JMSConstants.DELIVERY_MODE);
            if(tmp != null && tmp instanceof Integer)
                context.setProperty(JMSConstants.DELIVERY_MODE, tmp);
            else
                context.removeProperty(JMSConstants.DELIVERY_MODE);
        }

        String prio = null;
        if (jmsurl != null)
            prio = jmsurl.getPropertyValue(JMSConstants._PRIORITY);
        if (prio != null)
            context.setProperty(JMSConstants.PRIORITY, Integer.valueOf(prio));
        else
        {
            tmp = call.getProperty(JMSConstants.PRIORITY);
            if(tmp != null && tmp instanceof Integer)
                context.setProperty(JMSConstants.PRIORITY, tmp);
            else
                context.removeProperty(JMSConstants.PRIORITY);
        }

        String ttl = null;
        if (jmsurl != null)
            ttl = jmsurl.getPropertyValue(JMSConstants._TIME_TO_LIVE);
        if (ttl != null)
            context.setProperty(JMSConstants.TIME_TO_LIVE, Long.valueOf(ttl));
        else
        {
            tmp = call.getProperty(JMSConstants.TIME_TO_LIVE);
            if(tmp != null && tmp instanceof Long)
                context.setProperty(JMSConstants.TIME_TO_LIVE, tmp);
            else
                context.removeProperty(JMSConstants.TIME_TO_LIVE);
        }

        String wait = null;
        if (jmsurl != null)
            wait = jmsurl.getPropertyValue(JMSConstants._WAIT_FOR_RESPONSE);
        if (wait != null)
            context.setProperty(JMSConstants.WAIT_FOR_RESPONSE, Boolean.valueOf(wait));
        else
        {
            tmp = call.getProperty(JMSConstants.WAIT_FOR_RESPONSE);
            if(tmp != null && tmp instanceof Boolean)
                context.setProperty(JMSConstants.WAIT_FOR_RESPONSE, tmp);
            else
                context.removeProperty(JMSConstants.WAIT_FOR_RESPONSE);
        }
        setupApplicationProperties(context, call, jmsurl);
    }
    
    public void setupApplicationProperties(MessageContext context,
                                    Call call,
                                    JMSURLHelper jmsurl)
    {
        //start with application properties from the URL
        Map appProps = new HashMap();
        if (jmsurl != null && jmsurl.getApplicationProperties() != null) {
            for(Iterator itr=jmsurl.getApplicationProperties().iterator();
                itr.hasNext();) {
                String name = (String)itr.next();
                appProps.put(name,jmsurl.getPropertyValue(name));
            }
        }
        
        //next add application properties from the message context
        Map ctxProps = 
           (Map)context.getProperty(JMSConstants.JMS_APPLICATION_MSG_PROPS);
        if (ctxProps != null) {
            appProps.putAll(ctxProps);
        }
        
        //finally add the properties from the call
        Map callProps = 
            (Map)call.getProperty(JMSConstants.JMS_APPLICATION_MSG_PROPS);
        if (callProps != null) {
            appProps.putAll(callProps);
        }
        
        //now tore these properties within the context
        context.setProperty(JMSConstants.JMS_APPLICATION_MSG_PROPS,appProps);
    }
}