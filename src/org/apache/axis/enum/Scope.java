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

package org.apache.axis.enum;




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
