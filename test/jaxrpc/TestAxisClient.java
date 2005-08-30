package test.jaxrpc;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;

import org.apache.axis.AxisFault;
import org.apache.axis.ConfigurationException;
import org.apache.axis.Constants;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.client.AxisClient;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.configuration.EngineConfigurationFactoryFinder;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDTransport;
import org.apache.axis.handlers.soap.SOAPService;


/**
 *
 *
 * @author Guillaume Sauthier
 */
public class TestAxisClient extends AJAXRPC {


    /**
     *
     *
     * @author Guillaume Sauthier
     */
    protected class AxisFaultWSDDTransport extends WSDDTransport {

        /**
         * @see org.apache.axis.deployment.wsdd.WSDDDeployableItem#makeNewInstance(org.apache.axis.EngineConfiguration)
         */
        public Handler makeNewInstance(EngineConfiguration registry) throws ConfigurationException {
            return new MockServiceHandler();
        }
        /**
         * @see org.apache.axis.deployment.wsdd.WSDDDeployableItem#getQName()
         */
        public QName getQName() {
            return new QName("faulter");
        }
}
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    /**
     * All Handlers in Chain return true for handleRequest and handleResponse
     * <p/>
     * <p/>
     * * Expected Chain invocation sequence looks like.....
     * H0.handleRequest
     * H1.handleRequest
     * H2.handleRequest
     * H2.handleResponse
     * H1.handleResponse
     * H0.handleResponse
     * 
     * @throws Exception 
     */
    public void testPositiveCourseFlow() throws Exception {

        TestHandlerInfoChainFactory factory = buildInfoChainFactory();
        SOAPService soapService = new SOAPService();
        soapService.setOption(Constants.ATTR_HANDLERINFOCHAIN, factory);
        soapService.setOption(Call.WSDL_SERVICE, new Service());
        soapService.setOption(Call.WSDL_PORT_NAME, new QName("Fake"));
        soapService.init();
        AxisClient client = new AxisClient();
        MessageContext context = new TestMessageContext(client);
        context.setTransportName("local");
        context.setService(soapService);
        client.invoke(context);

        AAAHandler handlerZero = factory.getHandlers()[0];
        AAAHandler handlerOne = factory.getHandlers()[1];
        AAAHandler handlerTwo = factory.getHandlers()[2];
        assertHandlerRuntime("handlerZero", handlerZero, 1, 1, 0);
        assertHandlerRuntime("handlerOne", handlerOne, 1, 1, 0);
        assertHandlerRuntime("handlerTwo", handlerTwo, 1, 1, 0);
    }

    /**
     * Tests scenario where one handler returns false on a call
     * to handleRequest(...).
     * <p/>
     * Expected Chain invocation sequence looks like.....
     * H0.handleRequest
     * H1.handleRequest returns false
     * H1.handleResponse
     * H0.handleResponse
     * 
     * @throws Exception 
     */
    public void testRequestHandlerReturnsFalse() throws Exception {
        // SETUP THE 2nd HANDLER IN THE REQUEST CHAIN TO RETURN FALSE
        handler1Config.put("HANDLE_REQUEST_RETURN_VALUE", Boolean.FALSE);

        TestHandlerInfoChainFactory factory = buildInfoChainFactory();
        SOAPService soapService = new SOAPService();
        soapService.setOption(Constants.ATTR_HANDLERINFOCHAIN, factory);
        soapService.setOption(Call.WSDL_SERVICE, new Service());
        soapService.setOption(Call.WSDL_PORT_NAME, new QName("Fake"));
        soapService.init();
        AxisClient client = new AxisClient();
        MessageContext context = new TestMessageContext(client);
        context.setTransportName("local");
        context.setService(soapService);
        client.invoke(context);

        AAAHandler handlerZero = factory.getHandlers()[0];
        AAAHandler handlerOne = factory.getHandlers()[1];
        AAAHandler handlerTwo = factory.getHandlers()[2];
        assertHandlerRuntime("handlerZero", handlerZero, 1, 1, 0);
        assertHandlerRuntime("handlerOne", handlerOne, 1, 1, 0);
        assertHandlerRuntime("handlerTwo", handlerTwo, 0, 0, 0);
    }

