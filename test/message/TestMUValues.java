package test.message;

import junit.framework.TestCase;
import org.apache.axis.AxisEngine;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.server.AxisServer;

/**
 * This test confirms the behavior of the various possible values for
 * the mustUnderstand attribute in both SOAP 1.1 and SOAP 1.2.  In particular:
 * 
 * For SOAP 1.1, the only valid values are "0" and "1"
 * For SOAP 1.2, "0"/"false" and "1"/"true" are valid
 * 
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class TestMUValues extends TestCase {
    private AxisEngine engine;

    public TestMUValues(String name) {
        super(name);
    }

    private String header =
        "<?xml version=\"1.0\"?>\n" +
        "<soap:Envelope " +
          "xmlns:soap=\"";
    
    private String middle = "\">\n" +
          "<soap:Header>\n" +
            "<test soap:mustUnderstand=\"";
    
    private String footer =
            "\"/>\n" +
          "</soap:Header>\n" +
          "<soap:Body>\n" +
            "<noContent/>\n" +
          "</soap:Body>\n" +
        "</soap:Envelope>\n";

    public void setUp() throws Exception {
        SimpleProvider provider = new SimpleProvider();
        engine = new AxisServer(provider);
    }
    
    public void checkVal(String val, boolean desiredResult, String ns)
            throws Exception {
        String request = header + ns + middle + val + footer;

        // create a message in context
        MessageContext msgContext = new MessageContext(engine);
        Message message = new Message(request);
        message.setMessageContext(msgContext);

        // Parse the message and check the mustUnderstand value
        SOAPEnvelope envelope = message.getSOAPEnvelope();
        SOAPHeaderElement header = envelope.getHeaderByName("", "test");
        assertEquals("MustUnderstand value wasn't right using value '" +
                     val + "'",
                     desiredResult, header.getMustUnderstand());
    }

    public void testMustUnderstandValues() throws Exception {
        String soap12 = Constants.URI_SOAP12_ENV;
        String soap11 = Constants.URI_SOAP11_ENV;
        
        checkVal("0", false, soap12);
        checkVal("1", true, soap12);
        checkVal("true", true, soap12);
        checkVal("false", false, soap12);
        try {
            checkVal("dennis", false, soap12);
            fail("Didn't throw exception with bad MU value");
        } catch (Exception e) {
        }

        checkVal("0", false, soap11);
        checkVal("1", true, soap11);
        try {
            checkVal("true", false, soap11);
            fail("Didn't throw exception with bad MU value");
        } catch (Exception e) {
        }
        try {
            checkVal("false", false, soap11);
            fail("Didn't throw exception with bad MU value");
        } catch (Exception e) {
        }
        try {
            checkVal("dennis", false, soap11);
            fail("Didn't throw exception with bad MU value");
        } catch (Exception e) {
        }
    }
}
