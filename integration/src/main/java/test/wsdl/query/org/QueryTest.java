/**
 * QueryTestImpl.java
 *
 * Service that echos a ColdFusion MX query object.
 *
 */

package test.wsdl.query.org;

public class QueryTest  {

    /* echo query */
    public QueryBean echoQuery(QueryBean argQuery) throws java.rmi.RemoteException {
        return argQuery;
    }
}
