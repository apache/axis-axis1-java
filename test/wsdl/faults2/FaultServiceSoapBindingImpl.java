package test.wsdl.faults2;

import org.apache.axis.message.MessageElement;
import org.apache.axis.utils.XMLUtils;

public class FaultServiceSoapBindingImpl implements test.wsdl.faults2.FaultServicePortType {

    public String throwFault(java.lang.String p1, java.lang.String p2) throws java.rmi.RemoteException, test.wsdl.faults2.TestFault {
        if (p1.equals("throw") || p2.equals("throw")) {
            throw new TestFault(p1,0,p2);
        }
        return p2 + " " + p1;
    }
}
