/**
 * HeaderServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2alpha Dec 15, 2003 (12:04:17 EST) WSDL2Java emitter.
 */

package test.wsdl.header;

public class HeaderServiceTestCase extends junit.framework.TestCase {
    public HeaderServiceTestCase(java.lang.String name) {
        super(name);
    }

    
    /* FIXME: RUNTIME WSDL broken.
    public void testheaderWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new test.wsdl.header.HeaderServiceLocator().getheaderAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new test.wsdl.header.HeaderServiceLocator().getServiceName());
        assertTrue(service != null);
    }
    */

    public void test1headerOp1() throws Exception {
        test.wsdl.header.BindingStub binding;
        try {
            binding = (test.wsdl.header.BindingStub)
                          new test.wsdl.header.HeaderServiceLocator().getheader();
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
            float value = -3;
            value = binding.op1(0, new java.lang.String(), new test.wsdl.header.HeaderType());
        }
        catch (test.wsdl.header.Op1Fault e1) {
            throw new junit.framework.AssertionFailedError("op1Fault Exception caught: " + e1);
        }
            // TBD - validate results
    }

    public void test2headerOp2() throws Exception {
        test.wsdl.header.BindingStub binding;
        try {
            binding = (test.wsdl.header.BindingStub)
                          new test.wsdl.header.HeaderServiceLocator().getheader();
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
        binding.op2(0, new test.wsdl.header.HeaderType());
        // TBD - validate results
    }

}
