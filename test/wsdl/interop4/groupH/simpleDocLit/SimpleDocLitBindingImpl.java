/**
 * SimpleDocLitBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.interop4.groupH.simpleDocLit;

public class SimpleDocLitBindingImpl implements test.wsdl.interop4.groupH.simpleDocLit.SimpleDocLitPortType{
    public test.wsdl.interop4.groupH.simpleDocLit._echoEmptyFaultResponse echoEmptyFault(test.wsdl.interop4.groupH.simpleDocLit._echoEmptyFaultRequest param) throws java.rmi.RemoteException, test.wsdl.interop4.groupH.simpleDocLit._EmptyPart {
        throw new _EmptyPart();
    }

    public test.wsdl.interop4.groupH.simpleDocLit._echoStringFaultResponse echoStringFault(String param) throws java.rmi.RemoteException, test.wsdl.interop4.groupH.simpleDocLit.StringFault {
        throw new StringFault(param);
    }

    public test.wsdl.interop4.groupH.simpleDocLit._echoIntArrayFaultResponse echoIntArrayFault(test.wsdl.interop4.groupH.simpleDocLit.ArrayOfInt param) throws java.rmi.RemoteException, test.wsdl.interop4.groupH.simpleDocLit.ArrayOfInt {
        throw param;
    }

    public test.wsdl.interop4.groupH.simpleDocLit._echoMultipleFaults1Response echoMultipleFaults1(test.wsdl.interop4.groupH.simpleDocLit._echoMultipleFaults1Request param) throws java.rmi.RemoteException, test.wsdl.interop4.groupH.simpleDocLit.ArrayOfFloat, test.wsdl.interop4.groupH.simpleDocLit.StringFault, test.wsdl.interop4.groupH.simpleDocLit._EmptyPart {
        switch (param.getWhichFault()) {
            case 2:
                throw new StringFault(param.getParam1());
            case 3:
                throw new ArrayOfFloat(param.getParam2().getValue());
            default:
                throw new _EmptyPart();
        }
    }

    public test.wsdl.interop4.groupH.simpleDocLit._echoMultipleFaults2Response echoMultipleFaults2(test.wsdl.interop4.groupH.simpleDocLit._echoMultipleFaults2Request param) throws java.rmi.RemoteException, test.wsdl.interop4.groupH.simpleDocLit.ArrayOfString, test.wsdl.interop4.groupH.simpleDocLit.FloatFault, test.wsdl.interop4.groupH.simpleDocLit.StringFault {
        switch (param.getWhichFault()) {
             case 2:
                 throw new StringFault(param.getParam1());
             case 3:
                 throw new ArrayOfString(param.getParam3().getValue());
             default:
                 throw new FloatFault(param.getParam2());
         }
    }

    public test.wsdl.interop4.groupH.simpleDocLit._echoMultipleFaults3Response echoMultipleFaults3(test.wsdl.interop4.groupH.simpleDocLit._echoMultipleFaults3Request param) throws java.rmi.RemoteException, test.wsdl.interop4.groupH.simpleDocLit.String2Fault, test.wsdl.interop4.groupH.simpleDocLit.StringFault {
        switch (param.getWhichFault()) {
            case 2:
                throw new String2Fault(param.getParam2());
            default:
                throw new StringFault(param.getParam1());
        }
    }

    public test.wsdl.interop4.groupH.simpleDocLit._echoMultipleFaults4Response echoMultipleFaults4(test.wsdl.interop4.groupH.simpleDocLit._echoMultipleFaults4Request param) throws java.rmi.RemoteException, test.wsdl.interop4.groupH.simpleDocLit.EnumFault, test.wsdl.interop4.groupH.simpleDocLit.IntFault {
        switch (param.getWhichFault()) {
            case 2:
                throw new EnumFault(param.getParam2());
            default:
                throw new IntFault(param.getParam1());
        }
    }

}
