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
package org.apache.axis.components.net;

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.Base64;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

import java.net.Socket;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Default socket factory.
 *
 * @author Davanum Srinivas (dims@yahoo.com)
 */
public class DefaultSocketFactory implements SocketFactory {

    /** Field log           */
    protected static Log log =
            LogFactory.getLog(DefaultSocketFactory.class.getName());

    /** Field CONNECT_TIMEOUT */
    public static String CONNECT_TIMEOUT = "axis.client.connect.timeout";
    
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

        int timeout = 0;
        if(attributes.contains(CONNECT_TIMEOUT)) {
            timeout = Integer.parseInt((String)attributes.get(CONNECT_TIMEOUT));
        }
        TransportClientProperties tcp = TransportClientPropertiesFactory.create("http");

        Socket sock = null;
        boolean hostInNonProxyList = isHostInNonProxyList(host, tcp.getNonProxyHosts());

        if (tcp.getProxyUser().length() != 0) {
            StringBuffer tmpBuf = new StringBuffer();

            tmpBuf.append(tcp.getProxyUser())
                  .append(":")
                  .append(tcp.getProxyPassword());
            otherHeaders.append(HTTPConstants.HEADER_PROXY_AUTHORIZATION)
                        .append(": Basic ")
                        .append(Base64.encode(tmpBuf.toString().getBytes()))
                        .append("\r\n");
        }
        if (port == -1) {
            port = 80;
        }
        if ((tcp.getProxyHost().length() == 0) ||
            (tcp.getProxyPort().length() == 0) ||
            hostInNonProxyList)
        {
            sock = create(host, port, timeout);
            if (log.isDebugEnabled()) {
                log.debug(Messages.getMessage("createdHTTP00"));
            }
        } else {
            sock = create(tcp.getProxyHost(),
                    new Integer(tcp.getProxyPort()).intValue(),
                    timeout);
            if (log.isDebugEnabled()) {
                log.debug(Messages.getMessage("createdHTTP01", tcp.getProxyHost(),
                          tcp.getProxyPort()));
            }
            useFullURL.value = true;
        }
        return sock;
    }

    /**
     * Creates a socket with connect timeout using reflection API
     *  
     * @param host
     * @param port
     * @param timeout
     * @return
     * @throws Exception
     */ 
    private static Socket create(String host, int port, int timeout) throws Exception {
        boolean plain = true;
        Socket sock = null;
        try {
            Class clazz = Class.forName("java.net.InetSocketAddress");
            plain = false;
            Constructor constructor = clazz.getConstructor(new Class[]{String.class, int.class});
            Object address = constructor.newInstance(new Object[]{host, new Integer(port)});
            sock = (Socket) Socket.class.getConstructor(new Class[]{}).newInstance(new Object[]{});
            Method connect = sock.getClass().getMethod("connect", new Class[]{clazz.getSuperclass(), int.class});
            connect.invoke(sock, new Object[]{address, new Integer(timeout)});
        } catch (Exception e) {
            if (plain) {
                sock = new Socket(host, port);
            } else {
                throw e;
            }
        }
        return sock;
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
                log.debug(Messages.getMessage("match00",
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
