package test.RPCDispatch;

import junit.framework.TestCase;

import org.apache.axis.*;
import org.apache.axis.encoding.*;
import org.apache.axis.handlers.soap.*;
import org.apache.axis.message.*;
import org.apache.axis.server.*;
import org.apache.axis.registries.*;
import org.apache.axis.utils.QName;

import java.util.Vector;
import java.io.*;

/**
 * Test org.apache.axis.handlers.RPCDispatcher
 *
 * @author Sam Ruby <rubys@us.ibm.com>
 */
public class TestSerializedRPC extends TestCase {

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

    public TestSerializedRPC(String name) {
        super(name);
        engine.init();
        hr = (HandlerRegistry) engine.getHandlerRegistry();
        sr = (HandlerRegistry) engine.getServiceRegistry();
        RPCDispatcher = hr.find("RPCDispatcher");
        
        // Register the reverseString service
        SOAPService reverse = new SOAPService(RPCDispatcher, "RPCDispatcher");
        reverse.addOption("className", "test.RPCDispatch.Service");
        reverse.addOption("methodName", "reverseString reverseData");
        engine.deployService(SOAPAction, reverse);
        
    }

    /**
     * Invoke a given RPC method, and return the result
     * @param soapAction action to be performed
     * @param request XML body of the request
     * @return Deserialized result
     */
    private final Object rpc(String method, String bodyStr,
                             boolean setService)
        throws AxisFault
    {

        // Create the message context
        MessageContext msgContext = new MessageContext(engine);
        
        // Set the dispatch either by SOAPAction or methodNS
        String methodNS = "";
        if (setService) {
            msgContext.setTargetService(SOAPAction);
        } else {
            methodNS = SOAPAction;
        }

        String bodyElemHead = "<m:" + method + " xmlns:m=\"" +
                          methodNS + "\">";
        String bodyElemFoot = "</m:" + method + ">";
        // Construct the soap request
        String msgStr = header + bodyElemHead + bodyStr +
                        bodyElemFoot + footer;
        msgContext.setRequestMessage(new Message(msgStr));
        
        // Invoke the Axis engine
        try {
            engine.invoke(msgContext);
        } catch (AxisFault af) {
            return af;
        }

        // Extract the response Envelope
        Message message = msgContext.getResponseMessage();
        SOAPEnvelope envelope = (SOAPEnvelope)message.getAsSOAPEnvelope();
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
    }

    /**
     * Test a simple method that reverses a string
     */
    public void testSerReverseString() throws Exception {
        String arg = "<arg0 xsi:type=\"xsd:string\">abc</arg0>";
        // invoke the service and verify the result
        assertEquals("cba", rpc("reverseString", arg, true));
    }

    public void testSerReverseBodyDispatch() throws Exception {
        String arg = "<arg0 xsi:type=\"xsd:string\">abc</arg0>";
        // invoke the service and verify the result
        assertEquals("cba", rpc("reverseString", arg, false));
    }
    
    /**
     * Test a method that reverses a data structure
     */
    public void testSerReverseData() throws Exception {
        BeanSerializer ser = new BeanSerializer(Data.class);
        DeserializerFactory dSerFactory = ser.getFactory(Data.class);
        QName qName = new QName("urn:foo", "Data");
        engine.registerTypeMapping(qName, Data.class, dSerFactory,
                                   ser);
        
        // invoke the service and verify the result
        String arg = "<arg0 xmlns:foo=\"urn:foo\" xsi:type=\"foo:Data\">";
        arg += "<field1>5</field1><field2>abc</field2><field3>3</field3>";
        arg += "</arg0>";
        Data expected = new Data(3, "cba", 5);
        assertEquals(expected, rpc("reverseData", arg, true));
    }
    
    public static void main(String args[]) {
      try {
        TestSerializedRPC tester = new TestSerializedRPC("Test Serialized RPC");
        tester.testSerReverseString();
        tester.testSerReverseData();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
}
