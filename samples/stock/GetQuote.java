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

package samples.stock ;

import java.net.*;
import java.io.*;
import java.util.*;

import org.apache.axis.AxisFault ;
import org.apache.axis.client.ServiceClient ;
import org.apache.axis.client.Transport ;
import org.apache.axis.client.http.HTTPTransport ;
import org.apache.axis.utils.Debug ;
import org.apache.axis.utils.Options ;
import org.apache.axis.utils.QName ;
import org.apache.axis.encoding.ServiceDescription;
import org.apache.axis.encoding.SOAPTypeMappingRegistry;

/**
 *
 * @author Doug Davis (dug@us.ibm.com.com)
 */
public class GetQuote {
    public  String symbol ;
    
  // helper function; does all the real work
    public float getQuote (String args[]) throws Exception {
      Options opts = new Options( args );

      Debug.setDebugLevel( opts.isFlagSet( 'd' ) );

      args = opts.getRemainingArgs();

      if ( args == null ) {
        System.err.println( "Usage: GetQuote <symbol>" );
        System.exit(1);
      }

      String action = "urn:xmltoday-delayed-quotes";
      symbol = args[0] ;
      ServiceClient call = new ServiceClient
            (new HTTPTransport(opts.getURL(), action));
      ServiceDescription sd = new ServiceDescription("stockQuotes", true);
      sd.addOutputParam("return", SOAPTypeMappingRegistry.XSD_FLOAT);
      call.setServiceDescription(sd);
      
      if ( opts.isFlagSet('t') > 0 ) call.doLocal = true ;

      call.set( Transport.USER, opts.getUser() );
      call.set( Transport.PASSWORD, opts.getPassword() );

      // useful option for profiling - perhaps we should remove before
      // shipping?
      String countOption = opts.isValueSet('c');
      int count=1;
      if ( countOption != null) {
        count=Integer.valueOf(countOption).intValue();
        System.out.println("Iterating " + count + " times");
      }

      Float res = new Float(0.0F);
      for (int i=0; i<count; i++) {
        // the namespace of the call should be the service.
        // ...according to Glen... -- RobJ
          Object ret = call.invoke(
          action, "getQuote", new Object[] {symbol} );
          if (ret instanceof String) {
              System.out.println("Received problem response from server: "+ret);
              throw new AxisFault("", (String)ret, null, null);
          }
        res = (Float) ret;
      }

        return res.floatValue();
    }

  public static void main(String args[]) {
    try {
        GetQuote gq = new GetQuote();
        float val = gq.getQuote(args);
        // args array gets side-effected
        System.out.println(gq.symbol + ": " + val);
    }
    catch( Exception e ) {
        if ( e instanceof AxisFault ) {
            ((AxisFault)e).dump();
        } else
            e.printStackTrace();
    }
  }
  
  public GetQuote () {
  };

};
