/**
 * JavaNamesServiceTestTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4.1-SNAPSHOT Mar 09, 2012 (11:21:16 GMT) WSDL2Java emitter.
 */

package test.wsdl.names.javanames;

import test.HttpTestUtil;

public class JavaNamesServiceTestTestCase extends junit.framework.TestCase {
    public JavaNamesServiceTestTestCase(java.lang.String name) {
        super(name);
    }

    public void testJavaNamesWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = HttpTestUtil.getTestEndpoint(new test.wsdl.names.javanames.JavaNamesServiceTestLocator().getJavaNamesAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new test.wsdl.names.javanames.JavaNamesServiceTestLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1JavaNames_new() throws Exception {
        test.wsdl.names.javanames.JavaNames_SOAPBindingStub binding;
        try {
            JavaNamesServiceTestLocator loc = new JavaNamesServiceTestLocator();
            binding = (JavaNames_SOAPBindingStub)loc.getJavaNames(HttpTestUtil.getTestEndpoint(loc.getJavaNamesAddress()));
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
        binding._new(new java.lang.String(), new test.wsdl.names.javanames.MyAddress());
        // TBD - validate results
    }

    public void test2JavaNames_public() throws Exception {
        test.wsdl.names.javanames.JavaNames_SOAPBindingStub binding;
        try {
            JavaNamesServiceTestLocator loc = new JavaNamesServiceTestLocator();
            binding = (JavaNames_SOAPBindingStub)loc.getJavaNames(HttpTestUtil.getTestEndpoint(loc.getJavaNamesAddress()));
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
        test.wsdl.names.javanames.MyAddress value = null;
        value = binding._public(new java.lang.String());
        // TBD - validate results
    }

    public void test3JavaNamesCapitalized() throws Exception {
        test.wsdl.names.javanames.JavaNames_SOAPBindingStub binding;
        try {
            JavaNamesServiceTestLocator loc = new JavaNamesServiceTestLocator();
            binding = (JavaNames_SOAPBindingStub)loc.getJavaNames(HttpTestUtil.getTestEndpoint(loc.getJavaNamesAddress()));
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
        binding.capitalized(new java.lang.String(), new test.wsdl.names.javanames.MyAddress());
        // TBD - validate results
    }

}
