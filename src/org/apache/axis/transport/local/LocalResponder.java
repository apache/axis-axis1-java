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

package org.apache.axis.transport.local;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.handlers.BasicHandler;
import org.apache.commons.logging.Log;


/**
 * Tiny Handler which just makes sure to Stringize the outgoing
 * Message to appropriately use serializers on the server side.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class LocalResponder extends BasicHandler {
    protected static Log log =
        LogFactory.getLog(LocalResponder.class.getName());

    public void invoke(MessageContext msgContext) throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug("Enter: LocalResponder::invoke");
        }

        String msgStr = msgContext.getResponseMessage().getSOAPPartAsString();

        if (log.isDebugEnabled()) {
            log.debug(msgStr);

            log.debug("Exit: LocalResponder::invoke");
        }
    }
}
