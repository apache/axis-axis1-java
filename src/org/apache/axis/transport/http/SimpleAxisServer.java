/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights
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

package org.apache.axis.transport.http ;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.axis.* ;
import org.apache.axis.server.* ;
import org.apache.axis.utils.* ;

import org.apache.axis.session.Session;
import org.apache.axis.session.SimpleSession;

/**
 * This is a single threaded implementation of an HTTP server for processing
 * SOAP requests via Apache's xml-axis.  This is not intended for production
 * use.  Its intended uses are for demos, debugging, and performance
 * profiling.
 *
 * @author Sam Ruby (ruby@us.ibm.com)
 * @author Rob Jellinghaus (robj@unrealities.com)
 */
public class SimpleAxisServer implements Runnable {
    
    // session state.
    // This table maps session keys (random numbers) to SimpleAxisSession objects.
    //
    // There is NO CLEANUP of this table at present, and if clients are not
    // passing cookies, then a new session will be created for *every* request.
    // This is the biggest impediment to any kind of real SimpleAxisServer use.
    // So, if this becomes objectionable, we will implement some simpleminded
    // cleanup (perhaps just a cap on max # of sessions, and some kind of LRU
    // cleanup policy).
    private Hashtable sessions = new Hashtable();
    
    // Are we doing sessions?
    // Set this to false if you don't want any session overhead.
    public static boolean doSessions = true;
    
    // What is our current session index?
    // This is a monotonically increasing, non-thread-safe integer
    // (thread safety not considered crucial here)
    public static int sessionIndex = 0;

    // Axis server (shared between instances)
    private static AxisServer myAxisServer = null;
    private static synchronized AxisServer getAxisServer() {
        if (myAxisServer == null) {
            myAxisServer = new AxisServer();
            myAxisServer.init();
        }
        return myAxisServer;
    }

    // HTTP prefix
    private static byte HTTP[]   = "HTTP/1.0 ".getBytes();

    // HTTP status codes
    private static byte OK[]     = "200 OK".getBytes();
    private static byte UNAUTH[] = "401 Unauthorized".getBytes();
    private static byte ISE[]    = "500 Internal Server Error".getBytes();

    // Standard MIME headers
    private static byte MIME_STUFF[] =
        ( "\nContent-Type: text/xml\n" +
          "Content-Length: ").getBytes();

    // Mime/Content separator
    private static byte SEPARATOR[] = "\n\n".getBytes();
    
    // Tiddly little response
    private static byte cannedResponse[] = "<empty/>".getBytes();

    // Axis specific constants
    private static String transportInName = "HTTPAction";
    private static String transportOutName = "HTTP.output";
    //private static final String AXIS_ENGINE = "AxisEngine" ;
    
    // are we stopped?
    // latch to true if stop() is called
    private boolean stopped = false;

