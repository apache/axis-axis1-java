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

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.Base64;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.message.MessageElement;
import org.apache.axis.utils.JavaUtils;
import org.apache.log4j.Category;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URL;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * This is meant to be used on a SOAP Client to call a SOAP server.
 *
 * @author Doug Davis (dug@us.ibm.com)
 */
public class HTTPSender extends BasicHandler {
    static Category category =
            Category.getInstance(HTTPSender.class.getName());

    public void invoke(MessageContext msgContext) throws AxisFault {
        if (category.isDebugEnabled()) {
            category.debug( JavaUtils.getMessage("enter00", 
                "HTTPSender::invoke") );
        }

        /* Find the service we're invoking so we can grab it's options */
        /***************************************************************/
        String   targetURL = null ;
        Message  outMsg    = null ;
        String   reqEnv    = null ;

        targetURL = msgContext.getStrProp( MessageContext.TRANS_URL);

        try {
            String   host ;
            int      port   = 80 ;
            URL      tmpURL = new URL( targetURL );
            int      returnCode     = 0 ;
            boolean  useFullURL = false;

            // default SOAPAction to request namespaceURI/method
            String   action = msgContext.getStrProp(HTTPConstants.MC_HTTP_SOAPACTION);
            if (action == null) {
                Message rm = msgContext.getRequestMessage();
                MessageElement body = rm.getSOAPPart().getAsSOAPEnvelope().getFirstBody();
                action = body.getNamespaceURI();
                if (action == null) action = "";
                if (!action.endsWith("/")) action += "/";
                action += body.getName();
            }

            host = tmpURL.getHost();
            if ( (port = tmpURL.getPort()) == -1 ) port = 80;

            Socket             sock = null ;

            if (tmpURL.getProtocol().equalsIgnoreCase("https")) {
                if ( (port = tmpURL.getPort()) == -1 ) port = 443;

                // Use http.proxyXXX settings if https.proxyXXX is not set 
                String tunnelHost = System.getProperty("http.proxyHost");
                String tunnelPortStr = System.getProperty("http.proxyPort");
                String tunnelUsername = System.getProperty("http.proxyUsername");
                String tunnelPassword = System.getProperty("http.proxyPassword");
                
                if (tunnelHost==null) tunnelHost = System.getProperty("https.proxyHost");
                if (tunnelPortStr==null) tunnelPortStr = System.getProperty("https.proxyPort");
                if (tunnelUsername==null) tunnelUsername = System.getProperty("https.proxyUsername");
                if (tunnelPassword==null) tunnelPassword = System.getProperty("https.proxyPassword");

                try {
                    Class SSLSocketFactoryClass =
                                                 Class.forName("javax.net.ssl.SSLSocketFactory");
                    Class SSLSocketClass = Class.forName("javax.net.ssl.SSLSocket");
                    Method createSocketMethod =
                                               SSLSocketFactoryClass.getMethod("createSocket",
                                                                               new Class[] {String.class, Integer.TYPE});
                    Method getDefaultMethod =
                                             SSLSocketFactoryClass.getMethod("getDefault", new Class[] {});
                    Method startHandshakeMethod =
                                                 SSLSocketClass.getMethod("startHandshake", new Class[] {});
                    Object factory = getDefaultMethod.invoke(null, new Object[] {});
                    Object sslSocket = null;
                    if (tunnelHost == null || tunnelHost.equals("")) {
                        // direct SSL connection
                        sslSocket = createSocketMethod .invoke(factory,
                                                               new Object[] {host, new Integer(port)});
                    } else {
                        // SSL tunnelling through proxy server
                        Method createSocketMethod2 =
                                                    SSLSocketFactoryClass.getMethod("createSocket",
                                                                                    new Class[] {Socket.class, String.class, Integer.TYPE, Boolean.TYPE});

                        // Default proxy port is 80, even for https
                        int tunnelPort = (tunnelPortStr != null? (Integer.parseInt(tunnelPortStr) < 0? 80: Integer.parseInt(tunnelPortStr)): 80);

                        // Create the regular socket connection to the proxy
                        Socket tunnel = new Socket(tunnelHost, tunnelPort);

                        // The tunnel handshake method (condensed and made reflexive)
                        OutputStream tunnelOutputStream = (OutputStream)SSLSocketClass.getMethod("getOutputStream", new Class[] {}).invoke(tunnel, new Object[] {});
                        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(tunnelOutputStream)));
                        out.print("CONNECT " + host + ":" + port + " HTTP/1.0\n\r\n\r");
                        out.flush();
                        InputStream tunnelInputStream = (InputStream)SSLSocketClass.getMethod("getInputStream", new Class[] {}).invoke(tunnel, new Object[] {});
                        if (category.isDebugEnabled()) {
                            category.debug(JavaUtils.getMessage("isNull00", 
                              "tunnelInputStream", 
                              "" + (tunnelInputStream == null)));
                        }

                        String replyStr = ""; 

                        // Make sure to read all the response from the proxy to prevent SSL negotiation failure
                        // Response message terminated by two sequential newlines
                        int		newlinesSeen = 0;
                        boolean		headerDone = false;	/* Done on first newline */

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
                                    replyStr += String.valueOf((char)i);
                                }
                            }
                        }

