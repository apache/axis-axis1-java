package test.outparams2;

import junit.framework.TestCase;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.Message;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.configuration.DefaultEngineConfigurationFactory;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.providers.java.RPCProvider;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.local.LocalTransport;

import javax.xml.rpc.holders.StringHolder;
import javax.xml.namespace.QName;

public class TestOutParams extends TestCase { 

      /** A fixed message, with no parameters */
    private final String message =
	"<?xml version=\"1.0\"?>\n" +
	"<soapenv:Envelope " +
	"xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
	"xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
	"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
	"<soapenv:Body>\n" +
	"<ns1:serviceMethod soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" " +
	"xmlns:ns1=\"outParamsTest\"> </ns1:serviceMethod>\n" +
	"</soapenv:Body></soapenv:Envelope>\n";

    private Service s_service = null ;
    private Call    client    = null ;
    private SimpleProvider provider = new SimpleProvider();
    private AxisServer server = new AxisServer(provider);

    private static boolean called = false;

    public TestOutParams(String name) {
        super(name);
        server.init();
    }

    public TestOutParams() {
	super("Test Out Params");
    }

    public void testOutputParams() throws Exception {
        // Register the service
        s_service = new Service();
        client  = (Call) s_service.createCall();

	SOAPService service = new SOAPService(null, 
                                              new RPCProvider(),
                                              null);
	service.setName("TestOutParamsService");
        service.setOption("className", "test.outparams2.TestOutParams");
        service.setOption("allowedMethods", "serviceMethod");

        EngineConfiguration defaultConfig =
            (new DefaultEngineConfigurationFactory()).getServerEngineConfig();
        SimpleProvider config = new SimpleProvider(defaultConfig);
        config.deployService("outParamsTest", service);
        provider.deployService("outParamsTest", service);

        // Make sure the local transport uses the server we just configured
        client.setTransport(new LocalTransport(server));

	Message msg = new Message(message, false);
	SOAPEnvelope env = msg.getSOAPEnvelope();

	// test invocation. test Holder parameter defaults to INOUT type
	client.invoke(env);
	
	// method was succesfully invoked
	assertTrue(called);
	
	ServiceDesc description = null;
	OperationDesc operation = null;
	ParameterDesc parameter = null;

	description = service.getServiceDescription();
	operation = description.getOperationByName("serviceMethod");
	parameter = operation.getParamByQName(new QName("", "out1"));
	    
	assertEquals(ParameterDesc.INOUT, parameter.getMode());

	// Changing output parameter to OUT type.
	parameter.setMode(ParameterDesc.OUT);

	// rest called
	called = false;

	// invoke again
	client.invoke(env);
	assertTrue(this.called);
    }
    
    public void serviceMethod(String in1, StringHolder out1) {
	called = true;
    }

    public static void main(String args[])
    {
	try {
	    TestOutParams tester = new TestOutParams("OutParams Test");
	    tester.testOutputParams();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
