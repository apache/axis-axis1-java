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
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.configuration.ServletEngineConfigurationFactory;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPFaultElement;
import org.apache.axis.security.servlet.ServletSecurityProvider;
import org.apache.axis.server.AxisServer;
import org.apache.axis.utils.Admin;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUtils;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author Doug Davis (dug@us.ibm.com)
 */
public class AxisServlet extends HttpServlet
{
    protected static Log log =
        LogFactory.getLog(AxisServlet.class.getName());

    // These have default values.
    private String transportName = "http";
    private AxisEngine engine = null;
    private ServletSecurityProvider securityProvider = null;

    /**
     * Should we enable the "?list" functionality on GETs?  (off by
     * default because deployment information is a potential security
     * hole)
     */
    private boolean enableList = false;

    private static final String AXIS_ENGINE = "AxisEngine" ;

    private boolean isDebug= false;

    // Cached path to our WEB-INF directory
    private String webInfPath = null;
    // Cached path to JWS output directory
    private String jwsClassDir = null;
    // Cached path to our "root" dir
    private String homeDir = null;

    public AxisServlet() {
    }

    public void init() {
        ServletContext context = getServletConfig().getServletContext();

        webInfPath = context.getRealPath("/WEB-INF");
        homeDir = context.getRealPath("/");
        
        isDebug= log.isDebugEnabled();
        if(isDebug) log.debug("In servlet init");
        String param = getInitParameter("transport.name");

        if (param == null)
            param = context.getInitParameter("transport.name");
        if (param != null)
            transportName = param;

        param = getInitParameter("use-servlet-security");
        if ((param != null) && (param.equalsIgnoreCase("true"))) {
            securityProvider = new ServletSecurityProvider();
        }

        param = System.getProperty("axis.enableListQuery");
        if (!(param == null) && (param.equalsIgnoreCase("true"))) {
            enableList = true;
        }

        // Allow system property to override our default placement of
        // JWS class files.
        param = System.getProperty("axis.jws.servletClassDir");
        if (param != null) {
            jwsClassDir = homeDir + param;
        } else {
            jwsClassDir = context.getRealPath("/");
        }
    }

