/**
 * ComplexDocLitBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.interop4.groupH.complexDocLit;

public class ComplexDocLitBindingImpl implements ComplexDocLitPortType{
    public EchoSOAPStructFaultResponse echoSOAPStructFault(SOAPStruct param) throws java.rmi.RemoteException, SOAPStructFault {
        throw new SOAPStructFault(param);
    }

    public EchoBaseStructFaultResponse echoBaseStructFault(BaseStruct param) throws java.rmi.RemoteException, BaseStruct {
        throw param;
    }

    public EchoExtendedStructFaultResponse echoExtendedStructFault(ExtendedStruct param) throws java.rmi.RemoteException, ExtendedStruct {
        throw param;
    }

    public EchoMultipleFaults1Response echoMultipleFaults1(EchoMultipleFaults1Request param) throws java.rmi.RemoteException, BaseStruct, SOAPStructFault {
        if (param.getWhichFault() == 2) {
            throw param.getParam2();
        }
        throw new SOAPStructFault(param.getParam1());
    }

    public EchoMultipleFaults2Response echoMultipleFaults2(EchoMultipleFaults2Request param) throws java.rmi.RemoteException, MoreExtendedStruct, ExtendedStruct, BaseStruct {
        if (param.getWhichFault() == 2) {
            throw param.getParam2();
        } else if (param.getWhichFault() == 3) {
            throw param.getParam3();
        }
        throw param.getParam1();
    }

}
