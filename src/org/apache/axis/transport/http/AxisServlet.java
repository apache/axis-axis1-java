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

package org.apache.axis.transport.http ;

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.AxisProperties;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.configuration.ServletEngineConfigurationFactory;
import org.apache.axis.security.servlet.ServletSecurityProvider;
import org.apache.axis.server.AxisServer;
import org.apache.axis.utils.Admin;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.XMLUtils;

import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

import org.w3c.dom.Document;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUtils;
import javax.xml.soap.SOAPException;

import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.ArrayList;

/**
 *
 * @author Doug Davis (dug@us.ibm.com)
 */
public class AxisServlet extends HttpServlet
{
    protected static Log log =
        LogFactory.getLog(AxisServlet.class.getName());
    private static Log tlog =
        LogFactory.getLog("org.apache.axis.TIME");

    public static final String INIT_PROPERTY_TRANSPORT_NAME =
        "transport.name";

    public static final String INIT_PROPERTY_USE_SECURITY =
        "use-servlet-security";
    public static final String INIT_PROPERTY_ENABLE_LIST =
        "axis.enableListQuery";

    public static final String INIT_PROPERTY_JWS_CLASS_DIR =
        "axis.jws.servletClassDir";
    
    private static final String ATTR_AXIS_ENGINE =
        "AxisEngine" ;

    // These have default values.
    private String transportName;
    private AxisServer axisServer = null;
    private ServletSecurityProvider securityProvider = null;

    private static boolean isDebug = false;

    /**
     * Should we enable the "?list" functionality on GETs?  (off by
     * default because deployment information is a potential security
     * hole)
     */
    private boolean enableList = false;

    // Cached path to our WEB-INF directory
    private String webInfPath = null;
    protected String getWebInfPath() { return webInfPath; }
    
    // Cached path to JWS output directory
    private String jwsClassDir = null;
    protected String getJWSClassDir() { return jwsClassDir; }
    
    // Cached path to our "root" dir
    private String homeDir = null;
    protected String getHomeDir() { return homeDir; }
    

    public AxisServlet() {
    }

    public void init() {
        ServletContext context = getServletConfig().getServletContext();

        webInfPath = context.getRealPath("/WEB-INF");
        homeDir = context.getRealPath("/");
        
        isDebug= log.isDebugEnabled();
        if(isDebug) log.debug("In servlet init");

        transportName = getOption(context,
                                  INIT_PROPERTY_TRANSPORT_NAME,
                                  HTTPTransport.DEFAULT_TRANSPORT_NAME);

        if (JavaUtils.isTrueExplicitly(getOption(context, INIT_PROPERTY_USE_SECURITY, null))) {
            securityProvider = new ServletSecurityProvider();
        }

        enableList =
            JavaUtils.isTrueExplicitly(getOption(context, INIT_PROPERTY_ENABLE_LIST, null));

        jwsClassDir = getOption(context, INIT_PROPERTY_JWS_CLASS_DIR, null);
        if (jwsClassDir != null) {
            jwsClassDir = homeDir + jwsClassDir;
        } else {
            jwsClassDir = getDefaultJWSClassDir();
        }
    }

    /**
     * Destroy method is called when the servlet is going away.  Pass this
     * down to the AxisEngine to let it clean up...  But don't create the
     * engine if it hasn't already been created.
     */
    public void destroy() {
        super.destroy();
        if (axisServer != null) {
            axisServer.cleanup();
        }
    }

    public AxisServer getEngine() throws AxisFault {
        if (axisServer == null)
            axisServer = getEngine(this);
        return axisServer;
    }

