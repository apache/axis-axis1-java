/**
 * FaultServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Oct 12, 2005 (05:12:52 CEST) WSDL2Java emitter.
 */

package test.wsdl.faults2;

public class FaultServiceTestCase extends junit.framework.TestCase {
    public FaultServiceTestCase(java.lang.String name) {
        super(name);
    }

    public void testFaultServiceWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new test.wsdl.faults2.FaultServiceLocator().getFaultServiceAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new test.wsdl.faults2.FaultServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1FaultServiceThrowFault() throws Exception {
        test.wsdl.faults2.FaultServiceSoapBindingStub binding;
        try {
            binding = (test.wsdl.faults2.FaultServiceSoapBindingStub)
                          new test.wsdl.faults2.FaultServiceLocator().getFaultService();
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
            java.lang.String value = null;
            value = binding.throwFault("throw","throw");
        }
        catch (test.wsdl.faults2.TestFault e1) {
            System.out.println("TESTFAULT EXCEPTION THROWN");
            //throw new junit.framework.AssertionFailedError("throwFaultFault Exception caught: " + e1);
        }
            // TBD - validate results
    }

}
