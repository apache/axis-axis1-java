/**
 * ComplexTypeExtensionsBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.interop5.complextype;

public class ComplexTypeExtensionsBindingImpl implements test.wsdl.interop5.complextype.ComplexTypeExtensionsPortType{
    public void echoBaseType_1(test.wsdl.interop5.complextype.types.holders.BaseTypeHolder param) throws java.rmi.RemoteException {
    }

    public void echoBaseType_2(test.wsdl.interop5.complextype.types.holders.BaseTypeHolder param) throws java.rmi.RemoteException {
    }

    public void echoBaseType_3(test.wsdl.interop5.complextype.types.holders.BaseTypeHolder param) throws java.rmi.RemoteException {
    }

    public void echoBaseType_4(test.wsdl.interop5.complextype.types.holders.BaseTypeHolder param) throws java.rmi.RemoteException {
    }

    public void echoBaseType_5(test.wsdl.interop5.complextype.types.holders.BaseTypeHolder param) throws java.rmi.RemoteException {
    }

    public void echoL1DerivedType_1(test.wsdl.interop5.complextype.types.holders.L1DerivedTypeHolder param) throws java.rmi.RemoteException {
    }

    public void echoL1DerivedType_2(test.wsdl.interop5.complextype.types.holders.L1DerivedTypeHolder param) throws java.rmi.RemoteException {
    }

    public void echoL2DerivedType1_1(test.wsdl.interop5.complextype.types.holders.L2DerivedType1Holder param) throws java.rmi.RemoteException {
    }

    public test.wsdl.interop5.complextype.types.BaseType echoL1DerivedTypeAsBaseType(test.wsdl.interop5.complextype.types.L1DerivedType param) throws java.rmi.RemoteException {
        test.wsdl.interop5.complextype.types.BaseType output = new test.wsdl.interop5.complextype.types.BaseType();
        output.setBaseTypeMember1(param.getBaseTypeMember1());
        output.setBaseTypeMember2(param.getBaseTypeMember2());
        return output;
    }

    public test.wsdl.interop5.complextype.types.BaseType echoL2DerivedType1AsBaseType(test.wsdl.interop5.complextype.types.L2DerivedType1 param) throws java.rmi.RemoteException {
        test.wsdl.interop5.complextype.types.BaseType output = new test.wsdl.interop5.complextype.types.BaseType();
        output.setBaseTypeMember1(param.getBaseTypeMember1());
        output.setBaseTypeMember2(param.getBaseTypeMember2());
        return output;
    }

    public test.wsdl.interop5.complextype.types.L1DerivedType echoBaseTypeAsL1DerivedType(test.wsdl.interop5.complextype.types.BaseType param) throws java.rmi.RemoteException {
        return (test.wsdl.interop5.complextype.types.L1DerivedType)param;
    }

    public test.wsdl.interop5.complextype.types.L2DerivedType1 echoBaseTypeAsL2DerivedType1(test.wsdl.interop5.complextype.types.BaseType param) throws java.rmi.RemoteException {
        return (test.wsdl.interop5.complextype.types.L2DerivedType1)param;
    }

}
