package test.RPCDispatch;

import junit.framework.TestCase;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.providers.java.RPCProvider;
import org.apache.axis.server.AxisServer;
import org.xml.sax.SAXException;

import javax.xml.rpc.namespace.QName;
import java.util.Vector;

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

    private SimpleProvider provider = new SimpleProvider();
    private AxisServer engine = new AxisServer(provider);
    
    String firstParamName = null;
    String secondParamName = null;

    private String SOAPAction = "urn:reverse";

    public TestSerializedRPC(String name) throws Exception {
        super(name);
        engine.init();

        // Register the reverseString service
        SOAPService reverse = new SOAPService(new RPCProvider());
        reverse.setOption("className", "test.RPCDispatch.Service");
        reverse.setOption("allowedMethods", "*");
        provider.deployService(SOAPAction, reverse);
        
        // And deploy the type mapping
        Class javaType = Data.class;
        QName xmlType = new QName("urn:foo", "Data");
        BeanSerializerFactory   sf = new BeanSerializerFactory(javaType, xmlType);
        BeanDeserializerFactory df = new BeanDeserializerFactory(javaType, xmlType);

        TypeMappingRegistry tmr = engine.getTypeMappingRegistry();
        TypeMapping tm = (TypeMapping) tmr.getTypeMapping(Constants.URI_CURRENT_SOAP_ENC);
        if (tm == null || tm == tmr.getDefaultTypeMapping()) {
            tm = (TypeMapping) tmr.createTypeMapping();
            tmr.register(Constants.URI_CURRENT_SOAP_ENC, tm);
        }
        tm.register(javaType, xmlType, sf, df);

        ServiceDesc desc = new ServiceDesc();
        desc.loadServiceDescByIntrospection(Service.class, tm);
        reverse.setServiceDescription(desc);
        
        // Now we've got the service description loaded up.  We're going to
        // be testing parameter dispatch by name, so if debug info isn't
        // compiled into the Service class, the names are going to be "in0",
        // etc.  Make sure they match.
        OperationDesc oper = desc.getOperationDescByName("concatenate");
        assertNotNull(oper);
        
        firstParamName = oper.getParameter(0).getName();
        secondParamName = oper.getParameter(1).getName();
    }

    /**
     * Invoke a given RPC method, and return the result
     * @param soapAction action to be performed
     * @param request XML body of the request
     * @return Deserialized result
     */
    private final Object rpc(String method, String bodyStr,
                             boolean setService)
        throws AxisFault, SAXException
    {

        // Create the message context
        MessageContext msgContext = new MessageContext(engine);
        
        // Set the dispatch either by SOAPAction or methodNS
        String methodNS = "urn:dont.match.me";
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
        msgContext.setTypeMappingRegistry(engine.getTypeMappingRegistry());
        
        // Invoke the Axis engine
        try {
            engine.invoke(msgContext);
        } catch (AxisFault af) {
            return af;
        }

        // Extract the response Envelope
        Message message = msgContext.getResponseMessage();
        assertNotNull("Response message was null!", message);
        SOAPEnvelope envelope = (SOAPEnvelope)message.getSOAPPart().getAsSOAPEnvelope();
        assertNotNull("SOAP envelope was null", envelope);

        // Extract the body from the envelope
        RPCElement body = (RPCElement)envelope.getFirstBody();
        assertNotNull("SOAP body was null", body);

        // Extract the list of parameters from the body
        Vector arglist = body.getParams();
        assertNotNull("SOAP argument list was null", arglist);
        assertTrue("param.size()<=0 {Should be > 0}", arglist.size()>0);

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
        assertEquals("Did not reverse the string as expected", "cba", rpc("reverseString", arg, true));
    }

    public void testSerReverseBodyDispatch() throws Exception {
        String arg = "<arg0 xsi:type=\"xsd:string\">abc</arg0>";
        // invoke the service and verify the result
        assertEquals("Did not reverse the string as expected", "cba", rpc("reverseString", arg, false));
    }
    
    /**
     * Test a method that reverses a data structure
     */
    public void testSerReverseData() throws Exception {
        // invoke the service and verify the result
        String arg = "<arg0 xmlns:foo=\"urn:foo\" xsi:type=\"foo:Data\">";
        arg += "<field1>5</field1><field2>abc</field2><field3>3</field3>";
        arg += "</arg0>";
        Data expected = new Data(3, "cba", 5);
        assertEquals("Did not reverse data as expected", expected, rpc("reverseData", arg, true));
    }
    
    /**
     * Test a method that reverses a data structure
     */
    public void testReverseDataWithUntypedParam() throws Exception {
        // invoke the service and verify the result
        String arg = "<arg0 xmlns:foo=\"urn:foo\">";
        arg += "<field1>5</field1><field2>abc</field2><field3>3</field3>";
        arg += "</arg0>";
        Data expected = new Data(3, "cba", 5);
        assertEquals("Did not reverse data as expected", expected, rpc("reverseData", arg, true));
    }

    /**
     * Test out-of-order parameters, using the names to match
     */
    public void testOutOfOrderParams() throws Exception {
        String body = "<" + secondParamName + ">world!</" +
                secondParamName + "><" + firstParamName + ">Hello, </" +
                firstParamName + ">";
        String expected = "Hello, world!";
        assertEquals("Concatenated value was wrong",
                     expected,
                     rpc("concatenate", body, true));
    }

    /**
     * Test DOM round tripping
     */
    public void testArgAsDOM() throws Exception {
        // invoke the service and verify the result
        String arg = "<arg0 xmlns:foo=\"urn:foo\">";
        arg += "<field1>5</field1><field2>abc</field2><field3>3</field3>";
        arg += "</arg0>";
        
        // invoke the service and verify the result
        assertEquals("Did not echo arg correctly.", arg, rpc("argAsDOM", arg, true));
    }
    
    /**
     * Test overloaded method dispatch without the benefit of xsi:types
     */ 
    public void testOverloadedMethodDispatch() throws Exception
    {
        // invoke the service and verify the result
        String arg = "<arg0>true</arg0>";
        Boolean expected = Boolean.TRUE;
        assertEquals("Overloaded method test failed with a boolean", 
                     expected, 
                     rpc("overloaded", arg, true));        
        
        arg = "<arg0>5</arg0>";
        Integer expectedInt = new Integer(5);
        assertEquals("Overloaded method test failed with an int", 
                     expectedInt, 
                     rpc("overloaded", arg, true));        
    }
    
    public static void main(String args[]) {
      try {
        TestSerializedRPC tester = new TestSerializedRPC("Test Serialized RPC");
        tester.testSerReverseString();
        tester.testSerReverseData();
        tester.testReverseDataWithUntypedParam();
        tester.testArgAsDOM();
        tester.testOverloadedMethodDispatch();
          tester.testOutOfOrderParams();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
}
