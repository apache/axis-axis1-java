/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
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

package org.apache.axis.client.http ;

import java.net.*;
import java.io.*;
import java.util.*;

import org.apache.axis.utils.Options ;
import org.apache.axis.encoding.SerializationContext ;
import org.apache.axis.message.SOAPEnvelope ;
import org.apache.axis.message.SOAPBodyElement ;
import org.apache.axis.client.ServiceClient;
import org.apache.axis.client.Transport;
import org.apache.axis.Message ;
import org.apache.axis.MessageContext ;
import org.apache.axis.utils.Debug ;
import org.apache.axis.encoding.ServiceDescription;

/**
 * An admin client object, specific to HTTP.
 *
 * @author Rob Jellinghaus (robj@unrealities.com)
 * @author Doug Davis (dug@us.ibm.com)
 */

public class AdminClient {
    public static void main(String args[]) {
        try {
            new AdminClient().doAdmin(args);
        }
        catch( Exception e ) {
            System.err.println( e );
            e.printStackTrace( System.err );
        }
    }
    
    // do the real work, and throw exception if fubar
    // this is reused by the TestHTTPDeploy functional tests
    public void doAdmin (String[] args)
        throws Exception
    {
        Options opts = new Options( args );
        
        Debug.setDebugLevel( opts.isFlagSet('d') );
        
        args = opts.getRemainingArgs();
        
        if ( args == null ) {
            System.err.println( "Usage: AdminClient xml-files | list" );
            System.exit(1);
        }
        
        for ( int i = 0 ; i < args.length ; i++ ) {
            InputStream input = null ;
            
            if ( args[i].equals("list") ) {
                System.out.println( "Doing a list" );
                String str = "<list/>" ;
                input = new ByteArrayInputStream( str.getBytes() );
            } else if (args[i].equals("quit")) {
                System.out.println("Doing a quit");
                String str = "<quit/>";
                input = new ByteArrayInputStream(str.getBytes());
            }
            else {
                System.out.println( "Processing file: " + args[i] );
                input = new FileInputStream( args[i] );
            }
            
            ServiceClient     hMsg       =
                new ServiceClient(new HTTPTransport(opts.getURL(), "AdminService"));
            
            Message         inMsg      = new Message( input, "BodyInputStream" );
            hMsg.setRequestMessage( inMsg );
            
            if ( opts.isFlagSet('t') > 0 ) hMsg.doLocal = true ;
            hMsg.set( Transport.USER, opts.getUser() );
            hMsg.set( Transport.PASSWORD, opts.getPassword() );
            
            hMsg.invoke();
            
            Message outMsg = hMsg.getMessageContext().getResponseMessage();
            hMsg.getMessageContext().setServiceDescription(new ServiceDescription("Admin", false));
            input.close();
            SOAPEnvelope envelope = (SOAPEnvelope) outMsg.getAs("SOAPEnvelope");
            SOAPBodyElement body = envelope.getFirstBody();
            StringWriter writer = new StringWriter();
            SerializationContext ctx = new SerializationContext(writer, hMsg.getMessageContext());
            body.output(ctx);
            System.out.println(writer.toString());
        }
    }
}

