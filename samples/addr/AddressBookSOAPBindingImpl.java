/**
 * AddressBookSOAPBindingImpl.java
 *
 * This file was hand modified from the Emmitter generated code.
 */

package samples.addr;

import java.util.HashMap;
import java.util.Map;

public class AddressBookSOAPBindingImpl implements AddressBook {
    private Map addresses = new HashMap();

    public void addEntry(java.lang.String name, Address address) throws java.rmi.RemoteException {
        this.addresses.put(name, address);
    }
    public Address getAddressFromName(java.lang.String name) throws java.rmi.RemoteException {
        return (Address) this.addresses.get(name);
    }
}
