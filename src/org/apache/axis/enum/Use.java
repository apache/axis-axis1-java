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
