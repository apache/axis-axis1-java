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
import org.apache.axis.EngineConfiguration;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Options;
import org.apache.axis.deployment.wsdd.WSDDConstants;

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.components.i18n.Messages;
import org.apache.commons.logging.Log;

import javax.xml.rpc.ServiceException;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Vector;


/**
 * An admin client object that can be used both from the command line
 * and programmatically.
 *
 * @author Rob Jellinghaus (robj@unrealities.com)
 * @author Doug Davis (dug@us.ibm.com)
 * @author Simeon Simeonov (simeons@macromedia.com)
 */

public class AdminClient
{
    protected static Log log =
        LogFactory.getLog(AdminClient.class.getName());

    private static ThreadLocal defaultConfiguration = new ThreadLocal();

    /**
     * If the user calls this with an EngineConfiguration object, all
     * AdminClients on this thread will use that EngineConfiguration
     * rather than the default one.  This is primarily to enable the
     * deployment of custom transports and handlers.
     *
     * @param config the EngineConfiguration which should be used
     */
    public static void setDefaultConfiguration(EngineConfiguration config)
    {
        defaultConfiguration.set(config);
    }

    protected Call call;

    /**
     * Construct an admin client w/o a logger
     */
    public AdminClient()
    {
        try {
            // Initialize our Service - allow the user to override the
            // default configuration with a thread-local version (see
            // setDefaultConfiguration() above)
            EngineConfiguration config =
                    (EngineConfiguration)defaultConfiguration.get();
            Service service;
            if (config != null) {
                service = new Service(config);
            } else {
                service = new Service();
            }
            call = (Call) service.createCall();
        } catch (ServiceException e) {
            log.fatal(Messages.getMessage("couldntCall00"), e);
            call = null;
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
        log.debug( Messages.getMessage("doList00") );
        String               str   = "<m:list xmlns:m=\"" + WSDDConstants.URI_WSDD + "\"/>" ;
        ByteArrayInputStream input = new ByteArrayInputStream(str.getBytes());
        return process(input);
    }

    public String quit(Options opts) throws Exception { 
        processOpts( opts );
        return quit();
    }

    protected static final String ROOT_UNDEPLOY= WSDDConstants.QNAME_UNDEPLOY.getLocalPart(); 

    public String quit() throws Exception { 
        log.debug(Messages.getMessage("doQuit00"));
        String               str   = "<m:quit xmlns:m=\"" + WSDDConstants.URI_WSDD + "\"/>";
        ByteArrayInputStream input = new ByteArrayInputStream(str.getBytes());
        return process(input);
    }

    public String undeployHandler(String handlerName) throws Exception { 
        log.debug(Messages.getMessage("doQuit00"));
        String               str   = "<m:"+ROOT_UNDEPLOY +" xmlns:m=\"" + WSDDConstants.URI_WSDD + "\">" +
                                     "<handler name=\"" + handlerName + "\"/>"+
                                     "</m:"+ROOT_UNDEPLOY +">" ;
        ByteArrayInputStream input = new ByteArrayInputStream(str.getBytes());
        return process(input);
    }

    public String undeployService(String serviceName) throws Exception { 
        log.debug(Messages.getMessage("doQuit00"));
        String               str   = "<m:"+ROOT_UNDEPLOY +" xmlns:m=\"" + WSDDConstants.URI_WSDD + "\">" +
                                     "<service name=\"" + serviceName + "\"/>"+
                                     "</m:"+ROOT_UNDEPLOY +">" ;
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
            // Set logger properties... !!!
        }

        args = opts.getRemainingArgs();

        if ( args == null ) {
            log.info(Messages.getMessage("usage00","AdminClient xml-files | list"));
            return null;
        }

        for ( int i = 0 ; i < args.length ; i++ ) {
            InputStream input = null;

            if ( args[i].equals("list") ) 
              sb.append( list(opts) );
            else if (args[i].equals("quit")) 
              sb.append( quit(opts) );
            else if (args[i].equals("passwd")) {
                log.info(Messages.getMessage("changePwd00"));
                if (args[i + 1] == null) {
                    log.error(Messages.getMessage("needPwd00"));
                    return null;
                }
                String str = "<m:passwd xmlns:m=\"http://xml.apache.org/axis/wsdd/\">";
                str += args[i + 1];
                str += "</m:passwd>";
                input = new ByteArrayInputStream(str.getBytes());
                i++;
                sb.append( process(opts, input) );
            }
            else {
                if(args[i].indexOf(java.io.File.pathSeparatorChar)==-1){
                    log.info( Messages.getMessage("processFile00", args[i]) );
                    sb.append( process(opts, args[i] ) );
                } else {
                    java.util.StringTokenizer tokenizer = null ;
                    tokenizer = new java.util.StringTokenizer(args[i],
                                                 java.io.File.pathSeparator);
                    while(tokenizer.hasMoreTokens()) {
                        String file = tokenizer.nextToken();
                        log.info( Messages.getMessage("processFile00", file) );
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
            throw new Exception(Messages.getMessage("nullCall00"));

        call.setTargetEndpointAddress( new URL(opts.getURL()) );
        call.setUsername( opts.getUser() );
        call.setPassword( opts.getPassword() );

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
            throw new Exception(Messages.getMessage("nullCall00"));

        if ( opts != null ) processOpts( opts );
        
        call.setUseSOAPAction( true);
        call.setSOAPActionURI( "AdminService");

        Vector result = null ;
        Object[]  params = new Object[] { new SOAPBodyElement(input) };
        result = (Vector) call.invoke( params );

        input.close();

        if (result == null || result.isEmpty()) 
            throw new AxisFault(Messages.getMessage("nullResponse00"));

        SOAPBodyElement body = (SOAPBodyElement) result.elementAt(0);
        return body.toString();
    }

    /**
     * Creates in instance of <code>AdminClient</code> and
     * invokes <code>process(args)</code>.
     * <p>Diagnostic output goes to <code>log.info</code>.</p>
     * @param args Commands to process
     */
    public static void main (String[] args)
    {
        try {
            AdminClient admin = new AdminClient();

            String result = admin.process(args);
            if (result != null)
                log.info(result);
            else
                System.exit(1);
        } catch (Exception e) {
            log.error(Messages.getMessage("exception00"), e);
            System.exit(1);
        }
    }
}