    /**
     * Tests scenario where one handler throws a JAXRPCException
     * to handleRequest(...).
     * <p/>
     * Expected Chain invocation sequence looks like.....
     * H0.handleRequest
     * H1.handleRequest throws JAXRPCException
     *
     * @throws Exception 
     */
    public void testRequestHandlerThrowsJAXRPCException() throws Exception {
        // SETUP THE 2nd HANDLER IN THE REQUEST CHAIN TO THROW JAXRPCException
        handler1Config.put("HANDLE_REQUEST_RETURN_VALUE",
                new JAXRPCException());

        TestHandlerInfoChainFactory factory = buildInfoChainFactory();
        SOAPService soapService = new SOAPService();
        soapService.setOption(Constants.ATTR_HANDLERINFOCHAIN, factory);
        soapService.setOption(Call.WSDL_SERVICE, new Service());
        soapService.setOption(Call.WSDL_PORT_NAME, new QName("Fake"));
        soapService.init();
        AxisClient client = new AxisClient();
        MessageContext context = new TestMessageContext(client);
        context.setTransportName("local");
        context.setService(soapService);
        
        try {
            client.invoke(context);
        } catch (AxisFault e) {
            AAAHandler handlerZero = factory.getHandlers()[0];
            AAAHandler handlerOne = factory.getHandlers()[1];
            AAAHandler handlerTwo = factory.getHandlers()[2];
            assertHandlerRuntime("handlerZero", handlerZero, 1, 0, 0);
            assertHandlerRuntime("handlerOne", handlerOne, 1, 0, 0);
            assertHandlerRuntime("handlerTwo", handlerTwo, 0, 0, 0);
        }
    }

    /**
     * Tests scenario where one handler throws a RuntimeException
     * to handleRequest(...).
     * <p/>
     * Expected Chain invocation sequence looks like.....
     * H0.handleRequest
     * H1.handleRequest throws JAXRPCException
     *
     * @throws Exception 
     */
    public void testRequestHandlerThrowsRuntimeException() throws Exception {
        // SETUP THE 2nd HANDLER IN THE REQUEST CHAIN TO THROW JAXRPCException
        handler1Config.put("HANDLE_REQUEST_RETURN_VALUE",
                new RuntimeException());

        TestHandlerInfoChainFactory factory = buildInfoChainFactory();
        SOAPService soapService = new SOAPService();
        soapService.setOption(Constants.ATTR_HANDLERINFOCHAIN, factory);
        soapService.setOption(Call.WSDL_SERVICE, new Service());
        soapService.setOption(Call.WSDL_PORT_NAME, new QName("Fake"));
        soapService.init();
        AxisClient client = new AxisClient();
        MessageContext context = new TestMessageContext(client);
        context.setTransportName("local");
        context.setService(soapService);

        try {
            client.invoke(context);
            fail("Expected AxisFault to be thrown");
        } catch (AxisFault e) {
            AAAHandler handlerZero = factory.getHandlers()[0];
            AAAHandler handlerOne = factory.getHandlers()[1];
            AAAHandler handlerTwo = factory.getHandlers()[2];
            assertHandlerRuntime("handlerZero", handlerZero, 1, 0, 0);
            assertHandlerRuntime("handlerOne", handlerOne, 1, 0, 0);
            assertHandlerRuntime("handlerTwo", handlerTwo, 0, 0, 0);
        }
    }

    /**
     * Tests scenario where one handler returns false on a call
     * to handleResponse(...).
     * <p/>
     * Expected Chain invocation sequence looks like.....
     * H0.handleRequest
     * H1.handleRequest
     * H2.handleRequest
     * H2.handleResponse return false
     * 
     * @throws Exception 
     */
    public void testResponseHandlerReturnsFalse() throws Exception {
        // SETUP THE 3rd HANDLER IN THE CHAIN TO RETURN FALSE on handleResponse
        handler2Config.put("HANDLE_RESPONSE_RETURN_VALUE", Boolean.FALSE);

        TestHandlerInfoChainFactory factory = buildInfoChainFactory();
        SOAPService soapService = new SOAPService();
        soapService.setOption(Constants.ATTR_HANDLERINFOCHAIN, factory);
        soapService.setOption(Call.WSDL_SERVICE, new Service());
        soapService.setOption(Call.WSDL_PORT_NAME, new QName("Fake"));
        soapService.init();
        AxisClient client = new AxisClient();
        MessageContext context = new TestMessageContext(client);
        context.setTransportName("local");
        context.setService(soapService);

        client.invoke(context);
        
        AAAHandler handlerZero = factory.getHandlers()[0];
        AAAHandler handlerOne = factory.getHandlers()[1];
        AAAHandler handlerTwo = factory.getHandlers()[2];
        assertHandlerRuntime("handlerZero", handlerZero, 1, 0, 0);
        assertHandlerRuntime("handlerOne", handlerOne, 1, 0, 0);
        assertHandlerRuntime("handlerTwo", handlerTwo, 1, 1, 0);
    }

