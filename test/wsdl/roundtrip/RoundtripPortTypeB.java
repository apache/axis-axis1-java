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
 * The RoundtripPortTypeB defines methods for RoundtripPortType
 *
 * @author    Rich Scheuerle
 */
public interface RoundtripPortTypeB {

    public java.lang.Float methodSoapFloat(java.lang.Float inSoapFloat)
        throws java.rmi.RemoteException;
    public java.lang.Double methodSoapDouble(java.lang.Double inSoapDouble)
        throws java.rmi.RemoteException;
    public java.lang.Boolean methodSoapBoolean(java.lang.Boolean inSoapBoolean)
        throws java.rmi.RemoteException;
    public java.lang.Byte methodSoapByte(java.lang.Byte inSoapByte)
        throws java.rmi.RemoteException;
    public java.lang.Short methodSoapShort(java.lang.Short inSoapShort)
        throws java.rmi.RemoteException;
    public java.lang.Integer methodSoapInt(java.lang.Integer inSoapInt)
        throws java.rmi.RemoteException;
    public java.lang.Long methodSoapLong(java.lang.Long inSoapLong)
        throws java.rmi.RemoteException;
    public void throwInvalidTickerException()
        throws InvalidTickerSymbol, 
               java.rmi.RemoteException;
    public void throwInvalidTradeExchange()
        throws InvalidCompanyId, InvalidTradeExchange, InvalidTickerSymbol, 
               java.rmi.RemoteException;

    // Overloading test
    public int getId(BondInvestment investment) throws java.rmi.RemoteException;
    public int getId(Investment investment) throws java.rmi.RemoteException;

    public void holderTest(StringHolder sh, BondInvestmentHolder bih);
} // RoundtripPortType

