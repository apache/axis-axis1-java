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

import org.apache.axis.Constants;


/**
 * Use enum description
 * @author Richard Scheuerle
 */
public class Use extends Enum {

    /**
     * See Style.java for a description of the combination
     * of style and use.
     */

    private static final Type type = new Type();
    
    public static final String ENCODED_STR = "encoded";
    public static final String LITERAL_STR = "literal";
 
    public static final Use ENCODED = type.getUse(ENCODED_STR);
    public static final Use LITERAL = type.getUse(LITERAL_STR);

    public static final Use DEFAULT = ENCODED;
    
    static { type.setDefault(DEFAULT); }
    private String encoding;

    public static Use getDefault() { return (Use)type.getDefault(); }
    
    public final String getEncoding() { return encoding; }

    public static final Use getUse(int style) {
        return type.getUse(style);
    }

    public static final Use getUse(String style) {
        return type.getUse(style);
    }
    
    public static final Use getUse(String style, Use dephault) {
        return type.getUse(style, dephault);
    }
    
    public static final boolean isValid(String style) {
        return type.isValid(style);
    }
    
    public static final int size() {
        return type.size();
    }
    
    public static final String[] getUses() {
        return type.getEnumNames();
    }
    
    public static class Type extends Enum.Type {
        private Type() {
            super("style", new Enum[] {
            new Use(0, ENCODED_STR,
                  Constants.URI_DEFAULT_SOAP_ENC),
            new Use(1, LITERAL_STR,
                  Constants.URI_LITERAL_ENC),
            });
        }

        public final Use getUse(int style) {
            return (Use)this.getEnum(style);
        }

        public final Use getUse(String style) {
            return (Use)this.getEnum(style);
        }

        public final Use getUse(String style, Use dephault) {
            return (Use)this.getEnum(style, dephault);
        }

    }

    private Use(int value, String name, String encoding) {
        super(type, value, name);
        this.encoding = encoding;
    }
};
