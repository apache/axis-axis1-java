/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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

