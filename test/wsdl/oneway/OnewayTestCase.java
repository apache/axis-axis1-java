/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
