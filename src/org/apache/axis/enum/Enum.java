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

package org.apache.axis.enum;

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

import java.util.Hashtable;


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

    public int hashCode() {
        return value;
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
    
    public abstract static class Type implements java.io.Serializable {
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
        
        public void setDefault(Enum dephault) {
            this.dephault = dephault;
        }
        
        public Enum getDefault() {
            return dephault;
        }
        
        public final String getName() {
            return name;
        }
        
        public final boolean isValid(String enumName) {
            for (int enumElt = 0; enumElt < enums.length; enumElt++) {
                if (enums[enumElt].getName().equalsIgnoreCase(enumName))
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
        public final Enum getEnum(int enumElt) {
            return (enumElt >= 0  &&  enumElt < enums.length) ? enums[enumElt] : null;
        }
        
        /**
         * Returns enumerated value of name
         */
        public final Enum getEnum(String enumName) {
            Enum e = getEnum(enumName, null);
            
            if (e == null) {
                log.error(Messages.getMessage("badEnum02", name, enumName));
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
                for (int enumElt = 0; enumElt < enums.length; enumElt++) {
                    Enum e = enums[enumElt];
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
