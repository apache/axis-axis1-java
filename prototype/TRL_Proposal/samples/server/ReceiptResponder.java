package samples.server;

import org.apache.axis.Handler;
import org.apache.axis.MessageContext;
import org.apache.axis.util.xml.DOMConverter;
import org.apache.axis.util.xml.DOMHandler;
import org.apache.axis.util.Logger;

import org.apache.axis.message.SOAPDocument;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPBody;
import org.apache.axis.message.SOAPBodyEntry;
import org.apache.axis.message.impl.SOAPDocumentImpl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.apache.xerces.dom.DocumentImpl;

public class ReceiptResponder implements Handler {

    final private String RECEIPT="-received";

    public void init() {};

    public void cleanup() {};

    public void invoke(MessageContext msgCntxt) {
        try {
            SOAPDocument req = msgCntxt.getMessage();
            Logger.normal(DOMConverter.toString(req.getDocument()));
            
            SOAPDocument res = createResponseMessage(req);
            msgCntxt.setMessage(res);
        } catch(Exception e) {
            e.printStackTrace();
        }
    };
    
    private SOAPDocument createResponseMessage(SOAPDocument req) {
        try {
            Element requestRoot = req.getEnvelope().getBody().getBodyEntry(0).getDOMEntity();
            String reqRootName = requestRoot.getTagName();
            String reqRootNS = requestRoot.getAttribute("xmlns");
            
            Document responseDoc = new DocumentImpl();
            Element responseRoot = responseDoc.createElement(reqRootName+RECEIPT);
            responseRoot.setAttribute("xmlns", reqRootNS);
            NodeList list = requestRoot.getChildNodes();
            int length = list.getLength();
            for (int i=0; i<length; i++) {
                Node reqNode = list.item(i);
                Node resNode = responseDoc.importNode(reqNode, true);
                responseRoot.appendChild(resNode);
            }
            
            responseDoc.appendChild(responseRoot);
            Logger.normal(DOMConverter.toString(responseDoc));

            SOAPDocument doc = new SOAPDocumentImpl();
            SOAPEnvelope env = doc.getEnvelope() ;
            SOAPBody body = env.getBody() ;
            SOAPBodyEntry bodyEntry;
            SOAPBodyEntry entry = 
                doc.createBodyEntry(responseDoc.getDocumentElement());
            body.addBodyEntry(entry);
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
};
