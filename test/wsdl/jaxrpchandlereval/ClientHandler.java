package test.wsdl.jaxrpchandlereval;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.Handler;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import java.util.Iterator;
import javax.xml.soap.*;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.rpc.soap.*;
import javax.xml.rpc.*;


/**
 */
public class ClientHandler implements Handler {

private final static String _actorURI = "myActorURI";
/**
 * Constructor for ClientHandler.
 */
public ClientHandler() {
    System.out.println("ClientHandler:Constructor");
}

/**
 * @see javax.xml.rpc.handler.Handler#handleRequest(MessageContext)
 */
public boolean handleRequest(MessageContext context) {
    System.out.println("ClientHandler:handleRequest");
	HandlerTracker.addClientHandler("clienthandler1.handleRequest");
    if (context instanceof SOAPMessageContext) {
        try {
            SOAPMessageContext soapMsgCtx = (SOAPMessageContext)context;
            SOAPMessage soapMsg = soapMsgCtx.getMessage();
            SOAPPart sp = soapMsg.getSOAPPart();
            SOAPEnvelope se = sp.getEnvelope();
            SOAPBody sb = se.getBody();
            SOAPHeader sh = se.getHeader();

            Name name = se.createName("ClientHandler-handleRequest", "", "");
            SOAPHeaderElement hdr = sh.addHeaderElement(name);
            hdr.addTextNode("Processed");

            String instruction = sb.toString();
            if (instruction.indexOf("client-return-false") >= 0) {
                soapMsgCtx.setProperty("fault", "Returning false from ClientHandler.handleRequest");
                return false;
            }
        } catch (SOAPException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    return true;
}

public SOAPMessage prepareError(SOAPMessageContext soapMsgCtx) throws Exception{
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMsg = messageFactory.createMessage();
        soapMsgCtx.setMessage(soapMsg);

        String fault = (String)soapMsgCtx.getProperty("fault");
        if (fault != null) {
            SOAPFault soapFault =
                soapMsg.getSOAPPart().getEnvelope().getBody().addFault();
            soapFault.setFaultString(fault);
            soapFault.setFaultCode("client.Exception");
        }
        return soapMsg;
}

	private void trackServerHandler(SOAPHeader sh) {
		Iterator i = sh.getChildElements();
		while (i.hasNext()) {
			Node n = (Node) i.next();
			if (n instanceof Text) {
				continue;
			}
			SOAPElement e = (SOAPElement) n;
			String local = e.getLocalName();
			if (local.equals("ServiceHandler2-handleFault")) {
				HandlerTracker.addServerHandler("serverhandler2.handleFault");
			} else if (local.equals("ServiceHandler1-handleFault")) {
				HandlerTracker.addServerHandler("serverhandler1.handleFault");
			} else if (local.equals("ServiceHandler2-handleResponse")) {
				HandlerTracker.addServerHandler("serverhandler2.handleResponse");
			} else if (local.equals("ServiceHandler1-handleResponse")) {
				HandlerTracker.addServerHandler("serverhandler1.handleResponse");
			}
		}
	}


/**
 * @see javax.xml.rpc.handler.Handler#handleResponse(MessageContext)
 */
public boolean handleResponse(MessageContext context) {
    System.out.println("ClientHandler:handleResponse");
	HandlerTracker.addClientHandler("clienthandler1.handleResponse");

    if (context instanceof SOAPMessageContext) {
        try {
            SOAPMessageContext soapMsgCtx = (SOAPMessageContext)context;
            SOAPMessage soapMsg = soapMsgCtx.getMessage();
            if (soapMsg == null) {
                soapMsg = prepareError(soapMsgCtx);
            }
            SOAPPart sp = soapMsg.getSOAPPart();
            SOAPEnvelope se = sp.getEnvelope();
            SOAPBody sb = se.getBody();
            SOAPHeader sh = se.getHeader();

			trackServerHandler(sh);

            Name name = se.createName("ClientHandler-handleResponse", "", "");
            SOAPHeaderElement hdr = sh.addHeaderElement(name);
            hdr.addTextNode("Processed");
        } catch (SOAPException ex) {
            ex.printStackTrace();
            return false;
        } catch (Exception ex) {
            throw new JAXRPCException (ex);
        }
    }
    return true;
}

/**
 * @see javax.xml.rpc.handler.Handler#handleFault(MessageContext)
 */
public boolean handleFault(MessageContext context) {
    System.out.println("ClientHandler:handleFault");
	HandlerTracker.addClientHandler("clienthandler1.handleFault");
    if (context instanceof SOAPMessageContext) {
        try {
            SOAPMessageContext soapMsgCtx = (SOAPMessageContext)context;
            SOAPMessage soapMsg = soapMsgCtx.getMessage();
            SOAPPart sp = soapMsg.getSOAPPart();
            SOAPEnvelope se = sp.getEnvelope();
            SOAPBody sb = se.getBody();
            SOAPHeader sh = se.getHeader();

            Name name = se.createName("ClientHandler-handleFault", "", "");
            SOAPHeaderElement hdr = sh.addHeaderElement(name);
            hdr.addTextNode("Processed");
        } catch (SOAPException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    return false;
}

/**
 * @see javax.xml.rpc.handler.Handler#init(HandlerInfo)
 */
public void init(HandlerInfo config) {
    System.out.println("ClientHandler.init");

}

/**
 * @see javax.xml.rpc.handler.Handler#destroy()
 */
public void destroy() {
    System.out.println("ClientHandler.destroy");
}

/**
 * @see javax.xml.rpc.handler.Handler#getHeaders()
 */
public QName[] getHeaders() {
    System.out.println("ClientHandler.getheaders");

    return null;
}

}

