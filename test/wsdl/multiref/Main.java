/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights
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
 * 4. The names "SOAP" and "Apache Software Foundation" must
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
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2000, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package test.wsdl.multiref;

import org.apache.axis.utils.Options;

import java.net.URL;
import test.wsdl.multiref.holders.NodeHolder;


/**
 * This class shows/tests how multi-referenced objects are preserved.
 *
 * @author Rich Scheuerle (scheu@us.ibm.com)     
 */
public class Main {

    public static void main (String[] args) throws Exception {
        Options opts = new Options(args);
        
        MultiRefTestService service = new MultiRefTestServiceLocator();
        opts.setDefaultURL( service.getMultiRefTestAddress() );
        URL serviceURL = new URL(opts.getURL());
        if (serviceURL == null)
            serviceURL = new URL(service.getMultiRefTestAddress());

        MultiRefTest remoteTest = service.getMultiRefTest(serviceURL);

        // ----------------------
        // Create a simple tree
        Node t = new Node();
        t.setData(0);
        Node l = new Node();
        l.setData(1);
        Node r = new Node();
        r.setData(2);
        t.setLeft(l);
        t.setRight(r);
        NodeHolder holder = new NodeHolder(t);
        
        // Test for simple tree
        int rc = remoteTest.testSimpleTree(holder);
        if (rc == 0) {
            // System.err.println("Passed testSimpleTree 1");
        } else {
            System.err.println("Failed testSimpleTree 1");
            throw new Exception("Failed testSimpleTree 1 with "+rc);
        }
        // Test returns the tree.  To make sure it returned it successfully,
        // invoke the test again!
        rc = remoteTest.testSimpleTree(holder);
        if (rc == 0) {
            // System.err.println("Passed testSimpleTree 2");
        } else {
            System.err.println("Failed testSimpleTree 2");
            throw new Exception("Failed testSimpleTree 2 with "+rc);
        }

        // ----------------------
        // Create a diamond
        t = new Node();
        t.setData(0);
        l = new Node();
        l.setData(1);
        r = new Node();
        r.setData(2);
        t.setLeft(l);
        t.setRight(r);

        Node d = new Node();
        d.setData(3);
        t.getLeft().setRight(d);
        t.getRight().setLeft(d);
        holder = new NodeHolder(t);

        // Test for a diamond
        rc = remoteTest.testDiamond(holder);
        if (rc == 0) {
            // System.err.println("Passed testDiamond 1");
        } else {
            System.err.println("Failed testDiamond 1");
            throw new Exception("Failed testDiamond 1 with "+rc);
        }
        // Test returns the tree.  To make sure it returned it successfully,
        // invoke the test again!
        rc = remoteTest.testDiamond(holder);
        if (rc == 0) {
            // System.err.println("Passed testDiamond 2");
        } else {
            System.err.println("Failed testDiamond 2");
            throw new Exception("Failed testDiamond 2 with "+rc);
        }

        // ----------------------
        // Create a 'loop' tree.  The children of the root have children that reference the root.
        t = new Node();
        t.setData(0);
        l = new Node();
        l.setData(1);
        r = new Node();
        r.setData(2);
        t.setLeft(l);
        t.setRight(r);

        l.setLeft(t);
        l.setRight(t);
        r.setLeft(t);
        r.setRight(t);

        holder = new NodeHolder(t);

        // Test for loops
        rc = remoteTest.testLoop(holder);
        if (rc == 0) {
            // System.err.println("Passed testLoop 1");
        } else {
            System.err.println("Failed testLoop 1");
            throw new Exception("Failed testLoop 1 with "+rc);
        }
        // Test returns the tree.  To make sure it returned it successfully,
        // invoke the test again!
        rc = remoteTest.testLoop(holder);
        if (rc == 0) {
            // System.err.println("Passed testLoop 2");
        } else {
            System.err.println("Failed testLoop 2");
            throw new Exception("Failed testLoop 2 with "+rc);
        }

        // ----------------------
        // Test passing of the same node argument.
        t = new Node();
        t.setData(0);
        NodeHolder holder1 = new NodeHolder(t);
        NodeHolder holder2 = new NodeHolder(t);

        // Test
        rc = remoteTest.testSameArgs(holder1, holder2);
        if (rc == 0) {
            // System.err.println("Passed testSameArgs 1");
        } else {
            System.err.println("Failed testSameArgs 1");
            throw new Exception("Failed testSameArgs 1 with "+rc);
        }
        // Test returns the tree.  To make sure it returned it successfully,
        // invoke the test again!
        rc = remoteTest.testSameArgs(holder1,holder2);
        if (rc == 0) {
            // System.err.println("Passed testSameArgs 2");
        } else {
            System.err.println("Failed testSameArgs 2");
            throw new Exception("Failed testSameArgs 2 with "+rc);
        } 

        // ----------------------
        // Test args referencing same node.
        Node t1 = new Node();
        t1.setData(0);
        Node t2 = new Node();
        t2.setData(1);
        Node s  = new Node();
        s.setData(1);
        t1.setRight(s);
        t2.setLeft(s);
        holder1 = new NodeHolder(t1);
        holder2 = new NodeHolder(t2);

        // Test
        rc = remoteTest.testArgsRefSameNode(holder1, holder2);
        if (rc == 0) {
            // System.err.println("Passed testArgsRefSameNode 1");
        } else {
            System.err.println("Failed testArgsRefSameNode 1");
            throw new Exception("Failed testArgsRefSameNode 1 with "+rc);
        }
        // Test returns the tree.  To make sure it returned it successfully,
        // invoke the test again!
        rc = remoteTest.testArgsRefSameNode(holder1,holder2);
        if (rc == 0) {
            // System.err.println("Passed testArgsRefSameNode 2");
        } else {
            System.err.println("Failed testArgsRefSameNode 2");
            throw new Exception("Failed testArgsRefSameNode 2 with "+rc);
        }
        // ----------------------
        // Test args referencing each other.
        t1 = new Node();
        t1.setData(0);
        t2 = new Node();
        t2.setData(1);
        t2.setLeft(t1);
        t2.setRight(t1);
        t1.setRight(t2);
        t1.setLeft(t2);
        holder1 = new NodeHolder(t1);
        holder2 = new NodeHolder(t2);

        // Test
        rc = remoteTest.testArgsRefEachOther(holder1, holder2);
        if (rc == 0) {
            // System.err.println("Passed testArgsRefEachOther 1");
        } else {
            System.err.println("Failed testArgsRefEachOther 1");
            throw new Exception("Failed testArgsRefEachOther 1 with "+rc);
        }
        // Test returns the tree.  To make sure it returned it successfully,
        // invoke the test again!
        rc = remoteTest.testArgsRefEachOther(holder1,holder2);
        if (rc == 0) {
            // System.err.println("Passed testArgsRefEachOther 2");
        } else {
            System.err.println("Failed testArgsRefEachOther 2");
            throw new Exception("Failed testArgsRefEachOther 2 with "+rc);
        }

        // ----------------------
        // Create self referencing node
        t = new Node();
        t.setData(0);
        t.setLeft(t);
        t.setRight(t);
        holder = new NodeHolder(t);

        // Self-Ref Test
        rc = remoteTest.testSelfRef(holder);
        if (rc == 0) {
            // System.err.println("Passed testSelfRef 1");
        } else {
            System.err.println("Failed testSelfRef 1");
            throw new Exception("Failed testSelfRef 1 with "+rc);
        }
        // Test returns the tree.  To make sure it returned it successfully,
        // invoke the test again!
        rc = remoteTest.testSelfRef(holder);
        if (rc == 0) {
            // System.err.println("Passed testSelfRef 2");
        } else {
            System.err.println("Failed testSelfRef 2");
            throw new Exception("Failed testSelfRef 2 with "+rc);
        }
 
        return;
    }
}

