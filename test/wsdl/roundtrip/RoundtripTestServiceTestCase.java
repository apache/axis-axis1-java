/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package test.wsdl.roundtrip;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import test.wsdl.roundtrip.Investment;
import test.wsdl.roundtrip.BondInvestment;
import test.wsdl.roundtrip.StockInvestment;
import test.wsdl.roundtrip.PreferredStockInvestment;
import test.wsdl.roundtrip.RoundtripPortType;
import test.wsdl.roundtrip.RoundtripPortTypeServiceLocator;
import test.wsdl.roundtrip.CallOptions;
import test.wsdl.roundtrip.InvalidTickerSymbol;
import test.wsdl.roundtrip.InvalidTradeExchange;
import test.wsdl.roundtrip.InvalidCompanyId;

/**
 * This class contains the test methods to verify that Java mapping
 * to XML/WSDL works as specified by the JAX-RPC specification.
 *
 * The following items are tested:
 * - Primitives
 * - Standard Java Classes
 * - Arrays
 * - Multiple Arrays
 * - JAX-RPC Value Types
 * - Nillables (when used with literal element declarations) 
 *
 * @version   1.00  06 Feb 2002
 * @author    Brent Ulbricht
 */
public class RoundtripTestServiceTestCase extends TestCase {

    private RoundtripPortType binding = null;
    private static final double DOUBLE_DELTA = 0.0D;
    private static final float FLOAT_DELTA = 0.0F;

    /**
     *  The Junit framework requires that each class that subclasses
     *  TestCase define a constructor accepting a string.  This method
     *  can be used to specify a specific testXXXXX method in this
     *  class to run.
     */
    public RoundtripTestServiceTestCase(String name) {
        super(name);
    } // Constructor

