package test.wsdl.jaxrpchandler2;

import org.w3c.dom.Node;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.Handler;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.SOAPMessage;

public class EchoServiceServerHandler3 implements Handler {
    private HandlerInfo info;

    public void init(HandlerInfo handlerInfo) {
        info = handlerInfo;
    }

    public void destroy() {
    }

    public QName[] getHeaders() {
        return info.getHeaders();
    }

    public boolean handleRequest(MessageContext mc) {
        try {
            SOAPMessage msg = ((SOAPMessageContext) mc).getMessage();
            Node child = msg.getSOAPPart().getEnvelope().getBody()
                    .getFirstChild()
                    .getFirstChild()
                    .getFirstChild();
            String name = child.getNodeValue();
            if (name != null && name.equals("Joe")) {
                child.setNodeValue("Sam");
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean handleResponse(MessageContext mc) {
        return true;
    }

    public boolean handleFault(MessageContext mc) {
        return true;
    }
}
    