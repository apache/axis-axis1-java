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
import org.apache.axis.components.jms.JMSVendorAdapterFactory;

import java.util.HashMap;

/**
 * JMSConnectorFactory is a factory class for creating JMSConnectors. It can
 *   create both client connectors and server connectors. A server connector
 *   is configured to allow asynchronous message receipt, while a client
 *   connector is not.
 *
 * @author Jaime Meritt  (jmeritt@sonicsoftware.com)
 * @author Richard Chung (rchung@sonicsoftware.com)
 * @author Dave Chappell (chappell@sonicsoftware.com)
 */
public class JMSConnectorFactory
{
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
                                                     String password)
        throws Exception
    {
        return createConnector(connectorConfig, cfConfig, true,
                               username, password);
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
                                                     String password)
        throws Exception
    {
        return createConnector(connectorConfig, cfConfig, false,
                               username, password);
    }

    private static JMSConnector createConnector(HashMap connectorConfig,
                                                HashMap cfConfig,
                                                boolean allowReceive,
                                                String username,
                                                String password)
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

        if(cfConfig == null)
            throw new IllegalArgumentException("noCfConfig");

        JMSVendorAdapter adapter = JMSVendorAdapterFactory.getJMSVendorAdapter();
        if(domain.equals(JMSConstants.DOMAIN_QUEUE))
        {
            return new QueueConnector(adapter.getQueueConnectionFactory(cfConfig),
                                      numRetries, numSessions, connectRetryInterval,
                                      interactRetryInterval, timeoutTime,
                                      allowReceive, clientID, username, password,
                                      adapter);
        }
        else // domain is Topic
        {
            return new TopicConnector(adapter.getTopicConnectionFactory(cfConfig),
                                      numRetries, numSessions, connectRetryInterval,
                                      interactRetryInterval, timeoutTime,
                                      allowReceive, clientID, username, password,
                                      adapter);
        }
    }
}