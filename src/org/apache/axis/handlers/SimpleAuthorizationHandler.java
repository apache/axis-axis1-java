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
import org.apache.axis.Handler;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.security.AuthenticatedUser;
import org.apache.axis.security.SecurityProvider;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

import java.util.StringTokenizer;


/**
 * Just a simple Authorization Handler to see if the user
 * specified in the Bag in the MessageContext is allowed to preform this
 * action.
 *
 * Look at the <code>allowedRoles</code> handler parameter to determine if
 * user has rights to access the service
 *
 * The <code>allowByDefault</code> handler parameter can be used to authorize
 * all users if the parameter is set to true and the <code>allowedRoles</code>
 * access control list is not specified.
 *
 * Replace this with your 'real' Authorization code.
 *
 * @author Doug Davis (dug@us.ibm.com)
 * @author Sam Ruby (rubys@us.ibm.com)
 */
public class SimpleAuthorizationHandler extends BasicHandler {
    protected static Log log =
        LogFactory.getLog(SimpleAuthorizationHandler.class.getName());

    /**
     * Authorize the user and targetService from the msgContext
     */
    public void invoke(MessageContext msgContext) throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug("Enter: SimpleAuthorizationHandler::invoke");
        }

        boolean allowByDefault =
            JavaUtils.isTrueExplicitly(getOption("allowByDefault"));

        AuthenticatedUser user = (AuthenticatedUser)msgContext.
                                         getProperty(MessageContext.AUTHUSER);

        if (user == null)
            throw new AxisFault("Server.NoUser",
                    Messages.getMessage("needUser00"), null, null);

        String userID = user.getName();
        Handler serviceHandler = msgContext.getService();

        if (serviceHandler == null)
            throw new AxisFault(Messages.getMessage("needService00"));

        String serviceName = serviceHandler.getName();

        String allowedRoles = (String)serviceHandler.getOption("allowedRoles");
        if (allowedRoles == null) {
            if (allowByDefault) {
                if (log.isDebugEnabled()) {
                    log.debug(Messages.getMessage( "noRoles00"));
                }
            }
            else {
                if (log.isDebugEnabled()) {
                    log.debug(Messages.getMessage( "noRoles01"));
                }

                throw new AxisFault( "Server.Unauthorized",
                    Messages.getMessage("notAuth00", userID, serviceName),
                    null, null );
            }

            if (log.isDebugEnabled()) {
                log.debug("Exit: SimpleAuthorizationHandler::invoke");
            }
            return;
        }

        SecurityProvider provider = (SecurityProvider)msgContext.getProperty(MessageContext.SECURITY_PROVIDER);
        if (provider == null)
            throw new AxisFault(Messages.getMessage("noSecurity00"));

        StringTokenizer st = new StringTokenizer(allowedRoles, ",");
        while (st.hasMoreTokens()) {
            String thisRole = st.nextToken();
            if (provider.userMatches(user, thisRole)) {

                if (log.isDebugEnabled()) {
                    log.debug(Messages.getMessage("auth01",
                        userID, serviceName));
                }

                if (log.isDebugEnabled()) {
                    log.debug("Exit: SimpleAuthorizationHandler::invoke");
                }
                return;
            }
        }

        throw new AxisFault( "Server.Unauthorized",
            Messages.getMessage("cantAuth02", userID, serviceName),
            null, null );
    }

    /**
     * Nothing to undo
     */
    public void onFault(MessageContext msgContext) {
        if (log.isDebugEnabled()) {
            log.debug("Enter: SimpleAuthorizationHandler::onFault");
            log.debug("Exit: SimpleAuthorizationHandler::onFault");
        }
    }
};
