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
package org.apache.axis.ime.event;

import org.apache.axis.MessageContext;
import org.apache.axis.ime.MessageExchangeCorrelator;
import org.apache.axis.ime.internal.MessageExchangeReceiveContext;

/**
 * The MessageReceiveEvent is used to notify listeners that a message
 * has been received.
 *
 * @author Ray Chun (rchun@sonicsoftware.com)
 */
public class MessageReceiveEvent
        extends MessageCorrelatedEvent {

    protected MessageExchangeReceiveContext receiveContext;
    
    public MessageReceiveEvent(
            MessageExchangeCorrelator correlator, 
            MessageExchangeReceiveContext receiveContext,
            MessageContext context) {
        super(correlator, context);
        this.receiveContext = receiveContext;
    }
    
    public MessageExchangeReceiveContext getMessageExchangeReceiveContext()
    {
        return receiveContext;
    }
}
