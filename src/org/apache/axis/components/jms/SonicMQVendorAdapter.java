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

import javax.jms.ConnectionFactory;
import javax.jms.QueueConnectionFactory;
import javax.jms.TopicConnectionFactory;

/**
 * Defines SonicMQ specific constants for connnection factory creation.
 * Overrides methods in BeanVendorAdapter to fill in MQ classnames
 *
 * @author Jaime Meritt (jmeritt@sonicsoftware.com)
 */
public class SonicMQVendorAdapter extends BeanVendorAdapter
{
    private final static String QCF_CLASS =
        "progress.message.jclient.QueueConnectionFactory";

    private final static String TCF_CLASS =
        "progress.message.jclient.TopicConnectionFactory";

    /**
     * <code>SonicConnectionFactory</code> parameter valid for either domain.  This should
     * be used as a key in the environment map passed into calls to
     * <code>createConnector</code> in <code>JMSConnectorFactory</code>.
     * This is a required property.
     * The value must be a <code>java.lang.String</code>
     * See the SonicMQ documentation for information on this property
     */
    public final static String BROKER_URL              = "brokerURL";

    /**
     * <code>SonicConnectionFactory</code> parameter valid for either domains.  This should
     * be used as a key in the environment map passed into calls to
     * <code>createConnector</code> in <code>JMSConnectorFactory</code>
     * This is a required property for secure brokers.
     * The value must be a <code>java.lang.String</code>
     * See the SonicMQ documentation for information on this property
     */
    public final static String DEFAULT_USERNAME = "defaultUser";

    /**
     * <code>SonicConnectionFactory</code> parameter valid for either domain.  This should
     * be used as a key in the environment map passed into calls to
     * <code>createConnector</code> in <code>JMSConnectorFactory</code>
     * This is a required property for secure brokers.
     * The value must be a <code>java.lang.String</code>
     * See the SonicMQ documentation for information on this property
     */
    public final static String DEFAULT_PASSWORD        = "defaultPassword";
    /**
     * <code>SonicConnectionFactory</code> parameter valid for either domain.  This should
     * be used as a key in the environment map passed into calls to
     * <code>createConnector</code> in <code>JMSConnectorFactory</code>
     * The value must be a <code>java.lang.Long</code>
     * See the SonicMQ documentation for information on this property
     */
    public final static String PING_INTERVAL           = "pingIntervalLong";
    /**
     * <code>SonicConnectionFactory</code> parameter valid for either domain.  This should
     * be used as a key in the environment map passed into calls to
     * <code>createConnector</code> in <code>JMSConnectorFactory</code>
     * The value must be a <code>java.lang.Integer</code>
     * See the SonicMQ documentation for information on this property
     */
    public final static String RECONNECT_INTERVAL      = "reconnectIntervalInteger";
    /**
     * <code>SonicConnectionFactory</code> parameter valid for either domain.  This should
     * be used as a key in the environment map passed into calls to
     * <code>createConnector</code> in <code>JMSConnectorFactory</code>
     * The value must be a <code>java.lang.Integer</code>
     * See the SonicMQ documentation for information on this property
     */
    public final static String RECONNECT_TIMEOUT       = "reconnectTimeoutInteger";
    /**
     * <code>SonicConnectionFactory</code> parameter valid for either domain.  This should
     * be used as a key in the environment map passed into calls to
     * <code>createConnector</code> in <code>JMSConnectorFactory</code>
     * The value must be a <code>java.lang.String</code>
     * See the SonicMQ documentation for information on this property
     */
    public final static String CONNECT_ID              = "connectID";
    /**
     * <code>SonicConnectionFactory</code> parameter valid for either domain.  This should
     * be used as a key in the environment map passed into calls to
     * <code>createConnector</code> in <code>JMSConnectorFactory</code>
     * The value must be a <code>java.lang.String</code>
     * See the SonicMQ documentation for information on this property
     */
    public final static String CONNECTION_URLS         = "connectionURLs";
    /**
     * <code>SonicConnectionFactory</code> parameter valid for either domain.  This should
     * be used as a key in the environment map passed into calls to
     * <code>createConnector</code> in <code>JMSConnectorFactory</code>
     * The value must be a <code>java.lang.Boolean</code>
     * See the SonicMQ documentation for information on this property
     */
    public final static String LOAD_BALANCING          = "loadBalancingBoolean";
    /**
     * <code>SonicConnectionFactory</code> parameter valid for either domain.  This should
     * be used as a key in the environment map passed into calls to
     * <code>createConnector</code> in <code>JMSConnectorFactory</code>
     * The value must be a <code>java.lang.Long</code>
     * See the SonicMQ documentation for information on this property
     */
    public final static String MONITOR_INTERVAL        = "monitorInterval";
    /**
     * <code>SonicConnectionFactory</code> parameter valid for either domain.  This should
     * be used as a key in the environment map passed into calls to
     * <code>createConnector</code> in <code>JMSConnectorFactory</code>
     * The value must be a <code>java.lang.Boolean</code>
     * See the SonicMQ documentation for information on this property
     */
    public final static String PERSISTENT_DELIVERY     = "persistentDeliveryBoolean";
    /**
     * <code>SonicConnectionFactory</code> parameter valid for either domain.  This should
     * be used as a key in the environment map passed into calls to
     * <code>createConnector</code> in <code>JMSConnectorFactory</code>
     * The value must be a <code>java.lang.Boolean</code>
     * See the SonicMQ documentation for information on this property
     */
    public final static String SEQUENTIAL              = "sequentialBoolean";

    /**
     * <code>SonicConnectionFactory</code> parameter valid for the PTP domain.  This should
     * be used as a key in the environment map passed into calls to
     * <code>createConnector</code> in <code>JMSConnectorFactory</code>
     * The value must be a <code>java.lang.Integer</code>
     * See the SonicMQ documentation for information on this property
     */
    public final static String PREFETCH_COUNT          = "prefetchCountInteger";
    /**
     * <code>SonicConnectionFactory</code> parameter valid for the PTP domain.  This should
     * be used as a key in the environment map passed into calls to
     * <code>createConnector</code> in <code>JMSConnectorFactory</code>
     * The value must be a <code>java.lang.Integer</code>
     * See the SonicMQ documentation for information on this property
     */
    public final static String PREFETCH_THRESHOLD      = "prefetchThresholdInteger";


    /**
     * <code>SonicConnectionFactory</code> parameter valid for the PubSub domain.  This should
     * be used as a key in the environment map passed into calls to
     * <code>createConnector</code> in <code>JMSConnectorFactory</code>
     * The value must be a <code>java.lang.Boolean</code>
     * See the SonicMQ documentation for information on this property
     */
    public final static String SELECTOR_AT_BROKER      = "selectorAtBroker";


    public QueueConnectionFactory getQueueConnectionFactory(HashMap cfConfig)
        throws Exception
    {
        cfConfig = (HashMap)cfConfig.clone();
        cfConfig.put(CONNECTION_FACTORY_CLASS, QCF_CLASS);
        return super.getQueueConnectionFactory(cfConfig);
    }

    public TopicConnectionFactory getTopicConnectionFactory(HashMap cfConfig)
        throws Exception
    {
        cfConfig = (HashMap)cfConfig.clone();
        cfConfig.put(CONNECTION_FACTORY_CLASS, TCF_CLASS);
        return super.getTopicConnectionFactory(cfConfig);
    }
}