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

import test.wsdl.roundtrip.Investment;
import test.wsdl.roundtrip.BondInvestment;
import test.wsdl.roundtrip.StockInvestment;
import test.wsdl.roundtrip.PreferredStockInvestment;
import test.wsdl.roundtrip.CallOptions;
import test.wsdl.roundtrip.InvalidTickerSymbol;
import test.wsdl.roundtrip.InvalidTradeExchange;
import test.wsdl.roundtrip.InvalidCompanyId;

import test.wsdl.roundtrip.holders.BondInvestmentHolder;

import javax.xml.rpc.holders.StringHolder;

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

