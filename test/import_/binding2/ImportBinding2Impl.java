/**
 * ImportBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 */

package test.import_.binding2;

import test.import_.iface.Import;
import test.import_.types.Address;

import java.rmi.RemoteException;

public class ImportBinding2Impl implements Import {
    public void addEntry(String key, Address value) throws RemoteException {
    } // addEntry

    public Address getAddressFromName(String key) throws RemoteException {
        return new Address();
    } // getAddressFromName
}
