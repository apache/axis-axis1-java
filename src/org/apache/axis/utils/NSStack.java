/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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

import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Stack;
import java.util.Iterator;

/**
 * The abstraction this class provides is a push down stack of variable
 * length frames of prefix to namespace mappings.  Used for keeping track
 * of what namespaces are active at any given point as an XML document is
 * traversed or produced.
 *
 * From a performance point of view, this data will both be modified frequently
 * (at a minimum, there will be one push and pop per XML element processed),
 * and scanned frequently (many of the "good" mappings will be at the bottom
 * of the stack).  The one saving grace is that the expected maximum 
 * cardinalities of the number of frames and the number of total mappings
 * is only in the dozens, representing the nesting depth of an XML document
 * and the number of active namespaces at any point in the processing.
 *
 * Accordingly, this stack is implemented as a single array, will null
 * values used to indicate frame boundaries.
 *
 * @author: James Snell
 * @author Glen Daniels (gdaniels@macromedia.com)
 * @author Sam Ruby (rubys@us.ibm.com)
 */
public class NSStack {
    protected static Log log =
        LogFactory.getLog(NSStack.class.getName());
    
    private Mapping[] stack;
    private int top = 0;
    private int iterator = 0;
    
    public NSStack() {
        stack = new Mapping[32];
        stack[0] = null;
    }
    
    /**
     * Create a new frame at the top of the stack.
     */
    public void push() {
        top ++;

        if (top >= stack.length) {
           Mapping newstack[] = new Mapping[stack.length*2];
           System.arraycopy (stack, 0, newstack, 0, stack.length);
           stack = newstack;
        }

        if (log.isTraceEnabled())
            log.trace("NSPush (" + stack.length + ")");

        stack[top] = null;
    }
    
    /**
     * Remove the top frame from the stack.
     */
    public void pop() {
        clearFrame();

        if (top == 0) {
            if (log.isTraceEnabled())
                log.trace("NSPop (" + JavaUtils.getMessage("empty00") + ")");

            return;
        }
        
        top--;

        if (log.isTraceEnabled()){
            log.trace("NSPop (" + stack.length + ")");
        }
    }
    
    /**
     * Return a copy of the current frame.
     */
    public ArrayList cloneFrame() {
        ArrayList clone = new ArrayList();

        topOfFrame();

        while (iterator <= top) clone.add(stack[iterator++]);

        return clone;
    }

    /**
     * Remove all mappings from the current frame.
     */
    public void clearFrame() {
        while (stack[top] != null) top--;
    }

    /**
     * Reset the embedded iterator in this class to the top of the current
     * (i.e., last) frame.  Note that this is not threadsafe, nor does it
     * provide multiple iterators, so don't use this recursively.  Nor
     * should you modify the stack while iterating over it.
     */
    public Mapping topOfFrame() {
        iterator = top;
        while (stack[iterator] != null) iterator--;
        iterator++;
        return next();
    }

    /**
     * Return the next namespace mapping in the top frame.
     */
    public Mapping next() {
        if (iterator > top) {
            return null;
        } else {
            return stack[iterator++];
        }
    }

    /**
     * Add a mapping for a namespaceURI to the specified prefix to the top
     * frame in the stack.  If the prefix is already mapped in that frame,
     * remap it to the (possibly different) namespaceURI.
     */
    public void add(String namespaceURI, String prefix) {
        // Replace duplicate prefixes (last wins - this could also fault)
        for (int cursor=top; stack[cursor]!=null; cursor--) {
            if (stack[cursor].getPrefix().equals(prefix)) {
                stack[cursor].setNamespaceURI(namespaceURI);
                return;
            }
        }

        push();
        stack[top] = new Mapping(namespaceURI, prefix);
    }
    
    /**
     * Return an active prefix for the given namespaceURI.  NOTE : This
     * may return null even if the namespaceURI was actually mapped further
     * up the stack IF the prefix which was used has been repeated further
     * down the stack.  I.e.:
     * 
     * <pre:outer xmlns:pre="namespace">
     *   <pre:inner xmlns:pre="otherNamespace">
     *      *here's where we're looking*
     *   </pre:inner>
     * </pre:outer>
     * 
     * If we look for a prefix for "namespace" at the indicated spot, we won't
     * find one because "pre" is actually mapped to "otherNamespace"
     */ 
    public String getPrefix(String namespaceURI, boolean noDefault) {
        if ((namespaceURI == null) || (namespaceURI.equals("")))
            return null;
        
        int hash = namespaceURI.hashCode();

        for (int cursor=top; cursor>0; cursor--) {
            Mapping map = stack[cursor];
            if (map == null) continue;

            if (map.getNamespaceHash() == hash &&
                map.getNamespaceURI().equals(namespaceURI)) {
                String possiblePrefix = map.getPrefix();
                if (noDefault && possiblePrefix.length() == 0) continue;

                // now make sure that this is the first occurance of this 
                // particular prefix
                int ppHash = possiblePrefix.hashCode();
                for (int cursor2=top; true; cursor2--) {
                   if (cursor2 == cursor) return possiblePrefix;
                   map = stack[cursor2];
                   if (map == null) continue;
                   if (ppHash == map.getPrefixHash() &&
                       possiblePrefix.equals(map.getPrefix())) break;
                }
            }
        }
        
        return null;
    }

    /**
     * Return an active prefix for the given namespaceURI, including
     * the default prefix ("").
     */ 
    public String getPrefix(String namespaceURI) {
        return getPrefix(namespaceURI, false);
    }
    
    /**
     * Given a prefix, return the associated namespace (if any).
     */
    public String getNamespaceURI(String prefix) {
        if (prefix == null)
            prefix = "";

        int hash = prefix.hashCode();

        for (int cursor=top; cursor>0; cursor--) {
            Mapping map = stack[cursor];
            if (map == null) continue;
        
            if (map.getPrefixHash() == hash && map.getPrefix().equals(prefix))
                return map.getNamespaceURI();
        }
        
        return null;
    }
    
    /**
     * Produce a trace dump of the entire stack, starting from the top and
     * including frame markers.
     */
    public void dump(String dumpPrefix)
    {
        for (int cursor=top; cursor>0; cursor--) {
            Mapping map = stack[cursor];

            if (map == null) {
                log.trace(dumpPrefix + JavaUtils.getMessage("stackFrame00"));
            } else {
                log.trace(dumpPrefix + map.getNamespaceURI() + " -> " + map.getPrefix());
            }
        }
    }
}
