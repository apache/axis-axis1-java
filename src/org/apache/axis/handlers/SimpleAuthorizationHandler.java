/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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

        SecurityProvider provider = (SecurityProvider)msgContext.getProperty("securityProvider");
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
