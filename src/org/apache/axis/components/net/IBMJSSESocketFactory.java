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

import com.ibm.net.ssl.SSLContext;
import org.apache.axis.AxisProperties;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyStore;
import java.security.Security;
import java.util.Hashtable;

/**
 * SSL socket factory. It _requires_ a valid RSA key and
 * JSSE. (borrowed code from tomcat)
 *
 * @author Davanum Srinivas (dims@yahoo.com)
 */
public class IBMJSSESocketFactory extends DefaultSocketFactory {

    /** Field keystoreType           */
    private String keystoreType;

    /** Field defaultKeystoreType           */
    static String defaultKeystoreType = "JKS";

    /** Field defaultProtocol           */
    static String defaultProtocol = "TLS";

    /** Field defaultAlgorithm           */
    static String defaultAlgorithm = "IbmX509";

    /** Field defaultClientAuth           */
    static boolean defaultClientAuth = false;

    /** Field clientAuth           */
    private boolean clientAuth = false;

    /** Field sslFactory           */
    private SSLSocketFactory sslFactory = null;

    /** Field defaultKeystoreFile           */
    static String defaultKeystoreFile =
        System.getProperty("user.home") + "/.keystore";

    /** Field defaultKeyPass           */
    static String defaultKeyPass = "changeit";

    /**
     * Constructor IBMJSSESocketFactory
     *
     * @param attributes
     */
    public IBMJSSESocketFactory(Hashtable attributes) {
        super(attributes);
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
        Socket sslSocket = null;
        if (sslFactory == null) {
            initFactory();
        }
        if (port == -1) {
            port = 443;
        }

        TransportClientProperties tcp = TransportClientPropertiesFactory.create("https");

        boolean hostInNonProxyList = isHostInNonProxyList(host, tcp.getNonProxyHosts());

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
            if (!replyStr.startsWith("HTTP/1.0 200")
                    && !replyStr.startsWith("HTTP/1.1 200")) {
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

    /**
     * Read the keystore, init the SSL socket factory
     *
     * @throws IOException
     */
    protected void initFactory() throws IOException {

        try {
            Security.addProvider(new com.ibm.jsse.JSSEProvider());
            Security.addProvider(new com.ibm.crypto.provider.IBMJCA());

            if(attributes == null) {
                //No configuration specified. Get the default.
                sslFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            } else {
                //Configuration specified in wsdd.
                SSLContext context = getContext();
                sslFactory = context.getSocketFactory();
            }
        } catch (Exception e) {
            if (e instanceof IOException) {
                throw (IOException) e;
            }
            throw new IOException(e.getMessage());
        }
    }

    /**
     * gets a SSL Context
     *
     * @return SSLContext
     * @throws Exception
     */
    protected com.ibm.net.ssl.SSLContext getContext() throws Exception {
        // Please don't change the name of the attribute - other
        // software may depend on it ( j2ee for sure )
        String keystoreFile = (String) attributes.get("keystore");
        if (keystoreFile == null) {
            keystoreFile = defaultKeystoreFile;
        }

        keystoreType = (String) attributes.get("keystoreType");
        if (keystoreType == null) {
            keystoreType = defaultKeystoreType;
        }

        // determine whether we want client authentication
        // the presence of the attribute enables client auth
        clientAuth = null != (String) attributes.get("clientauth");
        String keyPass = (String) attributes.get("keypass");
        if (keyPass == null) {
            keyPass = defaultKeyPass;
        }

        String keystorePass = (String) attributes.get("keystorePass");
        if (keystorePass == null) {
            keystorePass = keyPass;
        }

        // protocol for the SSL ie - TLS, SSL v3 etc.
        String protocol = (String) attributes.get("protocol");
        if (protocol == null) {
            protocol = defaultProtocol;
        }

        // Algorithm used to encode the certificate ie - SunX509
        String algorithm = (String) attributes.get("algorithm");
        if (algorithm == null) {
            algorithm = defaultAlgorithm;
        }

        // You can't use ssl without a server certificate.
        // Create a KeyStore ( to get server certs )
        KeyStore kstore = initKeyStore(keystoreFile, keystorePass);

        // Key manager will extract the server key
        com.ibm.net.ssl.KeyManagerFactory kmf =
                com.ibm.net.ssl.KeyManagerFactory.getInstance(algorithm);

        kmf.init(kstore, keyPass.toCharArray());

        // If client authentication is needed, set up TrustManager
        com.ibm.net.ssl.TrustManager[] tm = null;

        if (clientAuth) {
            com.ibm.net.ssl.TrustManagerFactory tmf =
                    com.ibm.net.ssl.TrustManagerFactory.getInstance("SunX509");

            tmf.init(kstore);
            tm = tmf.getTrustManagers();
        }

        // Create a SSLContext ( to create the ssl factory )
        // This is the only way to use server sockets with JSSE 1.0.1
        com.ibm.net.ssl.SSLContext context =
                com.ibm.net.ssl.SSLContext.getInstance(protocol);    // SSL

        // init context with the key managers
        context.init(kmf.getKeyManagers(), tm,
                new java.security.SecureRandom());
        return context;
    }

    /**
     * intializes a keystore.
     *
     * @param keystoreFile
     * @param keyPass
     *
     * @return keystore
     * @throws IOException
     */
    private KeyStore initKeyStore(String keystoreFile, String keyPass)
            throws IOException {
        try {
            KeyStore kstore = KeyStore.getInstance(keystoreType);

            InputStream istream = new FileInputStream(keystoreFile);
            kstore.load(istream, keyPass.toCharArray());
            return kstore;
        } catch (FileNotFoundException fnfe) {
            throw fnfe;
        } catch (IOException ioe) {
            throw ioe;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new IOException("Exception trying to load keystore "
                    + keystoreFile + ": " + ex.getMessage());
        }
    }
}
