package test.soap12;

import junit.framework.TestCase;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.server.AxisServer;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.Messages;

import java.util.Vector;
/**
 * Test deserialization of SOAP responses
 */
public class TestDeser extends TestCase {

    private AxisServer server = new AxisServer();

    public TestDeser(String name) {
        super(name);
    }
    
    String text1 = 
        "<?xml version=\"1.0\"?>\n" +
        "<soap:Envelope " +
          "xmlns:soap=\"http://www.w3.org/2002/06/soap-envelope\" " +
          "xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" " +
          "xmlns:me=\"http://soapinterop.org/xsd\" " +
          "xmlns:this=\"http://encoding.test\" " + 
          "xmlns:xsi=\"" + Constants.URI_DEFAULT_SCHEMA_XSI + "\" " +
          "xmlns:xsd=\"" + Constants.URI_DEFAULT_SCHEMA_XSD + "\">\n" +
          "<soap:Body>\n" +
            "<methodResult xmlns=\"http://tempuri.org/\">\n" +
            "<item xsi:type=\"xsd:string\">abc</item>\n" +
            "</methodResult>\n" +
          "</soap:Body>\n";
    String text2 =
        "</soap:Envelope>\n";

    public void testDeser1() throws Exception {
        assertEquals(deserialize(""), "abc");
    }
    
    public void testDeser2() throws Exception {
        boolean expectedExceptionThrown = false;
        try {
            deserialize("<hello/>");
        } catch (org.apache.axis.AxisFault af) {
            String expected = Messages.getMessage("noElemAfterBody12");
            if(af.getFaultString().indexOf(expected)!=-1)
                expectedExceptionThrown = true;
        }
        assertTrue(expectedExceptionThrown);
    }
            
    public String deserialize (String extra) throws Exception {
        Message message = new Message(text1 + extra + text2);
        MessageContext context = new MessageContext(server);
        context.setSOAPConstants(SOAPConstants.SOAP12_CONSTANTS);
        
        message.setMessageContext(context);

        SOAPEnvelope envelope = message.getSOAPEnvelope();
        assertNotNull("SOAP envelope should not be null", envelope);

        RPCElement body = (RPCElement)envelope.getFirstBody();
        assertNotNull("SOAP body should not be null", body);

        Vector arglist = body.getParams();
        assertNotNull("arglist", arglist);
        assertTrue("param.size()<=0 {Should be > 0}", arglist.size()>0);

        RPCParam param = (RPCParam) arglist.get(0);
        assertNotNull("SOAP param should not be null", param);

        return (String)param.getValue();
    }
}
