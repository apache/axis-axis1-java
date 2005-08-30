package test.wsdl.jaxrpchandler2;

import junit.framework.TestCase;

import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceFactory;
import java.rmi.RemoteException;

public class JAXRPCHandler2TestCase extends TestCase {

    public JAXRPCHandler2TestCase(String arg0) {
        super(arg0);
    }

    public void testJAXRPCHandler2() throws Exception {
        String serviceEndpointUrl =
                "http://localhost:8080/axis/services/EchoService2";
        String qnameService = "EchoService2";
        String qnamePort = "EchoServicePort";
        Call call;
        String echoString = "my echo string";
        ServiceFactory serviceFactory = ServiceFactory.newInstance();
        Service service = serviceFactory.createService(new QName(qnameService));
        call = service.createCall(new QName(qnamePort));
        call.setTargetEndpointAddress(serviceEndpointUrl);
        call.setOperationName(new QName("http://soapinterop.org/", "echo"));
        String returnString = null;
        try {
            returnString = (String) call.invoke(new Object[]{echoString});
        } catch (RemoteException e) {
            e.printStackTrace();
            fail("Remote exception while calling invoke");
        }
        assertEquals("returnString does not match echoString",
                echoString,
                returnString);
    }

    public void testJAXRPCHandler3() throws Exception {
        String serviceEndpointUrl =
                "http://localhost:8080/axis/services/EchoService3";
        String qnameService = "EchoService3";
        String qnamePort = "EchoServicePort";
        Call call;
        String echoString = "Joe";
        ServiceFactory serviceFactory = ServiceFactory.newInstance();
        Service service = serviceFactory.createService(new QName(qnameService));
        call = service.createCall(new QName(qnamePort));
        call.setTargetEndpointAddress(serviceEndpointUrl);
        call.setOperationName(new QName("http://soapinterop.org/", "echo"));
        String returnString = null;
        try {
            returnString = (String) call.invoke(new Object[]{echoString});
        } catch (RemoteException e) {
            e.printStackTrace();
            fail("Remote exception while calling invoke");
        }
        assertEquals(
                "Sam",
                returnString);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

}
