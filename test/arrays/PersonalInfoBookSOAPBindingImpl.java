/**
 * PersonalInfoBookSOAPBindingImpl.java
 *
 * Hand-modified
 */

package test.arrays;
import java.util.HashMap;
import java.util.Map;

public class PersonalInfoBookSOAPBindingImpl implements test.arrays.PersonalInfoBook {
    private Map table = new HashMap();
    public void addEntry(java.lang.String name, test.arrays.PersonalInfo info) throws java.rmi.RemoteException {
        this.table.put(name, info);
    }
    public test.arrays.PersonalInfo getPersonalInfoFromName(java.lang.String name) throws java.rmi.RemoteException {
        return (test.arrays.PersonalInfo) table.get(name);
    }
    public String[] getPetsFromName(java.lang.String name) throws java.rmi.RemoteException {
        return ((test.arrays.PersonalInfo) table.get(name)).getPets();
    }
    public int[] getIDFromName(java.lang.String name) throws java.rmi.RemoteException {
        return ((test.arrays.PersonalInfo) table.get(name)).getId();
    }
    public int getID2FromName(java.lang.String name) throws java.rmi.RemoteException {
        return ((test.arrays.PersonalInfo) table.get(name)).getId2();
    }
    public int[][][] testArray3(int[][][] array3) throws java.rmi.RemoteException {
        for (int i=0; i<array3.length; i++) {
            int[][] array2 = array3[i];
            for (int j=0; j<array2.length; j++) {
                int[] array = array2[j];
                for (int k=0; k<array.length; k++) {
                    // Expecting a value that is the sum of i + 10*j + 100*k.
                    // If so increase by 1000
                    if (array[k] == i + 10*j + 100*k);
                    array[k] += 1000;
                }
            }
        }
        return array3;
    }
    public String[][][] testArray3S(String[][][] array3) throws java.rmi.RemoteException {
        for (int i=0; i<array3.length; i++) {
            String[][] array2 = array3[i];
            for (int j=0; j<array2.length; j++) {
                String[] array = array2[j];
                for (int k=0; k<array.length; k++) {
                    // Expecting a value that is the sum of i + 10*j + 100*k.
                    // If so increase by 1000
                    if (array[k].equals(String.valueOf(i + 10*j + 100*k)))
                        array[k] = String.valueOf(i + 10*j + 100*k + 1000);
                }
            }
        }
        return array3;
    }
}
