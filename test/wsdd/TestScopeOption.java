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

public class TestScopeOption extends TestCase
{
    static final String HANDLER_NAME = "logger";
    
    // Two-part WSDD, with a space for scope option in the middle
    static final String doc1 = 
            "<deployment xmlns=\"http://xml.apache.org/axis/wsdd/\" " +
                  "xmlns:java=\"" + WSDDConstants.WSDD_JAVA + "\">\n" +
            " <handler type=\"java:org.apache.axis.handlers.LogHandler\" " +
                      "name=\"" + HANDLER_NAME + "\" " +
                      "scope=\"";
    static final String doc2 = "\"/>\n" +
            "</deployment>";

    public TestScopeOption (String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestScopeOption.class);
    }

    protected void setup() {
    }

    /**
     * Initialize an engine with a single handler with per-access scope.
     * Then get the handler from the engine twice, and confirm that we get
     * two different objects.
     * 
     */ 
    public void testPerAccessScope() throws Exception
    {
        String doc = doc1 + "per-access" + doc2;
        XMLStringProvider provider = new XMLStringProvider(doc);
        AxisServer server = new AxisServer(provider);
        
        Handler h1 = server.getHandler(HANDLER_NAME);
        assertNotNull("Couldn't get first logger handler from engine!", h1);
        
        Handler h2 = server.getHandler(HANDLER_NAME);
        assertNotNull("Couldn't get second logger handler from engine!", h2);
        
        assertTrue("Per-access Handlers were identical!", (h1 != h2));
    }
    
    /**
     * Initialize an engine with a single handler of singleton scope.
     * Then get the handler from the engine twice, and confirm that we
     * get the same object both times.
     */ 
    public void testSingletonScope() throws Exception
    {
        String doc = doc1 + "singleton" + doc2;
        XMLStringProvider provider = new XMLStringProvider(doc);
        AxisServer server = new AxisServer(provider);
        
        Handler h1 = server.getHandler(HANDLER_NAME);
        assertNotNull("Couldn't get first logger handler from engine!", h1);
        
        Handler h2 = server.getHandler(HANDLER_NAME);
        assertNotNull("Couldn't get second logger handler from engine!", h2);
        
        assertTrue("Singleton Handlers were different!", (h1 == h2));
    }
    
    public static void main(String[] args) throws Exception {
        TestScopeOption tester = new TestScopeOption("foo");
        tester.testPerAccessScope();
    }
}
