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
