/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
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
     * any contained exception
     */
    private Exception containedException=null;

    private String stackTrace="";

    /**
     * Copy the orginal exception by default
     */
    protected static boolean copyStackByDefault= true;

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
     * stringify, including stack trace.
     * @return
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
     * get any contained exception
     * @return base exception or null
     * @since axis1.1
     */
    public Exception getContainedException() {
        return containedException;
    }
}