    /**
     * The main workhorse method.
     *
     * Accept requests from a given TCP port and send them through the
     * Axis engine for processing.
     */
    public void run() {

        // create an Axis server
        AxisServer engine = new AxisServer();
        engine.init();

        // create and initialize a message context
        MessageContext msgContext = new MessageContext(engine);
        Message        requestMsg;
        msgContext.setProperty(MessageContext.TRANS_INPUT , transportInName);
        msgContext.setProperty(MessageContext.TRANS_OUTPUT, transportOutName);

        // Reusuable, buffered, content length controlled, InputStream
        NonBlockingBufferedInputStream is =
            new NonBlockingBufferedInputStream();

        // buffers for the headers we care about
        StringBuffer soapAction = new StringBuffer();
        StringBuffer httpRequest = new StringBuffer();
        StringBuffer cookie = new StringBuffer();
        StringBuffer cookie2 = new StringBuffer();
        
        // Accept and process requests from the socket
        while (!stopped) {
            Socket socket = null;

            // prepare request (do as much as possible while waiting for the
            // next connection).  Note the next two statements are commented
            // out.  Uncomment them if you experience any problems with not
            // resetting state between requests:
            //   msgContext = new MessageContext();
            //   requestMsg = new Message("", "String");
            msgContext.setServiceDescription(null);
            msgContext.setTargetService(null);
            msgContext.setResponseMessage(null);
            msgContext.clearProperties();
            msgContext.setProperty("transport", "HTTPTransport");
            msgContext.setProperty(MessageContext.TRANS_INPUT, transportInName);
            msgContext.setProperty(MessageContext.TRANS_OUTPUT, transportOutName);
            
            try {
                try {
                    socket = serverSocket.accept();
                } catch (IOException ioe) {
                    break;
                }

                // assume the best
                byte[] status = OK;

                // cookie for this session, if any
                String cooky = null;
                
                try {
                    // wipe cookies if we're doing sessions
                    if (doSessions) {
                        cookie.delete(0, cookie.length());
                        cookie2.delete(0, cookie.length());
                    }
                    
                    // read headers
                    is.setInputStream(socket.getInputStream());
                    // parse all headers into hashtable
                    int contentLength = parseHeaders(is, soapAction, httpRequest,
                        cookie, cookie2);
                    is.setContentLength(contentLength);

                    // if get, then return simpleton document as response
                    if (httpRequest.toString().equals("GET")) {
                        OutputStream out = socket.getOutputStream();
                        out.write(HTTP);
                        out.write(status);
                        out.write(MIME_STUFF);
                        putInt(out, cannedResponse.length);
                        out.write(SEPARATOR);
                        out.write(cannedResponse);
                        out.flush();
                        continue;
                    }
                        
                    String soapActionString = soapAction.toString();
                    requestMsg = new Message(is);
                    msgContext.setRequestMessage(requestMsg);
                    msgContext.setTargetService(soapActionString);
                    msgContext.setProperty(HTTPConstants.MC_HTTP_SOAPACTION,
                                           soapActionString);
                    
                    // set up session, if any
                    if (doSessions) {
                        // did we get a cookie?
                        if (cookie.length() > 0) {
                            cooky = cookie.toString().trim();
                        } else if (cookie2.length() > 0) {
                            cooky = cookie2.toString().trim();
                        }
                        
                        // if cooky is null, cook up a cooky
                        if (cooky == null) {
                            // fake one up!
                            // make it be an arbitrarily increasing number
                            // (no this is not thread safe because ++ isn't atomic)
                            int i = sessionIndex++;
                            cooky = "" + i;
                        }
                            
                        // is there a session already?
                        Session session = null;
                        if (sessions.containsKey(cooky)) {
                            session = (Session)sessions.get(cooky);
                        } else {
                            // no session for this cooky, bummer
                            session = new SimpleSession();
                            
                            // ADD CLEANUP LOGIC HERE if needed
                            sessions.put(cooky, session);
                        }
                        
                        msgContext.setSession(session);
                    }

                    // invoke the Axis engine
                    engine.invoke(msgContext);

                } catch( AxisFault af ) {
                    if ("Server.Unauthorized".equals(af.getFaultCode())) {
                        status = ISE; // SC_INTERNAL_SERVER_ERROR
                    } else {
                        status = UNAUTH; // SC_UNAUTHORIZED
                    }

                    msgContext.setResponseMessage(new Message(af));

                } catch( Exception e ) {
                    status = ISE; // SC_INTERNAL_SERVER_ERROR
                    msgContext.setResponseMessage(new Message(new AxisFault(e)));
                }


                // Retrieve the response from Axis
                Message responseMsg = msgContext.getResponseMessage();
                byte[] response = (byte[]) responseMsg.getAsBytes();

                // Send it on its way...
                OutputStream out = socket.getOutputStream();
                out.write(HTTP);
                out.write(status);
                out.write(MIME_STUFF);
                putInt(out, response.length);
                
                if (doSessions) {
                    // write cookie headers, if any
                    // don't sweat efficiency *too* badly
                    // optimize at will
                    StringBuffer cookieOut = new StringBuffer();
                    cookieOut.append("\r\nSet-Cookie: ")
                        .append(cooky)
                        .append("\r\nSet-Cookie2: ")
                        .append(cooky);
                    // OH, THE HUMANITY!  yes this is inefficient.
                    out.write(cookieOut.toString().getBytes());
                }
                
                out.write(SEPARATOR);
                out.write(response);
                out.flush();
            
                if (msgContext.getProperty(msgContext.QUIT_REQUESTED) != null) {
                    // why then, quit!
                    this.stop();
                }

            } catch (InterruptedIOException iie) {
                break;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (socket!=null) socket.close();
                } catch (Exception e) {
                }
            }
        }
        System.out.println("SimpleAxisServer quitting.");
    }

    // ASCII character mapping to lower case
    private static final byte[] toLower = new byte[256];

    static {
        for (int i = 0; i < 256; i++) {
            toLower[i] = (byte)i;
        }
    
        for (int lc = 'a'; lc <= 'z'; lc++) {
            toLower[lc + 'A' - 'a']=(byte)lc;
        }
    }

    // mime header for content length
    private static final byte lenHeader[] = "content-length: ".getBytes();
    private static final int lenLen = lenHeader.length;

    // mime header for soap action
    private static final byte actionHeader[] = "soapaction: ".getBytes();
    private static final int actionLen = actionHeader.length;
    
    // mime header for cookie
    private static final byte cookieHeader[] = "cookie: ".getBytes();
    private static final int cookieLen = cookieHeader.length;
    
    // mime header for cookie2
    private static final byte cookie2Header[] = "cookie2: ".getBytes();
    private static final int cookie2Len = cookie2Header.length;
    
    // mime header for GET
    private static final byte getHeader[] = "GET".getBytes();

    // mime header for POST
    private static final byte postHeader[] = "POST".getBytes();
    
    // header ender
    private static final byte headerEnder[] = ": ".getBytes();

    // buffer for IO
    private static final int BUFSIZ = 4096;
    private byte buf[] = new byte[BUFSIZ];

    /**
     * Read a single line from the input stream
     * @param is        inputstream to read from
     * @param b         byte array to read into
     * @param off       starting offset into the byte array
     * @param len       maximum number of bytes to read
     */
    private int readLine(InputStream is, byte[] b, int off, int len)
        throws IOException
    {
        int count = 0, c;

        while ((c = is.read()) != -1) {
            b[off++] = (byte)c;
            count++;
            if (c == '\n' || count == len) break;
        }
        return count > 0 ? count : -1;
    }

    /**
     * Read all mime headers, returning the value of Content-Length and
     * SOAPAction.
     * @param is         InputStream to read from
     * @param soapAction StringBuffer to return the soapAction into
     * @param httpRequest StringBuffer for GET / POST
     * @param cookie first cookie header (if doSessions)
     * @param cookie2 second cookie header (if doSessions)
     * @return Content-Length
     */
    private int parseHeaders(InputStream is,
                             StringBuffer soapAction,
                             StringBuffer httpRequest,
                             StringBuffer cookie,
                             StringBuffer cookie2)
      throws IOException
    {
        int n;
        int len = 0;
        
        // parse first line as GET or POST
        n=this.readLine(is, buf, 0, buf.length);
        if (n < 0) {
            // nothing!
            throw new IOException("Unexpected end of stream");
        }
        
        // which does it begin with?
        httpRequest.delete(0, httpRequest.length());
        if (buf[0] == getHeader[0]) {
            httpRequest.append("GET");
            // return immediately, don't look for more headers
            return 0;
        } else if (buf[0] == postHeader[0]) {
            httpRequest.append("POST");
        } else {
            throw new IOException("Cannot handle non-GET, non-POST request");
        }
        
        while ((n=readLine(is,buf,0,buf.length)) > 0) {
        
            // if we are at the separator blank line, bail right now
            if ((n<=2) && (buf[0]=='\n'||buf[0]=='\r') && (len>0)) break;
            
            // RobJ gutted the previous logic; it was too hard to extend for more headers.
            // Now, all it does is search forwards for ": " in the buf,
            // then do a length / byte compare.
            // Hopefully this is still somewhat efficient (Sam is watching!).
            
            // First, search forwards for ": "
            int endHeaderIndex = 0;
            while (endHeaderIndex < n && toLower[buf[endHeaderIndex]] != headerEnder[0]) {
                endHeaderIndex++;
            }
            endHeaderIndex += 2;
            // endHeaderIndex now points _just past_ the ": ", and is
            // comparable to the various lenLen, actionLen, etc. values
            
            // convenience; i gets pre-incremented, so initialize it to one less
            int i = endHeaderIndex - 1;
            
            // which header did we find?
            if (endHeaderIndex == lenLen && matches(buf, lenHeader)) {
                // parse content length
                
                while ((++i<n) && (buf[i]>='0') && (buf[i]<='9')) {
                    len = (len*10) + (buf[i]-'0');
                }
                
            }
            else if (endHeaderIndex == actionLen
                       && matches(buf, actionHeader))
            {
                           
                soapAction.delete(0,soapAction.length());
                // skip initial '"'
                i++;
                while ((++i<n) && (buf[i]!='"')) {
                    soapAction.append((char)(buf[i] & 0x7f));
                }
                           
            }
            else if (doSessions && endHeaderIndex == cookieLen
                       && matches(buf, cookieHeader))
            {
                           
                // keep everything up to first ;
                while ((++i<n) && (buf[i]!=';') && (buf[i]!='\r') && (buf[i]!='\n')) {
                    cookie.append((char)(buf[i] & 0x7f));
                }
                           
            }
            else if (doSessions && endHeaderIndex == cookie2Len
                       && matches(buf, cookie2Header))
            {
                           
                // keep everything up to first ;
                while ((++i<n) && (buf[i]!=';') && (buf[i]!='\r') && (buf[i]!='\n')) {
                    cookie2.append((char)(buf[i] & 0x7f));
                }
                           
            }

        }
        return len;
    }
    
    
    /**
     * does tolower[buf] match the target byte array, up to the target's length?
     */
    public boolean matches (byte[] buf, byte[] target) {
        for (int i = 0; i < target.length; i++) {
            if (toLower[buf[i]] != target[i]) {
                return false;
            }
        }
        return true;
    }


    /**
     * output an integer into the output stream
     * @param out       OutputStream to be written to
     * @param value     Integer value to be written.
     */
    private void putInt(OutputStream out, int value)
        throws IOException
    {
        int len = 0;
        int offset=buf.length;

        // negative numbers
        if (value < 0) {
            buf[--offset] = (byte) '-';
            value=-value;
            len++;
        }

        // zero
        if (value == 0) {
            buf[--offset] = (byte) '0';
            len++;
        }

        // positive numbers
        while (value > 0) {
            buf[--offset] = (byte)(value%10 + '0');
            value=value/10;
            len++;
        }

        // write the result
        out.write(buf, offset, len);
    }

    // per thread socket information
    private ServerSocket serverSocket;
    private volatile Thread worker = null;

    /**
     * Obtain the serverSocket that that SimpleAxisServer is listening on.
     */
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    /**
     * Set the serverSocket this server should listen on.
     * (note : changing this will not affect a running server, but if you
     *  stop() and then start() the server, the new socket will be used).
     */
    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    /**
     * Start this server.
     *
     * Spawns a worker thread to listen for HTTP requests.
     *
     * @param daemon a boolean indicating if the thread should be a daemon.
     */
    public void start(boolean daemon) throws Exception {
        worker = new Thread(this);
        worker.setDaemon(daemon);
        worker.start();
    }

    /**
     * Start this server as a NON-daemon.
     */
    public void start() throws Exception {
        start(false);
    }

    /**
     * Stop this server.
     *
     * This will interrupt any pending accept().
     */
    public void stop() throws Exception {
        stopped = true;
        if (worker != null) worker.interrupt();
    }

    /**
     * Server process.
     * @parms args[1] port number (default is 8080)
     */
    public static void main(String args[]) {

        SimpleAxisServer sas = new SimpleAxisServer();

        //Debug.setDebugLevel(6);
        
        try {
            int port = (args.length==0)? 8080 : Integer.parseInt(args[0]);
            ServerSocket ss = new ServerSocket(port);
            sas.setServerSocket(ss);
            sas.run();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

     }

}
