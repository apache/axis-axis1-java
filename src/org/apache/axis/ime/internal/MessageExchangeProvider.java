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
import org.apache.axis.Handler;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.components.threadpool.ThreadPool;
import org.apache.axis.i18n.Messages;
import org.apache.axis.ime.MessageExchange;
import org.apache.axis.ime.MessageExchangeEventListener;
import org.apache.axis.ime.MessageExchangeFactory;
import org.apache.axis.ime.event.MessageSendEvent;
import org.apache.axis.ime.internal.util.KeyedBuffer;
import org.apache.axis.ime.internal.util.NonPersistentKeyedBuffer;
import org.apache.commons.logging.Log;

import java.util.Hashtable;

/**
 * @author James M Snell (jasnell@us.ibm.com)
 * @author Ray Chun (rchun@sonicsoftware.com)
 */
public abstract class MessageExchangeProvider
        implements MessageExchangeFactory {

    protected static Log log =
        LogFactory.getLog(MessageExchangeProvider.class.getName());

    public static final long SELECT_TIMEOUT = 1000 * 30;
    public static final long DEFAULT_THREAD_COUNT = 5;

    protected final ThreadPool WORKERS = new ThreadPool();
    protected final KeyedBuffer SEND = new NonPersistentKeyedBuffer(WORKERS);
    protected final KeyedBuffer RECEIVE = new NonPersistentKeyedBuffer(WORKERS);
    protected final KeyedBuffer RECEIVE_REQUESTS = new NonPersistentKeyedBuffer(WORKERS);
    protected Handler sendHandler = null;
    protected Handler receiveHandler = null; 

    protected boolean initialized = false;

    public Handler getSendHandler() {
      return sendHandler;
    }
    
    public Handler getReceiveHandler() {
      return receiveHandler;
    }

    public void setSendHandler(Handler handler) {
      this.sendHandler = handler;
    }
    
    public void setReceiveHandler(Handler handler) {
      this.receiveHandler = handler;
    }

    protected abstract MessageExchangeEventListener getMessageExchangeEventListener();

    protected abstract ReceivedMessageDispatchPolicy getReceivedMessageDispatchPolicy();

    public MessageExchange createMessageExchange()
            throws AxisFault {
        return new MessageExchangeImpl(this);
    }

    public MessageExchange createMessageExchange(
            Hashtable options)
            throws AxisFault {
      MessageExchange msgex = new MessageExchangeImpl(this);
      msgex.setOptions(options);
      return msgex;
    }
            
    public void cleanup()
            throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug("Enter: MessageExchangeProvider::cleanup");
        }
        WORKERS.cleanup();
        if (log.isDebugEnabled()) {
            log.debug("Exit: MessageExchangeProvider::cleanup");
        }
    }  

    public void init() {
        init(DEFAULT_THREAD_COUNT);
    }

    public void init(long THREAD_COUNT) {
        if (log.isDebugEnabled()) {
            log.debug("Enter: MessageExchangeProvider::init");
        }
        if (initialized)
            throw new IllegalStateException(Messages.getMessage("illegalStateException00"));
        for (int n = 0; n < THREAD_COUNT; n++) {
            WORKERS.addWorker(new MessageSender(WORKERS, SEND, getMessageExchangeEventListener(), getSendHandler()));
            WORKERS.addWorker(new MessageReceiver(WORKERS, RECEIVE, getReceivedMessageDispatchPolicy(), getReceiveHandler()));
        }
        initialized = true;
        if (log.isDebugEnabled()) {
            log.debug("Exit: MessageExchangeProvider::init");
        }
    }
    
    public void processReceive(
            MessageExchangeReceiveContext context) {
        if (log.isDebugEnabled()) {
            log.debug("Enter: MessageExchangeProvider::processReceive");
        }
        if (context.getMessageExchangeCorrelator() != null) {
          RECEIVE_REQUESTS.put(
            context.getMessageExchangeCorrelator(),
            context);
        } else {
          RECEIVE_REQUESTS.put(
            SimpleMessageExchangeCorrelator.NULL_CORRELATOR, context);
        }
        if (log.isDebugEnabled()) {
            log.debug("Exit: MessageExchangeProvider::processReceive");
        }
    }
    
    public void processSend(
            MessageExchangeSendContext context) {
        if (log.isDebugEnabled()) {
            log.debug("Enter: MessageExchangeProvider::processSend");
        }
        SEND.put(
            context.getMessageExchangeCorrelator(),
            context);
        if (log.isDebugEnabled()) {
            log.debug("Exit: MessageExchangeProvider::processSend");
        }
    }

    public void shutdown() {
        shutdown(false);
    }

    public void shutdown(boolean force) {
        if (log.isDebugEnabled()) {
            log.debug("Enter: MessageExchangeProvider::shutdown");
        }
        if (!force) {
            WORKERS.safeShutdown();
        } else {
            WORKERS.shutdown();
        }
        if (log.isDebugEnabled()) {
            log.debug("Exit: MessageExchangeProvider::shutdown");
        }
    }

    public void awaitShutdown()
            throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug("Enter: MessageExchangeProvider::awaitShutdown");
        }
        WORKERS.awaitShutdown();
        if (log.isDebugEnabled()) {
            log.debug("Exit: MessageExchangeProvider::awaitShutdown");
        }
    }

    public void awaitShutdown(long shutdown)
            throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug("Enter: MessageExchangeProvider::awaitShutdown");
        }
        WORKERS.awaitShutdown(shutdown);
        if (log.isDebugEnabled()) {
            log.debug("Exit: MessageExchangeProvider::awaitShutdown");
        }
    }

    /**
     * Unsupported for now
     * @see org.apache.axis.ime.MessageExchange@setProperty(String,Object)
     */
    public void setOption(
            String propertyId,
            Object propertyValue) {
        throw new UnsupportedOperationException(Messages.getMessage("unsupportedOperationException00"));
    }

    /**
     * Unsupported for now
     * @see org.apache.axis.ime.MessageExchange@getProperty(String)
     */
    public Object getOption(
            String propertyId) {
        throw new UnsupportedOperationException(Messages.getMessage("unsupportedOperationException00"));
    }

    /**
     * Unsupported for now
     * @see org.apache.axis.ime.MessageExchange@getProperty(String,Object)
     */
    public Object getOption(
            String propertyId,
            Object defaultValue) {
        throw new UnsupportedOperationException(Messages.getMessage("unsupportedOperationException00"));
    }

    /**
     * Unsupported for now
     * @see org.apache.axis.ime.MessageExchange@getProperties()
     */
    public Hashtable getOptions() {
        throw new UnsupportedOperationException(Messages.getMessage("unsupportedOperationException00"));
    }

    /**
     * Unsupported for now
     * @see org.apache.axis.ime.MessageExchange@setProperties(java.langHashtable)
     */
    public void setOptions(Hashtable properties) {
        throw new UnsupportedOperationException(Messages.getMessage("unsupportedOperationException00"));
    }

    /**
     * Unsupported for now
     * @see org.apache.axis.ime.MessageExchange@clearProperties()
     */
    public void clearOptions() {
        throw new UnsupportedOperationException(Messages.getMessage("unsupportedOperationException00"));
    }

  // -- Worker Classes --- //
    public static class MessageReceiver 
            implements Runnable {
        
        protected static Log log =
            LogFactory.getLog(MessageReceiver.class.getName());
        
        protected ThreadPool pool;
        protected KeyedBuffer channel;
        protected ReceivedMessageDispatchPolicy policy;
        protected Handler handler;
    
        protected MessageReceiver(
                ThreadPool pool,
                KeyedBuffer channel,
                ReceivedMessageDispatchPolicy policy,
                Handler handler) {
            this.pool = pool;
            this.channel = channel;
            this.policy = policy;
            this.handler = handler;
        }
    
        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            if (log.isDebugEnabled()) {
                log.debug("Enter: MessageExchangeProvider.MessageReceiver::run");
            }
            try {
                while (!pool.isShuttingDown()) {
                    MessageExchangeSendContext context = (MessageExchangeSendContext)channel.select(SELECT_TIMEOUT);
                    if (context != null) {
                      if (handler != null)
                        handler.invoke(context.getMessageContext());
                      policy.dispatch(context);
                    }
                }
            } catch (Throwable t) {
                log.error(Messages.getMessage("fault00"), t);
            } finally {
                pool.workerDone(this,true);
                if (log.isDebugEnabled()) {
                    log.debug("Exit: MessageExchangeProvider.MesageReceiver::run");
                }
            }
        }
    
    }



    public static class MessageSender 
            implements Runnable {

        protected static Log log =
            LogFactory.getLog(MessageReceiver.class.getName());
    
        protected ThreadPool pool;
        protected KeyedBuffer channel;
        protected MessageExchangeEventListener listener;
        protected Handler handler;
    
        protected MessageSender(
                ThreadPool pool,
                KeyedBuffer channel,
                MessageExchangeEventListener listener,
                Handler handler) {
            this.pool = pool;
            this.channel = channel;
            this.listener = listener;
            this.handler = handler;
        }
        
        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            if (log.isDebugEnabled()) {
                log.debug("Enter: MessageExchangeProvider.MessageSender::run");
            }
            try {
                while (!pool.isShuttingDown()) {
                    MessageExchangeSendContext context = (MessageExchangeSendContext)channel.select(SELECT_TIMEOUT);
                    if (context != null) {
                      if (handler != null)
                        handler.invoke(context.getMessageContext());
                      
                      MessageSendEvent sendEvent = new MessageSendEvent(
                            context.getMessageExchangeCorrelator(), 
                            context,
                            context.getMessageContext());
                      listener.onEvent(sendEvent);
                    }
                }
            } catch (Throwable t) {
                log.error(Messages.getMessage("fault00"), t);
            } finally {
                pool.workerDone(this,true);
                if (log.isDebugEnabled()) {
                    log.debug("Exit: MessageExchangeProvider.MessageSender::run");
                }
            }
        }
    
    }

}
