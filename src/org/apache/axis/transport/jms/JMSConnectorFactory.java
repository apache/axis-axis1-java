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

import org.apache.axis.components.jms.JMSVendorAdapter;
import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

import java.util.HashMap;

/**
 * JMSConnectorFactory is a factory class for creating JMSConnectors. It can
 *   create both client connectors and server connectors.  A server connector
 *   is configured to allow asynchronous message receipt, while a client
 *   connector is not.
 *
 * JMSConnectorFactory can also be used to select an appropriately configured
 *   JMSConnector from an existing pool of connectors.
 *
 * @author Jaime Meritt  (jmeritt@sonicsoftware.com)
 * @author Richard Chung (rchung@sonicsoftware.com)
 * @author Dave Chappell (chappell@sonicsoftware.com)
 * @author Ray Chun (rchun@sonicsoftware.com)
 */
public abstract class JMSConnectorFactory
{
    protected static Log log =
            LogFactory.getLog(JMSConnectorFactory.class.getName());

    /**
     * Performs an initial check on the connector properties, and then defers
     * to the vendor adapter for matching on the vendor-specific connection factory.
     *
     * @param connectors the list of potential matches
     * @param connectorProps the set of properties to be used for matching the connector
     * @param cfProps the set of properties to be used for matching the connection factory
     * @param username the user requesting the connector
     * @param password the password associated with the requesting user
     * @param adapter the vendor adapter specified in the JMS URL
     * @return a JMSConnector that matches the specified properties
     */
     public static JMSConnector matchConnector(java.util.Set connectors,
                                               HashMap connectorProps,
                                               HashMap cfProps,
                                               String username,
                                               String password,
                                               JMSVendorAdapter adapter)
    {
        java.util.Iterator iter = connectors.iterator();
        while (iter.hasNext())
        {
            JMSConnector conn = (JMSConnector) iter.next();

            // username
            String connectorUsername = conn.getUsername();
            if (!( ((connectorUsername == null) && (username == null)) ||
                   ((connectorUsername != null) && (username != null) && (connectorUsername.equals(username))) ))
                continue;

            // password
            String connectorPassword = conn.getPassword();
            if (!( ((connectorPassword == null) && (password == null)) ||
                   ((connectorPassword != null) && (password != null) && (connectorPassword.equals(password))) ))
                continue;

            // num retries
            int connectorNumRetries = conn.getNumRetries();
            String propertyNumRetries = (String)connectorProps.get(JMSConstants.NUM_RETRIES);
            int numRetries = JMSConstants.DEFAULT_NUM_RETRIES;
            if (propertyNumRetries != null)
                numRetries = Integer.parseInt(propertyNumRetries);
            if (connectorNumRetries != numRetries)
                continue;

            // client id
            String connectorClientID = conn.getClientID();
            String clientID = (String)connectorProps.get(JMSConstants.CLIENT_ID);
            if (!( ((connectorClientID == null) && (clientID == null))
                   ||
                   ((connectorClientID != null) && (clientID != null) && connectorClientID.equals(clientID)) ))
                continue;

            // domain
            String connectorDomain = (conn instanceof QueueConnector) ? JMSConstants.DOMAIN_QUEUE : JMSConstants.DOMAIN_TOPIC;
            String propertyDomain = (String)connectorProps.get(JMSConstants.DOMAIN);
            String domain = JMSConstants.DOMAIN_DEFAULT;
            if (propertyDomain != null)
                domain = propertyDomain;
            if (!( ((connectorDomain == null) && (domain == null))
                   ||
                   ((connectorDomain != null) && (domain != null) && connectorDomain.equalsIgnoreCase(domain)) ))
                continue;

            // the connection factory must also match for the connector to be reused
            JMSURLHelper jmsurl = conn.getJMSURL();
            if (adapter.isMatchingConnectionFactory(conn.getConnectionFactory(), jmsurl, cfProps))
            {
                // attempt to reserve the connector
                try
                {
                    JMSConnectorManager.getInstance().reserve(conn);

                    if (log.isDebugEnabled()) {
                        log.debug("JMSConnectorFactory: Found matching connector");
                    }
                }
                catch (Exception e)
                {
                    // ignore. the connector may be in the process of shutting down, so try the next element
                    continue;
                }

                return conn;
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("JMSConnectorFactory: No matching connectors found");
        }

        return null;
    }

    /**
     * Static method to create a server connector. Server connectors can
     *   accept incoming requests.
     *
     * @param connectorConfig
     * @param cfConfig
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public static JMSConnector createServerConnector(HashMap connectorConfig,
                                              HashMap cfConfig,
                                              String username,
                                              String password,
                                              JMSVendorAdapter adapter)
        throws Exception
    {
        return createConnector(connectorConfig, cfConfig, true,
                               username, password, adapter);
    }

    /**
     * Static method to create a client connector. Client connectors cannot
     *   accept incoming requests.
     *
     * @param connectorConfig
     * @param cfConfig
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public static JMSConnector createClientConnector(HashMap connectorConfig,
                                              HashMap cfConfig,
                                              String username,
                                              String password,
                                              JMSVendorAdapter adapter)
        throws Exception
    {
        return createConnector(connectorConfig, cfConfig, false,
                               username, password, adapter);
    }

    private static JMSConnector createConnector(HashMap connectorConfig,
                                                HashMap cfConfig,
                                                boolean allowReceive,
                                                String username,
                                                String password,
                                                JMSVendorAdapter adapter)
        throws Exception
    {
        if(connectorConfig != null)
            connectorConfig = (HashMap)connectorConfig.clone();
        int numRetries = MapUtils.removeIntProperty(connectorConfig,
                                    JMSConstants.NUM_RETRIES,
                                    JMSConstants.DEFAULT_NUM_RETRIES);

        int numSessions = MapUtils.removeIntProperty(connectorConfig,
                                    JMSConstants.NUM_SESSIONS,
                                    JMSConstants.DEFAULT_NUM_SESSIONS);

        long connectRetryInterval = MapUtils.removeLongProperty(connectorConfig,
                                    JMSConstants.CONNECT_RETRY_INTERVAL,
                                    JMSConstants.DEFAULT_CONNECT_RETRY_INTERVAL);

        long interactRetryInterval = MapUtils.removeLongProperty(connectorConfig,
                                    JMSConstants.INTERACT_RETRY_INTERVAL,
                                    JMSConstants.DEFAULT_INTERACT_RETRY_INTERVAL);

        long timeoutTime = MapUtils.removeLongProperty(connectorConfig,
                                    JMSConstants.TIMEOUT_TIME,
                                    JMSConstants.DEFAULT_TIMEOUT_TIME);

        String clientID = MapUtils.removeStringProperty(connectorConfig,
                                    JMSConstants.CLIENT_ID,
                                    null);
        String domain = MapUtils.removeStringProperty(connectorConfig,
                                    JMSConstants.DOMAIN,
                                    JMSConstants.DOMAIN_DEFAULT);

        // this will be set if the target endpoint address was set on the Axis call
        JMSURLHelper jmsurl = (JMSURLHelper)connectorConfig.get(JMSConstants.JMS_URL);

        if(cfConfig == null)
            throw new IllegalArgumentException("noCfConfig");

        if(domain.equals(JMSConstants.DOMAIN_QUEUE))
        {
            return new QueueConnector(adapter.getQueueConnectionFactory(cfConfig),
                                      numRetries, numSessions, connectRetryInterval,
                                      interactRetryInterval, timeoutTime,
                                      allowReceive, clientID, username, password,
                                      adapter, jmsurl);
        }
        else // domain is Topic
        {
            return new TopicConnector(adapter.getTopicConnectionFactory(cfConfig),
                                      numRetries, numSessions, connectRetryInterval,
                                      interactRetryInterval, timeoutTime,
                                      allowReceive, clientID, username, password,
                                      adapter, jmsurl);
        }
    }
}