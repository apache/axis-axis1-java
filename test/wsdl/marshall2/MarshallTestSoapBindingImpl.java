/**
 * MarshallTestSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC3 Feb 14, 2005 (08:56:33 EST) WSDL2Java emitter.
 */
package test.wsdl.marshall2;

import test.wsdl.marshall2.types.BigDecimalArrayTestResponse;
import test.wsdl.marshall2.types.BigDecimalTestResponse;
import test.wsdl.marshall2.types.BigIntegerArrayTestResponse;
import test.wsdl.marshall2.types.BigIntegerTestResponse;
import test.wsdl.marshall2.types.BooleanArrayTestResponse;
import test.wsdl.marshall2.types.BooleanTestResponse;
import test.wsdl.marshall2.types.ByteArrayTestResponse;
import test.wsdl.marshall2.types.ByteTestResponse;
import test.wsdl.marshall2.types.CalendarArrayTestResponse;
import test.wsdl.marshall2.types.CalendarTestResponse;
import test.wsdl.marshall2.types.DoubleArrayTestResponse;
import test.wsdl.marshall2.types.DoubleTestResponse;
import test.wsdl.marshall2.types.FloatArrayTestResponse;
import test.wsdl.marshall2.types.FloatTestResponse;
import test.wsdl.marshall2.types.IntArrayTestResponse;
import test.wsdl.marshall2.types.IntTestResponse;
import test.wsdl.marshall2.types.JavaBeanArrayTestResponse;
import test.wsdl.marshall2.types.JavaBeanTestResponse;
import test.wsdl.marshall2.types.LongArrayTestResponse;
import test.wsdl.marshall2.types.LongTestResponse;
import test.wsdl.marshall2.types.QNameArrayTestResponse;
import test.wsdl.marshall2.types.QNameTestResponse;
import test.wsdl.marshall2.types.ShortArrayTestResponse;
import test.wsdl.marshall2.types.ShortTestResponse;
import test.wsdl.marshall2.types.StringArrayTestResponse;
import test.wsdl.marshall2.types.StringTestResponse;

