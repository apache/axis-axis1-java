/**
 * TypeWrapper_ServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package test.wsdl.primitiveWrappers;

public class TypeWrapper_ServiceTestCase extends junit.framework.TestCase {
    public TypeWrapper_ServiceTestCase(java.lang.String name) {
        super(name);
    }

    public void testTypeWrapperWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new test.wsdl.primitiveWrappers.TypeWrapper_ServiceLocator().getTypeWrapperAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new test.wsdl.primitiveWrappers.TypeWrapper_ServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1TypeWrapperTestWrapping() throws Exception {
        test.wsdl.primitiveWrappers.TypeWrapper_BindingStub binding;
        try {
            binding = (test.wsdl.primitiveWrappers.TypeWrapper_BindingStub)
                          new test.wsdl.primitiveWrappers.TypeWrapper_ServiceLocator().getTypeWrapper();
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
        java.lang.Integer value = null;
        Bean bean = new test.wsdl.primitiveWrappers.Bean();
        bean.setPrimitive(5);
        bean.setWrapped(null); // Since this is null it won't appear on the
                               // wire.  We should check that at some point!
        value = binding.testWrapping(new java.lang.Integer(5), bean);
        assertEquals("Wrong result from service", value, new Integer(5));
    }

}
