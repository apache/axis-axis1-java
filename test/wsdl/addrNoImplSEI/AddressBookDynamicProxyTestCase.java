/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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


