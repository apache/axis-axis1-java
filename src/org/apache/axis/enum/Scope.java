package org.apache.axis.enum;

import javax.xml.namespace.QName;

import org.apache.axis.Constants;
import org.apache.axis.deployment.wsdd.WSDDConstants;


/**
 * @author rsitze
 */
public class Scope extends Enum {
    private static final Type type = new Type();
    
    public static final String REQUEST_STR = "Request";
    public static final String APPLICATION_STR = "Application";
    public static final String SESSION_STR = "Session";

    public static final Scope REQUEST = type.getScope(REQUEST_STR);
    public static final Scope APPLICATION = type.getScope(APPLICATION_STR);
    public static final Scope SESSION = type.getScope(SESSION_STR);
        
    public static final Scope DEFAULT = REQUEST;


    static { type.setDefault(DEFAULT); }

    
    // public int     getValue();
    // public String  getName();
    // public Type    getType();

    public static Scope getDefault() { return (Scope)type.getDefault(); }
    
    public static final Scope getScope(int scope) {
        return type.getScope(scope);
    }

    public static final Scope getScope(String scope) {
        return type.getScope(scope);
    }
    
    public static final Scope getScope(String scope, Scope dephault) {
        return type.getScope(scope, dephault);
    }
    
    public static final boolean isValid(String scope) {
        return type.isValid(scope);
    }
    
    public static final int size() {
        return type.size();
    }
    
    public static final String[] getScopes() {
        return type.getEnumNames();
    }
    
    public static class Type extends Enum.Type {
        private Type() {
            super("scope", new Enum[] {
                new Scope(0, REQUEST_STR),
                new Scope(1, APPLICATION_STR),
                new Scope(2, SESSION_STR)
            });
        }

        public final Scope getScope(int scope) {
            return (Scope)this.getEnum(scope);
        }

        public final Scope getScope(String scope) {
            return (Scope)this.getEnum(scope);
        }

        public final Scope getScope(String scope, Scope dephault) {
            return (Scope)this.getEnum(scope, dephault);
        }

        // public final String getName();
        // public boolean isValid(String enumName);
        // public final int size();
        // public final String[] getEnumNames();
        // public final Enum getEnum(int enum);
        // public final Enum getEnum(String enumName);
        // public final Enum getEnum(String enumName, Enum dephault);
    }

    private Scope(int value, String name) {
        super(type, value, name);
    }
};
