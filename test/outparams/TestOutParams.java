package test.outparams;

import junit.framework.TestCase;
import org.apache.axis.Constants;
import org.apache.axis.Handler;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.local.LocalTransport;

import javax.xml.rpc.namespace.QName;
import java.util.Map;

/**
 * Test org.apache.axis.handlers.RPCDispatcher
 *
 * @author Sam Ruby <rubys@us.ibm.com>
 */
public class TestOutParams extends TestCase {
    private final String serviceURN = "urn:X-test-outparams";


    /** A fixed message, since the return is hardcoded */
    private final String message =
        "<?xml version=\"1.0\"?>\n" +
        "<soap:Envelope " +
             "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
             "xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" " +
             "xmlns:xsi=\"" + Constants.URI_DEFAULT_SCHEMA_XSI + "\" " +
             "xmlns:xsd=\"" + Constants.URI_DEFAULT_SCHEMA_XSD + "\">\n" +
             "<soap:Body>\n" +
             "<ns:someMethod xmlns:ns=\"" + serviceURN + "\"/>\n" +
             "</soap:Body>\n" +
        "</soap:Envelope>\n";

    private Service s_service = null ;
    private Call    client    = null ;
    private SimpleProvider provider = new SimpleProvider();
    private AxisServer server = new AxisServer(provider);

    public TestOutParams(String name) {
        super(name);
        server.init();
    }

    /**
     * Test returning output params
     */
    public void testOutputParams() throws Exception {
        // Register the service
        Handler h = new ServiceHandler();
        s_service = new Service();
        client  = (Call) s_service.createCall();

        // ??? Do we need to register the handler?

        SOAPService service = new SOAPService(h);
        provider.deployService(serviceURN, service);

        // Make sure the local transport uses the server we just configured
        client.setTransport(new LocalTransport(server));

        // Create the message context
        MessageContext msgContext = new MessageContext(server);

        // Construct the soap request
        SOAPEnvelope envelope = new SOAPEnvelope();
        msgContext.setRequestMessage(new Message(envelope));

        client.addParameter(
                new QName("", "string"),
                XMLType.XSD_STRING,
                javax.xml.rpc.ParameterMode.IN);
        client.setReturnType(XMLType.XSD_INT);
        // Invoke the Axis server
        Object ret = client.invoke(serviceURN, "method",
                                new Object [] { "test" });

        Map outParams = client.getOutputParams();
        assertNotNull("No output Params returned!", outParams);

        Object param = outParams.get(new QName(null, "out1"));
        assertEquals("Param out1 does not equal expected value", ServiceHandler.OUTPARAM1, param);

        param = outParams.get(new QName(null, "out2"));
        assertEquals("Param out2 does not equal expected value", ServiceHandler.OUTPARAM2, param);

        assertEquals("Return value does not equal expected value", ((Integer)ret).intValue(), ServiceHandler.RESPONSE.intValue());
    }

    public static void main(String args[])
    {
      try {
        TestOutParams tester = new TestOutParams("RPC test");
        tester.testOutputParams();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
}
