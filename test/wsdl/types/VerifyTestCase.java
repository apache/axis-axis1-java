/**
 * TypeTestServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 */

package test.wsdl.types;

import java.util.Calendar;
import java.util.Date;

import javax.xml.rpc.ServiceException;

import javax.xml.rpc.holders.BigDecimalHolder;
import javax.xml.rpc.holders.BigIntegerHolder;
import javax.xml.rpc.holders.BooleanWrapperHolder;
import javax.xml.rpc.holders.BooleanHolder;
import javax.xml.rpc.holders.ByteArrayHolder;
import javax.xml.rpc.holders.ByteHolder;
import javax.xml.rpc.holders.CalendarHolder;
import javax.xml.rpc.holders.DoubleWrapperHolder;
import javax.xml.rpc.holders.DoubleHolder;
import javax.xml.rpc.holders.FloatWrapperHolder;
import javax.xml.rpc.holders.FloatHolder;
import javax.xml.rpc.holders.IntegerWrapperHolder;
import javax.xml.rpc.holders.IntHolder;
import javax.xml.rpc.holders.LongHolder;
import javax.xml.rpc.holders.ObjectHolder;
import javax.xml.rpc.holders.QNameHolder;
import javax.xml.rpc.holders.ShortWrapperHolder;
import javax.xml.rpc.holders.ShortHolder;
import javax.xml.rpc.holders.StringHolder;

import javax.xml.namespace.QName;

import test.wsdl.types.comprehensive_types.Animal;
import test.wsdl.types.comprehensive_types.AnimalHolder;
import test.wsdl.types.comprehensive_types.ArrayHolder;
import test.wsdl.types.comprehensive_types.ArrayMHolder;
import test.wsdl.types.comprehensive_types.Cat;
import test.wsdl.types.comprehensive_types.CatHolder;
import test.wsdl.types.comprehensive_types.PersionCat;
import test.wsdl.types.comprehensive_types.Yarn;
import test.wsdl.types.comprehensive_types.ComplexAll;
import test.wsdl.types.comprehensive_types.ComplexAllHolder;
import test.wsdl.types.comprehensive_types.ComplexSequence;
import test.wsdl.types.comprehensive_types.ComplexSequenceHolder;
import test.wsdl.types.comprehensive_types.ComplexWComplex;
import test.wsdl.types.comprehensive_types.ComplexWComplexHolder;
import test.wsdl.types.comprehensive_types.ElemWComplex;
import test.wsdl.types.comprehensive_types.ElemWComplexHolder;
import test.wsdl.types.comprehensive_types.EmptyComplexType;
import test.wsdl.types.comprehensive_types.EmptyComplexTypeHolder;
import test.wsdl.types.comprehensive_types.EmptyFault;
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
import test.wsdl.types.comprehensive_types.Simple;
import test.wsdl.types.comprehensive_types.SimpleFwd;
import test.wsdl.types.comprehensive_types.Time;
import test.wsdl.types.comprehensive_types.StringParameter;
import test.wsdl.types.comprehensive_types2.A;
import test.wsdl.types.comprehensive_types2.B;

import test.wsdl.types.comprehensive_service.TypeTest;
import test.wsdl.types.comprehensive_service.TypeTestServiceLocator;

public class VerifyTestCase extends junit.framework.TestCase {
    public VerifyTestCase(String name) {
        super(name);
    }

