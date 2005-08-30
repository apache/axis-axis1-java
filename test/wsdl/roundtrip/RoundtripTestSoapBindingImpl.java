/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package test.wsdl.roundtrip;

import test.wsdl.roundtrip.holders.BondInvestmentHolder;

import javax.xml.rpc.holders.StringHolder;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * This class contains the implementations of the methods defined in the
 * RoundtripPortType interface.  Most of the methods compare the actual
 * values received from the client against some expected values. 
 *
 * @version   1.00  06 Feb 2002
 * @author    Brent Ulbricht
 */
public class RoundtripTestSoapBindingImpl implements RoundtripPortType {

    public float getRealtimeLastTradePrice(StockInvestment in0) throws RemoteException {

        if ((in0.getLastTradePrice() == 200.55F) &&
              (in0.getTradeExchange().equals("NYSE")) &&
              (in0.getName().equals("International Business Machines")) &&
              (in0.getId() == 1)) {
            return 201.25F;
        } else {
            throw new RemoteException("Actual Value Did Not Match Expected Value.");
        }

    } // getRealtimeLastTradePrice

    public PreferredStockInvestment getDividends(PreferredStockInvestment in0) throws RemoteException {

        if ((in0.getLastTradePrice() == 10.50F) &&
              (in0.getTradeExchange().equals("NASDAQ")) &&
              (in0.getName().equals("SOAP Inc.")) &&
              (in0.getId() == 202) &&
              (in0.getDividendsInArrears() == 100.44D) &&
              (in0.getPreferredYield().equals(new BigDecimal("7.00")))) {
            in0.setName("AXIS Inc.");
            in0.setId(203);
            in0.setTradeExchange("NASDAQ");
            in0.setLastTradePrice(11.50F);
            in0.setDividendsInArrears(101.44D);
            in0.setPreferredYield(new BigDecimal("8.00"));
            return in0;
        } else {
            throw new RemoteException("Actual Value Did Not Match Expected Value.");
        }

    } // getDividend

