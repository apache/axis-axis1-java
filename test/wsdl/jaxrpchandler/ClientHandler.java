package test.wsdl.jaxrpchandler;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.Handler;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.*;
import java.util.Iterator;

/**
 */
public class ClientHandler implements Handler {

	private final static String _actorURI = "myActorURI";
	/**
	 * Constructor for ClientHandler.
	 */
	public ClientHandler() {
		super();
	}

	/**
	 * @see javax.xml.rpc.handler.Handler#handleRequest(MessageContext)
	 */
	public boolean handleRequest(MessageContext context) {
		System.out.println("Hey - in Handle request");
		try {
			SOAPMessageContext smc = (SOAPMessageContext) context;
			SOAPMessage msg = smc.getMessage();
			SOAPPart sp = msg.getSOAPPart();
			SOAPEnvelope se = sp.getEnvelope();
			SOAPHeader sh = se.getHeader();
			Name name =
			se.createName(
						 "HeaderTest",
						 "AXIS",
						 "http://xml.apache.org/axis");
			SOAPHeaderElement she = sh.addHeaderElement(name);
			she.setActor(_actorURI);
			she.addAttribute(se.createName("counter", "", ""), "1");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * @see javax.xml.rpc.handler.Handler#handleResponse(MessageContext)
	 */
	public boolean handleResponse(MessageContext context) {
		System.out.println("Hey - in Handle response");
		try {
			String counter = null;
			SOAPMessageContext smc = (SOAPMessageContext) context;
			SOAPMessage msg = smc.getMessage();
			SOAPPart sp = msg.getSOAPPart();
			SOAPEnvelope se = sp.getEnvelope();
			SOAPHeader sh = se.getHeader();
			Name name =
			se.createName(
						 "HeaderTest",
						 "AXIS",
						 "http://xml.apache.org/axis");
			Iterator iter = sh.extractHeaderElements(_actorURI);
			while (iter.hasNext()) {
				SOAPHeaderElement she = (SOAPHeaderElement) iter.next();
				counter =
				she.getAttributeValue(se.createName("counter", "", ""));
				System.out.println(
								  "The counter in the element sent back is " + counter);
			}

			if (counter.equals("3"))
				JAXRPCHandlerTestCase.completeRoundtrip();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * @see javax.xml.rpc.handler.Handler#handleFault(MessageContext)
	 */
	public boolean handleFault(MessageContext context) {
		return false;
	}

	/**
	 * @see javax.xml.rpc.handler.Handler#init(HandlerInfo)
	 */
	public void init(HandlerInfo config) {
	}

	/**
	 * @see javax.xml.rpc.handler.Handler#destroy()
	 */
	public void destroy() {
	}

	/**
	 * @see javax.xml.rpc.handler.Handler#getHeaders()
	 */
	public QName[] getHeaders() {
		return null;
	}

}
