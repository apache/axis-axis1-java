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
 * The StockInvestment class extends the Investment class so that
 * we can verify that the Java2WSDL tool correctly creates WSDL
 * to allow data members in Investment to be accessed. 
 *
 * @version   1.00  06 Feb 2002
 * @author    Brent Ulbricht
 */
public class StockInvestment extends Investment implements java.io.Serializable {

    public float lastTradePrice;
    private String tradeExchange;
    private float stockBeta;
    private double fiftyTwoWeekHigh;

    public StockInvestment() {

    } // Constructor

    public String getTradeExchange() {
        return tradeExchange;
    } // getTradeExchange

    public void setTradeExchange(String tradeExchange) {
        this.tradeExchange = tradeExchange; 
    } // setTradeExchange

} // StockInvestment 