    public BondInvestment methodBondInvestmentInOut(BondInvestment in0) throws RemoteException {

        short[] shortArray = {(short) 36};
        byte[] byteArray = {(byte) 7};
        CallOptions[] callOptions = new CallOptions[2];
        callOptions[0] = new CallOptions();
        Calendar date = Calendar.getInstance();
        TimeZone gmt = TimeZone.getTimeZone("GMT");
        date.setTimeZone(gmt);
        date.setTime(new Date(1013441507308L));
        callOptions[0].setCallDate(date);
        callOptions[1] = new CallOptions();
        date = Calendar.getInstance();
        date.setTimeZone(gmt);
        date.setTime(new Date(1013441507328L));
        callOptions[1].setCallDate(date);
        HashMap map = new HashMap();
        map.put("Test", "Test Works");

        Short[] wrapperShortArray = {new Short((short) 33), new Short((short) 86)};
        Byte[] wrapperByteArray = {new Byte((byte) 4), new Byte((byte) 18)};

        BondInvestment sendValue = new BondInvestment();

        sendValue.setMap(map);
        sendValue.setOptions(callOptions);
        sendValue.setOptions2(callOptions);
        sendValue.setOptions3(callOptions[0]);
        sendValue.setWrapperShortArray(wrapperShortArray);
        sendValue.setWrapperByteArray(wrapperByteArray);
        sendValue.setWrapperDouble(new Double(33.232D));
        sendValue.setWrapperFloat(new Float(2.23F));
        sendValue.setWrapperInteger(new Integer(3));
        sendValue.setWrapperShort(new Short((short) 2));
        sendValue.setWrapperByte(new Byte((byte) 21));
        sendValue.setWrapperBoolean(new Boolean(false));
        sendValue.setShortArray(shortArray);
        sendValue.setByteArray(byteArray);
        date = Calendar.getInstance();
        date.setTimeZone(gmt);
        date.setTime(new Date(1012937862997L));
        sendValue.setCallableDate(date);
        sendValue.setBondAmount(new BigDecimal("2735.23"));
        sendValue.setPortfolioType(new BigInteger("21093"));
        sendValue.setTradeExchange("AMEX");
        sendValue.setFiftyTwoWeekHigh(415.012D);
        sendValue.setLastTradePrice(8795.32F);
        sendValue.setYield(575L);
        sendValue.setStockBeta(3);
        sendValue.setDocType((short) 45);
        sendValue.setTaxIndicator((byte) 8);
        
        if ((in0.getStockBeta() == 32) &&
            (in0.getDocType() == (short) 35) &&
            (in0.getTaxIndicator() == (byte) 3)) 
            ;
        else
            throw new RemoteException("Actual attribute values did not match expected values.");

        Calendar expectedDate0 = Calendar.getInstance();
        expectedDate0.setTimeZone(gmt);
        expectedDate0.setTime(new Date(1013441507388L));
        Calendar expectedDate1 = Calendar.getInstance();
        expectedDate1.setTimeZone(gmt);
        expectedDate1.setTime(new Date(1013441507390L));
        Calendar expectedDate2 = Calendar.getInstance();
        expectedDate2.setTimeZone(gmt);
        expectedDate2.setTime(new Date(1013441507388L));
        Calendar expectedDate3 = Calendar.getInstance();
        expectedDate3.setTimeZone(gmt);
        expectedDate3.setTime(new Date(1013441507390L));
        Calendar expectedDate4 = Calendar.getInstance();
        expectedDate4.setTimeZone(gmt);
        expectedDate4.setTime(new Date(1012937861996L));
        if ((in0.getMap().get("Test").equals("Test Works")) &&
            (in0.getOptions()[0].getCallDate().equals(expectedDate0)) &&
            (in0.getOptions()[1].getCallDate().equals(expectedDate1)) &&
            (((CallOptions[])in0.getOptions2())[0].getCallDate().equals(expectedDate2)) &&
            (((CallOptions[])in0.getOptions2())[1].getCallDate().equals(expectedDate3)) &&
            (in0.getWrapperShortArray()[0].equals(new Short((short) 23))) &&
            (in0.getWrapperShortArray()[1].equals(new Short((short) 56))) &&
            (in0.getWrapperByteArray()[0].equals(new Byte((byte) 2))) &&
            (in0.getWrapperByteArray()[1].equals(new Byte((byte) 15))) &&
            (in0.getWrapperDouble().equals(new Double(2323.232D))) &&
            (in0.getWrapperFloat().equals(new Float(23.023F))) &&
            (in0.getWrapperInteger().equals(new Integer(2093))) &&
            (in0.getWrapperShort().equals(new Short((short) 203))) &&
            (in0.getWrapperByte().equals(new Byte((byte) 20))) &&
            (in0.getWrapperBoolean().equals(new Boolean(true))) &&
            (in0.getShortArray()[0] == (short) 30) &&
            (in0.getByteArray()[0] == (byte) 1) &&
            (in0.getCallableDate().equals(expectedDate4)) &&
            (in0.getBondAmount().equals(new BigDecimal("2675.23"))) &&
            (in0.getPortfolioType().equals(new BigInteger("2093"))) &&
            (in0.getTradeExchange().equals("NYSE")) &&
            (in0.getFiftyTwoWeekHigh() ==  45.012D) &&
            (in0.getLastTradePrice() == 87895.32F) &&
            (in0.getYield() == 5475L) && 
            (in0.getStockBeta() == 32) &&
            (in0.getDocType() == (short) 35) &&
            (in0.getTaxIndicator() == (byte) 3)) {
            return sendValue;
        } else {
            throw new RemoteException("Actual values did not match expected values.");
        }

    } // methodBondInvestmentInOut

