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

import org.apache.axis.i18n.Messages;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.ime.MessageExchange;
import org.apache.axis.ime.MessageContextListener;
import org.apache.axis.ime.MessageExchangeCorrelator;
import org.apache.axis.ime.MessageExchangeFactory;
import org.apache.axis.ime.MessageExchangeFaultListener;
import org.apache.axis.ime.internal.util.WorkerPool;
import org.apache.axis.ime.internal.util.KeyedBuffer;
import org.apache.axis.ime.internal.util.NonPersistentKeyedBuffer;

import java.util.Map;

/**
 * @author James M Snell (jasnell@us.ibm.com)
 */
public abstract class MessageExchangeProvider
        implements MessageExchangeFactory {

    public static final long SELECT_TIMEOUT = 1000 * 30;
    public static final long DEFAULT_THREAD_COUNT = 5;

    protected final WorkerPool WORKERS = new WorkerPool();
    protected final KeyedBuffer SEND = new NonPersistentKeyedBuffer(WORKERS);
    protected final KeyedBuffer RECEIVE = new NonPersistentKeyedBuffer(WORKERS);
    protected final KeyedBuffer RECEIVE_REQUESTS = new NonPersistentKeyedBuffer(WORKERS);

    protected boolean initialized = false;

    protected abstract MessageExchangeSendListener getMessageExchangeSendListener();

    protected abstract ReceivedMessageDispatchPolicy getReceivedMessageDispatchPolicy();

    public MessageExchange createMessageExchange()
            throws AxisFault {
        return new MessageExchangeImpl(this);
    }

    /**
     * Unsupported for now
     */
    public MessageExchange createMessageExchange(
            Map properties,
            String[] enabledFeatures)
            throws AxisFault {
        throw AxisFault.makeFault(
            new UnsupportedOperationException(
                Messages.getMessage("unsupportedOperationException00")));
    }
            
    public void cleanup()
            throws InterruptedException {
        WORKERS.cleanup();
    }  

    public void init() {
        init(DEFAULT_THREAD_COUNT);
    }

    public void init(long THREAD_COUNT) {
        if (initialized)
            throw new IllegalStateException(Messages.getMessage("illegalStateException00"));
        for (int n = 0; n < THREAD_COUNT; n++) {
            WORKERS.addWorker(new MessageSender(WORKERS, SEND, getMessageExchangeSendListener()));
            WORKERS.addWorker(new MessageReceiver(WORKERS, RECEIVE, getReceivedMessageDispatchPolicy()));
        }
        initialized = true;
    }
    
    public void processReceive(
            MessageExchangeReceiveContext context) {
        RECEIVE_REQUESTS.put(
            context.getMessageExchangeCorrelator(),
            context);
    }
    
    public void processSend(
            MessageExchangeSendContext context) {
        SEND.put(
            context.getMessageExchangeCorrelator(),
            context);
    }

    public void shutdown() {
        shutdown(false);
    }

    public void shutdown(boolean force) {
        if (!force) {
            WORKERS.safeShutdown();
        } else {
            WORKERS.shutdown();
        }
    }

    public void awaitShutdown()
            throws InterruptedException {
        WORKERS.awaitShutdown();
    }

    public void awaitShutdown(long shutdown)
            throws InterruptedException {
        WORKERS.awaitShutdown(shutdown);
    }



  // -- Worker Classes --- //
    public static class MessageReceiver 
            implements Runnable {
        
        protected WorkerPool pool;
        protected KeyedBuffer channel;
        protected ReceivedMessageDispatchPolicy policy;
    
        protected MessageReceiver(
                WorkerPool pool,
                KeyedBuffer channel,
                ReceivedMessageDispatchPolicy policy) {
            this.pool = pool;
            this.channel = channel;
            this.policy = policy;
        }
    
        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            try {
                while (!pool.isShuttingDown()) {
                    MessageExchangeSendContext context = (MessageExchangeSendContext)channel.select(SELECT_TIMEOUT);
                    policy.dispatch(context);
                }
            } catch (Throwable t) {
                // kill the thread if any type of exception occurs.
                // don't worry, we'll create another one to replace it
                // if we're not currently in the process of shutting down.
                // once I get the logging function plugged in, we'll
                // log whatever errors do occur
            } finally {
                pool.workerDone(this);
            }
        }
    
    }



    public static class MessageSender 
            implements Runnable {
    
        protected WorkerPool pool;
        protected KeyedBuffer channel;
        protected MessageExchangeSendListener listener;
    
        protected MessageSender(
                WorkerPool pool,
                KeyedBuffer channel,
                MessageExchangeSendListener listener) {
            this.pool = pool;
            this.channel = channel;
            this.listener = listener;
        }
        
        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            try {
                while (!pool.isShuttingDown()) {
                    MessageExchangeSendContext context = (MessageExchangeSendContext)channel.select(SELECT_TIMEOUT);
                    if (context != null)
                        listener.onSend(context);
                }
            } catch (Throwable t) {
                // kill the thread if any type of exception occurs.
                // don't worry, we'll create another one to replace it
                // if we're not currently in the process of shutting down.
                // once I get the logging function plugged in, we'll
                // log whatever errors do occur
            } finally {
                pool.workerDone(this);
            }
        }
    
    }

}
