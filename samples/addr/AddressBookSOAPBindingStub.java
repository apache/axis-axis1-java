/**
 * AddressBookSOAPBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 */

package samples.addr;

public class AddressBookSOAPBindingStub extends org.apache.axis.rpc.Stub implements AddressBook {
    private org.apache.axis.client.ServiceClient call = new org.apache.axis.client.ServiceClient(new org.apache.axis.transport.http.HTTPTransport());
    private java.util.Hashtable properties = new java.util.Hashtable();

    public AddressBookSOAPBindingStub(java.net.URL endpointURL) throws org.apache.axis.SerializationException {
         this();
         call.set(org.apache.axis.transport.http.HTTPTransport.URL, endpointURL.toString());
    }
    public AddressBookSOAPBindingStub() throws org.apache.axis.SerializationException {
        try {
            org.apache.axis.utils.QName qn1 = new org.apache.axis.utils.QName("urn:xml-soap-address-demo", "Phone");
            Class cls = Phone.class;
            call.addSerializer(cls, qn1, new org.apache.axis.encoding.BeanSerializer(cls));
            call.addDeserializerFactory(qn1, cls, org.apache.axis.encoding.BeanSerializer.getFactory());
        }
        catch (Throwable t) {
            throw new org.apache.axis.SerializationException("Phone", t);
        }

        try {
            org.apache.axis.utils.QName qn1 = new org.apache.axis.utils.QName("urn:xml-soap-address-demo", "Address");
            Class cls = Address.class;
            call.addSerializer(cls, qn1, new org.apache.axis.encoding.BeanSerializer(cls));
            call.addDeserializerFactory(qn1, cls, org.apache.axis.encoding.BeanSerializer.getFactory());
        }
        catch (Throwable t) {
            throw new org.apache.axis.SerializationException("Address", t);
        }

    }

    public void _setProperty(String name, Object value) {
        properties.put(name, value);
    }

    // From org.apache.axis.wsdl.Stub
    public Object _getProperty(String name) {
        return properties.get(name);
    }

    // From org.apache.axis.wsdl.Stub
    public void _setTargetEndpoint(java.net.URL address) {
        call.set(org.apache.axis.transport.http.HTTPTransport.URL, address.toString());
    }

    // From org.apache.axis.wsdl.Stub
    public java.net.URL _getTargetEndpoint() {
        try {
            return new java.net.URL((String) call.get(org.apache.axis.transport.http.HTTPTransport.URL));
        }
        catch (java.net.MalformedURLException mue) {
            return null; // ???
        }
    }

    // From org.apache.axis.wsdl.Stub
    public synchronized void setMaintainSession(boolean session) {
        call.setMaintainSession(session);
    }

    // From javax.naming.Referenceable
    public javax.naming.Reference getReference() {
        return null; // ???
    }

    public void addEntry(String name, Address address) throws java.rmi.RemoteException{
        if (call.get(org.apache.axis.transport.http.HTTPTransport.URL) == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        call.set(org.apache.axis.transport.http.HTTPTransport.ACTION, "");
        Object resp = call.invoke("urn:AddressFetcher2", "addEntry", new Object[] {new org.apache.axis.message.RPCParam("name", name), new org.apache.axis.message.RPCParam("address", address)});

        if (resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)resp;
        }
    }

    public Address getAddressFromName(String name) throws java.rmi.RemoteException{
        if (call.get(org.apache.axis.transport.http.HTTPTransport.URL) == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        call.set(org.apache.axis.transport.http.HTTPTransport.ACTION, "");
        Object resp = call.invoke("urn:AddressFetcher2", "getAddressFromName", new Object[] {new org.apache.axis.message.RPCParam("name", name)});

        if (resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)resp;
        }
        else {
             return (Address) resp;
        }
    }

}
