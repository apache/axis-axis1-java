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

package test.functional;

import java.net.*;
import java.io.*;
import java.util.*;

import org.apache.axis.AxisFault ;
import samples.transport.tcp.AdminClient;
import samples.transport.tcp.TCPTransport;
import org.apache.axis.client.ServiceClient;
import org.apache.axis.encoding.ServiceDescription;
import org.apache.axis.encoding.SOAPTypeMappingRegistry;

import junit.framework.TestCase;

/** Test the stock sample code.
 */
public class TestTCPTransportSample extends TestCase {
    
    public TestTCPTransportSample(String name) {
        super(name);
    }
    
    public void doTestDeploy () throws Exception {
        String[] args = { "-p8088", "samples/stock/deploy.xml" };
        new AdminClient().doAdmin(args);
    }
    
    public void doTestUndeploy () throws Exception {
        String[] args = { "-p8088", "samples/stock/undeploy.xml" };
        new AdminClient().doAdmin(args);
    }
    
    public void doTestStock() throws Exception {
        try {
            System.out.println("Testing TCP stock service...");
            String   symbol = "XXX"; // args[0] ;
            URL url = null;
            // parse host, port out of URL by hand
            // what to do about that URL format issue.....
            try {
                url = new URL("http://localhost:8088"); // (opts.getURL());
            } catch (IOException ex) {
                System.err.println("URL "+url+" hosed: "+ex);
                System.exit(1);
            }
            
            ServiceClient call   = new ServiceClient
                ( new TCPTransport(url.getHost(), ""+url.getPort()) );
            
            // reconstruct URL
            ServiceDescription sd = new ServiceDescription("stockQuotes", true);
            sd.addOutputParam("return", SOAPTypeMappingRegistry.XSD_FLOAT);
            call.setServiceDescription(sd);
            
            Float res = new Float(0.0F);
            //      for (int i=0; i<count; i++) {
            Object ret = call.invoke(
                "urn:xmltoday-delayed-quotes", "getQuote",
                new Object[] {symbol} );
            if (ret instanceof Float) {
                res = (Float) ret;
                // System.out.println( symbol + ": " + res );
                assertEquals("TestTCPTransportSample: stock price should be 55.25", res.floatValue(), 55.25, 0.000001);
            } else {
                throw new Exception("Bad return value from TCP stock test: "+ret);
            }
        }
        
        //    }
        catch( Exception e ) {
            if ( e instanceof AxisFault ) ((AxisFault)e).dump();
            e.printStackTrace();
            throw new Exception("Fault returned from TCP stock test: "+e);
        }
    }
    
    public void testTCPTransportSample () throws Exception {
        try {
            System.out.println("Testing TCP transport.");
            System.out.println("Testing deployment...");
            doTestDeploy();
            System.out.println("Testing service...");
            doTestStock();
            System.out.println("Testing undeployment...");
            doTestUndeploy();
            System.out.println("Test complete.");
        }
        catch( Exception e ) {
            if ( e instanceof AxisFault ) ((AxisFault)e).dump();
            e.printStackTrace();
            throw new Exception("Fault returned from test: "+e);
        }
    }
    
}

