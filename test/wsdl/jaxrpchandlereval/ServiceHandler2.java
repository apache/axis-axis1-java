package test.wsdl.jaxrpchandlereval;

import javax.xml.rpc.handler.*;
import javax.xml.rpc.soap.*;
import javax.xml.namespace.*;
import javax.xml.soap.*;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.soap.*;
import javax.xml.rpc.*;
import org.w3c.dom.NodeList;


public class ServiceHandler2 implements Handler {

    public ServiceHandler2() {
        System.out.println("ServiceHandler2:Constructor");
    }

    public boolean handleRequest(MessageContext context) {
        System.out.println("ServiceHandler2:handleRequest");

         try {
            SOAPMessageContext soapMsgCtx = (SOAPMessageContext)context;
            SOAPMessage soapMsg = soapMsgCtx.getMessage();
            SOAPPart sp = soapMsg.getSOAPPart();
            SOAPEnvelope se = sp.getEnvelope();
            SOAPBody sb = se.getBody();
            SOAPHeader sh = se.getHeader();

            Name xmlServicesHandler2HdrName =
                se.createName("ServiceHandler2-handleRequest", "", "");
            SOAPHeaderElement xmlServicesHandler2Hdr =
                sh.addHeaderElement(xmlServicesHandler2HdrName);
            xmlServicesHandler2Hdr.addTextNode(
                "Processed by ServiceHandler2Hdr2.handleRequest");

			/*--- getElementsByTagName() does not work.
			NodeList list = sb.getElementsByTagName("in0");
			if (list.getLength() == 0) {
				throw new JAXRPCException("No such element : 'in0'");
			}
			SOAPElement elem = (SOAPElement) list.item(0);
            String curBody = elem.getValue();
			---*/
			String curBody = sb.toString();
            if (curBody.indexOf("server-throw-soapfaultexception") >= 0) {
                String reason = "A FATAL EXCEPTION has occurred while processing ServiceHandler2.handleRequest";
                soapMsgCtx.setProperty("fault", reason);

                QName faultcode = new QName("Testimg Exception",
                                            "http://example.org/security/");
                throw new SOAPFaultException(faultcode, reason, null, null);
                //throw new JAXRPCException(reason);
            } else if (curBody.indexOf("server-return-false") >= 0) {
                soapMsgCtx.setProperty("fault",
                        "An error has occurred while processing ServiceHandler2.handleRequest - returning false");
                return false;
            }
         } catch (SOAPException ex) {
             throw new JAXRPCException(ex);
         }
        return true;
    }

    public boolean handleResponse(MessageContext context) {
        System.out.println("ServiceHandler2:handleResponse");

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

            Name xmlServicesHandler2HdrName =
                se.createName("ServiceHandler2-handleResponse", "", "");
            SOAPHeaderElement xmlServicesHandler2Hdr =
                sh.addHeaderElement(xmlServicesHandler2HdrName);
            xmlServicesHandler2Hdr.addTextNode(
                "Processed by ServiceHandler2Hdr2.handleResponse");


         } catch (Exception ex) {
             throw new JAXRPCException(ex);
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
        }
        return soapMsg;
    }

    public boolean handleFault(MessageContext context) {
        System.out.println("\nServiceHandler2:handleFault");
        try {
            SOAPMessageContext soapMsgCtx = (SOAPMessageContext)context;
            SOAPMessage soapMsg = soapMsgCtx.getMessage();

            soapMsg = prepareError(soapMsgCtx);


            // soapMsg.writeTo(System.out);

            SOAPPart sp = soapMsg.getSOAPPart();
            SOAPEnvelope se = sp.getEnvelope();
            SOAPBody sb = se.getBody();
            SOAPHeader sh = se.getHeader();

            Name xmlServicesHandler2HdrName =
                se.createName("ServiceHandler2-handleFault", "", "");
            SOAPHeaderElement xmlServicesHandler2Hdr =
                sh.addHeaderElement(xmlServicesHandler2HdrName);
            xmlServicesHandler2Hdr.addTextNode(
                "Processed by ServiceHandler2Hdr2.handleFault");


         } catch (Exception ex) {
             ex.printStackTrace();
         }
        return true;
    }

    public void init(HandlerInfo config) {
        System.out.println("ServiceHandler2:init");
    }

     public void destroy() {
        System.out.println("ServiceHandler2:destroy");
    }

    public QName[] getHeaders() {
        System.out.println("ServiceHandler2:getHeaders");
        return null;
    }

}




