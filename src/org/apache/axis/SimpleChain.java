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
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.axis ;

import java.util.* ;
import org.apache.axis.* ;
import org.apache.axis.utils.* ;

/**
 *
 * @author Doug Davis (dug@us.ibm.com)
 */
public class SimpleChain implements Chain {
  protected Vector     handlers ;
  protected Hashtable  options ;

  public void init() {
    for ( int i = 0 ; i < handlers.size() ; i++ )
      ((Handler) handlers.elementAt( i )).init();
  }

  public void cleanup() {
    for ( int i = 0 ; i < handlers.size() ; i++ )
      ((Handler) handlers.elementAt( i )).cleanup();
  }

  /**
   * Iterate over the chain invoking each handler.  If there's a fault
   * then call 'undo' for each completed handler in reverse order, then 
   * rethrow the exception.
   */
  public void invoke(MessageContext msgContext) throws AxisFault {
    Debug.Print( 1, "Enter: SimpleChain::invoke" );
    int i = 0 ;
    try {
      for ( i = 0 ; i < handlers.size() ; i++ )
        ((Handler) handlers.elementAt( i )).invoke( msgContext );
    }
    catch( Exception e ) {
      // undo in reverse order - rethrow
      Debug.Print( 1, e );
      if( !(e instanceof AxisFault ) )
        e = new AxisFault( e );
      while( --i >= 0 )
        ((Handler) handlers.elementAt( i )).undo( msgContext );
      throw (AxisFault) e ;
    }
    Debug.Print( 1, "Exit: SimpleChain::invoke" );
  }

  /**
   * Undo all of the work this chain completed because some handler
   * later on has faulted - in reverse order.
   */
  public void undo(MessageContext msgContext) {
    Debug.Print( 1, "Enter: SimpleChain::undo" );
    for ( int i = handlers.size()-1 ; i >= 0 ; i-- )
      ((Handler) handlers.elementAt( i )).undo( msgContext );
    Debug.Print( 1, "Exit: SimpleChain::undo" );
  }

  public boolean canHandleBlock(QName qname) {
    for ( int i = 0 ; i < handlers.size() ; i++ )
      if ( ((Handler) handlers.elementAt( i )).canHandleBlock(qname) )
        return( true );
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

  public void addHandler(Handler handler) {
    if ( handlers == null ) handlers = new Vector();
    handlers.add( handler );
  }

  public void removeHandler(int index) {
    if ( handlers != null )
      handlers.removeElementAt( index );
  }

  public void clear() {
    handlers.clear();
  }

  public boolean contains(Handler handler) {
    return( handlers != null ? handlers.contains( handler ) : false );
  }

  public Handler[] getHandlers() {
    return( (Handler[]) handlers.toArray() );
  }
};
