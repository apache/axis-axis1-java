package test.session;

import junit.framework.TestCase;
import org.apache.axis.session.SimpleSession;
import org.apache.axis.session.Session;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.handlers.EchoHandler;
import org.apache.axis.handlers.SimpleSessionHandler;
import org.apache.axis.client.Service;
import org.apache.axis.client.Call;
import org.apache.axis.transport.local.LocalTransport;
import org.apache.axis.server.AxisServer;
import org.apache.axis.MessageContext;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.configuration.XMLStringProvider;
import org.apache.axis.configuration.DefaultEngineConfigurationFactory;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.providers.java.RPCProvider;

import javax.xml.rpc.namespace.QName;


/** 
 * Test deserialization of SOAP responses
 */
public class TestSimpleSession extends TestCase {
    static final String clientWSDD =
            "<deployment xmlns=\"http://xml.apache.org/axis/wsdd/\" " +
                  "xmlns:java=\"" + WSDDConstants.WSDD_JAVA + "\">\n" +
            " <handler type=\"java:org.apache.axis.handlers.SimpleSessionHandler\" " +
                      "name=\"SimpleSessionHandler\"/>\n" +
            " <service name=\"sessionTest\">\n" +
            "  <requestFlow><handler type=\"SimpleSessionHandler\"/></requestFlow>\n" +
            "  <responseFlow><handler type=\"SimpleSessionHandler\"/></responseFlow>\n" +
            " </service>\n" +
            " <transport name=\"local\" " +
                "pivot=\"java:org.apache.axis.transport.local.LocalSender\"/>\n" +
            "</deployment>";
    static XMLStringProvider clientProvider = new XMLStringProvider(clientWSDD);

    /**
     * Default constructor for use as service
     */
    public TestSimpleSession()
    {
        super("serviceTest");
    }

    public TestSimpleSession(String name)
    {
        super(name);
    }
    
    public void testSessionAPI() {
        SimpleSession session = new SimpleSession();
        Object val = new Float(5.6666);
        session.set("test", val);
        
        assertEquals("\"test\" equals \"" + session.get("test") + "\", not \"" + val + "\" as expected",
                     val, session.get("test"));
        
        session.remove("test");
        
        assertNull("Did not remove \"test\" from the session successfully", session.get("test"));
    }

    /**
     * Actually test the session functionality using SOAP headers.
     *
     * Deploy a simple RPC service which returns a session-based call
     * counter.  Check it out using local transport.  To do this we need to
     * make sure the SimpleSessionHandler is deployed on the request and
     * response chains of both the client and the server.
     *
     */
    public void testSessionService() throws Exception
    {
        // Set up the server side
        SimpleSessionHandler sessionHandler = new SimpleSessionHandler();
        // Set a 3-second reap period, and a 3-second timeout
        sessionHandler.setReapPeriodicity(3000);
        sessionHandler.setDefaultSessionTimeout(3000);

        SOAPService service = new SOAPService(sessionHandler,
                                              new RPCProvider(),
                                              sessionHandler);

        service.setOption("scope", "session");
        service.setOption("className", "test.session.TestSimpleSession");
        service.setOption("allowedMethods", "counter");

        EngineConfiguration defaultConfig =
            (new DefaultEngineConfigurationFactory()).getServerEngineConfig();
        SimpleProvider config = new SimpleProvider(defaultConfig);
        config.deployService("sessionTest", service);

        AxisServer server = new AxisServer(config);

        // Set up the client side (using the WSDD above)
        Service svc = new Service(clientProvider);
        Call call = (Call)svc.createCall();
        svc.setMaintainSession(true);
        call.setTransport(new LocalTransport(server));

        // Try it - first invocation should return 1.
        Integer count = (Integer)call.invoke("sessionTest", "counter", null);
        assertNotNull("count was null!", count);
        assertEquals("count was " + count + " (expected 1)", count.intValue(),
                        1);

        // Next invocation should return 2, assuming the session-based
        // counter is working.
        count = (Integer)call.invoke("sessionTest", "counter", null);
        assertEquals("count was " + count + " (expected 2)", count.intValue(),
                        2);

        // Now start fresh and confirm a new session
        Service svc2 = new Service(clientProvider);
        Call call2 = (Call)svc2.createCall();
        svc2.setMaintainSession(true);
        call2.setTransport(new LocalTransport(server));

        // New session should cause us to return 1 again.
        count = (Integer)call2.invoke("sessionTest", "counter", null);
        assertNotNull("count was null on third call!", count);
        assertEquals("New session count was incorrect", 1,
                     count.intValue());

        // Wait around a few seconds to let the first session time out
        Thread.sleep(3000);

        // And now we should get a new session, therefore going back to 1
        count = (Integer)call.invoke("sessionTest", "counter", null);
        assertEquals("count after timeout was incorrect", 1, count.intValue());
    }

    /**
     * This is our service method for testing session data.  Simply
     * increments a session-scoped counter.
     */
    public Integer counter(MessageContext context) throws Exception
    {
        Session session = context.getSession();
        if (session == null)
            throw new Exception("No session in MessageContext!");
        Integer count = (Integer)session.get("counter");
        if (count == null) {
            count = new Integer(0);
        }
        count = new Integer(count.intValue() + 1);
        session.set("counter", count);
        return count;
    }

    public static void main(String args[]) throws Exception
    {
        TestSimpleSession test = new TestSimpleSession("test");
        test.testSessionAPI();
        test.testSessionService();
    }
}
