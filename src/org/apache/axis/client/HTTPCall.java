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

package org.apache.axis.client ;

import java.util.* ;
import org.apache.axis.* ;
import org.apache.axis.message.* ;
import org.apache.axis.handlers.* ;

/**
 *
 * @author Doug Davis (dug@us.ibm.com)
 */


// Need to add proxy, ssl.... other cool things - but it's a start
// Only supports String

public class HTTPCall {
  private String  url ;
  private String  action ;

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

  public static Object invoke(String url, String act, String m, Object[] args) {
    HTTPCall  ahc = new HTTPCall();
    ahc.setURL( url );
    ahc.setAction( act );
    return( ahc.invoke( m, args ) );
  }

  public Object invoke( String method, Object[] args ) {
    // quote = HTTPCall.invoke( "getQuote", Object[] { "IBM" } );
    RPCBody              body   = new RPCBody( method, args );
    SOAPEnvelope         reqEnv = new SOAPEnvelope();
    SOAPEnvelope         resEnv = null ;
    HTTPDispatchHandler  client = new HTTPDispatchHandler();
    Message              reqMsg = new Message( reqEnv, "SOAPEnvelope" );
    Message              resMsg = null ;
    MessageContext       msgContext = new MessageContext( reqMsg );
    Vector               resBodies = null ;
    Vector               resArgs = null ;
    RPCArg               arg ;

    body.setNamespace( "m" );
    body.setNamespaceURI( action );
    reqEnv.addBody( body );
    msgContext.setProperty( "HTTP_URL", url );   // horrible name!
    msgContext.setProperty( "HTTP_ACTION", action );   // horrible name!
    try {
      client.init();
      client.invoke( msgContext );
      client.cleanup();
    }
    catch( AxisFault fault ) {
      System.err.println( fault );
      System.exit(1); /// ha!
    }

    resMsg = msgContext.getOutgoingMessage();
    resEnv = (SOAPEnvelope) resMsg.getAs( "SOAPEnvelope" );
    resBodies = resEnv.getAsRPCBody();
    if ( resBodies == null || resBodies.size() == 0 ) return( null );
    body = (RPCBody) resBodies.get( 0 );
    resArgs = body.getArgs();
    arg = (RPCArg) resArgs.get(0);
    return( (String) arg.getValue() );
  }

}
