package test.soap12;

import junit.framework.TestCase;
import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.providers.java.RPCProvider;
import org.apache.axis.server.AxisServer;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.transport.local.LocalTransport;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.util.Vector;

/**
 * A test for RPC response
 *
 * @author Andras Avar (andras.avar@nokia.com)
 */
public class TestRPC extends TestCase {

    private SimpleProvider provider = new SimpleProvider();
    private AxisServer server = new AxisServer(provider);

    public TestRPC(String name) {
        super(name);
        server.init();
    }

    private RPCElement rpc(String method, Object[] params)
        throws AxisFault, SAXException
    {
        String SERVICE_NAME = "echoservice";
        LocalTransport transport = new LocalTransport(server);

        SOAPService service = new SOAPService(new RPCProvider());
        service.setOption("className", "test.soap12.Echo");
        service.setOption("allowedMethods", "*");

        ServiceDesc desc = service.getInitializedServiceDesc(null);
        desc.setDefaultNamespace(method);

        provider.deployService(SERVICE_NAME, service);

        MessageContext msgContext = new MessageContext(server);
        msgContext.setSOAPConstants(SOAPConstants.SOAP12_CONSTANTS);

        String methodNS = null;
        msgContext.setTargetService(SERVICE_NAME);

        // Construct the soap request
        SOAPEnvelope envelope = new SOAPEnvelope();
        msgContext.setRequestMessage(new Message(envelope));
        RPCElement body = new RPCElement(methodNS, method, params);

        envelope.addBodyElement(body);

        server.invoke(msgContext);

        Message message = msgContext.getResponseMessage();
        envelope = (SOAPEnvelope)message.getSOAPEnvelope();
        assertNotNull("SOAP envelope was null", envelope);
        body = (RPCElement)envelope.getFirstBody();

        return body;
    }

    public void testRPCReturn() throws Exception {

        RPCElement body = rpc("echo", new Object[] {"abc"});
        assertNotNull("SOAP body was null", body);

        // Check RPC result
        Vector arglist = body.getParams();
        assertNotNull("SOAP argument list was null", arglist);
        RPCParam param = (RPCParam) arglist.get(1);
        assertTrue("Not expected result", ((String)param.getValue()).equals("abc"));

        // Check DOM
        Element e = body.getAsDOM();
        NodeList l = e.getElementsByTagNameNS("http://www.w3.org/2002/06/soap-rpc","result");
        assertTrue("No result element was fount", l.getLength() == 1);
        String ptr = l.item(0).getFirstChild().getNodeValue();
        assertNotNull("Ptr to the result value was null", ptr);
        l = e.getElementsByTagName(ptr);
        assertTrue("No return element was fount", l.getLength() == 1);
        String res = l.item(0).getFirstChild().getNodeValue();
        assertTrue("Not expected result", res.equals("abc"));
    }
}
