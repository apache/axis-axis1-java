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
import org.apache.axis.ime.MessageExchangeCorrelator;
import org.apache.axis.ime.MessageExchangeEventListener;

/**
 * Note: the only challenge with making this class serializable
 * is that org.apache.axis.MessageContext is currently NOT
 * serializable.  MessageContext needs to change in order to 
 * take advantage of persistent Channels and CorrelatorServices
 * 
 * For thread safety, instances of this class are immutable
 * 
 * @author James M Snell (jasnell@us.ibm.com)
 * @author Ray Chun (rchun@sonicsoftware.com)
 */
public final class MessageExchangeSendContext
        extends MessageExchangeReceiveContext {

    public static MessageExchangeSendContext newInstance(
            MessageExchangeCorrelator correlator,
            MessageContext context,
            MessageExchangeEventListener listener) {
        MessageExchangeSendContext mectx =
                new MessageExchangeSendContext();
        mectx.correlator = correlator;
        mectx.context = context;
        mectx.listener = listener;
        return mectx;
    }

    protected MessageContext context;

    protected MessageExchangeSendContext() {}

    public MessageContext getMessageContext() {
        return this.context;
    }

}
