/**
 * FaultServiceSoapBindingImpl.java
 *
 *  This service always returns an InvalidTcikerFault.
 *
 * Author: Tom Jordahl (tomj@macromedia.com)
 */

package test.wsdl.faults;

import org.apache.axis.utils.XMLUtils;
import org.apache.axis.message.MessageElement;

public class FaultServiceSoapBindingImpl implements test.wsdl.faults.FaultServicePortType {
    public float getQuote(java.lang.String tickerSymbol) throws java.rmi.RemoteException, InvalidTickerFaultMessage {
        throw new InvalidTickerFaultMessage(tickerSymbol);
    }

    public int throwFault(int a, java.lang.String b, float c) throws java.rmi.RemoteException, test.wsdl.faults.DerivedFault {
        throw new DerivedFault2(a, b, c);
    }

    public int throwExtensionFault(java.lang.String description) throws java.rmi.RemoteException, test.wsdl.faults.ExtensionFault {
        ExtensionType extension = new ExtensionType();
        try {
            extension.set_any(new MessageElement[] {new MessageElement(XMLUtils.newDocument().createElement(description))});
        } catch (Exception e) {
            throw new java.rmi.RemoteException(e.getMessage());
        }
        throw new ExtensionFault(extension);
    }

}
