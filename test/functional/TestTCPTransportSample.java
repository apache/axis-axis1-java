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

package test.functional;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import org.apache.axis.AxisFault;
import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.log4j.Category;
import samples.transport.tcp.AdminClient;
import samples.transport.tcp.GetQuote;
import samples.transport.tcp.TCPSender;

import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.namespace.QName;

import java.net.URL;

/** Test the stock sample code.
 */
public class TestTCPTransportSample extends TestCase {
    static Category category =
            Category.getInstance(TestTCPTransportSample.class.getName());

    public TestTCPTransportSample(String name) {
        super(name);
    }

    public void doTestDeploy () throws Exception {
        String[] args = { "-ltcp://localhost:8088", "samples/transport/deploy.xml" };
        AdminClient.main(args);
    }

    public void doTestUndeploy () throws Exception {
        String[] args = { "-ltcp://localhost:8088", "samples/stock/undeploy.wsdd" };
        AdminClient.main(args);
    }

    public void doTestStock() throws Exception {
        try {
            category.info("Testing TCP stock service...");
            GetQuote tester = new GetQuote();
            tester.getQuote(new String [] { "-ltcp://localhost:8088", "XXX" });
            String   symbol = "XXX"; // args[0] ;

            Service  service = new Service();
            Call     call    = (Call) service.createCall();

            SimpleTargetedChain c = new SimpleTargetedChain(new TCPSender());
            service.getEngine().deployTransport("tcp", c);

            call.setTargetEndpointAddress( new URL("tcp://localhost:8088") );
            call.setOperationName( new QName("urn:xmltoday-delayed-quotes", "getQuote") );
            call.addParameter( "symbol", XMLType.XSD_STRING, ParameterMode.PARAM_MODE_IN );
            call.setReturnType( XMLType.XSD_FLOAT );

            Object ret = call.invoke(
                "urn:xmltoday-delayed-quotes", "getQuote",
                new Object[] {symbol} );
            if (ret instanceof Float) {
                Float res = (Float) ret;
                assertEquals("TestTCPTransportSample: stock price should be 55.25 +/- 0.000001", res.floatValue(), 55.25, 0.000001);
            } else {
                throw new AssertionFailedError("Bad return value from TCP stock test: "+ret);
            }
        }

        //    }
        catch( Exception e ) {
            if ( e instanceof AxisFault ) ((AxisFault)e).dump();
            e.printStackTrace();
            throw new AssertionFailedError("Fault returned from TCP stock test: "+e);
        }
    }

    public void testTCPTransportSample () throws Exception {
        try {
            category.info("Testing TCP transport.");

            category.info("Testing deployment...");
            doTestDeploy();
            category.info("OK!");

            category.info("Testing service...");
            doTestStock();
            category.info("OK!");

            // Commented out for now, because namespaced-based dispatch for the
            // TCPListener doesn't work yet.  Possible solutions:
            // 1. Deploy the AdminService at the WSDD namespace's name
            // 2. Build another dispatch mechanism into the TCP transport
            //
//            category.info("Testing undeployment...");
//            doTestUndeploy();
//            category.info("OK!");

            category.info("Test complete.");
        }
        catch( Exception e ) {
            if ( e instanceof AxisFault ) ((AxisFault)e).dump();
            e.printStackTrace();
            throw new AssertionFailedError("Fault returned from test: "+e);
        }
    }

    public static void main(String [] args)
    {
      TestTCPTransportSample tester = new TestTCPTransportSample("TCP test");
      try {
        tester.testTCPTransportSample();
      } catch (Exception e) {
      }
    }

}

