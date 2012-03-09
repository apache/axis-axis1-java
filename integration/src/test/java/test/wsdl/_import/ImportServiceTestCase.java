/**
 * ImportServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4.1-SNAPSHOT Mar 09, 2012 (11:21:16 GMT) WSDL2Java emitter.
 */

package test.wsdl._import;

import test.HttpTestUtil;
import test.wsdl._import.binding1.ImportBinding1Stub;
import test.wsdl._import.binding2.ImportBinding2Stub;

public class ImportServiceTestCase extends junit.framework.TestCase {
    public ImportServiceTestCase(java.lang.String name) {
        super(name);
    }

    public void testImportTest1WSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = HttpTestUtil.getTestEndpoint(new test.wsdl._import.ImportServiceLocator().getImportTest1Address() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new test.wsdl._import.ImportServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1ImportTest1AddEntry() throws Exception {
        test.wsdl._import.binding1.ImportBinding1Stub binding;
        try {
            ImportServiceLocator loc = new ImportServiceLocator();
            binding = (ImportBinding1Stub)loc.getImportTest1(HttpTestUtil.getTestEndpoint(loc.getImportTest1Address()));
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
        binding.addEntry(new java.lang.String(), new test._import.types.Address());
        // TBD - validate results
    }

    public void test2ImportTest1GetAddressFromName() throws Exception {
        test.wsdl._import.binding1.ImportBinding1Stub binding;
        try {
            ImportServiceLocator loc = new ImportServiceLocator();
            binding = (ImportBinding1Stub)loc.getImportTest1(HttpTestUtil.getTestEndpoint(loc.getImportTest1Address()));
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
        try {
            test._import.types.Address value = null;
            value = binding.getAddressFromName(new java.lang.String());
        }
        catch (test._import.messages.Fault e1) {
            throw new junit.framework.AssertionFailedError("Fault Exception caught: " + e1);
        }
            // TBD - validate results
    }

    public void testImportTest2WSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = HttpTestUtil.getTestEndpoint(new test.wsdl._import.ImportServiceLocator().getImportTest2Address() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new test.wsdl._import.ImportServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test3ImportTest2AddEntry() throws Exception {
        test.wsdl._import.binding2.ImportBinding2Stub binding;
        try {
            ImportServiceLocator loc = new ImportServiceLocator();
            binding = (ImportBinding2Stub)loc.getImportTest2(HttpTestUtil.getTestEndpoint(loc.getImportTest2Address()));
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
        binding.addEntry(new java.lang.String(), new test._import.types.Address());
        // TBD - validate results
    }

    public void test4ImportTest2GetAddressFromName() throws Exception {
        test.wsdl._import.binding2.ImportBinding2Stub binding;
        try {
            ImportServiceLocator loc = new ImportServiceLocator();
            binding = (ImportBinding2Stub)loc.getImportTest2(HttpTestUtil.getTestEndpoint(loc.getImportTest2Address()));
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
        try {
            test._import.types.Address value = null;
            value = binding.getAddressFromName(new java.lang.String());
        }
        catch (test._import.messages.Fault e1) {
            throw new junit.framework.AssertionFailedError("Fault Exception caught: " + e1);
        }
            // TBD - validate results
    }

}