    /**
     * This is a uniform method of initializing AxisServer in a servlet
     * context.
     */
    static public AxisServer getEngine(HttpServlet servlet) throws AxisFault
    {
        AxisServer engine = null;
        if (isDebug)
            log.debug("Enter: getEngine()");

        ServletContext context = servlet.getServletContext();
        synchronized (servlet) {
            engine = (AxisServer)context.getAttribute(ATTR_AXIS_ENGINE);
            if (engine == null) {
                Map environment = getEngineEnvironment(servlet);

                // Obtain an AxisServer by using whatever AxisServerFactory is
                // registered.  The default one will just use the provider we
                // passed in, and presumably JNDI ones will use the ServletContext
                // to figure out a JNDI name to look up.
                //
                // The point of doing this rather than just creating the server
                // manually with the provider above is that we will then support
                // configurations where the server instance is managed by the
                // container, and pre-registered in JNDI at deployment time.  It
                // also means we put the standard configuration pattern in one
                // place.
                engine = AxisServer.getServer(environment);
                context.setAttribute(ATTR_AXIS_ENGINE, engine);
            }
        }

        if (isDebug)
            log.debug("Exit: getEngine()");

        return engine;
    }

    
    private static Map getEngineEnvironment(HttpServlet servlet) {
        Map environment = new HashMap();
        
        String attdir= servlet.getInitParameter(AxisEngine.ENV_ATTACHMENT_DIR);
        if (attdir != null)
            environment.put(AxisEngine.ENV_ATTACHMENT_DIR, attdir);

        ServletContext context = servlet.getServletContext();
        environment.put(AxisEngine.ENV_SERVLET_CONTEXT, context);

        String webInfPath = context.getRealPath("/WEB-INF");
        if (webInfPath != null)
            environment.put(AxisEngine.ENV_SERVLET_REALPATH,
                            webInfPath + File.separator + "attachments");
        
        EngineConfiguration config =
            (new ServletEngineConfigurationFactory(context))
            .getServerEngineConfig();

        environment.put(EngineConfiguration.PROPERTY_NAME, config);
        
        return environment;
    }


    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException
    {
        if (isDebug) 
            log.debug("Enter: doGet()");

        PrintWriter writer = res.getWriter();

        try
        {
            AxisEngine engine = getEngine();
            ServletContext servletContext =
                getServletConfig().getServletContext();
            
            String pathInfo = req.getPathInfo();
            String realpath = servletContext.getRealPath(req.getServletPath());
            if (realpath == null) {
                realpath = req.getServletPath();
            }
            
            boolean wsdlRequested = false;
            boolean listRequested = false;

            // check first if we are doing WSDL or a list operation
            String queryString = req.getQueryString();
            if (queryString != null) {
                if (queryString.equalsIgnoreCase("wsdl")) {
                    wsdlRequested = true;
                } else if (queryString.equalsIgnoreCase("list")) {
                    listRequested = true;
                }
            }

            // If the user requested the servlet (i.e. /axis/services/)
            // with no service name, present the user with a list of deployed 
            // services to be helpful
            // Don't do this if we are doing WSDL or list.
            if (!wsdlRequested && !listRequested &&
                (pathInfo == null || pathInfo.equals(""))) {
                res.setContentType("text/html");
                writer.println("<h2>And now... Some Services</h2>");
                Iterator i = engine.getConfig().getDeployedServices();
                writer.println("<ul>");
                while (i.hasNext()) {
                    ServiceDesc sd = (ServiceDesc)i.next();
                    StringBuffer sb = new StringBuffer();
                    sb.append("<li>");
                    sb.append(sd.getName());
                    sb.append(" <a href=\"../services/");
                    sb.append(sd.getName());
                    sb.append("?wsdl\"><i>(wsdl)</i></a></li>");
                    writer.println(sb.toString());
                    ArrayList operations = sd.getOperations();
                    if (!operations.isEmpty()) {
                        writer.println("<ul>");
                        for (Iterator it = operations.iterator(); it.hasNext();) {
                            OperationDesc desc = (OperationDesc) it.next();
                            writer.println("<li>" + desc.getName());
                        }
                        writer.println("</ul>");
                    }
                }
                writer.println("</ul>");
            } else if (realpath != null) {
                // We have a pathname, so now we perform WSDL or list operations 
                
                // get message context w/ various properties set
                MessageContext msgContext = createMessageContext(engine, req, res);
    
                try {
                    // NOTE:  HttpUtils.getRequestURL has been deprecated.
                    // This line SHOULD be:
                    //    String url = req.getRequestURL().toString()
                    // HOWEVER!!!!  DON'T REPLACE IT!  There's a bug in
                    // req.getRequestURL that is not in HttpUtils.getRequestURL
                    // req.getRequestURL returns "localhost" in the remote
                    // scenario rather than the actual host name.
                    //
                    // ? Still true?  For which JVM's?
                    String url = HttpUtils.getRequestURL(req).toString();

                    msgContext.setProperty(MessageContext.TRANS_URL, url);
                    
                    if (wsdlRequested) {
                        // Do WSDL generation
                        engine.generateWSDL(msgContext);
                        Document doc = (Document) msgContext.getProperty("WSDL");
                        if (doc != null) {
                            res.setContentType("text/xml");
                            XMLUtils.DocumentToWriter(doc, writer);
                        } else {
                            res.setContentType("text/html");
                            writer.println("<h2>" +
                                           JavaUtils.getMessage("error00") +
                                           "</h2>");
                            writer.println("<p>" +
                                           JavaUtils.getMessage("noWSDL00") +
                                           "</p>");
                        }
                    } else if (listRequested) {
                        // Do list, if it is enabled
                        if (enableList) {
                            Document doc = Admin.listConfig(engine);
                            if (doc != null) {
                                res.setContentType("text/xml");
                                XMLUtils.DocumentToWriter(doc, writer);
                            } else {
                                res.setContentType("text/html");
                                writer.println("<h2>" +
                                               JavaUtils.getMessage("error00") +
                                               "</h2>");
                                writer.println("<p>" +
                                               JavaUtils.getMessage("noDeploy00") +
                                               "</p>");
                            }
                        } else {
                            // list not enable, return error
                            res.setContentType("text/html");
                            writer.println("<h2>" +
                                           JavaUtils.getMessage("error00") +
                                           "</h2>");
                            writer.println("<p><i>?list</i>" +
                                           JavaUtils.getMessage("disabled00") +
                                           "</p>");
                        }
                    } else if (req.getParameterNames().hasMoreElements()) {
                        // If we have ?method=x&param=y in the URL, make a stab
                        // at invoking the method with the parameters specified
                        // in the URL
                        
                        res.setContentType("text/html");
                        Enumeration enum = req.getParameterNames();
                        String method = null;
                        String args = "";
                        while (enum.hasMoreElements()) {
                            String param = (String) enum.nextElement();
                            if (param.equalsIgnoreCase("method")) {
                                method = req.getParameter(param);
                            } else {
                                args += "<" + param + ">" +
                                    req.getParameter(param) +
                                    "</" + param + ">";
                            }
                        }
                        
                        if (method == null) {
                            writer.println("<h2>" +
                                           JavaUtils.getMessage("error00") +
                                           ":  " +
                                           JavaUtils.getMessage("invokeGet00") +
                                           "</h2>");
                            writer.println("<p>" +
                                           JavaUtils.getMessage("noMethod01") +
                                           "</p>");
                        } else {
                            String body =
                                "<" + method + ">" + args + "</" + method + ">";

                            String msgtxt =
                                "<SOAP-ENV:Envelope" +
                                " xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                                "<SOAP-ENV:Body>" + body + "</SOAP-ENV:Body>" +
                                "</SOAP-ENV:Envelope>";

                            ByteArrayInputStream istream =
                                new ByteArrayInputStream(msgtxt.getBytes());

                            Message msg = new Message(istream, false);
                            msgContext.setRequestMessage(msg);

//                    if (msg != null) {
//                        writer.println(msg.getAsString());
//                        return;
//                    }
                            engine.invoke(msgContext);
                            Message respMsg = msgContext.getResponseMessage();
                            if (respMsg != null) {
                                writer.println("<p>" +
                                       JavaUtils.getMessage("gotResponse00") +
                                               "</p>");
                                writer.println(respMsg.getSOAPPartAsString());
                            } else {
                                writer.println("<p>" +
                                       JavaUtils.getMessage("noResponse01") +
                                               "</p>");
                            }
                        }
                    } else {
                        res.setContentType("text/html");

                        // See if we can locate the desired service.  If we
                        // can't, return a 404 Not Found.  Otherwise, just
                        // print the placeholder message.

                        String serviceName;
                        if (pathInfo.startsWith("/")) {
                            serviceName = pathInfo.substring(1);
                        } else {
                            serviceName = pathInfo;
                        }

                        SOAPService s = engine.getService(serviceName);
                        if (s == null) {
                            // Outta here, no such service....
                            res.setStatus(404);
                            return;
                        }

                        writer.println("<h1>" + serviceName +
                                       "</h1>");
                        writer.println(
                                "<p>" +
                                JavaUtils.getMessage("axisService00") +
                                "</p>");
                        writer.println(
                                "<i>" +
                                JavaUtils.getMessage("perhaps00") +
                                "</i>");
                    }
                } catch (AxisFault fault) {
                    res.setContentType("text/html");
                    writer.println("<h2>" +
                                   JavaUtils.getMessage("error00") + "</h2>");
                    writer.println("<p>" +
                                   JavaUtils.getMessage("somethingWrong00") +
                                   "</p>");
                    writer.println("<pre>Fault - " + fault.toString() + " </pre>");
                    writer.println("<pre>" + fault.dumpToString() + " </pre>");
                } catch (Exception e) {
                    res.setContentType("text/html");
                    writer.println("<h2>" +
                                   JavaUtils.getMessage("error00") +
                                   "</h2>");
                    writer.println("<p>" +
                                   JavaUtils.getMessage("somethingWrong00") +
                                   "</p>");
                    writer.println("<pre>Exception - " + e + "<br>");
                    writer.println(JavaUtils.stackToString(e));
                    writer.println("</pre>");
                }
            }
            else
            {
                // We didn't have a real path in the request, so just
                // print a message informing the user that they reached
                // the servlet.
                
                res.setContentType("text/html");
                writer.println( "<html><h1>Axis HTTP Servlet</h1>" );
                writer.println( JavaUtils.getMessage("reachedServlet00"));

                writer.println("<p>" +
                               JavaUtils.getMessage("transportName00",
                                         "<b>" + transportName + "</b>"));
                writer.println("</html>");
            }
        } finally {
            writer.close();

            if (isDebug) 
                log.debug("Exit: doGet()");
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException
    {
        long t0=0, t1=0, t2=0, t3=0, t4=0;
        String soapAction=null;
        MessageContext msgContext=null;
        if (isDebug) 
            log.debug("Enter: doPost()");
        if( tlog.isDebugEnabled() ) {
            t0=System.currentTimeMillis();
        }
        
        Message responseMsg = null;
        
        try {
            AxisEngine engine = getEngine();
            
            if (engine == null) {
                // !!! should return a SOAP fault...
                ServletException se =
                    new ServletException(JavaUtils.getMessage("noEngine00"));
                log.debug("No Engine!", se);
                throw se; 
            }
        
            res.setBufferSize(1024 * 8); // provide performance boost.       

            /** get message context w/ various properties set
             */
            msgContext = createMessageContext(engine, req, res);
    
            // ? OK to move this to 'getMessageContext',
            // ? where it would also be picked up for 'doGet()' ?
            if (securityProvider != null) {
                if (isDebug) log.debug("securityProvider:" + securityProvider);
                msgContext.setProperty("securityProvider", securityProvider);
            }

            /* Get request message
             */    
            Message requestMsg =
                new Message(req.getInputStream(),
                            false,
                            req.getHeader(HTTPConstants.HEADER_CONTENT_TYPE),
                            req.getHeader(HTTPConstants.HEADER_CONTENT_LOCATION));
                           
            if(isDebug) log.debug("Request Message:" + requestMsg);
            
            /* Set the request(incoming) message field in the context */
            /**********************************************************/
            msgContext.setRequestMessage(requestMsg);
        
            try {
                /**
                 * Save the SOAPAction header in the MessageContext bag.
                 * This will be used to tell the Axis Engine which service
                 * is being invoked.  This will save us the trouble of
                 * having to parse the Request message - although we will
                 * need to double-check later on that the SOAPAction header
                 * does in fact match the URI in the body.
                 */
                // (is this last stmt true??? (I don't think so - Glen))  
                /********************************************************/
                soapAction = getSoapAction(req);
    
                if (soapAction != null) {
                    msgContext.setUseSOAPAction(true);
                    msgContext.setSOAPActionURI(soapAction);
                }
    
                // Create a Session wrapper for the HTTP session.
                // These can/should be pooled at some point.
                // (Sam is Watching! :-)
                msgContext.setSession(new AxisHttpSession(req));
    
                if( tlog.isDebugEnabled() ) {
                    t1=System.currentTimeMillis();
                }
                /* Invoke the Axis engine... */
                /*****************************/
                if(isDebug) log.debug("Invoking Axis Engine.");
                engine.invoke(msgContext);
                if(isDebug) log.debug("Return from Axis Engine.");
                if( tlog.isDebugEnabled() ) {
                    t2=System.currentTimeMillis();
                }
            
                responseMsg = msgContext.getResponseMessage();
            } catch (AxisFault e) {
                log.error(JavaUtils.getMessage("exception00"), e);
                // It's been suggested that a lack of SOAPAction
                // should produce some other error code (in the 400s)...
                res.setStatus(getHttpServletResponseStatus(e));
                responseMsg = new Message(e);
            } catch (Exception e) {
                log.error(JavaUtils.getMessage("exception00"), e);
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                responseMsg = new Message(AxisFault.makeFault(e));
            }
        } catch (AxisFault fault) {
            log.error(JavaUtils.getMessage("axisFault00"), fault);
            responseMsg = new Message(fault);
        }
        if( tlog.isDebugEnabled() ) {
            t3=System.currentTimeMillis();
        }

        /* Send response back along the wire...  */
        /***********************************/
        if (responseMsg != null)
            sendResponse(getProtocolVersion(req), res, responseMsg);
            
        if (isDebug) {
            log.debug("Response sent.");
            log.debug("Exit: doPost()");
        }
        if( tlog.isDebugEnabled() ) {
            t4=System.currentTimeMillis();
            tlog.debug("axisServlet.doPost: " + soapAction +
                       " pre=" + (t1-t0) +
                       " invoke=" + (t2-t1) +
                       " post=" + (t3-t2) +
                       " send=" + (t4-t3) +
                       " " + msgContext.getTargetService() + "." +
                        ((msgContext.getOperation( ) == null) ?
                        "" : msgContext.getOperation().getName()) );
        }

    }
    
    /**
     * Extract information from AxisFault and map it to a HTTP Status code.
     * 
     * @param af Axis Fault
     * @return HTTP Status code.
     */
    protected int getHttpServletResponseStatus(AxisFault af) {
        // Should really be doing this with explicit AxisFault
        // subclasses... --Glen
        return af.getFaultCode().getLocalPart().equals("Server.Unauthorized")
                ? HttpServletResponse.SC_UNAUTHORIZED
                : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }

    private void sendResponse(final String clientVersion,
            HttpServletResponse res, Message responseMsg)
        throws AxisFault, IOException
    {
        if (responseMsg == null) {
            res.setStatus(HttpServletResponse.SC_NO_CONTENT);
            if(isDebug) log.debug("NO AXIS MESSAGE TO RETURN!");
            //String resp = JavaUtils.getMessage("noData00");
            //res.setContentLength((int) resp.getBytes().length);
            //res.getWriter().print(resp);
        } else {
            if(isDebug) {
                log.debug("Returned Content-Type:" +
                          responseMsg.getContentType());
                // log.debug("Returned Content-Length:" +
                //          responseMsg.getContentLength());
            }

            res.setContentType(responseMsg.getContentType());

            /* My understand of Content-Length
             * HTTP 1.0
             *   -Required for requests, but optional for responses.
             * HTTP 1.1
             *  - Either Content-Length or HTTP Chunking is required.
             *   Most servlet engines will do chunking if content-length is not specified.
             * 
             *
             */

            //if(clientVersion == HTTPConstants.HEADER_PROTOCOL_V10) //do chunking if necessary.
           //     res.setContentLength(responseMsg.getContentLength());
            
            try {
                responseMsg.writeTo(res.getOutputStream());
            } catch (SOAPException e){
                log.error(JavaUtils.getMessage("exception00"), e);
            }
        }
        
        if (!res.isCommitted()) {
            res.flushBuffer(); // Force it right now.
        }
    }

    /**    
     * Place the Request message in the MessagContext object - notice
     * that we just leave it as a 'ServletRequest' object and let the
     * Message processing routine convert it - we don't do it since we
     * don't know how it's going to be used - perhaps it might not
     * even need to be parsed.
     */
    private MessageContext createMessageContext(AxisEngine engine,
                                                HttpServletRequest req,
                                                HttpServletResponse res)
    {
        MessageContext msgContext = new MessageContext(engine);
        
        if(isDebug) {
            log.debug("MessageContext:" + msgContext);
            log.debug("HEADER_CONTENT_TYPE:" +
                      req.getHeader( HTTPConstants.HEADER_CONTENT_TYPE));
            log.debug("HEADER_CONTENT_LOCATION:" +
                      req.getHeader( HTTPConstants.HEADER_CONTENT_LOCATION));
            log.debug("Constants.MC_HOME_DIR:" +
                      getServletConfig().getServletContext().getRealPath("/"));
            log.debug("Constants.MC_RELATIVE_PATH:"+req.getServletPath());
            log.debug("HTTPConstants.MC_HTTP_SERVLETLOCATION:"+ webInfPath );
            log.debug("HTTPConstants.MC_HTTP_SERVLETPATHINFO:" +
                      req.getPathInfo() );
            log.debug("HTTPConstants.HEADER_AUTHORIZATION:" +
                      req.getHeader(HTTPConstants.HEADER_AUTHORIZATION));
            log.debug("Constants.MC_REMOTE_ADDR:"+req.getRemoteAddr());
            log.debug("configPath:" + webInfPath);
        }

        /* Set the Transport */
        /*********************/
        msgContext.setTransportName(transportName);

        /* Save some HTTP specific info in the bag in case someone needs it */
        /********************************************************************/
        msgContext.setProperty(Constants.MC_JWS_CLASSDIR, jwsClassDir);
        msgContext.setProperty(Constants.MC_HOME_DIR, homeDir);
        msgContext.setProperty(Constants.MC_RELATIVE_PATH,
                               req.getServletPath());
        msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLET, this );
        msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST, req );
        msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLETRESPONSE, res );
        msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLETLOCATION,
                               webInfPath );
        msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLETPATHINFO,
                               req.getPathInfo() );
        msgContext.setProperty(HTTPConstants.HEADER_AUTHORIZATION,
                               req.getHeader(HTTPConstants.HEADER_AUTHORIZATION));
        msgContext.setProperty(Constants.MC_REMOTE_ADDR, req.getRemoteAddr());

        // Set up a javax.xml.rpc.server.ServletEndpointContext
        ServletEndpointContextImpl sec = 
                new ServletEndpointContextImpl(req.getSession(),
                                               msgContext,
                                               req.getUserPrincipal(),
                                               getServletConfig().getServletContext());
        
        msgContext.setProperty(Constants.MC_SERVLET_ENDPOINT_CONTEXT, sec);
        /* Save the real path */
        /**********************/
        String realpath =
            getServletConfig().getServletContext()
            .getRealPath(req.getServletPath());

        if (realpath != null)
            msgContext.setProperty(Constants.MC_REALPATH, realpath);
            
        msgContext.setProperty(Constants.MC_CONFIGPATH, webInfPath);

        return msgContext;
    }

    /**
     * if SOAPAction is null then we'll we be forced to scan the body for it.
     * if SOAPAction is "" then use the URL
     */
    private String getSoapAction(HttpServletRequest req)
        throws AxisFault
    {
        String soapAction =
            (String)req.getHeader(HTTPConstants.HEADER_SOAP_ACTION);

        if(isDebug) log.debug("HEADER_SOAP_ACTION:" + soapAction);

        /**
         * Technically, if we don't find this header, we should probably fault.
         * It's required in the SOAP HTTP binding.
         */
        if (soapAction == null) {
            AxisFault af = new AxisFault("Client.NoSOAPAction",
                                         JavaUtils.getMessage("noHeader00",
                                                              "SOAPAction"),
                                         null, null);

            log.error(JavaUtils.getMessage("genFault00"), af);

            throw af; 
        }

        if (soapAction.length()==0)
            soapAction = req.getContextPath(); // Is this right?
                
        return soapAction;
    }
    
    /**
     * Retrieve option, in order of precedence:
     * System property, servlet init param, context init param.
     * Use of system properties is discouraged in production environments,
     * as it overrides everything else.
     */
    private String getOption(ServletContext context,
                             String param,
                             String dephault)
    {
        String value = AxisProperties.getProperty(param);

        if (value == null)
            value = getInitParameter(param);

        if (value == null)
            value = context.getInitParameter(param);
            
        return (value != null) ? value : dephault;
    }
    
    /**
     * Provided to allow overload of default JWSClassDir
     * by derived class.
     */
    protected String getDefaultJWSClassDir() {
        return getWebInfPath() + File.separator +  "jwsClasses";
    }

    /**
     * Return the HTTP protocol level 1.1 or 1.0 
     * by derived class.
     */
    protected String getProtocolVersion(HttpServletRequest req){
        String ret= HTTPConstants.HEADER_PROTOCOL_V10;
        String prot= req.getProtocol();
        if(prot!= null){
            int sindex= prot.indexOf('/');
            if(-1 != sindex){
                String ver= prot.substring(sindex+1);
                if(HTTPConstants.HEADER_PROTOCOL_V11.equals(ver.trim())){
                    ret= HTTPConstants.HEADER_PROTOCOL_V11;
                }
            }
        }
        return ret;
    }
}
