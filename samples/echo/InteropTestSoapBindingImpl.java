/**
 * InteropTestSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 * 
 * And then it was hand modified to echo 
 * the arguments back to the caller.
 */

package samples.echo;

public class InteropTestSoapBindingImpl implements samples.echo.InteropTestPortType {
    public java.lang.String echoString(java.lang.String inputString) throws java.rmi.RemoteException {
        return inputString;
    }

    public java.lang.String[] echoStringArray(java.lang.String[] inputStringArray) throws java.rmi.RemoteException {
        return inputStringArray;
    }

    public int echoInteger(int inputInteger) throws java.rmi.RemoteException {
        return inputInteger;
    }

    public int[] echoIntegerArray(int[] inputIntegerArray) throws java.rmi.RemoteException {
        return inputIntegerArray;
    }

    public float echoFloat(float inputFloat) throws java.rmi.RemoteException {
        return inputFloat;
    }

    public float[] echoFloatArray(float[] inputFloatArray) throws java.rmi.RemoteException {
        return inputFloatArray;
    }

    public samples.echo.SOAPStruct echoStruct(samples.echo.SOAPStruct inputStruct) throws java.rmi.RemoteException {
        return inputStruct;
    }

    public samples.echo.SOAPStruct[] echoStructArray(samples.echo.SOAPStruct[] inputStructArray) throws java.rmi.RemoteException {
        return inputStructArray;
    }

    public void echoVoid() throws java.rmi.RemoteException {
    }

    public byte[] echoBase64(byte[] inputBase64) throws java.rmi.RemoteException {
        return inputBase64;
    }

    public java.util.Calendar echoDate(java.util.Calendar inputDate) throws java.rmi.RemoteException {
        return inputDate;
    }

    public byte[] echoHexBinary(byte[] inputHexBinary) throws java.rmi.RemoteException {
        return inputHexBinary;
    }

    public java.math.BigDecimal echoDecimal(java.math.BigDecimal inputDecimal) throws java.rmi.RemoteException {
        return inputDecimal;
    }

    public boolean echoBoolean(boolean inputBoolean) throws java.rmi.RemoteException {
        return inputBoolean;
    }

    public void echoStructAsSimpleTypes(samples.echo.SOAPStruct inputStruct, javax.xml.rpc.holders.StringHolder outputString, javax.xml.rpc.holders.IntHolder outputInteger, javax.xml.rpc.holders.FloatHolder outputFloat) throws java.rmi.RemoteException {
        outputString.value = inputStruct.getVarString() ;
        outputInteger.value = inputStruct.getVarInt() ;
        outputFloat.value = inputStruct.getVarFloat() ;
    }

    public samples.echo.SOAPStruct echoSimpleTypesAsStruct(java.lang.String inputString, int inputInteger, float inputFloat) throws java.rmi.RemoteException {
        samples.echo.SOAPStruct s = new samples.echo.SOAPStruct();
        s.setVarInt(inputInteger);
        s.setVarString(inputString);
        s.setVarFloat(inputFloat);
        return s;
    }

    public java.lang.String[][] echo2DStringArray(java.lang.String[][] input2DStringArray) throws java.rmi.RemoteException {
        return input2DStringArray;
    }

    public samples.echo.SOAPStructStruct echoNestedStruct(samples.echo.SOAPStructStruct inputStruct) throws java.rmi.RemoteException {
        return inputStruct;
    }

    public samples.echo.SOAPArrayStruct echoNestedArray(samples.echo.SOAPArrayStruct inputStruct) throws java.rmi.RemoteException {
        return inputStruct;
    }

    /**
     * This method accepts a Map and echoes it back to the client.
     */
    public java.util.HashMap echoMap(java.util.HashMap input) {
        return input;
    }

    /**
     * This method accepts an array of Maps and echoes it back to the client.
     */
    public java.util.HashMap [] echoMapArray(java.util.HashMap[] input) {
        return input;
    }
}
