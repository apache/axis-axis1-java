package test.soap12;

import junit.framework.TestCase;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.server.AxisServer;
import org.apache.axis.utils.Messages;

import java.util.Vector;

/**
 * Test deserialization of SOAP 1.2 messages with references, by putting the
 * actual value in various places in the message.
 */
public class TestHrefs extends TestCase {

    private String HEAD;
    private String HEADERT;
    private String BODYT;

    public TestHrefs(String name) {
        this(name, Constants.URI_DEFAULT_SCHEMA_XSI,
             Constants.URI_DEFAULT_SCHEMA_XSD);
    }

    public TestHrefs(String name, String NS_XSI, String NS_XSD) {
        super(name);

        HEAD =
                "<?xml version=\"1.0\"?>\n" +
                "<soap:Envelope " +
                "xmlns:soap=\"http://www.w3.org/2002/12/soap-envelope\" " +
                "xmlns:soapenc=\"http://www.w3.org/2002/12/soap-encoding\" " +
                "xmlns:xsi=\"" + NS_XSI + "\" " +
                "xmlns:xsd=\"" + NS_XSD + "\">\n" +
                "<soap:Header>\n";

        HEADERT = "</soap:Header>\n" +
                  "<soap:Body>\n" +
                  "<methodResult xmlns=\"http://tempuri.org/\">\n";

        BODYT = "</methodResult>\n" +
                 "</soap:Body>\n" +
                 "</soap:Envelope>\n";
    }

    private void deserialize(String data, Object expected, int pos)
            throws Exception
    {
        Message message = new Message(data);
        MessageContext context = new MessageContext(new AxisServer());
        message.setMessageContext(context);
        context.setProperty(Constants.MC_NO_OPERATION_OK, Boolean.TRUE);

        SOAPEnvelope envelope = (SOAPEnvelope)message.getSOAPEnvelope();
        assertNotNull("SOAP envelope should not be null", envelope);

        RPCElement body = (RPCElement)envelope.getFirstBody();
        assertNotNull("SOAP body should not be null", body);

        Vector arglist = body.getParams();
        assertNotNull("arglist", arglist);
        assertTrue("SOAP param.size()<=0 {Should be > 0}", arglist.size()>0);

        RPCParam param = (RPCParam) arglist.get(pos);
        assertNotNull("SOAP param should not be null", param);

        Object result = param.getValue();
        assertEquals("Expected result not received", expected, result);
    }

    public void testStringReference1() throws Exception {
        String result = HEAD + HEADERT +
                        "<result root=\"0\" id=\"1\" xsi:type=\"xsd:string\">abc</result>" +
                        "<reference ref=\"#1\"/>\n" +
                        BODYT;
        deserialize(result, "abc", 1);
    }

    public void testIntReference1() throws Exception {
        String result = HEAD + HEADERT +
                        "<result root=\"0\" id=\"1\" xsi:type=\"xsd:int\">567</result>" +
                        "<reference ref=\"#1\"/>\n" +
                        BODYT;
        deserialize(result, new Integer(567), 1);
    }

    public void testStringReferenceInHeader() throws Exception {
        String result = HEAD +
                        "<result root=\"0\" id=\"1\" xsi:type=\"xsd:string\">abc</result>" +
                        HEADERT +
                        "<reference ref=\"#1\"/>\n" +
                        BODYT;
        deserialize(result, "abc", 0);
    }

    public void testIDANDHREF() throws Exception {
        String result = HEAD +
                        HEADERT +
                        "<result root=\"0\" ref=\"#1\" id=\"1\" xsi:type=\"xsd:string\">abc</result>" +
                        BODYT;
        try {
            deserialize(result, "abc", 0);
        } catch (AxisFault af) {
            assertTrue(af.getFaultString().indexOf(Messages.getMessage("noIDandHREFonSameElement")) != -1 &&
                               Constants.FAULT_SOAP12_SENDER.equals(af.getFaultCode()));
            return;
        }
        fail("Didn't got the expected fault");

    }


}