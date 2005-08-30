package test.outparams2;

import javax.xml.rpc.holders.StringHolder;

import junit.framework.TestCase;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.Message;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.configuration.DefaultEngineConfigurationFactory;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.description.JavaServiceDesc;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.providers.java.RPCProvider;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.local.LocalTransport;

/**
 * Test if operation is invoked with parameters in good order when out parameter
 * is not the last parameter. <br>
 * It makes sure that bug described at
 * http://issues.apache.org/jira/browse/AXIS-1975 is corrected.
 * 
 * @author Cedric Chabanois (cchabanois@natsystem.fr)
 */
public class TestOutParams2 extends TestCase {

    /** A fixed message, with one parameter */
    private final String message = "<?xml version=\"1.0\"?>\n"
            + "<soapenv:Envelope "
            + "xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
            + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
            + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
            + "<soapenv:Body>\n"
            + "<ns1:serviceMethod soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" "
            + "xmlns:ns1=\"outParamsTest\"><in1>18</in1></ns1:serviceMethod>\n"
            + "</soapenv:Body></soapenv:Envelope>\n";

    private Service s_service = null;

    private Call client = null;

    private SimpleProvider provider = new SimpleProvider();

    private AxisServer server = new AxisServer(provider);

    private static boolean called = false;

    public TestOutParams2(String name) {
        super(name);
        server.init();
    }

    public TestOutParams2() {
        super("Test Out Params");
    }

    public void testOutputParams() throws Exception {
        // Register the service
        s_service = new Service();
        client = (Call) s_service.createCall();

        SOAPService service = new SOAPService(null, new RPCProvider(), null);
        service.setName("TestOutParamsService");
        service.setOption("className", "test.outparams2.TestOutParams2");
        service.setOption("allowedMethods", "serviceMethod");

        ServiceDesc description = new JavaServiceDesc();
        OperationDesc operation = new OperationDesc();
        operation.setName("serviceMethod");
        ParameterDesc out1 = new ParameterDesc();
        out1.setName("out1");
        out1.setMode(ParameterDesc.OUT);
        operation.addParameter(out1);
        ParameterDesc in1 = new ParameterDesc();
        in1.setName("in1");
        in1.setMode(ParameterDesc.IN);
        operation.addParameter(in1);
        description.addOperationDesc(operation);
        service.setServiceDescription(description);

        EngineConfiguration defaultConfig = (new DefaultEngineConfigurationFactory())
                .getServerEngineConfig();
        SimpleProvider config = new SimpleProvider(defaultConfig);
        config.deployService("outParamsTest", service);
        provider.deployService("outParamsTest", service);

        // Make sure the local transport uses the server we just configured
        client.setTransport(new LocalTransport(server));

        Message msg = new Message(message, false);
        SOAPEnvelope env = msg.getSOAPEnvelope();

        // invoke
        client.invoke(env);
        assertTrue(this.called);
    }

    public void serviceMethod(StringHolder out1, byte in1) {
        called = true;
    }

    public static void main(String args[]) {
        try {
            TestOutParams2 tester = new TestOutParams2("OutParams Test");
            tester.testOutputParams();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
