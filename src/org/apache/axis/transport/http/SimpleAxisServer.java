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

/**
 * This is a single threaded implementation of an HTTP server for processing
 * SOAP requests via Apache's xml-axis.  This is not intended for production
 * use.  Its intended uses are for demos, debugging, and performance
 * profiling.
 */
public class SimpleAxisServer {

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

    // Axis specific constants
    private static String transportInName = "HTTPAction";
    private static String transportOutName = "HTTP.output";
    private static final String AXIS_ENGINE = "AxisEngine" ;

    /**
     * Process SOAP requests.  Terminates upon IOException on 
     * ServerSocket.accept.
     * @param ss ServerSocket to listen to
     */
    public void run(ServerSocket ss) {

        // create an Axis server
        AxisServer engine = new AxisServer();
        engine.init();

        // create and initialize a message context
        MessageContext msgContext = new MessageContext();
        Message        requestMsg = new Message("", "String");
        msgContext.setProperty(MessageContext.TRANS_INPUT , transportInName);
        msgContext.setProperty(MessageContext.TRANS_OUTPUT, transportOutName);

        // Reusuable, buffered, content length controlled, InputStream
        NonBlockingBufferedInputStream is = 
            new NonBlockingBufferedInputStream();

        // SoapAction
        StringBuffer soapAction = new StringBuffer();

        // And, just in case it is needed...
        Message faultMsg = new Message(null, "AxisFault");

        // Accept and process requests from the socket
	while (true) {
            Socket socket = null;

            // prepare request (do as much as possible while waiting for the 
            // next connection).  Note the next two statements are commented
            // out.  Uncomment them if you experience any problems with not
            // resetting state between requests:
            //   msgContext = new MessageContext();
            //   requestMsg = new Message("", "String");
            msgContext.setServiceDescription(null);
            msgContext.setRequestMessage(requestMsg);
            msgContext.setResponseMessage(null);
            msgContext.clearProperties();
            msgContext.setProperty(MessageContext.TRANS_INPUT, transportInName);
            msgContext.setProperty(MessageContext.TRANS_OUTPUT, transportOutName);
	    try {
                try {
	            socket = ss.accept();
                } catch (IOException ioe) {
                    break;
                }

                // assume the best
                byte[] status = OK;

                try {
                    // read headers
                    is.setInputStream(socket.getInputStream());
		    int contentLength = parseHeaders(is, soapAction);
                    is.setContentLength(contentLength);

                    // set up request
                    String soapActionString = soapAction.toString();
                    requestMsg.setCurrentMessage(is, "InputStream");
                    msgContext.setTargetService(soapActionString);
                    msgContext.setProperty(HTTPConstants.MC_HTTP_SOAPACTION, 
                                           soapActionString);

                    // invoke the Axis engine
                    engine.invoke(msgContext);

                } catch( AxisFault af ) {
                    if ("Server.Unauthorized".equals(af.getFaultCode())) {
                        status = ISE; // SC_INTERNAL_SERVER_ERROR 
                    } else {
                        status = UNAUTH; // SC_UNAUTHORIZED 
                    }

                    faultMsg.setCurrentMessage(af, "AxisFault");
                    msgContext.setResponseMessage(faultMsg);

                } catch( Exception e ) {
                    status = ISE; // SC_INTERNAL_SERVER_ERROR 
                    faultMsg.setCurrentMessage(new AxisFault(e), "AxisFault");
                    msgContext.setResponseMessage(faultMsg);
                }


                // Retrieve the response from Axis
                Message responseMsg = msgContext.getResponseMessage();
                byte[] response = (byte[]) responseMsg.getAs("Bytes");

                // Send it on its way...
		OutputStream out = socket.getOutputStream();
		out.write(HTTP);
		out.write(status);
		out.write(MIME_STUFF);
	        putInt(out, response.length);
		out.write(SEPARATOR);
                out.write(response);
                out.flush();

	    } catch (Exception e) {
		e.printStackTrace();
	    } finally {
		try {
		    if (socket!=null) socket.close();
		} catch (Exception e) {
		}
	    }
	}
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
    private static final byte actionHeader[] = "soapaction: \"".getBytes();
    private static final int actionLen = actionHeader.length;

    // simple buffer per server
    private final int BUFSIZ = 4096;
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
     * @return Content-Length
     */
    private int parseHeaders(InputStream is, StringBuffer soapAction)
      throws IOException 
    {
        int n;
        int len = 0;
        while ((n=readLine(is,buf,0,buf.length)) > 0) {
        
            int lenMatch = 0;
            int actionMatch = 0;
            for (int i=0; i<n; i++) {
                byte c = toLower[buf[i]];

                if (actionMatch==0 && c==lenHeader[lenMatch]) {
                    lenMatch++;
                    actionMatch=0;
                    if (lenMatch == lenLen) {
                        while ((++i<n) && (buf[i]>='0') && (buf[i]<='9')) {
                            len = (len*10) + (buf[i]-'0');
                        }
                        break;
                    }
                } else if (c==actionHeader[actionMatch]) {
                    lenMatch=0;
                    actionMatch++;
                    if (actionMatch == actionLen) {
                        soapAction.delete(0,soapAction.length());
                        while ((++i<n) && (buf[i]!='"') && (buf[i]!='9')) {
                            soapAction.append((char)(buf[i] & 0x7f));
                        }
                        break;
                    }
                } else {
                    lenMatch=0;
                    actionMatch=0;
                }
            }

            if ((n<=2) && (buf[0]=='\n'||buf[0]=='\r') && (len>0)) break;
        }
        return len;
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

    /**
     * Server process.
     * @parms args[1] port number (default is 8080)
     */
    public static void main(String args[]) {

        SimpleAxisServer sas = new SimpleAxisServer();

        try {
            int port = (args.length==0)? 8080 : Integer.parseInt(args[0]);
            ServerSocket ss = new ServerSocket(port);
            sas.run(ss);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

     }

}
