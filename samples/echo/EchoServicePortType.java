/**
 * EchoServicePortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 */

package samples.echo;

public interface EchoServicePortType extends java.rmi.Remote {
    public java.lang.String echoString(java.lang.String input) throws java.rmi.RemoteException;
    public java.lang.String[] echoStringArray(java.lang.String[] input) throws java.rmi.RemoteException;
    public int echoInteger(int input) throws java.rmi.RemoteException;
    public int[] echoIntegerArray(int[] input) throws java.rmi.RemoteException;
    public float echoFloat(float input) throws java.rmi.RemoteException;
    public float[] echoFloatArray(float[] input) throws java.rmi.RemoteException;
    public samples.echo.SOAPStruct echoStruct(samples.echo.SOAPStruct input) throws java.rmi.RemoteException;
    public samples.echo.SOAPStruct[] echoStructArray(samples.echo.SOAPStruct[] input) throws java.rmi.RemoteException;
    public void echoVoid() throws java.rmi.RemoteException;
    public byte[] echoBase64(byte[] input) throws java.rmi.RemoteException;
    public byte[] echoHexBinary(byte[] input) throws java.rmi.RemoteException;
    public java.util.Date echoDate(java.util.Date input) throws java.rmi.RemoteException;
    public java.math.BigDecimal echoDecimal(java.math.BigDecimal input) throws java.rmi.RemoteException;
    public boolean echoBoolean(boolean input) throws java.rmi.RemoteException;
    public java.util.Map echoMap(java.util.Map input) throws java.rmi.RemoteException;
    public java.util.Map[] echoMapArray(java.util.Map[] input) throws java.rmi.RemoteException;
    public void echoStructAsSimpleTypes(samples.echo.SOAPStruct inputStruct, javax.xml.rpc.holders.StringHolder outputString, javax.xml.rpc.holders.IntHolder outputInteger, javax.xml.rpc.holders.FloatHolder outputFloat) throws java.rmi.RemoteException;
    public samples.echo.SOAPStruct echoSimpleTypesAsStruct(java.lang.String inputString, int inputInteger, float inputFloat) throws java.rmi.RemoteException;
    public java.lang.String[][] echo2DStringArray(java.lang.String[][] input2DStringArray) throws java.rmi.RemoteException;
    public samples.echo.SOAPStructStruct echoNestedStruct(samples.echo.SOAPStructStruct inputStruct) throws java.rmi.RemoteException;
    public samples.echo.SOAPArrayStruct echoNestedArray(samples.echo.SOAPArrayStruct inputStruct) throws java.rmi.RemoteException;
}
