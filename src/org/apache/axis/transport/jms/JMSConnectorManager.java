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
import org.apache.axis.transport.jms.JMSConstants;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

import javax.jms.Destination;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * JMSConnectorManager manages a pool of connectors and works with the
 * vendor adapters to support the reuse of JMS connections.
 *
 * @author Ray Chun (rchun@sonicsoftware.com)
 */
public class JMSConnectorManager
{
    protected static Log log =
            LogFactory.getLog(JMSConnectorManager.class.getName());

    private static JMSConnectorManager s_instance = new JMSConnectorManager();

    private static HashMap vendorConnectorPools = new HashMap();
    private int DEFAULT_WAIT_FOR_SHUTDOWN = 90000; // 1.5 minutes

    private JMSConnectorManager()
    {
    }

    public static JMSConnectorManager getInstance()
    {
        return s_instance;
    }

    /**
     * Returns the pool of JMSConnectors for a particular vendor
     */
    public ShareableObjectPool getVendorPool(String vendorId)
    {
        return (ShareableObjectPool)vendorConnectorPools.get(vendorId);
    }

    /**
     * Retrieves a JMSConnector that satisfies the provided connector criteria
     */
    public JMSConnector getConnector(HashMap connectorProperties,
                                     HashMap connectionFactoryProperties,
                                     String username,
                                     String password,
                                     JMSVendorAdapter vendorAdapter)
        throws AxisFault
    {
        JMSConnector connector = null;

        try
        {
            // check for a vendor-specific pool, and create if necessary
            ShareableObjectPool vendorConnectors = getVendorPool(vendorAdapter.getVendorId());
            if (vendorConnectors == null)
            {
                synchronized (vendorConnectorPools)
                {
                    vendorConnectors = getVendorPool(vendorAdapter.getVendorId());
                    if (vendorConnectors == null)
                    {
                        vendorConnectors = new ShareableObjectPool();
                        vendorConnectorPools.put(vendorAdapter.getVendorId(), vendorConnectors);
                    }
                }
            }

            // look for a matching JMSConnector among existing connectors
            synchronized (vendorConnectors)
            {
                try
                {

                    connector = JMSConnectorFactory.matchConnector(vendorConnectors.getElements(),
                                                                   connectorProperties,
                                                                   connectionFactoryProperties,
                                                                   username,
                                                                   password,
                                                                   vendorAdapter);
                }
                catch (Exception e) {} // ignore. a new connector will be created if no match is found

                if (connector == null)
                {
                        connector = JMSConnectorFactory.createClientConnector(connectorProperties,
                                                                              connectionFactoryProperties,
                                                                              username,
                                                                              password,
                                                                              vendorAdapter);
                        connector.start();
                }
            }
        }
        catch (Exception e)
        {
            log.error(Messages.getMessage("cannotConnectError"), e);

            if(e instanceof AxisFault)
                throw (AxisFault)e;
            throw new AxisFault("cannotConnect", e);
        }

        return connector;
    }

