/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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

package org.apache.axis.ime.internal;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.i18n.Messages;
import org.apache.axis.ime.MessageExchange;
import org.apache.axis.ime.MessageExchangeConstants;
import org.apache.axis.ime.MessageExchangeFaultListener;
import org.apache.axis.ime.MessageExchangeStatusListener;
import org.apache.axis.ime.MessageExchangeCorrelator;
import org.apache.axis.ime.MessageContextListener;
import org.apache.axis.ime.MessageExchangeLifecycle;
import org.apache.axis.ime.internal.util.uuid.UUIDGenFactory;
import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

import java.util.Map;

/**
 * @author James M Snell (jasnell@us.ibm.com)
 */
public class MessageExchangeImpl
        implements MessageExchange, MessageExchangeLifecycle {

    protected static Log log =
        LogFactory.getLog(MessageExchangeImpl.class.getName());

    public static final long NO_TIMEOUT = -1;
    public static final long DEFAULT_TIMEOUT = 1000 * 30;

    private MessageExchangeFaultListener faultListener;
    private MessageExchangeStatusListener statusListener;
    private MessageExchangeProvider provider;
    protected Holder holder;

    public MessageExchangeImpl(
            MessageExchangeProvider provider) {
    }

    /**
     * @see org.apache.axis.ime.MessageExchange#send(MessageContext)
     */
    public MessageExchangeCorrelator send(
            MessageContext context)
            throws AxisFault {
        return send(context,null);
    }

    /**
     * @see org.apache.axis.ime.MessageExchange#send(MessageContext)
     */
    public MessageExchangeCorrelator send(
            MessageContext context,
            MessageContextListener listener)
            throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug("Enter: MessageExchangeImpl::send");
        }
        MessageExchangeCorrelator correlator =
                (MessageExchangeCorrelator) context.getProperty(
                        MessageExchangeConstants.MESSAGE_CORRELATOR_PROPERTY);
        if (correlator == null) {
            correlator = new MessageExchangeCorrelator(
                    UUIDGenFactory.getUUIDGen(null).nextUUID());
            context.setProperty(
                    MessageExchangeConstants.MESSAGE_CORRELATOR_PROPERTY,
                    correlator);
        }
        if (listener != null) {
            provider.processReceive(
                MessageExchangeReceiveContext.newInstance(
                    correlator,
                    listener,
                    faultListener,
                    statusListener));
        }
        provider.processSend(
            MessageExchangeSendContext.newInstance(
                correlator,
                context,
                faultListener,
                statusListener));
        if (log.isDebugEnabled()) {
            log.debug("Exit: MessageExchangeImpl::send");
        }
        return correlator;
    }

    /**
     * @see org.apache.axis.ime.MessageExchange#receive()
     */
    public MessageContext receive() 
            throws AxisFault {
        return receive(null,NO_TIMEOUT);
    }

    /**
     * @see org.apache.axis.ime.MessageExchange#receive(long)
     */
    public MessageContext receive(
            long timeout) 
            throws AxisFault {
        return receive(null,timeout);
    }

    /**
     * @see org.apache.axis.ime.MessageExchange#receive(MessageExchangeCorrelator)
     */
    public MessageContext receive(
            MessageExchangeCorrelator correlator) 
            throws AxisFault {
        return receive(correlator,NO_TIMEOUT);
    }

    /**
     * @see org.apache.axis.ime.MessageExchange#receive(MessageExchangeCorrelator,long)
     */
    public MessageContext receive(
            MessageExchangeCorrelator correlator,
            long timeout) 
            throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug("Enter: MessageExchangeImpl::receive");
        }
        holder = new Holder();
        Listener listener = new Listener(holder);
        setMessageExchangeFaultListener(listener);
        try {
            this.receive(correlator,listener);
            if (timeout != NO_TIMEOUT) 
              holder.waitForNotify(timeout);
            else 
              holder.waitForNotify();
        } catch (InterruptedException ie) {
            throw AxisFault.makeFault(ie);
        }
        if (log.isDebugEnabled()) {
            log.debug("Exit: MessageExchangeImpl::receive");
        }
        if (holder.context != null) {
            return holder.context;
        }
        if (holder.exception != null) {
            throw AxisFault.makeFault((Exception) holder.exception);
        }
        return null;
    }

    /**
     * @see org.apache.axis.ime.MessageExchange#receive(MessageContextListener)
     */
    public void receive(
            MessageContextListener listener) 
            throws AxisFault {
        receive(null,listener);
    }

    /**
     * @see org.apache.axis.ime.MessageExchange#receive(MessageExchangeCorrelator,MessageContextListener)
     */
    public void receive(
            MessageExchangeCorrelator correlator,
            MessageContextListener listener) 
            throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug("Enter: MessageExchangeImpl::receive");
        }
        provider.processReceive(
            MessageExchangeReceiveContext.newInstance(
                correlator,
                listener,
                faultListener,
                statusListener));
        if (log.isDebugEnabled()) {
            log.debug("Exit: MessageExchangeImpl::receive");
        }

    }

    /**
     * @see org.apache.axis.ime.MessageExchange#sendAndReceive(MessageContext)
     */
    public MessageContext sendAndReceive(
            MessageContext context)
            throws AxisFault {
        return sendAndReceive(context,NO_TIMEOUT);
    }

    /**
     * @see org.apache.axis.ime.MessageExchange#sendAndReceive(MessageContext,long)
     */
    public MessageContext sendAndReceive(
            MessageContext context,
            long timeout)
            throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug("Enter: MessageExchangeImpl::sendAndReceive");
        }
        holder = new Holder();
        Listener listener = new Listener(holder);
        setMessageExchangeFaultListener(listener);
        try {
            this.send(context,listener);
            if (timeout != NO_TIMEOUT) 
              holder.waitForNotify(timeout);
            else 
              holder.waitForNotify();
        } catch (InterruptedException ie) {
            throw AxisFault.makeFault(ie);
        }
        if (log.isDebugEnabled()) {
            log.debug("Exit: MessageExchangeImpl::sendAndReceive");
        }
        if (holder.context != null) {
            return holder.context;
        }
        if (holder.exception != null) {
            throw AxisFault.makeFault((Exception) holder.exception);
        }
        return null;
    }

    /**
     * see org.apache.axis.ime.MessageExchange#setMessageExchangeFaultListener(MessageExchangeFaultListener)
     */
    public synchronized void setMessageExchangeFaultListener(
            MessageExchangeFaultListener listener) {
        this.faultListener = listener;
    }

    /**
     * see org.apache.axis.ime.MessageExchange#getMessageExchangeFaultListener()
     */    
    public synchronized MessageExchangeFaultListener getMessageExchangeFaultListener() {
        return this.faultListener;
    }

    /**
     * see org.apache.axis.ime.MessageExchange#setMessageExchangeStatusListener(MessageExchangeStatusListener)
     */    
    public synchronized void setMessageExchangeStatusListener(
            MessageExchangeStatusListener listener) {
        this.statusListener = listener;
    }

    /**
     * see org.apache.axis.ime.MessageExchange#getMessageExchangeStatusListener()
     */        
    public synchronized MessageExchangeStatusListener getMessageExchangeStatusListener() {
        return this.statusListener;
    }

    /**
     * Unsupported for now
     * @see org.apache.axis.ime.MessageExchange@enableFeature(featureId)
     */
    public void enableFeature(String featureId)
            throws AxisFault {
        throw new UnsupportedOperationException(Messages.getMessage("unsupportedOperationException00"));
    }

    /**
     * Unsupported for now
     * @see org.apache.axis.ime.MessageExchange@disableFeature(featureId)
     */
    public void disableFeature(String featureId)
            throws AxisFault {
        throw new UnsupportedOperationException(Messages.getMessage("unsupportedOperationException00"));
    }

    /**
     * Unsupported for now
     * @see org.apache.axis.ime.MessageExchange@isFeatureEnabled(featureId)
     */
    public boolean isFeatureEnabled(String featureId)
            throws AxisFault {
        throw new UnsupportedOperationException(Messages.getMessage("unsupportedOperationException00"));
    }

    /**
     * Unsupported for now
     * @see org.apache.axis.ime.MessageExchange@setProperty(String,Object)
     */
    public void setProperty(
            String propertyId,
            Object propertyValue) {
        throw new UnsupportedOperationException(Messages.getMessage("unsupportedOperationException00"));
    }

    /**
     * Unsupported for now
     * @see org.apache.axis.ime.MessageExchange@getProperty(String)
     */
    public Object getProperty(
            String propertyId) {
        throw new UnsupportedOperationException(Messages.getMessage("unsupportedOperationException00"));
    }

    /**
     * Unsupported for now
     * @see org.apache.axis.ime.MessageExchange@getProperty(String,Object)
     */
    public Object getProperty(
            String propertyId,
            Object defaultValue) {
        throw new UnsupportedOperationException(Messages.getMessage("unsupportedOperationException00"));
    }

    /**
     * Unsupported for now
     * @see org.apache.axis.ime.MessageExchange@getProperties()
     */
    public Map getProperties() {
        throw new UnsupportedOperationException(Messages.getMessage("unsupportedOperationException00"));
    }

    /**
     * Unsupported for now
     * @see org.apache.axis.ime.MessageExchange@setProperties(java.lang.Map)
     */
    public void setProperties(Map properties) {
        throw new UnsupportedOperationException(Messages.getMessage("unsupportedOperationException00"));
    }

    /**
     * Unsupported for now
     * @see org.apache.axis.ime.MessageExchange@clearProperties()
     */
    public void clearProperties() {
        throw new UnsupportedOperationException(Messages.getMessage("unsupportedOperationException00"));
    }
    
    

  // -- Utility Classes --- //

    private class Holder {
        private MessageExchangeCorrelator correlator;
        private MessageContext context;
        private Throwable exception;
        private boolean done = false;

        public synchronized void set(
                MessageExchangeCorrelator correlator,
                MessageContext context) {
            this.correlator = correlator;
            this.context = context;
            done = true;
            notifyAll();
        }

        public synchronized void set(
                MessageExchangeCorrelator correlator,
                Throwable throwable) {
            this.correlator = correlator;
            this.exception = throwable;
            done = true;
            notifyAll();
        }

        public synchronized void waitForNotify()
                throws InterruptedException {
            if (!done) wait();
            return;
        }

        public synchronized void waitForNotify(long timeout)
                throws InterruptedException {
            if (!done) wait(timeout);
            return;
        }

    }

    public class Listener 
            extends MessageContextListener
            implements MessageExchangeFaultListener {

        protected Holder holder;

        public Listener(Holder holder) {
            this.holder = holder;
        }

        /**
         * @see org.apache.axis.ime.MessageExchangeReceiveListener#onReceive(MessageExchangeCorrelator, MessageContext)
         */
        public void onReceive(
                MessageExchangeCorrelator correlator,
                MessageContext context) {
            holder.set(correlator, context);
        }

        /**
         * @see org.apache.axis.ime.MessageExchangeFaultListener#onFault(MessageExchangeCorrelator, Throwable)
         */
        public void onFault(
                MessageExchangeCorrelator correlator,
                Throwable exception) {
            holder.set(correlator, exception);
        }

    }



  // -- MessageExchangeLifecycle Implementation --- //

    /**
     * @see org.apache.axis.ime.MessageExchangeLifecycle#awaitShutdown()
     */
    public void awaitShutdown()
            throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug("Enter: MessageExchangeImpl::awaitShutdown");
        }
        provider.awaitShutdown();
        if (log.isDebugEnabled()) {
            log.debug("Exit: MessageExchangeImpl::awaitShutdown");
        }
    }

    /**
     * @see org.apache.axis.ime.MessageExchangeLifecycle#cleanup()
     */
    public void cleanup()
            throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug("Enter: MessageExchangeImpl::cleanup");
        }
        provider.cleanup();
        if (log.isDebugEnabled()) {
            log.debug("Exit: MessageExchangeImpl::cleanup");
        }
    }

    /**
     * @see org.apache.axis.ime.MessageExchangeLifecycle#awaitShutdown(long)
     */
    public void awaitShutdown(long timeout)
            throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug("Enter: MessageExchangeImpl::awaitShutdown");
        }
        provider.awaitShutdown(timeout);
        if (log.isDebugEnabled()) {
            log.debug("Exit: MessageExchangeImpl::awaitShutdown");
        }
    }

    /**
     * @see org.apache.axis.ime.MessageExchangeLifecycle#init()
     */
    public void init() {
        if (log.isDebugEnabled()) {
            log.debug("Enter: MessageExchangeImpl::init");
        }
        provider.init();
        if (log.isDebugEnabled()) {
            log.debug("Exit: MessageExchangeImpl::init");
        }
    }

    /**
     * @see org.apache.axis.ime.MessageExchangeLifecycle#shutdown()
     */
    public void shutdown() {
        if (log.isDebugEnabled()) {
            log.debug("Enter: MessageExchangeImpl::shutdown");
        }
        provider.shutdown();
        if (log.isDebugEnabled()) {
            log.debug("Exit: MessageExchangeImpl::shutdown");
        }        
    }

    /**
     * @see org.apache.axis.ime.MessageExchangeLifecycle#shutdown(boolean)
     */
    public void shutdown(boolean force) {
        if (log.isDebugEnabled()) {
            log.debug("Enter: MessageExchangeImpl::shutdown");
        }
        provider.shutdown(force);
        if (log.isDebugEnabled()) {
            log.debug("Exit: MessageExchangeImpl::shutdown");
        }
    }

}
