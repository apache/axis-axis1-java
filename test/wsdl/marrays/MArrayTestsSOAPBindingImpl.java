/**
 * MArrayTestsSOAPBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 */

package test.wsdl.marrays;

public class MArrayTestsSOAPBindingImpl implements test.wsdl.marrays.MArrayTests {
    public int[][][] testIntArray(int[][][] in) throws java.rmi.RemoteException {
        // Each non-nill element should have a value that is i + 10*j + 100*k
        // Add 1000 to each correct value encountered.
        for (int i=0; i < in.length; i++) {
            int[][] array2 = in[i];
            if (array2 != null)
                for (int j=0; j < array2.length; j++) {
                    int[] array3 = array2[j];
                    if (array3 != null)
                        for (int k=0; k <array3.length; k++) {
                            if (array3[k] == i + 10*j + 100*k)
                                array3[k] += 1000;
                        }
                }
        }
        return in;
    }
    public test.wsdl.marrays.Foo[][][] testFooArray(test.wsdl.marrays.Foo[][][] in) throws java.rmi.RemoteException {
        // Each non-nill element should have a value that is i + 10*j + 100*k
        // Add 1000 to each correct value encountered.
        for (int i=0; i < in.length; i++) {
            Foo[][] array2 = in[i];
            if (array2 != null)
                for (int j=0; j < array2.length; j++) {
                    Foo[] array3 = array2[j];
                    if (array3 != null)
                        for (int k=0; k <array3.length; k++) {
                            if (array3[k].getValue() == i + 10*j + 100*k)
                                array3[k].setValue(i + 10*j + 100*k + 1000);
                        }
                }
        }
        return in;
    }
    public java.util.Map testMapFooArray(java.util.Map map)
        throws java.rmi.RemoteException {
        return map;
    }
}
