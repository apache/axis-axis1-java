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

package samples.transport.tcp;

import java.io.* ;
import java.net.* ;
import java.util.* ;
import java.lang.reflect.*;

import org.apache.axis.* ;
import org.apache.axis.utils.* ;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeader;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.transport.http.NonBlockingBufferedInputStream;
import org.apache.axis.encoding.Base64 ;

import org.w3c.dom.* ;

/**
 * This is meant to be used on a SOAP Client to call a SOAP server.
 *
 * @author Rob Jellinghaus (robj@unrealities.com)
 * @author Doug Davis (dug@us.ibm.com)
 */
public class TCPSender extends BasicHandler {
  public void invoke(MessageContext msgContext) throws AxisFault {
    Debug.Print( 0, "Enter: TCPSender::invoke" );
    /* Find the service we're invoking so we can grab it's options */
    /***************************************************************/
    String   targetURL = null ;
    Message  outMsg    = null ;
    String   reqEnv    = null ;

    targetURL = msgContext.getStrProp( MessageContext.TRANS_URL);
    try {
      String   host   = msgContext.getStrProp(TCPTransport.HOST);
      int      port   = Integer.parseInt(msgContext.getStrProp(TCPTransport.PORT));
      byte[]   buf    = new byte[4097];
      int      rc     = 0 ;

      Socket             sock = null ;

      sock    = new Socket( host, port );
      Debug.Print( 1, "Created an insecure HTTP connection");

      reqEnv  = (String) msgContext.getRequestMessage().getAsString();
      
      //System.out.println("Msg: " + reqEnv);

      BufferedInputStream inp = new BufferedInputStream(sock.getInputStream());
      OutputStream  out  = sock.getOutputStream();
      
      byte[] bytes = reqEnv.getBytes();
      String length = "" + bytes.length + "\r\n";
      out.write(length.getBytes());
      out.write( bytes );
      out.flush();

      Debug.Print( 1, "XML sent:" );
      Debug.Print( 1, "---------------------------------------------------");
      Debug.Print( 1, reqEnv );

      if ( Debug.getDebugLevel() > 8 ) {
        // Special case - if the debug level is this high then something
        // really bad must be going on - so just dump the input stream
        // to stdout.
        byte b;
        while ( (b = (byte) inp.read()) != -1 )
          System.err.print((char)b);
        System.err.println("");
      }

      outMsg = new Message( inp );
      if (Debug.getDebugLevel() > 0) {
        Debug.Print( 1, "\nNo Content-Length" );
        Debug.Print( 1, "\nXML received:" );
        Debug.Print( 1, "-----------------------------------------------");
        Debug.Print( 1, (String) outMsg.getAsString() );
      }
      
      msgContext.setResponseMessage( outMsg );
    }
    catch( Exception e ) {
      Debug.Print( 1, e );
      e.printStackTrace();
      if ( !(e instanceof AxisFault) ) e = new AxisFault(e);
      throw (AxisFault) e ;
    }
    Debug.Print( 0, "Exit: TCPSender::invoke" );
  }

  public void undo(MessageContext msgContext) {
    Debug.Print( 1, "Enter: TCPSender::undo" );
    Debug.Print( 1, "Exit: TCPSender::undo" );
  }
};
