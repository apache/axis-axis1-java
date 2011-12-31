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
        extends AJAXRPC {
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
