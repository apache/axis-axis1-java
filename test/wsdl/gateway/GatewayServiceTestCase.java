/**
 * GatewayServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.gateway;

public class GatewayServiceTestCase extends junit.framework.TestCase {
    public GatewayServiceTestCase(java.lang.String name) {
        super(name);
    }
    public void test1GatewayTest1() throws Exception {
        test.wsdl.gateway.Gateway binding;
        try {
            binding = new test.wsdl.gateway.GatewayServiceLocator().getGateway();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        test.wsdl.gateway.MyClass myClass = new test.wsdl.gateway.MyClass();
        myClass.setValues(new String[][]{{"hello"}});
        java.lang.String value = null;
        value = binding.test1(myClass);
        // TBD - validate results
    }

    public void test2GatewayTest2() throws Exception {
        test.wsdl.gateway.Gateway binding;
        try {
            binding = new test.wsdl.gateway.GatewayServiceLocator().getGateway();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        test.wsdl.gateway.MyClass response = null;
        response = binding.test2();
        assertTrue(response.getValues()!=null);
        System.out.println("Response cell 1,1="+response.getValues()[1][1]);
    }

    public void test3GatewayTest3() throws Exception {
        test.wsdl.gateway.Gateway binding;
        try {
            binding = new test.wsdl.gateway.GatewayServiceLocator().getGateway();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        java.lang.String[][] value = null;
        value = binding.test3();
        // TBD - validate results
    }

    public void test4GatewayTest4() throws Exception {
        test.wsdl.gateway.Gateway binding;
        try {
            binding = new test.wsdl.gateway.GatewayServiceLocator().getGateway();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        java.lang.String value = null;
        value = binding.test4(new java.lang.String[0][0]);
        // TBD - validate results
    }

}
