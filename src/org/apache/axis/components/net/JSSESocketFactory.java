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

import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.apache.axis.utils.StringUtils;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Hashtable;


/**
 * SSL socket factory. It _requires_ a valid RSA key and
 * JSSE. (borrowed code from tomcat)
 * 
 * THIS CODE STILL HAS DEPENDENCIES ON sun.* and com.sun.*
 *
 * @author Davanum Srinivas (dims@yahoo.com)
 */
public class JSSESocketFactory extends DefaultSocketFactory implements SecureSocketFactory {

    /** Field sslFactory           */
    protected SSLSocketFactory sslFactory = null;

    /**
     * Constructor JSSESocketFactory
     *
     * @param attributes
     */
    public JSSESocketFactory(Hashtable attributes) {
        super(attributes);
    }

    /**
     * Initialize the SSLSocketFactory
     * @throws IOException
     */ 
    protected void initFactory() throws IOException {
        sslFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
    }
    
    /**
     * creates a secure socket
     *
     * @param host
     * @param port
     * @param otherHeaders
     * @param useFullURL
     *
     * @return Socket
     * @throws Exception
     */
    public Socket create(
            String host, int port, StringBuffer otherHeaders, BooleanHolder useFullURL)
            throws Exception {
        if (sslFactory == null) {
            initFactory();
        }
        if (port == -1) {
            port = 443;
        }

        TransportClientProperties tcp = TransportClientPropertiesFactory.create("https");

        boolean hostInNonProxyList = isHostInNonProxyList(host, tcp.getNonProxyHosts());

        Socket sslSocket = null;
        if (tcp.getProxyHost().length() == 0 || hostInNonProxyList) {
            // direct SSL connection
            sslSocket = sslFactory.createSocket(host, port);
        } else {

            // Default proxy port is 80, even for https
            int tunnelPort = (tcp.getProxyPort().length() != 0)
                             ? Integer.parseInt(tcp.getProxyPort())
                             : 80;
            if (tunnelPort < 0)
                tunnelPort = 80;

            // Create the regular socket connection to the proxy
            Socket tunnel = new Socket(tcp.getProxyHost(), tunnelPort);

            // The tunnel handshake method (condensed and made reflexive)
            OutputStream tunnelOutputStream = tunnel.getOutputStream();
            PrintWriter out = new PrintWriter(
                    new BufferedWriter(new OutputStreamWriter(tunnelOutputStream)));

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
            if (tcp.getProxyUser().length() != 0 &&
                tcp.getProxyPassword().length() != 0) {

                // add basic authentication header for the proxy
                String encodedPassword = XMLUtils.base64encode((tcp.getProxyUser()
                        + ":"
                        + tcp.getProxyPassword()).getBytes());

                out.print("\nProxy-Authorization: Basic " + encodedPassword);
            }
            out.print("\nContent-Length: 0");
            out.print("\nPragma: no-cache");
            out.print("\r\n\r\n");
            out.flush();
            InputStream tunnelInputStream = tunnel.getInputStream();

            if (log.isDebugEnabled()) {
                log.debug(Messages.getMessage("isNull00", "tunnelInputStream",
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
            if (StringUtils.startsWithIgnoreWhitespaces("HTTP/1.0 200", replyStr) &&
                    StringUtils.startsWithIgnoreWhitespaces("HTTP/1.1 200", replyStr)) {
                throw new IOException(Messages.getMessage("cantTunnel00",
                        new String[]{
                            tcp.getProxyHost(),
                            "" + tunnelPort,
                            replyStr}));
            }

            // End of condensed reflective tunnel handshake method
            sslSocket = sslFactory.createSocket(tunnel, host, port, true);
            if (log.isDebugEnabled()) {
                log.debug(Messages.getMessage("setupTunnel00",
                          tcp.getProxyHost(),
                        "" + tunnelPort));
            }
        }

        ((SSLSocket) sslSocket).startHandshake();
        if (log.isDebugEnabled()) {
            log.debug(Messages.getMessage("createdSSL00"));
        }
        return sslSocket;
    }
}
