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
package org.apache.axis.client.async;

import org.apache.axis.enum.Enum;

/**
 * Status of the async request
 * 
 * @author Davanum Srinivas (dims@yahoo.com)
 */
public class Status extends Enum {

    /**
     * Field type
     */
    private static final Type type = new Type();

    /**
     * Field NONE_STR
     */
    public static final String NONE_STR = "none";

    /**
     * Field INTERRUPTED_STR
     */
    public static final String INTERRUPTED_STR = "interrupted";

    /**
     * Field COMPLETED_STR
     */
    public static final String COMPLETED_STR = "completed";

    /**
     * Field EXCEPTION_STR
     */
    public static final String EXCEPTION_STR = "exception";

    /**
     * Field NONE
     */
    public static final Status NONE = type.getStatus(NONE_STR);

    /**
     * Field INTERRUPTED
     */
    public static final Status INTERRUPTED = type.getStatus(INTERRUPTED_STR);

    /**
     * Field COMPLETED
     */
    public static final Status COMPLETED = type.getStatus(COMPLETED_STR);

    /**
     * Field EXCEPTION
     */
    public static final Status EXCEPTION = type.getStatus(EXCEPTION_STR);

    /**
     * Field DEFAULT
     */
    public static final Status DEFAULT = NONE;

    static {
        type.setDefault(DEFAULT);
    }

    /**
     * Method getDefault
     * 
     * @return 
     */
    public static Status getDefault() {
        return (Status) type.getDefault();
    }

    /**
     * Method getStatus
     * 
     * @param style 
     * @return 
     */
    public static final Status getStatus(int style) {
        return type.getStatus(style);
    }

    /**
     * Method getStatus
     * 
     * @param style 
     * @return 
     */
    public static final Status getStatus(String style) {
        return type.getStatus(style);
    }

    /**
     * Method getStatus
     * 
     * @param style    
     * @param dephault 
     * @return 
     */
    public static final Status getStatus(String style, Status dephault) {
        return type.getStatus(style, dephault);
    }

    /**
     * Method isValid
     * 
     * @param style 
     * @return 
     */
    public static final boolean isValid(String style) {
        return type.isValid(style);
    }

    /**
     * Method size
     * 
     * @return 
     */
    public static final int size() {
        return type.size();
    }

    /**
     * Method getUses
     * 
     * @return 
     */
    public static final String[] getUses() {
        return type.getEnumNames();
    }

    /**
     * Class Type
     * 
     * @author 
     * @version %I%, %G%
     */
    public static class Type extends Enum.Type {

        /**
         * Constructor Type
         */
        private Type() {

            super("status", new Enum[]{new Status(0, NONE_STR),
                                       new Status(1, INTERRUPTED_STR),
                                       new Status(2, COMPLETED_STR),
                                       new Status(3, EXCEPTION_STR), });
        }

        /**
         * Method getStatus
         * 
         * @param status 
         * @return 
         */
        public final Status getStatus(int status) {
            return (Status) this.getEnum(status);
        }

        /**
         * Method getStatus
         * 
         * @param status 
         * @return 
         */
        public final Status getStatus(String status) {
            return (Status) this.getEnum(status);
        }

        /**
         * Method getStatus
         * 
         * @param status   
         * @param dephault 
         * @return 
         */
        public final Status getStatus(String status, Status dephault) {
            return (Status) this.getEnum(status, dephault);
        }
    }

    /**
     * Constructor Status
     * 
     * @param value 
     * @param name  
     */
    private Status(int value, String name) {
        super(type, value, name);
    }
}
