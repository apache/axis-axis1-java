/**
 * CityBBBTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 */

package test.wsdl.wrapped;

public class CityBBBTestCase extends junit.framework.TestCase {
    public CityBBBTestCase(String name) {
        super(name);
    }
    public void test1CityBBBPortGetAttraction() {
        CityBBBBinding binding;
        try {
            binding = new CityBBBLocator().getCityBBBPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            GetAttractionResponse value = null;
            GetAttraction request = new GetAttraction();
            request.setAttname("Christmas");
            value = binding.getAttraction(request);
            System.out.println("OID:" + value.get_return().getOID());
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }
}

