/**
 * DoubleBackPortTypeServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2beta May 06, 2004 (03:41:36 CEST) WSDL2Java emitter.
 */

package test.wsdl.webref;

public class DoubleBackServiceTestCase extends junit.framework.TestCase {
    public DoubleBackServiceTestCase(java.lang.String name) {
        super(name);
    }

    public void testDoubleBackServiceWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new test.wsdl.webref.DoubleBackPortTypeServiceLocator().getDoubleBackServiceAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new test.wsdl.webref.DoubleBackPortTypeServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1DoubleBackServiceEcho() throws Exception {
        test.wsdl.webref.DoubleBackServiceSoapBindingStub binding;
        try {
            binding = (test.wsdl.webref.DoubleBackServiceSoapBindingStub)
                          new test.wsdl.webref.DoubleBackPortTypeServiceLocator().getDoubleBackService();
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
        value = binding.echo( "hello" );
       	assertEquals( "hellohello", value);
    }

}
