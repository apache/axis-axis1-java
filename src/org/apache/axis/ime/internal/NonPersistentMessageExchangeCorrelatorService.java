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

import org.apache.axis.ime.MessageExchangeCorrelator;

import java.util.Hashtable;

/**
 * @author James M Snell (jasnell@us.ibm.com)
 */
public class NonPersistentMessageExchangeCorrelatorService
        implements MessageExchangeCorrelatorService {

    Hashtable contexts = new Hashtable();

    /**
     * @see org.apache.axis.ime.MessageExchangeCorrelatorService#put(MessageExchangeCorrelator, MessageExchangeContext)
     */
    public void put(
            MessageExchangeCorrelator correlator,
            Object context) {
        synchronized (contexts) {
            contexts.put(correlator, context);
        }
    }

    /**
     * @see org.apache.axis.ime.MessageExchangeCorrelatorService#get(MessageExchangeCorrelator)
     */
    public Object get(MessageExchangeCorrelator correlator) {
        synchronized (contexts) {
            return contexts.remove(correlator);
        }
    }

}