                        if (!replyStr.startsWith("HTTP/1.0 200") && !replyStr.startsWith("HTTP/1.1 200")) {
                            throw new IOException(JavaUtils.getMessage("cantTunnel00",
                                    new String[] {tunnelHost, "" + tunnelPort, replyStr}));
                        }
                        // End of condensed reflective tunnel handshake method
                        sslSocket = createSocketMethod2.invoke(factory,
                                                               new Object[] {tunnel, host, new Integer(port), new Boolean(true)});

                        if (category.isDebugEnabled()) {
                            category.debug(JavaUtils.getMessage(
                                "setupTunnel00", tunnelHost, "" + tunnelPort));
                        }
                    }
                    // must shake out hidden errors!
                    startHandshakeMethod.invoke(sslSocket, new Object[] {});
                    sock = (Socket)sslSocket;
                } catch (ClassNotFoundException cnfe) {
                    if (category.isDebugEnabled()) {
                        category.debug( JavaUtils.getMessage("noJSSE00"));
                    }

                    throw AxisFault.makeFault(cnfe);
                } catch (NumberFormatException nfe) {
                      if (category.isDebugEnabled()) {
                          category.debug( JavaUtils.getMessage("badProxy00", 
                              tunnelPortStr));
                      }

                      throw AxisFault.makeFault(nfe);
                }

                if (category.isDebugEnabled()) {
                    category.debug( JavaUtils.getMessage("createdSSL00"));
                }
            } else {
                String proxyHost = System.getProperty("http.proxyHost");
                String proxyPort = System.getProperty("http.proxyPort");
                String nonProxyHosts = System.getProperty("http.nonProxyHosts");
                boolean hostInNonProxyList = isHostInNonProxyList(host, nonProxyHosts);

                if ((port = tmpURL.getPort()) == -1 ) port = 80;

                if (proxyHost == null || proxyHost.equals("")
                    || proxyPort == null || proxyPort.equals("")
                    || hostInNonProxyList) {
                    sock = new Socket( host, port );

                    if (category.isDebugEnabled()) {
                        category.debug( JavaUtils.getMessage("createdHTTP00"));
                    }
                } else {
                    sock = new Socket( proxyHost, new Integer(proxyPort).intValue() );
                    
                    if (category.isDebugEnabled()) {
                        category.debug( JavaUtils.getMessage("createdHTTP01", 
                            proxyHost, proxyPort));
                    }

                    useFullURL = true;
                }
            }

            // optionally set a timeout for the request
            if (msgContext.getTimeout() != 0) {
                sock.setSoTimeout(msgContext.getTimeout());
            }


            OutputStream  out  = new BufferedOutputStream(sock.getOutputStream(), 8*1024);
            StringBuffer  otherHeaders = new StringBuffer();
            String        userID = null ;
            String        passwd = null ;

            userID = msgContext.getStrProp( MessageContext.USERID );
            passwd = msgContext.getStrProp( MessageContext.PASSWORD );

            if ( userID != null ) {
                StringBuffer tmpBuf = new StringBuffer();
                tmpBuf.append( userID )
               .append( ":" )
               .append( (passwd == null) ? "" : passwd) ;
                otherHeaders.append( HTTPConstants.HEADER_AUTHORIZATION )
                     .append( ": Basic " )
                     .append( Base64.encode( tmpBuf.toString().getBytes() ) )
                     .append("\n" );
            }

            // don't forget the cookies!
            // mmm... cookies
            if (msgContext.getMaintainSession()) {
                String cookie = msgContext.getStrProp(HTTPConstants.HEADER_COOKIE);
                String cookie2 = msgContext.getStrProp(HTTPConstants.HEADER_COOKIE2);

                if (cookie != null) {
                    otherHeaders.append(HTTPConstants.HEADER_COOKIE)
                     .append(": ")
                     .append(cookie)
                     .append("\r\n");
                }

                if (cookie2 != null) {
                    otherHeaders.append(HTTPConstants.HEADER_COOKIE2)
                     .append(": ")
                     .append(cookie2)
                     .append("\r\n");
                }
            }

            StringBuffer header = new StringBuffer();
            // byte[] request = reqEnv.getBytes();

            header.append( HTTPConstants.HEADER_POST )
             .append(" " );
            if (useFullURL == true) {
                header.append(tmpURL.toExternalForm());
            } else {
                header.append( ((tmpURL.getFile() == null ||
                        tmpURL.getFile().equals(""))? "/": tmpURL.getFile()) );
            }

            Message reqMessage= msgContext.getRequestMessage();

            header.append( " HTTP/1.0\r\n" )
             .append( HTTPConstants.HEADER_CONTENT_LENGTH )
             .append( ": " )
             .append( reqMessage.getContentLength() )
             .append( "\r\n" )
             .append( HTTPConstants.HEADER_HOST )
             .append( ": " )
             .append( host )
             .append( "\r\n" )
             .append( HTTPConstants.HEADER_CONTENT_TYPE )
             .append( ": " )
             .append( reqMessage.getContentType())
             .append( "\r\n" )
             .append( (otherHeaders == null ? "" : otherHeaders.toString()))
             .append( HTTPConstants.HEADER_SOAP_ACTION )
             .append( ": \"" )
             .append( action )
             .append( "\"\r\n");

            header.append("\r\n");

            out.write( header.toString().getBytes(HTTPConstants.HEADER_DEFAULT_CHAR_ENCODING) );
            reqMessage.writeContentToStream(out);
            out.flush();

            if (category.isDebugEnabled()) {
                category.debug( JavaUtils.getMessage("xmlSent00") );
                category.debug( "---------------------------------------------------");
                category.debug( header + reqEnv );
            }

            byte       b ;
            int        len = 0 ;
            int        colonIndex = -1 ;
            Hashtable  headers = new Hashtable();
            String     name, value ;
            String     statusMessage = "";

            BufferedInputStream inp = new BufferedInputStream(sock.getInputStream());
                       //Should help performance. Temporary fix only till its all stream oriented.

            // Need to add logic for getting the version # and the return code
            // but that's for tomorrow!

            /*Logic to read HTTP response headers */
            boolean readTooMuch= false;
            b=0;
            for(ByteArrayOutputStream buf= new ByteArrayOutputStream(4097);;){
                if(!readTooMuch)b = (byte) inp.read();
                if(b == -1 ) break ;
                readTooMuch=false;
                if ( b != '\r' && b != '\n' ) {
                    if ( b == ':' && colonIndex == -1 ) colonIndex = len ;
                    len++;
                    buf.write(b);
                }
                else if ( b == '\r' )
                    continue ;
                else {  //b== '\n'
                    if ( len == 0 ) break ;

                    b = (byte) inp.read();
                    readTooMuch= true;
                        //A space or tab at the begining of a line means the header continues.
                    if( b == ' ' || b== '\t'){
                        continue;  
                    }

                    if ( colonIndex != -1 ) {
                        buf.close();
                        byte[]hdata= buf.toByteArray();
                        buf.reset();
                        name = new String( hdata, 0, colonIndex, HTTPConstants.HEADER_DEFAULT_CHAR_ENCODING );
                        value = new String( hdata, colonIndex+1, len-1-colonIndex, HTTPConstants.HEADER_DEFAULT_CHAR_ENCODING );
                        colonIndex = -1 ;
                    }
                    else {
                        buf.close();
                        byte[]hdata= buf.toByteArray();
                        buf.reset();
                        name = new String( hdata, 0, len, HTTPConstants.HEADER_DEFAULT_CHAR_ENCODING );
                        value = "" ;
                    }

                    if (category.isDebugEnabled()) {
                        category.debug( name + value );
                    }

                    if ( msgContext.getProperty(HTTPConstants.MC_HTTP_STATUS_CODE)==null){
                        // Reader status code
                        int start = name.indexOf( ' ' ) + 1 ;
                        String tmp = name.substring(start).trim();
                        int end   = tmp.indexOf( ' ' );
                        if ( end != -1 ) tmp = tmp.substring( 0, end );
                        returnCode = Integer.parseInt( tmp );
                        msgContext.setProperty( HTTPConstants.MC_HTTP_STATUS_CODE,
                                                new Integer(returnCode) );
                        statusMessage = name.substring(start + end + 1);
                        msgContext.setProperty( HTTPConstants.MC_HTTP_STATUS_MESSAGE,
                                                statusMessage);
                    }
                    else
                        headers.put( name.toLowerCase(), value );
                    len = 0 ;
                }
            }
            /*All HTTP headers have been read.*/

            if (returnCode > 199 && returnCode < 300) {
                // SOAP return is OK - so fall through
            } else if (returnCode > 499 && returnCode < 600) {
                // SOAP Fault should be in here - so fall through
            } else {
                // Unknown return code - so wrap up the content into a
                // SOAP Fault.
               ByteArrayOutputStream buf= new ByteArrayOutputStream(4097);
               while(-1 !=(b = (byte)inp.read())){
                  buf.write(b); 
               }

                AxisFault fault = new AxisFault("HTTP",
                                                statusMessage,
                                                null,
                                                null);
                fault.setFaultDetailString(JavaUtils.getMessage("return01",
                        "" + returnCode, buf.toString()));
                throw fault;
            }

            if ( b != -1 ) { //more data than just headers.
                String contentType = (String) headers.get(
                         HTTPConstants.HEADER_CONTENT_TYPE.toLowerCase());
                contentType= (null == contentType )? null:contentType.trim();

                String contentLocation = (String) headers.get(
                    HTTPConstants.HEADER_CONTENT_LOCATION.toLowerCase());
                contentLocation= (null == contentLocation )? null:contentLocation.trim();

                String contentLength = (String) headers.get(
                    HTTPConstants.HEADER_CONTENT_LENGTH.toLowerCase());
                contentLength= (null == contentLength )? null:contentLength.trim();

                outMsg = new Message( inp, false, contentType, contentLocation);

                outMsg.setMessageType(org.apache.axis.Message.RESPONSE);
                msgContext.setResponseMessage( outMsg );

                if (category.isDebugEnabled()) {
                    if(null==contentLength )
                        category.debug( "\n" + JavaUtils.getMessage("no00",
                          "Content-Length") );
                    category.debug( "\n" + JavaUtils.getMessage("xmlRecd00") );
                    category.debug( "-----------------------------------------------");
                    category.debug( (String) outMsg.getSOAPPart().getAsString() );
                }

            }


            // if we are maintaining session state,
            // handle cookies (if any)
            if (msgContext.getMaintainSession()) {
                handleCookie(HTTPConstants.HEADER_COOKIE,
                             HTTPConstants.HEADER_SET_COOKIE,
                             headers,
                             msgContext);
                handleCookie(HTTPConstants.HEADER_COOKIE2,
                             HTTPConstants.HEADER_SET_COOKIE2,
                             headers,
                             msgContext);
            }
            
        }
        catch( Exception e ) {
            category.debug( e );
            throw AxisFault.makeFault(e);
        }

        if (category.isDebugEnabled()) {
            category.debug( JavaUtils.getMessage("exit00", 
                "HTTPDispatchHandler::invoke") );
        }
    }

    // little helper function for cookies
    public void handleCookie
         (String cookieName, String setCookieName, Hashtable headers,
          MessageContext msgContext)
    {
        if (headers.containsKey(setCookieName.toLowerCase())) {
            String cookie = (String)headers.get(setCookieName.toLowerCase());
            cookie = cookie.trim();
            // chop after first ; a la Apache SOAP (see HTTPUtils.java there)
            int index = cookie.indexOf(';');
            if (index != -1) {
                cookie = cookie.substring(0, index);
            }
            msgContext.setProperty(cookieName, cookie);
        }
    }

    private boolean isHostInNonProxyList(String host, String nonProxyHosts)
    {
        if(nonProxyHosts == null || host == null)
            return false;
        StringTokenizer tokenizer = new StringTokenizer(nonProxyHosts,"|");
        while(tokenizer.hasMoreTokens()) {
            String pattern = tokenizer.nextToken();

            if (category.isDebugEnabled()) {
                category.debug( JavaUtils.getMessage(
                    "match00",
                    new String[] {"HTTPSender", host, pattern}));
            }

            if(match(pattern, host, false))
                return true;
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
     *
     * @return <code>true</code> when the string matches against the pattern,
     *         <code>false</code> otherwise.
     */
    private static boolean match(String pattern, String str, boolean isCaseSensitive) {
        char[] patArr = pattern.toCharArray();
        char[] strArr = str.toCharArray();
        int patIdxStart = 0;
        int patIdxEnd   = patArr.length-1;
        int strIdxStart = 0;
        int strIdxEnd   = strArr.length-1;
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
                return false; // Pattern and string do not have the same size
            }
            for (int i = 0; i <= patIdxEnd; i++) {
                ch = patArr[i];
                if (isCaseSensitive && ch != strArr[i]) {
                    return false;// Character mismatch
                }
                if (!isCaseSensitive && Character.toUpperCase(ch) !=
                    Character.toUpperCase(strArr[i])) {
                    return false; // Character mismatch
                }
            }
            return true; // String matches against pattern
        }

        if (patIdxEnd == 0) {
            return true; // Pattern contains only '*', which matches anything
        }

        // Process characters before first star
        while((ch = patArr[patIdxStart]) != '*' && strIdxStart <= strIdxEnd) {
            if (isCaseSensitive && ch != strArr[strIdxStart]) {
                return false;// Character mismatch
            }
            if (!isCaseSensitive && Character.toUpperCase(ch) !=
                Character.toUpperCase(strArr[strIdxStart])) {
                return false;// Character mismatch
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
        while((ch = patArr[patIdxEnd]) != '*' && strIdxStart <= strIdxEnd) {
            if (isCaseSensitive && ch != strArr[strIdxEnd]) {
                return false;// Character mismatch
            }
            if (!isCaseSensitive && Character.toUpperCase(ch) !=
                Character.toUpperCase(strArr[strIdxEnd])) {
                return false;// Character mismatch
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
        while (patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
            int patIdxTmp = -1;
            for (int i = patIdxStart+1; i <= patIdxEnd; i++) {
                if (patArr[i] == '*') {
                    patIdxTmp = i;
                    break;
                }
            }
            if (patIdxTmp == patIdxStart+1) {
                // Two stars next to each other, skip the first one.
                patIdxStart++;
                continue;
            }
            // Find the pattern between padIdxStart & padIdxTmp in str between
            // strIdxStart & strIdxEnd
            int patLength = (patIdxTmp-patIdxStart-1);
            int strLength = (strIdxEnd-strIdxStart+1);
            int foundIdx  = -1;
            strLoop:
            for (int i = 0; i <= strLength - patLength; i++) {
                for (int j = 0; j < patLength; j++) {
                    ch = patArr[patIdxStart+j+1];
                    if (isCaseSensitive && ch != strArr[strIdxStart+i+j]) {
                        continue strLoop;
                    }
                    if (!isCaseSensitive && Character.toUpperCase(ch) !=
                        Character.toUpperCase(strArr[strIdxStart+i+j])) {
                        continue strLoop;
                    }
                }

                foundIdx = strIdxStart+i;
                break;
            }

            if (foundIdx == -1) {
                return false;
            }

            patIdxStart = patIdxTmp;
            strIdxStart = foundIdx+patLength;
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


    public void undo(MessageContext msgContext) {
        if (category.isDebugEnabled()) {
            category.debug( JavaUtils.getMessage("enter00", 
                "HTTPDispatchHandler::undo") );
            category.debug( JavaUtils.getMessage("exit00", 
                "HTTPDispatchHandler::undo") );
        }
    }
};

