package test.RPCDispatch;

import junit.framework.TestCase;

import org.apache.axis.*;
import org.apache.axis.handlers.soap.*;
import org.apache.axis.message.*;
import org.apache.axis.server.*;
import org.apache.axis.registries.*;

import java.util.Vector;

/**
 * Test org.apache.axis.handlers.RPCDispatcher
 *
 * @author Sam Ruby <rubys@us.ibm.com>
 */
public class TestRPC extends TestCase {

    private final String header = 
        "<?xml version=\"1.0\"?>\n" +
        "<soap:Envelope " +
             "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
             "xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" " +
             "xmlns:xsi=\"" + Constants.URI_CURRENT_SCHEMA_XSI + "\" " +
             "xmlns:xsd=\"" + Constants.URI_CURRENT_SCHEMA_XSD + "\">\n" +
             "<soap:Body>\n";

    private final String footer = 
             "</soap:Body>\n" +
        "</soap:Envelope>\n";

    private AxisServer engine = new AxisServer();
    private HandlerRegistry hr;
    private Handler RPCDispatcher;

    private String SOAPAction = "urn:reverse";
    private String methodNS   = null;

    public TestRPC(String name) {
        super(name);
        engine.init();
        hr = (HandlerRegistry) engine.getOption(Constants.HANDLER_REGISTRY);
        RPCDispatcher = hr.find("RPCDispatcher");
        // Debug.setDebugLevel(5);
    }

    /**
     * Invoke a given RPC method, and return the result
     * @param soapAction action to be performed 
     * @param request XML body of the request
     * @return Deserialized result
     */
    private final Object rpc(String method, Object[] parms) {

        // Construct the soap request
        SOAPEnvelope envelope = new SOAPEnvelope();
        RPCElement body = new RPCElement(method);
        body.setNamespaceURI(methodNS);
        envelope.addBodyElement(body);
        for (int i=0; i<parms.length; i++) {
            body.addParam(new RPCParam("arg"+i, parms[i]));
        }

        // Create a message context with the action and message
        MessageContext msgContext = new MessageContext();
        msgContext.setRequestMessage(new Message(envelope, "SOAPEnvelope"));
        msgContext.setTargetService(SOAPAction);

        // Invoke the Axis engine
        try {
            engine.invoke(msgContext);
        } catch (AxisFault af) {
            return af;
        }

        // Extract the response Envelope
        Message message = msgContext.getResponseMessage();
        envelope = (SOAPEnvelope)message.getAs("SOAPEnvelope");
        assertNotNull("envelope", envelope);

        // Extract the body from the envelope
        body = (RPCElement)envelope.getFirstBody();
        assertNotNull("body", body);

        // Extract the list of parameters from the body
        Vector arglist = body.getParams();
        assertNotNull("arglist", arglist);
        assert("param.size()>0", arglist.size()>0);

        // Return the first parameter
        RPCParam param = (RPCParam) arglist.get(0);
        return param.getValue();
    }

    /**
     * Test a simple method that reverses a string
     */
    public void testReverseString() throws Exception {
        // Register the reverseString service
        SOAPService reverse = new SOAPService(RPCDispatcher, "RPCDispatcher");
        reverse.addOption("className", "test.RPCDispatch.Service");
        reverse.addOption("methodName", "reverseString");
        hr.add(SOAPAction, reverse);

        // invoke the service and verify the result
        assertEquals("cba", rpc("reverseString", new Object[] {"abc"}));
    }

    /**
     * Test a method that reverses a data structure
     */
    public void testReverseData() throws Exception {
        // Register the reverseData service
        SOAPService reverse = new SOAPService(RPCDispatcher, "RPCDispatcher");
        reverse.addOption("className", "test.RPCDispatch.Service");
        reverse.addOption("methodName", "reverseData");
        hr.add(SOAPAction, reverse);

        // invoke the service and verify the result
        Data input    = new Data(5, "abc", 3);
        Data expected = new Data(3, "cba", 5);
        assertEquals(expected, rpc("reverseData", new Object[] {input}));
    }
}
