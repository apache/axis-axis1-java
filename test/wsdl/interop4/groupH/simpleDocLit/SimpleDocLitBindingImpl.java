/**
 * SimpleDocLitBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.interop4.groupH.simpleDocLit;

public class SimpleDocLitBindingImpl implements test.wsdl.interop4.groupH.simpleDocLit.SimpleDocLitPortType{
    public test.wsdl.interop4.groupH.simpleDocLit.EchoEmptyFaultResponse echoEmptyFault(test.wsdl.interop4.groupH.simpleDocLit.EchoEmptyFaultRequest param) throws java.rmi.RemoteException, test.wsdl.interop4.groupH.simpleDocLit.EmptyPart {
        throw new EmptyPart();
    }

    public test.wsdl.interop4.groupH.simpleDocLit.EchoStringFaultResponse echoStringFault(String param) throws java.rmi.RemoteException, test.wsdl.interop4.groupH.simpleDocLit.StringFault {
        throw new StringFault(param);
    }

    public test.wsdl.interop4.groupH.simpleDocLit.EchoIntArrayFaultResponse echoIntArrayFault(test.wsdl.interop4.groupH.simpleDocLit.ArrayOfInt param) throws java.rmi.RemoteException, test.wsdl.interop4.groupH.simpleDocLit.ArrayOfInt {
        throw param;
    }

    public test.wsdl.interop4.groupH.simpleDocLit.EchoMultipleFaults1Response echoMultipleFaults1(test.wsdl.interop4.groupH.simpleDocLit.EchoMultipleFaults1Request param) throws java.rmi.RemoteException, test.wsdl.interop4.groupH.simpleDocLit.ArrayOfFloat, test.wsdl.interop4.groupH.simpleDocLit.StringFault, test.wsdl.interop4.groupH.simpleDocLit.EmptyPart {
        switch (param.getWhichFault()) {
            case 2:
                throw new StringFault(param.getParam1());
            case 3:
                throw new ArrayOfFloat(param.getParam2().getValue());
            default:
                throw new EmptyPart();
        }
    }

    public test.wsdl.interop4.groupH.simpleDocLit.EchoMultipleFaults2Response echoMultipleFaults2(test.wsdl.interop4.groupH.simpleDocLit.EchoMultipleFaults2Request param) throws java.rmi.RemoteException, test.wsdl.interop4.groupH.simpleDocLit.ArrayOfString, test.wsdl.interop4.groupH.simpleDocLit.FloatFault, test.wsdl.interop4.groupH.simpleDocLit.StringFault {
        switch (param.getWhichFault()) {
             case 2:
                 throw new StringFault(param.getParam1());
             case 3:
                 throw new ArrayOfString(param.getParam3().getValue());
             default:
                 throw new FloatFault(param.getParam2());
         }
    }

    public test.wsdl.interop4.groupH.simpleDocLit.EchoMultipleFaults3Response echoMultipleFaults3(test.wsdl.interop4.groupH.simpleDocLit.EchoMultipleFaults3Request param) throws java.rmi.RemoteException, test.wsdl.interop4.groupH.simpleDocLit.String2Fault, test.wsdl.interop4.groupH.simpleDocLit.StringFault {
        switch (param.getWhichFault()) {
            case 2:
                throw new String2Fault(param.getParam2());
            default:
                throw new StringFault(param.getParam1());
        }
    }

    public test.wsdl.interop4.groupH.simpleDocLit.EchoMultipleFaults4Response echoMultipleFaults4(test.wsdl.interop4.groupH.simpleDocLit.EchoMultipleFaults4Request param) throws java.rmi.RemoteException, test.wsdl.interop4.groupH.simpleDocLit.EnumFault, test.wsdl.interop4.groupH.simpleDocLit.IntFault {
        switch (param.getWhichFault()) {
            case 2:
                throw new EnumFault(param.getParam2());
            default:
                throw new IntFault(param.getParam1());
        }
    }

}
