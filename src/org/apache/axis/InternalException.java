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

package org.apache.axis;

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

/**
 * Encapsulates exceptions for "should never occur" situations.  Extends
 * RuntimeException so it need not explicitly be caught.  Logs the exception
 * as a fatal error, and if debug is enabled, includes the full stack trace.
 *
 * @author Sam Ruby (rubys@us.ibm.com)
 * @author Glyn Normington (glyn_normington@uk.ibm.com)
 */
public class InternalException extends RuntimeException {

    /**
     * The <code>Log</code> used by this class to log messages.
     */
    protected static Log log =
            LogFactory.getLog(InternalException.class.getName());

    /**
     * Attribute which controls whether or not logging of such events should
     * occur.  Default is true and is recommended.  Sometimes this may be
     * turned off in unit tests when internal errors are intentionally
     * provoked.
     */
    private static boolean shouldLog = true;

    /**
     * Enable or dissable logging.
     *
     * @param logging  true if you wish logging to be enabled, false otherwise
     */
    public static void setLogging(boolean logging) {
        shouldLog = logging;
    }

    /**
     * Discover whether the logging flag is set.
     *
     * @return true if we are logging, false otherwise
     */
    public static boolean getLogging() {
        return shouldLog;
    }

    /**
     * Construct an Internal Exception from a String.  The string is wrapped
     * in an exception, enabling a stack traceback to be obtained.
     * @param message String form of the error
     */
    public InternalException(String message) {
        this(new Exception(message));
    }

    /**
     * Construct an Internal Exception from an Exception.
     *
     * @param e original exception which was unexpected
     */
    public InternalException(Exception e) {
        super(e.toString());

        if (shouldLog) {
            // if the exception is merely bubbling up the stack, only log the
            // event if debug is turned on.
            if (e instanceof InternalException) {
                log.debug("InternalException: ", e);
            } else {
                log.fatal(Messages.getMessage("exception00"), e);
            }
        }
    }
}
