/**
 * ExtensibilityQueryBindingImpl.java
 *
 */

package test.wsdl.extensibility;

import java.util.Calendar;
import javax.xml.rpc.namespace.QName;

public class ExtensibilityQueryBindingImpl implements ExtensibilityQueryBinding {
    private final static String[] books = new String[] { "The Grid", "The Oxford Dictionary" }; 
    private final static String[] subjects = new String[] { "Computer Science", "English" }; 

    public ExtensibilityType query(ExtensibilityType query) throws java.rmi.RemoteException {
        ExtensibilityType result = new ExtensibilityType();
        Object obj = query.getAny();
        if (obj instanceof BookType) {
            BookType bookQuery = (BookType) obj;
            String subject = bookQuery.getSubject();
            System.out.println("ExtensibilityQueryBindingImpl: Found book subject query " + subject);
  
            QueryResultElement resultElement = new QueryResultElement();
            ResultListType resultList = new ResultListType();
            resultElement.setResultList(resultList);
            QueryResultType[] queryResult = new QueryResultType[books.length];
            for (int i = 0; i < books.length; i++) {
                queryResult[i] = new QueryResultType();
                queryResult[i].setName(subjects[i]);
                queryResult[i].setValue(books[i]);
                queryResult[i].setTime(Calendar.getInstance());
                queryResult[i].setQueryType(new QName("urn:QueryType","BookQuery"));
            }
            resultList.setResult(queryResult);
            result.setAny(resultElement);
        }
        return result;
    }
}
