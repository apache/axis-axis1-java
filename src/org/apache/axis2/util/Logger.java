/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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
 * 4. The names "Xerces" and "Apache Software Foundation" must
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
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.ibm.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.axis2.util ;

import java.io.OutputStream ;
import java.io.PrintWriter ;
import java.util.Date ;
import java.text.DateFormat ;

/**
 * <code>Logger</code> writes down logs.
 * @author Ryo Neyama (neyama@jp.ibm.com)
 * @version $Id$
 */
final public class Logger {
    public static final int MIN_LEVEL = 0 ;
    public static final int DEFAULT_LEVEL = 4 ;
    public static final int MAX_LEVEL = 9 ;
    private static final Object lock = new Object() ;
    private static int currentLevel = DEFAULT_LEVEL ;
    private static PrintWriter out = new PrintWriter(System.out);
    private static final String PROPERTY_NAME = "org.apache.axis2.util.logLevel" ;
    static {
        String value ;
        if ((value = System.getProperties().getProperty(PROPERTY_NAME)) != null) {
            try {
                setLevel(Integer.parseInt(value)) ;
                normal("Set log level to " + value) ;
            } catch (NumberFormatException e) {
                warning("Logger: " + e.getMessage()) ;
            }
        }
    }
    private Logger() {}

    /**
     * Sets the OutputStream as the target OutputStream
     * @param out the OutputStream
     */
    public static void setOut(OutputStream out) {
        setOut(new PrintWriter(out)) ;
    }

    /**
     * Sets the PrintWriter as the target PrintWriter
     * @param out the PrintWriter
     */
    public static void setOut(PrintWriter out) {
        synchronized (lock) { Logger.out = out ; }
    }

    /**
     * Sets the current logging level.  Only the messages with same or higher level than current logging level are actually written. The messages witout level is always written.
     */
    public static void setLevel(int level) {
        if (level < MIN_LEVEL) level = MIN_LEVEL ;
        if (level > MAX_LEVEL) level = MAX_LEVEL ;
        currentLevel = level ;
    }

    public static int getLevel() { return currentLevel ; }

    /**
     * Writes down a normal message. The message is always written.
     */
    public static void normal(String message) {
        print(message, ' ') ;
    }

    /**
     * Writes down a normal message with a specified level.
     */
    public static void normal(String message, int level) {
        if (level >= currentLevel)
            print(message, ' ') ;
    }

    /**
     * Writes down a warning message. The message is always written.
     */
    public static void warning(String message) {
        print(message, 'W') ;
    }

    /**
     * Writes down a fatal error message. The message is always written.
     */
    public static void fatal(String message) {
        print(message, 'F') ;
    }

    private static void print(String message, char type) {
        synchronized (lock) {
            if (out != null) {
                out.println(type + "[" + getTime() + "] " +
                            Thread.currentThread().getName() +
                            " \"" + message + "\"") ;
                out.flush() ;
            }
        }
    }

    private static String getTime() {
        return DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(new Date()) ;
    }
}
