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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.HashMap;

/**
 * The BondInvestment class contains data members for all the
 * primitives, standard Java classes, and primitive wrapper
 * classes.  This class is used to test that all the data
 * members transmit correctly over the wire.
 *
 * @version   1.00  06 Feb 2002
 * @author    Brent Ulbricht
 */
public class BondInvestment implements java.io.Serializable {

    private boolean taxableInvestment;
    public byte taxIndicator; 
    public short docType;  
    public int stockBeta;
    public long yield;
    public float lastTradePrice;
    public double fiftyTwoWeekHigh;
    private String tradeExchange;
    public BigInteger portfolioType;
    public BigDecimal bondAmount;
    public Calendar callableDate;
    public byte[] byteArray;
    private short[] shortArray;
    private Boolean wrapperBoolean;
    private Byte wrapperByte;
    private Short wrapperShort;
    public Integer wrapperInteger;
    public Float wrapperFloat;
    private Double wrapperDouble;
    public Byte[] wrapperByteArray;
    public Short[] wrapperShortArray;
    private CallOptions[] options;
    public Object options2;
    public Object options3;
    public int id;
    public HashMap map;

    public BondInvestment() {

    } // Constructor

    public void setTaxableInvestment(boolean taxableInvestment) {
        this.taxableInvestment = taxableInvestment;
    } // setTaxableInvestment

    public boolean getTaxableInvestment() {
        return this.taxableInvestment;
    } // getTaxableInvestment
    
    public void setTradeExchange(String tradeExchange) {
        this.tradeExchange = tradeExchange; 
    } // setTradeExchange

    public String getTradeExchange() {
        return this.tradeExchange;
    } // getTradeExchange

    public void setShortArray(short[] shortArray) {
        this.shortArray = shortArray;
    } // getShortArray

    public short[] getShortArray() {
        return this.shortArray;
    } // setShortArray

    public void setWrapperBoolean(Boolean wrapperBoolean) {
        this.wrapperBoolean = wrapperBoolean;
    } // setWrapperBoolean

    public Boolean getWrapperBoolean() {
        return this.wrapperBoolean;
    } // getWrapperBoolean
    
    public void setWrapperByte(Byte wrapperByte) {
        this.wrapperByte = wrapperByte;
    } // setWrapperByte

    public Byte getWrapperByte() {
        return this.wrapperByte;
    } // getWrapperByte
    
    public void setWrapperShort(Short wrapperShort) {
        this.wrapperShort = wrapperShort;
    } // setWrapperShort

    public Short getWrapperShort() {
        return this.wrapperShort;
    } // getWrapperShort

    public void setWrapperDouble(Double wrapperDouble) {
        this.wrapperDouble = wrapperDouble;
    } // setWrapperDouble

    public Double getWrapperDouble() {
        return this.wrapperDouble;
    } // getWrapperDouble

    // List of fields that are XML attributes
    private static java.lang.String[] _attrs = new String[] {
        "taxIndicator", 
        "docType",
        "stockBeta"
    };

    /**
     * Return list of bean field names that are attributes
     */
    public static java.lang.String[] getAttributeElements() {
        return _attrs;
    }
 
    public CallOptions getOptions(int i) {
        return options[i];
    }

    public void setOptions(int i, CallOptions value) {
        if (options == null ||
            options.length <= i) {
            CallOptions[] a = new CallOptions[i + 1];
            if (options != null) {
                for(int j=0; j<options.length; j++)
                    a[j] = options[j];
            }
            options = a;
        }
        options[i] = value;
    }

    public CallOptions[] getOptions() {
        return options;
    }

    public void setOptions(CallOptions[] options) {
        this.options = options;
    }

    public HashMap getMap() {
        return map;
    }

    public void setMap(HashMap map) {
        this.map = map;
    }
} // BondInvestment 
