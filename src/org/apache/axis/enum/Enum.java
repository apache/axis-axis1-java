package org.apache.axis.enum;

import java.util.Vector;

import org.apache.axis.utils.JavaUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * General support for 'enumerated' data types.
 * Name searches are case insensitive.
 * 
 * @author Richard Sitze (rsitze@apache.org)
 */
public abstract class Enum {
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

    public static abstract class Type {
        private final String name;
        private final Enum[] enums;
        private Enum  dephault = null;
        
        protected Type(String name, Enum[] enums) {
            this.name = name.intern();
            this.enums = enums;
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
    }
}
