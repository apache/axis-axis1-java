package test.wsdl.jaxrpchandler2;

import java.io.*;
import java.io.InputStream;
import java.util.*;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.*;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.*;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.stream.*;

public class EchoServiceServerHandler implements Handler {

    public boolean handleRequest(MessageContext messageContext) {

	try {
	    SOAPMessageContext soapMsgCtx = (SOAPMessageContext) messageContext;
	    
	    SOAPMessage soapMsg = soapMsgCtx.getMessage();
	    SOAPPart soapPart = soapMsg.getSOAPPart();
	    SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
	    soapEnvelope.getBody().detachNode();
	    SOAPBody soapBody = soapEnvelope.addBody();
	    SOAPBodyElement echoElement = soapBody.addBodyElement(soapEnvelope.createName("echo", "ns1", "http://soapinterop.org/"));
	    SOAPElement argElement = echoElement.addChildElement("arg0");
	    argElement = argElement.addAttribute(soapEnvelope.createName("type", "xsi", "http://www.w3.org/2001/XMLSchema-instance"), "xsd:string");
	    argElement.addTextNode("my echo string");
	    soapMsg.saveChanges();
	    
	} catch (Exception e) {
	    e.printStackTrace();
	}
	
	return true;
    }
    
    public boolean handleResponse(MessageContext messageContext) {
	return true;
    }

    
    public boolean handleFault(MessageContext messageContext) {
	return true;
    }

    public void init(HandlerInfo arg0) {
    }

    public void destroy() {
    }

    public QName[] getHeaders() {
	return null;
    }
    
}
