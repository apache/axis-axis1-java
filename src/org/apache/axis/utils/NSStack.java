/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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
package org.apache.axis.utils;

import java.util.ArrayList;
import java.util.Stack;
import java.util.Enumeration;

/**
 * @author: James Snell
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class NSStack {
    public static class Mapping {
        public String namespaceURI;
        public String prefix;
        public Mapping(String namespaceURI, String prefix)
        {
            this.namespaceURI = namespaceURI;
            this.prefix = prefix;
        }
        
        public String getNamespaceURI()
        {
            return namespaceURI;
        }
        public String getPrefix()
        {
            return prefix;
        }
    }
    private static final boolean DEBUG_LOG = false;
    
    private static final ArrayList EMPTY = new ArrayList();

    private Stack stack = new Stack();
    
    private NSStack parent = null;

    public NSStack() {}
    
    public NSStack(ArrayList table) {
        push(table);
    }
    
    public NSStack(NSStack parent) {
        this.parent = parent;
    }
    
    public void push() {
        if (stack == null) stack = new Stack();
        if (DEBUG_LOG)
            System.out.println("NSPush (" + stack.size() + ")");
        stack.push(EMPTY);
    }
    
    public void push(ArrayList table) {
        if (stack == null) stack = new Stack();
        if (DEBUG_LOG)
            System.out.println("NSPush (" + stack.size() + ")");
        if (table.size() == 0) 
           stack.push(EMPTY);
        else
           stack.push(table);
    }
    
    public ArrayList peek() {
        if (stack.isEmpty())
            if (parent != null)
                return parent.peek();
            else
                return EMPTY;
                
        
        return (ArrayList)stack.peek();
    }
    
    public ArrayList pop() {
        if (stack.isEmpty()) {
            if (DEBUG_LOG)
                System.out.println("NSPop (empty)");
            if (parent != null)
                return parent.pop();
            return null;
        }
        
        if (DEBUG_LOG) {
            ArrayList t = (ArrayList)stack.pop();
            System.out.println("NSPop (" + stack.size() + ")");
            return t;
        } else {
            return (ArrayList)stack.pop();
        }
    }
    
    public void add(String namespaceURI, String prefix) {
        if (stack.isEmpty()) push();
        ArrayList table = peek();
        if (table == EMPTY) {
            table = new ArrayList();
            stack.pop();
            stack.push(table);
        }
        table.add(new Mapping(namespaceURI, prefix));
    }
    
    /**
     * remove a namespace from the topmost table on the stack
     */
    /*
    public void remove(String namespaceURI) {
        if (stack.isEmpty()) return;
        ArrayList current = peek();
        for (int i = 0; i < current.size(); i++) {
            Mapping map = (Mapping)current.get(i);
            if (map.getNamespaceURI().equals(namespaceURI)) {
                current.removeElementAt(i);
                return; // ???
            }
        }
    }
    */
    
    public String getPrefix(String namespaceURI) {
        if ((namespaceURI == null) || (namespaceURI.equals("")))
            return null;
        
        if (!stack.isEmpty()) {
            for (int n = stack.size() - 1; n >= 0; n--) {
                ArrayList t = (ArrayList)stack.get(n);
                
                for (int i = 0; i < t.size(); i++) {
                    Mapping map = (Mapping)t.get(i);
                    if (map.getNamespaceURI().equals(namespaceURI))
                        return map.getPrefix();
                }
            }
        }
        
        if (parent != null)
            return parent.getPrefix(namespaceURI);
        return null;
    }
    
    public String getNamespaceURI(String prefix) {
        if (prefix == null)
            prefix = "";
        
        if (!stack.isEmpty()) {
            for (int n = stack.size() - 1; n >= 0; n--) {
                ArrayList t = (ArrayList)stack.get(n);
                
                for (int i = 0; i < t.size(); i++) {
                    Mapping map = (Mapping)t.get(i);
                    if (map.getPrefix().equals(prefix))
                        return map.getNamespaceURI();
                }
            }
        }
        
        if (parent != null)
            return parent.getNamespaceURI(prefix);

        if (DEBUG_LOG) {
            System.err.println("didn't find prefix '" + prefix + "'");
            dump();
        }

        return null;
    }
    
    public boolean isDeclared(String namespaceURI) {
        if (!stack.isEmpty()) {
            for (int n = stack.size() - 1; n >= 0; n--) {
                ArrayList t = (ArrayList)stack.get(n);
                if ((t != null) && (t != EMPTY)) {
                    for (int i = 0; i < t.size(); i++) {
                        if (((Mapping)t.get(i)).getNamespaceURI().
                                   equals(namespaceURI))
                            return true;
                    }
                }
            }
        }

        if (parent != null)
            return parent.isDeclared(namespaceURI);

        return false;
    }
    
    public void dump()
    {
        Enumeration e = stack.elements();
        while (e.hasMoreElements()) {
            ArrayList list = (ArrayList)e.nextElement();
            System.out.println("----");
            if (list == null) {
                System.out.println("null table??");
                continue;
            }
            for (int i = 0; i < list.size(); i++) {
                Mapping map = (Mapping)list.get(i);
                System.out.println(map.getNamespaceURI() + " -> " +
                                   map.getPrefix());
            }
        }

        if (parent != null) {
            System.out.println("----parent");
            parent.dump();
        }

        System.out.println("----end");
    }
}
