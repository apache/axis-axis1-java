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
import org.apache.axis.MessageContext;
import org.apache.axis.security.AuthenticatedUser;
import org.apache.axis.security.SecurityProvider;
import org.apache.axis.security.simple.SimpleSecurityProvider;
import org.apache.axis.utils.JavaUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


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
    static Log log =
            LogFactory.getLog(SimpleAuthenticationHandler.class.getName());

    /**
     * Authenticate the user and password from the msgContext
     */
    public void invoke(MessageContext msgContext) throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug(JavaUtils.getMessage("enter00", 
                "SimpleAuthenticationHandler::invoke"));
        }

        SecurityProvider provider = (SecurityProvider)msgContext.getProperty("securityProvider");
        if (provider == null) {
            provider = new SimpleSecurityProvider();
            msgContext.setProperty("securityProvider", provider);
        }

        if (provider != null) {
            String  userID = msgContext.getUsername();
            if (log.isDebugEnabled()) {
                log.debug( JavaUtils.getMessage("user00", userID) );
            }

            // in order to authenticate, the user must exist
            if ( userID == null || userID.equals(""))
                throw new AxisFault( "Server.Unauthenticated",
                    JavaUtils.getMessage("cantAuth00", userID),
                    null, null );

            String passwd = msgContext.getPassword();
            if (log.isDebugEnabled()) {
                log.debug( JavaUtils.getMessage("password00", passwd) );
            }

            AuthenticatedUser authUser = provider.authenticate(msgContext);

            // if a password is defined, then it must match
            if ( authUser == null)
                throw new AxisFault( "Server.Unauthenticated",
                    JavaUtils.getMessage("cantAuth01", userID),
                    null, null );

            if (log.isDebugEnabled()) {
                log.debug( JavaUtils.getMessage("auth00", userID) );
            }

            msgContext.setProperty(MessageContext.AUTHUSER, authUser);
        }

        if (log.isDebugEnabled()) {
            log.debug(JavaUtils.getMessage("exit00", 
                "SimpleAuthenticationHandler::invoke") );
        }
    }
};
