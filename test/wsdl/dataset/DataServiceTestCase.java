/**
 * DataServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.dataset;

public class DataServiceTestCase extends junit.framework.TestCase {
    public DataServiceTestCase(java.lang.String name) {
        super(name);
    }
    public void test1DataServiceSoapGetTitleAuthors() throws Exception {
        test.wsdl.dataset.DataServiceSoapStub binding;
        try {
            binding = (test.wsdl.dataset.DataServiceSoapStub)
                    new test.wsdl.dataset.DataServiceLocator().getDataServiceSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        binding.setTimeout(60000);
        // Test operation
        test.wsdl.dataset.GetTitleAuthorsResult value = null;
        value = binding.getTitleAuthors();
        assertTrue(value != null);
        // TBD - validate results
    }
}
