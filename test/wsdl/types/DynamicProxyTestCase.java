/**
 * TypeTestServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 */

package test.wsdl.types;

import java.math.BigDecimal;
import java.math.BigInteger;

import java.net.MalformedURLException;
import java.net.URL;

import java.rmi.RemoteException;

import java.util.Calendar;

import javax.xml.rpc.Call;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.ServiceFactory;
import javax.xml.rpc.Stub;

import javax.xml.rpc.holders.BigDecimalHolder;
import javax.xml.rpc.holders.BigIntegerHolder;
import javax.xml.rpc.holders.BooleanHolder;
import javax.xml.rpc.holders.BooleanWrapperHolder;
import javax.xml.rpc.holders.ByteArrayHolder;
import javax.xml.rpc.holders.ByteHolder;
import javax.xml.rpc.holders.CalendarHolder;
import javax.xml.rpc.holders.DoubleHolder;
import javax.xml.rpc.holders.DoubleWrapperHolder;
import javax.xml.rpc.holders.FloatHolder;
import javax.xml.rpc.holders.FloatWrapperHolder;
import javax.xml.rpc.holders.IntegerWrapperHolder;
import javax.xml.rpc.holders.IntHolder;
import javax.xml.rpc.holders.LongHolder;
import javax.xml.rpc.holders.QNameHolder;
import javax.xml.rpc.holders.ShortHolder;
import javax.xml.rpc.holders.ShortWrapperHolder;
import javax.xml.rpc.holders.StringHolder;

import javax.xml.namespace.QName;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import test.wsdl.types.comprehensive_service.TypeTest;

public class DynamicProxyTestCase extends TestCase {
    public DynamicProxyTestCase(String name) {
        super(name);
    }

    private TypeTest getProxyWithWSDL() {
        try {
            Service service = ServiceFactory.newInstance().createService(
                new URL("file", "", "test/wsdl/types/ComprehensiveTypes.wsdl"),
                new QName("urn:comprehensive-service.types.wsdl.test",
                        "TypeTestService"));
            return (TypeTest) service.getPort(
                new QName("", "TypeTest"), TypeTest.class);
        }
        catch (MalformedURLException mue) {
            throw new AssertionFailedError(
                    "MalformedURLException caught: " + mue);
        }
        catch (ServiceException jre) {
            throw new AssertionFailedError("ServiceException caught: " + jre);
        }
    } // getProxyWithWSDL

