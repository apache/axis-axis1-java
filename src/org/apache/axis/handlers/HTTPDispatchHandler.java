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
import java.net.* ;
import java.util.* ;
import org.w3c.dom.* ;
import org.xml.sax.InputSource ;
import org.apache.xerces.parsers.DOMParser ;
import org.apache.axis.* ;
import org.apache.axis.transport.http.HTTPConstants ;
import org.apache.axis.utils.* ;
import org.apache.axis.message.* ;
import org.apache.axis.encoding.Base64 ;

/**
 * This is meant to be used on a SOAP Client to call a SOAP server.
 *
 * @author Doug Davis (dug@us.ibm.com)
 */
public class HTTPDispatchHandler implements Handler {
  protected Hashtable  options ;

  public void init() {
  }

  public void cleanup() {
  }

  public void invoke(MessageContext msgContext) throws AxisFault {
    Debug.Print( 1, "Enter: HTTPDispatchHandler::invoke" );
    /* Find the service we're invoking so we can grab it's options */
    /***************************************************************/
    String   targetURL = null ;
    Message  outMsg    = null ;
    String   reqEnv    = null ;

    targetURL = (String) msgContext.getProperty( Constants.MC_TRANS_URL);
    try {
      String   host ;
      int      port = 80 ;
      String   action = (String) msgContext.getProperty( Constants.MC_HTTP_SOAPACTION );
      URL      tmpURL        = new URL( targetURL );
      byte[]   buf           = new byte[4097];
      int      rc            = 0 ;

      host = tmpURL.getHost();
      if ( (port = tmpURL.getPort()) == -1 ) port = 80;

      Socket             sock = null ;

      sock    = new Socket( host, port );
      reqEnv  = (String) msgContext.getIncomingMessage().getAs("String");

      OutputStream  out  = sock.getOutputStream();
      InputStream   inp  = sock.getInputStream();
      String        otherHeaders = null ;
      String        userID = null ;
      String        passwd = null ;
      
      userID = (String) msgContext.getProperty( Constants.MC_USERID );
      passwd = (String) msgContext.getProperty( Constants.MC_PASSWORD );

      if ( userID != null )
        otherHeaders = HTTPConstants.HEADER_AUTHORIZATION + ": Basic " + 
                       Base64.encode( (userID + ":" + 
                       ((passwd == null) ? "" : passwd) ).getBytes() ) + 
                       "\n" ;

      String  header = HTTPConstants.HEADER_POST + " " + 
                         tmpURL.getPath() + " HTTP/1.0\n" +
                       HTTPConstants.HEADER_CONTENT_LENGTH + ": " + 
                                          + reqEnv.length() + "\n" +
                       HTTPConstants.HEADER_CONTENT_TYPE + ": text.xml\n" +
                       (otherHeaders == null ? "" : otherHeaders) + 
                       HTTPConstants.HEADER_SOAP_ACTION + ":" + action + "\n\n" ;

      out.write( header.getBytes() );
      out.write( reqEnv.getBytes() );

      Debug.Print( 1, "XML sent:" );
      Debug.Print( 1, header );
      Debug.Print( 1, reqEnv );

      byte       lastB=0, b ;
      int        len = 0 ;
      int        colonIndex = -1 ;
      Hashtable  headers = new Hashtable();
      String     name, value ;

      // Need to add logic for getting the version # and the return code
      // but that's for tomorrow!

      for ( ;; ) {
        if ( (b = (byte) inp.read()) == -1 ) break ;
        if ( b != '\r' && b != '\n' ) {
          if ( b == ':' ) colonIndex = len ;
          lastB = (buf[len++] = b);
        }
        else if ( b == '\r' )
          continue ;
        else {
          if ( len == 0 ) break ;
          if ( colonIndex != -1 ) {
            name = new String( buf, 0, colonIndex );
            value = new String( buf, colonIndex+1, len-1-colonIndex );
          }
          else {
            name = new String( buf, 0, len );
            value = "" ;
          }
          Debug.Print( 2, name + value );
          if ( msgContext.getProperty(Constants.MC_HTTP_STATUS_CODE) == null ) {
            // Reader status code
            int start = name.indexOf( ' ' ) + 1 ;
            int end   = name.indexOf( ' ', start ) ;
            rc = Integer.parseInt( name.substring(start, end) );
            msgContext.setProperty( Constants.MC_HTTP_STATUS_CODE, 
                                    new Integer(rc) );
            msgContext.setProperty( Constants.MC_HTTP_STATUS_MESSAGE, 
                                    name.substring(end+1));
          }
          else 
            headers.put( name, value );
          len = 0 ;
        }
      }
      if ( b != -1 ) {
        DOMParser  parser = new DOMParser();
        parser.parse( new InputSource( inp ) );
  
        outMsg = new Message( parser.getDocument(), "Document" );
        msgContext.setOutgoingMessage( outMsg );
        if ( Debug.DebugOn(2) ) {
          Debug.Print( 2, "XML received:" );
          Message m = new Message( parser.getDocument(), "Document" );
          Debug.Print( 2, (String) m.getAs( "String" ) );
        }
      }

      inp.close();
      out.close();
      sock.close();
    }
    catch( Exception e ) {
      Debug.Print( 1, e );
      if ( !(e instanceof AxisFault) ) e = new AxisFault(e);
      throw (AxisFault) e ;
    } 
    Debug.Print( 1, "Exit: HTTPDispatchHandler::invoke" );
  }

  public void undo(MessageContext msgContext) { 
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
