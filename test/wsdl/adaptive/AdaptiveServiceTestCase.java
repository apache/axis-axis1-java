/**
 * AdaptiveServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.adaptive;

import java.util.Arrays;

public class AdaptiveServiceTestCase extends junit.framework.TestCase {
    public AdaptiveServiceTestCase(java.lang.String name) {
        super(name);
    }

    public void testAdaptiveWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new test.wsdl.adaptive.AdaptiveServiceLocator().getAdaptiveAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new test.wsdl.adaptive.AdaptiveServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1AdaptiveGetServiceDescription() throws Exception {
        test.wsdl.adaptive.AdaptiveInterfaceBindingStub binding;
        try {
            binding = (test.wsdl.adaptive.AdaptiveInterfaceBindingStub)
                          new test.wsdl.adaptive.AdaptiveServiceLocator().getAdaptive();
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
        java.lang.String value = null;
        value = binding.getServiceDescription();
        // TBD - validate results
    }

    public void test2AdaptiveRankResources() throws Exception {
        test.wsdl.adaptive.AdaptiveInterfaceBindingStub binding;
        try {
            binding = (test.wsdl.adaptive.AdaptiveInterfaceBindingStub)
                          new test.wsdl.adaptive.AdaptiveServiceLocator().getAdaptive();
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
        test.wsdl.adaptive.types.ResourceInfo[] value = null;
        value = binding.rankResources(new test.wsdl.adaptive.types.ResourceInfo[0], new test.wsdl.adaptive.types.ApplicationInfo());
        // TBD - validate results
        assertTrue(value.length > 0);
        assertEquals(value[0].getId(), "Adaptive #1");
        java.lang.Object[] collection = value[0].getProperties().getCollection();
        assertTrue(collection.length > 0);
        assertTrue(Arrays.equals(collection, new String[]{"A","B","C"}));
    }

    public void test3AdaptiveEstimateTransferTime() throws Exception {
        test.wsdl.adaptive.AdaptiveInterfaceBindingStub binding;
        try {
            binding = (test.wsdl.adaptive.AdaptiveInterfaceBindingStub)
                          new test.wsdl.adaptive.AdaptiveServiceLocator().getAdaptive();
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
        int[] value = null;
        value = binding.estimateTransferTime(true, new test.wsdl.adaptive.types.ResourceInfo(), new test.wsdl.adaptive.types.ResourceInfo[0], 0, java.util.Calendar.getInstance());
        // TBD - validate results
    }

    public void test4AdaptiveLogDataTransfer() throws Exception {
        test.wsdl.adaptive.AdaptiveInterfaceBindingStub binding;
        try {
            binding = (test.wsdl.adaptive.AdaptiveInterfaceBindingStub)
                          new test.wsdl.adaptive.AdaptiveServiceLocator().getAdaptive();
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
        binding.logDataTransfer(new test.wsdl.adaptive.types.ResourceInfo(), new test.wsdl.adaptive.types.ResourceInfo(), 0, java.util.Calendar.getInstance(), java.util.Calendar.getInstance());
        // TBD - validate results
    }

    public void test5AdaptiveEstimateUsage() throws Exception {
        test.wsdl.adaptive.AdaptiveInterfaceBindingStub binding;
        try {
            binding = (test.wsdl.adaptive.AdaptiveInterfaceBindingStub)
                          new test.wsdl.adaptive.AdaptiveServiceLocator().getAdaptive();
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
        java.lang.String value = null;
        value = binding.estimateUsage(true, new test.wsdl.adaptive.types.ResourceInfo(), new java.lang.String(), 0, java.util.Calendar.getInstance(), java.util.Calendar.getInstance());
        // TBD - validate results
    }

    public void test6AdaptiveEstimateMultipleUsage() throws Exception {
        test.wsdl.adaptive.AdaptiveInterfaceBindingStub binding;
        try {
            binding = (test.wsdl.adaptive.AdaptiveInterfaceBindingStub)
                          new test.wsdl.adaptive.AdaptiveServiceLocator().getAdaptive();
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
        java.lang.String[][] value = null;
        value = binding.estimateMultipleUsage(true, new test.wsdl.adaptive.types.ResourceInfo[0], new java.lang.String[0], 0, java.util.Calendar.getInstance(), java.util.Calendar.getInstance());
        // TBD - validate results
    }

    public void test7AdaptiveEstimateNetworkGraph() throws Exception {
        test.wsdl.adaptive.AdaptiveInterfaceBindingStub binding;
        try {
            binding = (test.wsdl.adaptive.AdaptiveInterfaceBindingStub)
                          new test.wsdl.adaptive.AdaptiveServiceLocator().getAdaptive();
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
        java.lang.String[][] value = null;
        value = binding.estimateNetworkGraph(true, new test.wsdl.adaptive.types.ResourceInfo[0], 0, java.util.Calendar.getInstance(), java.util.Calendar.getInstance());
        // TBD - validate results
    }

}
