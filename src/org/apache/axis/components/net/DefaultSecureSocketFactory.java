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
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.apache.axis.utils.ClassUtils ;
import org.apache.axis.utils.XMLUtils;
import org.apache.axis.AxisFault;


import java.net.Socket;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.lang.reflect.Method;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.lang.reflect.Method;



/**
 * Default socket factory.
 *
 * @author Davanum Srinivas (dims@yahoo.com)
 */
public class DefaultSecureSocketFactory extends DefaultSocketFactory {

    /** Field log           */
    protected static Log log =
            LogFactory.getLog(DefaultSecureSocketFactory.class.getName());

    /** attributes */
    protected Hashtable attributes = null;

    /**
     * Constructor is used only by subclasses.
     *
     * @param attributes
     */
    public DefaultSecureSocketFactory(Hashtable attributes) {
       super(attributes); 
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

        TransportClientProperties tcp = TransportClientPropertiesFactory.create("https");
        Socket sock = null;

        if (port == -1) {
            port = 443;
        }

        // Get https.proxyXXX settings
        String tunnelHost = tcp.getProxyHost();
        String tunnelPortStr = tcp.getProxyPort();
        String nonProxyHosts = tcp.getNonProxyHosts();


        boolean hostInNonProxyList = isHostInNonProxyList(host, nonProxyHosts);

        try {

            // Use java reflection to create a secure socket.
            Class SSLSocketFactoryClass = ClassUtils.forName("javax.net.ssl.SSLSocketFactory");
            Class SSLSocketClass = ClassUtils.forName("javax.net.ssl.SSLSocket");
            Method createSocketMethod =
                    SSLSocketFactoryClass.getMethod("createSocket",
                            new Class[]{String.class,
                                        Integer.TYPE});
            Method getDefaultMethod =
                    SSLSocketFactoryClass.getMethod("getDefault", new Class[]{});
            Method startHandshakeMethod =
                    SSLSocketClass.getMethod("startHandshake", new Class[]{});

            Object factory = getDefaultMethod.invoke(null, new Object[]{});

            Object sslSocket = null;

            if ((tunnelHost == null) || tunnelHost.equals("") || hostInNonProxyList) {
                // direct SSL connection
                sslSocket = createSocketMethod.invoke(factory,
                        new Object[]{host,new Integer(port)});
            } else {
                // SSL tunnelling through proxy server
                Method createSocketMethod2 =
                        SSLSocketFactoryClass.getMethod("createSocket",
                                new Class[]{Socket.class,
                                            String.class,
                                            Integer.TYPE,
                                            Boolean.TYPE});

                // Default proxy port is 80, even for https
                int tunnelPort = ((tunnelPortStr != null)
                        ? ((Integer.parseInt(tunnelPortStr) < 0)
                        ? 80
                        : Integer.parseInt(tunnelPortStr))
                        : 80);

                // Create the regular socket connection to the proxy
                Socket tunnel = new Socket(tunnelHost, tunnelPort);

                // The tunnel handshake method (condensed and made reflexive)
                OutputStream tunnelOutputStream =
                        (OutputStream) SSLSocketClass.getMethod("getOutputStream",
                                new Class[]{}).invoke(tunnel, new Object[]{});
                PrintWriter out = new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(tunnelOutputStream)));


                String tunnelUser = tcp.getProxyUser();
                String tunnelPassword = tcp.getProxyPassword();


                // More secure version... engage later?
                // PasswordAuthentication pa =
                // Authenticator.requestPasswordAuthentication(
                // InetAddress.getByName(tunnelHost),
                // tunnelPort, "SOCK", "Proxy","HTTP");
                // if(pa == null){
                // printDebug("No Authenticator set.");
                // }else{
                // printDebug("Using Authenticator.");
                // tunnelUser = pa.getUserName();
                // tunnelPassword = new String(pa.getPassword());
                // }
                out.print("CONNECT " + host + ":" + port + " HTTP/1.0\r\n"
                        + "User-Agent: AxisClient");
                if ((tunnelUser != null) && (tunnelPassword != null)) {
                    // add basic authentication header for the proxy
                    String encodedPassword =
                            XMLUtils.base64encode((tunnelUser + ":"
                            + tunnelPassword).getBytes());

                    out.print("\nProxy-Authorization: Basic "
                            + encodedPassword);
                }
                out.print("\nContent-Length: 0");
                out.print("\nPragma: no-cache");
                out.print("\r\n\r\n");
                out.flush();
                InputStream tunnelInputStream =
                        (InputStream) SSLSocketClass.getMethod("getInputStream",
                                new Class[]{}).invoke(tunnel, new Object[]{});

                if (log.isDebugEnabled()) {
                    log.debug(Messages.getMessage("isNull00",
                            "tunnelInputStream",
                            "" + (tunnelInputStream
                            == null)));
                }
                String replyStr = "";

                // Make sure to read all the response from the proxy to prevent SSL negotiation failure
                // Response message terminated by two sequential newlines
                int newlinesSeen = 0;
                boolean headerDone = false;    /* Done on first newline */

                while (newlinesSeen < 2) {
                    int i = tunnelInputStream.read();

                    if (i < 0) {
                        throw new IOException("Unexpected EOF from proxy");
                    }
                    if (i == '\n') {
                        headerDone = true;
                        ++newlinesSeen;
                    } else if (i != '\r') {
                        newlinesSeen = 0;
                        if (!headerDone) {
                            replyStr += String.valueOf((char) i);
                        }
                    }
                }
                if (!replyStr.startsWith("HTTP/1.0 200")
                        && !replyStr.startsWith("HTTP/1.1 200")) {
                    throw new IOException(Messages.getMessage("cantTunnel00",
                            new String[]{
                                tunnelHost,
                                "" + tunnelPort,
                                replyStr}));
                }

                // End of condensed reflective tunnel handshake method
                sslSocket = createSocketMethod2.invoke(factory,
                        new Object[]{tunnel,
                                     host,
                                     new Integer(port),
                                     new Boolean(true)});
                if (log.isDebugEnabled()) {
                    log.debug(Messages.getMessage("setupTunnel00", tunnelHost,
                            "" + tunnelPort));
                }
            }

            // must shake out hidden errors!
            startHandshakeMethod.invoke(sslSocket, new Object[]{
            });
            sock = (Socket) sslSocket;
        } catch (ClassNotFoundException cnfe) {
            if (log.isDebugEnabled()) {
                log.debug(Messages.getMessage("noJSSE00"));
            }
            throw AxisFault.makeFault(cnfe);
        } catch (NumberFormatException nfe) {
            if (log.isDebugEnabled()) {
                log.debug(Messages.getMessage("badProxy00", tunnelPortStr));
            }
            throw AxisFault.makeFault(nfe);
        }
        if (log.isDebugEnabled()) {
            log.debug(Messages.getMessage("createdSSL00"));
        }
        return sock;

    }
}
