package test.wsdd;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.axis.Handler;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.configuration.XMLStringProvider;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.server.AxisServer;

import java.util.List;

public class TestRoles extends TestCase
{
    static final String GLOBAL_ROLE = "http://apache.org/globalRole";
    static final String SERVICE_ROLE = "http://apache.org/serviceRole";
    static final String SERVICE_NAME = "roleService";

    static final String doc =
            "<deployment xmlns=\"http://xml.apache.org/axis/wsdd/\" " +
                  "xmlns:java=\"" + WSDDConstants.URI_WSDD_JAVA + "\">\n" +
            " <globalConfiguration>\n" +
            "  <role>" + GLOBAL_ROLE + "</role>\n" +
            " </globalConfiguration>\n" +
            " <service name=\"" + SERVICE_NAME + "\">\n" +
            "  <parameter name=\"className\" value=\"test.wsdd.TestRoles\"/>\n" +
            "  <role>" + SERVICE_ROLE + "</role>" +
            " </service>\n"+
            "</deployment>";

    public TestRoles (String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestRoles.class);
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
        XMLStringProvider provider = new XMLStringProvider(doc);
        AxisServer server = new AxisServer(provider);

        SOAPService service = server.getService(SERVICE_NAME);
        assertNotNull("Couldn't get service from engine!", service);

        List roles = service.getRoles();
        assertTrue("Service role not accessible",
                   roles.contains(SERVICE_ROLE));
        assertTrue("Global role not accessible",
                   roles.contains(GLOBAL_ROLE));

        roles = service.getServiceActors();
        assertTrue("Service role not accessible from specific list",
                   roles.contains(SERVICE_ROLE));
        assertFalse("Global role is accessible from specific list",
                   roles.contains(GLOBAL_ROLE));
    }
    
    public static void main(String[] args) throws Exception {
        TestRoles tester = new TestRoles("foo");
        tester.testOptions();
    }
}
