package test.wsdd;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.axis.Handler;
import org.apache.axis.client.AdminClient;
import org.apache.axis.client.Call;
import org.apache.axis.configuration.XMLStringProvider;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.local.LocalTransport;

import java.io.ByteArrayInputStream;

/**
 * Test WSDD functions via the AdminService.
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
 */ 
public class TestAdminService extends TestCase
{
    static final String HANDLER_NAME = "logger";
    static final String PARAM_NAME = "testParam";
    static final String PARAM_VAL  = "testValue";

    static final String deployDoc =
            "<deployment xmlns=\"http://xml.apache.org/axis/wsdd/\" " +
                  "xmlns:java=\"" + WSDDConstants.URI_WSDD_JAVA + "\">\n" +
            " <handler type=\"java:org.apache.axis.handlers.LogHandler\" " +
                      "name=\"" + HANDLER_NAME + "\">\n" +
            "  <parameter name=\"" + PARAM_NAME +
                          "\" value=\"" + PARAM_VAL + "\"/>\n" +
            " </handler>\n" +
            " <handler type=\"logger\" name=\"other\"/>\n" +
            " <service name=\"AdminService\" provider=\"java:MSG\">\n" +
            "  <parameter name=\"className\" value=\"org.apache.axis.utils.Admin\"/>" +
            "  <parameter name=\"methodName\" value=\"AdminService\"/>\n" +
            " </service>\n" +
            "</deployment>";
    
    static final String undeployDoc =
            "<undeployment xmlns=\"http://xml.apache.org/axis/wsdd/\">\n" +
            " <handler name=\"other\"/>\n" +
            "</undeployment>";

    public TestAdminService (String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestAdminService.class);
    }

    protected void setup() {
    }

    /**
     * Load up a server with a couple of handlers as spec'ed above,
     * then undeploy one of them.  Confirm that all looks reasonable
     * throughout.
     */ 
    public void testUndeployHandlerViaAdmin() throws Exception
    {
        XMLStringProvider provider = new XMLStringProvider(deployDoc);
        AxisServer server = new AxisServer(provider);
        
        Handler handler = server.getHandler("other");
        assertNotNull("Couldn't get handler", handler);

        AdminClient client = new AdminClient();
        Call call = client.getCall();
        LocalTransport transport = new LocalTransport(server);
        transport.setRemoteService("AdminService");

        call.setTransport(transport);
        client.process(new ByteArrayInputStream(undeployDoc.getBytes()));

        server.refreshGlobalOptions();
        
        handler = server.getHandler("other");
        assertNull("Undeployed handler is still available", handler);
        
        handler = server.getHandler(HANDLER_NAME);
        assertNotNull("Couldn't get handler (2nd time)", handler);
    }
    
    public static void main(String[] args) throws Exception {
        TestAdminService tester = new TestAdminService("foo");
        tester.testUndeployHandlerViaAdmin();
    }
}
