/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.Constants;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.components.net.BooleanHolder;
import org.apache.axis.components.net.SocketFactory;
import org.apache.axis.components.net.SocketFactoryFactory;
import org.apache.axis.encoding.Base64;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.soap.SOAP12Constants;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

import javax.xml.soap.SOAPException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.Hashtable;

/**
 * This is meant to be used on a SOAP Client to call a SOAP server.
 *
 * @author Doug Davis (dug@us.ibm.com)
 * @author Davanum Srinivas (dims@yahoo.com)
 */
public class HTTPSender extends BasicHandler {

    protected static Log log = LogFactory.getLog(HTTPSender.class.getName());

    /**
     * the url; used for error reporting
     */
    URL targetURL;

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
            log.debug(Messages.getMessage("enter00", "HTTPSender::invoke"));
        }
        try {
            BooleanHolder useFullURL = new BooleanHolder(false);
            StringBuffer otherHeaders = new StringBuffer();
            targetURL = new URL(msgContext.getStrProp(MessageContext.TRANS_URL));
            String host = targetURL.getHost();
            int port = targetURL.getPort();
            Socket sock = null;


            sock = getSocket(targetURL.getProtocol(), host, port, otherHeaders, useFullURL);

            // optionally set a timeout for the request
            if (msgContext.getTimeout() != 0) {
                sock.setSoTimeout(msgContext.getTimeout());
            }

            // Send the SOAP request to the server
            InputStream inp = writeToSocket(sock, msgContext, targetURL,
                        otherHeaders, host, port, useFullURL);

            // Read the response back from the server
            Hashtable headers = new Hashtable();
            inp = readHeadersFromSocket(sock, msgContext, inp, headers);
            readFromSocket(sock, msgContext, inp, headers);
        } catch (Exception e) {
            log.debug(e);
            throw AxisFault.makeFault(e);
        }
        if (log.isDebugEnabled()) {
            log.debug(Messages.getMessage("exit00",
                    "HTTPDispatchHandler::invoke"));
        }
    }

    /**
     * Creates a socket connection to the SOAP server
     *
     * @param protocol "http" for standard, "https" for ssl.
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
            String protocol,
            String host, int port, StringBuffer otherHeaders, BooleanHolder useFullURL)
            throws Exception {
        SocketFactory factory = SocketFactoryFactory.getFactory(protocol, getOptions());
        return factory.create(host, port, otherHeaders, useFullURL);
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
    private InputStream writeToSocket(
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

        String webMethod = null;
        boolean posting = true;

        // If we're SOAP 1.2, allow the web method to be set from the
        // MessageContext.
        if (msgContext.getSOAPConstants() == SOAPConstants.SOAP12_CONSTANTS) {
            webMethod = msgContext.getStrProp(SOAP12Constants.PROP_WEBMETHOD);
        }
        if (webMethod == null) {
            webMethod = HTTPConstants.HEADER_POST;
        } else {
            posting = webMethod.equals(HTTPConstants.HEADER_POST);
        }

        header.append(webMethod).append(" ");
        if (useFullURL.value) {
            header.append(tmpURL.toExternalForm());
        } else {
            header.append((((tmpURL.getFile() == null)
                    || tmpURL.getFile().equals(""))
                    ? "/"
                    : tmpURL.getFile()));
        }

        Message reqMessage = msgContext.getRequestMessage();

        boolean http10 = true; //True if this is to use HTTP 1.0 / false HTTP 1.1
        boolean httpChunkStream = false; //Use HTTP chunking or not.
        boolean httpContinueExpected = false; //Under HTTP 1.1 if false you *MAY* need to wait for a 100 rc,
                                              //  if true the server MUST reply with 100 continue.
        String httpConnection = null;

        String httpver = msgContext.getStrProp(MessageContext.HTTP_TRANSPORT_VERSION);
        if (null == httpver) {
            httpver = HTTPConstants.HEADER_PROTOCOL_V10;
        }
        httpver = httpver.trim();
        if (httpver.equals(HTTPConstants.HEADER_PROTOCOL_V11)) {
            http10 = false;
        }

        //process user defined headers for information.
        Hashtable userHeaderTable = (Hashtable) msgContext.
                getProperty(HTTPConstants.REQUEST_HEADERS);

        if (userHeaderTable != null) {
            if (null == otherHeaders) {
                otherHeaders = new StringBuffer(1024);
            }

            for (java.util.Iterator e = userHeaderTable.entrySet().iterator();
                 e.hasNext();) {

                java.util.Map.Entry me = (java.util.Map.Entry) e.next();
                Object keyObj = me.getKey();
                if (null == keyObj) continue;
                String key = keyObj.toString().trim();

                if (key.equalsIgnoreCase(HTTPConstants.HEADER_TRANSFER_ENCODING)) {
                    if (!http10) {
                        String val = me.getValue().toString();
                        if (null != val && val.trim().equalsIgnoreCase(HTTPConstants.HEADER_TRANSFER_ENCODING_CHUNKED))
                            httpChunkStream = true;
                    }
                } else if (key.equalsIgnoreCase(HTTPConstants.HEADER_HOST)) {
                    //ignore
                } else if (key.equalsIgnoreCase(HTTPConstants.HEADER_CONTENT_TYPE)) {
                    //ignore
                } else if (key.equalsIgnoreCase(HTTPConstants.HEADER_SOAP_ACTION)) {
                    //ignore
                } else if (key.equalsIgnoreCase(HTTPConstants.HEADER_CONTENT_LENGTH)) {
                    //ignore
                } else if (key.equalsIgnoreCase(HTTPConstants.HEADER_COOKIE)) {
                    //ignore
                } else if (key.equalsIgnoreCase(HTTPConstants.HEADER_COOKIE2)) {
                    //ignore
                } else if (key.equalsIgnoreCase(HTTPConstants.HEADER_AUTHORIZATION)) {
                    //ignore
                } else if (key.equalsIgnoreCase(HTTPConstants.HEADER_PROXY_AUTHORIZATION)) {
                    //ignore
                } else if (key.equalsIgnoreCase(HTTPConstants.HEADER_CONNECTION)) {
                    if (!http10) {
                        String val = me.getValue().toString();
                        if (val.trim().equalsIgnoreCase(HTTPConstants.HEADER_CONNECTION_CLOSE))
                            httpConnection = HTTPConstants.HEADER_CONNECTION_CLOSE;
                    }
                    //HTTP 1.0 will always close.
                    //HTTP 1.1 will use persistent. //no need to specify
                } else {
                    if( !http10 && key.equalsIgnoreCase(HTTPConstants.HEADER_EXPECT)) {
                        String val = me.getValue().toString();
                        if (null != val && val.trim().equalsIgnoreCase(HTTPConstants.HEADER_EXPECT_100_Continue))
                            httpContinueExpected = true;
                    }

                    otherHeaders.append(key).append(": ").append(me.getValue()).append("\r\n");
                }
            }
        }

        if (!http10) {
            //Force close for now.
            //TODO HTTP/1.1
            httpConnection = HTTPConstants.HEADER_CONNECTION_CLOSE;
        }

        header.append(" ");
        header.append(http10 ? HTTPConstants.HEADER_PROTOCOL_10 :
                HTTPConstants.HEADER_PROTOCOL_11)
                .append("\r\n");
        if (posting) {
            header.append(HTTPConstants.HEADER_CONTENT_TYPE)
                    .append(": ")
                    .append(reqMessage.getContentType(msgContext.getSOAPConstants()))
                    .append("\r\n");
        }
        header.append( HTTPConstants.HEADER_ACCEPT ) //Limit to the types that are meaningful to us.
                .append( ": ")
                .append( HTTPConstants.HEADER_ACCEPT_APPL_SOAP)
                .append( ", ")
                .append( HTTPConstants.HEADER_ACCEPT_APPLICATION_DIME)
                .append( ", ")
                .append( HTTPConstants.HEADER_ACCEPT_MULTIPART_RELATED)
                .append( ", ")
                .append( HTTPConstants.HEADER_ACCEPT_TEXT_ALL)
                .append("\r\n")
                .append(HTTPConstants.HEADER_USER_AGENT)   //Tell who we are.
                .append( ": ")
                .append(Messages.getMessage("axisUserAgent"))
                .append("\r\n")
                .append(HTTPConstants.HEADER_HOST)  //used for virtual connections
                .append(": ")
                .append(host)
                .append((port == -1)?(""):(":" + port))
                .append("\r\n")
                .append(HTTPConstants.HEADER_CACHE_CONTROL)   //Stop caching proxies from caching SOAP reqeuest.
                .append(": ")
                .append(HTTPConstants.HEADER_CACHE_CONTROL_NOCACHE)
                .append("\r\n")
                .append(HTTPConstants.HEADER_PRAGMA)
                .append(": ")
                .append(HTTPConstants.HEADER_CACHE_CONTROL_NOCACHE)
                .append("\r\n")
                .append(HTTPConstants.HEADER_SOAP_ACTION)  //The SOAP action.
                .append(": \"")
                .append(action)
                .append("\"\r\n");

        if (posting) {
            if (!httpChunkStream) {
                //Content length MUST be sent on HTTP 1.0 requests.
                header.append(HTTPConstants.HEADER_CONTENT_LENGTH)
                        .append(": ")
                        .append(reqMessage.getContentLength())
                        .append("\r\n");
            } else {
                //Do http chunking.
                header.append(HTTPConstants.HEADER_TRANSFER_ENCODING)
                        .append(": ")
                        .append(HTTPConstants.HEADER_TRANSFER_ENCODING_CHUNKED)
                        .append("\r\n");
            }
        }

        if (null != httpConnection) {
            header.append(HTTPConstants.HEADER_CONNECTION);
            header.append(": ");
            header.append(httpConnection);
            header.append("\r\n");
        }

        if (null != otherHeaders) {
            //Add other headers to the end.
            //for pre java1.4 support, we have to turn the string buffer argument into
            //a string before appending.
            header.append(otherHeaders.toString());
        }


        header.append("\r\n"); //The empty line to start the BODY.

        OutputStream out = sock.getOutputStream();

        if (!posting) {
            out.write(header.toString()
                    .getBytes(HTTPConstants.HEADER_DEFAULT_CHAR_ENCODING));
            out.flush();
            return null;
        }

        InputStream inp = null;

        if (httpChunkStream) {
            out.write(header.toString()
                    .getBytes(HTTPConstants.HEADER_DEFAULT_CHAR_ENCODING));
        }

        if(httpContinueExpected ){ //We need to get a reply from the server as to whether
            // it wants us send anything more.
            out.flush();
            Hashtable cheaders= new Hashtable ();
            inp = readHeadersFromSocket(sock, msgContext, null, cheaders);
            int returnCode= -1;
            Integer Irc= (Integer)msgContext.getProperty(HTTPConstants.MC_HTTP_STATUS_CODE);
            if(null != Irc) {
                returnCode= Irc.intValue();
            }
            if(100 == returnCode){  // got 100 we may continue.
                //Need todo a little msgContext house keeping....
                msgContext.removeProperty(HTTPConstants.MC_HTTP_STATUS_CODE);
                msgContext.removeProperty(HTTPConstants.MC_HTTP_STATUS_MESSAGE);
            }
            else{ //If no 100 Continue then we must not send anything!
                String statusMessage= (String)
                        msgContext.getProperty(HTTPConstants.MC_HTTP_STATUS_MESSAGE);

                AxisFault fault = new AxisFault("HTTP", "(" + returnCode+ ")" + statusMessage, null, null);

                fault.setFaultDetailString(Messages.getMessage("return01",
                                                               "" + returnCode, ""));
                throw fault;
            }
        }
        if (httpChunkStream) {
            ChunkedOutputStream chunkedOutputStream = new ChunkedOutputStream(out);
            out = new BufferedOutputStream(chunkedOutputStream, Constants.HTTP_TXR_BUFFER_SIZE);
            try {
                reqMessage.writeTo(out);
            } catch (SOAPException e) {
                log.error(Messages.getMessage("exception00"), e);
            }
            out.flush();
            chunkedOutputStream.eos();
        } else {
            out = new BufferedOutputStream(out, Constants.HTTP_TXR_BUFFER_SIZE);
            try {
                out.write(header.toString()
                        .getBytes(HTTPConstants.HEADER_DEFAULT_CHAR_ENCODING));
                reqMessage.writeTo(out);
            } catch (SOAPException e) {
                log.error(Messages.getMessage("exception00"), e);
            }
            // Flush ONLY once.
            out.flush();
        }
        if (log.isDebugEnabled()) {
            log.debug(Messages.getMessage("xmlSent00"));
            log.debug("---------------------------------------------------");
            log.debug(header + reqEnv);
        }

        return inp;
    }

    private InputStream readHeadersFromSocket(Socket sock,
                                              MessageContext msgContext,
                                              InputStream inp,
                                              Hashtable headers)
            throws IOException {
        byte b = 0;
        int len = 0;
        int colonIndex = -1;
        String name, value;
        int returnCode = 0;
        if(null == inp) {
            inp = new BufferedInputStream(sock.getInputStream());
        }

        if (headers == null) {
            headers = new Hashtable();
        }

        // Should help performance. Temporary fix only till its all stream oriented.
        // Need to add logic for getting the version # and the return code
        // but that's for tomorrow!

        /* Logic to read HTTP response headers */
        boolean readTooMuch = false;

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
                    msgContext.setProperty(HTTPConstants.MC_HTTP_STATUS_MESSAGE,
                            name.substring(start + end + 1));
                } else {
                    headers.put(name.toLowerCase(), value);
                }
                len = 0;
            }
        }

        return inp;
    }

    /**
     * Reads the SOAP response back from the server
     *
     * @param sock socket
     * @param msgContext message context
     *
     * @throws IOException
     */
    private InputStream readFromSocket(Socket sock,
                                       MessageContext msgContext,
                                       InputStream inp,
                                       Hashtable headers)
            throws IOException {
        Message outMsg = null;
        byte b;

        Integer rc = (Integer)msgContext.getProperty(
                                            HTTPConstants.MC_HTTP_STATUS_CODE);
        int returnCode = 0;
        if (rc != null) {
            returnCode = rc.intValue();
        } else {
            // No return code?? Should have one by now.
        }

        /* All HTTP headers have been read. */
        String contentType =
                (String) headers
                .get(HTTPConstants.HEADER_CONTENT_TYPE.toLowerCase());

        contentType = (null == contentType)
                ? null
                : contentType.trim();
        if ((returnCode > 199) && (returnCode < 300)) {
            if (returnCode == 202)
                return inp;
            // SOAP return is OK - so fall through
        } else if (msgContext.getSOAPConstants() ==
                SOAPConstants.SOAP12_CONSTANTS) {
            // For now, if we're SOAP 1.2, fall through, since the range of
            // valid result codes is much greater
        } else if ((contentType != null) && !contentType.startsWith("text/html")
                && ((returnCode > 499) && (returnCode < 600))) {
            // SOAP Fault should be in here - so fall through
        } else {
            // Unknown return code - so wrap up the content into a
            // SOAP Fault.
            ByteArrayOutputStream buf = new ByteArrayOutputStream(4097);

            while (-1 != (b = (byte) inp.read())) {
                buf.write(b);
            }
            String statusMessage = msgContext.getStrProp(
                                        HTTPConstants.MC_HTTP_STATUS_MESSAGE);
            AxisFault fault = new AxisFault("HTTP", "(" + returnCode + ")" +
                                                    statusMessage, null, null);

            fault.setFaultDetailString(Messages.getMessage("return01",
                    "" + returnCode, buf.toString()));
            fault.addFaultDetail(Constants.QNAME_FAULTDETAIL_HTTPERRORCODE,
                    Integer.toString(returnCode));
            throw fault;
        }

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
                .get(HTTPConstants.HEADER_TRANSFER_ENCODING);

        if (null != transferEncoding) {
            transferEncoding = transferEncoding.toLowerCase();
        }

        if (null != transferEncoding
                && transferEncoding.trim()
                .equals(HTTPConstants.HEADER_TRANSFER_ENCODING_CHUNKED)) {
            inp = new ChunkedInputStream(inp);
        }


        outMsg = new Message( new SocketInputStream(inp, sock), false,
                              contentType, contentLocation);
        outMsg.setMessageType(Message.RESPONSE);
        msgContext.setResponseMessage(outMsg);
        if (log.isDebugEnabled()) {
            if (null == contentLength) {
                log.debug("\n"
                          + Messages.getMessage("no00", "Content-Length"));
            }
            log.debug("\n" + Messages.getMessage("xmlRecd00"));
            log.debug("-----------------------------------------------");
            log.debug(outMsg.getSOAPEnvelope().toString());
        }

        // if we are maintaining session state,
        // handle cookies (if any)
        if (msgContext.getMaintainSession()) {
            handleCookie(HTTPConstants.HEADER_COOKIE,
                    HTTPConstants.HEADER_SET_COOKIE, headers, msgContext);
            handleCookie(HTTPConstants.HEADER_COOKIE2,
                    HTTPConstants.HEADER_SET_COOKIE2, headers, msgContext);
        }
        return inp;
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
}
