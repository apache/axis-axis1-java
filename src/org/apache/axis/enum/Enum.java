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

package org.apache.axis.enum;

import java.util.Vector;
import java.util.Hashtable;

import org.apache.axis.utils.JavaUtils;

import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;


/**
 * General support for 'enumerated' data types.
 * Name searches are case insensitive.
 * 
 * @author Richard Sitze (rsitze@apache.org)
 */
public abstract class Enum implements java.io.Serializable {
    private static final Hashtable types = new Hashtable(13);
    
    protected static Log log =
        LogFactory.getLog(Enum.class.getName());

    private final Type   type;
    public  final int    value;
    public  final String name;
    
    protected Enum(Type type, int value, String name) {
        this.type = type;
        this.value = value;
        this.name = name.intern();
    }
    
    public final int    getValue() { return value; }
    public final String getName()  { return name; }
    public final Type   getType()  { return type; }

    public String toString() {
        return name;
    }

    public final boolean equals(Object obj) {
        return (obj != null  &&  obj instanceof Enum)
               ? _equals((Enum)obj)
               : false;
    }
    
    public final boolean equals(Enum obj) {
        return (obj != null) ? _equals(obj) : false;
    }

    /**
     * The 'equals' logic assumes that there is a one-to-one
     * relationship between value & name.  If this isn't true,
     * then expect to be confused when using this class with
     * Collections.
     */
    private final boolean _equals(Enum obj) {
        return (//obj.name == name  &&  // names are internalized
                obj.type == type  &&
                obj.value == value);
    }

    private Object readResolve() throws java.io.ObjectStreamException {
        return getType().getEnum(value);
    }
    
    public static abstract class Type implements java.io.Serializable {
        private final String name;
        private final Enum[] enums;
        private Enum  dephault = null;
        
        protected Type(String name, Enum[] enums) {
            this.name = name.intern();
            this.enums = enums;
            synchronized (types) {
                types.put(name, this);
            }
        }
        
        protected void setDefault(Enum dephault) {
            this.dephault = dephault;
        }
        
        public Enum getDefault() {
            return dephault;
        }
        
        public final String getName() {
            return name;
        }
        
        public final boolean isValid(String enumName) {
            for (int enum = 0; enum < enums.length; enum++) {
                if (enums[enum].getName().equalsIgnoreCase(enumName))
                    return true;
            }
            
            return false;
        }
        
        public final int size() {
            return enums.length;
        }
        
        /**
         * Returns array of names for enumerated values
         */
        public final String[] getEnumNames() {
            String[] nms = new String[ size() ];
            
            for (int idx = 0; idx < enums.length; idx++)
                nms[idx] = enums[idx].getName();
    
            return nms;
        }
        
        /**
         * Returns name of enumerated value
         */
        public final Enum getEnum(int enum) {
            return (enum >= 0  &&  enum < enums.length) ? enums[enum] : null;
        }
        
        /**
         * Returns enumerated value of name
         */
        public final Enum getEnum(String enumName) {
            Enum e = getEnum(enumName, null);
            
            if (e == null) {
                log.error(JavaUtils.getMessage("badEnum02", name, enumName));
            }
    
            return e;
        }
        
        /**
         * Returns enumerated value of name
         * 
         * For large sets of enumerated values, a HashMap could
         * be used to retrieve.  It's not clear if there is any
         * benefit for small (3 to 4) sets, as used now.
         */
        public final Enum getEnum(String enumName, Enum dephault) {
            if (enumName != null  &&  enumName.length() > 0) {
                for (int enum = 0; enum < enums.length; enum++) {
                    Enum e = enums[enum];
                    if (e.getName().equalsIgnoreCase(enumName))
                        return e;
                }
            }
    
            return dephault;
        }

        private Object readResolve() throws java.io.ObjectStreamException {
            Object type = types.get(name);
            if (type == null) {
                type = this;
                types.put(name, type);
            }
            return type;
        }
    }
}
