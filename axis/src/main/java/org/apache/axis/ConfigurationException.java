/*
 * Copyright 2002-2004 The Apache Software Foundation.
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

package org.apache.axis;

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.JavaUtils;
import org.apache.commons.logging.Log;

import java.io.IOException;

/**
 * ConfigurationException is thrown when an error occurs trying to
 * use an EngineConfiguration.
 *
 * @author Glyn Normington (glyn@apache.org)
 */
public class ConfigurationException extends IOException {

    /**
     * The contained exception if present.
     */
    private Exception containedException=null;

    private String stackTrace="";

    /**
     * Flag wether to copy the orginal exception by default.
     */
    protected static boolean copyStackByDefault= true;

    /**
     * The <code>Log</code> used by this class for logging all messages.
     */
    protected static Log log =
        LogFactory.getLog(ConfigurationException.class.getName());

    /**
     * Construct a ConfigurationException from a String.  The string is wrapped
     * in an exception, enabling a stack traceback to be obtained.
     * @param message String form of the error
     */
    public ConfigurationException(String message) {
        super(message);
        if(copyStackByDefault) {
            stackTrace= JavaUtils.stackToString(this);
        }
        logException( this);
    }

    /**
     * Construct a ConfigurationException from an Exception.
     * @param exception original exception which was unexpected
     */
    public ConfigurationException(Exception exception) {
        this(exception,copyStackByDefault);
    }

    /**
     * Stringify, including stack trace.
     *
     * @return a <code>String</code> view of this object
     */
    public String toString() {
        String stack;
        if(stackTrace.length()== 0) {
            stack = "";
        } else {
            stack="\n"+stackTrace;
        }
        return super.toString()+stack;
    }

    /**
     * Construct a ConfigurationException from an Exception.
     * @param exception original exception which was unexpected
     * @param copyStack set to true to copy the orginal exception's stack
     */
    public ConfigurationException(Exception exception, final boolean copyStack) {
        super(exception.toString()  + (copyStack ? "\n"
           + JavaUtils.stackToString(exception) : "" ));
        containedException = exception;
        if(copyStack) {
            stackTrace = JavaUtils.stackToString(this);
        }
        // Log the exception the first time it appears.
        if (!(exception instanceof ConfigurationException)) {
            logException(exception);
        }
    }

    /**
     * single point for logging exceptions.
     * @param exception
     */
    private void logException(Exception exception) {
        log.debug("Exception: ", exception);
    }

    /**
     * Get any contained exception.
     *
     * @return base exception or null
     * @since axis1.1
     */
    public Exception getContainedException() {
        return containedException;
    }
}
