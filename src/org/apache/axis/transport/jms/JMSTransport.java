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

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.client.Call;
import org.apache.axis.client.Transport;
import org.apache.axis.components.jms.JMSVendorAdapter;
import org.apache.axis.components.jms.JMSVendorAdapterFactory;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

import java.util.HashMap;

/**
 * JMSTransport is the JMS-specific implemenation of org.apache.axis.client.Transport.
 *   It implements the setupMessageContextImpl() function to set JMS-specific message
 *   context fields and transport chains.
 *
 * There are two
 *   Connector and connection factory
 *   properties are passed in during instantiation and are in turn passed through
 *   when creating a connector.
 *
 * @author Jaime Meritt  (jmeritt@sonicsoftware.com)
 * @author Richard Chung (rchung@sonicsoftware.com)
 * @author Dave Chappell (chappell@sonicsoftware.com)
 * @author Ray Chun (rchun@sonicsoftware.com)
 */
public class JMSTransport extends Transport
{
    protected static Log log =
            LogFactory.getLog(JMSTransport.class.getName());

    private static HashMap vendorConnectorPools = new HashMap();

    private HashMap defaultConnectorProps;
    private HashMap defaultConnectionFactoryProps;

    static
    {
        // add a shutdown hook to close JMS connections
        Runtime.getRuntime().addShutdownHook(
            new Thread()
            {
                public void run()
                {
                    JMSTransport.closeAllConnectors();
                }
            }
        );
    }

    public JMSTransport()
    {
        transportName = "JMSTransport";
    }

    // this cons is provided for clients that instantiate the JMSTransport directly
    public JMSTransport(HashMap connectorProps,
                        HashMap connectionFactoryProps)
    {
        this();
        defaultConnectorProps = connectorProps;
        defaultConnectionFactoryProps = connectionFactoryProps;
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
        if (log.isDebugEnabled()) {
            log.debug("Enter: JMSTransport::setupMessageContextImpl");
        }

        JMSConnector connector = null;
        HashMap connectorProperties = null;
        HashMap connectionFactoryProperties = null;

        JMSVendorAdapter vendorAdapter = null;
        JMSURLHelper jmsurl = null;

        // a security context is required to create/use JMSConnectors
        String username = message.getUsername();
        String password = message.getPassword();

        // the presence of an endpoint address indicates whether the client application
        //  is instantiating the JMSTransport directly (deprecated) or indirectly via JMS URL
        String endpointAddr = message.getTargetEndpointAddress();
        if (endpointAddr != null)
        {
            try
            {
                // performs minimal validation ('jms:/destination?...')
                jmsurl = new JMSURLHelper(new java.net.URL(endpointAddr));

                // lookup the appropriate vendor adapter
                String vendorId = jmsurl.getVendor();
                if (vendorId == null)
                    vendorId = JMSConstants.JNDI_VENDOR_ID;

                if (log.isDebugEnabled())
                    log.debug("JMSTransport.setupMessageContextImpl(): endpt=" + endpointAddr +
                              ", vendor=" + vendorId);

                vendorAdapter = JMSVendorAdapterFactory.getJMSVendorAdapter(vendorId);
                if (vendorAdapter == null)
                {
                    throw new AxisFault("cannotLoadAdapterClass:" + vendorId);
                }

                // populate the connector and connection factory properties tables
                connectorProperties = vendorAdapter.getJMSConnectorProperties(jmsurl);
                connectionFactoryProperties = vendorAdapter.getJMSConnectionFactoryProperties(jmsurl);
            }
            catch (java.net.MalformedURLException e)
            {
                log.error(Messages.getMessage("malformedURLException00"), e);
                throw new AxisFault(Messages.getMessage("malformedURLException00"), e);
            }
        }
        else
        {
            // the JMSTransport was instantiated directly, use the default adapter
            vendorAdapter = JMSVendorAdapterFactory.getJMSVendorAdapter();
            if (vendorAdapter == null)
            {
                throw new AxisFault("cannotLoadAdapterClass");
            }

            // use the properties passed in to the constructor
            connectorProperties = defaultConnectorProps;
            connectionFactoryProperties = defaultConnectionFactoryProps;
        }

        try
        {
            connector = JMSConnectorManager.getInstance().getConnector(connectorProperties, connectionFactoryProperties,
                                                           username, password, vendorAdapter);
        }
        catch (Exception e)
        {
            log.error(Messages.getMessage("cannotConnectError"), e);

            if(e instanceof AxisFault)
                throw (AxisFault)e;
            throw new AxisFault("cannotConnect", e);
        }

        // store these in the context for later use
        context.setProperty(JMSConstants.CONNECTOR, connector);
        context.setProperty(JMSConstants.VENDOR_ADAPTER, vendorAdapter);

        // vendors may populate the message context
        vendorAdapter.setupMessageContext(context, message, jmsurl);

        if (log.isDebugEnabled()) {
            log.debug("Exit: JMSTransport::setupMessageContextImpl");
        }
    }

    /**
     * Shuts down the connectors managed by this JMSTransport.
     */
    public void shutdown()
    {
        if (log.isDebugEnabled()) {
            log.debug("Enter: JMSTransport::shutdown");
        }

        closeAllConnectors();

        if (log.isDebugEnabled()) {
            log.debug("Exit: JMSTransport::shutdown");
        }
    }

    /**
     * Closes all JMS connectors
     */
    public static void closeAllConnectors()
    {
        if (log.isDebugEnabled()) {
            log.debug("Enter: JMSTransport::closeAllConnectors");
        }

        JMSConnectorManager.getInstance().closeAllConnectors();

        if (log.isDebugEnabled()) {
            log.debug("Exit: JMSTransport::closeAllConnectors");
        }
    }

    /**
     * Closes JMS connectors that match the specified endpoint address
     *
     * @param endpointAddr the JMS endpoint address
     * @param username
     * @param password
     */
    public static void closeMatchingJMSConnectors(String endpointAddr, String username, String password)
    {
        if (log.isDebugEnabled()) {
            log.debug("Enter: JMSTransport::closeMatchingJMSConnectors");
        }

        try
        {
            JMSURLHelper jmsurl = new JMSURLHelper(new java.net.URL(endpointAddr));
            String vendorId = jmsurl.getVendor();

            JMSVendorAdapter vendorAdapter = null;
            if (vendorId == null)
                vendorId = JMSConstants.JNDI_VENDOR_ID;
            vendorAdapter = JMSVendorAdapterFactory.getJMSVendorAdapter(vendorId);

            // the vendor adapter may not exist
            if (vendorAdapter == null)
                return;

            // determine the set of properties to be used for matching the connection
            HashMap connectorProps = vendorAdapter.getJMSConnectorProperties(jmsurl);
            HashMap cfProps = vendorAdapter.getJMSConnectionFactoryProperties(jmsurl);

            JMSConnectorManager.getInstance().closeMatchingJMSConnectors(connectorProps, cfProps,
                                                                         username, password,
                                                                         vendorAdapter);
        }
        catch (java.net.MalformedURLException e)
        {
            log.warn(Messages.getMessage("malformedURLException00"), e);
        }

        if (log.isDebugEnabled()) {
            log.debug("Exit: JMSTransport::closeMatchingJMSConnectors");
        }
    }
}