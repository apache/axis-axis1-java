/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.ibm.com.  For more
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
 * Just a simple Authorization Handler to see if the user
 * specified in the Bag in the MessageContext is allowed to preform this
 * action.
 *
 * Just look for 'user' and 'action' in a file called 'perms.lst'
 *
 * Replace this with your 'real' Authorization code.
 *
 * @author Doug Davis (dug@us.ibm.com)
 */
public class SimpleAuthorizationHandler implements Handler {
  protected Hashtable  options ;

  public void init() {
  }

  public void cleanup() {
  }

  public void invoke(MessageContext msgContext) throws AxisFault {
    Debug.Print( 1, "Enter: SimpleAuthenticationHandler::invoke" );
    try {
      String  userID = (String) msgContext.getProperty( Constants.MC_USERID );
      String  action = (String) msgContext.getProperty( Constants.MC_HTTP_SOAPACTION );

      Debug.Print( 1, "User: " + userID );
      Debug.Print( 1, "Action: " + action );

      if ( userID == null || userID.equals("") )
        throw new AxisFault( "Server.Unauthorized", 
                             "User not authorized",
                             null, null );

      FileReader        fr   = new FileReader( "perms.lst" );
      LineNumberReader  lnr  = new LineNumberReader( fr );
      String            line = null ;
      boolean           done = false ;
      while ( (line = lnr.readLine()) != null ) {
        StringTokenizer  st = new StringTokenizer( line );
        String           u  = null ,
                         a  = null ;

        if ( st.hasMoreTokens() ) u = st.nextToken();
        if ( st.hasMoreTokens() ) a = st.nextToken();
        Debug.Print( 2, "From file: " + u + ":" + a );

        if ( !userID.equals(u) ) continue ;
        if ( !action.equals(a) ) continue ;

        Debug.Print( 1, "User '" + userID + "' authorized to: " +a );
        done = true ;
        break ;
      }
      lnr.close();
      fr.close();
      if ( !done ) 
        throw new AxisFault( "Server.Unauthorized", 
                             "User not authorized",
                             null, null );
    }
    catch( Exception e ) {
      Debug.Print( 1, e );
      if ( !(e instanceof AxisFault) ) e = new AxisFault(e);
      throw (AxisFault) e ;
    }
    Debug.Print( 1, "Exit: SimpleAuthorizationHandler::invoke" );
  }

  public void undo(MessageContext msgContext) {
    Debug.Print( 1, "Enter: SimpleAuthorizationHandler::undo" );
    Debug.Print( 1, "Exit: SimpleAuthorizationHandler::undo" );
  }

  public boolean canHandleBlock(QName qname) {
    return( false );
  }

  /**
   * Add the given option (name/value) to this handler's bag of options
   */
  public void addOption(String name, Object value) {
    if ( options == null ) options = new Hashtable();
    options.put( name, value );
  }

  /**
   * Returns the option corresponding to the 'name' given
   */
  public Object getOption(String name) {
    if ( options == null ) return( null );
    return( options.get(name) );
  }

  /**
   * Return the entire list of options
   */
  public Hashtable getOptions() {
    return( options );
  }

  public void setOptions(Hashtable opts) {
    options = opts ;
  }

};
