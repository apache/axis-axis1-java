/**
 * AddressBookService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 */

package samples.addr;

public class AddressBookService {

    // Use to get a proxy class for AddressBook
    private final java.lang.String AddressBook_address = "http://localhost:8080/axis/servlet/AxisServlet";
    public AddressBook getAddressBook() {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(AddressBook_address);
        }
        catch (java.net.MalformedURLException e) {
            return null; // unlikely as URL was validated in wsdl2java
        }
        return getAddressBook(endpoint);
    }

    public AddressBook getAddressBook(java.net.URL portAddress) {
        try {
            return new AddressBookSOAPBindingStub(portAddress);
        }
        catch (org.apache.axis.SerializationException e) {
            return null; // ???
        }
    }
}
