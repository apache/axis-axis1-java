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

package org.apache.axis.client ;

import java.util.* ;
import org.jdom.* ;
import org.apache.axis.* ;
import org.apache.axis.message.* ;
import org.apache.axis.handlers.* ;
import org.apache.axis.utils.* ;
import org.apache.axis.* ;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.transport.http.HTTPDispatchHandler;

/**
 * This class is meant to be the interface that client/requestor code
 * uses to access the SOAP server.  In this class, we'll use HTTP to
 * connect to the server and send an RPC SOAP request.
 *
 * @author Doug Davis (dug@us.ibm.com)
 */


// Need to add proxy, ssl.... other cool things - but it's a start
// Only supports String

public class HTTPCall {
  private String  url ;
  private String  action ;
  private String  userID ;
  private String  passwd ;

  // For testing
  public  boolean doLocal = false ;

  public HTTPCall() {
  }

  public HTTPCall(String url) {
    this.url = url ;
  }

  public HTTPCall(String url, String action) {
    setURL( url );
    setAction( action );
  }

  public void setURL( String url ) {
    this.url = url ;
  }

  public void setAction( String action ) {
    this.action = action ;
  }

  public void setUserID( String user ) {
    userID = user ;
  }

  public String getUserID() {
    return( userID );
  }

  public void setPassword( String pwd ) {
    passwd = pwd ;
  }

  public String getPassword() {
    return( passwd );
  }

  public static Object invoke(String url, String act, String m, Object[] args) 
      throws AxisFault
  {
    HTTPCall  ahc = new HTTPCall();
    ahc.setURL( url );
    ahc.setAction( act );
    return( ahc.invoke( m, args ) );
  }

  public Object invoke( String method, Object[] args ) throws AxisFault {
    // quote = HTTPCall.invoke( "getQuote", Object[] { "IBM" } );
    Debug.Print( 1, "Enter: HTTPCall.invoke" );
    RPCBody              body   = new RPCBody( method, args );
    SOAPEnvelope         reqEnv = new SOAPEnvelope();
    SOAPEnvelope         resEnv = null ;
    HTTPMessage          hMsg   = new HTTPMessage( url, action );
    Message              reqMsg = new Message( reqEnv, "SOAPEnvelope" );
    Message              resMsg = null ;
    MessageContext       msgContext = new MessageContext( reqMsg );
    Vector               resBodies = null ;
    Vector               resArgs = null ;
    RPCArg               arg  = null ;
    Object               result = null ;

    hMsg.setUserID( userID );
    hMsg.setPassword( passwd );

    // for testing - skip HTTP layer
    hMsg.doLocal = this.doLocal ;

    body.setPrefix( "m" );
    body.setNamespaceURI( action );
    reqEnv.addBody( body.getAsSOAPBody() );

    try {
      hMsg.invoke( msgContext );
    }
    catch( Exception e ) {
      Debug.Print( 1, e );
      if ( !(e instanceof AxisFault ) ) e = new AxisFault( e );
      throw (AxisFault) e ;
    }

    resMsg = msgContext.getResponseMessage();
    Document doc = (Document) resMsg.getAs("Document");
    body = new RPCBody( doc.getRootElement() );
    resArgs = body.getArgs();
    if ( args != null && resArgs.size() > 0 )
      result = (String) ((RPCArg) resArgs.get(0)).getValue() ;
    Debug.Print( 1, "Exit: HTTPCall.invoke" );
    return( result );
  }

}
