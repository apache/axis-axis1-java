/**
 * AddressBookSOAPBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 */

package samples.addr;

public class AddressBookSOAPBindingSkeleton {
    private AddressBook impl;

    public AddressBookSOAPBindingSkeleton() {
        this.impl = new AddressBookSOAPBindingImpl();
    }

    public AddressBookSOAPBindingSkeleton(AddressBook impl) {
        this.impl = impl;
    }

    public void addEntry(String name, Address address) throws java.rmi.RemoteException
    {
        impl.addEntry(name, address);
    }

    public Object getAddressFromName(String name) throws java.rmi.RemoteException
    {
        Object ret = impl.getAddressFromName(name);
        return ret;
    }

}
