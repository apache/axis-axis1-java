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

package org.apache.axis.client ;

import org.apache.axis.AxisFault;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.utils.Options;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Vector;

/**
 * An admin client object that can be used both from the command line
 * and programmatically. The admin client supports simple logging that
 * allows the output of its operations to be inspected in environments
 * where <code>System.out</code> and <code>System.err</code> should
 * not be used.
 *
 * @author Rob Jellinghaus (robj@unrealities.com)
 * @author Doug Davis (dug@us.ibm.com)
 * @author Simeon Simeonov (simeons@macromedia.com)
 */

public class AdminClient
{
    protected PrintWriter _log;

    /**
     * Construct an admin client w/o a logger
     */
    public AdminClient()
    {
    }

    /**
     * Construct an admin client with a logger
     */
    public AdminClient(PrintWriter log)
    {
        _log = log;
    }

    /**
     * Construct an admin client with a logger
     */
    public AdminClient(OutputStream out)
    {
        _log = new PrintWriter(out);
    }

    /**
     * Logs a message if a logger has been provided
     */
    protected void log(String msg) throws IOException
    {
        if (_log != null)
        {
            _log.println(msg);
            _log.flush();
        }
    }

    /**
     * <p>Processes a set of administration commands.</p>
     * <p>The following commands are available:</p>
     * <ul>
     *   <li><code>-l<i>url</i></code> sets the AxisServlet URL</li>
     *   <li><code>-h<i>hostName</i></code> sets the AxisServlet host</li>
     *   <li><code>-p<i>portNumber</i></code> sets the AxisServlet port</li>
     *   <li><code>-s<i>servletPath</i></code> sets the path to the
     *              AxisServlet</li>
     *   <li><code>-f<i>fileName</i></code> specifies that a simple file
     *              protocol should be used</li>
     *   <li><code>-u<i>username</i></code> sets the username</li>
     *   <li><code>-p<i>password</i></code> sets the password</li>
     *   <li><code>-d</code> sets the debug flag (for instance, -ddd would
     *      set it to 3)</li>
     *   <li><code>-t<i>name</i></code> sets the transport chain touse</li>
     *   <li><code>list</code> will list the currently deployed services</li>
     *   <li><code>quit</code> will quit (???)</li>
     *   <li><code>passwd <i>value</i></code> changes the admin password</li>
     *   <li><code><i>xmlConfigFile</i></code> deploys or undeploys
     *       Axis components and web services</li>
     * </ul>
     * <p>If <code>-l</code> or <code>-h -p -s</code> are not set, the
     * AdminClient will invoke
     * <code>http://localhost:8080/axis/servlet/AxisServlet</code>.</p>
     *
     * @param args Commands to process
     * @return XML result or null in case of failure. In the case of multiple
     * commands, the XML results will be concatenated, separated by \n
     * @exception Exception Could be an IO exception, an AxisFault or something else
     */
    public String process (String[] args) throws Exception
    {
        StringBuffer sb = new StringBuffer();

        Options opts = new Options( args );

        if (opts.isFlagSet('d') > 0) {
            // Set log4j properties... !!!
        }

        args = opts.getRemainingArgs();

        if ( args == null ) {
            log( "Usage: AdminClient xml-files | list" );
            return null;
        }

        for ( int i = 0 ; i < args.length ; i++ )
        {
            InputStream input = null;

            if ( args[i].equals("list") ) {
                log( "Doing a list" );
                String str = "<m:list xmlns:m=\"AdminService\"/>" ;
                input = new ByteArrayInputStream( str.getBytes() );
                sb.append( process(opts, input) );
            } else if (args[i].equals("quit")) {
                log("Doing a quit");
                String str = "<m:quit xmlns:m=\"AdminService\"/>";
                input = new ByteArrayInputStream(str.getBytes());
                sb.append( process(opts, input) );
            } else if (args[i].equals("passwd")) {
                log("Changing admin password");
                if (args[i + 1] == null) {
                    log("Must specify a password!");
                    return null;
                }
                String str = "<m:passwd xmlns:m=\"AdminService\">";
                str += args[i + 1];
                str += "</m:passwd>";
                input = new ByteArrayInputStream(str.getBytes());
                i++;
                sb.append( process(opts, input) );
            }
            else {
                if(args[i].indexOf(java.io.File.pathSeparatorChar)==-1){
                    log( "Processing file: " + args[i] );
                    input = new FileInputStream( args[i] );
                    sb.append( process(opts, input) );
                } else {
                    java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(args[i],java.io.File.pathSeparator);
                    while(tokenizer.hasMoreTokens()) {
                        String file = tokenizer.nextToken();
                        log( "Processing file: " + file );
                        input = new FileInputStream( file );
                        sb.append( process(opts, input) );
                        if(tokenizer.hasMoreTokens())
                            sb.append("\n");
                    }
                }
            }
        }

        return sb.toString();
    }

    public static String process(Options opts, InputStream input)  throws Exception
    {
        Service service = new Service();
        Call    call = (org.apache.axis.client.Call) service.createCall();

        call.setTargetEndpointAddress( new URL(opts.getURL()) );
        call.setProperty( HTTPConstants.MC_HTTP_SOAPACTION, "AdminService");
        call.setProperty( Transport.USER, opts.getUser() );
        call.setProperty( Transport.PASSWORD, opts.getPassword() );

        String tName = opts.isValueSet( 't' );
        if ( tName != null && !tName.equals("") )
            call.setProperty( Call.TRANSPORT_NAME, tName );

        Vector result = null ;
        Object[]  params = new Object[] { new SOAPBodyElement(input) };
        result = (Vector) call.invoke( params );

        input.close();

        if (result == null || result.isEmpty()) {
            throw new AxisFault("Null response message!");
        }

        SOAPBodyElement body = (SOAPBodyElement) result.elementAt(0);
        return body.toString();
    }

    /**
     * Creates in instance of <code>AdminClient</code> and
     * invokes <code>process(args)</code>.
     * <p>Diagnostic output goes to <code>System.out</code>.</p>
     * @param args Commands to process
     */
    public static void main (String[] args)
    {
        try {
            AdminClient admin = new AdminClient(System.err);
            String result = admin.process(args);
            if (result != null)
                System.out.println(result);
            else
                System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}

