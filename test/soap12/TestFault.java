package test.soap12;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.SerializationContextImpl;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPFault;
import org.apache.axis.server.AxisServer;
import org.apache.axis.soap.SOAPConstants;

import javax.xml.namespace.QName;
import java.io.StringWriter;
import java.io.Writer;

/**
 * A test of soap 1.2 fault serialization and deserialization.
 *
 * @author Andras Avar (andras.avar@nokia.com)
 */
public class TestFault extends TestCase {
    public static Test suite() {
        return new TestSuite(TestFault.class);
    }

    public TestFault(String name) {
        super(name);
    }
    public void setUp() throws Exception {
    }

    public static final QName FAULTCODE = new QName("http://c","faultcode_c");
    public static final QName FAULTSUBCODE[] = { new QName("http://a","subcode_a"),
                                                 new QName("http://b","subcode_b") };
    public static final String FAULTREASON = "reason";
    public static final String FAULTROLE = "role";
    public static final String FAULTNODE = "node";

    public void testFault() throws Exception
    {
        // Serialize
        MessageContext msgContext = new MessageContext(new AxisServer());
        msgContext.setSOAPConstants(SOAPConstants.SOAP12_CONSTANTS);
        SOAPEnvelope msg = new SOAPEnvelope(SOAPConstants.SOAP12_CONSTANTS);

        SOAPFault fault = new SOAPFault(new AxisFault(FAULTCODE, FAULTSUBCODE,
                                                      FAULTREASON, FAULTROLE,
                                                      FAULTNODE, null));

        msg.addBodyElement(fault);
        Writer stringWriter = new StringWriter();
        SerializationContext context = 
                new SerializationContextImpl(stringWriter, msgContext);
        context.setDoMultiRefs(false);
        msg.output(context);
        String msgString = stringWriter.toString();

        // Deserialize and check
        AxisServer server = new AxisServer();
        Message message = new Message(msgString);
        message.setMessageContext(new MessageContext(server));

        SOAPEnvelope envelope = message.getSOAPEnvelope();
        assertNotNull("envelope should not be null", envelope);

        SOAPBodyElement respBody = envelope.getFirstBody();
        assertTrue("respBody should be a SOAPFaultElement", respBody
                        instanceof SOAPFault);
        AxisFault aFault = ((SOAPFault) respBody).getFault();

        assertNotNull("Fault should not be null", aFault);

        assertEquals(FAULTCODE, aFault.getFaultCode());
        assertEquals(FAULTREASON, aFault.getFaultReason());
        assertEquals(FAULTROLE, aFault.getFaultRole());
        assertEquals(FAULTNODE, aFault.getFaultNode());
        QName q[] = aFault.getFaultSubCodes();
        for (int i = 0; i < q.length; i++)
            assertEquals(FAULTSUBCODE[i], q[i]);
    }

}