    private TypeTest getProxy() {
        try {
            Service service = ServiceFactory.newInstance().createService(null);
            Stub binding = (Stub) service.getPort(TypeTest.class);
            binding._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY,
                    "http://localhost:8080/axis/services/TypeTest");
            return (TypeTest) binding;
            
        }
        catch (ServiceException jre) {
            jre.printStackTrace();
            throw new AssertionFailedError("ServiceException caught: " + jre);
        }
    } // getProxy

    private void allPrimitivesIn(TypeTest binding) {
        assertTrue("binding is null", binding != null);
        try {

            binding.allPrimitivesIn(
                    new String(),
                    new BigInteger("0"),
                    0,
                    0,
                    (short)0,
                    new BigDecimal(0),
                    0,
                    0,
                    true,
                    (byte)0,
                    new QName("http://double-double", "toil-and-trouble"),
                    Calendar.getInstance(),
                    new byte[0],
                    new byte[0],
                    new String(),
                    new Boolean(false),
                    new Float(0),
                    new Double(0),
                    new BigDecimal(0),
                    new Integer(0),
                    new Short((short)0),
                    new byte[0]);
        }
        catch (RemoteException re) {
            re.printStackTrace();
            throw new AssertionFailedError("Remote Exception caught: " + re);
        }
    } // allPrimitivesIn

    public void test1TypeTestAllPrimitivesIn() {
        TypeTest binding = getProxyWithWSDL();
        allPrimitivesIn(binding);
        binding = getProxy();
        allPrimitivesIn(binding);
    } // test2TypeTestAllPrimitivesInout

    private void allPrimitivesInout(TypeTest binding) {
        assertTrue("binding is null", binding != null);
        try {
            binding.allPrimitivesInout(
                    new StringHolder(new String()),
                    new BigIntegerHolder(new BigInteger("0")),
                    new IntHolder(0),
                    new LongHolder(0),
                    new ShortHolder((short)0),
                    new BigDecimalHolder(new BigDecimal(0)),
                    new FloatHolder(0),
                    new DoubleHolder(0),
                    new BooleanHolder(true),
                    new ByteHolder((byte)0),
                    new QNameHolder(new QName("http://double-double",
                            "toil-and-trouble")),
                    new CalendarHolder(Calendar.getInstance()),
                    new ByteArrayHolder(new byte[0]),
                    new ByteArrayHolder(new byte[0]),
                    new StringHolder(new String()),
                    new BooleanWrapperHolder(new Boolean(false)),
                    new FloatWrapperHolder(new Float(0)),
                    new DoubleWrapperHolder(new Double(0)),
                    new BigDecimalHolder(new BigDecimal(0)),
                    new IntegerWrapperHolder(new Integer(0)),
                    new ShortWrapperHolder(new Short((short)0)),
                    new ByteArrayHolder(new byte[0]));
        }
        catch (RemoteException re) {
            re.printStackTrace();
            throw new AssertionFailedError("Remote Exception caught: " + re);
        }
    } // allPrimitivesInout

    public void test2TypeTestAllPrimitivesInout() {
/*
        TypeTest binding = getProxyWithWSDL();
        allPrimitivesInout(binding);
        binding = getProxy();
        allPrimitivesInout(binding);
*/
    } // test2TypeTestAllPrimitivesInout

    private void allPrimitivesOut(TypeTest binding) {
        assertTrue("binding is null", binding != null);
        try {
            binding.allPrimitivesOut(
                    new StringHolder(), 
                    new BigIntegerHolder(), 
                    new IntHolder(), 
                    new LongHolder(), 
                    new ShortHolder(), 
                    new BigDecimalHolder(), 
                    new FloatHolder(), 
                    new DoubleHolder(), 
                    new BooleanHolder(), 
                    new ByteHolder(), 
                    new QNameHolder(), 
                    new CalendarHolder(), 
                    new ByteArrayHolder(), 
                    new ByteArrayHolder(), 
                    new StringHolder(), 
                    new BooleanWrapperHolder(), 
                    new FloatWrapperHolder(), 
                    new DoubleWrapperHolder(), 
                    new BigDecimalHolder(), 
                    new IntegerWrapperHolder(), 
                    new ShortWrapperHolder(), 
                    new ByteArrayHolder());
        }
        catch (RemoteException re) {
            throw new AssertionFailedError("Remote Exception caught: " + re);
        }
    } // allPrimitivesOut

    public void test3TypeTestAllPrimitivesOut() {
/*
        TypeTest binding = getProxyWithWSDL();
        allPrimitivesOut(binding);
        binding = getProxy();
        allPrimitivesOut(binding);
*/
    } // test3TypeTestAllPrimitivesOut

