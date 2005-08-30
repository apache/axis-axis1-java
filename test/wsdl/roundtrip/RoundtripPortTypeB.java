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

