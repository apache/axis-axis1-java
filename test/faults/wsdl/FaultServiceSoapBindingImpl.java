/**
 * FaultServiceSoapBindingImpl.java
 *
 *  This service always returns an InvalidTcikerFault.
 *
 * Author: Tom Jordahl (tomj@macromedia.com)
 */

package test.faults.wsdl;

public class FaultServiceSoapBindingImpl implements test.faults.wsdl.FaultServicePortType {
    public float getQuote(java.lang.String tickerSymbol) throws java.rmi.RemoteException, InvalidTickerFaultMessage {
        throw new InvalidTickerFaultMessage(tickerSymbol);
    }
}
