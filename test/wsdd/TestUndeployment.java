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
import org.apache.axis.utils.Options;
import org.apache.axis.utils.XMLUtils;
import org.apache.axis.client.AdminClient;
import org.apache.axis.transport.local.LocalTransport;

import java.io.StringReader;
import java.io.StringBufferInputStream;
import java.io.InputStream;

/**
 * Test WSDD undeployment.
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
 */ 
public class TestUndeployment extends TestCase
{
    static final String HANDLER_NAME = "logger";
    static final String PARAM_NAME = "testParam";
    static final String PARAM_VAL  = "testValue";

    static final String deployDoc =
            "<deployment xmlns=\"http://xml.apache.org/axis/wsdd/\" " +
                  "xmlns:java=\"" + WSDDConstants.WSDD_JAVA + "\">\n" +
            " <handler type=\"java:org.apache.axis.handlers.LogHandler\" " +
                      "name=\"" + HANDLER_NAME + "\">\n" +
            "  <parameter name=\"" + PARAM_NAME +
                          "\" value=\"" + PARAM_VAL + "\"/>\n" +
            " </handler>\n" +
            " <handler type=\"logger\" name=\"other\"/>\n" +
            "</deployment>";
    
    static final String undeployDoc =
            "<undeployment xmlns=\"http://xml.apache.org/axis/wsdd/\">\n" +
            " <handler name=\"other\"/>\n" +
            "</undeployment>";

    public TestUndeployment (String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestUndeployment.class);
    }

    protected void setup() {
    }

    /**
     * Load up a server with a couple of handlers as spec'ed above,
     * then undeploy one of them.  Confirm that all looks reasonable
     * throughout.
     */ 
    public void testUndeployHandler() throws Exception
    {
        XMLStringProvider provider = new XMLStringProvider(deployDoc);
        AxisServer server = new AxisServer(provider);
        
        Handler handler = server.getHandler("other");
        assertNotNull("Couldn't get handler", handler);

        InputStream is = new StringBufferInputStream(undeployDoc);
        WSDDDocument doc = new WSDDDocument(XMLUtils.newDocument(is));

        WSDDDeployment dep = provider.getDeployment();
        doc.deploy(dep);

        server.refreshGlobalOptions();
        
        handler = server.getHandler("other");
        assertNull("Undeployed handler is still available", handler);
        
        handler = server.getHandler(HANDLER_NAME);
        assertNotNull("Couldn't get handler (2nd time)", handler);
    }
    
    public static void main(String[] args) throws Exception {
        TestUndeployment tester = new TestUndeployment("foo");
        tester.testUndeployHandler();
    }
}
