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
package test.wsdl.oneway;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.Stub;
import java.rmi.RemoteException;

/**
* This tests various oneway operation features.
*/

public class OnewayTestCase extends TestCase {
    public OnewayTestCase(String name) {
        super(name);
    }

    /**
     * Sessions shouldn't work with oneway operations, so the call to getAddressFromName
     * should return null.
     */
    public void test1NoSessionOnOneway() {
        Oneway binding;
        try {
            binding = new OnewayServiceLocator().getOneway();
        }
        catch (ServiceException jre) {
            throw new AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            ((Stub) binding)._setProperty(Stub.SESSION_MAINTAIN_PROPERTY, new Boolean(true));
            binding.addEntry("hi", new Address());
            Address address = binding.getAddressFromName("hi");
            assertTrue("session doesn't work on oneway operations, address should have been null", address == null);
        }
        catch (RemoteException re) {
            throw new AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    /**
     * binding.throwException will cause the server impl to throw an exception,
     * but since this is a oneway operation, that exception should not propagate
     * back to the client.
     */
    public void test2NoExceptionOnOneway() {
        Oneway binding;
        try {
            binding = new OnewayServiceLocator().getOneway();
        }
        catch (ServiceException jre) {
            throw new AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            binding.throwException();
        }
        catch (Throwable t) {
            throw new AssertionFailedError("Throwable: " + t);
        }
    }

}
