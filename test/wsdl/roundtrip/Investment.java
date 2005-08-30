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
 * The Investment class is the base class for other
 * types of investment classes.  The main purpose of this
 * class is to insure that when other classes subclass
 * that the data members in Investment can be accessed
 * and transmit correctly across the wire.
 *
 * @version   1.00  06 Feb 2002
 * @author    Brent Ulbricht
 */
public abstract class Investment implements java.io.Serializable {

    public static int dontMapToWSDL;  // This should not be mapped to the WSDL
    public String name;
    private int id;
    private double avgYearlyReturn;   // This should not be mapped to the WSDL

    public Investment() {

    } // Constructor

    public int getId() {
        return id;
    } // getId

    public void setId(int id) {
        this.id = id;
    } // setId

    public float calcAvgYearlyReturn() {
        return 0.0F;
    } // calcAvgYearlyReturn

} // End class Investment
