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

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.ime.MessageExchange;
import org.apache.commons.logging.Log;

/**
 * This could probably be a bit more sophisticated, 
 * but it works for now
 * 
 * @author James M Snell (jasnell@us.ibm.com)
 */
public class MessageExchangeHandler 
    extends BasicHandler {
    
    protected static Log log =
        LogFactory.getLog(MessageExchangeHandler.class.getName());
    
    private MessageExchange messageExchange;   
    
    public MessageExchangeHandler() {}
    
    public MessageExchangeHandler(MessageExchange exchange) {
        this.messageExchange = exchange;
    }
    
    public void invoke(
            MessageContext msgContext) 
            throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug("Enter: MessageExchangeHandler::invoke");
        }
        msgContext = messageExchange.sendAndReceive(msgContext);
        if (log.isDebugEnabled()) {
            log.debug("Exit: MessageExchangeHandler::invoke");
        }
    }
    
    public MessageExchange getMessageExchange() {
        return this.messageExchange;
    }
    
    public void setMessageExchange(MessageExchange exchange) {
        this.messageExchange = exchange;
    }
}
