/**
 * Implementation of simple RPC encoded fault services for interop4 tests
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */

package test.wsdl.interop4.groupH.simpleRPCenc;

public class SimpleRpcEncBindingImpl implements test.wsdl.interop4.groupH.simpleRPCenc.SimpleRpcEncPortType{
    public void echoEmptyFault() throws java.rmi.RemoteException, test.wsdl.interop4.groupH.simpleRPCenc.EmptyFault {
        throw new EmptyFault();
    }

    public void echoStringFault(java.lang.String param) throws java.rmi.RemoteException, test.wsdl.interop4.groupH.simpleRPCenc.StringFault {
        StringFault fault = new StringFault(param);
        throw fault;
    }

    public void echoIntArrayFault(int[] param) throws java.rmi.RemoteException, test.wsdl.interop4.groupH.simpleRPCenc.IntArrayFault {
        throw new IntArrayFault(param);
    }

    public void echoMultipleFaults1(int whichFault, java.lang.String param1, float[] param2) throws java.rmi.RemoteException, test.wsdl.interop4.groupH.simpleRPCenc.FloatArrayFault, test.wsdl.interop4.groupH.simpleRPCenc.StringFault, test.wsdl.interop4.groupH.simpleRPCenc.EmptyFault {
        switch (whichFault) {
            case 2:
                throw new StringFault(param1);
            case 3:
                throw new FloatArrayFault(param2);
            default:
                throw new EmptyFault();
        }
    }

    public void echoMultipleFaults2(int whichFault, java.lang.String param1, float param2, java.lang.String[] param3) throws java.rmi.RemoteException, test.wsdl.interop4.groupH.simpleRPCenc.StringArrayFault, test.wsdl.interop4.groupH.simpleRPCenc.FloatFault, test.wsdl.interop4.groupH.simpleRPCenc.StringFault {
        switch (whichFault) {
            case 2:
                throw new StringFault(param1);
            case 3:
                throw new StringArrayFault(param3);
            default:
                throw new FloatFault(param2);
        }
    }

    public void echoMultipleFaults3(int whichFault, java.lang.String param1, java.lang.String param2) throws java.rmi.RemoteException, test.wsdl.interop4.groupH.simpleRPCenc.String2Fault, test.wsdl.interop4.groupH.simpleRPCenc.StringFault {
        switch (whichFault) {
            case 2:
                throw new String2Fault(param2);
            default:
                throw new StringFault(param1);
        }
    }

    public void echoMultipleFaults4(int whichFault, int param1, test.wsdl.interop4.groupH.simpleRPCenc._enum param2) throws java.rmi.RemoteException, test.wsdl.interop4.groupH.simpleRPCenc.EnumFault, test.wsdl.interop4.groupH.simpleRPCenc.IntFault {
        switch (whichFault) {
            case 2:
                throw new EnumFault(param2);
            default:
                throw new IntFault(param1);
        }
    }
}
