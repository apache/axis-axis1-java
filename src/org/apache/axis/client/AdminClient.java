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
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Options;
import org.apache.log4j.Category;

import javax.xml.rpc.JAXRPCException;
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
    static Category category =
            Category.getInstance(AdminClient.class.getName());

    protected PrintWriter _log;
    protected Call call;

    /**
     * Construct an admin client w/o a logger
     */
    public AdminClient()
    {
        try {
            Service service = new Service();
            call = (Call) service.createCall();
        } catch (JAXRPCException e) {
            category.fatal(JavaUtils.getMessage("couldntCall00"), e);
            call = null;
        }
    }

    /**
     * Construct an admin client with a logger
     */
    public AdminClient(PrintWriter log)
    {
        this();
        _log = log;
    }

    /**
     * Construct an admin client with a logger
     */
    public AdminClient(OutputStream out)
    {
        this();
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
     * External access to our Call object
     */
    public Call getCall()
    {
        return call;
    }

    public String list(Options opts) throws Exception { 
        processOpts( opts );
        return list();
    }

    public String list() throws Exception { 
        log( JavaUtils.getMessage("doList00") );
        String               str   = "<m:list xmlns:m=\"AdminService\"/>" ;
        ByteArrayInputStream input = new ByteArrayInputStream(str.getBytes());
        return process(input);
    }

    public String quit(Options opts) throws Exception { 
        processOpts( opts );
        return quit();
    }

    public String quit() throws Exception { 
        log(JavaUtils.getMessage("doQuit00"));
        String               str   = "<m:quit xmlns:m=\"AdminService\"/>";
        ByteArrayInputStream input = new ByteArrayInputStream(str.getBytes());
        return process(input);
    }

    public String undeployHandler(String handlerName) throws Exception { 
        log(JavaUtils.getMessage("doQuit00"));
        String               str   = "<m:undeploy xmlns:m=\"AdminService\">" +
                                     "<handler name=\"" + handlerName + "\"/>"+
                                     "</m:undeploy>" ;
        ByteArrayInputStream input = new ByteArrayInputStream(str.getBytes());
        return process(input);
    }

    public String undeployService(String serviceName) throws Exception { 
        log(JavaUtils.getMessage("doQuit00"));
        String               str   = "<m:undeploy xmlns:m=\"AdminService\">" +
                                     "<service name=\"" + serviceName + "\"/>"+
                                     "</m:undeploy>" ;
        ByteArrayInputStream input = new ByteArrayInputStream(str.getBytes());
        return process(input);
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

    public String process(String[] args) throws Exception {
        StringBuffer sb = new StringBuffer();

        Options opts = new Options( args );
        opts.setDefaultURL("http://localhost:8080/axis/services/AdminService");

        if (opts.isFlagSet('d') > 0) {
            // Set log4j properties... !!!
        }

        args = opts.getRemainingArgs();

        if ( args == null ) {
            log(JavaUtils.getMessage("usage00","AdminClient xml-files | list"));
            return null;
        }

        for ( int i = 0 ; i < args.length ; i++ ) {
            InputStream input = null;

            if ( args[i].equals("list") ) 
              sb.append( list(opts) );
            else if (args[i].equals("quit")) 
              sb.append( quit(opts) );
            else if (args[i].equals("passwd")) {
                log(JavaUtils.getMessage("changePwd00"));
                if (args[i + 1] == null) {
                    log(JavaUtils.getMessage("needPwd00"));
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
                    log( JavaUtils.getMessage("processFile00", args[i]) );
                    sb.append( process(opts, args[i] ) );
                } else {
                    java.util.StringTokenizer tokenizer = null ;
                    tokenizer = new java.util.StringTokenizer(args[i],
                                                 java.io.File.pathSeparator);
                    while(tokenizer.hasMoreTokens()) {
                        String file = tokenizer.nextToken();
                        log( JavaUtils.getMessage("processFile00", file) );
                        sb.append( process(opts, file) );
                        if(tokenizer.hasMoreTokens())
                            sb.append("\n");
                    }
                }
            }
        }

        return sb.toString();
    }

    public void processOpts(Options opts) throws Exception {
        if (call == null) 
            throw new Exception(JavaUtils.getMessage("nullCall00"));

        call.setTargetEndpointAddress( new URL(opts.getURL()) );
        call.setProperty( Transport.USER, opts.getUser() );
        call.setProperty( Transport.PASSWORD, opts.getPassword() );

        String tName = opts.isValueSet( 't' );
        if ( tName != null && !tName.equals("") )
            call.setProperty( Call.TRANSPORT_NAME, tName );
    }

    public String  process(InputStream input) throws Exception { 
        return process(null, input );
    }

    public String process(URL xmlURL) throws Exception { 
        return process(null, xmlURL.openStream() );
    }

    public String process(String xmlFile) throws Exception { 
        FileInputStream in     = new FileInputStream(xmlFile);
        String          result =  process(null, in );
        in.close();
        return result ;
    }

    public String process(Options opts, String xmlFile)  throws Exception {
        processOpts( opts );
        return process( xmlFile );
    }

    public String process(Options opts, InputStream input)  throws Exception {
        if (call == null) 
            throw new Exception(JavaUtils.getMessage("nullCall00"));

        if ( opts != null ) processOpts( opts );
        
        call.setProperty( HTTPConstants.MC_HTTP_SOAPACTION, "AdminService");

        Vector result = null ;
        Object[]  params = new Object[] { new SOAPBodyElement(input) };
        result = (Vector) call.invoke( params );

        input.close();

        if (result == null || result.isEmpty()) 
            throw new AxisFault(JavaUtils.getMessage("nullResponse00"));

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

