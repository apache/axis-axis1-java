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

import org.apache.axis.Handler;
import org.apache.axis.MessageContext;
import org.apache.axis.ime.MessageExchangeContext;
import org.apache.axis.ime.MessageExchangeContextListener;
import org.apache.axis.ime.MessageExchangeCorrelator;
import org.apache.axis.ime.MessageExchangeFaultListener;
import org.apache.axis.ime.MessageExchangeReceiveListener;
import org.apache.axis.ime.internal.MessageExchangeProvider1;

/**
 * Used to wrap synchronous handlers (e.g. Axis 1.0 transports)
 * 
 * @author James M Snell (jasnell@us.ibm.com)
 */
public class HandlerWrapper1
        extends MessageExchangeProvider1 {

    private Handler handler;

    public HandlerWrapper1(Handler handler) {
        this.handler = handler;
    }

    /**
     * @see org.apache.axis.ime.internal.MessageExchangeProvider1#createReceiveMessageContextListener()
     */
    protected MessageExchangeContextListener createReceiveMessageContextListener() {
        return new ReceiveListener();
    }

    /**
     * @see org.apache.axis.ime.internal.MessageExchangeProvider1#createSendMessageContextListener()
     */
    protected MessageExchangeContextListener createSendMessageContextListener() {
        return new SendListener(handler);
    }


    public class SendListener
            implements MessageExchangeContextListener {

        private Handler handler;

        public SendListener(Handler handler) {
            this.handler = handler;
        }

        /**
         * @see org.apache.axis.ime.MessageExchangeContextListener#onMessageExchangeContext(MessageExchangeContext)
         */
        public void onMessageExchangeContext(
                MessageExchangeContext context) {
            try {
                MessageContext msgContext =
                        context.getMessageContext();
                MessageExchangeCorrelator correlator =
                        context.getMessageExchangeCorrelator();
            
                // should I do init's and cleanup's in here?  
                handler.invoke(msgContext);


                RECEIVE.put(correlator, context);
            } catch (Exception exception) {
                MessageExchangeFaultListener listener =
                        context.getMessageExchangeFaultListener();
                if (listener != null)
                    listener.onFault(
                            context.getMessageExchangeCorrelator(),
                            exception);
            }
        }
    }

    public class ReceiveListener
            implements MessageExchangeContextListener {

        /**
         * @see org.apache.axis.ime.MessageExchangeContextListener#onMessageExchangeContext(MessageExchangeContext)
         */
        public void onMessageExchangeContext(
                MessageExchangeContext context) {

            MessageExchangeReceiveListener receiveListener =
                    context.getMessageExchangeReceiveListener();
            MessageExchangeFaultListener faultListener =
                    context.getMessageExchangeFaultListener();
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
}
