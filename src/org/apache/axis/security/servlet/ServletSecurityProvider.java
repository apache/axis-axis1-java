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

package org.apache.axis.security.servlet;

import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.security.AuthenticatedUser;
import org.apache.axis.security.SecurityProvider;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;


/**
 * A ServletSecurityProvider, combined with the ServletAuthenticatedUser
 * class, allows the standard servlet security mechanisms (isUserInRole(),
 * etc.) to integrate with Axis' access control mechanism.
 *
 * By utilizing this class (which the AxisServlet can be configured to
 * do automatically), authentication and role information will come from
 * your servlet engine.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class ServletSecurityProvider implements SecurityProvider {
    protected static Log log =
        LogFactory.getLog(ServletSecurityProvider.class.getName());

    static HashMap users = null;

    /** Authenticate a user from a username/password pair.
     *
     * @param username the user name to check
     * @param password the password to check
     * @return an AuthenticatedUser or null
     */
    public AuthenticatedUser authenticate(MessageContext msgContext) {
        HttpServletRequest req = (HttpServletRequest)msgContext.getProperty(
                                      HTTPConstants.MC_HTTP_SERVLETREQUEST);

        if (req == null)
            return null;

        log.debug(Messages.getMessage("got00", "HttpServletRequest"));

        Principal principal = req.getUserPrincipal();
        if (principal == null) {
            log.debug(Messages.getMessage("noPrincipal00"));
            return null;
        }

        log.debug(Messages.getMessage("gotPrincipal00",  principal.getName()));

        return new ServletAuthenticatedUser(req);
    }

    /** See if a user matches a principal name.  The name might be a user
     * or a group.
     *
     * @return true if the user matches the passed name
     */
    public boolean userMatches(AuthenticatedUser user, String principal) {
        if (user == null) return principal == null;

        if (user instanceof ServletAuthenticatedUser) {
            ServletAuthenticatedUser servletUser = (ServletAuthenticatedUser)user;
            return servletUser.getRequest().isUserInRole(principal);
        }

        return false;
    }
}
