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
package org.apache.axis.transport.http;

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.AxisInternalServices;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.Base64;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;

import javax.xml.soap.SOAPException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * This is meant to be used on a SOAP Client to call a SOAP server.
 *
 * @author Doug Davis (dug@us.ibm.com)
 * @author Davanum Srinivas (dims@yahoo.com)
 */
public class HTTPSender extends BasicHandler {

    protected static Log log = AxisInternalServices.getLog(HTTPSender.class.getName());

    /** Hook for creating a different SSL socket factory
     * XXX The whole thing can be refactored to use something like tomcat.util, which
     *  is cleaner and support PureTLS.
     */
    public static interface SocketFactoryFactory {

        /** Returns an instance of SSLSocketFactory, possibly with
         *  different parameters ( like different trust policies )
         */
        public Object createFactory() throws Exception;
    }


    /**
     * Utility Class BooleanHolder
     */
    static class BooleanHolder {
        public boolean value;
        public BooleanHolder(boolean value) {
            this.value = value;
        }
    }

    /**
     * invoke creates a socket connection, sends the request SOAP message and then
     * reads the response SOAP message back from the SOAP server
     *
     * @param msgContext the messsage context
     *
     * @throws AxisFault
     */
    public void invoke(MessageContext msgContext) throws AxisFault {

        if (log.isDebugEnabled()) {
            log.debug(JavaUtils.getMessage("enter00", "HTTPSender::invoke"));
        }
        try {
            BooleanHolder useFullURL = new BooleanHolder(false);
            StringBuffer otherHeaders = new StringBuffer();
            URL targetURL = new URL(msgContext.getStrProp(MessageContext.TRANS_URL));
            String host = targetURL.getHost();
            int port = targetURL.getPort();
            Socket sock = null;

            try {

                // create socket based on the url protocol type
                if (targetURL.getProtocol().equalsIgnoreCase("https")) {
                    sock = getSecureSocket(host, port);
                } else {
                    sock = getSocket(host, port, otherHeaders, useFullURL);
                }

                // optionally set a timeout for the request
                if (msgContext.getTimeout() != 0) {
                    sock.setSoTimeout(msgContext.getTimeout());
                }

                // Send the SOAP request to the server
                writeToSocket(sock, msgContext, targetURL,
                              otherHeaders, host, port, useFullURL);
            } finally {
                // FIXME (DIMS): IS THIS REALLY NEEDED? SalesRankNPrice fails
                // for a direct (non-proxy) connection if this is enabled.
                //if(null != sock) sock.shutdownOutput(); //need to change for http 1.1
            }

            // Read the response back from the server
            readFromSocket(sock, msgContext);
        } catch (Exception e) {
            log.debug(e);
            throw AxisFault.makeFault(e);
        }
        if (log.isDebugEnabled()) {
            log.debug(JavaUtils.getMessage("exit00",
                    "HTTPDispatchHandler::invoke"));
        }
    }

