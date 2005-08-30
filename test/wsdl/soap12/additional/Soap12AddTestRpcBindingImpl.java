/**
 * Soap12AddTestRpcBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.soap12.additional;

public class Soap12AddTestRpcBindingImpl implements test.wsdl.soap12.additional.Soap12AddTestPortTypeRpc{
    public void echoVoid() throws java.rmi.RemoteException {
    }

    public test.wsdl.soap12.additional.xsd.SOAPStruct echoSimpleTypesAsStruct(java.lang.String inputString, int inputInteger, float inputFloat) throws java.rmi.RemoteException {
        return null;
    }

    // getTime is a notification style operation and is unsupported.

    public java.lang.String echoString(java.lang.String inputString) throws java.rmi.RemoteException {
        return inputString;
    }

    public test.wsdl.soap12.additional.xsd.SOAPStructTypes echoSimpleTypesAsStructOfSchemaTypes(java.lang.Object input1, java.lang.Object input2, java.lang.Object input3, java.lang.Object input4) throws java.rmi.RemoteException {
        return null;
    }

    public int echoInteger(int inputInteger) throws java.rmi.RemoteException {
        return -3;
    }

}
