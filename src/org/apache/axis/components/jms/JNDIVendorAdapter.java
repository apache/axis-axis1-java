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
import java.util.Hashtable;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.axis.transport.jms.JMSConstants;
import org.apache.axis.transport.jms.JMSURLHelper;

/**
 * Uses JNDI to locate ConnectionFactory and Destinations
 *
 * @author Jaime Meritt (jmeritt@sonicsoftware.com)
 * @author Ray Chun (rchun@sonicsoftware.com)
 */
public class JNDIVendorAdapter extends JMSVendorAdapter
{
    public final static String CONTEXT_FACTORY                = "java.naming.factory.initial";
    public final static String PROVIDER_URL                   = "java.naming.provider.url";

    public final static String _CONNECTION_FACTORY_JNDI_NAME  = "ConnectionFactoryJNDIName";
    public final static String CONNECTION_FACTORY_JNDI_NAME   = JMSConstants.JMS_PROPERTY_PREFIX +
                                                                    _CONNECTION_FACTORY_JNDI_NAME;

    private Context context;

    public QueueConnectionFactory getQueueConnectionFactory(HashMap cfConfig)
        throws Exception
    {
        return (QueueConnectionFactory)getConnectionFactory(cfConfig);
    }

    public TopicConnectionFactory getTopicConnectionFactory(HashMap cfConfig)
        throws Exception
    {
        return (TopicConnectionFactory)getConnectionFactory(cfConfig);
    }

    private ConnectionFactory getConnectionFactory(HashMap cfProps)
        throws Exception
    {
        if(cfProps == null)
                throw new IllegalArgumentException("noCFProps");
        String jndiName = (String)cfProps.get(CONNECTION_FACTORY_JNDI_NAME);
        if(jndiName == null || jndiName.trim().length() == 0)
            throw new IllegalArgumentException("noCFName");

        Hashtable environment = new Hashtable(cfProps);

        // set the context factory if provided in the JMS URL
        String ctxFactory = (String)cfProps.get(CONTEXT_FACTORY);
        if (ctxFactory != null)
            environment.put(CONTEXT_FACTORY, ctxFactory);

        // set the provider url if provided in the JMS URL
        String providerURL = (String)cfProps.get(PROVIDER_URL);
        if (providerURL != null)
            environment.put(PROVIDER_URL, providerURL);

        context = new InitialContext(environment);

        return (ConnectionFactory)context.lookup(jndiName);
    }

    /**
     * Populates the connection factory config table with properties from
     * the JMS URL query string
     *
     * @param jmsurl The target endpoint address of the Axis call
     * @param cfConfig The set of properties necessary to create/configure the connection factory
     */
    public void addVendorConnectionFactoryProperties(JMSURLHelper jmsurl,
                                                     HashMap cfConfig)
    {
        // add the connection factory jndi name
        String cfJNDIName = jmsurl.getPropertyValue(_CONNECTION_FACTORY_JNDI_NAME);
        if (cfJNDIName != null)
            cfConfig.put(CONNECTION_FACTORY_JNDI_NAME, cfJNDIName);

        // add the initial ctx factory
        String ctxFactory = jmsurl.getPropertyValue(CONTEXT_FACTORY);
        if (ctxFactory != null)
            cfConfig.put(CONTEXT_FACTORY, ctxFactory);

        // add the provider url
        String providerURL = jmsurl.getPropertyValue(PROVIDER_URL);
        if (providerURL != null)
            cfConfig.put(PROVIDER_URL, providerURL);
    }

    /**
     * Check that the attributes of the candidate connection factory match the
     * requested connection factory properties.
     *
     * @param cf the candidate connection factory
     * @param originalJMSURL the URL which was used to create the connection factory
     * @param cfProps the set of properties that should be used to determine the match
     * @return true or false to indicate whether a match has been found
     */
    public boolean isMatchingConnectionFactory(ConnectionFactory cf,
                                               JMSURLHelper originalJMSURL,
                                               HashMap cfProps)
    {
        JMSURLHelper jmsurl = (JMSURLHelper)cfProps.get(JMSConstants.JMS_URL);

        // just check the connection factory jndi name
        String cfJndiName = jmsurl.getPropertyValue(_CONNECTION_FACTORY_JNDI_NAME);
        String originalCfJndiName = originalJMSURL.getPropertyValue(_CONNECTION_FACTORY_JNDI_NAME);

        if (cfJndiName.equalsIgnoreCase(originalCfJndiName))
            return true;

        return false;
    }

    public Queue getQueue(QueueSession session, String name)
        throws Exception
    {
        return (Queue)context.lookup(name);
    }

    public Topic getTopic(TopicSession session, String name)
        throws Exception
    {
        return (Topic)context.lookup(name);
    }
}