/**
 * ExtensibilityQueryBindingImpl.java
 *
 */

package test.wsdl.extensibility;

import java.util.Calendar;
import javax.xml.rpc.namespace.QName;

public class ExtensibilityQueryBindingImpl implements ExtensibilityQueryBinding {

    public ExtensibilityType query(ExtensibilityType query) throws java.rmi.RemoteException {
        ExtensibilityType result = new ExtensibilityType();
        Object obj = query.getAny();
        if (obj instanceof BookQuery) {
            System.out.println("ExtensibilityQueryBindingImpl: Found book");
            BookQuery bookQuery = (BookQuery) obj;
            String subject = bookQuery.getSubject();
            QueryResultType queryResultType = new QueryResultType();
            QueryResult queryResult = new QueryResult(); 
            queryResult.setName(subject);
            queryResult.setValue("The Grid");
            queryResult.setTime(Calendar.getInstance());
            queryResult.setQueryType(new QName("urn:QueryType","BookQuery"));
            queryResultType.setQueryResult(queryResult);
            result.setAny(queryResultType);
        }
        return result;
    }
}
