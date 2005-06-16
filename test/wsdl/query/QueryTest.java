/**
 * QueryTestImpl.java
 *
 * Service that echos a ColdFusion MX query object.
 *
 */

package test.wsdl.query;

public class QueryTest  {

    /* echo query */
    public test.wsdl.query.QueryBean echoQuery(test.wsdl.query.QueryBean argQuery) throws java.rmi.RemoteException {
        return argQuery;
    }
}
