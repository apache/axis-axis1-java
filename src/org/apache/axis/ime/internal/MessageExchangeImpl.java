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
import org.apache.axis.ime.MessageChannel;
import org.apache.axis.ime.MessageExchange;
import org.apache.axis.ime.MessageExchangeConstants;
import org.apache.axis.ime.MessageExchangeContext;
import org.apache.axis.ime.MessageExchangeContextListener;
import org.apache.axis.ime.MessageExchangeCorrelator;
import org.apache.axis.ime.MessageExchangeFaultListener;
import org.apache.axis.ime.MessageExchangeLifecycle;
import org.apache.axis.ime.MessageExchangeReceiveListener;
import org.apache.axis.ime.MessageExchangeStatusListener;
import org.apache.axis.ime.internal.util.uuid.UUIDGenFactory;

/**
 * @author James M Snell (jasnell@us.ibm.com)
 */
public class MessageExchangeImpl
        implements MessageExchange, MessageExchangeLifecycle {

    public static final long WORKER_COUNT = 5;
    public static final long DEFAULT_TIMEOUT = 1000 * 20;

    private MessageExchangeProvider provider;
    private MessageChannel send;
    private MessageChannel receive;
    private MessageExchangeReceiveListener receiveListener;
    private MessageExchangeStatusListener statusListener;
    private MessageExchangeFaultListener faultListener;
    private MessageWorkerGroup workers = new MessageWorkerGroup();
    private boolean listening = false;
    protected Holder holder;

    public MessageExchangeImpl(
            MessageExchangeProvider provider,
            MessageChannel sendChannel,
            MessageChannel receiveChannel) {
        this.send = sendChannel;
        this.receive = receiveChannel;
    }

    /**
     * @see org.apache.axis.ime.MessageExchange#send(MessageContext)
     */
    public MessageExchangeCorrelator send(
            MessageContext context)
            throws AxisFault {
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
        MessageExchangeContext meContext =
                MessageExchangeContext.newInstance(
                        correlator,
                        statusListener,
                        receiveListener,
                        faultListener,
                        context);
        send.put(correlator, meContext);
        return correlator;
    }

    /**
     * @see org.apache.axis.ime.MessageExchange#setMessageExchangeStatusListener(MessageExchangeStatusListener)
     */
    public void setMessageExchangeStatusListener(
            MessageExchangeStatusListener listener)
            throws AxisFault {
        if (listening)
            throw new IllegalStateException(Messages.getMessage("illegalStateException00"));
        this.statusListener = listener;
    }

    /**
     * @see org.apache.axis.ime.MessageExchange#setMessageExchangeReceiveListener(MessageExchangeReceiveListener)
     */
    public void setMessageExchangeReceiveListener(
            MessageExchangeReceiveListener listener)
            throws AxisFault {
        if (listening)
            throw new IllegalStateException(Messages.getMessage("illegalStateException00"));
        this.receiveListener = listener;
    }

    /**
     * @see org.apache.axis.ime.MessageExchange#setMessageExchangeReceiveListener(MessageExchangeReceiveListener)
     */
    public void setMessageExchangeFaultListener(
            MessageExchangeFaultListener listener)
            throws AxisFault {
        if (listening)
            throw new IllegalStateException(Messages.getMessage("illegalStateException00"));
        this.faultListener = listener;
    }

    /**
     * @see org.apache.axis.ime.MessageExchange#cancel(MessageExchangeCorrelator)
     */
    public MessageContext cancel(
            MessageExchangeCorrelator correlator)
            throws AxisFault {
        MessageExchangeContext context = send.cancel(correlator);
        if (context != null)
            return context.getMessageContext();
        else
            return null;
    }

    /**
     * @see org.apache.axis.ime.MessageExchange#getReceiveChannel()
     */
    public MessageChannel getReceiveChannel() {
        return receive;
    }

    /**
     * @see org.apache.axis.ime.MessageExchange#getSendChannel()
     */
    public MessageChannel getSendChannel() {
        return send;
    }


    public MessageContext sendAndReceive(
            MessageContext context)
            throws AxisFault {
        holder = new Holder();
        Listener listener = new Listener(holder);
        this.setMessageExchangeFaultListener(listener);
        this.setMessageExchangeReceiveListener(listener);
        try {
            this.send(context);
            holder.waitForNotify();
        } catch (InterruptedException ie) {
            throw AxisFault.makeFault(ie);
        }
        if (holder.context != null) {
            return holder.context;
        }
        if (holder.exception != null) {
            throw AxisFault.makeFault((Exception) holder.exception);
        }
        return null;
    }

    public MessageContext sendAndReceive(
            MessageContext context,
            long timeout)
            throws AxisFault {
        holder = new Holder();
        Listener listener = new Listener(holder);
        this.setMessageExchangeFaultListener(listener);
        this.setMessageExchangeReceiveListener(listener);
        try {
            this.send(context);
            holder.waitForNotify(timeout);
        } catch (InterruptedException ie) {
            throw AxisFault.makeFault(ie);
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
     * @see org.apache.axis.ime.MessageExchange#startListening()
     */
    public void startListening() {
        if (provider instanceof MessageExchangeProvider1)
            throw new UnsupportedOperationException(Messages.getMessage("unsupportedOperationException00"));
        for (int n = 0; n < WORKER_COUNT; n++) {
            workers.addWorker(receive, new ReceiverListener());
        }
        listening = true;
    }

    /**
     * @see org.apache.axis.ime.MessageExchange#startListening()
     */
    public void startListening(MessageExchangeCorrelator correlator) {
        throw new UnsupportedOperationException(Messages.getMessage("unsupportedOperationException01", "Unsupported For Now"));
    }

    /**
     * @see org.apache.axis.ime.MessageExchange#stopListening()
     */
    public void stopListening() {
        stopListening(false);
    }

    /**
     * @see org.apache.axis.ime.MessageExchange#stopListening(boolean)
     */
    public void stopListening(boolean force) {
        if (provider instanceof MessageExchangeProvider1)
            throw new UnsupportedOperationException(Messages.getMessage("unsupportedOperationException00"));
        if (!force)
            workers.safeShutdown();
        else
            workers.shutdown();
        listening = false;
    }

    private class Holder {
        private MessageExchangeCorrelator correlator;
        private MessageContext context;
        private Throwable exception;

        public synchronized void set(
                MessageExchangeCorrelator correlator,
                MessageContext context) {
            this.correlator = correlator;
            this.context = context;
            notifyAll();
        }

        public synchronized void set(
                MessageExchangeCorrelator correlator,
                Throwable throwable) {
            this.correlator = correlator;
            this.exception = throwable;
            notifyAll();
        }

        public synchronized void waitForNotify()
                throws InterruptedException {
            wait();
            return;
        }

        public synchronized void waitForNotify(long timeout)
                throws InterruptedException {
            wait(timeout);
            return;
        }

    }

    public class Listener
            implements MessageExchangeReceiveListener,
            MessageExchangeFaultListener {

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


    private class ReceiverListener
            implements MessageExchangeContextListener {

        /**
         * @see org.apache.axis.ime.MessageExchangeContextListener#onMessageExchangeContext(MessageExchangeContext)
         */
        public void onMessageExchangeContext(
                MessageExchangeContext context) {

            MessageContext msgContext =
                    context.getMessageContext();
            MessageExchangeCorrelator correlator =
                    context.getMessageExchangeCorrelator();

            try {
                // there should be code here to see if the message
                // contains a fault.  if so, the fault listener should
                // be invoked
                if (msgContext != null &&
                        msgContext.getResponseMessage() != null &&
                        receiveListener != null) {
                    receiveListener.onReceive(correlator, msgContext);
                }
            } catch (Exception exception) {
                if (faultListener != null)
                    faultListener.onFault(
                            correlator, exception);
            }

        }
    }

    /**
     * @see org.apache.axis.ime.MessageExchangeLifecycle#awaitShutdown()
     */
    public void awaitShutdown()
            throws InterruptedException {
        provider.awaitShutdown();
    }

    /**
     * @see org.apache.axis.ime.MessageExchangeLifecycle#awaitShutdown(long)
     */
    public void awaitShutdown(long timeout)
            throws InterruptedException {
        provider.awaitShutdown(timeout);
    }

    /**
     * @see org.apache.axis.ime.MessageExchangeLifecycle#init()
     */
    public void init() {
        provider.init();
    }

    /**
     * @see org.apache.axis.ime.MessageExchangeLifecycle#shutdown()
     */
    public void shutdown() {
        provider.shutdown();
    }

    /**
     * @see org.apache.axis.ime.MessageExchangeLifecycle#shutdown(boolean)
     */
    public void shutdown(boolean force) {
        provider.shutdown(force);
    }

    /**
     * @see org.apache.axis.ime.MessageExchange#receive()
     */
    public MessageContext receive() throws AxisFault {
        throw new UnsupportedOperationException(Messages.getMessage("unsupportedOperationException00"));
    }

    /**
     * @see org.apache.axis.ime.MessageExchange#receive(long)
     */
    public MessageContext receive(long timeout) throws AxisFault {
        throw new UnsupportedOperationException(Messages.getMessage("unsupportedOperationException00"));
    }

}