    /**
     *  The setUp method executes before each test method in this class
     *  to get the binding.
     */
    public void setUp() {

        try {
            binding = new RoundtripPortTypeServiceLocator().getRoundtripTest();
        } catch (ServiceException jre) {
            fail("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

    } // setUp

    /**
     *  Test to insure that a JAX-RPC Value Type works correctly.  StockInvestment
     *  subclasses Investment and should pass data members in both the Investment 
     *  and StockInvestment classes across the wire correctly.
     */
    public void testStockInvestment() {

        try {
            StockInvestment stock = new StockInvestment();
            stock.setName("International Business Machines");
            stock.setId(1);
            stock.setTradeExchange("NYSE");
            stock.setLastTradePrice(200.55F);
            float lastTradePrice = binding.getRealtimeLastTradePrice(stock);
            assertEquals("The expected and actual values did not match.",
                         201.25F,
                         lastTradePrice,
                         FLOAT_DELTA);
            // Make sure static field dontMapToWSDL is not mapped.
            try {
                Method m = (StockInvestment.class).
                    getDeclaredMethod("getDontMapToWSDL", 
                                      new Class[] {});
                fail("Should not map static member dontMapToWSDL");
            } catch (NoSuchMethodException e) {
                // Cool the method should not be in the class
            }

            // Make sure private field avgYearlyReturn is not mapped.
            try {
                Method m = (StockInvestment.class).
                    getDeclaredMethod("getAvgYearlyReturn", 
                                      new Class[] {});
                fail("Should not map private member avgYearlyReturn");
            } catch (NoSuchMethodException e) {
                // Cool the method should not be in the class
            }
        } catch (RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testStockInvestment


    /**
     *  Test to insure that a JAX-RPC Value Type works correctly.  PreferredStockInvestment
     *  subclasses StockInvestment and should pass data members in both the Investment, 
     *  StockInvestment, and PreferredStockInvestment classes across the wire correctly.
     */
    public void testPreferredStockInvestment() {
        
        try {
            PreferredStockInvestment oldStock = new PreferredStockInvestment();
            oldStock.setName("SOAP Inc.");
            oldStock.setId(202);
            oldStock.setTradeExchange("NASDAQ");
            oldStock.setLastTradePrice(10.50F);
            oldStock.setDividendsInArrears(100.44D);
            oldStock.setPreferredYield(new BigDecimal("7.00"));
            PreferredStockInvestment newStock = binding.getDividends(oldStock);
            assertEquals("The expected and actual values did not match.",
                         newStock.getName(),
                         "AXIS Inc.");
            assertEquals("The expected and actual values did not match.",
                         203,
                         newStock.getId());
            assertEquals("The expected and actual values did not match.",
                         "NASDAQ",
                         newStock.getTradeExchange());
            assertEquals("The expected and actual values did not match.",
                         101.44D,
                         newStock.getDividendsInArrears(),
                         DOUBLE_DELTA);
            assertEquals("The expected and actual values did not match.",
                         new BigDecimal("8.00"),
                         newStock.getPreferredYield());
            assertEquals("The expected and actual values did not match.",
                         11.50F,
                         newStock.getLastTradePrice(),
                         FLOAT_DELTA);
        } catch (RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testPreferredStockInvestment

    /**
     *  The BondInvestment class contains all the supported data members:
     *  primitives, standard Java classes, arrays, and primitive wrapper
     *  classes.  This test insures that the data is transmitted across
     *  the wire correctly.
     */
    public void testRoundtripBondInvestment() {
        
        try {

            CallOptions[] callOptions = new CallOptions[2];
            callOptions[0] = new CallOptions();
            Calendar date = Calendar.getInstance();
            TimeZone gmt = TimeZone.getTimeZone("GMT");
            date.setTimeZone(gmt);
            date.setTime(new Date(1013441507388L));
            callOptions[0].setCallDate(date);
            callOptions[1] = new CallOptions();
            date = Calendar.getInstance();
            date.setTimeZone(gmt);
            date.setTime(new Date(1013441507390L));
            callOptions[1].setCallDate(date);

            short[] shortArray = {(short) 30};
            byte[] byteArray = {(byte) 1};
            Short[] wrapperShortArray = {new Short((short) 23), new Short((short) 56)};
            Byte[] wrapperByteArray = {new Byte((byte) 2), new Byte((byte) 15)};

            BondInvestment sendValue = new BondInvestment();

            sendValue.setOptions(callOptions);
            sendValue.setOptions2(callOptions);
            sendValue.setOptions3(callOptions[0]);
            sendValue.setWrapperShortArray(wrapperShortArray);
            sendValue.setWrapperByteArray(wrapperByteArray);
            sendValue.setWrapperDouble(new Double(2323.232D));
            sendValue.setWrapperFloat(new Float(23.023F));
            sendValue.setWrapperInteger(new Integer(2093));
            sendValue.setWrapperShort(new Short((short) 203));
            sendValue.setWrapperByte(new Byte((byte) 20));
            sendValue.setWrapperBoolean(new Boolean(true));
            sendValue.setShortArray(shortArray);
            sendValue.setByteArray(byteArray);
            date = Calendar.getInstance();
            date.setTimeZone(gmt);
            date.setTime(new Date(1012937861996L));
            sendValue.setCallableDate(date);
            sendValue.setBondAmount(new BigDecimal("2675.23"));
            sendValue.setPortfolioType(new BigInteger("2093"));
            sendValue.setTradeExchange("NYSE");
            sendValue.setFiftyTwoWeekHigh(45.012D);
            sendValue.setLastTradePrice(87895.32F);
            sendValue.setYield(5475L);
            sendValue.setStockBeta(32);
            sendValue.setDocType((short) 35);
            sendValue.setTaxIndicator((byte) 3);

            BondInvestment actual = binding.methodBondInvestmentInOut(sendValue);
            date.setTime(new Date(1013441507308L));

            assertEquals("The expected and actual values did not match.",
                         date,
                         actual.getOptions()[0].getCallDate());
            date.setTime(new Date(1013441507328L));
            assertEquals("The expected and actual values did not match.",
                         date,
                         actual.getOptions()[1].getCallDate());
            assertEquals("The expected and actual values did not match.",
                         new Short((short) 33),
                         actual.getWrapperShortArray()[0]);
            assertEquals("The expected and actual values did not match.",
                         new Short((short) 86),
                         actual.getWrapperShortArray()[1]);
            assertEquals("The expected and actual values did not match.",
                         new Byte((byte) 4),
                         actual.getWrapperByteArray()[0]);
            assertEquals("The expected and actual values did not match.",
                         new Byte((byte) 18),
                         actual.getWrapperByteArray()[1]);
            assertEquals("The expected and actual values did not match.",
                         new Double(33.232D),
                         actual.getWrapperDouble());
            assertEquals("The expected and actual values did not match.",
                         new Float(2.23F),
                         actual.getWrapperFloat());
            assertEquals("The expected and actual values did not match.",
                         new Integer(3),
                         actual.getWrapperInteger());
            assertEquals("The expected and actual values did not match.",
                         new Short((short) 2),
                         actual.getWrapperShort());
            assertEquals("The expected and actual values did not match.",
                         new Byte((byte) 21),
                         actual.getWrapperByte()); 
            assertEquals("The expected and actual values did not match.",
                         new Boolean(false),
                         actual.getWrapperBoolean()); 
            assertEquals("The expected and actual values did not match.",
                         (short) 36,
                         actual.getShortArray()[0]);
            assertEquals("The expected and actual values did not match.",
                         (byte) 7,
                         actual.getByteArray()[0]);
            date.setTime(new Date(1012937862997L));
            assertEquals("The expected and actual values did not match.",
                         date,
                         actual.getCallableDate());
            assertEquals("The expected and actual values did not match.",
                         new BigDecimal("2735.23"),
                         actual.getBondAmount());
            assertEquals("The expected and actual values did not match.",
                         new BigInteger("21093"),
                         actual.getPortfolioType());
            assertEquals("The expected and actual values did not match.",
                         new String("AMEX"),
                         actual.getTradeExchange());
            assertEquals("The expected and actual values did not match.",
                         415.012D,
                         actual.getFiftyTwoWeekHigh(),
                         DOUBLE_DELTA);
            assertEquals("The expected and actual values did not match.",
                         8795.32F,
                         actual.getLastTradePrice(),
                         FLOAT_DELTA);
            assertEquals("The expected and actual values did not match.",
                         575L,
                         actual.getYield());
            assertEquals("The expected and actual values did not match.",
                         3,
                         actual.getStockBeta());
            assertEquals("The expected and actual values did not match.",
                         (short) 45,
                         actual.getDocType());
            assertEquals("The expected and actual values did not match.",
                         (byte) 8,
                         actual.getTaxIndicator());

        } catch (RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testRoundtripBondInvestment

    /**
     *  The BondInvestment class contains all the supported data members:
     *  primitives, standard Java classes, arrays, and primitive wrapper
     *  classes.  This test insures that a BondInvestment class received
     *  by a remote method contains the expected values.
     */
    public void testBondInvestmentOut() {

        try {
            BondInvestment actual = binding.methodBondInvestmentOut();
            Calendar date = Calendar.getInstance();
            TimeZone gmt = TimeZone.getTimeZone("GMT");
            date.setTimeZone(gmt);
            date.setTime(new Date(1013441507308L));
            assertEquals("The expected and actual values did not match.",
                         date,
                         actual.getOptions()[0].getCallDate());
            date.setTime(new Date(1013441507328L));
            assertEquals("The expected and actual values did not match.",
                         date,
                         actual.getOptions()[1].getCallDate());
            assertEquals("The expected and actual values did not match.",
                         new Short((short) 33),
                         actual.getWrapperShortArray()[0]);
            assertEquals("The expected and actual values did not match.",
                         new Short((short) 86),
                         actual.getWrapperShortArray()[1]);
            assertEquals("The expected and actual values did not match.",
                         new Byte((byte) 4),
                         actual.getWrapperByteArray()[0]);
            assertEquals("The expected and actual values did not match.",
                         new Byte((byte) 18),
                         actual.getWrapperByteArray()[1]);
            assertEquals("The expected and actual values did not match.",
                         new Double(33.232D),
                         actual.getWrapperDouble());
            assertEquals("The expected and actual values did not match.",
                         new Float(2.23F),
                         actual.getWrapperFloat());
            assertEquals("The expected and actual values did not match.",
                         new Integer(3),
                         actual.getWrapperInteger());
            assertEquals("The expected and actual values did not match.",
                         new Short((short) 2),
                         actual.getWrapperShort());
            assertEquals("The expected and actual values did not match.",
                         new Byte((byte) 21),
                         actual.getWrapperByte()); 
            assertEquals("The expected and actual values did not match.",
                         new Boolean(false),
                         actual.getWrapperBoolean()); 
            assertEquals("The expected and actual values did not match.",
                         (short) 36,
                         actual.getShortArray()[0]);
            assertEquals("The expected and actual values did not match.",
                         (byte) 7,
                         actual.getByteArray()[0]); 
            date.setTime(new Date(1012937862997L));
            assertEquals("The expected and actual values did not match.",
                         date,
                         actual.getCallableDate());
            assertEquals("The expected and actual values did not match.",
                         new BigDecimal("2735.23"),
                         actual.getBondAmount());
            assertEquals("The expected and actual values did not match.",
                         new BigInteger("21093"),
                         actual.getPortfolioType());
            assertEquals("The expected and actual values did not match.",
                         new String("AMEX"),
                         actual.getTradeExchange());
            assertEquals("The expected and actual values did not match.",
                         415.012D,
                         actual.getFiftyTwoWeekHigh(),
                         DOUBLE_DELTA);
            assertEquals("The expected and actual values did not match.",
                         8795.32F,
                         actual.getLastTradePrice(),
                         FLOAT_DELTA);
            assertEquals("The expected and actual values did not match.",
                         575L,
                         actual.getYield());
            assertEquals("The expected and actual values did not match.",
                         3,
                         actual.getStockBeta());
            assertEquals("The expected and actual values did not match.",
                         (short) 45,
                         actual.getDocType());
            assertEquals("The expected and actual values did not match.",
                         (byte) 8,
                         actual.getTaxIndicator());
        } catch (RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testBondInvestmentOut

    /**
     *  The BondInvestment class contains all the supported data members:
     *  primitives, standard Java classes, arrays, and primitive wrapper
     *  classes.  This test insures that a remote method can recieve the
     *  BondInvestment class and that its values match the expected values.
     */
    public void testBondInvestmentIn() {

        try {

            CallOptions[] callOptions = new CallOptions[2];
            callOptions[0] = new CallOptions();
            Calendar date = Calendar.getInstance();
            TimeZone gmt = TimeZone.getTimeZone("GMT");
            date.setTimeZone(gmt);
            date.setTime(new Date(1013441507388L));
            callOptions[0].setCallDate(date);
            callOptions[1] = new CallOptions();
            date = Calendar.getInstance();
            date.setTimeZone(gmt);
            date.setTime(new Date(1013441507390L));
            callOptions[1].setCallDate(date);

            short[] shortArray = {(short) 30};
            byte[] byteArray = {(byte) 1};
            Short[] wrapperShortArray = {new Short((short) 23), new Short((short) 56)};
            Byte[] wrapperByteArray = {new Byte((byte) 2), new Byte((byte) 15)};

            BondInvestment sendValue = new BondInvestment();
            
            sendValue.setOptions(callOptions);
            sendValue.setOptions2(callOptions);
            sendValue.setOptions3(callOptions[0]);
            sendValue.setWrapperShortArray(wrapperShortArray);
            sendValue.setWrapperByteArray(wrapperByteArray);
            sendValue.setWrapperDouble(new Double(2323.232D));
            sendValue.setWrapperFloat(new Float(23.023F));
            sendValue.setWrapperInteger(new Integer(2093));
            sendValue.setWrapperShort(new Short((short) 203));
            sendValue.setWrapperByte(new Byte((byte) 20));
            sendValue.setWrapperBoolean(new Boolean(true));
            sendValue.setShortArray(shortArray);
            sendValue.setByteArray(byteArray);
            date = Calendar.getInstance();
            date.setTimeZone(gmt);
            date.setTime(new Date(1012937861996L));
            sendValue.setCallableDate(date);
            sendValue.setBondAmount(new BigDecimal("2675.23"));
            sendValue.setPortfolioType(new BigInteger("2093"));
            sendValue.setTradeExchange("NYSE");
            sendValue.setFiftyTwoWeekHigh(45.012D);
            sendValue.setLastTradePrice(87895.32F);
            sendValue.setYield(5475L);
            sendValue.setStockBeta(32);
            sendValue.setDocType((short) 35);
            sendValue.setTaxIndicator((byte) 3);

            binding.methodBondInvestmentIn(sendValue);

        } catch (RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testBondInvestmentIn

    /**
     *  Test the overloaded method getId with a BondInvestment.
     */
    public void testBondInvestmentGetId() {

        try {

            CallOptions[] callOptions = new CallOptions[2];
            callOptions[0] = new CallOptions();
            Calendar date = Calendar.getInstance();
            TimeZone gmt = TimeZone.getTimeZone("GMT");
            date.setTimeZone(gmt);
            date.setTime(new Date(1013441507388L));
            callOptions[0].setCallDate(date);
            callOptions[1] = new CallOptions();
            date = Calendar.getInstance();
            date.setTimeZone(gmt);
            date.setTime(new Date(1013441507390L));
            callOptions[1].setCallDate(date);

            short[] shortArray = {(short) 30};
            byte[] byteArray = {(byte) 1};
            Short[] wrapperShortArray = {new Short((short) 23), new Short((short) 56)};
            Byte[] wrapperByteArray = {new Byte((byte) 2), new Byte((byte) 15)};

            BondInvestment sendValue = new BondInvestment();
            
            sendValue.setOptions(callOptions);
            sendValue.setOptions2(callOptions);
            sendValue.setOptions3(callOptions[0]);
            sendValue.setWrapperShortArray(wrapperShortArray);
            sendValue.setWrapperByteArray(wrapperByteArray);
            sendValue.setWrapperDouble(new Double(2323.232D));
            sendValue.setWrapperFloat(new Float(23.023F));
            sendValue.setWrapperInteger(new Integer(2093));
            sendValue.setWrapperShort(new Short((short) 203));
            sendValue.setWrapperByte(new Byte((byte) 20));
            sendValue.setWrapperBoolean(new Boolean(true));
            sendValue.setShortArray(shortArray);
            sendValue.setByteArray(byteArray);
            date = Calendar.getInstance();
            date.setTimeZone(gmt);
            date.setTime(new Date(1012937861996L));
            sendValue.setCallableDate(date);
            sendValue.setBondAmount(new BigDecimal("2675.23"));
            sendValue.setPortfolioType(new BigInteger("2093"));
            sendValue.setTradeExchange("NYSE");
            sendValue.setFiftyTwoWeekHigh(45.012D);
            sendValue.setLastTradePrice(87895.32F);
            sendValue.setYield(5475L);
            sendValue.setStockBeta(32);
            sendValue.setDocType((short) 35);
            sendValue.setTaxIndicator((byte) 3);
            sendValue.setId(-123);

            int id = binding.getId(sendValue);
            assertEquals("The wrong id was sent back", -123, id);

        } catch (RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testBondInvestmentGetId

    /**
     *  Test the overloaded method getId with a StockInvestment.
     */
    public void testInvestmentGetId() {

        try {
            StockInvestment stock = new StockInvestment();
            stock.setName("International Business Machines");
            stock.setId(1);
            stock.setTradeExchange("NYSE");
            stock.setLastTradePrice(200.55F);

            // Temporarily commented out until I can get this to work.
            int id = binding.getId(stock);
            assertEquals("The wrong id was sent back", 1, id);            
        } catch (RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testInvestmentGetId

    /**
     *  Test to insure that a multiple array sent by a remote method can be
     *  received and its values match the expected values.
     */
    public void testMethodStringMArrayOut() {

        try {
            String[][] expected = {{"Out-0-0"}, {"Out-1-0"}};
            String[][] actual = binding.methodStringMArrayOut();
            assertEquals("The expected and actual values did not match.",
                         expected[0][0],
                         actual[0][0]);
            assertEquals("The expected and actual values did not match.",
                         expected[1][0],
                         actual[1][0]);
        } catch (RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testMethodStringMArrayOut

    /**
     *  Test to insure that a multiple array can be sent to a remote method.  The
     *  server matches the received array against its expected values.
     */
    public void testMethodStringMArrayIn() {

        try {
            String[][] sendArray = {{"In-0-0", "In-0-1"}, {"In-1-0", "In-1-1"}};
            binding.methodStringMArrayIn(sendArray);
        } catch (RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testMethodStringMArrayIn

    /**
     *  Test to insure that a multiple array matches the expected values on both
     *  the client and server.
     */
    public void testMethodStringMArrayInOut() {

        try {
            String[][] sendArray = {{"Request-0-0", "Request-0-1"}, {"Request-1-0", "Request-1-1"}};
            String[][] expected = {{"Response-0-0", "Response-0-1"}, {"Response-1-0", "Response-1-1"}};
            String[][] actual = binding.methodStringMArrayInOut(sendArray);
            assertEquals("The expected and actual values did not match.",
                         expected[0][0],
                         actual[0][0]);
            assertEquals("The expected and actual values did not match.",
                         expected[0][1],
                         actual[0][1]);
            assertEquals("The expected and actual values did not match.",
                         expected[1][0],
                         actual[1][0]);
            assertEquals("The expected and actual values did not match.",
                         expected[1][1],
                         actual[1][1]);
        } catch (RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testMethodStringMArrayInOut

    /**
     *  Test to insure that an int array can be sent by a remote method and
     *  the received values match the expected values on the client.
     */
    public void testMethodIntArrayOut() {

        try {
            int[] expected = {3, 78, 102};
            int[] actual = binding.methodIntArrayOut();
            assertEquals("The expected and actual values did not match.",
                         expected[0],
                         actual[0]);
            assertEquals("The expected and actual values did not match.",
                         expected[1],
                         actual[1]);
            assertEquals("The expected and actual values did not match.",
                         expected[2],
                         actual[2]);
        } catch (RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testMethodIntArrayOut

    /**
     *  Test to insure that an int array can be sent to a remote method.  The server
     *  checks the received array against its expected values.
     */
    public void testMethodIntArrayIn() {

        try {
            int[] sendValue = {91, 54, 47, 10};
            binding.methodIntArrayIn(sendValue);
        } catch (RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testMethodIntArrayIn

    /**
     *  Test to insure that an int array can roundtrip between the client
     *  and server.  The actual and expected values are compared on both
     *  the client and server.
     */
    public void testMethodIntArrayInOut() {

        try {
            int[] sendValue = {90, 34, 45, 239, 45, 10};
            int[] expected = {12, 39, 50, 60, 28, 39};
            int[] actual = binding.methodIntArrayInOut(sendValue);
            assertEquals("The expected and actual values did not match.",
                         expected[0],
                         actual[0]);
            assertEquals("The expected and actual values did not match.",
                         expected[1],
                         actual[1]);
            assertEquals("The expected and actual values did not match.",
                         expected[2],
                         actual[2]);
            assertEquals("The expected and actual values did not match.",
                         expected[3],
                         actual[3]);
            assertEquals("The expected and actual values did not match.",
                         expected[4],
                         actual[4]);
            assertEquals("The expected and actual values did not match.",
                         expected[5],
                         actual[5]);
        } catch (RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testMethodIntArrayInOut

    /**
     *  Test to insure that all the XML -> Java types can be sent to a remote 
     *  method.  The server checks for the expected values.
     */
    public void testMethodAllTypesIn() {

        try {
            byte[] sendByteArray = {(byte) 5, (byte) 10, (byte) 12};
            Byte[] sendWrapperByteArray = {new Byte((byte) 9), new Byte((byte) 7)};
            Calendar dateTime = Calendar.getInstance();
            TimeZone gmt = TimeZone.getTimeZone("GMT");
            dateTime.setTimeZone(gmt);
            dateTime.setTime(new Date(1012937861986L));
            binding.methodAllTypesIn(new String("Request methodAllTypesIn"),
                                     new BigInteger("545"),
                                     new BigDecimal("546.545"),
                                     dateTime,
                                     true,
                                     (byte) 2,
                                     (short) 14,
                                     234,
                                     10900L,
                                     23098.23F,
                                     2098098.01D,
                                     sendByteArray,
                                     new Boolean(false),
                                     new Byte((byte) 11),
                                     new Short((short) 45),
                                     new Integer(101),
                                     new Long(232309L),
                                     new Float(67634.12F),
                                     new Double(892387.232D),
                                     sendWrapperByteArray);
        } catch (RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testMethodAllTypesIn

    /**
     *  Test to insure that a primitive byte array matches the expected values on
     *  both the client and server.
     */
    public void testMethodByteArray() {

        try {
            byte[] expected = {(byte) 5, (byte) 4};
            byte[] sendByte = {(byte) 3, (byte) 9};
            byte[] actual = binding.methodByteArray(sendByte);
            assertEquals("The expected and actual values did not match.",
                         expected[0],
                         actual[0]);
            assertEquals("The expected and actual values did not match.",
                         expected[1],
                         actual[1]);
        } catch (RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testMethodByteArray

    /**
     *  Test to insure that a Calendar object matches the expected values
     *  on both the client and server.
     */
    public void testMethodDateTime() {

        try {
            Calendar expected = Calendar.getInstance();
            TimeZone gmt = TimeZone.getTimeZone("GMT");
            expected.setTimeZone(gmt);
            expected.setTime(new Date(1012937861800L));
            Calendar parameter = Calendar.getInstance();
            parameter.setTimeZone(gmt);
            parameter.setTime(new Date(1012937861996L));
            Calendar actual = binding.methodDateTime(parameter);
            assertEquals("The expected and actual values did not match.",
                         expected,
                         actual);
        } catch (RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testMethodDateTime

    /**
     *  Test to insure that a BigDecimal matches the expected values on 
     *  both the client and server.
     */
    public void testMethodBigDecimal() {

        try {
            BigDecimal expected = new BigDecimal("903483.304");
            BigDecimal actual = binding.methodBigDecimal(new BigDecimal("3434.456"));
            assertEquals("The expected and actual values did not match.",
                         expected,
                         actual);
        } catch (RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testMethodBigDecimal

    /**
     *  Test to insure that a BigInteger matches the expected values on 
     *  both the client and server.
     */
    public void testMethodBigInteger() {

        try {
            BigInteger expected = new BigInteger("2323");
            BigInteger actual = binding.methodBigInteger(new BigInteger("8789"));
            assertEquals("The expected and actual values did not match.",
                         expected,
                         actual);
        } catch (RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testMethodBigInteger

    /**
     *  Test to insure that a String matches the expected values on
     *  both the client and server.
     */
    public void testMethodString() {

        try {
            String expected = "Response";
            String actual = binding.methodString(new String("Request"));
            assertEquals("The expected and actual values did not match.",
                         expected,
                         actual);
        } catch (RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testMethodString

    /**
     *  Test to insure that a primitive double matches the expected
     *  values on both the client and server.
     */
    public void testMethodDouble() {

        try {
            double expected = 567.547D;
            double actual = binding.methodDouble(87502.002D);
            assertEquals("The expected and actual values did not match.",
                         expected,
                         actual,
                         DOUBLE_DELTA);
        } catch (RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testMethodDouble

    /**
     *  Test to insure that a primitive float matches the expected
     *  values on both the client and server.
     */
    public void testMethodFloat() {

        try {
            float expected = 12325.545F;
            float actual = binding.methodFloat(8787.25F);
            assertEquals("The expected and actual values did not match.",
                         expected,
                         actual,
                         FLOAT_DELTA);
        } catch (RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testMethodFloat

    /**
     *  Test to insure that a primitive long matches the expected
     *  values on both the client and server.
     */
    public void testMethodLong() {

        try {
            long expected = 787985L;
            long actual = binding.methodLong(45425L);
            assertEquals("The expected and actual values did not match.",
                         expected,
                         actual);
        } catch (RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testMethodLong

    /**
     *  Test to insure that a primitive int matches the expected
     *  values on both the client and server.
     */
    public void testMethodInt() {

        try {
            int expected = 10232;
            int actual = binding.methodInt(1215);
            assertEquals("The expected and actual values did not match.",
                         expected,
                         actual);
        } catch (RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testMethodInt

    /**
     *  Test to insure that a primitive short matches the expected
     *  values on both the client and server.
     */
    public void testMethodShort() {

        try {
            short expected = (short) 124;
            short actual = binding.methodShort((short) 302);
            assertEquals("The expected and actual values did not match.",
                         expected,
                         actual);
        } catch (RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testMethodShort

    /**
     *  Test to insure that a primitive byte matches the expected
     *  values on both the client and server.
     */
    public void testMethodByte() {

        try {
            byte expected = (byte) 35;
            byte actual = binding.methodByte((byte) 61);
            assertEquals("The expected and actual values did not match.",
                         expected,
                         actual);
        } catch (RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testMethodByte

    /**
     *  Test to insure that a primitive boolean matches the expected
     *  values on both the client and server.
     */
    public void testMethodBoolean() {

        try {
            boolean expected = false;
            boolean actual = binding.methodBoolean(true);
            assertEquals("The expected and actual values did not match.",
                         expected,
                         actual);
        } catch (RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testMethodBoolean


    /**
     *  Test to insure that an array of a  user defined class matches
     *  the expected values on both the client and server.
     */
    public void testMethodCallOptions() {

        try {
            CallOptions[] callOptions = new CallOptions[1];
            callOptions[0] = new CallOptions();
            Calendar cal = Calendar.getInstance();
            TimeZone gmt = TimeZone.getTimeZone("GMT");
            cal.setTimeZone(gmt);
            cal.setTime(new Date(1013459984577L));
            callOptions[0].setCallDate(cal);

            CallOptions[] actual = binding.methodCallOptions(callOptions);
            cal.setTime(new Date(1013459984507L));
            assertEquals("The expected and actual values did not match.",
                         cal,
                         actual[0].getCallDate());
        } catch(RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testMethodCallOptions

    /**
     *  Test to insure that a wrapper Float object matches
     *  the expected values on both the client and server.
     */
    public void testMethodSoapFloat() {

        try {
            Float actual = binding.methodSoapFloat(new Float(23423.234F));
            assertEquals("The expected and actual values did not match.",
                         new Float(232.23F),
                         actual);
        } catch(RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testMethodSoapFloat

    /**
     *  Test to insure that a wrapper Double object matches
     *  the expected values on both the client and server.
     */
    public void testMethodSoapDouble() {

        try {
            Double actual = binding.methodSoapDouble(new Double(123423.234D));
            assertEquals("The expected and actual values did not match.",
                         new Double(2232.23D),
                         actual);
        } catch(RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testMethodSoapDouble

    /**
     *  Test to insure that a wrapper Boolean object matches
     *  the expected values on both the client and server.
     */
    public void testMethodSoapBoolean() {

        try {
            Boolean actual = binding.methodSoapBoolean(new Boolean(true));
            assertEquals("The expected and actual values did not match.",
                         new Boolean(false),
                         actual);
        } catch(RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testMethodSoapBoolean

    /**
     *  Test to insure that a wrapper Byte object matches
     *  the expected values on both the client and server.
     */
    public void testMethodSoapByte() {

        try {
            Byte actual = binding.methodSoapByte(new Byte((byte) 9));
            assertEquals("The expected and actual values did not match.",
                         new Byte((byte) 10),
                         actual);
        } catch(RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testMethodSoapByte

    /**
     *  Test to insure that a wrapper Short object matches
     *  the expected values on both the client and server.
     */
    public void testMethodSoapShort() {

        try {
            Short actual = binding.methodSoapShort(new Short((short) 32));
            assertEquals("The expected and actual values did not match.",
                         new Short((short) 44),
                         actual);
        } catch(RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testMethodSoapShort

    /**
     *  Test to insure that a wrapper Integer object matches
     *  the expected values on both the client and server.
     */
    public void testMethodSoapInt() {

        try {
            Integer actual = binding.methodSoapInt(new Integer(332));
            assertEquals("The expected and actual values did not match.",
                         new Integer(441),
                         actual);
        } catch(RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testMethodSoapInt

    /**
     *  Test to insure that a wrapper Long object matches
     *  the expected values on both the client and server.
     */
    public void testMethodSoapLong() {

        try {
            Long actual = binding.methodSoapLong(new Long(3321L));
            assertEquals("The expected and actual values did not match.",
                         new Long(4412L),
                         actual);
        } catch(RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testMethodSoapLong

    /**
     *  Test to insure that a user defined exception can be
     *  thrown and received.
     */
    public void testInvalidTickerSymbol() {

        try {
            binding.throwInvalidTickerException();
            fail("Should have received an InvalidTickerSymbol exception.");
        } catch (InvalidTickerSymbol its) {
            // Test was successful
        } catch(RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testInvalidTickerSymbol

    /**
     *  Test to insure that more than one user defined exception can be
     *  defined in a method.
     */
    public void testInvalidTradeExchange() {

        try {
            binding.throwInvalidTradeExchange();
            fail("Should have received an InvalidTradeExchange exception.");
        } catch (InvalidTradeExchange ite) {
            // Test was successful
        } catch (InvalidTickerSymbol its) {
            fail("Should have received an InvalidTradeExchange exception.");
        } catch (InvalidCompanyId ici) {
            fail("Should have received an InvalidTradeExchange exception.");
        } catch (RemoteException re) {
            fail("Remote Exception caught: " + re);
        }

    } // testInvalidTradeExchange

} // End class RoundtripTestServiceTestCase


