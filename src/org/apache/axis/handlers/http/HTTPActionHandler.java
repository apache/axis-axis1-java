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

package org.apache.axis.handlers.http;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;


/** An <code>HTTPActionHandler</code> simply sets the context's TargetService
 * property from the HTTPAction property.  We expect there to be a
 * Router on the chain after us, to dispatch to the service named in
 * the SOAPAction.
 *
 * In the real world, this might do some more complex mapping of
 * SOAPAction to a TargetService.
 *
 * @author Glen Daniels (gdaniels@allaire.com)
 * @author Doug Davis (dug@us.ibm.com)
 */
public class HTTPActionHandler extends BasicHandler
{
    protected static Log log =
        LogFactory.getLog(HTTPActionHandler.class.getName());

    public void invoke(MessageContext msgContext) throws AxisFault
    {
        log.debug("Enter: HTTPActionHandler::invoke");

        /** If there's already a targetService then just return.
         */
        if ( msgContext.getService() == null ) {
            String action = (String) msgContext.getSOAPActionURI();
            log.debug( "  HTTP SOAPAction: " + action );
            
            /** The idea is that this handler only goes in the chain IF this
            * service does a mapping between SOAPAction and target.  Therefore
            * if we get here with no action, we're in trouble.
            */
            if (action == null) {
                throw new AxisFault( "Server.NoHTTPSOAPAction",
                    Messages.getMessage("noSOAPAction00"),
                    null, null );
            }
            
            action = action.trim();

            // handle empty SOAPAction
            if (action.length() > 0 && action.charAt(0) == '\"') {
                // assertTrue(action.endsWith("\"")
                if (action.equals("\"\"")) {
                    action = "";
                } else {
                    action = action.substring(1, action.length() - 1);
                }
            }
            
            // if action is zero-length string, don't set anything
            if (action.length() > 0) {
                msgContext.setTargetService( action );
            }
        }

        log.debug("Exit: HTTPActionHandler::invoke");
    }
}
