/**
 * PlanWSTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.extension;

public class PlanWSTestCase extends junit.framework.TestCase {
    public PlanWSTestCase(java.lang.String name) {
        super(name);
    }

    public void testPlanWSSoapWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new test.wsdl.extension.PlanWSLocator().getPlanWSSoapAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new test.wsdl.extension.PlanWSLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1PlanWSSoapGetPlan() throws Exception {
        test.wsdl.extension.PlanWSSoapStub binding;
        try {
            binding = (test.wsdl.extension.PlanWSSoapStub)
                          new test.wsdl.extension.PlanWSLocator().getPlanWSSoap();
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
        test.wsdl.extension.Plan value = null;
        value = binding.getPlan();
        // TBD - validate results
        
        assertEquals(value.getDisposition().getCode(),"CODE #1");
        assertEquals(value.getDisposition().getDescription(),"CODE #1 Description");
    }

}