    public BondInvestment methodBondInvestmentOut() throws RemoteException {

        short[] shortArray = {(short) 36};
        byte[] byteArray = {(byte) 7};
        CallOptions[] callOptions = new CallOptions[2];
        callOptions[0] = new CallOptions();
        Calendar date = Calendar.getInstance();
        TimeZone gmt = TimeZone.getTimeZone("GMT");
        date.setTimeZone(gmt);
        date.setTime(new Date(1013441507308L));
        callOptions[0].setCallDate(date);
        callOptions[1] = new CallOptions();
        date = Calendar.getInstance();
        date.setTimeZone(gmt);
        date.setTime(new Date(1013441507328L));
        callOptions[1].setCallDate(date);
        Short[] wrapperShortArray = {new Short((short) 33), new Short((short) 86)};
        Byte[] wrapperByteArray = {new Byte((byte) 4), new Byte((byte) 18)};
        HashMap map = new HashMap();
        map.put("Test", "Test Works");


        BondInvestment sendValue = new BondInvestment();
        
        sendValue.setMap(map);
        sendValue.setOptions(callOptions);
        sendValue.setOptions2(callOptions);
        sendValue.setOptions3(callOptions[0]);
        sendValue.setWrapperShortArray(wrapperShortArray);
        sendValue.setWrapperByteArray(wrapperByteArray);
        sendValue.setWrapperDouble(new Double(33.232D));
        sendValue.setWrapperFloat(new Float(2.23F));
        sendValue.setWrapperInteger(new Integer(3));
        sendValue.setWrapperShort(new Short((short) 2));
        sendValue.setWrapperByte(new Byte((byte) 21));
        sendValue.setWrapperBoolean(new Boolean(false));
        sendValue.setShortArray(shortArray);
        sendValue.setByteArray(byteArray);
        date = Calendar.getInstance();
        date.setTimeZone(gmt);
        date.setTime(new Date(1012937862997L));
        sendValue.setCallableDate(date);
        sendValue.setBondAmount(new BigDecimal("2735.23"));
        sendValue.setPortfolioType(new BigInteger("21093"));
        sendValue.setTradeExchange("AMEX");
        sendValue.setFiftyTwoWeekHigh(415.012D);
        sendValue.setLastTradePrice(8795.32F);
        sendValue.setYield(575L);
        sendValue.setStockBeta(3);
        sendValue.setDocType((short) 45);
        sendValue.setTaxIndicator((byte) 8);

        return sendValue;

    } // methodBondInvestmentOut

    public void methodBondInvestmentIn(BondInvestment in0) throws RemoteException {
        Calendar expectedDate0 = Calendar.getInstance();
        TimeZone gmt = TimeZone.getTimeZone("GMT");
        expectedDate0.setTimeZone(gmt);
        expectedDate0.setTime(new Date(1013441507388L));
        Calendar expectedDate1 = Calendar.getInstance();
        expectedDate1.setTimeZone(gmt);
        expectedDate1.setTime(new Date(1013441507390L));
        Calendar expectedDate2 = Calendar.getInstance();
        expectedDate2.setTimeZone(gmt);
        expectedDate2.setTime(new Date(1013441507388L));
        Calendar expectedDate3 = Calendar.getInstance();
        expectedDate3.setTimeZone(gmt);
        expectedDate3.setTime(new Date(1013441507390L));
        Calendar expectedDate4 = Calendar.getInstance();
        expectedDate4.setTimeZone(gmt);
        expectedDate4.setTime(new Date(1012937861996L));
        if (!((in0.getMap().get("Test").equals("Test Works")) &&
              (in0.getOptions()[0].getCallDate().equals(expectedDate0)) &&
              (in0.getOptions()[1].getCallDate().equals(expectedDate1)) &&
              (((CallOptions[])in0.getOptions2())[0].getCallDate().equals(expectedDate2)) &&
              (((CallOptions[])in0.getOptions2())[1].getCallDate().equals(expectedDate3)) &&
              (in0.getWrapperShortArray()[0].equals(new Short((short) 23))) &&
              (in0.getWrapperShortArray()[1].equals(new Short((short) 56))) &&
              (in0.getWrapperByteArray()[0].equals(new Byte((byte) 2))) &&
              (in0.getWrapperByteArray()[1].equals(new Byte((byte) 15))) &&
              (in0.getWrapperDouble().equals(new Double(2323.232D))) &&
              (in0.getWrapperFloat().equals(new Float(23.023F))) &&
              (in0.getWrapperInteger().equals(new Integer(2093))) &&
              (in0.getWrapperShort().equals(new Short((short) 203))) &&
              (in0.getWrapperByte().equals(new Byte((byte) 20))) &&
              (in0.getWrapperBoolean().equals(new Boolean(true))) &&
              (in0.getShortArray()[0] == (short) 30) &&
              (in0.getByteArray()[0] == (byte) 1) &&
              (in0.getCallableDate().equals(expectedDate4)) &&
              (in0.getBondAmount().equals(new BigDecimal("2675.23"))) &&
              (in0.getPortfolioType().equals(new BigInteger("2093"))) &&
              (in0.getTradeExchange().equals("NYSE")) &&
              (in0.getFiftyTwoWeekHigh() ==  45.012D) &&
              (in0.getLastTradePrice() == 87895.32F) &&
              (in0.getYield() == 5475L) && 
              (in0.getStockBeta() == 32) &&
              (in0.getDocType() == (short) 35) &&
              (in0.getTaxIndicator() == (byte) 3))) {
            throw new RemoteException("Actual values did not match expected values.");
        }

    } // methodBondInvestmentIn

