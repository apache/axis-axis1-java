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

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * This is meant to be used on a SOAP Client to call a SOAP server.
 *
 * @author Rob Jellinghaus (robj@unrealities.com)
 * @author Doug Davis (dug@us.ibm.com)
 */
public class TCPSender extends BasicHandler {
    static Log log =
            LogFactory.getLog(TCPSender.class.getName());

    public void invoke(MessageContext msgContext) throws AxisFault {
        log.info( "Enter: TCPSender::invoke" );

        /* Find the service we're invoking so we can grab it's options */
        /***************************************************************/
        String   targetURL = null ;
        Message  outMsg    = null ;
        String   reqEnv    = null ;

        targetURL = msgContext.getStrProp( MessageContext.TRANS_URL);
        try {
            String   host   = msgContext.getStrProp(TCPTransport.HOST);
            int      port   = Integer.parseInt(msgContext.getStrProp(TCPTransport.PORT));
            byte[]   buf    = new byte[4097];
            int      rc     = 0 ;

            Socket             sock = null ;

            sock    = new Socket( host, port );
            log.info( "Created an insecure HTTP connection");

            reqEnv  = (String) msgContext.getRequestMessage().getSOAPPart().getAsString();

            //System.out.println("Msg: " + reqEnv);

            BufferedInputStream inp = new BufferedInputStream(sock.getInputStream());
            OutputStream  out  = sock.getOutputStream();

            byte[] bytes = reqEnv.getBytes();
            String length = "" + bytes.length + "\r\n";
            out.write(length.getBytes());
            out.write( bytes );
            out.flush();

            log.debug( "XML sent:" );
            log.debug( "---------------------------------------------------");
            log.debug( reqEnv );

            if ( false ) {
                // Special case - if the debug level is this high then something
                // really bad must be going on - so just dump the input stream
                // to stdout.
                byte b;
                while ( (b = (byte) inp.read()) != -1 )
                    System.err.print((char)b);
                System.err.println("");
            }

            outMsg = new Message( inp );
            if (log.isDebugEnabled()) {
                log.debug( "\nNo Content-Length" );
                log.debug( "\nXML received:" );
                log.debug( "-----------------------------------------------");
                log.debug( (String) outMsg.getSOAPPart().getAsString() );
            }

            msgContext.setResponseMessage( outMsg );
        }
        catch( Exception e ) {
            log.error( e );
            e.printStackTrace();
            throw AxisFault.makeFault(e);
        }
        log.info( "Exit: TCPSender::invoke" );
    }
};
