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

public class InteropTestListServiceTestCase extends junit.framework.TestCase {
    public static URL url;

    public InteropTestListServiceTestCase(String name) {
        super(name);
    }

    public void testInteropTestListEchoLinkedList() {
        InteropTestList binding;
        try {
            if (url == null) {
                binding = new InteropTestListServiceLocator().getInteropTestList();
            } else {
                binding = new InteropTestListServiceLocator().getInteropTestList(url);
            }
        } catch (javax.xml.rpc.ServiceException jre) {
            throw new AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            List node1 = new List();
            node1.setVarInt(1);
            node1.setVarString("last");
            List node2 = new List();
            node2.setVarInt(2);
            node2.setVarString("middle");
            node2.setChild(node1);
            List list = new List();
            list.setVarInt(3);
            list.setVarString("first");
            list.setChild(node2);

            List value = binding.echoLinkedList(list);
            List vnode2 = value.getChild();
            List vnode1 = null;
            if (vnode2 != null) {
                vnode1 = vnode2.getChild();
            }
                
            if (value.getVarInt() != list.getVarInt() ||
                !value.getVarString().equals(list.getVarString()) ||
                vnode2 == null || 
                vnode2.getVarInt() != node2.getVarInt() ||
                !vnode2.getVarString().equals(node2.getVarString()) ||
                vnode1 == null ||
                vnode1.getVarInt() != node1.getVarInt() ||
                !vnode1.getVarString().equals(node1.getVarString()) ||
                vnode1.getChild() != null) {
                throw new AssertionFailedError("List echo failed");
            }
        }
        catch (java.rmi.RemoteException re) {
            throw new AssertionFailedError("Remote Exception caught: " + re);
        }
    }

}

