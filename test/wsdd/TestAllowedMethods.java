/*
 * Created by IntelliJ IDEA.
 * User: gdaniels
 * Date: Apr 2, 2002
 * Time: 10:14:06 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package test.wsdd;

import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.server.AxisServer;
import org.apache.axis.configuration.XMLStringProvider;
import org.apache.axis.transport.local.LocalTransport;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import junit.framework.TestCase;

public class TestAllowedMethods extends TestCase {
    static final String SERVICE_NAME = "AllowedMethodService";
    private static final String MESSAGE = "Allowed method";

    AxisServer server;
    LocalTransport transport;

    // Two-part WSDD, with a space for scope option in the middle
    static final String doc1 =
            "<deployment xmlns=\"http://xml.apache.org/axis/wsdd/\" " +
                  "xmlns:java=\"" + WSDDConstants.URI_WSDD_JAVA + "\">\n" +
            " <service name=\"" + SERVICE_NAME + "\" " +
                      "provider=\"java:RPC\">\n" +
            "   <parameter name=\"allowedMethods\" value=\"allowed\"/>" +
            "   <parameter name=\"className\" value=\"test.wsdd.TestAllowedMethods\"/>" +
            " </service>\n" +
            "</deployment>";

    public TestAllowedMethods() {
        super("test");
    }

    public TestAllowedMethods(String s) {
        super(s);
    }

    protected void setUp() throws Exception {
        XMLStringProvider config = new XMLStringProvider(doc1);
        server = new AxisServer(config);
        transport = new LocalTransport(server);
        transport.setRemoteService(SERVICE_NAME);
    }

    public void testAllowedMethods() throws Exception {
        Call call = new Call(new Service());
        call.setTransport(transport);

        String ret = (String)call.invoke("allowed", null);
        assertEquals("Return didn't match", MESSAGE, ret);

        try {
            ret = (String)call.invoke("disallowed", null);
        } catch (Exception e) {
            // Success, we shouldn't have been allowed to call that.
            return;
        }

        fail("Successfully called disallowed method!");
    }

    public String disallowed() throws Exception {
        return "You shouldn't have called me!";
    }

    public String allowed() {
        return MESSAGE;
    }
}
