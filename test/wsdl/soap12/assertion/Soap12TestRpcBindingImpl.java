/**
 * Soap12TestRpcBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.soap12.assertion;

public class Soap12TestRpcBindingImpl implements test.wsdl.soap12.assertion.Soap12TestPortTypeRpc{
    public void returnVoid() throws java.rmi.RemoteException {
    }

    public test.wsdl.soap12.assertion.xsd.SOAPStruct echoStruct(test.wsdl.soap12.assertion.xsd.SOAPStruct inputStruct) throws java.rmi.RemoteException {
        return inputStruct;
    }

    public test.wsdl.soap12.assertion.xsd.SOAPStruct[] echoStructArray(test.wsdl.soap12.assertion.xsd.SOAPStruct[] inputStructArray) throws java.rmi.RemoteException {
        return inputStructArray;
    }

    public void echoStructAsSimpleTypes(test.wsdl.soap12.assertion.xsd.SOAPStruct inputStruct, javax.xml.rpc.holders.StringHolder outputString, javax.xml.rpc.holders.IntHolder outputInteger, javax.xml.rpc.holders.FloatHolder outputFloat) throws java.rmi.RemoteException {
        outputString.value = inputStruct.getVarString();
        outputInteger.value = inputStruct.getVarInt();
        outputFloat.value = inputStruct.getVarFloat();
    }

    public test.wsdl.soap12.assertion.xsd.SOAPStruct echoSimpleTypesAsStruct(java.lang.String inputString, int inputInteger, float inputFloat) throws java.rmi.RemoteException {
        test.wsdl.soap12.assertion.xsd.SOAPStruct ret = new test.wsdl.soap12.assertion.xsd.SOAPStruct();
        ret.setVarString(inputString);
        ret.setVarInt(inputInteger);
        ret.setVarFloat(inputFloat);
        return ret;
    }

    public test.wsdl.soap12.assertion.xsd.SOAPStructStruct echoNestedStruct(test.wsdl.soap12.assertion.xsd.SOAPStructStruct inputStruct) throws java.rmi.RemoteException {
        return inputStruct;
    }

    public test.wsdl.soap12.assertion.xsd.SOAPArrayStruct echoNestedArray(test.wsdl.soap12.assertion.xsd.SOAPArrayStruct inputStruct) throws java.rmi.RemoteException {
        return inputStruct;
    }

    public float[] echoFloatArray(float[] inputFloatArray) throws java.rmi.RemoteException {
        return inputFloatArray;
    }

    public java.lang.String[] echoStringArray(java.lang.String[] inputStringArray) throws java.rmi.RemoteException {
        return inputStringArray;
    }

    public int[] echoIntegerArray(int[] inputIntegerArray) throws java.rmi.RemoteException {
        return inputIntegerArray;
    }

    public byte[] echoBase64(byte[] inputBase64) throws java.rmi.RemoteException {
        return inputBase64;
    }

    public boolean echoBoolean(boolean inputBoolean) throws java.rmi.RemoteException {
        return inputBoolean;
    }

    public java.util.Calendar echoDate(java.util.Calendar inputDate) throws java.rmi.RemoteException {
        return inputDate;
    }

    public java.math.BigDecimal echoDecimal(java.math.BigDecimal inputDecimal) throws java.rmi.RemoteException {
        return inputDecimal;
    }

    public float echoFloat(float inputFloat) throws java.rmi.RemoteException {
        return inputFloat;
    }

    public java.lang.String echoString(java.lang.String inputString) throws java.rmi.RemoteException {
        return inputString;
    }

    public int countItems(java.lang.String[] inputStringArray) throws java.rmi.RemoteException {
        return inputStringArray.length;
    }

    public boolean isNil(java.lang.String inputString) throws java.rmi.RemoteException {
        return (inputString == null || inputString.length() == 0);
    }
}
