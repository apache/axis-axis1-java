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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.net.HttpURLConnection;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUtils;
import javax.xml.soap.SOAPException;
import javax.xml.namespace.QName;

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.ConfigurationException;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.security.servlet.ServletSecurityProvider;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.Admin;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;

import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

import org.w3c.dom.Document;

/**
 *
 * @author Doug Davis (dug@us.ibm.com)
 * @author Steve Loughran
 * xdoclet tags are not active yet; keep web.xml in sync
 * @web.servlet name="AxisServlet"  display-name="Apache-Axis Servlet"
 * @web.servlet-mapping url-pattern="/servlet/AxisServlet"
 * @web.servlet-mapping url-pattern="*.jws"
 * @web.servlet-mapping url-pattern="/services/*"
 */
public class AxisServlet extends AxisServletBase {
    protected static Log log =
        LogFactory.getLog(AxisServlet.class.getName());

    /**
     * this log is for timing
     */
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

    // These have default values.
    private String transportName;

    private ServletSecurityProvider securityProvider = null;

    /**
     * cache of logging debug option; only evaluated at init time.
     * So no dynamic switching of logging options with this servlet.
     */
     private static boolean isDebug = false;

    /**
     * Should we enable the "?list" functionality on GETs?  (off by
     * default because deployment information is a potential security
     * hole)
     */
    private boolean enableList = false;


    /**
     * Cached path to JWS output directory
     */
    private String jwsClassDir = null;
    protected String getJWSClassDir() { return jwsClassDir; }




    /**
     * create a new servlet instance
     */
    public AxisServlet() {
    }

    /**
     * Initialization method.
     */
    public void init() {
        super.init();
        ServletContext context = getServletConfig().getServletContext();


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

        /**
         * There are DEFINITATE problems here if
         * getHomeDir and/or getDefaultJWSClassDir return null
         * (as they could with WebLogic).
         * This needs to be reexamined in the future, but this
         * should fix any NPE's in the mean time.
         */
        if (jwsClassDir != null) {
            if (getHomeDir() != null) {
                jwsClassDir = getHomeDir() + jwsClassDir;
            }
        } else {
            jwsClassDir = getDefaultJWSClassDir();
        }
    }



