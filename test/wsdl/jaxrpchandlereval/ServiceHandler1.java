package test.wsdl.jaxrpchandlereval;

import javax.xml.rpc.handler.*;
import javax.xml.soap.*;
import javax.xml.namespace.*;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.rpc.JAXRPCException;


public class ServiceHandler1 implements javax.xml.rpc.handler.Handler{

    public ServiceHandler1() {
        System.out.println("ServiceHandler1:Constructor");
    }

    public boolean handleRequest(MessageContext context) {
        System.out.println("ServiceHandler1:handleRequest");

         try {
            SOAPMessageContext soapMsgCtx = (SOAPMessageContext)context;
            SOAPMessage soapMsg = soapMsgCtx.getMessage();
            SOAPPart sp = soapMsg.getSOAPPart();
            SOAPEnvelope se = sp.getEnvelope();
            SOAPBody sb = se.getBody();
            SOAPHeader sh = se.getHeader();

            Name xmlServicesHandler1HdrName =
                se.createName("ServiceHandler1-handleRequest", "", "");
                SOAPHeaderElement xmlServicesHandler1Hdr =
                    sh.addHeaderElement(xmlServicesHandler1HdrName);
                xmlServicesHandler1Hdr.addTextNode(
                    "Processed by ServiceHandler1Hdr1.handleRequest");
         } catch (Exception ex) {
             throw new JAXRPCException(ex);
         }
        return true;
    }

    public boolean handleResponse(MessageContext context) {
        System.out.println("ServiceHandler1:handleResponse");
          try {
            SOAPMessageContext soapMsgCtx = (SOAPMessageContext)context;
            SOAPMessage soapMsg = soapMsgCtx.getMessage();
            SOAPPart sp = soapMsg.getSOAPPart();
            SOAPEnvelope se = sp.getEnvelope();
            SOAPBody sb = se.getBody();
            SOAPHeader sh = se.getHeader();

            Name xmlServicesHandler1HdrName =
                se.createName("ServiceHandler1-handleResponse", "", "");
                SOAPHeaderElement xmlServicesHandler1Hdr =
                    sh.addHeaderElement(xmlServicesHandler1HdrName);
                xmlServicesHandler1Hdr.addTextNode(
                    "Processed by ServiceHandler1Hdr1.handleResponse");

           sp.addMimeHeader("MY_MIME_HEADER", "ADDING A NEW HEADER IN SOAPPART");
         } catch (Exception ex) {
             throw new JAXRPCException(ex);
         }
        return true;
    }

    public boolean handleFault(MessageContext context) {
        System.out.println("\nServiceHandler1:handleFault");
        try {
            SOAPMessageContext soapMsgCtx = (SOAPMessageContext)context;
            SOAPMessage soapMsg = soapMsgCtx.getMessage();


            SOAPPart sp = soapMsg.getSOAPPart();
            SOAPEnvelope se = sp.getEnvelope();
            SOAPBody sb = se.getBody();
            SOAPHeader sh = se.getHeader();

            Name xmlServicesHandler1HdrName =
                se.createName("ServiceHandler1-handleFault", "", "");
            SOAPHeaderElement xmlServicesHandler1Hdr =
                sh.addHeaderElement(xmlServicesHandler1HdrName);
            xmlServicesHandler1Hdr.addTextNode(
                "Processed by ServiceHandler1Hdr1.handleFault");

                    soapMsgCtx.setMessage(soapMsg);
         } catch (Exception ex) {
             ex.printStackTrace();
         }
        return true;
    }

    public void init(HandlerInfo config) {
        System.out.println("ServiceHandler1:init");
    }

     public void destroy() {
        System.out.println("ServiceHandler1:destroy");
    }

    public QName[] getHeaders() {
        System.out.println("ServiceHandler1:getHeaders");
        return null;
    }

}

