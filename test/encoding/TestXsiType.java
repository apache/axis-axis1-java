package test.encoding;

import org.apache.axis.MessageContext;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.RPCElement;
import org.apache.axis.encoding.*;
import org.apache.axis.server.AxisServer;
import org.apache.axis.utils.QName;
import org.apache.axis.client.Call;

import java.io.Writer;
import java.io.StringWriter;

import junit.framework.TestCase;

/**
 * Verify that shutting off xsi:types in the ServiceDescription works
 * as expected.
 */
public class TestXsiType extends TestCase {

    private String header;
    private String footer;
    private AxisServer server = new AxisServer();

    public TestXsiType(String name) {
        super(name);
    }

    public void testNoXsiTypes()
       throws Exception
    {
        MessageContext msgContext = new MessageContext(new AxisServer());
        ServiceDescription sd = new ServiceDescription("testXsiType", true);

        // Don't serialize xsi:type attributes
        msgContext.setProperty(Call.SEND_TYPE_ATTR, "false" );

        msgContext.setProperty(MessageContext.SERVICE_DESCRIPTION, sd);

        SOAPEnvelope msg = new SOAPEnvelope();
        RPCParam arg1 = new RPCParam("urn:myNamespace",
                                     "testParam",
                                     "this is a string");
        RPCElement body = new RPCElement("urn:myNamespace",
                                         "method1",
                                         new Object[]{ arg1 });
        msg.addBodyElement(body);

        Writer stringWriter = new StringWriter();
        SerializationContext context = new SerializationContext(stringWriter,
                                                                msgContext);

        msg.output(context);

        String msgString = stringWriter.toString();
        assertTrue("Found unexpected xsi:type!",
                   msgString.indexOf("xsi:type") == -1);
    }


    public static void main(String [] args) throws Exception
    {
        TestXsiType tester = new TestXsiType("test");
        tester.testNoXsiTypes();
    }
}