    public String[][] methodStringMArrayOut() throws RemoteException {

        String[][] sendArray = { {"Out-0-0"}, {"Out-1-0"}};
        return sendArray;

    } // methodStringMArrayOut

    public void methodStringMArrayIn(String[][] in0) throws RemoteException {

        if (!((in0[0][0].equals("In-0-0")) &&
              (in0[0][1].equals("In-0-1")) &&
              (in0[1][0].equals("In-1-0")) &&
              (in0[1][1].equals("In-1-1")))) {
            throw new RemoteException("The actual values did not match expected values.");
        }

    } // methodStringMArrayIn

    public String[][] methodStringMArrayInOut(String[][] in0) throws RemoteException {

        String[][] sendArray = { {"Response-0-0", "Response-0-1"}, {"Response-1-0", "Response-1-1"}};

        if ((in0[0][0].equals("Request-0-0")) &&
            (in0[0][1].equals("Request-0-1")) &&
            (in0[1][0].equals("Request-1-0")) &&
            (in0[1][1].equals("Request-1-1"))) {
            return sendArray;
        } else {
            throw new RemoteException("The actual values did not match expected values.");
        }

    } // methodStringMArrayInOut

    public int[] methodIntArrayOut() throws RemoteException {

        int[] returnByteArray = {3, 78, 102};
        return returnByteArray;

    } // methodIntArrayOut

    public void methodIntArrayIn(int[] in0) throws RemoteException {

        if (!((in0[0] == 91) &&
              (in0[1] == 54) &&
              (in0[2] == 47) &&
              (in0[3] == 10))) {
            throw new RemoteException("The actual values did not match expected values.");
        }

    } // methodIntArrayIn

    public int[] methodIntArrayInOut(int[] in0) throws RemoteException {

        int[] returnByteArray = {12, 39, 50, 60, 28, 39};

        if ((in0[0] == 90) &&
              (in0[1] == 34) &&
              (in0[2] == 45) &&
              (in0[3] == 239) &&
              (in0[4] == 45) &&
              (in0[5] == 10)) {
            return returnByteArray;
        } else {
            throw new RemoteException("The actual values did not match expected values.");
        }

    } // methodIntArrayIn

