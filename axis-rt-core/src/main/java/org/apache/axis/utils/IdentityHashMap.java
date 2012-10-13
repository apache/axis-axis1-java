/**
 * Created by IntelliJ IDEA.
 * User: srida01
 * Date: Dec 2, 2002
 * Time: 10:38:46 AM
 * To change this template use Options | File Templates.
 */
package org.apache.axis.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * IdentityHashMap similar to JDK1.4's java.util.IdentityHashMap
 * @author Davanum Srinivas <dims@yahoo.com>
 */
public class IdentityHashMap extends HashMap
{
    /**
     * Constructor for IdentityHashMap.
     * @param initialCapacity
     * @param loadFactor
     */
    public IdentityHashMap(int initialCapacity, float loadFactor)
    {
        super(initialCapacity, loadFactor);
    }

    /**
     * Constructor for IdentityHashMap.
     * @param initialCapacity
     */
    public IdentityHashMap(int initialCapacity)
    {
        super(initialCapacity);
    }

    /**
     * Constructor for IdentityHashMap.
     */
    public IdentityHashMap()
    {
        super();
    }

    /**
     * Constructor for IdentityHashMap.
     * @param t
     */
    public IdentityHashMap(Map t)
    {
        super(t);
    }

    /**
     * @see Map#get(Object)
     */
    public Object get(Object key)
    {
        return super.get(new IDKey(key));
    }

    /**
     * @see Map#put(Object, Object)
     */
    public Object put(Object key, Object value)
    {
        return super.put(new IDKey(key), value);
    }

    /**
     * adds an object to the Map. new Identity(obj) is used as key
     */
    public Object add(Object value)
    {
        Object key = new IDKey(value);
        if (! super.containsKey(key))
        {
            return super.put(key, value);
        }
        else return null;

    }

    /**
     * @see Map#remove(Object)
     */
    public Object remove(Object key)
    {
        return super.remove(new IDKey(key));
    }

    /**
     * @see Map#containsKey(Object)
     */
    public boolean containsKey(Object key)
    {
        return super.containsKey(new IDKey(key));
    }
}
