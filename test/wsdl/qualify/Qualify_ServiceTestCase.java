/**
 * Qualify_ServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 */

package test.wsdl.qualify;

public class Qualify_ServiceTestCase extends junit.framework.TestCase {
    public Qualify_ServiceTestCase(String name) {
        super(name);
    }
    public void test1QualifySimple() {
        test.wsdl.qualify.Qualify_Binding binding;
        try {
            binding = new test.wsdl.qualify.Qualify_ServiceLocator().getQualify();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            java.lang.String value = null;
            java.lang.String name = "Tommy";
            value = binding.simple(name);
            
            // Get the XML response
            // Validate XML reponse to make sure elements are properly qualified
            // or not per the WSDL
            // FIXME
            
            // Check the response
            junit.framework.Assert.assertEquals(value, "Hello there: " + name);
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test2QualifyFormOverride() {
        test.wsdl.qualify.Qualify_Binding binding;
        try {
            binding = new test.wsdl.qualify.Qualify_ServiceLocator().getQualify();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            test.wsdl.qualify.Response value = null;
            value = binding.formOverride(new test.wsdl.qualify.Complex());
            
            // Get the XML response
            // Validate XML reponse to make sure elements are properly qualified
            // or not per the WSDL
            // FIXME
            
            // Check the response
            junit.framework.Assert.assertEquals(value.getName(), "Tommy");
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

}

