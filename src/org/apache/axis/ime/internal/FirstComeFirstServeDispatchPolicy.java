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

import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.ime.MessageExchangeCorrelator;
import org.apache.axis.ime.MessageExchangeEventListener;
import org.apache.axis.ime.event.MessageFaultEvent;
import org.apache.axis.ime.event.MessageReceiveEvent;
import org.apache.axis.ime.internal.util.KeyedBuffer;
import org.apache.commons.logging.Log;

/**
 * @author James M Snell (jasnell@us.ibm.com)
 * @author Ray Chun (rchun@sonicsoftware.com)
 */
public class FirstComeFirstServeDispatchPolicy
        implements ReceivedMessageDispatchPolicy {
  
    protected static Log log =
        LogFactory.getLog(FirstComeFirstServeDispatchPolicy.class.getName());
  
    protected KeyedBuffer RECEIVE_REQUESTS;
    protected KeyedBuffer RECEIVE;
    
    public FirstComeFirstServeDispatchPolicy(
            KeyedBuffer RECEIVE,
            KeyedBuffer RECEIVE_REQUESTS) {
        this.RECEIVE = RECEIVE;
        this.RECEIVE_REQUESTS = RECEIVE_REQUESTS;
    }
  
    public void dispatch(
            MessageExchangeSendContext context) {

        if (log.isDebugEnabled()) {
            log.debug("Enter: FirstComeFirstServeDispatchPolicy::dispatch");
        }
    
      // 1. Get the correlator
      // 2. See if there are any receive requests based on the correlator
      // 3. If there are receive requests for the correlator, deliver to the first one
      // 4. If there are no receive requests for the correlator, deliver to the first "anonymous" receive request
      // 5. If there are no receive requests, put the message back on the Queue
      
        MessageExchangeReceiveContext receiveContext = null;
        MessageExchangeCorrelator correlator =
            context.getMessageExchangeCorrelator();
        receiveContext = (MessageExchangeReceiveContext)RECEIVE_REQUESTS.get(correlator);
        if (receiveContext == null) {
            receiveContext = (MessageExchangeReceiveContext)RECEIVE_REQUESTS.get(SimpleMessageExchangeCorrelator.NULL_CORRELATOR);
        }
        if (receiveContext == null) 
            RECEIVE.put(correlator,context);
        else {
            MessageExchangeEventListener eventListener = 
              receiveContext.getMessageExchangeEventListener();
            MessageContext msgContext = 
              context.getMessageContext();
            try {
                MessageReceiveEvent receiveEvent = 
                    new org.apache.axis.ime.event.MessageReceiveEvent(
                            correlator, 
                            receiveContext, 
                            context.getMessageContext());
                eventListener.onEvent(receiveEvent);
            } catch (Exception exception) {
              if (eventListener != null) {
                  MessageFaultEvent faultEvent = new MessageFaultEvent(
                        correlator,
                        exception);
                  eventListener.onEvent(faultEvent);
              }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Exit: FirstComeFirstServeDispatchPolicy::dispatch");
        }
        
    }
  
}
