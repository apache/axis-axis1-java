package test.jaxrpc;

import junit.framework.TestCase;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Handler;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.handlers.HandlerInfoChainFactory;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.message.Detail;
import org.apache.axis.server.AxisServer;

import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.handler.HandlerChain;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.soap.SOAPFaultException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestSOAPService
        extends TestCase {
    HandlerInfo handlerInfo0 = null;
    HandlerInfo handlerInfo1 = null;
    HandlerInfo handlerInfo2 = null;

    Map handler0Config = null;
    Map handler1Config = null;
    Map handler2Config = null;

    /**
     * Sets up the handlerInfo and handlerConfigs for all tests.
     * Each test has 3 JAX-RPC Handlers of the same type to keep things
     * simple.
     * 
     * @throws Exception 
     */
    protected void setUp() throws Exception {
        handlerInfo0 = new HandlerInfo();
        handlerInfo0.setHandlerClass(AAAHandler.class);
        handlerInfo1 = new HandlerInfo();
        handlerInfo1.setHandlerClass(AAAHandler.class);
        handlerInfo2 = new HandlerInfo();
        handlerInfo2.setHandlerClass(AAAHandler.class);
        handler0Config = new HashMap();
        handler1Config = new HashMap();
        handler2Config = new HashMap();
        handlerInfo0.setHandlerConfig(handler0Config);
        handlerInfo1.setHandlerConfig(handler1Config);
        handlerInfo2.setHandlerConfig(handler2Config);
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
        soapService.init();
        soapService.invoke(new TestMessageContext());
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
        SOAPService soapService = new SOAPService();

        // SETUP THE 2nd HANDLER IN THE REQUEST CHAIN TO RETURN FALSE
        handler1Config.put("HANDLE_REQUEST_RETURN_VALUE", Boolean.FALSE);
        TestHandlerInfoChainFactory factory = buildInfoChainFactory();
        soapService.setOption(Constants.ATTR_HANDLERINFOCHAIN, factory);
        soapService.init();
        MessageContext msgContext = new TestMessageContext();
        soapService.invoke(msgContext);
        AAAHandler handlerZero = factory.getHandlers()[0];
        AAAHandler handlerOne = factory.getHandlers()[1];
        AAAHandler handlerTwo = factory.getHandlers()[2];
        assertHandlerRuntime("handlerZero", handlerZero, 1, 1, 0);
        assertHandlerRuntime("handlerOne", handlerOne, 1, 1, 0);
        assertHandlerRuntime("handlerTwo", handlerTwo, 0, 0, 0);
    }

    /**
     * @throws Exception 
     */
    public void testRequestHandlerThrowsSFE() throws Exception {
        SOAPService soapService = new SOAPService();

        // SETUP THE 2nd HANDLER IN THE REQUEST CHAIN TO THROW SOAPFaultException
        handler1Config.put("HANDLE_REQUEST_RETURN_VALUE",
                new SOAPFaultException(null, "f", "f", new Detail()));
        TestHandlerInfoChainFactory factory = buildInfoChainFactory();
        soapService.setOption(Constants.ATTR_HANDLERINFOCHAIN, factory);
        soapService.init();
        MessageContext msgContext = new TestMessageContext();
        soapService.invoke(msgContext);
        AAAHandler handlerZero = factory.getHandlers()[0];
        AAAHandler handlerOne = factory.getHandlers()[1];
        AAAHandler handlerTwo = factory.getHandlers()[2];
        assertHandlerRuntime("handlerZero", handlerZero, 1, 0, 1);
        assertHandlerRuntime("handlerOne", handlerOne, 1, 0, 1);
        assertHandlerRuntime("handlerTwo", handlerTwo, 0, 0, 0);
    }

    /**
     * @throws Exception 
     */
    public void testRequestHandlerThrowsJAXRPCException() throws Exception {
        SOAPService soapService = new SOAPService();

        // SETUP THE 2nd HANDLER IN THE REQUEST CHAIN TO THROW JAXRPCException
        handler1Config.put("HANDLE_REQUEST_RETURN_VALUE",
                new JAXRPCException());
        TestHandlerInfoChainFactory factory = buildInfoChainFactory();
        soapService.setOption(Constants.ATTR_HANDLERINFOCHAIN, factory);
        soapService.init();
        MessageContext msgContext = new TestMessageContext();
        try {
            soapService.invoke(msgContext);
        } catch (AxisFault e) {
            AAAHandler handlerZero = factory.getHandlers()[0];
            AAAHandler handlerOne = factory.getHandlers()[1];
            AAAHandler handlerTwo = factory.getHandlers()[2];
            assertHandlerRuntime("handlerZero", handlerZero, 1, 0, 0);
            assertHandlerRuntime("handlerOne", handlerOne, 1, 0, 0);
            assertHandlerRuntime("handlerTwo", handlerTwo, 0, 0, 0);
        }
    }

    public void testRequestHandlerThrowsRuntimeException() throws Exception {
        SOAPService soapService = new SOAPService();

        // SETUP THE 2nd HANDLER IN THE REQUEST CHAIN TO THROW JAXRPCException
        handler1Config.put("HANDLE_REQUEST_RETURN_VALUE",
                new RuntimeException());
        TestHandlerInfoChainFactory factory = buildInfoChainFactory();
        soapService.setOption(Constants.ATTR_HANDLERINFOCHAIN, factory);
        soapService.init();
        MessageContext msgContext = new TestMessageContext();
        try {
            soapService.invoke(msgContext);
        } catch (AxisFault e) {
            AAAHandler handlerZero = factory.getHandlers()[0];
            AAAHandler handlerOne = factory.getHandlers()[1];
            AAAHandler handlerTwo = factory.getHandlers()[2];
            assertHandlerRuntime("handlerZero", handlerZero, 1, 0, 0);
            assertHandlerRuntime("handlerOne", handlerOne, 1, 0, 0);
            assertHandlerRuntime("handlerTwo", handlerTwo, 0, 0, 0);
        }
    }

    public void testResponseHandlerReturnsFalse() throws Exception {
        SOAPService soapService = new SOAPService();

        // SETUP THE 3rd HANDLER IN THE CHAIN TO RETURN FALSE on handleResponse
        handler2Config.put("HANDLE_RESPONSE_RETURN_VALUE", Boolean.FALSE);
        TestHandlerInfoChainFactory factory = buildInfoChainFactory();
        soapService.setOption(Constants.ATTR_HANDLERINFOCHAIN, factory);
        soapService.init();
        MessageContext msgContext = new TestMessageContext();
        soapService.invoke(msgContext);
        AAAHandler handlerZero = factory.getHandlers()[0];
        AAAHandler handlerOne = factory.getHandlers()[1];
        AAAHandler handlerTwo = factory.getHandlers()[2];
        assertHandlerRuntime("handlerZero", handlerZero, 1, 0, 0);
        assertHandlerRuntime("handlerOne", handlerOne, 1, 0, 0);
        assertHandlerRuntime("handlerTwo", handlerTwo, 1, 1, 0);
    }

    public void testResponseHandlerThrowsJAXRPCException() throws Exception {
        SOAPService soapService = new SOAPService();

        // SETUP THE 2nd HANDLER IN THE CHAIN TO THROW JAXRPCException on handleResponse
        handler1Config.put("HANDLE_RESPONSE_RETURN_VALUE",
                new JAXRPCException());
        TestHandlerInfoChainFactory factory = buildInfoChainFactory();
        soapService.setOption(Constants.ATTR_HANDLERINFOCHAIN, factory);
        soapService.init();
        MessageContext msgContext = new TestMessageContext();
        try {
            soapService.invoke(msgContext);
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

    public void testResponseHandlerThrowsRuntimeException() throws Exception {
        SOAPService soapService = new SOAPService();

        // SETUP THE 2nd HANDLER IN THE CHAIN TO THROW RuntimeException on handleResponse
        handler1Config.put("HANDLE_RESPONSE_RETURN_VALUE",
                new RuntimeException());
        TestHandlerInfoChainFactory factory = buildInfoChainFactory();
        soapService.setOption(Constants.ATTR_HANDLERINFOCHAIN, factory);
        soapService.init();
        MessageContext msgContext = new TestMessageContext();
        try {
            soapService.invoke(msgContext);
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

    public void testHandleFaultReturnsFalse() throws Exception {
        SOAPService soapService = new SOAPService();

        // SETUP THE LAST HANDLER IN THE REQUEST CHAIN TO THROW SOAPFaultException
        handler2Config.put("HANDLE_REQUEST_RETURN_VALUE",
                new SOAPFaultException(null, "f", "f", new Detail()));
        handler1Config.put("HANDLE_FAULT_RETURN_VALUE", Boolean.FALSE);
        TestHandlerInfoChainFactory factory = buildInfoChainFactory();
        soapService.setOption(Constants.ATTR_HANDLERINFOCHAIN, factory);
        soapService.init();
        MessageContext msgContext = new TestMessageContext();
        soapService.invoke(msgContext);
        AAAHandler handlerZero = factory.getHandlers()[0];
        AAAHandler handlerOne = factory.getHandlers()[1];
        AAAHandler handlerTwo = factory.getHandlers()[2];
        assertHandlerRuntime("handlerZero", handlerZero, 1, 0, 0);
        assertHandlerRuntime("handlerOne", handlerOne, 1, 0, 1);
        assertHandlerRuntime("handlerTwo", handlerTwo, 1, 0, 1);
    }

    /**
     * Tests to see if we handle the scenario of a handler throwing a
     * runtime exception during the handleFault(...) processing correctly
     * <p/>
     * Expected chain sequence:
     * H0.handleRequest
     * H1.handleRequest
     * H2.handleRequest SOAPFaultException
     * H2.handleFault
     * H1.handleFault throws JAXRPCException
     * 
     * @throws Exception 
     */
    public void testFaultHandlerThrowsJAXRPCException() throws Exception {
        SOAPService soapService = new SOAPService();

        // SETUP THE LAST HANDLER IN THE REQUEST CHAIN TO THROW SOAPFaultException
        handler2Config.put("HANDLE_REQUEST_RETURN_VALUE",
                new SOAPFaultException(null, "f", "f", new Detail()));
        handler1Config.put("HANDLE_FAULT_RETURN_VALUE", new JAXRPCException());
        TestHandlerInfoChainFactory factory = buildInfoChainFactory();
        soapService.setOption(Constants.ATTR_HANDLERINFOCHAIN, factory);
        soapService.init();
        MessageContext msgContext = new TestMessageContext();
        try {
            soapService.invoke(msgContext);
            fail("Expected AxisFault to be thrown");
        } catch (AxisFault e) {
            AAAHandler handlerZero = factory.getHandlers()[0];
            AAAHandler handlerOne = factory.getHandlers()[1];
            AAAHandler handlerTwo = factory.getHandlers()[2];
            assertHandlerRuntime("handlerZero", handlerZero, 1, 0, 0);
            assertHandlerRuntime("handlerOne", handlerOne, 1, 0, 1);
            assertHandlerRuntime("handlerTwo", handlerTwo, 1, 0, 1);
        }
    }

    /**
     * Tests to see if we handle the scenario of a handler throwing a
     * runtime exception during the handleFault(...) processing correctly
     * <p/>
     * Expected chain sequence:
     * H0.handleRequest
     * H1.handleRequest
     * H2.handleRequest throws SOAPFaultException
     * H2.handleFault
     * H1.handleFault throws RuntimeException
     * 
     * @throws Exception 
     */
    public void testFaultHandlerThrowsRuntimeException() throws Exception {
        SOAPService soapService = new SOAPService();

        // SETUP THE LAST HANDLER IN THE REQUEST CHAIN TO THROW SOAPFaultException
        handler2Config.put("HANDLE_REQUEST_RETURN_VALUE",
                new SOAPFaultException(null, "f", "f", new Detail()));
        handler1Config.put("HANDLE_FAULT_RETURN_VALUE", new RuntimeException());
        TestHandlerInfoChainFactory factory = buildInfoChainFactory();
        soapService.setOption(Constants.ATTR_HANDLERINFOCHAIN, factory);
        soapService.init();
        MessageContext msgContext = new TestMessageContext();
        try {
            soapService.invoke(msgContext);
            fail("Expected AxisFault to be thrown");
        } catch (AxisFault e) {
            AAAHandler handlerZero = factory.getHandlers()[0];
            AAAHandler handlerOne = factory.getHandlers()[1];
            AAAHandler handlerTwo = factory.getHandlers()[2];
            assertHandlerRuntime("handlerZero", handlerZero, 1, 0, 0);
            assertHandlerRuntime("handlerOne", handlerOne, 1, 0, 1);
            assertHandlerRuntime("handlerTwo", handlerTwo, 1, 0, 1);
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
        Handler serviceHandler = new MockServiceHandler();
        SOAPService soapService = new SOAPService(null, null, serviceHandler);
        TestHandlerInfoChainFactory factory = buildInfoChainFactory();
        soapService.setOption(Constants.ATTR_HANDLERINFOCHAIN, factory);
        soapService.init();
        MessageContext msgContext = new TestMessageContext();
        try {
            soapService.invoke(msgContext);
            fail("Expected AxisFault to be thrown");
        } catch (AxisFault e) {
            AAAHandler handlerZero = factory.getHandlers()[0];
            AAAHandler handlerOne = factory.getHandlers()[1];
            AAAHandler handlerTwo = factory.getHandlers()[2];
            assertHandlerRuntime("handlerZero", handlerZero, 1, 0, 1);
            assertHandlerRuntime("handlerOne", handlerOne, 1, 0, 1);
            assertHandlerRuntime("handlerTwo", handlerTwo, 1, 0, 1);
        }
    }

    /**
     * Convenience method to organize all test checking for a particular
     * handler.
     * <p/>
     * Checks to see if the expected number of calls to handleRequest, handleResponse
     * and handleFault reconciles with actuals
     * 
     * @param message           
     * @param handler           the target handler to reconcile
     * @param numHandleRequest  # of expected calls to handleRequest
     * @param numHandleResponse # of expected calls to handleResponse
     * @param numHandleFault    # of expected call to handleFault
     */
    protected void assertHandlerRuntime(String message, AAAHandler handler,
                                        int numHandleRequest,
                                        int numHandleResponse,
                                        int numHandleFault) {
        assertEquals(message + ": handleRequest", numHandleRequest,
                handler.getHandleRequestInvocations());
        assertEquals(message + ": handleResponse", numHandleResponse,
                handler.getHandleResponseInvocations());
        assertEquals(message + ": handleFault", numHandleFault,
                handler.getHandleFaultInvocations());
    }

    /**
     * Convenience method to create a HandlerInfoChainFactory
     * 
     * @return 
     */
    protected TestHandlerInfoChainFactory buildInfoChainFactory() {
        List handlerInfos = new ArrayList();
        handlerInfos.add(handlerInfo0);
        handlerInfos.add(handlerInfo1);
        handlerInfos.add(handlerInfo2);
        TestHandlerInfoChainFactory factory = new TestHandlerInfoChainFactory(
                handlerInfos);
        return factory;
    }

    /**
     * Mock Service Handler used to simulate a service object throwing
     * an AxisFault.
     */
    private class MockServiceHandler extends BasicHandler {
        public void invoke(MessageContext msgContext) throws AxisFault {
            throw new AxisFault();
        }
    }

    /**
     * Helper class for keeping references to the JAX-RPC Handlers
     * that are created by the Factory.
     * <p/>
     * This class allows us to access the individual handlers after
     * the test case has been executed in order to make sure that
     * the expected methods of the handler instance have been invoked.
     */
    private class TestHandlerInfoChainFactory extends HandlerInfoChainFactory {
        AAAHandler[] handlers;

        public TestHandlerInfoChainFactory(List handlerInfos) {
            super(handlerInfos);
        }

        public HandlerChain createHandlerChain() {
            HandlerChain chain = super.createHandlerChain();
            handlers = new AAAHandler[chain.size()];
            for (int i = 0; i < chain.size(); i++) {
                handlers[i] = (AAAHandler) chain.get(i);
            }
            return chain;
        }

        public AAAHandler[] getHandlers() {
            return handlers;
        }

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

        public TestMessageContext() {
            super(new AxisServer());
            Message message = new Message(listByAreaCode);
            message.setMessageType(Message.REQUEST);
            setRequestMessage(message);
        }
    }

}
