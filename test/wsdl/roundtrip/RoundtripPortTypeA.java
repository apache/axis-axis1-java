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



/**
 * Defines methods for RoundtripPortType
 *
 * @author    Rich Scheuerle
 */
public interface RoundtripPortTypeA {
    public java.lang.String methodString(java.lang.String inString)
        throws java.rmi.RemoteException;
    public java.math.BigInteger methodBigInteger(java.math.BigInteger inInteger)
        throws java.rmi.RemoteException;
    public java.math.BigDecimal methodBigDecimal(java.math.BigDecimal inDecimal)
        throws java.rmi.RemoteException;
    public java.util.Calendar methodDateTime(java.util.Calendar inDateTime)
        throws java.rmi.RemoteException;
    public java.util.Date methodDate(java.util.Date inDateTime)
        throws java.rmi.RemoteException;
    public byte[] methodByteArray(byte[] inByteArray)
        throws java.rmi.RemoteException;
    public void methodAllTypesIn(java.lang.String string,
                                 java.math.BigInteger integer,
                                 java.math.BigDecimal decimal,
                                 java.util.Calendar dateTime,
                                 java.util.Date date,
                                 boolean _boolean,
                                 byte _byte,
                                 short _short,
                                 int _int,
                                 long _long,
                                 float _float,
                                 double _double,
                                 byte[] base64Binary,
                                 java.lang.Boolean soapBoolean,
                                 java.lang.Byte soapByte,
                                 java.lang.Short soapShort,
                                 java.lang.Integer soapInt,
                                 java.lang.Long soapLong,
                                 java.lang.Float soapFloat,
                                 java.lang.Double soapDouble,
                                 java.lang.Byte[] soapBase64)
        throws java.rmi.RemoteException;
    public int[] methodIntArrayInOut(int[] inIntArray)
        throws java.rmi.RemoteException;
    public void methodIntArrayIn(int[] inIntArray)
        throws java.rmi.RemoteException;
    public int[] methodIntArrayOut()
        throws java.rmi.RemoteException;
    public String[][] methodStringMArrayInOut(String[][] inStringArray)
        throws java.rmi.RemoteException;
    public void methodStringMArrayIn(String[][] inStringArray)
        throws java.rmi.RemoteException;
    public String[][] methodStringMArrayOut()
        throws java.rmi.RemoteException;
    public void methodBondInvestmentIn(BondInvestment bondInvestment)
        throws java.rmi.RemoteException;
    public BondInvestment methodBondInvestmentOut()
        throws java.rmi.RemoteException;
    public BondInvestment methodBondInvestmentInOut(BondInvestment bondInvestment)
        throws java.rmi.RemoteException;
    public float getRealtimeLastTradePrice(StockInvestment stockInvestment)
        throws java.rmi.RemoteException;
    public PreferredStockInvestment getDividends(PreferredStockInvestment preferredStock)
        throws java.rmi.RemoteException;
    public CallOptions[] methodCallOptions(CallOptions[] callOptions)
        throws java.rmi.RemoteException;
} // RoundtripPortType

