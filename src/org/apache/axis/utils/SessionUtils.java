/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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
package org.apache.axis.utils;

import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

import java.util.Random;

/**
 * Code borrowed from AuthenticatorBase.java for generating a secure id's.
 */
public class SessionUtils {

    /** Field log           */
    protected static Log log = LogFactory.getLog(SessionUtils.class.getName());

    /**
     * The number of random bytes to include when generating a
     * session identifier.
     */
    protected static final int SESSION_ID_BYTES = 16;

    /**
     * A random number generator to use when generating session identifiers.
     */
    protected static Random random = null;

    /**
     * The Java class name of the random number generator class to be used
     * when generating session identifiers.
     */
    protected static String randomClass = "java.security.SecureRandom";

    /**
     * Host name/ip.
     */
    private static String thisHost = null;

    /**
     * Generate and return a new session identifier.
     *
     * @return a new session id
     */
    public static synchronized String generateSessionId() {
        // Generate a byte array containing a session identifier
        byte bytes[] = new byte[SESSION_ID_BYTES];

        getRandom().nextBytes(bytes);

        // Render the result as a String of hexadecimal digits
        StringBuffer result = new StringBuffer();

        for (int i = 0; i < bytes.length; i++) {
            byte b1 = (byte) ((bytes[i] & 0xf0) >> 4);
            byte b2 = (byte) (bytes[i] & 0x0f);

            if (b1 < 10) {
                result.append((char) ('0' + b1));
            } else {
                result.append((char) ('A' + (b1 - 10)));
            }
            if (b2 < 10) {
                result.append((char) ('0' + b2));
            } else {
                result.append((char) ('A' + (b2 - 10)));
            }
        }
        return (result.toString());
    }

    /**
     * Generate and return a new session identifier.
     *
     * @return a new session.
     */
    public static synchronized Long generateSession() {
        return new Long(getRandom().nextLong());
    }

    /**
     * Return the random number generator instance we should use for
     * generating session identifiers.  If there is no such generator
     * currently defined, construct and seed a new one.
     *
     * @return Random object
     */
    private static synchronized Random getRandom() {
        if (random == null) {
            try {
                Class clazz = Class.forName(randomClass);

                random = (Random) clazz.newInstance();
                long seed = System.currentTimeMillis();
                char entropy[] = getEntropy().toCharArray();

                for (int i = 0; i < entropy.length; i++) {
                    long update = ((byte) entropy[i]) << ((i % 8) * 8);

                    seed ^= update;
                }
                random.setSeed(seed);
            } catch (Exception e) {
                random = new java.util.Random();
            }
        }
        return (random);
    }

    /**
     * Method getEntropy
     *
     * @return a unique string
     */
    private static String getEntropy() {
        if (null == thisHost) {
            try {
                thisHost = java.net.InetAddress.getLocalHost().getHostName();
            } catch (java.net.UnknownHostException e) {
                log.error(Messages.getMessage("javaNetUnknownHostException00"),
                        e);
                thisHost = "localhost";
            }
        }
        StringBuffer s = new StringBuffer();

        // Unique string
        s.append(s.hashCode()).append('.').append(System.currentTimeMillis())
                .append(".AXIS@").append(thisHost);
        return s.toString();
    }
}
