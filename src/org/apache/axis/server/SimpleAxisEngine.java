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

package org.apache.axis.server ;

import java.util.* ;
import org.apache.axis.* ;
import org.apache.axis.utils.* ;
import org.apache.axis.handlers.* ;
import org.apache.axis.registries.* ;

/**
 *
 * @author Doug Davis (dug@us.ibm.com)
 */
public class SimpleAxisEngine implements Handler {
  /**
   * This entry point into the SOAP server 
   */
  protected Hashtable options ;

  /**
   * Find/load the registries and save them so we don't need to do this
   * each time we're called.
   */
  public void init() {
    // Load the simple handler registry and init it
    HandlerRegistry  hr = new SimpleHandlerRegistry();
    hr.init();
    addOption( Constants.HANDLER_REGISTRY, hr );

    // Load the simple deployed services registry and init it
    HandlerRegistry  sr = new SimpleServiceRegistry();
    sr.init();
    addOption( Constants.SERVICE_REGISTRY, sr );
  }

  public void cleanup() {
  };

  /**
   * Main routine of the AXIS server.  In short we locate the appropriate
   * handler for the desired service and invoke() it.
   */
  public void invoke(MessageContext msgContext) throws AxisFault {
    HandlerRegistry hr = (HandlerRegistry)getOption(Constants.HANDLER_REGISTRY);
    HandlerRegistry sr = (HandlerRegistry)getOption(Constants.SERVICE_REGISTRY);

    /* The target web-server should be place in the MC_TARGET entry in the */
    /* bag of the msgContext object.  If it's not there we need to scan    */
    /* the incoming message to find it.                                    */
    /***********************************************************************/
    String action = (String) msgContext.getProperty( Constants.MC_TARGET );
    if ( action == null ) action = "EchoService" ; // Temporary - need 2 scan

    Handler h = sr.find( action );

    if ( h == null ) {
      throw new AxisFault( "Server.NoSuchService",
                           "Service '" + action + "' was not found",
                           null, null );
    }

    /* Place in the bag so that handlers down the line can have access to */
    /* it - ie. can look thru it's list of options                        */
    /**********************************************************************/
    msgContext.setProperty( Constants.MC_SVC_HANDLER, h );

    h.init();   // ???
    try {
      h.invoke( msgContext );
    }
    catch( Exception e ) {
      // Should we even bother catching it ?
      if ( !(e instanceof AxisFault) ) e = new AxisFault( e );
      throw (AxisFault) e ;
    }
    h.cleanup();   // ???
  };

  public void undo(MessageContext msgContext) {
  };

  public boolean canHandleBlock(QName qname) {
    return( false );
  };
 
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