    /**
     * Process GET requests. Because Axis does not support the GET-style
     * pseudo execution of SOAP methods, this handler deals with queries
     * of various kinds, not real SOAP actions.
     *
     * @todo for secure installations, dont stack trace on faults
     * @param request request in
     * @param response request out
     * @throws ServletException
     * @throws IOException
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        if (isDebug)
            log.debug("Enter: doGet()");

        PrintWriter writer = response.getWriter();

        try
        {
            AxisEngine engine = getEngine();
            ServletContext servletContext =
                getServletConfig().getServletContext();

            String pathInfo = request.getPathInfo();
            String realpath = servletContext.getRealPath(request.getServletPath());
            if (realpath == null) {
                realpath = request.getServletPath();
            }

            boolean wsdlRequested = false;
            boolean listRequested = false;
            boolean hasParameters = request.getParameterNames().hasMoreElements();

            //JWS pages are special; they are the servlet path and there
            //is no pathinfo...we map the pathinfo to the servlet path to keep
            //it happy
            boolean isJWSPage = request.getRequestURI().endsWith(".jws");
            if(isJWSPage) {
                pathInfo= request.getServletPath();
            }

            // check first if we are doing WSDL or a list operation
            String queryString = request.getQueryString();
            if (queryString != null) {
                if (queryString.equalsIgnoreCase("wsdl")) {
                    wsdlRequested = true;
                } else if (queryString.equalsIgnoreCase("list")) {
                    listRequested = true;
                }
            }

            boolean hasNoPath = (pathInfo == null || pathInfo.equals(""));
            if (!wsdlRequested && !listRequested && hasNoPath) {
                // If the user requested the servlet (i.e. /axis/servlet/AxisServlet)
                // with no service name, present the user with a list of deployed
                // services to be helpful
                // Don't do this if we are doing WSDL or list.
                reportAvailableServices(response, writer, request);
            } else if (realpath != null) {
                // We have a pathname, so now we perform WSDL or list operations

                // get message context w/ various properties set
                MessageContext msgContext = createMessageContext(engine, request, response);

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
                    String url = HttpUtils.getRequestURL(request).toString();

                    msgContext.setProperty(MessageContext.TRANS_URL, url);


                    if (wsdlRequested) {
                        // Do WSDL generation
                        processWsdlRequest(msgContext, response, writer);
                    } else if (listRequested) {
                        // Do list, if it is enabled
                        processListRequest(response, writer);
                    } else if (hasParameters) {
                        // If we have ?method=x&param=y in the URL, make a stab
                        // at invoking the method with the parameters specified
                        // in the URL

                        processMethodRequest(msgContext, request, response, writer);

                    } else {

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
                            //no service: report it
                            if(isJWSPage) {
                                reportCantGetJWSService(request, response, writer);
                            } else {
                                reportCantGetAxisService(request, response, writer);
                            }

                        } else {
                            //print a snippet of service info.
                            reportServiceInfo(response, writer, s, serviceName);
                        }
                    }
                } catch (AxisFault fault) {
                    log.error(Messages.getMessage("exception00"), fault);
                    response.setContentType("text/html");
                    response.setStatus(500);
                    writer.println("<h2>" +
                                   Messages.getMessage("error00") + "</h2>");
                    writer.println("<p>" +
                                   Messages.getMessage("somethingWrong00") +
                                   "</p>");
                    writer.println("<pre>Fault - " + fault.toString() + " </pre>");
                    writer.println("<pre>" + fault.dumpToString() + " </pre>");
                } catch (Exception e) {
                    log.error(Messages.getMessage("exception00"), e);
                    response.setContentType("text/html");
                    response.setStatus(500);
                    writer.println("<h2>" +
                                   Messages.getMessage("error00") +
                                   "</h2>");
                    writer.println("<p>" +
                                   Messages.getMessage("somethingWrong00") +
                                   "</p>");
                    writer.println("<pre>Exception - " + e + "<br>");
                    //dev systems only give fault dumps
                    if (isDevelopment()) {
                        writer.println(JavaUtils.stackToString(e));
                    }
                    writer.println("</pre>");
                }
            }
            else
            {
                // We didn't have a real path in the request, so just
                // print a message informing the user that they reached
                // the servlet.

                response.setContentType("text/html");
                writer.println( "<html><h1>Axis HTTP Servlet</h1>" );
                writer.println( Messages.getMessage("reachedServlet00"));

                writer.println("<p>" +
                               Messages.getMessage("transportName00",
                                         "<b>" + transportName + "</b>"));
                writer.println("</html>");
            }
        } finally {
            writer.close();

            if (isDebug)
                log.debug("Exit: doGet()");
        }
    }

    /**
     * scan through the request for parameters, invoking the endpoint
     * if we get a method param. If there was no method param then the
     * response is set to a 400 Bad Request and some error text
     * @param msgContext current message
     * @param request incoming requests
     * @param response response to generate
     * @param writer output stream
     * @throws AxisFault if anything goes wrong during method execution
     */
    protected void processMethodRequest(MessageContext msgContext,
                                        HttpServletRequest request,
                                        HttpServletResponse response,
                                        PrintWriter writer) throws AxisFault {
        Enumeration enum = request.getParameterNames();
        String method = null;
        String args = "";
        while (enum.hasMoreElements()) {
            String param = (String) enum.nextElement();
            if (param.equalsIgnoreCase("method")) {
                method = request.getParameter(param);
            } else {
                args += "<" + param + ">" +
                    request.getParameter(param) +
                    "</" + param + ">";
            }
        }

        if (method == null) {
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writer.println("<h2>" +
                           Messages.getMessage("error00") +
                           ":  " +
                           Messages.getMessage("invokeGet00") +
                           "</h2>");
            writer.println("<p>" +
                           Messages.getMessage("noMethod01") +
                           "</p>");
        } else {
            invokeEndpointFromGet(msgContext, response, writer, method, args);

        }
    }

