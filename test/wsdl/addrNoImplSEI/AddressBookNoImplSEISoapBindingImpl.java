/**
 * AddressBookNoImplSEISOAPBindingImpl.java
 *
 * This file was hand modified from the Emmitter generated code.
 */

package test.wsdl.addrNoImplSEI;

import java.util.Hashtable;
import java.util.Map;

// Don't implement the AddressBook interface
public class AddressBookNoImplSEISoapBindingImpl {
    private Map addresses = new Hashtable();

    public void addEntry(java.lang.String name, test.wsdl.addrNoImplSEI.Address address) 
        throws java.rmi.RemoteException, 
               java.lang.IllegalArgumentException  // This should be accepted
    {
        if (address == null) {
            throw new java.lang.IllegalArgumentException(); 
        }
        this.addresses.put(name, address);
    }
    public test.wsdl.addrNoImplSEI.Address getAddressFromName(java.lang.String name) 
        throws java.rmi.RemoteException,
               javax.xml.rpc.JAXRPCException // This should be accepted
    {
        return (test.wsdl.addrNoImplSEI.Address) this.addresses.get(name);
    }

    public test.wsdl.addrNoImplSEI.Address[] getAddresses() throws java.rmi.RemoteException {
        test.wsdl.addrNoImplSEI.Address[] array = new test.wsdl.addrNoImplSEI.Address[this.addresses.size()];
        this.addresses.values().toArray(array);
        return array;
    }
}