/*
    public void test4TypeTestEnumIn() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            binding.enumIn(test.wsdl.types.comprehensive_types.Enum.one);
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test5TypeTestEnumInout() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            binding.enumInout(new test.wsdl.types.comprehensive_types.EnumHolder(test.wsdl.types.comprehensive_types.Enum.one));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test6TypeTestEnumOut() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            binding.enumOut(new test.wsdl.types.comprehensive_types.EnumHolder());
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test7TypeTestEnumReturn() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            test.wsdl.types.comprehensive_types.Enum value = null;
            value = binding.enumReturn();
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test8TypeTestArrayIn() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            binding.arrayIn(new String[0]);
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test9TypeTestArrayInout() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            binding.arrayInout(new test.wsdl.types.comprehensive_types.ArrayHolder(new String[0]));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test10TypeTestArrayOut() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            binding.arrayOut(new test.wsdl.types.comprehensive_types.ArrayHolder());
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test11TypeTestArrayReturn() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            String[] value = null;
            value = binding.arrayReturn();
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test12TypeTestArrayMIn() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            binding.arrayMIn(new int[0][0][0]);
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test13TypeTestArrayMInout() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            binding.arrayMInout(new test.wsdl.types.comprehensive_types.ArrayMHolder(new int[0][0][0]));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test14TypeTestArrayMOut() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            binding.arrayMOut(new test.wsdl.types.comprehensive_types.ArrayMHolder());
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test15TypeTestArrayMReturn() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            int[][][] value = null;
            value = binding.arrayMReturn();
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test16TypeTestComplexAllIn() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            binding.complexAllIn(new test.wsdl.types.comprehensive_types.ComplexAll());
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test17TypeTestComplexAllInout() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            binding.complexAllInout(new test.wsdl.types.comprehensive_types.ComplexAllHolder(new test.wsdl.types.comprehensive_types.ComplexAll()));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test18TypeTestComplexAllOut() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            binding.complexAllOut(new test.wsdl.types.comprehensive_types.ComplexAllHolder());
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test19TypeTestComplexAllReturn() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            test.wsdl.types.comprehensive_types.ComplexAll value = null;
            value = binding.complexAllReturn();
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test20TypeTestComplexSequenceIn() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            binding.complexSequenceIn(new test.wsdl.types.comprehensive_types.ComplexSequence());
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test21TypeTestComplexSequenceInout() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            binding.complexSequenceInout(new test.wsdl.types.comprehensive_types.ComplexSequenceHolder(new test.wsdl.types.comprehensive_types.ComplexSequence()));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test22TypeTestComplexSequenceOut() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            binding.complexSequenceOut(new test.wsdl.types.comprehensive_types.ComplexSequenceHolder());
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test23TypeTestComplexSequenceReturn() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            test.wsdl.types.comprehensive_types.ComplexSequence value = null;
            value = binding.complexSequenceReturn();
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test24TypeTestElemWComplexIn() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            binding.elemWComplexIn(new test.wsdl.types.comprehensive_types.ElemWComplex());
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test25TypeTestElemWComplexInout() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            binding.elemWComplexInout(new test.wsdl.types.comprehensive_types.ElemWComplexHolder(new test.wsdl.types.comprehensive_types.ElemWComplex()));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test26TypeTestElemWComplexOut() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            binding.elemWComplexOut(new test.wsdl.types.comprehensive_types.ElemWComplexHolder());
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test27TypeTestElemWComplexReturn() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            test.wsdl.types.comprehensive_types.ElemWComplex value = null;
            value = binding.elemWComplexReturn();
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test28TypeTestComplexWComplexIn() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            binding.complexWComplexIn(new test.wsdl.types.comprehensive_types.ComplexWComplex());
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test29TypeTestComplexWComplexInout() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            binding.complexWComplexInout(new test.wsdl.types.comprehensive_types.ComplexWComplexHolder(new test.wsdl.types.comprehensive_types.ComplexWComplex()));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test30TypeTestComplexWComplexOut() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            binding.complexWComplexOut(new test.wsdl.types.comprehensive_types.ComplexWComplexHolder());
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test31TypeTestComplexWComplexReturn() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            test.wsdl.types.comprehensive_types.ComplexWComplex value = null;
            value = binding.complexWComplexReturn();
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test32TypeTestAnyIn() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            binding.anyIn(new String());
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test33TypeTestAnyInout() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            binding.anyInout(new ObjectHolder(new String()));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test34TypeTestAnyOut() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            binding.anyOut(new ObjectHolder());
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test35TypeTestAnyReturn() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            Object value = null;
            value = binding.anyReturn();
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test36TypeTestAnimalIn() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            binding.animalIn(new test.wsdl.types.comprehensive_types.Animal());
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test37TypeTestAnimalInout() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            binding.animalInout(new test.wsdl.types.comprehensive_types.AnimalHolder(new test.wsdl.types.comprehensive_types.Animal()));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test38TypeTestAnimalOut() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            binding.animalOut(new test.wsdl.types.comprehensive_types.AnimalHolder());
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test39TypeTestAnimalReturn() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            test.wsdl.types.comprehensive_types.Animal value = null;
            value = binding.animalReturn();
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test40TypeTestCatIn() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            binding.catIn(new test.wsdl.types.comprehensive_types.Cat());
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test41TypeTestCatInout() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            binding.catInout(new test.wsdl.types.comprehensive_types.CatHolder(new test.wsdl.types.comprehensive_types.Cat()));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test42TypeTestCatOut() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            binding.catOut(new test.wsdl.types.comprehensive_types.CatHolder());
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test43TypeTestCatReturn() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            test.wsdl.types.comprehensive_types.Cat value = null;
            value = binding.catReturn();
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test44TypeTestMethodBoolean() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            boolean value = false;
            value = binding.methodBoolean(true, new BooleanHolder(true));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test45TypeTestMethodByte() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            byte value = -3;
            value = binding.methodByte((byte)0, new ByteHolder((byte)0));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test46TypeTestMethodShort() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            short value = -3;
            value = binding.methodShort((short)0, new ShortHolder((short)0));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test47TypeTestMethodInt() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            int value = -3;
            value = binding.methodInt(0, new IntHolder(0));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test48TypeTestMethodLong() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            long value = -3;
            value = binding.methodLong(0, new LongHolder(0));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test49TypeTestMethodFloat() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            float value = -3;
            value = binding.methodFloat(0, new FloatHolder(0));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test50TypeTestMethodDouble() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            double value = -3;
            value = binding.methodDouble(0, new DoubleHolder(0));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test51TypeTestMethodString() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            String value = null;
            value = binding.methodString(new String(), new StringHolder(new String()));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test52TypeTestMethodInteger() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            java.math.BigInteger value = null;
            value = binding.methodInteger(new java.math.BigInteger("0"), new BigIntegerHolder(new java.math.BigInteger("0")));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test53TypeTestMethodDecimal() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            java.math.BigDecimal value = null;
            value = binding.methodDecimal(new java.math.BigDecimal(0), new BigDecimalHolder(new java.math.BigDecimal(0)));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test54TypeTestMethodDateTime() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            java.util.Calendar value = null;
            value = binding.methodDateTime(java.util.Calendar.getInstance(), new CalendarHolder(java.util.Calendar.getInstance()));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test55TypeTestMethodQName() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            javax.xml.namespace.QName value = null;
            value = binding.methodQName(new javax.xml.namespace.QName("http://double-double", "toil-and-trouble"), new QNameHolder(new javax.xml.namespace.QName("http://double-double", "toil-and-trouble")));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test56TypeTestMethodSoapString() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            String value = null;
            value = binding.methodSoapString(new String(), new StringHolder(new String()));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test57TypeTestMethodSoapBoolean() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            Boolean value = null;
            value = binding.methodSoapBoolean(new Boolean(false), new BooleanWrapperHolder(new Boolean(false)));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test58TypeTestMethodSoapFloat() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            Float value = null;
            value = binding.methodSoapFloat(new Float(0), new FloatWrapperHolder(new Float(0)));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test59TypeTestMethodSoapDouble() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            Double value = null;
            value = binding.methodSoapDouble(new Double(0), new DoubleWrapperHolder(new Double(0)));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test60TypeTestMethodSoapDecimal() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            java.math.BigDecimal value = null;
            value = binding.methodSoapDecimal(new java.math.BigDecimal(0), new BigDecimalHolder(new java.math.BigDecimal(0)));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test61TypeTestMethodSoapInt() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            Integer value = null;
            value = binding.methodSoapInt(new Integer(0), new IntegerWrapperHolder(new Integer(0)));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test62TypeTestMethodSoapShort() {
        test.wsdl.types.comprehensive_service.TypeTest binding;
        try {
            binding = new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            Short value = null;
            value = binding.methodSoapShort(new Short((short)0), new ShortWrapperHolder(new Short((short)0)));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }
*/
}

