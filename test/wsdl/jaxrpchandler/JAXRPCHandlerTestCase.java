package test.wsdl.jaxrpchandler;

import junit.framework.TestCase;
import org.apache.axis.client.AdminClient;
import org.apache.axis.utils.Admin;
import org.apache.axis.utils.Options;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.HandlerRegistry;
import java.net.URL;

/**
 *
 */
public class JAXRPCHandlerTestCase extends TestCase {

	private static boolean _roundtrip = false;
	public static void completeRoundtrip() {
		_roundtrip = true;
	}

	/**
	 * Default constructor for use as service
	 */
	public JAXRPCHandlerTestCase() {
		super("JAXRPCHandlerTest");
	}

	public JAXRPCHandlerTestCase(String name) {
		super(name);
	}

	public void testStockQuote() throws Exception {
	    String[] args = {};
	    goStockQuote(args);
	}

	public void goStockQuote(String[] args) throws Exception {
		Options opts = new Options( args );
		args = opts.getRemainingArgs();

		URL url = new URL(opts.getURL());
		String user = opts.getUser();
		String passwd = opts.getPassword();
		System.out.println("URL is " + url);

		_roundtrip = false;
		doTestDeploy();
		float val = getQuote(url,false);
		assertEquals("Stock price is not the expected 55.25 +/- 0.01", val, 55.25, 0.01);
		assertTrue("Expected setting for config-based handlers should be true", _roundtrip == true);
		doTestClientUndeploy();
		_roundtrip = false;
		val = getQuote(url,true);
		assertEquals("Stock price is not the expected 55.25 +/- 0.01", val, 55.25, 0.01);
		assertTrue("Expected setting for programmatic-based handlers should be true", _roundtrip == true);
		doTestServerUndeploy();

	}

	public float getQuote (URL url, boolean runJAXRPCHandler) throws Exception {
		StockQuoteService  service = new StockQuoteServiceLocator();
		if (runJAXRPCHandler) {
			HandlerRegistry hr = service.getHandlerRegistry();
			java.util.List lhi = new java.util.Vector();
			test.wsdl.jaxrpchandler.ClientHandler mh = new test.wsdl.jaxrpchandler.ClientHandler(); 
			Class myhandler = mh.getClass();
			HandlerInfo hi = new HandlerInfo(myhandler,null,null);
			lhi.add(hi);
			hr.setHandlerChain(new QName("","jaxrpchandler"),lhi);
		}

		float res;

		StockQuote sq = service.getjaxrpchandler(url);
		res = sq.getQuote("XXX");

		return res;
	}

	public static void main(String[] args) throws Exception {
		JAXRPCHandlerTestCase test = new JAXRPCHandlerTestCase("test");
		test.goStockQuote(args);
	}

	public void doTestClientUndeploy() throws Exception {
		String[] args1 = { "client", "test/wsdl/jaxrpchandler/undeploy.wsdd"};
		Admin.main(args1);
	}

	public void doTestServerUndeploy() throws Exception {
		String[] args = { "test/wsdl/jaxrpchandler/undeploy.wsdd"};
		AdminClient.main(args);
	}

	public void doTestDeploy() throws Exception {
		String[] args = { "test/wsdl/jaxrpchandler/server_deploy.wsdd"};
		AdminClient.main(args);
		String[] args1 = { "client", "test/wsdl/jaxrpchandler/client_deploy.wsdd"};
		Admin.main(args1);
	}

}
