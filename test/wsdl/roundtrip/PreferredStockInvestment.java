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

/**
 * The PreferredStockInvestment class extends the StockInvestment
 * class so that we can verify that the Java2WSDL tool correctly
 * creates WSDL to allow data members in StockInvestment and Investment
 * to be accessed. 
 *
 * @version   1.00  06 Feb 2002
 * @author    Brent Ulbricht
 */
public class PreferredStockInvestment extends StockInvestment implements java.io.Serializable {

    public BigDecimal preferredYield;
    private double dividendsInArrears;

    public PreferredStockInvestment() {

    } // Constructor

    public void setDividendsInArrears(double dividendsInArrears) {
        this.dividendsInArrears = dividendsInArrears; 
    } // setDividendsInArrears

    public double getDividendsInArrears() {
        return this.dividendsInArrears;
    } // getDividendsInArrears

} // PreferredStockInvestment 
