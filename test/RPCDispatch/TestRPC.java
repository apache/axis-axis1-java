package test.RPCDispatch;

import junit.framework.TestCase;

import org.apache.axis.*;
import org.apache.axis.encoding.*;
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
    private HandlerRegistry sr;
    private Handler RPCDispatcher;

    private String SOAPAction = "urn:reverse";

    public TestRPC(String name) {
        super(name);
        engine.init();
        hr = (HandlerRegistry) engine.getHandlerRegistry();
        sr = (HandlerRegistry) engine.getServiceRegistry();
        RPCDispatcher = hr.find("RPCDispatcher");
        // Debug.setDebugLevel(5);
    }

    /**
     * Invoke a given RPC method, and return the result
     * @param soapAction action to be performed
     * @param request XML body of the request
     * @return Deserialized result
     */
    private final Object rpc(String method, Object[] parms)
        throws AxisFault
    {

        // Create the message context
        MessageContext msgContext = new MessageContext(engine);

        // Set the dispatch either by SOAPAction or methodNS
        String methodNS = null;
        msgContext.setTargetService(SOAPAction);

        // Construct the soap request
        SOAPEnvelope envelope = new SOAPEnvelope();
        msgContext.setRequestMessage(new Message(envelope));
        RPCElement body = new RPCElement(methodNS, method, parms);
        envelope.addBodyElement(body);

        // Invoke the Axis engine
        try {
            engine.invoke(msgContext);
        } catch (AxisFault af) {
            return af;
        }

        // Extract the response Envelope
        Message message = msgContext.getResponseMessage();
        envelope = (SOAPEnvelope)message.getAsSOAPEnvelope();
        assertNotNull("envelope", envelope);

        // Extract the body from the envelope
        body = (RPCElement)envelope.getFirstBody();
        assertNotNull("body", body);

        // Extract the list of parameters from the body
        Vector arglist = body.getParams();
        assertNotNull("arglist", arglist);
        if (arglist.size()==0) return null;

        // Return the first parameter
        RPCParam param = (RPCParam) arglist.get(0);
        return param.getValue();
    }

    /**
     * Test a simple method that reverses a string
     */
    public void testReverseString() throws Exception {
        // Register the reverseString service
        SOAPService reverse = new SOAPService(RPCDispatcher);
        reverse.addOption("className", "test.RPCDispatch.Service");
        reverse.addOption("methodName", "reverseString");
        sr.add(SOAPAction, reverse);

        // invoke the service and verify the result
        assertEquals("cba", rpc("reverseString", new Object[] {"abc"}));
    }

    /**
     * Test a method that reverses a data structure
     */
    public void testReverseData() throws Exception {
        // Register the reverseData service
        SOAPService reverse = new SOAPService(RPCDispatcher);
        reverse.addOption("className", "test.RPCDispatch.Service");
        reverse.addOption("methodName", "reverseData");
        sr.add(SOAPAction, reverse);

        // invoke the service and verify the result
        Data input    = new Data(5, "abc", 3);
        Data expected = new Data(3, "cba", 5);
        assertEquals(expected, rpc("reverseData", new Object[] {input}));
    }

    /**
     * Test a simple method that returns a field from the message context
     */
    public void testMessageContext() throws Exception {
        // Register the targetService service
        SOAPService tgtSvc = new SOAPService(RPCDispatcher);
        tgtSvc.addOption("className", "test.RPCDispatch.Service");
        tgtSvc.addOption("methodName", "targetService");
        sr.add(SOAPAction, tgtSvc);

        // invoke the service and verify the result
        assertEquals(SOAPAction, rpc("targetService", new Object[] {}));
    }

    /**
     * Test a simple method that accepts and returns a null
     */
    public void testNull() throws Exception {
        // Register the echoInt service
        SOAPService echoInt = new SOAPService(RPCDispatcher);
        echoInt.addOption("className", "test.RPCDispatch.Service");
        echoInt.addOption("methodName", "echoInt");
        sr.add(SOAPAction, echoInt);

        // invoke the service and verify the result
        assertNull(rpc("echoInt", new Object[] {null}));
    }
    
    public static void main(String args[])
    {
      try {
        TestRPC tester = new TestRPC("RPC test");
        tester.testReverseString();
        tester.testReverseData();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
}