    public void testTypeTest() {
        TypeTest binding;
        try {
            binding = new TypeTestServiceLocator().getTypeTest();
        }
        catch (ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);
        try {
            binding.allPrimitivesIn(
                    "hi",
                    new java.math.BigInteger("5"),
                    0,
                    (long) 0,
                    (short) 0,
                    new java.math.BigDecimal(6),
                    (float) 0,
                    (double) 0,
                    true,
                    (byte) 0,
                    new QName("hi", "ho"),
                    Calendar.getInstance(),
                    new byte[]{(byte) 5},
                    new byte[]{(byte) 6},
                    "hi ho",
                    new Boolean(true),
                    new Float(0),
                    new Double(0),
                    new java.math.BigDecimal(7),
                    new Integer(0),
                    new Short((short) 0),
                    new byte[]{(byte) 7});
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
                    new CalendarHolder(Calendar.getInstance()),
                    new ByteArrayHolder(new byte[]{(byte) 8}),
                    new ByteArrayHolder(new byte[]{(byte) 9}),
                    new StringHolder("ho hi"),
                    new BooleanWrapperHolder(new Boolean(true)),
                    new FloatWrapperHolder(new Float(10)),
                    new DoubleWrapperHolder(new Double(11)),
                    new BigDecimalHolder(new java.math.BigDecimal(12)),
                    new IntegerWrapperHolder(new Integer(13)),
                    new ShortWrapperHolder(new Short((short) 14)),
                    new ByteArrayHolder(new byte[]{(byte) 15}));
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
            EnumHolder value = new EnumHolder();
            binding.enumOut(value);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            Enum value = null;
            value = binding.enumReturn();
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
            ArrayHolder value = new ArrayHolder();
            binding.arrayOut(value);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
        try {
            String[] value = binding.arrayReturn();
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
            ArrayMHolder value = new ArrayMHolder();
            binding.arrayMOut(value);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
        try {
            int[][][] value = binding.arrayMReturn();
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
        ComplexAll complexAll = new ComplexAll();
        complexAll.setAreaCode(512);
        complexAll.setExchange("838");
        complexAll.setNumber("4544");
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
            ComplexAllHolder value = new ComplexAllHolder();
            binding.complexAllOut(value);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            ComplexAll value = null;
            value = binding.complexAllReturn();
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        ComplexSequence complexSequence = new ComplexSequence();
        complexSequence.setAreaCode(512);
        complexSequence.setExchange("838");
        complexSequence.setNumber("4544");

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
            ComplexSequenceHolder value = new ComplexSequenceHolder();
            binding.complexSequenceOut(value);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            ComplexSequence value = null;
            value = binding.complexSequenceReturn();
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        String[] optArray = new String[] {"abc", "def"};
        byte[][] byteArray = new byte[][] { new byte[] {'a', 'b', 'c'}, new byte[] {'x', 'y', 'z'} };
        B b = new B();
        A a = new A();
        a.setC(3);
        b.setD(a);
        ElemWComplex elemWComplex = new ElemWComplex();
        StringParameter sp = new StringParameter("sweet!");
        sp.setDescription("Pass this as an element and an attribute...wow!");

        elemWComplex.setOne( new Simple("one"));
        elemWComplex.setTwo( new QName[] {new QName("two")});
        elemWComplex.setThree( new Enum[] {Enum.three});
        elemWComplex.setEnum1( EnumString.value1);
        elemWComplex.setEnum2( EnumInt.value1);
        elemWComplex.setEnum3( EnumLong.value2);
        elemWComplex.setEnum4( EnumFloat.value3);
        elemWComplex.setEnum5( EnumDouble.value3);
        elemWComplex.setEnum6( EnumShort.value2);
        elemWComplex.setEnum7( EnumByte.value1);
        elemWComplex.setNested( b);
        elemWComplex.setOptArray( optArray );
        elemWComplex.setByteArray( byteArray );
        elemWComplex.setAttr(Enum.two);
        elemWComplex.setParm(sp);
        elemWComplex.setParmAttr(sp);

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
            ElemWComplexHolder value = new ElemWComplexHolder();
            binding.elemWComplexOut(value);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            ElemWComplex value = null;
            value = binding.elemWComplexReturn();
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        StockQuote stockQuote = new StockQuote();
        Time time = new Time();
        time.setDST(false);
        stockQuote.setTime(time);
        stockQuote.setChange(new SimpleFwd("5"));
        stockQuote.setPctchange("100%");
        stockQuote.setBid("9");
        stockQuote.setAsk("11");
        stockQuote.setSymbol("AXS");
        stockQuote.setLast("5");
        ComplexWComplex complexWComplex = new ComplexWComplex();
        complexWComplex.setStockQuote(stockQuote);
        complexWComplex.setOutside(22);
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
            ComplexWComplexHolder value = new ComplexWComplexHolder();
            binding.complexWComplexOut(value);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            ComplexWComplex value = null;
            value = binding.complexWComplexReturn();
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            EmptyComplexType value = new EmptyComplexType();
            binding.emptyComplexTypeIn(value);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            EmptyComplexTypeHolder value = new EmptyComplexTypeHolder( new EmptyComplexType());
            binding.emptyComplexTypeInout(value);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            EmptyComplexTypeHolder value = new EmptyComplexTypeHolder();
            binding.emptyComplexTypeOut(value);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            EmptyComplexType value = null;
            value = binding.emptyComplexTypeReturn();
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            binding.anyIn(new java.lang.String("hi ho"));
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            binding.anyInout(new ObjectHolder(new java.lang.String("yo ho ho")));
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            ObjectHolder value = new ObjectHolder();
            binding.anyOut(value);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            java.lang.Object value = binding.anyReturn();
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        Cat cat = new Cat();
        cat.setPurr("meow");
        PersionCat persion = new PersionCat();
        Yarn yarn = new Yarn();
        yarn.setColor("green");
        persion.setPurr("meow meow");
        persion.setColor("blue");
        persion.setToy(yarn); 
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
            AnimalHolder value = new AnimalHolder();
            binding.animalOut(value);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            Animal value = null;
            value = binding.animalReturn();
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
            CatHolder value = new CatHolder();
            binding.catOut(value);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            Cat value = null;
            value = binding.catReturn();
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            binding.catIn(persion);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            binding.catInout(new CatHolder(persion));
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }

        try {
            BooleanHolder bh = new BooleanHolder(true);
            boolean actual = binding.methodBoolean(true, bh);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            ByteHolder bh = new ByteHolder((byte)5);
            byte actual = binding.methodByte((byte)5, bh);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            ShortHolder sh = new ShortHolder((short)127);
            short actual = binding.methodShort((short)127, sh);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            IntHolder ih = new IntHolder(2002);
            int actual = binding.methodInt(2002, ih);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            LongHolder lh = new LongHolder(14003L);
            long actual = binding.methodLong(14003L, lh);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            FloatHolder fh = new FloatHolder(2.342F);
            float delta = 0.0F;
            float actual = binding.methodFloat(2.342F, fh);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            DoubleHolder dh = new DoubleHolder(5006.345D);
            double value = 110312.2325D;
            double delta = 0.0D;
            double actual = binding.methodDouble(5006.345D, dh);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            String sendValue = "Sent String"; 
            StringHolder sh = new StringHolder(sendValue);
            String actual = binding.methodString(sendValue, sh);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            java.math.BigInteger sendValue = new java.math.BigInteger("3048");
            BigIntegerHolder bih = new BigIntegerHolder(sendValue);
            java.math.BigInteger actual = binding.methodInteger(sendValue, bih);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            java.math.BigDecimal sendValue = new java.math.BigDecimal("1205.258");
            BigDecimalHolder bdh = new BigDecimalHolder(sendValue);
            java.math.BigDecimal actual = binding.methodDecimal(sendValue, bdh);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            Calendar sendValue = Calendar.getInstance();
            sendValue.setTime(new Date(1012182070626L));
            CalendarHolder ch = new CalendarHolder(sendValue);
            Calendar actual = binding.methodDateTime(sendValue, ch);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
// Comment out for now because causes compile errors
//        try {
//            byte[] sendValue = {(byte) 10, (byte) 9};
//            ByteArrayHolder bah = new ByteArrayHolder(sendValue);
//            byte[] actual = binding.methodBase64Binary(sendValue, bah);
//        } catch (java.rmi.RemoteException re) {
//            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
//        }
        try {
            QName sendValue = new QName("test1", "test2");
            QNameHolder qh = new QNameHolder(sendValue);
            QName actual = binding.methodQName(sendValue, qh);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
// Comment out for now because causes compile errors
//        try {
//            byte[] sendValue = {(byte) 10, (byte) 9};
//            ByteArrayHolder bah = new ByteArrayHolder(sendValue);
//            byte[] actual = binding.methodHexBinary(sendValue, bah);
//        } catch (java.rmi.RemoteException re) {
//            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
//        }
        try {
            String sendValue = "Sent String"; 
            StringHolder sh = new StringHolder(sendValue);
            String actual = binding.methodSoapString(sendValue, sh);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            java.lang.Boolean sendValue = new java.lang.Boolean(true);
            BooleanWrapperHolder bh = new BooleanWrapperHolder(sendValue);
            java.lang.Boolean actual = binding.methodSoapBoolean(sendValue, bh);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            java.lang.Float sendValue = new java.lang.Float(93049.0394F);
            FloatWrapperHolder fh = new FloatWrapperHolder(sendValue);
            java.lang.Float actual = binding.methodSoapFloat(sendValue, fh);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            java.lang.Double sendValue = new java.lang.Double(193049.0394D);
            DoubleWrapperHolder dh = new DoubleWrapperHolder(sendValue);
            java.lang.Double actual = binding.methodSoapDouble(sendValue, dh);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            java.math.BigDecimal sendValue = new java.math.BigDecimal("1205.258");
            BigDecimalHolder bdh = new BigDecimalHolder(sendValue);
            java.math.BigDecimal actual = binding.methodDecimal(sendValue, bdh);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            java.lang.Integer sendValue = new java.lang.Integer(94);
            IntegerWrapperHolder ich = new IntegerWrapperHolder(sendValue);
            java.lang.Integer actual = binding.methodSoapInt(sendValue, ich);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            java.lang.Short sendValue = new java.lang.Short((short) 5);
            ShortWrapperHolder sch = new ShortWrapperHolder(sendValue);
            java.lang.Short actual = binding.methodSoapShort(sendValue, sch);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
    }
}