    /**
     * Tests scenario where one handler throws JAXRPCException on a call
     * to handleResponse(...).
     * <p/>
     * Expected Chain invocation sequence looks like.....
     * H0.handleRequest
     * H1.handleRequest
     * H2.handleRequest
     * H2.handleResponse
     * H1.handlerResponse throws JAXRPCException
     * 
     * @throws Exception 
     */
    public void testResponseHandlerThrowsJAXRPCException() throws Exception {
        // SETUP THE 2nd HANDLER IN THE CHAIN TO THROW JAXRPCException on handleResponse
        handler1Config.put("HANDLE_RESPONSE_RETURN_VALUE",
                new JAXRPCException());

        TestHandlerInfoChainFactory factory = buildInfoChainFactory();
        SOAPService soapService = new SOAPService();
        soapService.setOption(Constants.ATTR_HANDLERINFOCHAIN, factory);
        soapService.setOption(Call.WSDL_SERVICE, new Service());
        soapService.setOption(Call.WSDL_PORT_NAME, new QName("Fake"));
        soapService.init();
        AxisClient client = new AxisClient();
        MessageContext context = new TestMessageContext(client);
        context.setTransportName("local");
        context.setService(soapService);

        try {
            client.invoke(context);
            fail("Expected AxisFault to be thrown");
        } catch (AxisFault e) {
            AAAHandler handlerZero = factory.getHandlers()[0];
            AAAHandler handlerOne = factory.getHandlers()[1];
            AAAHandler handlerTwo = factory.getHandlers()[2];
            assertHandlerRuntime("handlerZero", handlerZero, 1, 0, 0);
            assertHandlerRuntime("handlerOne", handlerOne, 1, 1, 0);
            assertHandlerRuntime("handlerTwo", handlerTwo, 1, 1, 0);
        }
    }

    /**
     * Tests scenario where one handler throws RuntimeException on a call
     * to handleResponse(...).
     * <p/>
     * Expected Chain invocation sequence looks like.....
     * H0.handleRequest
     * H1.handleRequest
     * H2.handleRequest
     * H2.handleResponse
     * H1.handlerResponse throws RuntimeException
     * 
     * @throws Exception 
     */
    public void testResponseHandlerThrowsRuntimeException() throws Exception {
        // SETUP THE 2nd HANDLER IN THE CHAIN TO THROW RuntimeException on handleResponse
        handler1Config.put("HANDLE_RESPONSE_RETURN_VALUE",
                new RuntimeException());

        TestHandlerInfoChainFactory factory = buildInfoChainFactory();
        SOAPService soapService = new SOAPService();
        soapService.setOption(Constants.ATTR_HANDLERINFOCHAIN, factory);
        soapService.setOption(Call.WSDL_SERVICE, new Service());
        soapService.setOption(Call.WSDL_PORT_NAME, new QName("Fake"));
        soapService.init();
        AxisClient client = new AxisClient();
        MessageContext context = new TestMessageContext(client);
        context.setTransportName("local");
        context.setService(soapService);

        try {
            client.invoke(context);
            fail("Expected AxisFault to be thrown");
        } catch (AxisFault e) {
            AAAHandler handlerZero = factory.getHandlers()[0];
            AAAHandler handlerOne = factory.getHandlers()[1];
            AAAHandler handlerTwo = factory.getHandlers()[2];
            assertHandlerRuntime("handlerZero", handlerZero, 1, 0, 0);
            assertHandlerRuntime("handlerOne", handlerOne, 1, 1, 0);
            assertHandlerRuntime("handlerTwo", handlerTwo, 1, 1, 0);
        }
    }

