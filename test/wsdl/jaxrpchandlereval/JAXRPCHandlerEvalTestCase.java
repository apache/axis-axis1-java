package test.wsdl.jaxrpchandlereval;

import junit.framework.TestCase;

import org.apache.axis.client.AdminClient;
import org.apache.axis.utils.Admin;

import java.rmi.RemoteException;
import javax.xml.rpc.JAXRPCException;

public class JAXRPCHandlerEvalTestCase extends TestCase {

	public JAXRPCHandlerEvalTestCase() {
		super("JAXRPCHandlerEvalTest");
	}

	public JAXRPCHandlerEvalTestCase(String name) {
		super(name);
	}

	public void testHappyPath() throws Exception {
		doClientDeploy();
		HandlerTracker.init();
		try {
			updateInfo("Using the happy path");
			HandlerTracker.assertClientHandlerOrder(new String[] { 
				"clienthandler1.handleRequest", "clienthandler2.handleRequest",
				"clienthandler2.handleResponse", "clienthandler1.handleResponse" });
			HandlerTracker.assertServerHandlerOrder(new String[] { 
				"serverhandler2.handleResponse", "serverhandler1.handleResponse" });
		} finally {
			doClientUndeploy();
		}
	}

	public void testServerReturnFalse() throws Exception {
		doClientDeploy();
		HandlerTracker.init();
		try {
			updateInfo("server-return-false");
		} catch (RemoteException e) {
			HandlerTracker.assertClientHandlerOrder(new String[] { 
				"clienthandler1.handleRequest", "clienthandler2.handleRequest",
				"clienthandler2.handleResponse", "clienthandler1.handleResponse" });
			HandlerTracker.assertServerHandlerOrder(new String[] { 
				"serverhandler2.handleResponse", "serverhandler1.handleResponse" });
		} finally {
			doClientUndeploy();
		}
	}

	public void testServerThrowSoapFaultException() throws Exception {
		doClientDeploy();
		HandlerTracker.init();
		try {
			updateInfo("server-throw-soapfaultexception");
		} catch (RemoteException e) {
			HandlerTracker.assertClientHandlerOrder(new String[] { 
				"clienthandler1.handleRequest", "clienthandler2.handleRequest",
				"clienthandler2.handleResponse", "clienthandler1.handleResponse" });
			HandlerTracker.assertServerHandlerOrder(new String[] { 
				"serverhandler2.handleFault", "serverhandler1.handleFault" });
		} finally {
			doClientUndeploy();
		}
	}

	public void testClientReturnFalse() throws Exception {
		doClientDeploy();
		HandlerTracker.init();
		try {
			updateInfo("client-return-false");
		} catch (RemoteException e) {
			HandlerTracker.assertClientHandlerOrder(new String[] { 
				"clienthandler1.handleRequest", "clienthandler1.handleResponse" });
			HandlerTracker.assertServerHandlerOrder(new String[] {});
		} finally {
			doClientUndeploy();
		}
	}

	public void testClientThrowJaxRpcException() throws Exception {
		doClientDeploy();
		HandlerTracker.init();
		try {
			updateInfo("client-throw-jaxrpcexception");
		} catch (RemoteException e) {
			HandlerTracker.assertClientHandlerOrder(new String[] { 
				"clienthandler1.handleRequest", "clienthandler2.handleRequest" });
			HandlerTracker.assertServerHandlerOrder(new String[] {});
		} finally {
			doClientUndeploy();
		}
	}

	public String updateInfo(String payload) throws Exception {
	    UserAccountServiceLocator locator = new UserAccountServiceLocator();
	    UserAccount port = locator.getUserAccount();
	    String retval = null;
	
	    try {
	        retval = port.updateInfo(payload);
	    } catch (Exception ex) {
			throw ex;
	    }
		return retval;
	}

	public void doClientDeploy() throws Exception {
		String[] args1 = { "client", "test/wsdl/jaxrpchandlereval/client-deploy.wsdd" };
		Admin.main(args1);
	}

	public void doClientUndeploy() throws Exception {
		String[] args1 = { "client", "test/wsdl/jaxrpchandlereval/client-undeploy.wsdd" };
		Admin.main(args1);
	}
}



