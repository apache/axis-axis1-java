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
package test.dynamic;

import junit.framework.TestCase;
import samples.addr.AddressBook;
import samples.addr.AddressBookSOAPBindingStub;

import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceFactory;
import java.net.URL;
import java.rmi.Remote;

/**
 * This class tests Fault deserialization.
 *
 * @author Sam Ruby (rubys@us.ibm.com)
 */

public class ServiceGetPort extends TestCase {
    
    public ServiceGetPort(String name) {
        super(name);
    } // ctor
    
    public void testGetGeneratedStub() throws Exception {
        Service service = ServiceFactory.newInstance().createService(
                new URL("file:samples/addr/AddressBook.wsdl"),
                new QName("urn:AddressFetcher2", "AddressBookService"));
        QName portName = new QName("urn:AddressFetcher2", "AddressBook");
        Remote stub = service.getPort(portName, AddressBook.class);
        assertTrue("Stub should be an instance of AddressBookSOAPBindingStub; instead, it is " + stub.getClass().getName(), stub instanceof AddressBookSOAPBindingStub);
    } // testGetGeneratedStub
}
