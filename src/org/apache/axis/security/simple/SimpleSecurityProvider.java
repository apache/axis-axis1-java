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

package org.apache.axis.security.simple;

import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.security.AuthenticatedUser;
import org.apache.axis.security.SecurityProvider;
import org.apache.axis.utils.JavaUtils;

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.components.i18n.Messages;
import org.apache.commons.logging.Log;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * SimpleSecurityProvider
 *
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class SimpleSecurityProvider implements SecurityProvider {
    protected static Log log =
        LogFactory.getLog(SimpleSecurityProvider.class.getName());

    HashMap users = null;
    HashMap perms = null;

    boolean initialized = false;

    // load the users list
    private synchronized void initialize(MessageContext msgContext)
    {
        if (initialized) return;

        String configPath = msgContext.getStrProp(Constants.MC_CONFIGPATH);
        if (configPath == null) {
            configPath = "";
        } else {
            configPath += File.separator;
        }
        File userFile = new File(configPath + "users.lst");
        if (userFile.exists()) {
            users = new HashMap();

            try {

                FileReader        fr   = new FileReader( userFile );
                LineNumberReader  lnr  = new LineNumberReader( fr );
                String            line = null ;

                // parse lines into user and passwd tokens and add result to hash table
                while ( (line = lnr.readLine()) != null ) {
                    StringTokenizer  st = new StringTokenizer( line );
                    if ( st.hasMoreTokens() ) {
                        String userID = st.nextToken();
                        String passwd = (st.hasMoreTokens()) ? st.nextToken() : "";

                        if (log.isDebugEnabled()) {
                            log.debug( Messages.getMessage("fromFile00", 
                                userID, passwd) );
                        }

                        users.put(userID, passwd);
                    }
                }

                lnr.close();

            } catch( Exception e ) {
                log.error( Messages.getMessage("exception00"), e );
                return;
            }
        }
        initialized = true;
    }

    /** Authenticate a user from a username/password pair.
     *
     * @param username the user name to check
     * @param password the password to check
     * @return an AuthenticatedUser or null
     */
    public AuthenticatedUser authenticate(MessageContext msgContext) {

        if (!initialized) {
            initialize(msgContext);
        }

        String username = msgContext.getUsername();
        String password = msgContext.getPassword();

        if (users != null) {
            if (log.isDebugEnabled()) {
                log.debug( Messages.getMessage("user00", username) );
            }

            // in order to authenticate, the user must exist
            if ( username == null ||
                 username.equals("") ||
                 !users.containsKey(username) )
                return null;

            String valid = (String) users.get(username);

            if (log.isDebugEnabled()) {
                log.debug( Messages.getMessage("password00", password) );
            }

            // if a password is defined, then it must match
            if ( valid.length()>0 && !valid.equals(password) )
                return null;

            if (log.isDebugEnabled()) {
                log.debug( Messages.getMessage("auth00", username) );
            }

            return new SimpleAuthenticatedUser(username);
        }

        return null;
    }

    /** See if a user matches a principal name.  The name might be a user
     * or a group.
     *
     * @return true if the user matches the passed name
     */
    public boolean userMatches(AuthenticatedUser user, String principal) {
        if (user == null) return principal == null;
        return user.getName().compareToIgnoreCase(principal) == 0;
    }
}
