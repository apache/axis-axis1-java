/**
 * ComplexRpcEncBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.interop4.groupH.complexRPCenc;

public class ComplexRpcEncBindingImpl implements test.wsdl.interop4.groupH.complexRPCenc.ComplexRpcEncPortType{
    public void echoSOAPStructFault(test.wsdl.interop4.groupH.complexRPCenc.SOAPStructFault param) throws java.rmi.RemoteException, test.wsdl.interop4.groupH.complexRPCenc.SOAPStructFault {
        throw param;
    }

    public void echoBaseStructFault(test.wsdl.interop4.groupH.complexRPCenc.BaseStruct param) throws java.rmi.RemoteException, test.wsdl.interop4.groupH.complexRPCenc.BaseStruct {
        throw param;
    }

    public void echoExtendedStructFault(test.wsdl.interop4.groupH.complexRPCenc.ExtendedStruct param) throws java.rmi.RemoteException, test.wsdl.interop4.groupH.complexRPCenc.ExtendedStruct {
        throw param;
    }

    public void echoMultipleFaults1(int whichFault, test.wsdl.interop4.groupH.complexRPCenc.SOAPStruct param1, test.wsdl.interop4.groupH.complexRPCenc.BaseStruct param2) throws java.rmi.RemoteException, test.wsdl.interop4.groupH.complexRPCenc.BaseStruct, test.wsdl.interop4.groupH.complexRPCenc.SOAPStructFault {
        if (whichFault == 2) {
            throw param2;
        }
        throw new SOAPStructFault(param1);
    }

    public void echoMultipleFaults2(int whichFault, test.wsdl.interop4.groupH.complexRPCenc.BaseStruct param1, test.wsdl.interop4.groupH.complexRPCenc.ExtendedStruct param2, test.wsdl.interop4.groupH.complexRPCenc.MoreExtendedStruct param3) throws java.rmi.RemoteException, test.wsdl.interop4.groupH.complexRPCenc.MoreExtendedStruct, test.wsdl.interop4.groupH.complexRPCenc.ExtendedStruct, test.wsdl.interop4.groupH.complexRPCenc.BaseStruct {
        if (whichFault == 2) {
            throw param2;
        } else if (whichFault == 3) {
            throw param3;
        }
        throw param1;
    }

}