public class MarshallTestSoapBindingImpl implements test.wsdl.marshall2.
        MarshallTest {
    public test.wsdl.marshall2.types.BigDecimalArrayTestResponse bigDecimalArrayTest(test.wsdl.marshall2.types.BigDecimalArrayTest parameters)
            throws java.rmi.RemoteException {
        return new BigDecimalArrayTestResponse(parameters.getBigDecimalArray());
    }

    public test.wsdl.marshall2.types.BigDecimalTestResponse bigDecimalTest(test.wsdl.marshall2.types.BigDecimalTest parameters)
            throws java.rmi.RemoteException {
        return new BigDecimalTestResponse(parameters.getBigDecimal());
    }

    public test.wsdl.marshall2.types.BigIntegerArrayTestResponse bigIntegerArrayTest(test.wsdl.marshall2.types.BigIntegerArrayTest parameters)
            throws java.rmi.RemoteException {
        return new BigIntegerArrayTestResponse(parameters.getBigIntegerArray());
    }

    public test.wsdl.marshall2.types.BigIntegerTestResponse bigIntegerTest(test.wsdl.marshall2.types.BigIntegerTest parameters)
            throws java.rmi.RemoteException {
        return new BigIntegerTestResponse(parameters.getBigInteger());
    }

    public test.wsdl.marshall2.types.BooleanArrayTestResponse booleanArrayTest(test.wsdl.marshall2.types.BooleanArrayTest parameters)
            throws java.rmi.RemoteException {
        return new BooleanArrayTestResponse(parameters.getBooleanArray());
    }

    public test.wsdl.marshall2.types.BooleanTestResponse booleanTest(test.wsdl.marshall2.types.BooleanTest parameters)
            throws java.rmi.RemoteException {
        return new BooleanTestResponse(parameters.isBooleanValue());
    }

    public test.wsdl.marshall2.types.ByteArrayTestResponse byteArrayTest(test.wsdl.marshall2.types.ByteArrayTest parameters)
            throws java.rmi.RemoteException {
        return new ByteArrayTestResponse(parameters.getByteArray());
    }

    public test.wsdl.marshall2.types.ByteTestResponse byteTest(test.wsdl.marshall2.types.ByteTest parameters)
            throws java.rmi.RemoteException {
        return new ByteTestResponse(parameters.getByteValue());
    }

    public test.wsdl.marshall2.types.DoubleArrayTestResponse doubleArrayTest(test.wsdl.marshall2.types.DoubleArrayTest parameters)
            throws java.rmi.RemoteException {
        return new DoubleArrayTestResponse(parameters.getDoubleArray());
    }

    public test.wsdl.marshall2.types.DoubleTestResponse doubleTest(test.wsdl.marshall2.types.DoubleTest parameters)
            throws java.rmi.RemoteException {
        return new DoubleTestResponse(parameters.getDoubleValue());
    }

    public test.wsdl.marshall2.types.FloatArrayTestResponse floatArrayTest(test.wsdl.marshall2.types.FloatArrayTest parameters)
            throws java.rmi.RemoteException {
        return new FloatArrayTestResponse(parameters.getFloatArray());
    }

    public test.wsdl.marshall2.types.FloatTestResponse floatTest(test.wsdl.marshall2.types.FloatTest parameters)
            throws java.rmi.RemoteException {
        return new FloatTestResponse(parameters.getFloatValue());
    }

    public test.wsdl.marshall2.types.IntArrayTestResponse intArrayTest(test.wsdl.marshall2.types.IntArrayTest parameters)
            throws java.rmi.RemoteException {
        return new IntArrayTestResponse(parameters.getIntArray());
    }

    public test.wsdl.marshall2.types.IntTestResponse intTest(test.wsdl.marshall2.types.IntTest parameters)
            throws java.rmi.RemoteException {
        return new IntTestResponse(parameters.getIntValue());
    }

    public test.wsdl.marshall2.types.LongArrayTestResponse longArrayTest(test.wsdl.marshall2.types.LongArrayTest parameters)
            throws java.rmi.RemoteException {
        return new LongArrayTestResponse(parameters.getLongArray());
    }

    public test.wsdl.marshall2.types.LongTestResponse longTest(test.wsdl.marshall2.types.LongTest parameters)
            throws java.rmi.RemoteException {
        return new LongTestResponse(parameters.getLongValue());
    }

    public test.wsdl.marshall2.types.ShortArrayTestResponse shortArrayTest(test.wsdl.marshall2.types.ShortArrayTest parameters)
            throws java.rmi.RemoteException {
        return new ShortArrayTestResponse(parameters.getShortArray());
    }

    public test.wsdl.marshall2.types.ShortTestResponse shortTest(test.wsdl.marshall2.types.ShortTest parameters)
            throws java.rmi.RemoteException {
        return new ShortTestResponse(parameters.getShortValue());
    }

    public test.wsdl.marshall2.types.StringArrayTestResponse stringArrayTest(test.wsdl.marshall2.types.StringArrayTest parameters)
            throws java.rmi.RemoteException {
        return new StringArrayTestResponse(parameters.getStringArray());
    }

    public test.wsdl.marshall2.types.StringTestResponse stringTest(test.wsdl.marshall2.types.StringTest parameters)
            throws java.rmi.RemoteException {
        return new StringTestResponse(parameters.getStringValue());
    }

    public test.wsdl.marshall2.types.QNameTestResponse qnameTest(test.wsdl.marshall2.types.QNameTest parameters)
            throws java.rmi.RemoteException {
        return new QNameTestResponse(parameters.getQname_1());
    }

    public test.wsdl.marshall2.types.QNameArrayTestResponse qnameArrayTest(test.wsdl.marshall2.types.QNameArrayTest parameters)
            throws java.rmi.RemoteException {
        return new QNameArrayTestResponse(parameters.getQnameArray_1());
    }

    public test.wsdl.marshall2.types.CalendarArrayTestResponse calendarArrayTest(test.wsdl.marshall2.types.CalendarArrayTest parameters)
            throws java.rmi.RemoteException {
        return new CalendarArrayTestResponse(parameters.getCalendarArray());
    }

    public test.wsdl.marshall2.types.CalendarTestResponse calendarTest(test.wsdl.marshall2.types.CalendarTest parameters)
            throws java.rmi.RemoteException {
        return new CalendarTestResponse(parameters.getCalendar());
    }

    public test.wsdl.marshall2.types.JavaBeanArrayTestResponse javaBeanArrayTest(test.wsdl.marshall2.types.JavaBeanArrayTest parameters)
            throws java.rmi.RemoteException {
        return new JavaBeanArrayTestResponse(parameters.getJavaBeanArray());
    }

    public test.wsdl.marshall2.types.JavaBeanTestResponse javaBeanTest(test.wsdl.marshall2.types.JavaBeanTest parameters)
            throws java.rmi.RemoteException {
        return new JavaBeanTestResponse(parameters.getJavaBean());
    }
}