    /**
     * Closes JMSConnectors in all pools
     */
    void closeAllConnectors()
    {
        if (log.isDebugEnabled()) {
            log.debug("Enter: JMSConnectorManager::closeAllConnectors");
        }

        synchronized (vendorConnectorPools)
        {
            Iterator iter = vendorConnectorPools.values().iterator();
            while (iter.hasNext())
            {
                // close all connectors in the vendor pool
                ShareableObjectPool pool = (ShareableObjectPool)iter.next();
                synchronized (pool)
                {
                    java.util.Iterator connectors = pool.getElements().iterator();
                    while (connectors.hasNext())
                    {
                        JMSConnector conn = (JMSConnector)connectors.next();
                        try
                        {
                            // shutdown automatically decrements the ref count of a connector before closing it
                            // call reserve() to simulate the checkout
                            reserve(conn);
                            closeConnector(conn);
                        }
                        catch (Exception e) {} // ignore. the connector is already being deactivated
                    }
                }
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("Exit: JMSConnectorManager::closeAllConnectors");
        }
    }

    /**
     * Closes JMS connectors that match the specified endpoint address
     */
    void closeMatchingJMSConnectors(HashMap connectorProps, HashMap cfProps,
                                    String username, String password,
                                    JMSVendorAdapter vendorAdapter)
    {
        if (log.isDebugEnabled()) {
            log.debug("Enter: JMSConnectorManager::closeMatchingJMSConnectors");
        }

        try
        {
            String vendorId = vendorAdapter.getVendorId();

            // get the vendor-specific pool of connectors
            ShareableObjectPool vendorConnectors = null;
            synchronized (vendorConnectorPools)
            {
                vendorConnectors = getVendorPool(vendorId);
            }

            // it's possible that there is no pool for that vendor
            if (vendorConnectors == null)
                return;

            synchronized (vendorConnectors)
            {
                // close any matched connectors
                JMSConnector connector = null;
                while ((vendorConnectors.size() > 0) &&
                       (connector = JMSConnectorFactory.matchConnector(vendorConnectors.getElements(),
                                                                       connectorProps,
                                                                       cfProps,
                                                                       username,
                                                                       password,
                                                                       vendorAdapter)) != null)
                {
                    closeConnector(connector);
                }
            }
        }
        catch (Exception e)
        {
            log.warn(Messages.getMessage("failedJMSConnectorShutdown"), e);
        }

        if (log.isDebugEnabled()) {
            log.debug("Exit: JMSConnectorManager::closeMatchingJMSConnectors");
        }
    }

    private void closeConnector(JMSConnector conn)
    {
        conn.stop();
        conn.shutdown();
    }

    /**
     * Adds a JMSConnector to the appropriate vendor pool
     */
    public void addConnectorToPool(JMSConnector conn)
    {
        if (log.isDebugEnabled()) {
            log.debug("Enter: JMSConnectorManager::addConnectorToPool");
        }

        ShareableObjectPool vendorConnectors = null;
        synchronized (vendorConnectorPools)
        {
            String vendorId = conn.getVendorAdapter().getVendorId();
            vendorConnectors = getVendorPool(vendorId);
            // it's possible the pool does not yet exist (if, for example, the connector
            // is created before invoking the call/JMSTransport, as is the case with
            // SimpleJMSListener)
            if (vendorConnectors == null)
            {
                vendorConnectors = new ShareableObjectPool();
                vendorConnectorPools.put(vendorId, vendorConnectors);
            }
        }

        synchronized (vendorConnectors)
        {
            vendorConnectors.addObject(conn);
        }

        if (log.isDebugEnabled()) {
            log.debug("Exit: JMSConnectorManager::addConnectorToPool");
        }
    }

    /**
     * Removes a JMSConnector from the appropriate vendor pool
     */
    public void removeConnectorFromPool(JMSConnector conn)
    {
        if (log.isDebugEnabled()) {
            log.debug("Enter: JMSConnectorManager::removeConnectorFromPool");
        }

        ShareableObjectPool vendorConnectors = null;
        synchronized (vendorConnectorPools)
        {
            vendorConnectors = getVendorPool(conn.getVendorAdapter().getVendorId());
        }
        if (vendorConnectors == null)
            return;

        synchronized (vendorConnectors)
        {
            // first release, to decrement the ref count (it is automatically incremented when
            // the connector is matched)
            vendorConnectors.release(conn);
            vendorConnectors.removeObject(conn);
        }

        if (log.isDebugEnabled()) {
            log.debug("Exit: JMSConnectorManager::removeConnectorFromPool");
        }
    }

    /**
     * Performs a non-exclusive checkout of the JMSConnector
     */
    public void reserve(JMSConnector connector) throws Exception
    {
        ShareableObjectPool pool = null;
        synchronized (vendorConnectorPools)
        {
            pool = getVendorPool(connector.getVendorAdapter().getVendorId());
        }
        if (pool != null)
            pool.reserve(connector);
    }

    /**
     * Performs a non-exclusive checkin of the JMSConnector
     */
    public void release(JMSConnector connector)
    {
        ShareableObjectPool pool = null;
        synchronized (vendorConnectorPools)
        {
            pool = getVendorPool(connector.getVendorAdapter().getVendorId());
        }
        if (pool != null)
            pool.release(connector);
    }

    /**
     * A simple non-blocking pool impl for objects that can be shared.
     * Only a ref count is necessary to prevent collisions at shutdown.
     * Todo: max size, cleanup stale connections
     */
    public class ShareableObjectPool
    {
        // maps object to ref count wrapper
        private java.util.HashMap m_elements;

        // holds objects which should no longer be leased (pending removal)
        private java.util.HashMap m_expiring;

        private int m_numElements = 0;

        public ShareableObjectPool()
        {
            m_elements = new java.util.HashMap();
            m_expiring = new java.util.HashMap();
        }

        /**
         * Adds the object to the pool, if not already added
         */
        public void addObject(Object obj)
        {
            ReferenceCountedObject ref = new ReferenceCountedObject(obj);
            synchronized (m_elements)
            {
                if (!m_elements.containsKey(obj) && !m_expiring.containsKey(obj))
                    m_elements.put(obj, ref);
            }
        }

        /**
         * Removes the object from the pool.  If the object is reserved,
         * waits the specified time before forcibly removing
         * Todo: check expirations with the next request instead of holding up the current request
         */
        public void removeObject(Object obj, long waitTime)
        {
            ReferenceCountedObject ref = null;
            synchronized (m_elements)
            {
                ref = (ReferenceCountedObject)m_elements.get(obj);
                if (ref == null)
                    return;

                m_elements.remove(obj);

                if (ref.count() == 0)
                    return;
                else
                    // mark the object for expiration
                    m_expiring.put(obj, ref);
            }

            // connector is now marked for expiration. wait for the ref count to drop to zero
            long expiration = System.currentTimeMillis() + waitTime;
            while (ref.count() > 0)
            {
                try
                {
                    Thread.sleep(5000);
                }
                catch (InterruptedException e) {} // ignore
                if (System.currentTimeMillis() > expiration)
                    break;
            }

            // also clear from the expiring list
            m_expiring.remove(obj);
        }

        public void removeObject(Object obj)
        {
            removeObject(obj, DEFAULT_WAIT_FOR_SHUTDOWN);
        }

        /**
         * Marks the connector as in use by incrementing the connector's reference count
         */
        public void reserve(Object obj) throws Exception
        {
            synchronized (m_elements)
            {
                if (m_expiring.containsKey(obj))
                    throw new Exception("resourceUnavailable");

                ReferenceCountedObject ref = (ReferenceCountedObject)m_elements.get(obj);
                ref.increment();
            }
        }

        /**
         * Decrements the connector's reference count
         */
        public void release(Object obj)
        {
            synchronized (m_elements)
            {
                ReferenceCountedObject ref = (ReferenceCountedObject)m_elements.get(obj);
                ref.decrement();
            }
        }

        public synchronized java.util.Set getElements()
        {
            return m_elements.keySet();
        }

        public synchronized int size()
        {
            return m_elements.size();
        }

        /**
         * Wrapper to track the use count of an object
         */
        public class ReferenceCountedObject
        {
            private Object m_object;
            private int m_refCount;

            public ReferenceCountedObject(Object obj)
            {
                m_object = obj;
                m_refCount = 0;
            }

            public synchronized void increment()
            {
                m_refCount++;
            }

            public synchronized void decrement()
            {
                if (m_refCount > 0)
                    m_refCount--;
            }

            public synchronized int count()
            {
                return m_refCount;
            }
        }
    }
}