/**
 * IncludeTestTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4.1-SNAPSHOT Mar 09, 2012 (11:21:16 GMT) WSDL2Java emitter.
 */

package test.wsdl.include;

import test.HttpTestUtil;

public class IncludeTestTestCase extends junit.framework.TestCase {
    public IncludeTestTestCase(java.lang.String name) {
        super(name);
    }

    public void testIncludeTestWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = HttpTestUtil.getTestEndpoint(new test.wsdl.include.IncludeTestLocator().getIncludeTestAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new test.wsdl.include.IncludeTestLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1IncludeTestAddEntry() throws Exception {
        test.wsdl.include.AddressBookSOAPBindingStub binding;
        try {
            IncludeTestLocator loc = new IncludeTestLocator();
            binding = (AddressBookSOAPBindingStub)loc.getIncludeTest(HttpTestUtil.getTestEndpoint(loc.getIncludeTestAddress()));
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        binding.addEntry(new java.lang.String(), new test.wsdl.include.Address());
        // TBD - validate results
    }

    public void test2IncludeTestGetAddressFromName() throws Exception {
        test.wsdl.include.AddressBookSOAPBindingStub binding;
        try {
            IncludeTestLocator loc = new IncludeTestLocator();
            binding = (AddressBookSOAPBindingStub)loc.getIncludeTest(HttpTestUtil.getTestEndpoint(loc.getIncludeTestAddress()));
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        test.wsdl.include.Address value = null;
        value = binding.getAddressFromName(new java.lang.String());
        // TBD - validate results
    }

}
