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
}
