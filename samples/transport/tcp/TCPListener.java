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

package samples.transport.tcp;

import java.io.*;
import java.util.*;
import org.apache.axis.* ;
import org.apache.axis.server.* ;
import org.apache.axis.utils.* ;
import org.apache.axis.registries.HandlerRegistry;
import org.apache.axis.transport.http.NonBlockingBufferedInputStream;
import org.apache.axis.handlers.soap.SOAPService;

import java.net.*;

/**
 * Listen for incoming socket connections on the specified socket.  Take
 * incoming messages and dispatch them.
 *
 * @author Rob Jellinghaus (robj@unrealities.com)
 * @author Doug Davis (dug@us.ibm.com)
 */
public class TCPListener implements Runnable {
    // These have default values.
    private String transportName = "TCPTransport";
    
    private static final String AXIS_ENGINE = "AxisEngine" ;
    
    private int port;
    private ServerSocket srvSocket;
    
    private AxisEngine engine = null ;
    
    // becomes true when we want to quit
    private boolean done = false;
    
    public static void main (String args[]) {
        new TCPListener(args).run();
    }
    
    public TCPListener (String[] args) {
        // look for -p, -d arguments
        try {
            Options options = new Options(args);
            port = new URL(options.getURL()).getPort();
            String tmp;
            if ((tmp = options.isValueSet('d')) != null) {
                Debug.setDebugLevel(Integer.parseInt(tmp));
            }
        } catch (MalformedURLException ex) {
            System.err.println("Hosed URL: "+ex);
            System.exit(1);
        }
        
        try {
            srvSocket = new ServerSocket(port);
        } catch (IOException ex) {
            System.err.println("Can't create server socket on port "+port);
            System.exit(1);
        }
        
        System.out.println("TCPListener is listening on port "+port+".");
    }
    
    public void run () {
        if (srvSocket == null) {
            return;
        }
        
        Socket sock;
        while (!done) {
            try {
                sock = srvSocket.accept();
                System.out.println("TCPListener received new connection: "+sock);
                new Thread(new SocketHandler(sock)).start();
            } catch (IOException ex) {
                /** stop complaining about this! it seems to happen on quit,
                    and is not worth mentioning.  unless I am confused. -- RobJ
                 System.err.println("Got IOException on srvSocket.accept: "+ex);
                 ex.printStackTrace();
                 */
            }
        }
    }
    
    
    public class SocketHandler implements Runnable {
        private Socket socket;
        public SocketHandler (Socket socket) {
            this.socket = socket;
        }
        public void run () {
            // get the input stream
            if ( engine == null ) {
                engine = new AxisServer();
                engine.init();
                
                HandlerRegistry hr = engine.getHandlerRegistry();
                HandlerRegistry sr = engine.getServiceRegistry();
                // add the TCPSender
                //hr.add("TCPSender", new TCPSender());
                
                SimpleTargetedChain c = new SimpleTargetedChain();
                c.setPivotHandler(new TCPSender());
                
                engine.deployTransport(transportName, c);
            }
            
            /* Place the Request message in the MessagContext object - notice */
            /* that we just leave it as a 'ServletRequest' object and let the  */
            /* Message processing routine convert it - we don't do it since we */
            /* don't know how it's going to be used - perhaps it might not     */
            /* even need to be parsed.                                         */
            /*******************************************************************/
            MessageContext    msgContext = new MessageContext(engine);
            
            InputStream inp;
            try {
                inp = socket.getInputStream();
            } catch (IOException ex) {
                System.err.println("Couldn't get input stream from "+socket);
                return;
            }
            
            // ROBJ 911
            // the plain ol' inputstream seems to hang in the SAX parse..... WHY?????
            // because there is no content length!
            //Message           msg        = new Message( nbbinp, "InputStream" );
            Message msg = null;
            try {
                StringBuffer line = new StringBuffer();
                int b = 0;
                while ((b = inp.read()) != '\r') {
                    line.append((char)b);
                }
                // got to '\r', skip it and '\n'
                if (inp.read() != '\n') {
                    System.err.println("Length line "+line+" was not terminated with \r\n");
                    return;
                }
                
                // TEST SUPPORT ONLY
                // If the line says "ping", then respond "\n".
                // If the line says "quit", then respond "\n" and exit.
                if (line.toString().equals("ping")) {
                    socket.getOutputStream().write(new String("\n").getBytes());
                    return;
                } else if (line.toString().equals("quit")) {
                    // peacefully die
                    socket.getOutputStream().write(new String("\n").getBytes());
                    socket.close();
                    // The following appears to deadlock.  It will get cleaned
                    // up on exit anyway...
                    // srvSocket.close();
                    System.err.println("AxisListener quitting.");
                    System.exit(0);
                }
                
                
                // OK, assume it is content length
                int len = Integer.parseInt(line.toString());
                // read that many bytes into ByteArrayInputStream...
                
                // experiment, doesn't work:
                //        NonBlockingBufferedInputStream nbbinp = new NonBlockingBufferedInputStream();
                //        nbbinp.setContentLength(len);
                //        nbbinp.setInputStream(inp);
                //        msg = new Message(nbbinp, "InputStream");
                
                byte[] mBytes = new byte[len];
                inp.read(mBytes);
                msg = new Message(new ByteArrayInputStream(mBytes));
            } catch (IOException ex) {
                System.err.println("Couldn't read from socket input stream: "+ex);
                return;
            }
            
            
            /* Set the request(incoming) message field in the context */
            /**********************************************************/
            msgContext.setRequestMessage( msg );
            
            /* Set the Transport Specific Request/Response chains IDs */
            /******************************************************/
            msgContext.setTransportName(transportName);
            
            try {
                /* Invoke the Axis engine... */
                /*****************************/
                engine.invoke( msgContext );
            }
            catch( Exception e ) {
                if ( !(e instanceof AxisFault) )
                    e = new AxisFault( e );
                msgContext.setResponseMessage( new Message((AxisFault)e) );
            }
            
            /* Send it back along the wire...  */
            /***********************************/
            msg = msgContext.getResponseMessage();
            String response = (String) msg.getAsString();
            if (msg == null) response="No data";
            try {
                OutputStream buf = new BufferedOutputStream(socket.getOutputStream());
                // this should probably specify UTF-8, but for now, for Java interop,
                // use default encoding
                buf.write(response.getBytes());
                buf.close();
            } catch (IOException ex) {
                System.err.println("Can't write response to socket "+port+", response is: "+response);
            }
        }
    }
}


