/*
 * Copyright 2002-2004 The Apache Software Foundation.
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

/**
 * This file was based on a testcase auto-generated from WSDL by the
 * Apache Axis Wsdl2java emitter.
 *
 * @author Glyn Normington <glyn@apache.org>
 */

package test.wsdl.interop3.groupE.client;

import junit.framework.AssertionFailedError;

import java.net.URL;

public class InteropTestRpcEncServiceTestCase extends junit.framework.TestCase {
    public static URL url;

    public InteropTestRpcEncServiceTestCase(String name) {
        super(name);
    }
    public void testInteropTestRpcEncEchoString() {
        InteropTestRpcEnc binding;
        try {
            if (url == null) {
                binding = new InteropTestRpcEncServiceLocator().getInteropTestRpcEnc();
            } else {
                binding = new InteropTestRpcEncServiceLocator().getInteropTestRpcEnc(url);
            }
        } catch (javax.xml.rpc.ServiceException jre) {
            throw new AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            String input = "a string";
            String value = binding.echoString(input);
            if (!value.equals(input)) {
                throw new AssertionFailedError("String echo failed");
            }
        }
        catch (java.rmi.RemoteException re) {
            throw new AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void testInteropTestRpcEncEchoStringArray() {
        InteropTestRpcEnc binding;
        try {
            if (url == null) {
                binding = new InteropTestRpcEncServiceLocator().getInteropTestRpcEnc();
            } else {
                binding = new InteropTestRpcEncServiceLocator().getInteropTestRpcEnc(url);
            }
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            String[] input = {"string 1", "string 2"};
            String[] value = binding.echoStringArray(input);

            boolean equal = true;
            if (input.length != value.length) {
                equal = false;
            } else {
                for (int i = 0; i < value.length; i++) {
                    if (!input[i].equals(value[i])) {
                        equal = false;
                    }
                }
            }
            if (!equal) {
                throw new AssertionFailedError("StringArray echo failed");
            }
        }
        catch (java.rmi.RemoteException re) {
            throw new AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void testInteropTestRpcEncEchoStruct() {
        InteropTestRpcEnc binding;
        try {
            if (url == null) {
                binding = new InteropTestRpcEncServiceLocator().getInteropTestRpcEnc();
            } else {
                binding = new InteropTestRpcEncServiceLocator().getInteropTestRpcEnc(url);
            }
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            SOAPStruct input = new SOAPStruct();
            input.setVarFloat(3.142f);
            input.setVarInt(3);
            input.setVarString("Pi");
            SOAPStruct value = binding.echoStruct(input);
            if (value.getVarFloat() != input.getVarFloat() ||
                value.getVarInt() != input.getVarInt() ||
                !value.getVarString().equals(input.getVarString())) {
                throw new AssertionFailedError("Struct echo failed");
            }
        }
        catch (java.rmi.RemoteException re) {
            throw new AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void testInteropTestRpcEncEchoVoid() {
        InteropTestRpcEnc binding;
        try {
            if (url == null) {
                binding = new InteropTestRpcEncServiceLocator().getInteropTestRpcEnc();
            } else {
                binding = new InteropTestRpcEncServiceLocator().getInteropTestRpcEnc(url);
            }
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            binding.echoVoid();
        }
        catch (java.rmi.RemoteException re) {
            throw new AssertionFailedError("Remote Exception caught: " + re);
        }
    }

}

