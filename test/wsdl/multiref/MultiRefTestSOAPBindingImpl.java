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

/**
 * MultiRefTestSOAPBindingImpl.java
 *
 * @author Rich Scheuerle (scheu@us.ibm.com)
 */
public class MultiRefTestSOAPBindingImpl implements test.wsdl.multiref.MultiRefTest {

    /**
     * Tests for the following arrangement of nodes:
     *        0
     *       / \
     *      1   2
     */
    public int testSimpleTree(test.wsdl.multiref.holders.NodeHolder root) throws java.rmi.RemoteException {
        Node t = root.value;    // Root of tree 
        Node l = t.getLeft();    // Left side
        Node r = t.getRight();   // Right side

        if (t != null && l != null && r != null &&
            r != l && 
            l.getLeft() == null &&
            l.getRight()== null &&
            r.getLeft() == null &&
            r.getRight()== null)
            return 0;  // Great
        
        return 1;  // Bad       
    }

    /**
     * Tests for the following arrangement of nodes:
     *        0
     *       / \
     *      1   2
     *       \ /
     *        3
     */
    public int testDiamond(test.wsdl.multiref.holders.NodeHolder root) throws java.rmi.RemoteException {
        Node t = root.value;    // Root of tree 
        Node l = t.getLeft();    // Left side
        Node r = t.getRight();   // Right side

        if (t != null && l != null && r != null &&
            r != l && 
            l.getLeft() == null &&
            r.getRight()== null &&
            l.getRight()!= null &&
            l.getRight()== r.getLeft())
            return 0;  // Great
        
        return 1;  // Bad
    }

    /**
     * Tests for the following arrangement of nodes:
     *        0
     *       / \
     *      1   2   and the children of 1 & 2 are backward references to 0
     */
    public int testLoop(test.wsdl.multiref.holders.NodeHolder root) throws java.rmi.RemoteException {
        Node t = root.value;    // Root of tree 
        Node l = t.getLeft();    // Left side
        Node r = t.getRight();   // Right side

        if (t != null && l != null && r != null &&
            r != l && 
            l.getLeft() == t &&
            l.getRight()== t &&
            r.getLeft() == t &&
            r.getRight()== t)
            return 0;  // Great
        
        return 1;  // Bad       
    }
    /**
     * Tests for the following arrangement of nodes:
     *        0
     *        and the children of 0 are backward references to 0
     */
    public int testSelfRef(test.wsdl.multiref.holders.NodeHolder root) throws java.rmi.RemoteException {
        Node t = root.value;    // Root of tree 
        Node l = t.getLeft();    // Left side
        Node r = t.getRight();   // Right side

        if (t != null && l != null && r != null &&
            t == l && t == r) 
            return 0;  // Great
        
        return 1;  // Bad       
    }

    /**
     * Tests that both arguments are the same node & the nodes don't have children
     */
    public int testSameArgs(test.wsdl.multiref.holders.NodeHolder root1,test.wsdl.multiref.holders.NodeHolder root2)
        throws java.rmi.RemoteException {
        Node t1 = root1.value;    // Root1 of tree 
        Node t2 = root2.value;    // Root2 of tree 

        if (t1 != null && t2 != null  &&
            t1 == t2 &&
            t1.getRight() == null &&
            t1.getLeft()  == null)
            return 0;  // Great
        
        return 1;  // Bad       
    }

    /**
     * Tests for the following arrangement of nodes:
     *      0   1
     *       \ /
     *        2     where 0 and 1 are the argument nodes.                      
     */
    public int testArgsRefSameNode(test.wsdl.multiref.holders.NodeHolder root1,test.wsdl.multiref.holders.NodeHolder root2)
        throws java.rmi.RemoteException {
        Node t1 = root1.value;    // Root1 of tree 
        Node t2 = root2.value;    // Root2 of tree 

        if (t1 != null && t2 != null &&
            t1 != t2 &&
            t1.getLeft()  == null &&
            t2.getRight() == null &&
            t1.getRight() != null &&
            t1.getRight() == t2.getLeft() &&
            t1.getRight().getRight() == null &&
            t1.getRight().getLeft() == null)
            return 0;  // Great
        
        return 1;  // Bad       
    }
    /**
     * Tests for two node arguments that reference each other.
     */
    public int testArgsRefEachOther(test.wsdl.multiref.holders.NodeHolder root1,test.wsdl.multiref.holders.NodeHolder root2)
        throws java.rmi.RemoteException {
        Node t1 = root1.value;    // Root1 of tree 
        Node t2 = root2.value;    // Root2 of tree 

        if (t1 != null && t2 != null &&
            t1 != t2 &&
            t1.getLeft()  == t2 &&
            t1.getRight() == t2 &&
            t2.getLeft() == t1 &&
            t2.getRight() == t1)
            return 0;  // Great
        
        return 1;  // Bad       
    }

}
