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

import java.io.* ;
import java.util.* ;
import org.apache.axis.* ;
import org.apache.axis.utils.* ;
import org.apache.axis.message.* ;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.log4j.Category;

/**
 * Just a simple Authorization Handler to see if the user
 * specified in the Bag in the MessageContext is allowed to preform this
 * action.
 *
 * Just look for 'user' and 'action' in a file called 'perms.lst'
 *
 * Replace this with your 'real' Authorization code.
 *
 * @author Doug Davis (dug@us.ibm.com)
 * @author Sam Ruby (rubys@us.ibm.com)
 */
public class SimpleAuthorizationHandler extends BasicHandler {
    static Category category =
            Category.getInstance(SimpleAuthorizationHandler.class.getName());

    // Simple hashtable of users.  Null means everybody
    // will authorize (replace with new Hashtable() if you want
    // the default to be that nobody is authorized
    //
    // Values will be hashtables of valid actions for the user
    static private Hashtable entries = null;

    // load the perms list
    static {
        File permFile = new File("perms.lst");
        if (permFile.exists()) {
            entries = new Hashtable();

            try {
                FileReader        fr   = new FileReader( permFile );
                LineNumberReader  lnr  = new LineNumberReader( fr );
                String            line = null ;

                // parse lines into user and passwd tokens and add result to hash table
                while ( (line = lnr.readLine()) != null ) {
                    StringTokenizer  st = new StringTokenizer( line );
                    if ( st.hasMoreTokens() ) {
                        String userID = st.nextToken();
                        String action = (st.hasMoreTokens()) ? st.nextToken() : "";

                        category.info( "User '" + userID + "' authorized to: " + action );

                        // if we haven't seen this user before, create an entry
                        if (!entries.containsKey(userID))
                            entries.put(userID, new Hashtable());

                        // add this action to the list of actions permitted to this user
                        Hashtable authlist = (Hashtable) entries.get(userID);
                        authlist.put(action, action);
                    }
                }

                lnr.close();

            } catch( Exception e ) {
                category.error( e );
            }
        }
    }

    /**
     * Authorize the user and targetService from the msgContext
     */
    public void invoke(MessageContext msgContext) throws AxisFault {
        category.debug("Enter: SimpleAuthorizationHandler::invoke" );

        String userID = (String) msgContext.getProperty( MessageContext.USERID );
        String action = msgContext.getTargetService();

        category.debug( "User: '" + userID + "'" );
        category.debug( "Action: '" + action + "'" );

        if (entries != null) { // perm.list exists

            Hashtable authlist = (Hashtable) entries.get(userID);
            if ( authlist == null || !authlist.containsKey(action) ) {
                throw new AxisFault( "Server.Unauthorized",
                    "User '" + userID + "' not authorized to '" + action + "'",
                    null, null );
            }
        }

        category.debug( "User '" + userID + "' authorized to: " + action );

        category.debug("Exit: SimpleAuthorizationHandler::invoke" );
    }

    /**
     * Nothing to undo
     */
    public void undo(MessageContext msgContext) {
        category.debug("Enter: SimpleAuthorizationHandler::undo" );
        category.debug("Exit: SimpleAuthorizationHandler::undo" );
    }
};
