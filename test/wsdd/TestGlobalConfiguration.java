package test.wsdd;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.axis.deployment.wsdd.WSDDDocument;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDHandler;
import org.apache.axis.configuration.XMLStringProvider;
import org.apache.axis.server.AxisServer;

public class TestGlobalConfiguration extends TestCase
{
    static final String PARAM_NAME = "testParam";
    static final String PARAM_VAL  = "testValue";
    
    String doc = 
            "<deployment xmlns=\"http://xml.apache.org/axis/wsdd/\">\n" +
            " <globalConfiguration>\n" +
            "  <parameter name=\"" + PARAM_NAME +
                          "\" value=\"" + PARAM_VAL + "\"/>\n" +
            " </globalConfiguration>\n" +
            "</deployment>";

    public TestGlobalConfiguration (String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestGlobalConfiguration.class);
    }

    protected void setup() {
    }

    public void testEngineProperties() throws Exception
    {
        XMLStringProvider provider = new XMLStringProvider(doc);
        AxisServer server = new AxisServer(provider);
        
        Object optVal = server.getOption(PARAM_NAME);
        assertNotNull("Option value was null!", optVal);
        assertEquals("Option was not expected value", optVal, PARAM_VAL);
        
        optVal = server.getOption("someOptionWhichIsntSet");
        assertNull("Got value for bad option!", optVal);
    }
    
    public static void main(String[] args) throws Exception {
        TestGlobalConfiguration tester = new TestGlobalConfiguration("foo");
        tester.testEngineProperties();
    }
}
