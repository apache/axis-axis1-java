/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package test.functional;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.configuration.DefaultEngineConfigurationFactory;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.encoding.XMLType;
import org.apache.commons.logging.Log;
import samples.transport.tcp.AdminClient;
import samples.transport.tcp.GetQuote;
import samples.transport.tcp.TCPSender;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import java.net.URL;

/** Test the stock sample code.
 */
public class TestTCPTransportSample extends TestCase {
    static Log log =
            LogFactory.getLog(TestTCPTransportSample.class.getName());

    public TestTCPTransportSample(String name) {
        super(name);
    }

    public void doTestDeploy () throws Exception {
        String[] args = { "-ltcp://localhost:8088", "samples/transport/deploy.wsdd" };
        AdminClient.main(args);
    }

    public void doTestUndeploy () throws Exception {
        String[] args = { "-ltcp://localhost:8088", "samples/stock/undeploy.wsdd" };
        AdminClient.main(args);
    }

    public void doTestStock() throws Exception {
        try {
            log.info("Testing TCP stock service...");
            GetQuote tester = new GetQuote();
            tester.getQuote(new String [] { "-ltcp://localhost:8088", "XXX" });
            String   symbol = "XXX"; // args[0] ;

            EngineConfiguration defaultConfig =
                (new DefaultEngineConfigurationFactory()).
                getClientEngineConfig();
            SimpleProvider config = new SimpleProvider(defaultConfig);
            SimpleTargetedChain c = new SimpleTargetedChain(new TCPSender());
            config.deployTransport("tcp", c);

            Service service = new Service(config);

            Call     call    = (Call) service.createCall();

            call.setTargetEndpointAddress( new URL("tcp://localhost:8088") );
            call.setOperationName( new QName("urn:xmltoday-delayed-quotes", "getQuote") );
            call.addParameter( "symbol", XMLType.XSD_STRING, ParameterMode.IN );
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
            e.printStackTrace();
            throw new AssertionFailedError("Fault returned from TCP stock test: "+e);
        }
    }

    public void testTCPTransportSample () throws Exception {
        try {
            log.info("Testing TCP transport.");

            log.info("Testing deployment...");
            doTestDeploy();
            log.info("OK!");

            log.info("Testing service...");
            doTestStock();
            log.info("OK!");

            // Commented out for now, because namespaced-based dispatch for the
            // TCPListener doesn't work yet.  Possible solutions:
            // 1. Deploy the AdminService at the WSDD namespace's name
            // 2. Build another dispatch mechanism into the TCP transport
            //
//            log.info("Testing undeployment...");
//            doTestUndeploy();
//            log.info("OK!");

            log.info("Test complete.");
        }
        catch( Exception e ) {
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