    /**
     * Tests scenario where one handler returns false on a call
     * to handleFault(...).
     * <p/>
     * Expected Chain invocation sequence looks like.....
     * H0.handleRequest
     * H1.handleRequest
     * H2.handleRequest
     * H2.handleFault
     * H1.handleFault return false
     * 
     * @throws Exception 
     */
    public void testHandleFaultReturnsFalse() throws Exception {
        // SETUP A MOCK SERVICE THAT SIMULATE A SOAPFAULT THROWN BY ENDPOINT
        handler1Config.put("HANDLE_FAULT_RETURN_VALUE", Boolean.FALSE);

        TestHandlerInfoChainFactory factory = buildInfoChainFactory();
        SOAPService soapService = new SOAPService();
        soapService.setOption(Constants.ATTR_HANDLERINFOCHAIN, factory);
        soapService.setOption(Call.WSDL_SERVICE, new Service());
        soapService.setOption(Call.WSDL_PORT_NAME, new QName("Fake"));
        soapService.init();
        AxisClient client = new AxisClient();        
        addFaultTransport(client);

        MessageContext context = new TestMessageContext(client);
        context.setTransportName("faulter");
        context.setService(soapService);

        try {
            client.invoke(context);
            fail("Expecting an AxisFault");
        } catch (AxisFault f) {
            AAAHandler handlerZero = factory.getHandlers()[0];
            AAAHandler handlerOne = factory.getHandlers()[1];
            AAAHandler handlerTwo = factory.getHandlers()[2];
            assertHandlerRuntime("handlerZero", handlerZero, 1, 0, 0);
            assertHandlerRuntime("handlerOne", handlerOne, 1, 0, 0);
            assertHandlerRuntime("handlerTwo", handlerTwo, 1, 0, 0);
        }
    }

    /**
     * Tests to see if we handle the scenario of a handler throwing a
     * runtime exception during the handleFault(...) processing correctly
     * <p/>
     * Expected chain sequence:
     * H0.handleRequest
     * H1.handleRequest
     * H2.handleRequest
     * H2.handleFault
     * H1.handleFault throws JAXRPCException
     * 
     * @throws Exception 
     */
    public void testFaultHandlerThrowsJAXRPCException() throws Exception {
        handler1Config.put("HANDLE_FAULT_RETURN_VALUE", new JAXRPCException());

        TestHandlerInfoChainFactory factory = buildInfoChainFactory();
        Handler serviceHandler = new MockServiceHandler();
        SOAPService soapService = new SOAPService(null, serviceHandler, null);
        soapService.setOption(Constants.ATTR_HANDLERINFOCHAIN, factory);
        soapService.setOption(Call.WSDL_SERVICE, new Service());
        soapService.setOption(Call.WSDL_PORT_NAME, new QName("Fake"));
        soapService.init();
        AxisClient client = new AxisClient();        
        addFaultTransport(client);

        MessageContext context = new TestMessageContext(client);
        context.setTransportName("faulter");
        context.setService(soapService);

        try {
            client.invoke(context);
            fail("Expected AxisFault to be thrown");
        } catch (AxisFault e) {
            AAAHandler handlerZero = factory.getHandlers()[0];
            AAAHandler handlerOne = factory.getHandlers()[1];
            AAAHandler handlerTwo = factory.getHandlers()[2];
            assertHandlerRuntime("handlerZero", handlerZero, 1, 0, 0);
            assertHandlerRuntime("handlerOne", handlerOne, 1, 0, 0);
            assertHandlerRuntime("handlerTwo", handlerTwo, 1, 0, 0);
        }
    }

