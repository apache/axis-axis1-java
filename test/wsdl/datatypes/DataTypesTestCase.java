/**
 * DataTypesTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 */

package test.wsdl.datatypes;

public class DataTypesTestCase extends junit.framework.TestCase {
    public DataTypesTestCase(String name) {
        super(name);
    }
    public void test1DataTypesSoapSayHello() {
        DataTypes_Port binding;
        try {
            binding = new DataTypes_ServiceLocator().getDataTypes();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            java.lang.String value = null;
            value = binding.sayHello();
            assertTrue("sayHello should be \"Hello World!\", but instead is " + value, "Hello World!".equals(value));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test2DataTypesSoapSayHelloName() {
        DataTypes_Port binding;
        try {
            binding = new DataTypes_ServiceLocator().getDataTypes();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            java.lang.String value = null;
            value = binding.sayHelloName(new java.lang.String("Axis"));
            assertTrue("sayHelloName should be \"Hello Axis\", but instead is " + value, "Hello Axis".equals(value));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test3DataTypesSoapGetIntArray() {
        DataTypes_Port binding;
        try {
            binding = new DataTypes_ServiceLocator().getDataTypes();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            ArrayOfInt value = null;
            value = binding.getIntArray();
            int[] array = value.get_int();
            assertTrue("getIntArray size should be 5, instead is " + array.length, array.length == 5);
            for(int i=0;i<array.length;i++) {
                assertTrue("getIntArray[" + i + "] should be " + (i * 10) + ", instead is " + array[i], array[i] == i * 10);
            }
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test4DataTypesSoapGetMode() {
        DataTypes_Port binding;
        try {
            binding = new DataTypes_ServiceLocator().getDataTypes();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            Mode value = null;
            value = binding.getMode();
            assertTrue("getMode should be Off, instead it is " + value.toString(), "Off".equals(value.toString()));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test5DataTypesSoapGetOrder() {
        DataTypes_Port binding;
        try {
            binding = new DataTypes_ServiceLocator().getDataTypes();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            Order value = null;
            value = binding.getOrder();
            assertTrue("getOrder.getOrderID should be 323232, instead it is " + value.getOrderID(), value.getOrderID() == 323232);
            assertTrue("getOrder.getPrice should be 34.5, instead it is " + value.getPrice(), value.getPrice() == 34.5);
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test6DataTypesSoapGetOrders() {
        DataTypes_Port binding;
        try {
            binding = new DataTypes_ServiceLocator().getDataTypes();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            ArrayOfOrder value = null;
            value = binding.getOrders();
            Order[] orders = value.getOrder();
            assertTrue("getOrders size should be 2, instead is " + orders.length, orders.length == 2);
            for(int i=0;i<orders.length;i++) {
                if (i == 0) {
                    assertTrue("getOrders[0].getOrderID should be 323232, instead it is " + orders[i].getOrderID(), orders[i].getOrderID() == 323232);
                    assertTrue("getOrders[0].getPrice should be 34.5, instead it is " + orders[i].getPrice(), orders[i].getPrice() == 34.5);
                }
                else if (i == 1) {
                    assertTrue("getOrders[1].getOrderID should be 645645, instead it is " + orders[i].getOrderID(), orders[i].getOrderID() == 645645);
                    assertTrue("getOrders[1].getPrice should be 99.4, instead it is " + orders[i].getPrice(), orders[i].getPrice() == 99.4);
                }
            }
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

}

