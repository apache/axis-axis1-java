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

package org.apache.axis.security.servlet;

import org.apache.axis.AxisInternalServices;
import org.apache.axis.MessageContext;
import org.apache.axis.security.AuthenticatedUser;
import org.apache.axis.security.SecurityProvider;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.utils.JavaUtils;
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
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class ServletSecurityProvider implements SecurityProvider {
    protected static Log log =
        AxisInternalServices.getLog(ServletSecurityProvider.class.getName());

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

        log.debug(JavaUtils.getMessage("got00", "HttpServletRequest"));

        Principal principal = req.getUserPrincipal();
        if (principal == null) {
            log.debug(JavaUtils.getMessage("noPrincipal00"));
            return null;
        }

        log.debug(JavaUtils.getMessage("gotPrincipal00",  principal.getName()));

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
