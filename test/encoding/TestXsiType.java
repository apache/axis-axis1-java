package test.encoding;

import junit.framework.TestCase;
import org.apache.axis.MessageContext;
import org.apache.axis.AxisEngine;
import org.apache.axis.transport.local.LocalTransport;
import org.apache.axis.providers.java.RPCProvider;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.client.Call;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.server.AxisServer;

import java.io.StringWriter;
import java.io.Writer;

/**
 * Verify that shutting off xsi:types in the Message Context works
 * as expected.
 */
public class TestXsiType extends TestCase {

    private String header;
    private String footer;
    private AxisServer server = new AxisServer();
    
    public TestXsiType()
    {
        super("testing");
    }

    public TestXsiType(String name) {
        super(name);
    }

    /**
     * Trivial test just to make sure there aren't xsi:type attributes
     */ 
    public void testNoXsiTypes()
       throws Exception
    {
        MessageContext msgContext = new MessageContext(new AxisServer());

        // Don't serialize xsi:type attributes
        msgContext.setProperty(Call.SEND_TYPE_ATTR, "false" );

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
    
    /**
     * More complex test which checks to confirm that we can still
     * in fact deserialize if we know the type via the Call object.
     */ 
    public void testTypelessDeserialization() throws Exception
    {
        // Set up a server to NOT send XSI types, and deploy
        // this class as a service there.
        
        AxisServer server = new AxisServer();
        server.addOption(AxisEngine.PROP_SEND_XSI, Boolean.FALSE);
        
        SOAPService service = new SOAPService(new RPCProvider());
        service.addOption("className", "test.encoding.TestXsiType");
        service.addOption("methodName", "*");
        server.deployService("TestService", service);
        
        // Call that same server, accessing a method we know returns
        // a double.  We should figure this out and deserialize it
        // correctly, even without the xsi:type attribute, because
        // we set the return type manually.
        
        Call call = new Call();
        call.setTransport(new LocalTransport(server));
        call.setReturnType(XMLType.XSD_DOUBLE);
        
        Object result = call.invoke("TestService", 
                                    "serviceMethod", 
                                    new Object [] {});
        
        assertTrue("Return value was not the expected type (Double)!",
                    (result instanceof Double));
    }
    
    /**
     * A method for our service, returning a double.
     */ 
    public double serviceMethod()
    {
        return 3.14159;
    }

    public static void main(String [] args) throws Exception
    {
        TestXsiType tester = new TestXsiType("test");
        tester.testNoXsiTypes();
        tester.testTypelessDeserialization();
    }
}
