/**
 * SequenceTestSOAPBindingImpl.java
 *
 * @author: Rich Scheuerle (scheu@us.ibm.com)
 */

package test.sequence;
import java.util.Vector;

public class SequenceTestSoapBindingImpl implements test.sequence.SequenceTestPortType {
    public int testSequence(test.sequence.SequenceInfo info) throws java.rmi.RemoteException {
        Vector v = info.order();
        if (v == null || v.size() != 6) 
            return -100;
        if (!((String)v.elementAt(0)).equals("zero")) {
            return -1;
        }
        if (!((String)v.elementAt(1)).equals("one")) {
            return -2;
        }
        if (!((String)v.elementAt(2)).equals("two")) {
            return -3;
        }
        if (!((String)v.elementAt(3)).equals("three")) {
            return -4;
        }
        if (!((String)v.elementAt(4)).equals("four")) {
            return -5;
        }
        if (!((String)v.elementAt(5)).equals("five")) {
            return -6;
        }
        return 0; // Success
    }
}
