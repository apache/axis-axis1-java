package test.wsdd;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.axis.Chain;
import org.apache.axis.Handler;
import org.apache.axis.TargetedChain;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.server.AxisServer;

import java.io.InputStream;

/**
 *  Positive test of basic structure of a WSDD document
 */ 
public class TestStructure extends TestCase
{
    static final String INPUT_FILE = "testStructure1.wsdd";
    AxisServer server;

    public TestStructure (String name) 
    {
        super(name);
    }

    public static Test suite() 
    {
        return new TestSuite(TestStructure.class);
    }

    protected void setUp() 
    {
        InputStream is = getClass().getResourceAsStream(INPUT_FILE);
        FileProvider provider = new FileProvider(is);
        server = new AxisServer(provider);
    }

    public static void main(String[] args) throws Exception 
    {
        TestStructure tester = new TestStructure("TestStructure");
        tester.setUp();
        tester.testServiceBackReference();
    }
    
    public void testChainAnonymousHandler() throws Exception
    {
        Chain chainOne = (Chain) server.getHandler("chain.one");
        assertNotNull("chain.one should be non-null!", chainOne);

        Handler chainOne_handlers[] = chainOne.getHandlers();
        assertNotNull("chain.one/handlers should be non-null!", 
                      chainOne_handlers);
        assertTrue("chain.one should have exactly 1 handler!",
                (1 == chainOne_handlers.length));

        Handler chainOne_handler = chainOne_handlers[0];
        assertNotNull("chain.one's handler should be non-null!",
                      chainOne_handler);
        assertTrue("chain.one's handler should be a JWSHandler!",
                (chainOne_handler instanceof 
                 org.apache.axis.handlers.JWSHandler));
    }

    public void testServiceBackReference() throws Exception
    {
        SOAPService serviceOne = (SOAPService)server.getService("service.one");
        assertNotNull("service.one should be non-null!", serviceOne);

        Chain serviceOne_responseFlow = (Chain)serviceOne.getResponseHandler();
        assertNotNull("service.two/responseFlow should be non-null!",
                      serviceOne_responseFlow);

        Handler serviceOne_responseFlow_handlers[] = 
                             serviceOne_responseFlow.getHandlers();
        assertNotNull("service.one/responseFlow/handlers should be non-null!",
                        serviceOne_responseFlow_handlers);
        assertTrue("service.one should have exactly 1 handler!",
                (1 == serviceOne_responseFlow_handlers.length));

        Handler serviceOne_responseFlow_handler = 
                        serviceOne_responseFlow_handlers[0];
        assertNotNull("service.one's handler should be non-null!",
                      serviceOne_responseFlow_handler);
        assertTrue("service.one's handler should be a RPCProvider!",
                    (serviceOne_responseFlow_handler instanceof
                         org.apache.axis.providers.java.RPCProvider));

        Handler serviceOne_handler_byName = server.getHandler("BackReference");
        assertTrue("service.one's 'BackReference' should be same as directly accessed 'BR'!",
                   (serviceOne_responseFlow_handler == 
                            serviceOne_handler_byName));

         /*******************************************************
          <service name="service.two" provider="java:MSG">
           <requestFlow>
             <handler type="BackReference"/>
           </requestFlow>
          </service>
         ******************************************************/
        SOAPService serviceTwo = null;
        serviceTwo = (SOAPService) server.getService("service.two");
        assertTrue("service.two should be non-null!",
                   (null != serviceTwo));

        Chain serviceTwo_requestFlow = (Chain) serviceTwo.getRequestHandler();
        assertTrue("service.two/requestFlow should be non-null!",
                (null != serviceTwo_requestFlow));

        Handler serviceTwo_requestFlow_handlers[] = 
                                serviceTwo_requestFlow.getHandlers();
        assertTrue("service.two/requestFlow/handlers should be non-null!",
                (null != serviceTwo_requestFlow_handlers));
        assertTrue("service.two should have exactly 1 handler!",
                (1 == serviceTwo_requestFlow_handlers.length));

        Handler serviceTwo_requestFlow_handler = 
                                serviceTwo_requestFlow_handlers[0];
        assertTrue("service.two's handler should be non-null!",
                (null != serviceTwo_requestFlow_handler));
        assertTrue("service.two's handler should be a RPCProvider!",
                    (serviceTwo_requestFlow_handler instanceof
                                org.apache.axis.providers.java.RPCProvider));

        assertTrue("service.two's 'BackReference' should be same as service.one's!",
                   (serviceTwo_requestFlow_handler == 
                            serviceOne_responseFlow_handler));
    }

    public void testTransportForwardReference()
            throws Exception
    {
        TargetedChain transportOne = 
                        (TargetedChain)server.getTransport("transport.one");
        assertNotNull("transport.one should be non-null!", transportOne);

        Chain transportOne_responseFlow = 
                        (Chain)transportOne.getResponseHandler();
        assertNotNull("transport.two/responseFlow should be non-null!",
                      transportOne_responseFlow);

        Handler transportOne_responseFlow_handlers[] = 
                        transportOne_responseFlow.getHandlers();
        assertNotNull("transport.one/responseFlow/handlers should be non-null!",
                      transportOne_responseFlow_handlers);
        assertTrue("transport.one should have exactly 1 handler!",
                (1 == transportOne_responseFlow_handlers.length));

        Handler transportOne_responseFlow_handler =
                        transportOne_responseFlow_handlers[0];
        assertNotNull("transport.one's handler should be non-null!",
                      transportOne_responseFlow_handler);
        assertTrue("transport.one's handler should be a URLMapper!",
                    (transportOne_responseFlow_handler instanceof 
                                org.apache.axis.handlers.http.URLMapper));

        Handler transportOne_handler_byName = 
                server.getHandler("ForwardReference");
        assertTrue("transport.one's 'ForwardReference' should be same as directly accessed 'BR'!",
                   (transportOne_responseFlow_handler == 
                            transportOne_handler_byName));

        TargetedChain transportTwo = 
                (TargetedChain)server.getTransport("transport.two");
        assertNotNull("transport.two should be non-null!", transportTwo);

        Chain transportTwo_requestFlow = (Chain) transportTwo.getRequestHandler();
        assertNotNull("transport.two/requestFlow should be non-null!",
                      transportTwo_requestFlow);

        Handler transportTwo_requestFlow_handlers[] = 
                transportTwo_requestFlow.getHandlers();
        assertNotNull("transport.two/requestFlow/handlers should be non-null!",
                      transportTwo_requestFlow_handlers);
        assertTrue("transport.two should have exactly 1 handler!",
                (1 == transportTwo_requestFlow_handlers.length));

        Handler transportTwo_requestFlow_handler = transportTwo_requestFlow_handlers[0];
        assertNotNull("transport.two's handler should be non-null!",
                      transportTwo_requestFlow_handler);
        assertTrue("transport.two's handler should be a URLMapper!",
                    (transportTwo_requestFlow_handler instanceof 
                        org.apache.axis.handlers.http.URLMapper));

        assertTrue("transport.two's 'ForwardReference' should be same as transport.one's!",
                   (transportTwo_requestFlow_handler == transportOne_responseFlow_handler));
    }
}
