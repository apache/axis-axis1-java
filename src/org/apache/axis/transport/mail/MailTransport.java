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

package org.apache.axis.transport.mail;

import org.apache.axis.AxisEngine;
import org.apache.axis.MessageContext;
import org.apache.axis.client.Call;
import org.apache.axis.client.Transport;

/**
 * A Transport which will cause an invocation via "mail" 
 * 
 * @author Davanum Srinivas <dims@yahoo.com>
 */
public class MailTransport extends Transport {
    public MailTransport() {
        transportName = "mail";
    }

    /**
     * Set up any transport-specific derived properties in the message context.
     * @param mc the context to set up
     * @param call the Call object
     * @param engine the engine containing the registries
     */
    public void setupMessageContextImpl(MessageContext mc,
                                        Call call,
                                        AxisEngine engine) {
    }
}