    public AxisServer getEngine() throws AxisFault {
        ServletContext  context = getServletContext();

        if (context.getAttribute("AxisEngine") == null) {
            String webInfPath = context.getRealPath("/WEB-INF");

            EngineConfiguration config =
                (new ServletEngineConfigurationFactory(context)).
                getServerEngineConfig();

            Map environment = new HashMap();
            environment.put("servletContext", context);
            String attdir= getInitParameter("axis.attachments.Directory");
            if(attdir != null) environment.put("axis.attachments.Directory",  attdir);
            if(null != webInfPath){
                environment.put("servlet.realpath",  webInfPath + File.separator + "attachments");
            }
            environment.put(EngineConfiguration.PROPERTY_NAME, config);

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
            context.setAttribute("AxisEngine", 
                                 AxisServer.getServer(environment));
        }
        return (AxisServer)context.getAttribute("AxisEngine");
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
        if(isDebug) log.debug("In doGet");
        PrintWriter writer = res.getWriter();

        if (engine == null) {
            try {
                engine = getEngine();
            } catch (AxisFault fault) {
                res.setContentType("text/html");
                writer.println("<h2>" +
                        JavaUtils.getMessage("error00") + "</h2>");
                writer.println("<p>" +
                        JavaUtils.getMessage("somethingWrong00") + "</p>");
                writer.println("<pre>" + fault.toString() + " </pre>");
                return;
            }
        }

        ServletContext context = getServletConfig().getServletContext();
        MessageContext msgContext = new MessageContext(engine);

        msgContext.setProperty(Constants.MC_JWS_CLASSDIR,
                               jwsClassDir);
        msgContext.setProperty(Constants.MC_HOME_DIR, homeDir);

        String realpath = context.getRealPath(req.getServletPath());
        String configPath = webInfPath;
        if (realpath != null) {
            msgContext.setProperty(Constants.MC_RELATIVE_PATH,
                                   req.getServletPath());
            msgContext.setProperty(Constants.MC_REALPATH, realpath);
            msgContext.setProperty(Constants.MC_CONFIGPATH, configPath);

            /* Set the Transport */
            /*********************/
            msgContext.setTransportName(transportName);

            /* Save some HTTP specific info in the bag in case we need it */
            /**************************************************************/
            msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLET, this );
            msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST, req);
            msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLETRESPONSE, res);
            msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLETLOCATION,
                                   webInfPath);
            msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLETPATHINFO,
                                   req.getPathInfo() );
            msgContext.setProperty(HTTPConstants.HEADER_AUTHORIZATION,
                            req.getHeader(HTTPConstants.HEADER_AUTHORIZATION));
            msgContext.setProperty(Constants.MC_REMOTE_ADDR,
                                   req.getRemoteAddr());

            try {
                String url = HttpUtils.getRequestURL(req).toString();

                msgContext.setProperty(MessageContext.TRANS_URL, url);

                boolean wsdlRequested = false;
                boolean listRequested = false;

                String queryString = req.getQueryString();
                if (queryString != null) {
                    if (queryString.equalsIgnoreCase("wsdl")) {
                        wsdlRequested = true;
                    } else if (queryString.equalsIgnoreCase("list")) {
                        listRequested = true;
                    }
                }

                if (wsdlRequested) {
                    engine.generateWSDL(msgContext);
                    Document doc = (Document) msgContext.getProperty("WSDL");
                    if (doc != null) {
                        res.setContentType("text/xml");
                        XMLUtils.DocumentToWriter(doc, writer);
                    } else {
                        res.setContentType("text/html");
                        writer.println("<h2>" +
                                JavaUtils.getMessage("error00") + "</h2>");
                        writer.println("<p>" +
                                JavaUtils.getMessage("noWSDL00") + "</p>");
                    }
                } else if (listRequested) {
                    if (enableList) {
                        Document doc = Admin.listConfig(engine);
                        if (doc != null) {
                            res.setContentType("text/xml");
                            XMLUtils.DocumentToWriter(doc, writer);
                        } else {
                            res.setContentType("text/html");
                            writer.println("<h2>" +
                                    JavaUtils.getMessage("error00") + "</h2>");
                            writer.println("<p>" +
                                           JavaUtils.getMessage("noDeploy00") +
                                           "</p>");
                        }
                    } else {
                        res.setContentType("text/html");
                        writer.println("<h2>" +
                                JavaUtils.getMessage("error00") + "</h2>");
                        writer.println("<p><i>?list</i>" +
                                JavaUtils.getMessage("disabled00") + "</p>");
                    }
                } else if (req.getParameterNames().hasMoreElements()) {
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
                                JavaUtils.getMessage("noMethod01") + "</p>");
                        return;
                    }
                    String body = "<" + method + ">" + args +
                                  "</" + method + ">";
                    String msgtxt = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                                 "<SOAP-ENV:Body>" + body + "</SOAP-ENV:Body>" +
                                 "</SOAP-ENV:Envelope>";
                    ByteArrayInputStream istream = new ByteArrayInputStream(
                        msgtxt.getBytes());
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
                        writer.println(respMsg.getSOAPPart().getAsString());
                    } else {
                        writer.println("<p>" +
                                JavaUtils.getMessage("noResponse01") + "</p>");
                    }
                } else {
                    res.setContentType("text/html");
                    writer.println("<h1>" + req.getRequestURI() +
                            "</h1>");
                    writer.println(configPath);
                    writer.println(
                            "<p>" +
                            JavaUtils.getMessage("axisService00") + "</p>");
                    writer.println(
                           "<i>" + JavaUtils.getMessage("perhaps00") + "</i>");
                }
            } catch (AxisFault fault) {
                res.setContentType("text/html");
                writer.println("<h2>" +
                        JavaUtils.getMessage("error00") + "</h2>");
                writer.println("<p>" +
                        JavaUtils.getMessage("somethingWrong00") + "</p>");
                writer.println("<pre>" + fault.toString() + " </pre>");
            } catch (Exception e) {
                res.setContentType("text/html");
                writer.println("<h2>" +
                        JavaUtils.getMessage("error00") + "</h2>");
                writer.println("<p>" +
                        JavaUtils.getMessage("somethingWrong00") + "</p>");
                writer.println("<pre>Exception - " + e + "<br>");
                e.printStackTrace(res.getWriter());
                writer.println("</pre>");
            } finally {
                writer.close();
                return;
            }
        }

        res.setContentType("text/html");
        writer.println( "<html><h1>Axis HTTP Servlet</h1>" );
        writer.println( JavaUtils.getMessage("reachedServlet00"));

        writer.println("<p>" + JavaUtils.getMessage("transportName00",
                "<b>" + transportName + "</b>"));
        writer.println("</html>");
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
        if(isDebug) log.debug("In doPost");
        if (engine == null) {
            try {
                engine = getEngine();
            } catch (AxisFault fault) {
                log.debug(fault);
                Message msg = new Message(fault);
                res.setContentType( msg.getContentType() );
                res.setContentLength( msg.getContentLength() );
                msg.writeContentToStream(res.getOutputStream());
                return;
            }
        }

        ServletConfig  config  = getServletConfig();
        ServletContext context = config.getServletContext();
        res.setBufferSize(1024 * 8); //provide performance boost.       

        if (engine == null)
            engine = (AxisEngine)context.getAttribute(AXIS_ENGINE);

        if (engine == null) {
            // !!! should return a SOAP fault...
            ServletException se =
                    new ServletException(JavaUtils.getMessage("noEngine00"));
            log.debug(se);
            throw se; 
        }

        /* Place the Request message in the MessagContext object - notice */
        /* that we just leave it as a 'ServletRequest' object and let the  */
        /* Message processing routine convert it - we don't do it since we */
        /* don't know how it's going to be used - perhaps it might not     */
        /* even need to be parsed.                                         */
        /*******************************************************************/
        MessageContext    msgContext = new MessageContext(engine);
        if(isDebug) log.debug("MessageContext:" + msgContext );

        if(isDebug) log.debug("HEADER_CONTENT_TYPE:" +  
          req.getHeader( HTTPConstants.HEADER_CONTENT_TYPE));
        if(isDebug) log.debug("HEADER_CONTENT_LOCATION:" +
          req.getHeader( HTTPConstants.HEADER_CONTENT_LOCATION));

        Message msg = new Message( req.getInputStream(),
                       false,
                       req.getHeader( HTTPConstants.HEADER_CONTENT_TYPE),
                       req.getHeader( HTTPConstants.HEADER_CONTENT_LOCATION));
        if(isDebug) log.debug("Message:" + msg);

        /* Set the request(incoming) message field in the context */
        /**********************************************************/
        msgContext.setRequestMessage( msg );

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
                          req.getHeader(HTTPConstants.HEADER_AUTHORIZATION) );
        msgContext.setProperty(Constants.MC_REMOTE_ADDR, req.getRemoteAddr());


        if (securityProvider != null)
            msgContext.setProperty("securityProvider", securityProvider);

        if(isDebug){
            log.debug("Constants.MC_HOME_DIR:" + context.getRealPath("/"));
            log.debug("Constants.MC_RELATIVE_PATH:"+req.getServletPath());
            log.debug("HTTPConstants.MC_HTTP_SERVLETLOCATION:"+
                           webInfPath );
            log.debug("HTTPConstants.MC_HTTP_SERVLETPATHINFO:" + req.getPathInfo() );
            log.debug("HTTPConstants.HEADER_AUTHORIZATION:" + req.getHeader(HTTPConstants.HEADER_AUTHORIZATION));
            log.debug("Constants.MC_REMOTE_ADDR:"+req.getRemoteAddr());
            log.debug("securityProvider:"+securityProvider );
        }

        /* Save the SOAPAction header in the MessageContext bag - this will */
        /* be used to tell the Axis Engine which service is being invoked.  */
        /* This will save us the trouble of having to parse the Request     */
        /* message - although we will need to double-check later on that    */
        /* the SOAPAction header does in fact match the URI in the body.    */
        /* (is this last stmt true??? (I don't think so - Glen))            */
        /* if SOAPAction is "" then use the URL                             */
        /* if SOAPAction is null then we'll we be forced to scan the body   */
        /*   for it.                                                        */
        /********************************************************************/
        String  tmp ;
        tmp = (String) req.getHeader( HTTPConstants.HEADER_SOAP_ACTION );
        if(isDebug) log.debug("HEADER_SOAP_ACTION:" + tmp);

        try {
            /** Technically, if we don't find this header, we should probably fault.
            * It's required in the SOAP HTTP binding.
            */
            if ( tmp == null ) {
                AxisFault af=  new AxisFault( "Client.NoSOAPAction",
                    JavaUtils.getMessage("noHeader00", "SOAPAction"),
                    null, null );

                 log.debug(af);
                throw af; 
            }

            if ( "".equals(tmp) )
                tmp = req.getContextPath(); // Is this right?

            if ( tmp != null ) {
                msgContext.setUseSOAPAction( true );
                msgContext.setSOAPActionURI( tmp );
            }

            // Create a Session wrapper for the HTTP session.
            // These can/should be pooled at some point.  (Sam is Watching! :-)
            msgContext.setSession(new AxisHttpSession(req));

            /* Save the real path */
            /**********************/
            String realpath = context.getRealPath(req.getServletPath());
            if (realpath != null)
                msgContext.setProperty(Constants.MC_REALPATH, realpath);

            String configPath = webInfPath;
            if(isDebug) log.debug("configPath:" + configPath);

            msgContext.setProperty(Constants.MC_CONFIGPATH, configPath);

            /* Invoke the Axis engine... */
            /*****************************/
            if(isDebug) log.debug("Invoking Axis Engine.");
            engine.invoke( msgContext );
            if(isDebug) log.debug("Return from Axis Engine.");
        }
        catch( Exception e ) {
            log.debug(e);
            if ( e instanceof AxisFault ) {
                AxisFault  af = (AxisFault) e ;
                if ( "Server.Unauthorized".equals( af.getFaultCode() ) )
                    res.setStatus( HttpServletResponse.SC_UNAUTHORIZED );
                else
                    res.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
                // It's been suggested that a lack of SOAPAction should produce some
                // other error code (in the 400s)...
            }
            else
                res.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
            if ( !(e instanceof AxisFault) )
                e = AxisFault.makeFault(e);
            if (msg == null) {
                msg = new Message((AxisFault)e);
                msgContext.setResponseMessage(msg);
            } else {
                try {
                    SOAPEnvelope env = msg.getSOAPEnvelope();
                    env.clearBody();
                    env.addBodyElement(new SOAPFaultElement((AxisFault)e));
                    msgContext.setResponseMessage(msg);
                } catch (AxisFault af) {
                    // Should never reach here!
                }
            }
        }

        /* Send it back along the wire...  */
        /***********************************/


        if(null== (msg = msgContext.getResponseMessage())) {
          if(isDebug) log.debug("NO AXIS MESSAGE TO RETURN!");
          String resp= JavaUtils.getMessage("noData00");
          res.setContentLength(resp.getBytes().length );
          res.getWriter().print(resp);
        } else {

          if(isDebug) log.debug("Returned Content-Type:" + msg.getContentType());
          int respContentlength=0;
          res.setContentType( msg.getContentType() );
          res.setContentLength(respContentlength=  msg.getContentLength() );
          if(isDebug) log.debug("Returned Content-Length:" + respContentlength);
          msg.writeContentToStream(res.getOutputStream());
        }
        if(!res.isCommitted()) {
            res.flushBuffer(); //Force it right now.
        }
        if(isDebug) log.debug("Response sent.");
    }
}
