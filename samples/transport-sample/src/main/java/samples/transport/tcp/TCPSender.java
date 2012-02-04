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

package samples.transport.tcp;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.handlers.BasicHandler;
import org.apache.commons.logging.Log;

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

            reqEnv  = (String) msgContext.getRequestMessage().getSOAPPartAsString();

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
                log.debug( (String) outMsg.getSOAPPartAsString() );
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
