/**
 * ExampleSoapImpl.java
 *
 * Verifies that wrapped operations featuring Java wrapper array types are working.
 */

package test.wsdl.wrapperHolder2;

public class ExampleSoapImpl implements test.wsdl.wrapperHolder2.ExampleSoap {
    public void doExample(byte[][] value1, java.lang.Long value2, holders.ByteArrayArrayHolder val1, test.wsdl.wrapperHolder2.holders.OutArrayHolder val2, holders.IntArrayWrapperHolder val3, holders.IntArrayHolder val4, holders.ByteArrayHolder val5) throws java.rmi.RemoteException {

        val1.value = value1;
        val2.value = new long[] { 1, 2, 3 };
        val3.value = new Integer[] { new Integer(4), new Integer(5) };
        val4.value = new int[] { 6, 7, 8 };
        val5.value = new byte[] { (byte)0x9, (byte)0x10 };
    }
}
