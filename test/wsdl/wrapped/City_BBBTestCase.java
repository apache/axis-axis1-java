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
        City_BBBPortType binding;
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

    public void test2CityBBBPortGetAttractions() {
        City_BBBPortType binding;
        try {
            binding = new City_BBBLocator().getCity_BBBPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            // Invoke getAttractions with two inputs
            String[] attName = new String[] {"Christmas", "Xmas"};
            Attraction[] value = binding.getAttractions(attName);
            assertEquals("OID value was wrong for first attraction", value[0].get_OID(),
                         City_BBBBindingImpl.OID_STRING);
            assertEquals("OID value was wrong for second attaction", value[1].get_OID(),
                         City_BBBBindingImpl.OID_STRING);
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test3CityBBBPortGetAttractions() {
        City_BBBPortType binding;
        try {
            binding = new City_BBBLocator().getCity_BBBPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            // Invoke getAttractions with one input
            String[] attName = new String[] {"Christmas"};
            Attraction[] value = binding.getAttractions(attName);
            assertEquals("OID value was wrong for first attraction", value[0].get_OID(),
                         City_BBBBindingImpl.OID_STRING);
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test4CityBBBPortGetAttractions() {
        City_BBBPortType binding;
        try {
            binding = new City_BBBLocator().getCity_BBBPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            // Invoke getAttractions with one input
            String[] attName = new String[] {"Christmas", null};
            Attraction[] value = binding.getAttractions(attName);
            assertEquals("Expected returned Attraction length of 2", value.length, 2);
            assertEquals("OID value was wrong for first attraction", value[0].get_OID(),
                         City_BBBBindingImpl.OID_STRING);
            assertEquals("Attracton[1] should be null", value[1], null);
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }
    public void test5CityBBBPortGetAttractions() {
        City_BBBPortType binding;
        try {
            binding = new City_BBBLocator().getCity_BBBPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            // Invoke getAttractions with one input
            String[] attName = new String[] {"Christmas", ""};
            Attraction[] value = binding.getAttractions(attName);
            assertEquals("Expected returned Attraction length of 2", value.length, 2);
            assertEquals("OID value was wrong for first attraction", value[0].get_OID(),
                         City_BBBBindingImpl.OID_STRING);
            assertEquals("Attracton[1] should be null", value[1], null);
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }
}

