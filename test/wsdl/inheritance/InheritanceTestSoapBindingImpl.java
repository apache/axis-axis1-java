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

package test.wsdl.inheritance;

/**
 * This class contains the remote method implementations for the 
 * inheritance test.  
 *
 * @version   1.00  21 Jan 2002
 * @author    Brent Ulbricht
 */
public class InheritanceTestSoapBindingImpl implements test.wsdl.inheritance.InheritancePortType {

    /**
     *  This method will return a hard coded value to the client depending on the value of the
     *  tickerSymbol received.
     *
     *  The getLastTradePrice originates from the InheritancePortType interface.
     */
    public float getLastTradePrice(java.lang.String tickerSymbol) throws java.rmi.RemoteException {

        if (tickerSymbol.equals("SOAP")) {
            return 20.25F;
        } else {
            return 0.00F;
        }

    } // getLastTradePrice
    
    /**
     *  This method will return a hard coded value to the client depending on the value of the
     *  tickerSymbol received.     
     *
     *  The getRealtimeLastTradePrice originates from the StockQuoteProvider interface.
     */
    public float getRealtimeLastTradePrice(java.lang.String tickerSymbol) throws java.rmi.RemoteException {
        
        if (tickerSymbol.equals("AXIS")) {
            return 21.75F;
        } else {
            return 0.00F;
        }

    } // getRealtimeLastTradePrice

} // End class InheritanceTestSoapBindingImpl
