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
        DataTypes_Binding binding;
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
            System.out.println("sayHello: " + value);
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test2DataTypesSoapSayHelloName() {
        DataTypes_Binding binding;
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
            System.out.println("sayHelloName: " + value);
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test3DataTypesSoapGetIntArray() {
        DataTypes_Binding binding;
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
            System.out.print("getIntArray: {");
            for(int i=0;i<array.length;i++) {
                System.out.print(array[i]);
                if((i+1) != array.length)
                    System.out.print(",");
                else
                    System.out.println("}");
            }
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test4DataTypesSoapGetMode() {
        DataTypes_Binding binding;
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
            System.out.println("getMode: " + value.toString());
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test5DataTypesSoapGetOrder() {
        DataTypes_Binding binding;
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
            System.out.println("getOrder: {" + value.getOrderID() + "," + value.getPrice() + "}");
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test6DataTypesSoapGetOrders() {
        DataTypes_Binding binding;
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
            for(int i=0;i<orders.length;i++)
                System.out.println("getOrder["+i+"]: {" + orders[i].getOrderID() + "," + orders[i].getPrice() + "}");
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

}

