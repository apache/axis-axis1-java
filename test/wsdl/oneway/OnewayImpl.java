/**
 * AddressBookSOAPBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.oneway;

import java.rmi.RemoteException;

import java.util.Hashtable;

public class OnewayImpl implements Oneway {
    private Hashtable ht = new Hashtable();

    public void addEntry(String name, Address address) throws RemoteException {
        ht.put(name, address);
    }

    public Address getAddressFromName(String name) throws RemoteException {
        return (Address) ht.get(name);
    }

    public void throwException() throws RemoteException {
        throw new RemoteException("OnewayImpl throws RemoteException");
    }

}
