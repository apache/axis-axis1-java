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

package org.apache.axis.ime.internal.util.handler;

import java.util.Hashtable;

import org.apache.axis.Handler;
import org.apache.axis.TargetedChain;
import org.apache.axis.MessageContext;
import org.apache.axis.ime.MessageExchangeCorrelator;
import org.apache.axis.ime.MessageExchangeEventListener;
import org.apache.axis.ime.MessageExchangeEvent;
import org.apache.axis.ime.event.MessageFaultEvent;
import org.apache.axis.ime.event.MessageSendEvent;
import org.apache.axis.ime.internal.MessageExchangeProvider;
import org.apache.axis.ime.internal.MessageExchangeSendContext;
import org.apache.axis.ime.internal.ReceivedMessageDispatchPolicy;
import org.apache.axis.ime.internal.FirstComeFirstServeDispatchPolicy;
import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

/**
 * Used to wrap synchronous handlers (e.g. Axis 1.0 transports)
 * 
 * @author James M Snell (jasnell@us.ibm.com)
 */
public class HandlerMessageExchange
        extends MessageExchangeProvider {

    protected static Log log =
        LogFactory.getLog(HandlerMessageExchange.class.getName());

    private Handler handler;

    public HandlerMessageExchange(Handler handler) {
        this.handler = handler;
    }

    /**
     * @see org.apache.axis.ime.internal.MessageExchangeProvider1#createSendMessageContextListener()
     */
    protected MessageExchangeEventListener getMessageExchangeEventListener() {
        return new Listener(handler);
    }

    protected ReceivedMessageDispatchPolicy getReceivedMessageDispatchPolicy() {
        return new FirstComeFirstServeDispatchPolicy(
            RECEIVE, 
            RECEIVE_REQUESTS);
    }

    public Handler getSendHandler() {
      Handler h = super.getSendHandler();
      if (h == null && handler instanceof TargetedChain) {
        h = ((TargetedChain)handler).getRequestHandler();
      }
      return h;
    }
    
    public Handler getReceiveHandler() {
      Handler h = super.getReceiveHandler();
      if (h == null && handler instanceof TargetedChain) {
        h = ((TargetedChain)handler).getResponseHandler();
      }
      return h;
    }

    public class Listener
            implements MessageExchangeEventListener {

        private Handler handler;

        public Listener(Handler handler) {
            this.handler = handler;
        }

        /**
         * @see org.apache.axis.ime.MessageExchangeContextListener#onMessageExchangeContext(MessageExchangeContext)
         */
        public void onEvent(
                MessageExchangeEvent event) {
            if (!(event instanceof MessageSendEvent))
                return;
            
            MessageSendEvent sendEvent = (MessageSendEvent)event;
            MessageExchangeSendContext context = sendEvent.getMessageExchangeSendContext();
            
            if (log.isDebugEnabled()) {
                log.debug("Enter: HandlerMessageExchange.Listener::onSend");
            }
            MessageExchangeEventListener listener = 
                context.getMessageExchangeEventListener();
            try {
                MessageContext msgContext =
                        context.getMessageContext();
                MessageExchangeCorrelator correlator =
                        context.getMessageExchangeCorrelator();
            
                if (handler instanceof TargetedChain) {
                  ((TargetedChain)handler).getPivotHandler().invoke(msgContext);
                } else {
                  handler.invoke(msgContext);
                }


                RECEIVE.put(correlator, context);
            } catch (Exception exception) {
                if (listener != null) {
                    MessageFaultEvent faultEvent = new MessageFaultEvent(
                            context.getMessageExchangeCorrelator(),
                            exception);
                    listener.onEvent(faultEvent);
                }
            } finally {
                if (log.isDebugEnabled()) {
                    log.debug("Exit: HandlerMessageExchange.Listener::onSend");
                }
            }
        }
    }
    
  public void clearOptions() {
    handler.setOptions(null);
  }

  public Hashtable getOptions() {
    return handler.getOptions();
  }

  public Object getOption(String propertyId, Object defaultValue) {
    Object value = getOption(propertyId);
    return (value == null) ? defaultValue : value;
  }

  public Object getOption(String propertyId) {
    return handler.getOption(propertyId);
  }

  public void setOptions(Hashtable properties) {
    handler.setOptions(properties);
  }

  public void setOption(String propertyId, Object propertyValue) {
    handler.setOption(propertyId, propertyValue);
  }

}