    public void methodAllTypesIn(String in0,
                                 BigInteger in1,
                                 BigDecimal in2,
                                 Calendar in35,
                                 Calendar in36,
                                 boolean in4,
                                 byte in5,
                                 short in6,
                                 int in7,
                                 long in8,
                                 float in9,
                                 double in10,
                                 byte[] in11,
                                 Boolean in13,
                                 Byte in14,
                                 Short in15, 
                                 Integer in16,
                                 Long in17,
                                 Float in18,
                                 Double in19,
                                 Byte[] in12) throws RemoteException {
        Calendar expectedDateTime = Calendar.getInstance();
        TimeZone gmt = TimeZone.getTimeZone("GMT");
        expectedDateTime.setTimeZone(gmt);
        expectedDateTime.setTime(new Date(1012937861986L));
        if (!((in0.equals(new String("Request methodAllTypesIn"))) &&
              (in1.equals(new BigInteger("545"))) &&
              (in2.equals(new BigDecimal("546.545"))) &&
              (in35.equals(expectedDateTime)) &&
              (in13.equals(new Boolean(false))) &&
              (in14.equals(new Byte((byte) 11))) &&
              (in15.equals(new Short((short) 45))) &&
              (in16.equals(new Integer(101))) &&
              (in17.equals(new Long(232309L))) &&
              (in18.equals(new Float(67634.12F))) &&
              (in19.equals(new Double(892387.232D))) &&
              (in4) &&
              (in5 == (byte) 2) &&
              (in6 == (short) 14) &&
              (in7 == 234) &&
              (in8 == 10900L) &&
              (in9 == 23098.23F) &&
              (in10 == 2098098.01D) &&
              (in11[0] == (byte) 5) &&
              (in11[1] == (byte) 10) &&
              (in11[2] == (byte) 12) &&
              (in12[0].equals(new Byte((byte) 9))) &&
              (in12[1].equals(new Byte((byte) 7))))) {
            throw new RemoteException("Expected values did not match actuals.");
        }

    } // methodAllTypesIn

    public byte[] methodByteArray(byte[] in0) throws RemoteException {

        byte[] returnByte = {(byte) 5, (byte) 4};

        if ((in0[0] == (byte) 3) && (in0[1] == (byte) 9)) {
            return returnByte;    
        } else {
            throw new RemoteException("Expecting a byte array with 3 and 9.");
        }

    } // methodByteArray

    public Calendar methodDateTime(Calendar in0) throws RemoteException {

        Calendar expectedDateTime = Calendar.getInstance();
        TimeZone gmt = TimeZone.getTimeZone("GMT");
        expectedDateTime.setTimeZone(gmt);
        expectedDateTime.setTime(new Date(1012937861996L));

        if (in0.equals(expectedDateTime)) {
            Calendar dateTime = Calendar.getInstance();
            dateTime.setTimeZone(gmt);
            dateTime.setTime(new Date(1012937861800L));
            return dateTime;
        } else {
            throw new RemoteException("Expecting a Date value of " + expectedDateTime + ".");
        }

    } // methodDateTime

    public Calendar methodDate(Calendar in0) throws RemoteException {
        return methodDateTime(in0);
    } // methodDate

    public BigDecimal methodBigDecimal(BigDecimal in0) throws RemoteException {

        if (in0.equals(new BigDecimal("3434.456"))) {
            return new BigDecimal("903483.304");
        } else {
            throw new RemoteException("Expecting a BigDecimal value of 3434.456.");
        }

    } // methodBigDecimal

    public BigInteger methodBigInteger(BigInteger in0) throws RemoteException {

        if (in0.equals(new BigInteger("8789"))) {
            return new BigInteger("2323");
        } else {
            throw new RemoteException("Expecting a BigInteger value of 8789.");
        }

    } // methodBigInteger

    public String methodString(String in0) throws RemoteException {

        if (in0.equals("Request")) {
            return "Response";
        } else {
            throw new RemoteException("Expecting a string value of \"Request\"");
        }

    } // methodString

    public double methodDouble(double in0) throws RemoteException {

        if (in0 == 87502.002D) {
            return 567.547D;
        } else {
            throw new RemoteException("Expecting a double value of 87502.002D"); 
        }

    } // methodDouble

    public float methodFloat(float in0) throws RemoteException {

        if (in0 == 8787.25F) {
            return 12325.545F;
        } else {
            throw new RemoteException("Expecting a float value of 8787.25F");
        }

    } // methodFloat

    public long methodLong(long in0) throws RemoteException {

        if (in0 == 45425L) {
            return 787985L;
        } else {
            throw new RemoteException("Expecting a long value of 45425L.");
        }

    } // methodLong

    public int methodInt(int in0) throws RemoteException {

        if (in0 == 1215) {
            return 10232;
        } else {
            throw new RemoteException("Expecting an int value of 1215.");
        }

    } // methodInt

