/**
 * AddressBookSOAPBindingImpl.java
 *
 * This file was hand modified from the Emmitter generated code.
 */

package test.wsdl.addrNoImplSEI;

import java.util.Hashtable;
import java.util.Map;

// Don't implement the AddressBook interface
public class AddressBookSOAPBindingImpl {
    private Map addresses = new Hashtable();

    public void addEntry(java.lang.String name, test.wsdl.addrNoImplSEI.Address address) throws java.rmi.RemoteException {
        this.addresses.put(name, address);
    }
    public test.wsdl.addrNoImplSEI.Address getAddressFromName(java.lang.String name) throws java.rmi.RemoteException {
        return (test.wsdl.addrNoImplSEI.Address) this.addresses.get(name);
    }
}
