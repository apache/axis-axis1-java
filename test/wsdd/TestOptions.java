package test.wsdd;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.axis.deployment.wsdd.WSDDDocument;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDHandler;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.configuration.XMLStringProvider;
import org.apache.axis.server.AxisServer;
import org.apache.axis.Handler;

public class TestOptions extends TestCase
{
    static final String HANDLER_NAME = "logger";
    static final String PARAM_NAME = "testParam";
    static final String PARAM_VAL  = "testValue";

    // Two-part WSDD, with a space for scope option in the middle
    static final String doc =
            "<deployment xmlns=\"http://xml.apache.org/axis/wsdd/\" " +
                  "xmlns:java=\"" + WSDDConstants.URI_WSDD_JAVA + "\">\n" +
            " <handler type=\"java:org.apache.axis.handlers.LogHandler\" " +
                      "name=\"" + HANDLER_NAME + "\">\n" +
            "  <parameter name=\"" + PARAM_NAME +
                          "\" value=\"" + PARAM_VAL + "\"/>\n" +
            " </handler>\n" +
            " <handler type=\"logger\" name=\"other\"/>\n" +
            "</deployment>";

    public TestOptions (String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestOptions.class);
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
        
        Handler h1 = server.getHandler(HANDLER_NAME);
        assertNotNull("Couldn't get logger handler from engine!", h1);
        
        Object optVal = h1.getOption(PARAM_NAME);
        assertNotNull("Option value was null!", optVal);
        assertEquals("Option was not expected value", optVal, PARAM_VAL);

        optVal = server.getOption("someOptionWhichIsntSet");
        assertNull("Got value for bad option!", optVal);

        Handler h2 = server.getHandler("other");
        assertNotNull("Couldn't get second handler", h2);

        optVal = h1.getOption(PARAM_NAME);
        assertNotNull("Option value was null for 2nd handler!", optVal);
        assertEquals("Option was not expected value for 2nd handler",
                     optVal, PARAM_VAL);

        optVal = server.getOption("someOptionWhichIsntSet");
        assertNull("Got value for bad option on 2nd handler!", optVal);
    }
    
    public static void main(String[] args) throws Exception {
        TestOptions tester = new TestOptions("foo");
        tester.testOptions();
    }
}
