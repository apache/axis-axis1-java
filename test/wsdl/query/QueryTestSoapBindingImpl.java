/**
 * QueryTestBindingImpl.java
 *
 * Implementation for Query test.
 * 
 */

package test.wsdl.query;

public class QueryTestSoapBindingImpl implements test.wsdl.query.QueryTest {
    public test.wsdl.query.QueryBean echoQuery(test.wsdl.query.QueryBean argQuery) throws java.rmi.RemoteException {
        return argQuery;
    }

}
