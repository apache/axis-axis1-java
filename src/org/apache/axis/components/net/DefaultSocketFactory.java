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
 *    Apache Software Foundation (http://www.apache.org/)."
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
package org.apache.axis.components.net;

import org.apache.axis.AxisProperties;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.Base64;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.utils.JavaUtils;
import org.apache.commons.logging.Log;

import java.net.Socket;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * Default socket factory.
 *
 * @author Davanum Srinivas (dims@yahoo.com)
 */
public class DefaultSocketFactory implements SocketFactory {

    /** Field log           */
    protected static Log log =
            LogFactory.getLog(DefaultSocketFactory.class.getName());

    /** attributes */
    protected Hashtable attributes = null;

    /**
     * Constructor is used only by subclasses.
     *
     * @param attributes
     */
    public DefaultSocketFactory(Hashtable attributes) {
        this.attributes = attributes;
    }

    /**
     * Creates a socket.
     *
     * @param host
     * @param port
     * @param otherHeaders
     * @param useFullURL
     *
     * @return Socket
     *
     * @throws Exception
     */
    public Socket create(
            String host, int port, StringBuffer otherHeaders, BooleanHolder useFullURL)
            throws Exception {

        Socket sock = null;
        String proxyHost = getGlobalProperty("http.proxyHost");
        String proxyPort = getGlobalProperty("http.proxyPort");
        String nonProxyHosts = getGlobalProperty("http.nonProxyHosts");
        boolean hostInNonProxyList = isHostInNonProxyList(host, nonProxyHosts);
        String proxyUsername = getGlobalProperty("http.proxyUser");
        String proxyPassword = getGlobalProperty("http.proxyPassword");

        if (proxyUsername != null) {
            StringBuffer tmpBuf = new StringBuffer();

            tmpBuf.append(proxyUsername).append(":").append((proxyPassword
                    == null)
                    ? ""
                    : proxyPassword);
            otherHeaders.append(HTTPConstants.HEADER_PROXY_AUTHORIZATION)
                    .append(": Basic ")
                    .append(Base64.encode(tmpBuf.toString().getBytes()))
                    .append("\r\n");
        }
        if (port == -1) {
            port = 80;
        }
        if ((proxyHost == null) || proxyHost.equals("") || (proxyPort == null)
                || proxyPort.equals("") || hostInNonProxyList) {
            sock = new Socket(host, port);
            if (log.isDebugEnabled()) {
                log.debug(JavaUtils.getMessage("createdHTTP00"));
            }
        } else {
            sock = new Socket(proxyHost, new Integer(proxyPort).intValue());
            if (log.isDebugEnabled()) {
                log.debug(JavaUtils.getMessage("createdHTTP01", proxyHost,
                        proxyPort));
            }
            useFullURL.value = true;
        }
        return sock;
    }

    /**
     * Method getGlobalProperty
     *
     * @param property
     *
     * @return
     */
    protected String getGlobalProperty(String property) {
        return AxisProperties.getGlobalProperty(property);
    }

    /**
     * Check if the specified host is in the list of non proxy hosts.
     *
     * @param host host name
     * @param nonProxyHosts string containing the list of non proxy hosts
     *
     * @return true/false
     */
    protected boolean isHostInNonProxyList(String host, String nonProxyHosts) {

        if ((nonProxyHosts == null) || (host == null)) {
            return false;
        }

        /*
         * The http.nonProxyHosts system property is a list enclosed in
         * double quotes with items separated by a vertical bar.
         */
        StringTokenizer tokenizer = new StringTokenizer(nonProxyHosts, "|\"");

        while (tokenizer.hasMoreTokens()) {
            String pattern = tokenizer.nextToken();

            if (log.isDebugEnabled()) {
                log.debug(JavaUtils.getMessage("match00",
                        new String[]{"HTTPSender",
                                     host,
                                     pattern}));
            }
            if (match(pattern, host, false)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Matches a string against a pattern. The pattern contains two special
     * characters:
     * '*' which means zero or more characters,
     *
     * @param pattern the (non-null) pattern to match against
     * @param str     the (non-null) string that must be matched against the
     *                pattern
     * @param isCaseSensitive
     *
     * @return <code>true</code> when the string matches against the pattern,
     *         <code>false</code> otherwise.
     */
    protected static boolean match(String pattern, String str,
                                   boolean isCaseSensitive) {

        char[] patArr = pattern.toCharArray();
        char[] strArr = str.toCharArray();
        int patIdxStart = 0;
        int patIdxEnd = patArr.length - 1;
        int strIdxStart = 0;
        int strIdxEnd = strArr.length - 1;
        char ch;
        boolean containsStar = false;

        for (int i = 0; i < patArr.length; i++) {
            if (patArr[i] == '*') {
                containsStar = true;
                break;
            }
        }
        if (!containsStar) {

            // No '*'s, so we make a shortcut
            if (patIdxEnd != strIdxEnd) {
                return false;        // Pattern and string do not have the same size
            }
            for (int i = 0; i <= patIdxEnd; i++) {
                ch = patArr[i];
                if (isCaseSensitive && (ch != strArr[i])) {
                    return false;    // Character mismatch
                }
                if (!isCaseSensitive
                        && (Character.toUpperCase(ch)
                        != Character.toUpperCase(strArr[i]))) {
                    return false;    // Character mismatch
                }
            }
            return true;             // String matches against pattern
        }
        if (patIdxEnd == 0) {
            return true;    // Pattern contains only '*', which matches anything
        }

        // Process characters before first star
        while ((ch = patArr[patIdxStart]) != '*'
                && (strIdxStart <= strIdxEnd)) {
            if (isCaseSensitive && (ch != strArr[strIdxStart])) {
                return false;    // Character mismatch
            }
            if (!isCaseSensitive
                    && (Character.toUpperCase(ch)
                    != Character.toUpperCase(strArr[strIdxStart]))) {
                return false;    // Character mismatch
            }
            patIdxStart++;
            strIdxStart++;
        }
        if (strIdxStart > strIdxEnd) {

            // All characters in the string are used. Check if only '*'s are
            // left in the pattern. If so, we succeeded. Otherwise failure.
            for (int i = patIdxStart; i <= patIdxEnd; i++) {
                if (patArr[i] != '*') {
                    return false;
                }
            }
            return true;
        }

        // Process characters after last star
        while ((ch = patArr[patIdxEnd]) != '*' && (strIdxStart <= strIdxEnd)) {
            if (isCaseSensitive && (ch != strArr[strIdxEnd])) {
                return false;    // Character mismatch
            }
            if (!isCaseSensitive
                    && (Character.toUpperCase(ch)
                    != Character.toUpperCase(strArr[strIdxEnd]))) {
                return false;    // Character mismatch
            }
            patIdxEnd--;
            strIdxEnd--;
        }
        if (strIdxStart > strIdxEnd) {

            // All characters in the string are used. Check if only '*'s are
            // left in the pattern. If so, we succeeded. Otherwise failure.
            for (int i = patIdxStart; i <= patIdxEnd; i++) {
                if (patArr[i] != '*') {
                    return false;
                }
            }
            return true;
        }

        // process pattern between stars. padIdxStart and patIdxEnd point
        // always to a '*'.
        while ((patIdxStart != patIdxEnd) && (strIdxStart <= strIdxEnd)) {
            int patIdxTmp = -1;

            for (int i = patIdxStart + 1; i <= patIdxEnd; i++) {
                if (patArr[i] == '*') {
                    patIdxTmp = i;
                    break;
                }
            }
            if (patIdxTmp == patIdxStart + 1) {

                // Two stars next to each other, skip the first one.
                patIdxStart++;
                continue;
            }

            // Find the pattern between padIdxStart & padIdxTmp in str between
            // strIdxStart & strIdxEnd
            int patLength = (patIdxTmp - patIdxStart - 1);
            int strLength = (strIdxEnd - strIdxStart + 1);
            int foundIdx = -1;

            strLoop:
            for (int i = 0; i <= strLength - patLength; i++) {
                for (int j = 0; j < patLength; j++) {
                    ch = patArr[patIdxStart + j + 1];
                    if (isCaseSensitive
                            && (ch != strArr[strIdxStart + i + j])) {
                        continue strLoop;
                    }
                    if (!isCaseSensitive && (Character
                            .toUpperCase(ch) != Character
                            .toUpperCase(strArr[strIdxStart + i + j]))) {
                        continue strLoop;
                    }
                }
                foundIdx = strIdxStart + i;
                break;
            }
            if (foundIdx == -1) {
                return false;
            }
            patIdxStart = patIdxTmp;
            strIdxStart = foundIdx + patLength;
        }

        // All characters in the string are used. Check if only '*'s are left
        // in the pattern. If so, we succeeded. Otherwise failure.
        for (int i = patIdxStart; i <= patIdxEnd; i++) {
            if (patArr[i] != '*') {
                return false;
            }
        }
        return true;
    }
}
