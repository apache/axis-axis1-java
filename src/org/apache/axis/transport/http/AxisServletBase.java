/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Ant", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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


package org.apache.axis.transport.http;

import org.apache.axis.*;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.configuration.EngineConfigurationFactoryFinder;
import org.apache.axis.server.AxisServer;

import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.io.IOException;

/**
 * Base class for servlets used in axis, has common methods
 * to get and save the engine to a common location, currently the
 * webapp's context, though some alternate persistence mechanism is always
 * possible. Also has a load counter shared by all servlets; tracks the
 * # of active http requests open to any of the subclasses.
 * @author Steve Loughran
 */

public class AxisServletBase extends HttpServlet {

    /**
     * per-instance cache of the axis server
     */
    protected AxisServer axisServer = null;

    private static Log log =
        LogFactory.getLog(AxisServlet.class.getName());
        
    private static boolean isDebug = false;

    /**
     *  count number of service requests in progress
     */
    private static int loadCounter = 0;

    /**
     *  and a lock
     */
    private static Object loadCounterLock = new Object();

    /**
     * name of the axis engine to use in the servlet context
     */
    protected static final String ATTR_AXIS_ENGINE =
        "AxisEngine" ;

    /**
     *  Cached path to our WEB-INF directory
     */
    private String webInfPath = null;

    /**
     * Cached path to our "root" dir
     */
    private String homeDir = null;

    /**
     * flag set to true for a 'production' server
     */
    private boolean isProduction;

    /**
     * property name for a production server
     */
    private static final String INIT_PROPERTY_PRODUCTION_SYSTEM=
               "axis.production-system";


    /**
     * our initialize routine; subclasses should call this if they override it
     */
    public void init() {
        ServletContext context = getServletConfig().getServletContext();

        webInfPath = context.getRealPath("/WEB-INF");
        homeDir = context.getRealPath("/");

        isDebug = log.isDebugEnabled();
        if(log.isDebugEnabled()) log.debug("In AxisServletBase init");
        isProduction= JavaUtils.isTrueExplicitly(getOption(context,
                        INIT_PROPERTY_PRODUCTION_SYSTEM, null));

    }

    /**
     * Destroy method is called when the servlet is going away.  Pass this
     * down to the AxisEngine to let it clean up...  But don't create the
     * engine if it hasn't already been created.
     * @todo Fixme for multiple servlets.
     * This has always been slightly broken
     * (the context's copy stayed around), but now we have extracted it into
     * a superclass it is blatantly broken.
     */
    public void destroy() {
        super.destroy();

        //if we have had anything to do with creating an axis server
        if (axisServer != null) {
            //then we lock it
            synchronized(axisServer) {
                if (axisServer != null) {
                    //clean it up
                    axisServer.cleanup();
                    //and erase our history of it
                    axisServer =null;
                    storeEngine(getServletContext(),null);
                }
            }
        }
    }

    /**
     * get the engine for this servlet from cache or context
     * @return
     * @throws AxisFault
     */
    public AxisServer getEngine() throws AxisFault {
        if (axisServer == null)
            axisServer = getEngine(this);
        return axisServer;
    }


    /**
     * This is a uniform method of initializing AxisServer in a servlet
     * context.
     * @todo add catch for not being able to cast the context attr to an
     * engine and reinit the engine if so.
     */
    public static AxisServer getEngine(HttpServlet servlet) throws AxisFault
    {
        AxisServer engine = null;
        if (isDebug)
            log.debug("Enter: getEngine()");

        ServletContext context = servlet.getServletContext();
        synchronized (context) {
            engine = retrieveEngine(context);
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
                storeEngine(context, engine);
            }
        }

        if (isDebug)
            log.debug("Exit: getEngine()");

        return engine;
    }

    /**
     * put the engine back in to the context.
     * @param context
     * @param engine
     */
    private static void storeEngine(ServletContext context, AxisServer engine) {
        context.setAttribute(ATTR_AXIS_ENGINE, engine);
    }

    /**
     * Get an engine from the servlet context; robust againt serialization
     * issues of hot-updated webapps. Remember than if a webapp is marked
     * as distributed, there is more than 1 servlet context, hence more than
     * one AxisEngine instance
     * @param context
     * @return the engine or null if either the engine couldnt be found or
     *         the attribute wasnt of the right type
     */
    private static AxisServer retrieveEngine(ServletContext context) {
        Object contextObject = context.getAttribute(ATTR_AXIS_ENGINE);
        if (contextObject instanceof AxisServer) {
            return (AxisServer) contextObject;
        }
        else {
            return null;
        }
     }


    /**
     * extract information from the servlet configuration files
     * @param servlet
     * @return
     */
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
            EngineConfigurationFactoryFinder.newFactory(context)
                    .getServerEngineConfig();

        if (config != null) {
            environment.put(EngineConfiguration.PROPERTY_NAME, config);
        }

        return environment;
    }


    /**
     *  get a count of the # of services running. This is only
     *  ever an approximate number in a busy system
     *
     * @return    The TotalServiceCount value
     */

    public static int getLoadCounter() {
            return loadCounter;
    }

    /**
     * thread safe lock counter increment
     */
    protected static void incLockCounter() {
        synchronized(loadCounterLock) {
            loadCounter++;
        }
    }

    /**
     * thread safe lock counter decrement
     */
    protected static void decLockCounter() {
        synchronized(loadCounterLock) {
            loadCounter--;
        }
    }

    /**
     * subclass of service method that tracks entry count; calls the
     * parent's implementation to have the http method cracked and delegated
     * to the doGet, doPost method.
     * @param req request
     * @param resp response
     * @throws ServletException something went wrong
     * @throws IOException something different went wrong
     */
    protected void service(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        incLockCounter();
        try {
            super.service(req, resp);
        }
        finally {
            decLockCounter();
        }
    }
    
    /**
     * extract the base of our webapp from an inbound request
     *
     * @param request request containing http://foobar/axis/services/
     * @return http://foobar/axis/services/
     */
    protected String getWebappBase(HttpServletRequest request) {
        StringBuffer baseURL=new StringBuffer(128);
        baseURL.append(request.getScheme());
        baseURL.append("//");
        baseURL.append(request.getServerName());
        if(request.getServerPort()!=80) {
            baseURL.append(":");
            baseURL.append(request.getServerPort());
        }
        baseURL.append("/");
        baseURL.append(request.getContextPath());
        return baseURL.toString();
    }

    /**
     * what is the servlet context
     * @return get the context from the servlet config
     */
    public ServletContext getServletContext() {
        return getServletConfig().getServletContext();
    }

    /**
     * accessor to webinf
     * @return path to WEB-INF/ in the local filesystem
     */
    protected String getWebInfPath() {
        return webInfPath;
    }

    /**
     * what is the root dir of the applet?
     * @return path of root dir
     */
    protected String getHomeDir() {
        return homeDir;
    }

    /**
     * Retrieve option, in order of precedence:
     * (Managed) System property (see discovery.ManagedProperty),
     * servlet init param, context init param.
     * Use of system properties is discouraged in production environments,
     * as it overrides everything else.
     */
    protected String getOption(ServletContext context,
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
     * probe for the system being 'production'
     * @return true for a secure/robust system.
     */
    public boolean isProduction() {
        return isProduction;
    }

}