    /**
     * Tests to see if we handle the scenario of a handler throwing a
     * runtime exception during the handleFault(...) processing correctly
     * <p/>
     * Expected chain sequence:
     * H0.handleRequest
     * H1.handleRequest
     * H2.handleRequest
     * H2.handleFault
     * H1.handleFault throws RuntimeException
     * 
     * @throws Exception 
     */
    public void testFaultHandlerThrowsRuntimeException() throws Exception {
        // SETUP THE LAST HANDLER IN THE REQUEST CHAIN TO THROW SOAPFaultException
        handler1Config.put("HANDLE_FAULT_RETURN_VALUE", new RuntimeException());

        TestHandlerInfoChainFactory factory = buildInfoChainFactory();
        Handler serviceHandler = new MockServiceHandler();
        SOAPService soapService = new SOAPService(null, serviceHandler, null);
        soapService.setOption(Constants.ATTR_HANDLERINFOCHAIN, factory);
        soapService.setOption(Call.WSDL_SERVICE, new Service());
        soapService.setOption(Call.WSDL_PORT_NAME, new QName("Fake"));
        soapService.init();
        AxisClient client = new AxisClient();        
        addFaultTransport(client);

        MessageContext context = new TestMessageContext(client);
        context.setTransportName("faulter");
        context.setService(soapService);

        try {
            client.invoke(context);
            fail("Expected AxisFault to be thrown");
        } catch (AxisFault e) {
            AAAHandler handlerZero = factory.getHandlers()[0];
            AAAHandler handlerOne = factory.getHandlers()[1];
            AAAHandler handlerTwo = factory.getHandlers()[2];
            assertHandlerRuntime("handlerZero", handlerZero, 1, 0, 0);
            assertHandlerRuntime("handlerOne", handlerOne, 1, 0, 0);
            assertHandlerRuntime("handlerTwo", handlerTwo, 1, 0, 0);
        }
    }

    /**
     * Tests scenario where service object throws Axis Fault.
     * <p/>
     * Expected chain sequence:
     * H0.handleRequest
     * H1.handleRequest
     * H2.handleRequest
     * ServiceObject.invoke() throws AxisFault
     * H2.handleFault
     * H1.handleFault
     * H0.handleFault
     * 
     * @throws Exception 
     */
    public void testServiceObjectThrowsAxisFault() throws Exception {
        TestHandlerInfoChainFactory factory = buildInfoChainFactory();
        Handler serviceHandler = new MockServiceHandler();
        SOAPService soapService = new SOAPService(null, serviceHandler, null);
        soapService.setOption(Constants.ATTR_HANDLERINFOCHAIN, factory);
        soapService.setOption(Call.WSDL_SERVICE, new Service());
        soapService.setOption(Call.WSDL_PORT_NAME, new QName("Fake"));
        soapService.init();
        AxisClient client = new AxisClient();        
        addFaultTransport(client);

        MessageContext context = new TestMessageContext(client);
        context.setTransportName("faulter");
        context.setService(soapService);

        try {
            client.invoke(context);
            fail("Expected AxisFault to be thrown");
        } catch (AxisFault e) {
            AAAHandler handlerZero = factory.getHandlers()[0];
            AAAHandler handlerOne = factory.getHandlers()[1];
            AAAHandler handlerTwo = factory.getHandlers()[2];
            assertHandlerRuntime("handlerZero", handlerZero, 1, 0, 0);
            assertHandlerRuntime("handlerOne", handlerOne, 1, 0, 0);
            assertHandlerRuntime("handlerTwo", handlerTwo, 1, 0, 0);
        }
    }
    
    /**
     * Configure a transport handler that simulate a Fault on server-side
     * @param client AxisClient
     */
    private void addFaultTransport(AxisClient client) {

        FileProvider config = (FileProvider) client.getConfig();

        WSDDDeployment depl = config.getDeployment();
        if (depl == null) {
            depl = new WSDDDeployment();
        }
        WSDDTransport trans = new AxisFaultWSDDTransport();
        depl.deployTransport(trans);
        
    }

    private class TestMessageContext extends org.apache.axis.MessageContext {

        public String listByAreaCode = "<soap:Envelope\n" +
                "xmlns:s0=\"http://www.tilisoft.com/ws/LocInfo/literalTypes\"\n" +
                "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                "xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n" +
                "<soap:Header>\n" +
                "<WSABIHeader>\n" +
                "<SubscriptionId>192168001100108165800640600008</SubscriptionId>\n" +
                "</WSABIHeader>\n" +
                "</soap:Header>\n" +
                "<soap:Body>\n" +
                "<s0:ListByAreaCode>\n" +
                "<s0:AreaCode>617</s0:AreaCode>\n" +
                "</s0:ListByAreaCode>\n" +
                "</soap:Body>\n" +
                "</soap:Envelope>\n";

        public TestMessageContext(AxisClient client) {
            super(client);
            Message message = new Message(listByAreaCode);
            message.setMessageType(Message.REQUEST);
            setRequestMessage(message);
        }
    }

}
