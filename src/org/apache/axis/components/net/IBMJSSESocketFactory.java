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

import com.ibm.jsse.JSSEProvider;
import com.ibm.net.ssl.KeyManagerFactory;
import com.ibm.net.ssl.SSLContext;
import com.ibm.net.ssl.TrustManager;
import com.ibm.net.ssl.TrustManagerFactory;
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
public class IBMJSSESocketFactory extends JSSESocketFactory implements SecureSocketFactory {

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
     * Read the keystore, init the SSL socket factory
     *
     * @throws IOException
     */
    protected void initFactory() throws IOException {

        try {
            Security.addProvider(new com.ibm.jsse.JSSEProvider());
            Security.addProvider(new com.ibm.crypto.provider.IBMJCE());

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
    protected SSLContext getContext() throws Exception {
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
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);

        kmf.init(kstore, keyPass.toCharArray());

        // If client authentication is needed, set up TrustManager
        TrustManager[] tm = null;

        if (clientAuth) {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");

            tmf.init(kstore);
            tm = tmf.getTrustManagers();
        }

        // Create a SSLContext ( to create the ssl factory )
        // This is the only way to use server sockets with JSSE 1.0.1
        SSLContext context = SSLContext.getInstance(protocol);    // SSL

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
