/**
 * TypeTestServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 */

package test.wsdl.types;

import javax.xml.rpc.holders.BigDecimalHolder;
import javax.xml.rpc.holders.BigIntegerHolder;
import javax.xml.rpc.holders.BooleanClassHolder;
import javax.xml.rpc.holders.BooleanHolder;
import javax.xml.rpc.holders.ByteArrayHolder;
import javax.xml.rpc.holders.ByteClassArrayHolder;
import javax.xml.rpc.holders.ByteHolder;
import javax.xml.rpc.holders.DateHolder;
import javax.xml.rpc.holders.DoubleClassHolder;
import javax.xml.rpc.holders.DoubleHolder;
import javax.xml.rpc.holders.FloatClassHolder;
import javax.xml.rpc.holders.FloatHolder;
import javax.xml.rpc.holders.IntegerClassHolder;
import javax.xml.rpc.holders.IntHolder;
import javax.xml.rpc.holders.LongHolder;
import javax.xml.rpc.holders.QNameHolder;
import javax.xml.rpc.holders.ShortClassHolder;
import javax.xml.rpc.holders.ShortHolder;
import javax.xml.rpc.holders.StringHolder;

import javax.xml.rpc.namespace.QName;

import test.wsdl.types.comprehensive_types.Animal;
import test.wsdl.types.comprehensive_types.AnimalHolder;
import test.wsdl.types.comprehensive_types.ArrayHolder;
import test.wsdl.types.comprehensive_types.ArrayMHolder;
import test.wsdl.types.comprehensive_types.Cat;
import test.wsdl.types.comprehensive_types.CatHolder;
import test.wsdl.types.comprehensive_types.ComplexAll;
import test.wsdl.types.comprehensive_types.ComplexAllHolder;
import test.wsdl.types.comprehensive_types.ComplexSequence;
import test.wsdl.types.comprehensive_types.ComplexSequenceHolder;
import test.wsdl.types.comprehensive_types.ComplexWComplex;
import test.wsdl.types.comprehensive_types.ComplexWComplexHolder;
import test.wsdl.types.comprehensive_types.ElemWComplex;
import test.wsdl.types.comprehensive_types.ElemWComplexHolder;
import test.wsdl.types.comprehensive_types.Enum;
import test.wsdl.types.comprehensive_types.EnumHolder;
import test.wsdl.types.comprehensive_types.EnumByte;
import test.wsdl.types.comprehensive_types.EnumDouble;
import test.wsdl.types.comprehensive_types.EnumFloat;
import test.wsdl.types.comprehensive_types.EnumInt;
import test.wsdl.types.comprehensive_types.EnumLong;
import test.wsdl.types.comprehensive_types.EnumShort;
import test.wsdl.types.comprehensive_types.EnumString;
import test.wsdl.types.comprehensive_types.StockQuote;
import test.wsdl.types.comprehensive_types2.A;
import test.wsdl.types.comprehensive_types2.B;

import test.wsdl.types.comprehensive_service.TypeTest;
import test.wsdl.types.comprehensive_service.TypeTestService;

public class VerifyTestCase extends junit.framework.TestCase {
    public VerifyTestCase(String name) {
        super(name);
    }

