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
package org.apache.axis.utils ;

import java.util.Hashtable;
import java.util.Vector;

/**
 * This subclass of the java Hashtable allows individual 
 * entries to be "locked" so that their values cannot be
 * overwritten or removed.
 * 
 * Note, only the put() and remove() methods have been
 * overridden.  The clear() method still removes all
 * entries whether they've been locked or not.
 * 
 * @author James Snell (jasnell@us.ibm.com)
 */
public class LockableHashtable extends Hashtable { 

    /**
     * Stores the keys of the locked entries
     */
    Vector lockedEntries = new Vector();
    
    /** Place to look for properties which we don't find locally. */
    private Hashtable parent = null;

    public LockableHashtable() {
        super();
    }
    
    public LockableHashtable(int p1, float p2) {
        super(p1, p2);
    }
    
    public LockableHashtable(java.util.Map p1) {
        super(p1);
    }
    
    public LockableHashtable(int p1) {
        super(p1);
    }
    
    /**
     * Set the parent Hashtable for this object
     */ 
    public synchronized void setParent(Hashtable parent)
    {
        this.parent = parent;
    }

    /**
     * Get an entry from this hashtable, and if we don't find anything,
     * defer to our parent, if any.
     */ 
    public synchronized Object get(Object key) {
        Object ret = super.get(key);
        if ((ret == null) && (parent != null)) {
            ret = parent.get(key);
        }
        return ret;
    }
    /**
     * New version of the put() method that allows for explicitly marking
     * items added to the hashtable as locked.
     */
    public synchronized Object put(Object p1, Object p2, boolean locked) {
        if (this.containsKey(p1) && lockedEntries.contains(p1)) {
            return null;
        }
        if (locked) lockedEntries.add(p1);
        return super.put(p1, p2);
    }
    
    /**
     * Overrides the Hashtable.put() method to mark items as not being locked.
     */
    public synchronized Object put(Object p1, Object p2) {
        return put(p1, p2, false);
    }

    /**
     * Checks to see if an item is locked before it is removed. 
     */
    public synchronized Object remove(Object p1) {
        if (lockedEntries.contains(p1)) {
            return null;
        }
        return super.remove(p1);
    }
    
    /**
     * Returns true if a given key is in our locked list
     */ 
    public boolean isKeyLocked(Object key)
    {
        return lockedEntries.contains(key);
    }
}
