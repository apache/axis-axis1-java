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

package test.multiref;

/**
 * MultiRefTestSOAPBindingImpl.java
 *
 * @author Rich Scheuerle (scheu@us.ibm.com)
 */
public class MultiRefTestSOAPBindingImpl implements test.multiref.MultiRefTest {

    /**
     * Tests for the following arrangement of nodes:
     *        0
     *       / \
     *      1   2
     */
    public int testSimpleTree(test.multiref.NodeHolder root) throws java.rmi.RemoteException {
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
    public int testDiamond(test.multiref.NodeHolder root) throws java.rmi.RemoteException {
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
    public int testLoop(test.multiref.NodeHolder root) throws java.rmi.RemoteException {
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
    public int testSelfRef(test.multiref.NodeHolder root) throws java.rmi.RemoteException {
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
    public int testSameArgs(test.multiref.NodeHolder root1,test.multiref.NodeHolder root2)
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
    public int testArgsRefSameNode(test.multiref.NodeHolder root1,test.multiref.NodeHolder root2)
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
    public int testArgsRefEachOther(test.multiref.NodeHolder root1,test.multiref.NodeHolder root2)
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
