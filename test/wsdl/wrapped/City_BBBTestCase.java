/**
 * CityBBBTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 */

package test.wsdl.wrapped;

public class City_BBBTestCase extends junit.framework.TestCase {
    public City_BBBTestCase(String name) {
        super(name);
    }
    public void test1CityBBBPortGetAttraction() {
        City_BBBBinding binding;
        try {
            binding = new City_BBBLocator().getCity_BBBPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            Attraction value = binding.getAttraction("Christmas");
            assertEquals("OID value was wrong", value.get_OID(),
                         City_BBBBindingImpl.OID_STRING);
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }
}

