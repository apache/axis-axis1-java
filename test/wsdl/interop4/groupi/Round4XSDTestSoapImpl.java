/**
 * Round4XSDTestSoapImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.interop4.groupi;

public class Round4XSDTestSoapImpl implements test.wsdl.interop4.groupi.Round4XSDTestSoap {
    public void echoVoid() throws java.rmi.RemoteException {
        return;
    }

    public int echoInteger(int inputInteger) throws java.rmi.RemoteException {
        return inputInteger;
    }

    public float echoFloat(float inputFloat) throws java.rmi.RemoteException {
        return inputFloat;
    }

    public java.lang.String echoString(java.lang.String inputString) throws java.rmi.RemoteException {
        return inputString;
    }

    public byte[] echoBase64(byte[] inputBase64) throws java.rmi.RemoteException {
        return inputBase64;
    }

    public java.util.Calendar echoDate(java.util.Calendar inputDate) throws java.rmi.RemoteException {
        return inputDate;
    }

    public test.wsdl.interop4.groupi.xsd.SOAPComplexType echoComplexType(test.wsdl.interop4.groupi.xsd.SOAPComplexType inputComplexType) throws java.rmi.RemoteException {
        return inputComplexType;
    }

    public int[] echoIntegerMultiOccurs(test.wsdl.interop4.groupi.ArrayOfInt inputIntegerMultiOccurs) throws java.rmi.RemoteException {
        return inputIntegerMultiOccurs.get_int();
    }

    public float[] echoFloatMultiOccurs(test.wsdl.interop4.groupi.ArrayOfFloat inputFloatMultiOccurs) throws java.rmi.RemoteException {
        return inputFloatMultiOccurs.get_float();
    }

    public java.lang.String[] echoStringMultiOccurs(test.wsdl.interop4.groupi.ArrayOfString inputStringMultiOccurs) throws java.rmi.RemoteException {
        return inputStringMultiOccurs.getString();
    }

    public test.wsdl.interop4.groupi.xsd.SOAPComplexType[] echoComplexTypeMultiOccurs(test.wsdl.interop4.groupi.xsd.ArrayOfSOAPComplexType inputComplexTypeMultiOccurs) throws java.rmi.RemoteException {
        return inputComplexTypeMultiOccurs.getSOAPComplexType();
    }

    public java.math.BigDecimal echoDecimal(java.math.BigDecimal inputDecimal) throws java.rmi.RemoteException {
        return inputDecimal;
    }

    public boolean echoBoolean(boolean inputBoolean) throws java.rmi.RemoteException {
        return inputBoolean;
    }

    public byte[] echoHexBinary(byte[] inputHexBinary) throws java.rmi.RemoteException {
        return inputHexBinary;
    }

    public void echoComplexTypeAsSimpleTypes(test.wsdl.interop4.groupi.xsd.SOAPComplexType inputComplexType, javax.xml.rpc.holders.StringHolder outputString, javax.xml.rpc.holders.IntHolder outputInteger, javax.xml.rpc.holders.FloatHolder outputFloat) throws java.rmi.RemoteException {
        outputString.value = new java.lang.String(inputComplexType.getVarString());
        outputInteger.value = inputComplexType.getVarInt();
        outputFloat.value = inputComplexType.getVarFloat();
    }

    public test.wsdl.interop4.groupi.xsd.SOAPComplexType echoSimpleTypesAsComplexType(java.lang.String inputString, int inputInteger, float inputFloat) throws java.rmi.RemoteException {
        test.wsdl.interop4.groupi.xsd.SOAPComplexType output = new test.wsdl.interop4.groupi.xsd.SOAPComplexType();
        output.setVarFloat(inputFloat);
        output.setVarInt(inputInteger);
        output.setVarString(inputString);
        return output;
    }

    public test.wsdl.interop4.groupi.xsd.SOAPComplexTypeComplexType echoNestedComplexType(test.wsdl.interop4.groupi.xsd.SOAPComplexTypeComplexType inputComplexType) throws java.rmi.RemoteException {
        return inputComplexType;
    }

    public test.wsdl.interop4.groupi.xsd.SOAPMultiOccursComplexType echoNestedMultiOccurs(test.wsdl.interop4.groupi.xsd.SOAPMultiOccursComplexType inputComplexType) throws java.rmi.RemoteException {
        return inputComplexType;
    }

    public test.wsdl.interop4.groupi.xsd.ChoiceComplexType echoChoice(test.wsdl.interop4.groupi.xsd.ChoiceComplexType inputChoice) throws java.rmi.RemoteException {
        return inputChoice;
    }

    public test.wsdl.interop4.groupi.xsd.Enum echoEnum(test.wsdl.interop4.groupi.xsd.Enum inputEnum) throws java.rmi.RemoteException {
        return inputEnum;
    }

    public java.lang.Object retAnyType(java.lang.Object inputAnyType) throws java.rmi.RemoteException {
        return inputAnyType;
    }

    public test.wsdl.interop4.groupi._return retAny(test.wsdl.interop4.groupi.InputAny inputAny) throws java.rmi.RemoteException {
        test.wsdl.interop4.groupi._return output = new test.wsdl.interop4.groupi._return();
        output.set_any(inputAny.get_any());
        return output;
    }

    public void echoVoidSoapHeader() throws java.rmi.RemoteException {
    }

}