    /**
     * handle a ?wsdl request
     * @param msgContext message context so far
     * @param response response to write to
     * @param writer output stream
     * @throws AxisFault when anything other than a Server.NoService fault is reported
     * during WSDL generation
     */
    protected void processWsdlRequest(MessageContext msgContext,
                                      HttpServletResponse response,
                                      PrintWriter writer) throws AxisFault {
        AxisEngine engine = getEngine();
        try {
            engine.generateWSDL(msgContext);
            Document doc = (Document) msgContext.getProperty("WSDL");
            if (doc != null) {
                response.setContentType("text/xml");
                XMLUtils.DocumentToWriter(doc, writer);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("processWsdlRequest: failed to create WSDL");
                }
                reportNoWSDL(response, writer, "noWSDL02", null);
            }
        } catch (AxisFault axisFault) {
            //the no-service fault is mapped to a no-wsdl error
            if(axisFault.getFaultCode() .equals(Constants.QNAME_NO_SERVICE_FAULT_CODE)) {
                //which we log before reporting.
                if(log.isDebugEnabled()) {
                    log.debug(Messages.getMessage("exception00"), axisFault);
                }
                reportNoWSDL(response, writer, "noWSDL01", axisFault);
            } else {
                throw axisFault;
            }
        }
    }

    /**
     * invoke an endpoint from a get request by building an XML request and
     * handing it down
     * @param msgContext current message
     * @param response to return data
     * @param writer output stream
     * @param method method to invoke (may be null)
     * @param args argument list in XML form
     * @throws AxisFault
     */
    protected void invokeEndpointFromGet(MessageContext msgContext,
                                       HttpServletResponse response,
                                       PrintWriter writer,
                                       String method,
                                       String args) throws AxisFault {
        AxisEngine engine = getEngine();
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
        engine.invoke(msgContext);
        Message respMsg = msgContext.getResponseMessage();
        if (respMsg != null) {
            response.setContentType("text/xml");
            writer.println(respMsg.getSOAPPartAsString());
        } else {
            //TODO: error code
            writer.println("<p>" +
                   Messages.getMessage("noResponse01") +
                           "</p>");
        }
    }

    /**
     * print a snippet of service info.
     * @param service service
     * @param writer output channel
     * @param serviceName where to put stuff
     */

    protected  void reportServiceInfo(HttpServletResponse response, PrintWriter writer, SOAPService service, String serviceName) {
        response.setContentType("text/html");

        writer.println("<h1>"
                + service.getName()
                +"</h1>");
        writer.println(
                "<p>" +
                Messages.getMessage("axisService00") +
                "</p>");
        writer.println(
                "<i>" +
                Messages.getMessage("perhaps00") +
                "</i>");
    }

    /**
     * respond to the ?list command.
     * if enableList is set, we list the engine config. If it isnt, then an
     * error is written out
     * @param response
     * @param writer
     * @throws AxisFault
     */
    protected void processListRequest(HttpServletResponse response, PrintWriter writer) throws AxisFault {
        AxisEngine engine = getEngine();
        if (enableList) {
            Document doc = Admin.listConfig(engine);
            if (doc != null) {
                response.setContentType("text/xml");
                XMLUtils.DocumentToWriter(doc, writer);
            } else {
                //error code is 404
                response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
                response.setContentType("text/html");
                writer.println("<h2>" +
                               Messages.getMessage("error00") +
                               "</h2>");
                writer.println("<p>" +
                               Messages.getMessage("noDeploy00") +
                               "</p>");
            }
        } else {
            // list not enable, return error
            //error code is, what, 401
            response.setStatus(HttpURLConnection.HTTP_FORBIDDEN);
            response.setContentType("text/html");
            writer.println("<h2>" +
                           Messages.getMessage("error00") +
                           "</h2>");
            writer.println("<p><i>?list</i>" +
                           Messages.getMessage("disabled00") +
                           "</p>");
        }
    }

    /**
     * report that we have no WSDL
     * @param res
     * @param writer
     * @param moreDetailCode optional name of a message to provide more detail
     * @param axisFault optional fault string, for extra info at debug time only
     */
    protected void reportNoWSDL(HttpServletResponse res, PrintWriter writer,
                                String moreDetailCode, AxisFault axisFault) {
        res.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
        res.setContentType("text/html");
        writer.println("<h2>" +
                       Messages.getMessage("error00") +
                       "</h2>");
        writer.println("<p>" +
                       Messages.getMessage("noWSDL00") +
                       "</p>");
        if(moreDetailCode!=null) {
            writer.println("<p>"
                    +Messages.getMessage(moreDetailCode)
                    +"</p>");
        }

        if(axisFault!=null && isDevelopment()) {
            //dev systems only give fault dumps
            writer.println("<pre>Exception - " + axisFault.getLocalizedMessage()+ "<br>");
            writer.println(axisFault.dumpToString());
            writer.println("</pre>");
        }
    }

    /**
     * This method lists the available services; it is called when there is
     * nothing to execute on a GET
     * @param response
     * @param writer
     * @param request
     * @throws ConfigurationException
     * @throws AxisFault
     */
    protected void reportAvailableServices(HttpServletResponse response,
                                       PrintWriter writer,
                                       HttpServletRequest request)
            throws ConfigurationException, AxisFault {
        AxisEngine engine = getEngine();
        response.setContentType("text/html");
        writer.println("<h2>And now... Some Services</h2>");
        Iterator i = engine.getConfig().getDeployedServices();
        String baseURL = getWebappBase(request)+"/services/";
        writer.println("<ul>");
        while (i.hasNext()) {
            ServiceDesc sd = (ServiceDesc)i.next();
            StringBuffer sb = new StringBuffer();
            sb.append("<li>");
            String name = sd.getName();
            sb.append(name);
            sb.append(" <a href=\"");
            sb.append(baseURL);
            sb.append(name);
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
    }

    /**
     * generate the error response to indicate that there is apparently no endpoint there
     * @param request the request that didnt have an edpoint
     * @param response response we are generating
     * @param writer open writer for the request
     */
    protected void reportCantGetAxisService(HttpServletRequest request, HttpServletResponse response, PrintWriter writer) {
        // no such service....
        response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
        response.setContentType("text/html");
        writer.println("<h2>" +
                Messages.getMessage("error00") + "</h2>");
        writer.println("<p>" +
                Messages.getMessage("noService06") +
                "</p>");
    }

    /**
     * indicate that there may be a JWS page, and that the user should look
     * at the WSDL to se
     * @param request the request that didnt have an edpoint
     * @param response response we are generating
     * @param writer open writer for the request
     */
    protected void reportCantGetJWSService(HttpServletRequest request, HttpServletResponse response, PrintWriter writer) {
        // no such service....
        response.setStatus(HttpURLConnection.HTTP_OK);
        response.setContentType("text/html");
        String urltext= Messages.getMessage("noService08");
        String url=request.getRequestURI();
        writer.println(Messages.getMessage("noService07") + "<p>");
        writer.println("<a href='"+url+"?wsdl'>"+urltext+"</a>");
    }


    /**
     * Process a POST to the servlet by handing it off to the Axis Engine.
     * Here is where SOAP messages are received
     * @param req posted request
     * @param res respose
     * @throws ServletException trouble
     * @throws IOException different trouble
     */
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
        String  contentType = null;

        try {
            AxisEngine engine = getEngine();

            if (engine == null) {
                // !!! should return a SOAP fault...
                ServletException se =
                    new ServletException(Messages.getMessage("noEngine00"));
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
            String url = HttpUtils.getRequestURL(req).toString();
            msgContext.setProperty(MessageContext.TRANS_URL, url);

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
                contentType = responseMsg.getContentType(msgContext.getSOAPConstants()); 
            } catch (AxisFault e) {
                log.error(Messages.getMessage("exception00"), e);
                // It's been suggested that a lack of SOAPAction
                // should produce some other error code (in the 400s)...
                int status = getHttpServletResponseStatus(e);
                if (status == HttpServletResponse.SC_UNAUTHORIZED)
                  res.setHeader("WWW-Authenticate","Basic realm=\"AXIS\"");
                  // TODO: less generic realm choice?
                res.setStatus(status);
                responseMsg = msgContext.getResponseMessage();
                if (responseMsg == null)
                    responseMsg = new Message(e);
                contentType = responseMsg.getContentType(msgContext.getSOAPConstants()); 
            } catch (Exception e) {
                log.error(Messages.getMessage("exception00"), e);
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                responseMsg = msgContext.getResponseMessage();
                if (responseMsg == null)
                    responseMsg = new Message(AxisFault.makeFault(e));
                contentType = responseMsg.getContentType(msgContext.getSOAPConstants()); 
            }
        } catch (AxisFault fault) {
            log.error(Messages.getMessage("axisFault00"), fault);
            responseMsg = msgContext.getResponseMessage();
            if (responseMsg == null)
                responseMsg = new Message(fault);
            contentType = responseMsg.getContentType(msgContext.getSOAPConstants()); 
        }
        if( tlog.isDebugEnabled() ) {
            t3=System.currentTimeMillis();
        }

        /* Send response back along the wire...  */
        /***********************************/
        if (responseMsg != null)
            sendResponse(getProtocolVersion(req), contentType,
                         res, responseMsg);

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
        // TODO: Should really be doing this with explicit AxisFault
        // subclasses... --Glen
                return af.getFaultCode().getLocalPart().startsWith("Server.Unauth")
                         ? HttpServletResponse.SC_UNAUTHORIZED
                         : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
               // This will raise a 401 for both
               // "Unauthenticated" & "Unauthorized"...
    }

    /**
     * write a message to the response, set appropriate headers for content
     * type..etc.
     * @param clientVersion client protocol, one of the HTTPConstants strings
     * @param res   response
     * @param responseMsg message to write
     * @throws AxisFault
     * @throws IOException if the response stream can not be written to
     */
    private void sendResponse(final String clientVersion, 
            String contentType,
            HttpServletResponse res, Message responseMsg)
        throws AxisFault, IOException
    {
        if (responseMsg == null) {
            res.setStatus(HttpServletResponse.SC_NO_CONTENT);
            if(isDebug) log.debug("NO AXIS MESSAGE TO RETURN!");
            //String resp = Messages.getMessage("noData00");
            //res.setContentLength((int) resp.getBytes().length);
            //res.getWriter().print(resp);
        } else {
            if(isDebug) {
                log.debug("Returned Content-Type:" +
                    contentType);
                // log.debug("Returned Content-Length:" +
                //          responseMsg.getContentLength());
            }

            try {
                res.setContentType(contentType);

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

                responseMsg.writeTo(res.getOutputStream());
            } catch (SOAPException e){
                log.error(Messages.getMessage("exception00"), e);
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
     * @return a message context
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
            log.debug("Constants.MC_HOME_DIR:" + String.valueOf(getHomeDir()));
            log.debug("Constants.MC_RELATIVE_PATH:"+req.getServletPath());
            
            log.debug("HTTPConstants.MC_HTTP_SERVLETLOCATION:"+ String.valueOf(getWebInfPath()));
            log.debug("HTTPConstants.MC_HTTP_SERVLETPATHINFO:" +
                      req.getPathInfo() );
            log.debug("HTTPConstants.HEADER_AUTHORIZATION:" +
                      req.getHeader(HTTPConstants.HEADER_AUTHORIZATION));
            log.debug("Constants.MC_REMOTE_ADDR:"+req.getRemoteAddr());
            log.debug("configPath:" + String.valueOf(getWebInfPath()));
        }

        /* Set the Transport */
        /*********************/
        msgContext.setTransportName(transportName);

        /* Save some HTTP specific info in the bag in case someone needs it */
        /********************************************************************/
        msgContext.setProperty(Constants.MC_JWS_CLASSDIR, jwsClassDir);
        msgContext.setProperty(Constants.MC_HOME_DIR, getHomeDir());
        msgContext.setProperty(Constants.MC_RELATIVE_PATH,
                               req.getServletPath());
        msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLET, this );
        msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST, req );
        msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLETRESPONSE, res );
        msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLETLOCATION,
                               getWebInfPath() );
        msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLETPATHINFO,
                               req.getPathInfo() );
        msgContext.setProperty(HTTPConstants.HEADER_AUTHORIZATION,
                               req.getHeader(HTTPConstants.HEADER_AUTHORIZATION));
        msgContext.setProperty(Constants.MC_REMOTE_ADDR, req.getRemoteAddr());

        // Set up a javax.xml.rpc.server.ServletEndpointContext
        ServletEndpointContextImpl sec = new ServletEndpointContextImpl();

        msgContext.setProperty(Constants.MC_SERVLET_ENDPOINT_CONTEXT, sec);
        /* Save the real path */
        /**********************/
        String realpath =
            getServletConfig().getServletContext()
            .getRealPath(req.getServletPath());

        if (realpath != null) {
            msgContext.setProperty(Constants.MC_REALPATH, realpath);
        }

        msgContext.setProperty(Constants.MC_CONFIGPATH, getWebInfPath());

        return msgContext;
    }

    /**
     * Extract the SOAPAction header.
     * if SOAPAction is null then we'll we be forced to scan the body for it.
     * if SOAPAction is "" then use the URL
     * @param req incoming request
     * @return the action
     * @throws AxisFault
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
                                         Messages.getMessage("noHeader00",
                                                              "SOAPAction"),
                                         null, null);

            log.error(Messages.getMessage("genFault00"), af);

            throw af;
        }

        if (soapAction.length()==0)
            soapAction = req.getContextPath(); // Is this right?

        return soapAction;
    }

    /**
     * Provided to allow overload of default JWSClassDir
     * by derived class.
     * @return directory for JWS files
     */
    protected String getDefaultJWSClassDir() {
        return (getWebInfPath() == null)
               ? null  // ??? what is a good FINAL default for WebLogic?
               : getWebInfPath() + File.separator +  "jwsClasses";
    }

    /**
     * Return the HTTP protocol level 1.1 or 1.0
     * by derived class.
     * @return one of the HTTPConstants values
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
