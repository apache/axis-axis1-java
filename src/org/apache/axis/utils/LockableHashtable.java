/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
package org.apache.axis.utils ;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Set;
import java.util.HashSet;

// fixme: Is there a reason to use Hashtable rather than Map here?
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

    // fixme - we are potentialy synchronizing on /both/ the current Hashtable
    //  and also the Vector - a non-synchronizing List impl such as ArrayList
    //  may give better performance. We are doing lots of .contains on this
    //  Vector - it would probably be better to use a Set impl
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
     * Gets the parent Hashtable for this object (if any)
     */
    public synchronized Hashtable getParent() {
      return parent;
    }

    /**
     * Returns the keys in this hashtable, and its parent chain
     */
    public Set getAllKeys() {
        HashSet set = new HashSet();
        set.addAll(super.keySet());
        Hashtable p = parent;
        while (p != null) {
            set.addAll(p.keySet());
            if (p instanceof LockableHashtable) {
                p = ((LockableHashtable) p).getParent();
            } else {
                p = null;
            }
        }
        return set;
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
