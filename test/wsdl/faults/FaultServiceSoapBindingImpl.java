/**
 * FaultServiceSoapBindingImpl.java
 *
 *  This service always returns an InvalidTcikerFault.
 *
 * Author: Tom Jordahl (tomj@macromedia.com)
 */

package test.wsdl.faults;

public class FaultServiceSoapBindingImpl implements test.wsdl.faults.FaultServicePortType {
    public float getQuote(java.lang.String tickerSymbol) throws java.rmi.RemoteException, InvalidTickerFaultMessage {
        throw new InvalidTickerFaultMessage(tickerSymbol);
    }

    public int throwFault(int a, java.lang.String b, float c) throws java.rmi.RemoteException, test.wsdl.faults.DerivedFault {
        throw new DerivedFault2(a, b, c);
    }
}
