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

package org.apache.axis ;

import java.util.* ;
import org.apache.axis.* ;
import org.apache.axis.utils.* ;
import org.apache.axis.handlers.* ;

import org.w3c.dom.* ;

/**
 *
 * @author Doug Davis (dug@us.ibm.com)
 */
public class SimpleTargetedChain extends BasicHandler implements TargetedChain  {
  protected Handler    requestHandler ;
  protected Handler    pivotHandler ;
  protected Handler    responseHandler ;

  public void init() { 
    if ( requestHandler   != null )   requestHandler.init();
    if ( pivotHandler != null ) pivotHandler.init();
    if ( responseHandler  != null )  responseHandler.init();
  }

  public void cleanup() {
    if ( requestHandler   != null )   requestHandler.cleanup();
    if ( pivotHandler != null ) pivotHandler.cleanup();
    if ( responseHandler  != null )  responseHandler.cleanup();
  }

  /**
   * Invoke the request chain, pivot handler and response chain.  If there's
   * a fault we need to make sure that we undo any completed handler
   * that has been successfully invoked and then rethrow the fault.
   */
  public void invoke(MessageContext msgContext) throws AxisFault {
    Debug.Print( 1, "Enter: SimpleTargetedChain::invoke" );
    if ( requestHandler != null ) requestHandler.invoke( msgContext );
    try {
      if ( pivotHandler != null ) pivotHandler.invoke( msgContext );
    }
    catch( Exception e ) {
      Debug.Print( 1, e );
      if ( !(e instanceof AxisFault ) )
        e = new AxisFault( e );
      if ( requestHandler != null ) requestHandler.undo( msgContext );
      throw (AxisFault) e ;
    }
    try {
      if ( responseHandler != null )  responseHandler.invoke( msgContext );
    }
    catch( Exception e ) {
      Debug.Print( 1, e );
      if ( !(e instanceof AxisFault ) )
        e = new AxisFault( e );
      if ( pivotHandler != null ) pivotHandler.undo( msgContext );
      if ( requestHandler   != null )   requestHandler.undo( msgContext );
      throw (AxisFault) e ;
    }
    Debug.Print( 1, "Exit: SimpleTargetedChain::invoke" );
  }

  /**
   * Undo all of the work - in reverse order.
   */
  public void undo(MessageContext msgContext) {
    Debug.Print( 1, "Enter: SimpleTargetedChain::undo" );
    if ( responseHandler   != null )   responseHandler.undo( msgContext );
    if ( pivotHandler  != null )  pivotHandler.undo( msgContext );
    if ( requestHandler    != null )    requestHandler.undo( msgContext );
    Debug.Print( 1, "Exit: SimpleTargetedChain::undo" );
  }

  public boolean canHandleBlock(QName qname) {
    return( (requestHandler==null)   ? false : requestHandler.canHandleBlock(qname) ||
            (pivotHandler==null) ? false : pivotHandler.canHandleBlock(qname) ||
            (responseHandler==null)  ? false : responseHandler.canHandleBlock(qname) );
  }

  public Handler getRequestHandler() { return( requestHandler ); }

  public void setRequestHandler(Handler reqHandler)
  {
    requestHandler = reqHandler;
  }

  public Handler getPivotHandler() { return( pivotHandler ); }

  public void setPivotHandler(Handler handler) { pivotHandler = handler ; }

  public Handler getResponseHandler() { return( responseHandler ); }

  public void setResponseHandler(Handler respHandler)
  {
    responseHandler = respHandler;
  }
  
  public void clear() {
    requestHandler = null ;
    pivotHandler = null ;
    responseHandler = null ;
  }

  public Element getDeploymentData(Document doc) {
    Debug.Print( 1, "Enter: SimpleTargetedChain::getDeploymentData" );

    Element   root = doc.createElement( "chain" );
    fillInDeploymentData(root);

    Debug.Print( 1, "Exit: SimpleTargetedChain::getDeploymentData" );
    return( root );
  }
  
  /**
   * Used by subclasses (i.e. SOAPService) to fill in deployment
   * data into an Element which might not be named "chain".
   * 
   * @param root the Element to fill in with deployment data.
   */
  public void fillInDeploymentData(Element root)
  {
    Document doc = root.getOwnerDocument();
    StringBuffer str  = new StringBuffer();
    Handler      h ;

    if ( requestHandler != null ) {
      if (requestHandler instanceof Chain) {
        Handler[]  handlers = ((Chain)requestHandler).getHandlers();
        str = new StringBuffer();
        for ( int i = 0 ; i < handlers.length ; i++ ) {
          h = (Handler) handlers[i];
          if ( i != 0 ) str.append(",");
          str.append( h.getName() );
        }
      } else {
        str.append(requestHandler.getName());
      }
      root.setAttribute( "request", str.toString() );
    }
    if ( pivotHandler != null ) {
      root.setAttribute( "pivot", pivotHandler.getName() );
    }
    if ( responseHandler != null ) {
      if (responseHandler instanceof Chain) {
        Handler[]  handlers = ((Chain)responseHandler).getHandlers();
        str = new StringBuffer();
        for ( int i = 0 ; i < handlers.length ; i++ ) {
          h = (Handler) handlers[i];
          if ( i != 0 ) str.append(",");
          str.append( h.getName() );
        }
      } else {
        str.append(responseHandler.getName());
      }
      root.setAttribute( "response", str.toString() );
    }

    options = this.getOptions();
    if ( options != null ) {
      Enumeration e = options.keys();
      while ( e.hasMoreElements() ) {
        String k = (String) e.nextElement();
        Object v = options.get(k);
        Element e1 = doc.createElement( "option" );
        e1.setAttribute( "name", k );
        e1.setAttribute( "value", v.toString() );
        root.appendChild( e1 );
      }
    }
  }

};
