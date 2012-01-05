/*
 * Copyright 2000,2004 The Apache Software Foundation.
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

package test.wsdl.multiref;

import org.apache.axis.utils.Options;
import test.wsdl.multiref.holders.NodeHolder;

import java.net.URL;


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
        // Create a 'loop' tree.  The children of the root have children that reference the root.
        // In this test both children have the same data (thus the same equals()).
        // There should still be separate nodes passed over the wire
        t = new Node();
        t.setData(0);
        l = new Node();
        l.setData(1);    
        r = new Node();
        r.setData(1);
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
            // System.err.println("Passed testLoop 1B");
        } else {
            System.err.println("Failed testLoop 1B");
            throw new Exception("Failed testLoop 1B with "+rc);
        }
        // Test returns the tree.  To make sure it returned it successfully,
        // invoke the test again!
        rc = remoteTest.testLoop(holder);
        if (rc == 0) {
            // System.err.println("Passed testLoop 2B");
        } else {
            System.err.println("Failed testLoop 2B");
            throw new Exception("Failed testLoop 2B with "+rc);
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

