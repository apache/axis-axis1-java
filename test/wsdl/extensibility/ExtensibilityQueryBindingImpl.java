/**
 * ExtensibilityQueryBindingImpl.java
 *
 */

package test.wsdl.extensibility;

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.Text;
import org.apache.commons.logging.Log;

import javax.xml.namespace.QName;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.Calendar;

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
    
    public ExtensibilityType mixedQuery(ExtensibilityType query) 
        throws RemoteException {
        MessageElement [] elements = query.get_any();
        if (elements == null) {
            throw new RemoteException("No any");
        }
        if (elements.length != 3) {
            throw new RemoteException("Expected: 3 got: " + elements.length +
                                      " element");
        }
        
        String expected = "123  456";
        String received = elements[0].toString();
        
        if (!expected.equals(received)) {
            throw new RemoteException("Expected: " + expected + 
                                      " received: " + received);
        }
        
        Object obj = null;
        try {
            obj = elements[1].getObjectValue(BookType.class);
        } catch (Exception e) {
            throw new RemoteException("Failed to deserialize", e);
        }
        BookType bookQuery = (BookType)obj;
        String subject = bookQuery.getSubject();
        if (!"all".equals(subject)) {
            throw new RemoteException("ExtensibilityQueryBindingImpl: Book subject query should be all, instead was " + subject);
        }
        
        expected = "789";
        received = elements[2].toString();
        
        if (!expected.equals(received)) {
            throw new RemoteException("Expected: " + expected + 
                                      " received: " + received);
        }
        
        ExtensibilityType reply = new ExtensibilityType(); 
        
        MessageElement [] replyElements = new MessageElement[2];
        
        BookType book = new BookType();
        book.setSubject("gotAll");
        QName elementName = _FindBooksQueryExpressionElement.getTypeDesc().getFields()[0].getXmlName();
        replyElements[0] = new MessageElement(elementName.getNamespaceURI(), elementName.getLocalPart(), book);
        replyElements[1] = new MessageElement(new Text("ABCD"));
        
        reply.set_any(replyElements);
        
        return reply;
    }
}
