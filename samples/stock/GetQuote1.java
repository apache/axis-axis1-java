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

package samples.stock ;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.utils.Options;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import java.net.URL;

/**
 * This version of the ever so popular GetQuote shows how to use the
 * Axis client APIs with and without WSDL.  The first flavor (getQuote1)
 * will use WSDL to prefill all of the data about the remote service.
 * The second one (getQuote2) will do it all manually.  Either way the
 * service is invoked it should produce the exact same request XML and
 * of course same results.
 *
 * This sample supports the use of the standard options too (-p ...)
 *
 * @author Doug Davis (dug@us.ibm.com.com)
 */
public class GetQuote1 {
    public  String   symbol ;
    
    /**
     * This will use the WSDL to prefill all of the info needed to make
     * the call.  All that's left is filling in the args to invoke().
     */
    public float getQuote1(String args[]) throws Exception {
      Options  opts = new Options( args );

      args = opts.getRemainingArgs();

      if ( args == null ) {
        System.err.println( "Usage: GetQuote <symbol>" );
        System.exit(1);
      }

      /* Define the service QName and port QName */
      /*******************************************/
      QName servQN = new QName("urn:xmltoday-delayed-quotes","GetQuoteService");
      QName portQN = new QName("urn:xmltoday-delayed-quotes","GetQuote");

      /* Now use those QNames as pointers into the WSDL doc */
      /******************************************************/
      Service service = new Service( new URL("file:GetQuote.wsdl"), servQN );
      Call    call    = (Call) service.createCall( portQN, "getQuote" );

      /* Strange - but allows the user to change just certain portions of */
      /* the URL we're gonna use to invoke the service.  Useful when you  */
      /* want to run it thru tcpmon (ie. put  -p81 on the cmd line).      */
      /********************************************************************/
      opts.setDefaultURL( call.getTargetEndpointAddress() );
      call.setTargetEndpointAddress( new URL(opts.getURL()) );

      /* Define some service specific properties */
      /*******************************************/
      call.setUsername( opts.getUser() );
      call.setPassword( opts.getPassword() );

      /* Get symbol and invoke the service */
      /*************************************/
        Object result = call.invoke( new Object[] { symbol = args[0] } );

      return( ((Float) result).floatValue() );
    }

    /**
     * This will do everything manually (ie. no WSDL).
     */
    public float getQuote2(String args[]) throws Exception {
      Options  opts    = new Options( args );

      args = opts.getRemainingArgs();

      if ( args == null ) {
        System.err.println( "Usage: GetQuote <symbol>" );
        System.exit(1);
      }

      /* Create default/empty Service and Call object */
      /************************************************/
      Service  service = new Service();
      Call     call    = (Call) service.createCall();

      /* Strange - but allows the user to change just certain portions of */
      /* the URL we're gonna use to invoke the service.  Useful when you  */
      /* want to run it thru tcpmon (ie. put  -p81 on the cmd line).      */
      /********************************************************************/
      opts.setDefaultURL( "http://localhost:8080/axis/servlet/AxisServlet" );

      /* Set all of the stuff that would normally come from WSDL */
      /***********************************************************/
      call.setTargetEndpointAddress( new URL(opts.getURL()) );
      call.setUseSOAPAction( true );
      call.setSOAPActionURI( "getQuote" );
      call.setEncodingStyle( "http://schemas.xmlsoap.org/soap/encoding/" );
      call.setOperationName( new QName("urn:xmltoday-delayed-quotes", "getQuote") );
      call.addParameter( "symbol", XMLType.XSD_STRING, ParameterMode.IN );
      call.setReturnType( XMLType.XSD_FLOAT );

      /* Define some service specific properties */
      /*******************************************/
      call.setUsername( opts.getUser() );
      call.setPassword( opts.getPassword() );

      /* Get symbol and invoke the service */
      /*************************************/
      Object result = call.invoke( new Object[] { symbol = args[0] } );

      return( ((Float) result).floatValue() );
    }

    /**
     * This will use the WSDL to prefill all of the info needed to make
     * the call.  All that's left is filling in the args to invoke().
     */
    public float getQuote3(String args[]) throws Exception {
      Options  opts = new Options( args );

      args = opts.getRemainingArgs();

      if ( args == null ) {
        System.err.println( "Usage: GetQuote <symbol>" );
        System.exit(1);
      }

      /* Define the service QName and port QName */
      /*******************************************/
      QName servQN = new QName("urn:xmltoday-delayed-quotes","GetQuoteService");
      QName portQN = new QName("urn:xmltoday-delayed-quotes","GetQuote");

      /* Now use those QNames as pointers into the WSDL doc */
      /******************************************************/
      Service service = new Service( new URL("file:GetQuote.wsdl"), servQN );
      Call    call    = (Call) service.createCall( portQN, "getQuote" );

      /* Strange - but allows the user to change just certain portions of */
      /* the URL we're gonna use to invoke the service.  Useful when you  */
      /* want to run it thru tcpmon (ie. put  -p81 on the cmd line).      */
      /********************************************************************/
      opts.setDefaultURL( call.getTargetEndpointAddress() );
      call.setTargetEndpointAddress( new URL(opts.getURL()) );

      /* Define some service specific properties */
      /*******************************************/
      call.setUsername( opts.getUser() );
      call.setPassword( opts.getPassword() );

      /* Get symbol and invoke the service */
      /*************************************/
      Object result = call.invoke( new Object[] { symbol = args[0] } );
      result = call.invoke( new Object[] { symbol = args[0] } );

      /* Reuse the call object to call the test method */
      /*************************************************/
      call.setOperation( portQN, "test" );
      call.setReturnType( XMLType.XSD_STRING );

      System.out.println( call.invoke(new Object[]{}) );

      return( ((Float) result).floatValue() );
    }

    public static void main(String args[]) {
      try {
          String    save_args[] = new String[args.length];
          float     val ;
          GetQuote1 gq  = new GetQuote1();

          /* Call the getQuote() that uses the WDSL */
          /******************************************/
          System.out.println("Using WSDL");
          System.arraycopy( args, 0, save_args, 0, args.length );
          val = gq.getQuote1( args );
          System.out.println( gq.symbol + ": " + val );

          /* Call the getQuote() that does it all manually */
          /*************************************************/
          System.out.println("Manually");
          System.arraycopy( save_args, 0, args, 0, args.length );
          val = gq.getQuote2( args );
          System.out.println( gq.symbol + ": " + val );

          /* Call the getQuote() that uses Axis's generated WSDL */
          /*******************************************************/
          System.out.println("WSDL + Reuse Call");
          System.arraycopy( save_args, 0, args, 0, args.length );
          val = gq.getQuote3( args );
          System.out.println( gq.symbol + ": " + val );
      }
      catch( Exception e ) {
          e.printStackTrace();
      }
    }
};
