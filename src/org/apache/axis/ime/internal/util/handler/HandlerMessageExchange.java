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

package org.apache.axis.ime.internal.util.handler;

import org.apache.axis.Handler;
import org.apache.axis.MessageContext;
import org.apache.axis.TargetedChain;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.ime.MessageExchangeCorrelator;
import org.apache.axis.ime.MessageExchangeEvent;
import org.apache.axis.ime.MessageExchangeEventListener;
import org.apache.axis.ime.event.MessageFaultEvent;
import org.apache.axis.ime.event.MessageSendEvent;
import org.apache.axis.ime.internal.FirstComeFirstServeDispatchPolicy;
import org.apache.axis.ime.internal.MessageExchangeProvider;
import org.apache.axis.ime.internal.MessageExchangeSendContext;
import org.apache.axis.ime.internal.ReceivedMessageDispatchPolicy;
import org.apache.commons.logging.Log;

import java.util.Hashtable;

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
