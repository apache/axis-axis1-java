/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001, 2002 The Apache Software Foundation.  All rights
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

package org.apache.axis.transport.jms;

import java.util.Map;

/**
 * MapUtils provides convenience methods for accessing a java.util.Map
 *
 * @author Jaime Meritt  (jmeritt@sonicsoftware.com)
 * @author Richard Chung (rchung@sonicsoftware.com)
 * @author Dave Chappell (chappell@sonicsoftware.com)
 */
public class MapUtils
{
    /**
     * Returns an int property from a Map and removes it.
     *
     * @param properties
     * @param key
     * @param defaultValue
     * @return
     */
    public static int removeIntProperty(Map properties, String key, int defaultValue)
    {
        int value = defaultValue;
        if(properties != null && properties.containsKey(key))
        {
            try{value = ((Integer)properties.remove(key)).intValue();}catch(Exception ignore){}
        }
        return value;
    }

    /**
     * Returns a long property from a Map and removes it.
     *
     * @param properties
     * @param key
     * @param defaultValue
     * @return
     */
    public static long removeLongProperty(Map properties, String key, long defaultValue)
    {
        long value = defaultValue;
        if(properties != null && properties.containsKey(key))
        {
            try{value = ((Long)properties.remove(key)).longValue();}catch(Exception ignore){}
        }
        return value;
    }

    /**
     * Returns a String property from a Map and removes it.
     *
     * @param properties
     * @param key
     * @param defaultValue
     * @return
     */
    public static String removeStringProperty(Map properties, String key, String defaultValue)
    {
        String value = defaultValue;
        if(properties != null && properties.containsKey(key))
        {
            try{value = (String)properties.remove(key);}catch(Exception ignore){}
        }
        return value;
    }

    /**
     * Returns a boolean property from a Map and removes it.
     *
     * @param properties
     * @param key
     * @param defaultValue
     * @return
     */
    public static boolean removeBooleanProperty(Map properties, String key, boolean defaultValue)
    {
        boolean value = defaultValue;
        if(properties != null && properties.containsKey(key))
        {
            try{value = ((Boolean)properties.remove(key)).booleanValue();}catch(Exception ignore){}
        }
        return value;
    }

    /**
     * Returns an Object property from a Map and removes it.
     *
     * @param properties
     * @param key
     * @param defaultValue
     * @return
     */
    public static Object removeObjectProperty(Map properties, String key, Object defaultValue)
    {
        Object value = defaultValue;
        if(properties != null && properties.containsKey(key))
        {
            value = properties.remove(key);
        }
        return value;
    }
}