    public void testTypeTest() {
        TypeTest binding = new TypeTestService().getTypeTest();
        assertTrue("binding is null", binding != null);
        try {
            binding.allPrimitivesIn("hi", new java.math.BigInteger("5"), 0, (long) 0, (short) 0, new java.math.BigDecimal(6), (float) 0, (double) 0, true, (byte) 0, new QName("hi", "ho"), new java.util.Date(), new byte[]{(byte) 5}, new byte[]{(byte) 6}, "hi ho", new Boolean(true), new Float(0), new Double(0), new java.math.BigDecimal(7), new Integer(0), new Short((short) 0), new Byte[]{new Byte((byte) 7)});
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            binding.allPrimitivesInout(
                    new StringHolder("hi"),
                    new BigIntegerHolder(new java.math.BigInteger("0")),
                    new IntHolder(1),
                    new LongHolder(2),
                    new ShortHolder((short) 3),
                    new BigDecimalHolder(new java.math.BigDecimal(4)),
                    new FloatHolder(5),
                    new DoubleHolder(6),
                    new BooleanHolder(true),
                    new ByteHolder((byte) 7),
                    new QNameHolder(new QName("ho", "hi")),
                    new DateHolder(new java.util.Date()),
                    new ByteArrayHolder(new byte[]{(byte) 8}),
                    new ByteArrayHolder(new byte[]{(byte) 9}),
                    new StringHolder("ho hi"),
                    new BooleanClassHolder(new Boolean(true)),
                    new FloatClassHolder(new Float(10)),
                    new DoubleClassHolder(new Double(11)),
                    new BigDecimalHolder(new java.math.BigDecimal(12)),
                    new IntegerClassHolder(new Integer(13)),
                    new ShortClassHolder(new Short((short) 14)),
                    new ByteClassArrayHolder(new Byte[]{new Byte((byte) 15)}));
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
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
                    new DateHolder(),
                    new ByteArrayHolder(),
                    new ByteArrayHolder(),
                    new StringHolder(),
                    new BooleanClassHolder(),
                    new FloatClassHolder(),
                    new DoubleClassHolder(),
                    new BigDecimalHolder(),
                    new IntegerClassHolder(),
                    new ShortClassHolder(),
                    new ByteClassArrayHolder());
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            binding.enumIn(Enum.one);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            binding.enumInout(new EnumHolder(Enum.two));
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            Enum value = null;
            value = binding.enumOut();
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            binding.arrayIn(new String[] {"hi", "ho"});
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
        try {
            binding.arrayInout(new ArrayHolder(new String[] {"hee", "hee"}));
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
        try {
            String[] value = binding.arrayOut();
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
        try {
            binding.arrayMIn(new int[][][] {new int[][] {new int[] {2}}});
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
        try {
            binding.arrayMInout(new ArrayMHolder(new int[][][] {new int[][] {new int[] {2}}}));
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
        try {
            int[][][] value = binding.arrayMOut();
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
        ComplexAll complexAll = new ComplexAll(512, "838", "4544");
        try {
            binding.complexAllIn(complexAll);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
        try {
            binding.complexAllInout(new ComplexAllHolder(complexAll));
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            ComplexAll value = null;
            value = binding.complexAllOut();
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        ComplexSequence complexSequence = new ComplexSequence(512, "838", "4544");
        try {
            binding.complexSequenceIn(complexSequence);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            binding.complexSequenceInout(new ComplexSequenceHolder(complexSequence));
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            ComplexSequence value = null;
            value = binding.complexSequenceOut();
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        ElemWComplex elemWComplex = new ElemWComplex(
                "one",
                new QName[] {new QName("two")},
                new Enum[] {Enum.three},
                EnumString.value1,
                EnumInt.value1,
                EnumLong.value2,
                EnumFloat.value3,
                EnumDouble.value3,
                EnumShort.value2,
                EnumByte.value1,
                new B(new A(3)));
        try {
            binding.elemWComplexIn(elemWComplex);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            binding.elemWComplexInout(new ElemWComplexHolder(elemWComplex));
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            ElemWComplex value = null;
            value = binding.elemWComplexOut();
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        StockQuote stockQuote = new StockQuote("hi ho!", "hi ho!", "it's off to work", "we go", "tweet tweet tweet tweet", "tweet tweet tweet tweet", "tweet tweet, tweet tweet");
        ComplexWComplex complexWComplex = new ComplexWComplex(stockQuote, 22);
        try {
            binding.complexWComplexIn(complexWComplex);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            binding.complexWComplexInout(new ComplexWComplexHolder(complexWComplex));
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            ComplexWComplex value = null;
            value = binding.complexWComplexOut();
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            binding.anyIn(new java.lang.String("hi ho"));
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            binding.anyInout(new javax.xml.rpc.holders.ObjectClassHolder(new java.lang.String("yo ho ho")));
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            java.lang.Object value = binding.anyOut();
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        Cat cat = new Cat("meow");
        try {
            binding.animalIn(cat);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            binding.animalInout(new AnimalHolder(cat));
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            Animal value = null;
            value = binding.animalOut();
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            binding.catIn(cat);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            binding.catInout(new CatHolder(cat));
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            Cat value = null;
            value = binding.catOut();
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
    }
}

