/**
 * SequenceTestSOAPBindingImpl.java
 *
 * @author: Rich Scheuerle (scheu@us.ibm.com)
 */

package test.wsdl.sequence;
import java.util.Vector;

public class SequenceTestSoapBindingImpl implements test.wsdl.sequence.SequenceTestPortType {
    public Integer testSequence(test.wsdl.sequence.SequenceInfo info) throws java.rmi.RemoteException {
        Vector v = info.order();
        if (v == null || v.size() != 6) 
            return new Integer(-100);
        if (!((String)v.elementAt(0)).equals("zero")) {
            return new Integer(-1);
        }
        if (!((String)v.elementAt(1)).equals("one")) {
            return new Integer(-2);
        }
        if (!((String)v.elementAt(2)).equals("two")) {
            return new Integer(-3);
        }
        if (!((String)v.elementAt(3)).equals("three")) {
            return new Integer(-4);
        }
        if (!((String)v.elementAt(4)).equals("four")) {
            return new Integer(-5);
        }
        if (!((String)v.elementAt(5)).equals("five")) {
            return new Integer(-6);
        }
        return new Integer(0); // Success
    }
}
