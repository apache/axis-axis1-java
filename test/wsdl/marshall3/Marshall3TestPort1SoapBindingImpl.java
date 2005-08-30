/**
 * Marshall3TestPort1SoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC3 Apr 29, 2005 (10:05:23 EDT) WSDL2Java emitter.
 */

package test.wsdl.marshall3;

import java.rmi.RemoteException;
import test.wsdl.marshall3.types.QNameArrayTest;
import test.wsdl.marshall3.types.QNameArrayTestResponse;
import test.wsdl.marshall3.types.ShortArrayTest;
import test.wsdl.marshall3.types.ShortArrayTestResponse;
import test.wsdl.marshall3.types.StringArrayTest;
import test.wsdl.marshall3.types.StringArrayTestResponse;

public class Marshall3TestPort1SoapBindingImpl implements test.wsdl.marshall3.MarshallTest{
    public ShortArrayTestResponse shortArrayTest(ShortArrayTest parameters) throws java.rmi.RemoteException {
        return new ShortArrayTestResponse(parameters.getShortArray());
    }

    public StringArrayTestResponse stringArrayTest(StringArrayTest parameters) throws java.rmi.RemoteException {
        return new StringArrayTestResponse(parameters.getStringArray());
    }

    public QNameArrayTestResponse qnameArrayTest(QNameArrayTest parameters) throws java.rmi.RemoteException {
        if (parameters.getQnameArray().length != 3) {
            throw new java.rmi.RemoteException("Array size mismatch");
        }
        return new QNameArrayTestResponse(parameters.getQnameArray());
    }

    public short[] echoShortListTypeTest(short[] fooShortListTypeRequest) throws java.rmi.RemoteException {
        return fooShortListTypeRequest;
    }

}
