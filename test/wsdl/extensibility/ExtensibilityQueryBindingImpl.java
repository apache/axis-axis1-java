/**
 * ExtensibilityQueryBindingImpl.java
 *
 */

package test.wsdl.extensibility;

import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.client.Call;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.DeserializationContextImpl;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.SerializationContextImpl;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.namespace.QName;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Vector;
import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

public class ExtensibilityQueryBindingImpl implements ExtensibilityQueryPortType {
    private final static String[] books = new String[] { "The Grid", "The Oxford Dictionary" }; 
    private final static String[] subjects = new String[] { "Computer Science", "English" }; 
    protected static Log log =
            LogFactory.getLog(ExtensibilityQueryBindingImpl.class.getName());

    public ExtensibilityType query(ExtensibilityType query) throws RemoteException {
        ExtensibilityType result = new ExtensibilityType();
        Object obj = null;
        try {
            obj = query.get_any()[0].getObjectValue(BookType.class);
        } catch (Exception e) {
            StringWriter writer = new StringWriter();
            PrintWriter out = new PrintWriter(writer);
            log.error("Error converting query: " + writer.toString());
            throw new RemoteException(e.toString());
        }
        log.error("Incoming MessageContext " + obj + " : " + query.get_any()[0].toString());
        if (obj instanceof BookType) {
            BookType bookQuery = (BookType)obj;
            String subject = bookQuery.getSubject();
            if (!"all".equals(subject)) {
                throw new RemoteException("ExtensibilityQueryBindingImpl: Book subject query should be all, instead was " + subject);
            }
            ResultListType resultList = new ResultListType();
            QueryResultType[] queryResult = new QueryResultType[books.length];
            for (int i = 0; i < books.length; i++) {
                queryResult[i] = new QueryResultType();
                queryResult[i].setName(subjects[i]);
                queryResult[i].setStatus(StatusType.MORE);
                queryResult[i].setValue(books[i]);
                queryResult[i].setTime(Calendar.getInstance());
                queryResult[i].setQueryType(new QName("urn:QueryType","BookQuery"));
            }
            resultList.setResult(queryResult);
            QName elementName = _QueryResultElement.getTypeDesc().getFields()[0].getXmlName();
            MessageElement me = new MessageElement(elementName.getNamespaceURI(), elementName.getLocalPart(), resultList);
            log.debug("Outgoing message: " + me.toString());
            result.set_any(new MessageElement [] { me });
        } else {
            throw new RemoteException("Failed to get FindBooksQueryExpressionElement. Got: " + obj);
        }
        return result;
    }
}
