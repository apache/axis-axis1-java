/**
 * Qualify_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 */

package test.wsdl.qualify;

public class Qualify_BindingImpl implements test.wsdl.qualify.Qualify_Binding {
    public java.lang.String simple(java.lang.String name) throws java.rmi.RemoteException {
        // Validate XML request to make sure elements are properly qualified
        // or not per the WSDL
        
        // FIXME
        
        // Return a response (which the client will validate)
        return "Hello there: " + name;
    }

    public test.wsdl.qualify.Response formOverride(test.wsdl.qualify.Complex complex) throws java.rmi.RemoteException {
        // Validate XML request to make sure elements are properly qualified
        // or not per the WSDL
        // FIXME
        
        // Return a response (which the client will validate)
        test.wsdl.qualify.Response r = new Response();
        r.setName("Tommy");
        return r;
    }

}
