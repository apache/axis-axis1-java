/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
