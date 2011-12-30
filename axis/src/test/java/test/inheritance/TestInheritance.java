package test.inheritance;

import junit.framework.TestCase;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.providers.java.RPCProvider;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.local.LocalTransport;

public class TestInheritance extends TestCase {
    private AxisServer server;
    private LocalTransport transport;

    public TestInheritance(String s) {
        super(s);
    }

    protected void setUp() throws Exception {
        SimpleProvider config = new SimpleProvider();

        SOAPService service = new SOAPService(new RPCProvider());
        service.setOption("className", "test.inheritance.Child");
        service.setOption("allowedMethods", "*");
        config.deployService("inheritanceTest", service);

        server = new AxisServer(config);
        transport = new LocalTransport(server);
        transport.setRemoteService("inheritanceTest");
    }

    public void testInheritance() throws Exception {
        Call call = new Call(new Service());
        call.setTransport(transport);

        String ret = (String)call.invoke("inherited", null);
        assertEquals("Inherited method returned bad result",
                Parent.HELLO_MSG, ret);

        ret = (String)call.invoke("normal", null);
        assertEquals("Child method returned bad result",
                Child.HELLO_MSG, ret);

        ret = (String)call.invoke("overloaded", new Object [] { "test" });
        assertTrue("Overloaded (String) method returned bad result",
                ret.startsWith(Parent.OVERLOAD_MSG));
        ret = (String)call.invoke("overloaded",
                                  new Object [] { new Integer(5) });
        assertTrue("Overloaded (int) method returned bad result",
                ret.startsWith(Child.OVERLOAD_MSG));

    }
}