    public short methodShort(short in0) throws RemoteException {

        if (in0 == (short) 302) {
            return(short) 124;
        } else {
            throw new RemoteException("Expecting a short value of 302.");
        }

    } // methodShort

    public byte methodByte(byte in0) throws RemoteException {

        if (in0 == (byte) 61) {
            return(byte) 35;
        } else {
            throw new RemoteException("Expecting a byte value of 61.");
        }

    } // methodByte

    public boolean methodBoolean(boolean in0) throws RemoteException {

        if (in0) {
            return false;
        } else {
            throw new RemoteException("Expecting a boolean value of true.");
        } 

    } // methodBoolean

    public CallOptions[] methodCallOptions(CallOptions[] in0) throws RemoteException {
        Calendar dateTime = Calendar.getInstance();
        TimeZone gmt = TimeZone.getTimeZone("GMT");
        dateTime.setTimeZone(gmt);
        dateTime.setTime(new Date(1013459984577L));
        if (in0[0].getCallDate().equals(dateTime)) {
            in0[0] = new CallOptions();
            dateTime.setTime(new Date(1013459984507L));
            in0[0].setCallDate(dateTime);
            return in0;
        } else {
            throw new RemoteException("Actual value did not match expected value.");
        }

    } // methodCallOptions

    public Float methodSoapFloat(Float in0) throws RemoteException {

        if (in0.equals(new Float(23423.234F))) {
            return new Float(232.23F);
        } else {
            throw new RemoteException("Expecting a float value of 23423.234F");
        }

    } // methodSoapFloat
    
    public Double methodSoapDouble(Double in0) throws RemoteException {

        if (in0.equals(new Double(123423.234D))) {
            return new Double(2232.23D);
        } else {
            throw new RemoteException("Expecting a float value of 123423.234D");
        }

    } // methodSoapDouble

    public Boolean methodSoapBoolean(Boolean in0) throws RemoteException {

        if (in0.equals(new Boolean(true))) {
            return new Boolean(false);
        } else {
            throw new RemoteException("Expecting a boolean value of true");
        }

    } // methodSoapBoolean

    public Byte methodSoapByte(Byte in0) throws RemoteException {

        if (in0.equals(new Byte((byte) 9))) {
            return new Byte((byte) 10);
        } else {
            throw new RemoteException("Expecting a byte value of 9");
        }

    } // methodSoapByte

    public Short methodSoapShort(Short in0) throws RemoteException {

        if (in0.equals(new Short((short) 32))) {
            return new Short((short) 44);
        } else {
            throw new RemoteException("Expecting a short value of 32");
        }

    } // methodSoapShort

    public Integer methodSoapInt(Integer in0) throws RemoteException {

        if (in0.equals(new Integer(332))) {
            return new Integer(441);
        } else {
            throw new RemoteException("Expecting a short value of 332");
        }

    } // methodSoapInt

    public Long methodSoapLong(Long in0) throws RemoteException {

        if (in0.equals(new Long(3321L))) {
            return new Long(4412L);
        } else {
            throw new RemoteException("Expecting a short value of 3321L");
        }

    } // methodSoapLong

    public void throwInvalidTickerException() 
        throws InvalidTickerSymbol, RemoteException {

        throw new InvalidTickerSymbol("ABC");

    } // throwInvalidTickerSymbol

    public void throwInvalidTradeExchange()
          throws InvalidTickerSymbol,
                 InvalidTradeExchange,
                 InvalidCompanyId,
                 RemoteException {

        throw new InvalidTradeExchange("XYZ");

    } // throwInvalidTradeExchange

    public int getId(BondInvestment investment) 
        throws java.rmi.RemoteException {
        return investment.getId();
    }
    public int getId(Investment investment) 
        throws java.rmi.RemoteException {
        return investment.getId();
    }

    // This is a compile-time test, so we don't need any runtime test code.
    public void holderTest(StringHolder sh, BondInvestmentHolder bih) {
    }

} // End class RoundtripTypesTestSoapBindingImpl
