package test.wsdd;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.axis.Handler;
import org.apache.axis.client.AdminClient;
import org.apache.axis.configuration.XMLStringProvider;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.local.LocalTransport;

import java.io.ByteArrayInputStream;

/**
 * Try various bad deployments, and make sure that we get back reasonable
 * errors and don't screw up the engine's configuration.
 * 
 * @author Glen Daniels (gdaniels@apache.org)
 */ 
public class TestBadWSDD extends TestCase
{
    static final String HANDLER_NAME = "logger";
    static final String PARAM_NAME = "testParam";
    static final String PARAM_VAL  = "testValue";

    static final String goodWSDD =
            "<deployment xmlns=\"http://xml.apache.org/axis/wsdd/\" " +
                  "xmlns:java=\"" + WSDDConstants.URI_WSDD_JAVA + "\">\n" +
            " <handler type=\"java:org.apache.axis.handlers.LogHandler\" " +
                      "name=\"" + HANDLER_NAME + "\">\n" +
            "  <parameter name=\"" + PARAM_NAME +
                          "\" value=\"" + PARAM_VAL + "\"/>\n" +
            " </handler>\n" +
            " <handler type=\"logger\" name=\"other\"/>\n" +
            " <service name=\"AdminService\" provider=\"java:MSG\">\n" + 
            "  <parameter name=\"allowedMethods\" value=\"AdminService\"/>" +
            "  <parameter name=\"enableRemoteAdmin\" value=\"false\"/>" +
            "  <parameter name=\"className\"" +
                    " value=\"org.apache.axis.utils.Admin\"/>" +
            " </service>" +
            "</deployment>";

    static final String header =
            "<deployment xmlns=\"http://xml.apache.org/axis/wsdd/\" " +
                  "xmlns:java=\"" + WSDDConstants.URI_WSDD_JAVA + "\">\n";
    static final String footer =
            "</deployment>";

    static final String badHandler = 
            " <handler name=\"nameButNoType\"/>\n";

    public TestBadWSDD (String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestBadWSDD.class);
    }

    protected void setup() {
    }

    /**
     * Initialize an engine with a single handler with a parameter set, and
     * another reference to that same handler with a different name.
     *
     * Make sure the param is set for both the original and the reference
     * handler.
     * 
     */ 
    public void testOptions() throws Exception
    {
        XMLStringProvider provider = new XMLStringProvider(goodWSDD);
        AxisServer server = new AxisServer(provider);
        
        Handler h1 = server.getHandler(HANDLER_NAME);
        assertNotNull("Couldn't get logger handler from engine!", h1);

        AdminClient client = new AdminClient(true);
        String doc = header + badHandler + footer;
        ByteArrayInputStream stream = new ByteArrayInputStream(doc.getBytes());
        
        LocalTransport transport = new LocalTransport(server);
        transport.setUrl("local:///AdminService");
        client.getCall().setTransport(transport);
        try {
            client.process(stream);
        } catch (Exception e) {
             return;
        }
        
        fail("Successfully processed bad WSDD!");
    }
    
    public static void main(String[] args) throws Exception {
        TestBadWSDD tester = new TestBadWSDD("foo");
        tester.testOptions();
    }
}
