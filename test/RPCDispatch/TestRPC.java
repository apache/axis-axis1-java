package test.RPCDispatch;

import junit.framework.TestCase;

import org.apache.axis.*;
import org.apache.axis.handlers.soap.*;
import org.apache.axis.message.*;
import org.apache.axis.server.*;
import org.apache.axis.registries.*;
import org.apache.axis.utils.*;

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

    public TestRPC(String name) {
        super(name);
        engine.init();
        hr = (HandlerRegistry)  engine.getOption(Constants.HANDLER_REGISTRY);
        RPCDispatcher = hr.find("RPCDispatcher");
        // Debug.setDebugLevel(5);
    }

    /**
     * Invoke a given RPC method, and return the result
     * @param soapAction action to be performed 
     * @param request XML body of the request
     * @return Deserialized result
     */
    private final Object rpc(String soapAction, String request) {

        // Wrap request in a SOAP envelope
        Message reqMessage = new Message(header+request+footer, "String");

        // Create a message context with the action and message
        MessageContext msgContext = new MessageContext();
        msgContext.setRequestMessage(reqMessage);
        msgContext.setTargetService(soapAction);

        try {
            // Invoke the Axis engine
            engine.invoke(msgContext);

            // Extract the response Envelope
            Message message = msgContext.getResponseMessage();
            SOAPEnvelope envelope = (SOAPEnvelope)message.getAs("SOAPEnvelope");
            assertNotNull("envelope", envelope);

            // Extract the body from the envelope
            RPCElement body = (RPCElement)envelope.getFirstBody();
            assertNotNull("body", body);

            // Extract the list of parameters from the body
            Vector arglist = body.getParams();
            assertNotNull("arglist", arglist);
            assert("param.size()>0", arglist.size()>0);

            // Return the first parameter
            RPCParam param = (RPCParam) arglist.get(0);
            return param.getValue();
        } catch (AxisFault af) {
            return af;
        }
    }

    /**
     * Test a simple method that reverses a string
     */
    public void testReverse() {
        // Register the reverse service
        SOAPService reverse = new SOAPService(RPCDispatcher, "RPCDispatcher");
        reverse.addOption("className", "test.RPCDispatch.Service");
        reverse.addOption("methodName", "reverse");
        hr.add("urn:reverse", reverse);
        
        // Invoke the service
        Object response = 
           rpc("urn:reverse", "<reverse><arg>abc</arg></reverse>");

        // Verify the result
        assertEquals("cba", response);
    }
}
