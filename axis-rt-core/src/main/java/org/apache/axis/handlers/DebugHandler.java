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

package org.apache.axis.handlers ;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;


/**
 *
 * @author Doug Davis (dug@us.ibm.com)
 */
public class DebugHandler extends BasicHandler {
    protected static Log log =
        LogFactory.getLog(DebugHandler.class.getName());

    public static final String NS_URI_DEBUG = "http://xml.apache.org/axis/debug";
    
    public void invoke(MessageContext msgContext) throws AxisFault {
        log.debug("Enter: DebugHandler::invoke");
        try {
            Message       msg = msgContext.getRequestMessage();

            SOAPEnvelope message = (SOAPEnvelope)msg.getSOAPEnvelope();
            SOAPHeaderElement header = message.
                getHeaderByName(NS_URI_DEBUG, "Debug");

            if (header != null) {
                Integer i = ((Integer)header
                             .getValueAsType(Constants.XSD_INT));
                if (i == null)
                    throw new AxisFault(Messages.getMessage("cantConvert03"));

                int debugVal = i.intValue();
                log.debug(Messages.getMessage("debugLevel00", "" + debugVal) );
                //Debug.setDebugLevel(debugVal);
                header.setProcessed(true);
            }
        }
        catch( Exception e ) {
            log.error( Messages.getMessage("exception00"), e );
            throw AxisFault.makeFault(e);
        }
        log.debug("Exit: DebugHandler::invoke");
    }

    public void onFault(MessageContext msgContext) {
        log.debug("Enter: DebugHandler::onFault");
        log.debug("Exit: DebugHandler::onFault");
    }

};
