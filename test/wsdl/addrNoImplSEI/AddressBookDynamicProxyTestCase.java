/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package test.wsdl.addrNoImplSEI;

import junit.framework.TestCase;
import org.apache.axis.transport.http.SimpleAxisWorker;

import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceFactory;
import javax.xml.rpc.Stub;
import javax.xml.rpc.encoding.TypeMapping;
import javax.xml.rpc.encoding.TypeMappingRegistry;
import java.net.URL;


/** 
 * Test the address book sample code using JAX-RPC's Dynamic Proxy support.
 */
public class AddressBookDynamicProxyTestCase extends TestCase {

    public AddressBookDynamicProxyTestCase(String name) {
        super(name);
    }

    // Use pure JAX-RPC to talk to the server.
    public void testAddressBookServiceUsingDynamicProxy() throws Exception {
        String nameSpaceUri = "http://addrNoImplSEI.wsdl.test";
        String serviceName = "AddressBookNoImplSEIService";

        String thisHost = SimpleAxisWorker.getLocalHost();
        String thisPort = System.getProperty("test.functional.ServicePort", "8080");

        //location of wsdl file
        String wsdlLocation = "http://" + thisHost + ":" + thisPort + "/axis/services/AddressBookNoImplSEI?WSDL";
        URL orgWsdlUrl = new URL(wsdlLocation);

        ServiceFactory serviceFactory = ServiceFactory.newInstance();
        Service addressBookService =
                serviceFactory.createService(orgWsdlUrl,
                        new QName(nameSpaceUri, serviceName));

        // Add the typemapping entries
        TypeMappingRegistry registry = addressBookService.getTypeMappingRegistry();
        TypeMapping map = registry.getDefaultTypeMapping();
        map.register(test.wsdl.addrNoImplSEI.Address.class,
                new QName("urn:AddrNoImplSEI", "Address"),
                new org.apache.axis.encoding.ser.BeanSerializerFactory(test.wsdl.addrNoImplSEI.Address.class, new QName("urn:AddrNoImplSEI", "Address")),
                new org.apache.axis.encoding.ser.BeanDeserializerFactory(test.wsdl.addrNoImplSEI.Address.class, new QName("urn:AddrNoImplSEI", "Address")));
        map.register(test.wsdl.addrNoImplSEI._Phone.class,
                new QName("urn:AddrNoImplSEI", "_Phone"),
                new org.apache.axis.encoding.ser.BeanSerializerFactory(test.wsdl.addrNoImplSEI._Phone.class, new QName("urn:AddrNoImplSEI", "_Phone")),
                new org.apache.axis.encoding.ser.BeanDeserializerFactory(test.wsdl.addrNoImplSEI._Phone.class, new QName("urn:AddrNoImplSEI", "_Phone")));
        map.register(test.wsdl.addrNoImplSEI.StateType.class,
                new QName("urn:AddrNoImplSEI", "StateType"),
                new org.apache.axis.encoding.ser.EnumSerializerFactory(test.wsdl.addrNoImplSEI.StateType.class, new QName("urn:AddrNoImplSEI", "StateType")),
                new org.apache.axis.encoding.ser.EnumDeserializerFactory(test.wsdl.addrNoImplSEI.StateType.class, new QName("urn:AddrNoImplSEI", "StateType")));

        AddressBookNoImplSEI myProxy = (AddressBookNoImplSEI) addressBookService.getPort(AddressBookNoImplSEI.class);
        
        // Set session on.
        ((Stub) myProxy)._setProperty(Stub.SESSION_MAINTAIN_PROPERTY, Boolean.TRUE);

        String name1;
        Address addr1;
        _Phone phone1;

        name1 = "Purdue Boilermaker";
        addr1 = new Address();
        phone1 = new _Phone();
        addr1.setStreetNum(1);
        addr1.setStreetName("University Drive");
        addr1.setCity("West Lafayette");
        addr1.setState(StateType.IN);
        addr1.setZip(47907);
        phone1.setAreaCode(765);
        phone1.setExchange("494");
        phone1.setNumber("4900");
        addr1.setPhone(phone1);
        
        // Add an entry 
        myProxy.addEntry(name1, addr1);
        
        // Get the list of entries
        test.wsdl.addrNoImplSEI.Address[] addresses = myProxy.getAddresses();
        assertTrue(addresses.length > 0);
    }
}


