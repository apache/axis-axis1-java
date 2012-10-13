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
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.security.AuthenticatedUser;
import org.apache.axis.security.SecurityProvider;
import org.apache.axis.security.simple.SimpleSecurityProvider;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;


/**
 * Just a simple Authentication Handler to see if the user
 * specified in the Bag in the MessageContext is allowed to continue.
 *
 * Just look for 'user' and 'password' in a file called 'users.lst'.
 *
 * Replace this with your 'real' authenication code.
 *
 * @author Doug Davis (dug@us.ibm.com)
 * @author Sam Ruby (rubys@us.ibm.com)
 */
public class SimpleAuthenticationHandler extends BasicHandler {
    protected static Log log =
        LogFactory.getLog(SimpleAuthenticationHandler.class.getName());

    /**
     * Authenticate the user and password from the msgContext
     */
    public void invoke(MessageContext msgContext) throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug("Enter: SimpleAuthenticationHandler::invoke");
        }

        SecurityProvider provider = (SecurityProvider)msgContext.getProperty(MessageContext.SECURITY_PROVIDER);
        if (provider == null) {
            provider = new SimpleSecurityProvider();
            msgContext.setProperty(MessageContext.SECURITY_PROVIDER, provider);
        }

        if (provider != null) {
            String  userID = msgContext.getUsername();
            if (log.isDebugEnabled()) {
                log.debug( Messages.getMessage("user00", userID) );
            }

            // in order to authenticate, the user must exist
            if ( userID == null || userID.equals(""))
                throw new AxisFault( "Server.Unauthenticated",
                    Messages.getMessage("cantAuth00", userID),
                    null, null );

            String passwd = msgContext.getPassword();
            if (log.isDebugEnabled()) {
                log.debug( Messages.getMessage("password00", passwd) );
            }

            AuthenticatedUser authUser = provider.authenticate(msgContext);

            // if a password is defined, then it must match
            if ( authUser == null)
                throw new AxisFault( "Server.Unauthenticated",
                    Messages.getMessage("cantAuth01", userID),
                    null, null );

            if (log.isDebugEnabled()) {
                log.debug( Messages.getMessage("auth00", userID) );
            }

            msgContext.setProperty(MessageContext.AUTHUSER, authUser);
        }

        if (log.isDebugEnabled()) {
            log.debug("Exit: SimpleAuthenticationHandler::invoke");
        }
    }
};