    /**
     * getSecureSocket is used when we need a secure SSL connection to the SOAP Server
     *
     * @param host host name
     * @param port port that we need to connect to
     *
     * @return a secure socket
     *
     * @throws Exception
     */
    private Socket getSecureSocket(String host, int port) throws Exception {
        Socket sock = null;

        if (port == -1) {
            port = 443;
        }

        // Get https.proxyXXX settings
        String tunnelHost = getGlobalProperty("https.proxyHost");
        String tunnelPortStr = getGlobalProperty("https.proxyPort");
        String nonProxyHosts = getGlobalProperty("https.nonProxyHosts");

        // Use http.proxyXXX settings if https.proxyXXX is not set
        if (tunnelHost == null) {
            tunnelHost = getGlobalProperty("http.proxyHost");
        }
        if (tunnelPortStr == null) {
            tunnelPortStr = getGlobalProperty("http.proxyPort");
        }
        if (nonProxyHosts == null) {
            nonProxyHosts = getGlobalProperty("http.nonProxyHosts");
        }

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

            Object factory = null;

            // Hook in a different SSL socket factory
            String socketFactoryClass = getGlobalProperty("axis.socketFactory");
            if (socketFactoryClass != null) {
                try {
                    Class c1 = ClassUtils.forName(socketFactoryClass);
                    SocketFactoryFactory sff = (SocketFactoryFactory) c1.newInstance();
                    factory = sff.createFactory();
                    if (log.isDebugEnabled()) {
                        log.debug("Created socket factory " + sff.getClass().getName());
                    }
                } catch (Exception ex) {
                }
            }


            if (factory == null)
                factory = getDefaultMethod.invoke(null, new Object[]{});
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
                String tunnelUser = getGlobalProperty("https.proxyUser");
                String tunnelPassword = getGlobalProperty("https.proxyPassword");

                if (tunnelUser == null) {
                    tunnelUser = getGlobalProperty("http.proxyUser");
                }
                if (tunnelPassword == null) {
                    tunnelPassword = getGlobalProperty("http.proxyPassword");
                }

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
                    log.debug(JavaUtils.getMessage("isNull00",
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
                    throw new IOException(JavaUtils.getMessage("cantTunnel00",
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
                    log.debug(JavaUtils.getMessage("setupTunnel00", tunnelHost,
                            "" + tunnelPort));
                }
            }

            // must shake out hidden errors!
            startHandshakeMethod.invoke(sslSocket, new Object[]{
            });
            sock = (Socket) sslSocket;
        } catch (ClassNotFoundException cnfe) {
            if (log.isDebugEnabled()) {
                log.debug(JavaUtils.getMessage("noJSSE00"));
            }
            throw AxisFault.makeFault(cnfe);
        } catch (NumberFormatException nfe) {
            if (log.isDebugEnabled()) {
                log.debug(JavaUtils.getMessage("badProxy00", tunnelPortStr));
            }
            throw AxisFault.makeFault(nfe);
        }
        if (log.isDebugEnabled()) {
            log.debug(JavaUtils.getMessage("createdSSL00"));
        }
        return sock;
    }

    /**
     * Creates a non-ssl socket connection to the SOAP server
     *
     * @param host host name
     * @param port port to connect to
     * @param otherHeaders buffer for storing additional headers that need to be sent
     * @param useFullURL flag to indicate if the complete URL has to be sent
     *
     * @return the socket
     *
     * @throws IOException
     */
    private Socket getSocket(
            String host, int port, StringBuffer otherHeaders, BooleanHolder useFullURL)
            throws IOException {
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
     * Send the soap request message to the server
     *
     * @param sock socket
     * @param msgContext message context
     * @param tmpURL url to connect to
     * @param otherHeaders other headers if any
     * @param host host name
     * @param port port
     * @param useFullURL flag to indicate if the whole url needs to be sent
     *
     * @throws IOException
     */
    private void writeToSocket(
            Socket sock, MessageContext msgContext, URL tmpURL,
            StringBuffer otherHeaders, String host, int port,
            BooleanHolder useFullURL)
            throws IOException {

        String userID = null;
        String passwd = null;
        String reqEnv = null;

        userID = msgContext.getUsername();
        passwd = msgContext.getPassword();

        // Get SOAPAction, default to ""
        String action = msgContext.useSOAPAction()
                ? msgContext.getSOAPActionURI()
                : "";

        if (action == null) {
            action = "";
        }

        // if UserID is not part of the context, but is in the URL, use
        // the one in the URL.
        if ((userID == null) && (tmpURL.getUserInfo() != null)) {
            String info = tmpURL.getUserInfo();
            int sep = info.indexOf(':');

            if ((sep >= 0) && (sep + 1 < info.length())) {
                userID = info.substring(0, sep);
                passwd = info.substring(sep + 1);
            } else {
                userID = info;
            }
        }
        if (userID != null) {
            StringBuffer tmpBuf = new StringBuffer();

            tmpBuf.append(userID).append(":").append((passwd == null)
                    ? ""
                    : passwd);
            otherHeaders.append(HTTPConstants.HEADER_AUTHORIZATION)
                    .append(": Basic ")
                    .append(Base64.encode(tmpBuf.toString().getBytes()))
                    .append("\r\n");
        }

        // don't forget the cookies!
        // mmm... cookies
        if (msgContext.getMaintainSession()) {
            String cookie = msgContext.getStrProp(HTTPConstants.HEADER_COOKIE);
            String cookie2 = msgContext.getStrProp(HTTPConstants.HEADER_COOKIE2);

            if (cookie != null) {
                otherHeaders.append(HTTPConstants.HEADER_COOKIE).append(": ")
                        .append(cookie).append("\r\n");
            }
            if (cookie2 != null) {
                otherHeaders.append(HTTPConstants.HEADER_COOKIE2).append(": ")
                        .append(cookie2).append("\r\n");
            }
        }
        StringBuffer header = new StringBuffer();

        // byte[] request = reqEnv.getBytes();
        header.append(HTTPConstants.HEADER_POST).append(" ");
        if (useFullURL.value) {
            header.append(tmpURL.toExternalForm());
        } else {
            header.append((((tmpURL.getFile() == null)
                    || tmpURL.getFile().equals(""))
                    ? "/"
                    : tmpURL.getFile()));
        }
        Message reqMessage = msgContext.getRequestMessage();

        boolean http10= true;
        boolean httpChunkStream= false;
        String httpConnection= null;

        String httpver= msgContext.getStrProp(MessageContext.HTTP_TRANSPORT_VERSION);
        if( null == httpver) httpver= HTTPConstants.HEADER_PROTOCOL_V10;
        httpver=httpver.trim();
        if(httpver.equals( HTTPConstants.HEADER_PROTOCOL_V11)){
            http10= false;
        }

        //process user defined headers for information. 
        Hashtable userHeaderTable = (Hashtable) msgContext.
             getProperty(HTTPConstants.REQUEST_HEADERS);

        if(userHeaderTable != null) {
            if(null== otherHeaders) otherHeaders= new StringBuffer(1024);

            for (java.util.Iterator e = userHeaderTable.entrySet().iterator();
                  e.hasNext();) {

                java.util.Map.Entry me= (java.util.Map.Entry)e.next();
                Object keyObj= me.getKey();
                if(null == keyObj) continue;
                String key= keyObj.toString().trim();

                if(key.equalsIgnoreCase(HTTPConstants.HEADER_TRANSFER_ENCODING)){
                    if(!http10){
                      String val=  me.getValue().toString();
                      if(null != val && val.trim().equalsIgnoreCase(HTTPConstants.HEADER_TRANSFER_ENCODING_CHUNKED))
                          httpChunkStream= true; 
                    }
                }
                else if(key.equalsIgnoreCase(HTTPConstants.HEADER_HOST)){
                  //ignore
                }
                else if(key.equalsIgnoreCase(HTTPConstants.HEADER_CONTENT_TYPE)){
                  //ignore
                }
                else if(key.equalsIgnoreCase(HTTPConstants.HEADER_SOAP_ACTION)){
                  //ignore
                }
                else if(key.equalsIgnoreCase(HTTPConstants.HEADER_CONTENT_LENGTH)){
                  //ignore
                }
                else if(key.equalsIgnoreCase(HTTPConstants.HEADER_COOKIE)){
                  //ignore
                }
                else if(key.equalsIgnoreCase(HTTPConstants.HEADER_COOKIE2)){
                  //ignore
                }
                else if(key.equalsIgnoreCase(HTTPConstants.HEADER_AUTHORIZATION)){
                  //ignore
                }
                else if(key.equalsIgnoreCase(HTTPConstants.HEADER_PROXY_AUTHORIZATION)){
                  //ignore
                }
                else if(key.equalsIgnoreCase(HTTPConstants.HEADER_CONNECTION)){
                    if(!http10) {
                      String val= me.getValue().toString();
                      if(val.trim().equalsIgnoreCase(HTTPConstants.HEADER_CONNECTION_CLOSE))
                          httpConnection= HTTPConstants.HEADER_CONNECTION_CLOSE; 
                    }
                    //HTTP 1.0 will always close.
                    //HTTP 1.1 will use persistent. //no need to specify 
                }
                else{
                    otherHeaders.append(key).append(": ").append(me.getValue()).append("\r\n");
                }
            }
        }

if(!http10)
   httpConnection= HTTPConstants.HEADER_CONNECTION_CLOSE; //Force close for now.

        header.append(" ");
        header.append(http10 ? HTTPConstants.HEADER_PROTOCOL_10 :
                     HTTPConstants.HEADER_PROTOCOL_11) 
                .append("\r\n")
                .append(HTTPConstants.HEADER_HOST)
                .append(": ")
                .append(host)
                .append((port==-1)?(""):(":"+port))
                .append("\r\n")
                .append(HTTPConstants.HEADER_CONTENT_TYPE)
                .append(": ")
                .append(reqMessage.getContentType())
                .append("\r\n")
                .append(HTTPConstants.HEADER_SOAP_ACTION)
                .append(": \"")
                .append(action)
                .append("\"\r\n");

        if(!httpChunkStream ){
            //Content length MUST be sent on HTTP 1.0 requests.
            header.append(HTTPConstants.HEADER_CONTENT_LENGTH)
                  .append(": ")
                  .append(reqMessage.getContentLength())
                  .append("\r\n");
         }
         else{
            //Do http chunking.
            header.append(HTTPConstants.HEADER_TRANSFER_ENCODING )
            .append(": " )
            .append(HTTPConstants.HEADER_TRANSFER_ENCODING_CHUNKED)
            .append("\r\n" );
         }

        if(null != httpConnection){
           header.append(HTTPConstants.HEADER_CONNECTION); 
           header.append(": " );
           header.append(httpConnection); 
           header.append("\r\n" );
        }

        if(null != otherHeaders)
            header.append(otherHeaders); //Add other headers to the end.

        header.append("\r\n"); //The empty line to start the BODY.

        OutputStream out = sock.getOutputStream();

        out.write(header.toString()
                .getBytes(HTTPConstants.HEADER_DEFAULT_CHAR_ENCODING));
        out.flush();        
        ChunkedOutputStream chunkedOutputStream= null; 
        if(httpChunkStream){
            out= chunkedOutputStream=  new ChunkedOutputStream(out);
        }

        out = new BufferedOutputStream(out, 8 * 1024);
        try {
            reqMessage.writeTo(out);
        } catch (SOAPException e) {
            log.error(JavaUtils.getMessage("exception00"), e);
        }
        if(null != chunkedOutputStream){
            out.flush();
            chunkedOutputStream.eos();
        }
        out.flush();
        if (log.isDebugEnabled()) {
            log.debug(JavaUtils.getMessage("xmlSent00"));
            log.debug("---------------------------------------------------");
            log.debug(header + reqEnv);
        }
    }

    /**
     * Reads the SOAP response back from the server
     *
     * @param sock socket
     * @param msgContext message context
     *
     * @throws IOException
     */
    private void readFromSocket(Socket sock, MessageContext msgContext)
            throws IOException {
        Message outMsg = null;
        byte b;
        int len = 0;
        int colonIndex = -1;
        Hashtable headers = new Hashtable();
        String name, value;
        String statusMessage = "";
        int returnCode = 0;
        InputStream inp = new BufferedInputStream(sock.getInputStream());

        // Should help performance. Temporary fix only till its all stream oriented.
        // Need to add logic for getting the version # and the return code
        // but that's for tomorrow!

        /* Logic to read HTTP response headers */
        boolean readTooMuch = false;

        b = 0;
        for (ByteArrayOutputStream buf = new ByteArrayOutputStream(4097); ;) {
            if (!readTooMuch) {
                b = (byte) inp.read();
            }
            if (b == -1) {
                break;
            }
            readTooMuch = false;
            if ((b != '\r') && (b != '\n')) {
                if ((b == ':') && (colonIndex == -1)) {
                    colonIndex = len;
                }
                len++;
                buf.write(b);
            } else if (b == '\r') {
                continue;
            } else {    // b== '\n'
                if (len == 0) {
                    break;
                }
                b = (byte) inp.read();
                readTooMuch = true;

                // A space or tab at the begining of a line means the header continues.
                if ((b == ' ') || (b == '\t')) {
                    continue;
                }
                buf.close();
                byte[] hdata = buf.toByteArray();
                buf.reset();
                if (colonIndex != -1) {
                    name =
                            new String(hdata, 0, colonIndex,
                                    HTTPConstants.HEADER_DEFAULT_CHAR_ENCODING);
                    value =
                            new String(hdata, colonIndex + 1, len - 1 - colonIndex,
                                    HTTPConstants.HEADER_DEFAULT_CHAR_ENCODING);
                    colonIndex = -1;
                } else {

                    name =
                            new String(hdata, 0, len,
                                    HTTPConstants.HEADER_DEFAULT_CHAR_ENCODING);
                    value = "";
                }
                if (log.isDebugEnabled()) {
                    log.debug(name + value);
                }
                if (msgContext.getProperty(HTTPConstants.MC_HTTP_STATUS_CODE)
                        == null) {

                    // Reader status code
                    int start = name.indexOf(' ') + 1;
                    String tmp = name.substring(start).trim();
                    int end = tmp.indexOf(' ');

                    if (end != -1) {
                        tmp = tmp.substring(0, end);
                    }
                    returnCode = Integer.parseInt(tmp);
                    msgContext.setProperty(HTTPConstants.MC_HTTP_STATUS_CODE,
                            new Integer(returnCode));
                    statusMessage = name.substring(start + end + 1);
                    msgContext.setProperty(HTTPConstants.MC_HTTP_STATUS_MESSAGE,
                            statusMessage);
                } else {
                    headers.put(name.toLowerCase(), value);
                }
                len = 0;
            }
        }

        /* All HTTP headers have been read. */
        String contentType =
                (String) headers
                .get(HTTPConstants.HEADER_CONTENT_TYPE.toLowerCase());

        contentType = (null == contentType)
                ? null
                : contentType.trim();
        if ((returnCode > 199) && (returnCode < 300)) {
            // SOAP return is OK - so fall through
        } else if ((contentType != null) && !contentType.equals("text/html")
                && ((returnCode > 499) && (returnCode < 600))) {
            // SOAP Fault should be in here - so fall through
        } else {
            // Unknown return code - so wrap up the content into a
            // SOAP Fault.
            ByteArrayOutputStream buf = new ByteArrayOutputStream(4097);

            while (-1 != (b = (byte) inp.read())) {
                buf.write(b);
            }
            AxisFault fault = new AxisFault("HTTP", "(" + returnCode + ")" + statusMessage, null, null);

            fault.setFaultDetailString(JavaUtils.getMessage("return01",
                    "" + returnCode, buf.toString()));
            throw fault;
        }
        if (b != -1) {    // more data than just headers.
            String contentLocation =
                    (String) headers
                    .get(HTTPConstants.HEADER_CONTENT_LOCATION.toLowerCase());

            contentLocation = (null == contentLocation)
                    ? null
                    : contentLocation.trim();

            String contentLength =
                    (String) headers
                    .get(HTTPConstants.HEADER_CONTENT_LENGTH.toLowerCase());

            contentLength = (null == contentLength)
                    ? null
                    : contentLength.trim();

            String transferEncoding =
                    (String) headers
                    .get(HTTPConstants.HEADER_TRANSFER_ENCODING.toLowerCase());
            if(null != transferEncoding
                  && transferEncoding.trim()
                    .equals(HTTPConstants.HEADER_TRANSFER_ENCODING_CHUNKED )){
                 inp= new ChunkedInputStream(inp); 
            }

                    
            outMsg = new Message(inp, false, contentType,
                    contentLocation);
            outMsg.setMessageType(Message.RESPONSE);
            msgContext.setResponseMessage(outMsg);
            if (log.isDebugEnabled()) {
                if (null == contentLength) {
                    log.debug("\n"
                            + JavaUtils.getMessage("no00", "Content-Length"));
                }
                log.debug("\n" + JavaUtils.getMessage("xmlRecd00"));
                log.debug("-----------------------------------------------");
                log.debug((String) outMsg.getSOAPPartAsString());
            }
        }

        // if we are maintaining session state,
        // handle cookies (if any)
        if (msgContext.getMaintainSession()) {
            handleCookie(HTTPConstants.HEADER_COOKIE,
                    HTTPConstants.HEADER_SET_COOKIE, headers, msgContext);
            handleCookie(HTTPConstants.HEADER_COOKIE2,
                    HTTPConstants.HEADER_SET_COOKIE2, headers, msgContext);
        }
    }

    /**
     * little helper function for cookies
     *
     * @param cookieName
     * @param setCookieName
     * @param headers
     * @param msgContext
     */
    public void handleCookie(String cookieName, String setCookieName,
                             Hashtable headers, MessageContext msgContext) {

        if (headers.containsKey(setCookieName.toLowerCase())) {
            String cookie = (String) headers.get(setCookieName.toLowerCase());
            cookie = cookie.trim();

            // chop after first ; a la Apache SOAP (see HTTPUtils.java there)
            int index = cookie.indexOf(';');

            if (index != -1) {
                cookie = cookie.substring(0, index);
            }
            msgContext.setProperty(cookieName, cookie);
        }
    }

    /**
     * Check if the specified host is in the list of non proxy hosts.
     *
     * @param host host name
     * @param nonProxyHosts string containing the list of non proxy hosts
     *
     * @return true/false
     */
    private boolean isHostInNonProxyList(String host, String nonProxyHosts) {
        if ((nonProxyHosts == null) || (host == null)) {
            return false;
        }
        /* The http.nonProxyHosts system property is a list enclosed in
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
    private static boolean match(String pattern, String str,
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
