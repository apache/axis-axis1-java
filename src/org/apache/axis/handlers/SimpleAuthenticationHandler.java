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
    
    // Simple hashtable of user and password.  Null means everybody
    // will authenticate (replace with new Hashtable() if you want 
    // the default to be that nobody will be authenticated.
    static private Hashtable entries = null;

    // load the users list
    static {
        File userFile = new File("users.lst");
        if (userFile.exists()) {
            entries = new Hashtable();

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

                        Debug.Print( 2, "From file: '", userID, "':'", passwd, "'" );
                        entries.put(userID, passwd);
                    }
                }

                lnr.close();

            } catch( Exception e ) {
                Debug.Print( 1, e );
            }
        }
    }

    /**
     * Authenticate the user and password from the msgContext
     */
    public void invoke(MessageContext msgContext) throws AxisFault {
        Debug.Print( 1, "Enter: SimpleAuthenticationHandler::invoke" );

        if (entries != null) {
            String  userID = (String) msgContext.getProperty( MessageContext.USERID );
            Debug.Print( 1, "User: ",  userID );

            // in order to authenticate, the user must exist
            if ( userID == null || userID.equals("") || !entries.containsKey(userID) )
                throw new AxisFault( "Server.Unauthenticated", 
                    "User '" + userID + "' not authenticated (unknown user)",
                    null, null );
            
            String passwd = (String) msgContext.getProperty( MessageContext.PASSWORD );
            String valid = (String) entries.get(userID);
            Debug.Print( 2, "Pass: ", passwd );
            
            // if a password is defined, then it must match
            if ( valid.length()>0 && !valid.equals(passwd) ) 
                throw new AxisFault( "Server.Unauthenticated", 
                    "User '" + userID + "' not authenticated (bad password)",
                    null, null );

            Debug.Print( 1, "User '", userID, "' authenticated to server" );
        }

        Debug.Print( 1, "Exit: SimpleAuthenticationHandler::invoke" );
    }

    /**
     * Nothing to undo
     */
    public void undo(MessageContext msgContext) {
        Debug.Print( 1, "Enter: SimpleAuthenticationHandler::undo" );
        Debug.Print( 1, "Exit: SimpleAuthenticationHandler::undo" );
    }
};
