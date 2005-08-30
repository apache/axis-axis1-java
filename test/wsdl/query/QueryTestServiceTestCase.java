/**
 * QueryTest_ServiceTestCase.java
 *
 * Test the QueryBean object.
 *
 */

package test.wsdl.query;

public class QueryTestServiceTestCase extends junit.framework.TestCase {
    public QueryTestServiceTestCase(java.lang.String name) {
        super(name);
    }

    public void testQueryTestWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new test.wsdl.query.QueryTestServiceLocator().getQueryTestAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new test.wsdl.query.QueryTestServiceLocator().getServiceName());
        assertTrue(service != null);
    }


    public void test2QueryTestEchoQuery() throws Exception {
        test.wsdl.query.QueryTestSoapBindingStub binding;
        try {
            binding = (test.wsdl.query.QueryTestSoapBindingStub)
                          new test.wsdl.query.QueryTestServiceLocator().getQueryTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Set up input data
        test.wsdl.query.QueryBean inQuery = new QueryBean();
        String[] columns = new String[] {"first", "last", "number"};
        Object[][] data = new Object[][] { {new String("Joe"), new String("Blow"), new Integer(3)},
                                           {new String("John"), new String("Doe"), new Integer(2)} };
        inQuery.setColumnList(columns);
        inQuery.setData(data);

        // Do the operation
        QueryBean outQuery = binding.echoQuery(inQuery);

        // Check the results
        assertNotNull("return value from echoQuery is null", outQuery);
        String[] outCols = outQuery.getColumnList();
        assertNotNull("column list in the returned Query is null, should have string array in it", outCols);
        assertEquals("column value #1 doesn't match", columns[0], outCols[0]);
        assertEquals("column value #2 doesn't match", columns[1], outCols[1]);
        assertEquals("column value #3 doesn't match", columns[2], outCols[2]);
        Object[][] outData = outQuery.getData();
        assertNotNull("data array in the returned Query is null, should have Query data in it", outData);
        assertEquals("data value 0,0 doesn't match", data[0][0], outData[0][0]);
        assertEquals("data value 0,1 doesn't match", data[0][1], outData[0][1]);
        assertEquals("data value 0,2 doesn't match", data[0][2], outData[0][2]);
        assertEquals("data value 1,0 doesn't match", data[1][0], outData[1][0]);
        assertEquals("data value 1,1 doesn't match", data[1][1], outData[1][1]);
        assertEquals("data value 1,2 doesn't match", data[1][2], outData[1][2]);
    }


}
