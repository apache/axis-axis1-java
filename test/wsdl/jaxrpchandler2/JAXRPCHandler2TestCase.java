package test.wsdl.jaxrpchandler2;

import java.rmi.RemoteException;

import javax.xml.namespace.QName;
import javax.xml.rpc.*;

import junit.framework.TestCase;

public class JAXRPCHandler2TestCase extends TestCase {

    protected String serviceEndpointUrl =
	"http://localhost:8080/axis/services/EchoService";
    protected String qnameService = "EchoService";
    protected String qnamePort = "EchoServicePort";
    
    protected Call call;
    
    protected String echoString = "my echo string";
    
    public JAXRPCHandler2TestCase(String arg0) {
	super(arg0);
    }

    public void testJAXRPCHandler2() {
	call.setOperationName(new QName("http://soapinterop.org/", "echo"));
	String returnString = null;
	try {
	    returnString = (String) call.invoke(new Object[] { echoString });
	} catch (RemoteException e) {
	    e.printStackTrace();
	    fail("Remote exception while calling invoke");
	}
	
	assertEquals(
		     "returnString does not match echoString",
		     echoString,
		     returnString);
    }
    
    protected void setUp() throws Exception {
	super.setUp();

	ServiceFactory serviceFactory = ServiceFactory.newInstance();
	Service service = serviceFactory.createService(new QName(qnameService));
	call = service.createCall(new QName(qnamePort));
	call.setTargetEndpointAddress(serviceEndpointUrl);
    }

}
