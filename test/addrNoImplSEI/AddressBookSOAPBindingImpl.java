/**
 * AddressBookSOAPBindingImpl.java
 *
 * This file was hand modified from the Emmitter generated code.
 * This code tests whether the impl class must implement
 * the SEI
 */

package test.addrNoImplSEI;

import java.util.Hashtable;
import java.util.Map;
import samples.addr.Address;

/**
 * Note that the Impl class does not implement the
 * SEI AddressBook.  However it should still work since
 * the methods are the same.
 */
public class AddressBookSOAPBindingImpl
    // implements AddressBook
{
    private Map addresses = new Hashtable();

    public void addEntry(java.lang.String name, samples.addr.Address address) throws java.rmi.RemoteException {
        this.addresses.put(name, address);
    }
    public samples.addr.Address getAddressFromName(java.lang.String name) throws java.rmi.RemoteException {
        return (samples.addr.Address) this.addresses.get(name);
    }
}
