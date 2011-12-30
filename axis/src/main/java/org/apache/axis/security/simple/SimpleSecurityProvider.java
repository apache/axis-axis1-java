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

package org.apache.axis.security.simple;

import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.security.AuthenticatedUser;
import org.apache.axis.security.SecurityProvider;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * SimpleSecurityProvider
 *
 * @author Glen Daniels (gdaniels@apache.org)
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
