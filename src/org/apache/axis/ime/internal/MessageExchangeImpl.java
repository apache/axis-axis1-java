/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.axis.ime.internal;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.axis.ime.MessageExchange;
import org.apache.axis.ime.MessageExchangeConstants;
import org.apache.axis.ime.MessageExchangeCorrelator;
import org.apache.axis.ime.MessageExchangeEvent;
import org.apache.axis.ime.MessageExchangeEventListener;
import org.apache.axis.ime.MessageExchangeLifecycle;
import org.apache.axis.ime.event.MessageFaultEvent;
import org.apache.axis.ime.event.MessageReceiveEvent;
import org.apache.commons.logging.Log;

import java.util.Hashtable;

/**
 * @author James M Snell (jasnell@us.ibm.com)
 * @author Ray Chun (rchun@sonicsoftware.com)
 */
public class MessageExchangeImpl
        implements MessageExchange, MessageExchangeLifecycle {

    protected static Log log =
        LogFactory.getLog(MessageExchangeImpl.class.getName());

    public static final long NO_TIMEOUT = -1;
    public static final long DEFAULT_TIMEOUT = 1000 * 30;

    private MessageExchangeEventListener eventListener;
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
            MessageExchangeEventListener listener)
            throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug("Enter: MessageExchangeImpl::send");
        }
        MessageExchangeCorrelator correlator =
                (MessageExchangeCorrelator) context.getProperty(
                        MessageExchangeConstants.MESSAGE_CORRELATOR_PROPERTY);
        if (correlator == null) {
            correlator = new SimpleMessageExchangeCorrelator(
                    UUIDGenFactory.getUUIDGen().nextUUID());
            context.setProperty(
                    MessageExchangeConstants.MESSAGE_CORRELATOR_PROPERTY,
                    correlator);
        }
        MessageExchangeSendContext sendContext = 
            MessageExchangeSendContext.newInstance(
                correlator,
                context,
                listener);     
        if (listener != null) {
            provider.processReceive(sendContext);
        }
        provider.processSend(sendContext);
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
        MessageExchangeEventListener oldListener = 
          getMessageExchangeEventListener();
        Listener listener = new Listener(holder);
        setMessageExchangeEventListener(listener);
        try {
            this.receive(correlator,listener);
            if (timeout != NO_TIMEOUT) 
              holder.waitForNotify(timeout);
            else 
              holder.waitForNotify();
        } catch (InterruptedException ie) {
            throw AxisFault.makeFault(ie);
        } finally {
          setMessageExchangeEventListener(oldListener);
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
            MessageExchangeEventListener listener) 
            throws AxisFault {
        receive(null,listener);
    }

    /**
     * @see org.apache.axis.ime.MessageExchange#receive(MessageExchangeCorrelator,MessageContextListener)
     */
    public void receive(
            MessageExchangeCorrelator correlator,
            MessageExchangeEventListener listener) 
            throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug("Enter: MessageExchangeImpl::receive");
        }
        provider.processReceive(
            MessageExchangeReceiveContext.newInstance(
                correlator,
                listener));
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
        MessageExchangeEventListener oldListener = 
          getMessageExchangeEventListener();
        Listener listener = new Listener(holder);
        setMessageExchangeEventListener(listener);
        try {
            this.send(context,listener);
            if (timeout != NO_TIMEOUT) 
              holder.waitForNotify(timeout);
            else 
              holder.waitForNotify();
        } catch (InterruptedException ie) {
            throw AxisFault.makeFault(ie);
        } finally {
          setMessageExchangeEventListener(oldListener);
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
    public synchronized void setMessageExchangeEventListener(
            MessageExchangeEventListener listener) {
        this.eventListener = listener;
    }

    /**
     * see org.apache.axis.ime.MessageExchange#getMessageExchangeStatusListener()
     */        
    public synchronized MessageExchangeEventListener getMessageExchangeEventListener() {
        return this.eventListener;
    }

    /**
     * Unsupported for now
     * @see org.apache.axis.ime.MessageExchange@setOption(String,Object)
     */
    public void setOption(
            String OptionId,
            Object OptionValue) {
        provider.setOption(OptionId, OptionValue);
    }

    /**
     * Unsupported for now
     * @see org.apache.axis.ime.MessageExchange@getOption(String)
     */
    public Object getOption(
            String OptionId) {
        return provider.getOption(OptionId);
    }

    /**
     * Unsupported for now
     * @see org.apache.axis.ime.MessageExchange@getOption(String,Object)
     */
    public Object getOption(
            String OptionId,
            Object defaultValue) {
        return provider.getOption(OptionId, defaultValue);
    }

    /**
     * Unsupported for now
     * @see org.apache.axis.ime.MessageExchange@getProperties()
     */
    public Hashtable getOptions() {
        return provider.getOptions();
    }

    /**
     * Unsupported for now
     * @see org.apache.axis.ime.MessageExchange@setProperties(java.lang.Hashtable)
     */
    public void setOptions(Hashtable options) {
        provider.setOptions(options);
    }

    /**
     * Unsupported for now
     * @see org.apache.axis.ime.MessageExchange@clearProperties()
     */
    public void clearOptions() {
        provider.clearOptions();
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
            implements MessageExchangeEventListener {

        protected Holder holder;

        public Listener(Holder holder) {
            this.holder = holder;
        }

        /**
         * @see org.apache.axis.ime.MessageExchangeReceiveListener#onReceive(MessageExchangeCorrelator, MessageContext)
         */
        public void onEvent(
                MessageExchangeEvent event) {
            if (event instanceof MessageReceiveEvent) {
                MessageReceiveEvent receiveEvent = (MessageReceiveEvent)event;
                holder.set(
                        receiveEvent.getMessageExchangeCorrelator(), 
                        receiveEvent.getMessageContext());
            }
            else if (event instanceof MessageFaultEvent) {
                MessageFaultEvent faultEvent = (MessageFaultEvent)event;
                holder.set(faultEvent.getMessageExchangeCorrelator(), faultEvent.getException());
        }
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
