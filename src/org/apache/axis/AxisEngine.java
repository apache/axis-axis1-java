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
 *    Apache Software Foundation (http://www.apache.org/)."
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

package org.apache.axis;

import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.handlers.HandlerChainImpl;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.session.Session;
import org.apache.axis.session.SimpleSession;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.cache.ClassCache;
import org.apache.axis.client.Service;
import org.apache.axis.client.Call;

import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

import javax.xml.namespace.QName;
import javax.xml.rpc.server.ServiceLifecycle;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Enumeration;


/**
 * An <code>AxisEngine</code> is the base class for AxisClient and
 * AxisServer.  Handles common functionality like dealing with the
 * handler/service registries and loading properties.
 *
 * @author Glen Daniels (gdaniels@macromedia.com)
 * @author Glyn Normington (glyn@apache.org)
 */
public abstract class AxisEngine extends BasicHandler
{
    protected static Log log =
        LogFactory.getLog(AxisEngine.class.getName());

    // Engine property names
    public static final String PROP_XML_DECL = "sendXMLDeclaration";
    public static final String PROP_DEBUG_LEVEL = "debugLevel";
    public static final String PROP_DEBUG_FILE = "debugFile";
    public static final String PROP_DOMULTIREFS = "sendMultiRefs";
    public static final String PROP_PASSWORD = "adminPassword";
    public static final String PROP_SYNC_CONFIG = "syncConfiguration";
    public static final String PROP_SEND_XSI = "sendXsiTypes";
    public static final String PROP_ATTACHMENT_DIR = "attachments.Directory";
    public static final String PROP_ATTACHMENT_IMPLEMENTATION  = "attachments.implementation" ;
    public static final String PROP_ATTACHMENT_CLEANUP = "attachment.DirectoryCleanUp";
    public static final String PROP_DEFAULT_CONFIG_CLASS = "axis.engineConfigClass";
    public static final String PROP_SOAP_VERSION = "defaultSOAPVersion";

    public static final String DEFAULT_ATTACHMENT_IMPL="org.apache.axis.attachments.AttachmentsImpl";

    public static final String ENV_ATTACHMENT_DIR = "axis.attachments.Directory";
    public static final String ENV_SERVLET_REALPATH = "servlet.realpath";
    public static final String ENV_SERVLET_CONTEXT = "servletContext";

    // Default admin. password
    private static final String DEFAULT_ADMIN_PASSWORD = "admin";


    /** Our go-to guy for configuration... */
    protected EngineConfiguration config;

    /** Has the user changed the password yet? */
    protected boolean _hasSafePassword = false;

    /** Should we save the engine config each time we modify it? */
    protected boolean shouldSaveConfig = false;

    /** Java class cache */
    protected ClassCache classCache = new ClassCache();

    /**
     * This engine's Session.  This Session supports "application scope"
     * in the Apache SOAP sense... if you have a service with "application
     * scope", have it store things in this Session.
     */
    private Session session = new SimpleSession();

    /**
     * What actor URIs hold for the entire engine?
     */
    private ArrayList actorURIs = new ArrayList();

    /**
     * Thread local storage used for locating the active message context.
     * This information is only valid for the lifetime of this request.
     */
    private static ThreadLocal currentMessageContext = new ThreadLocal();

    /**
     * Set the active message context.
     *
     * @param mc - the new active message context.
     */
    protected static void setCurrentMessageContext(MessageContext mc) {
        currentMessageContext.set(mc);
    }

    /**
     * Get the active message context.
     *
     * @return the current active message context
     */
    public static MessageContext getCurrentMessageContext() {
        return (MessageContext) currentMessageContext.get();
    }

    /**
     * Construct an AxisEngine using the specified engine configuration.
     *
     * @param config the EngineConfiguration for this engine
     */
    public AxisEngine(EngineConfiguration config)
    {
        this.config = config;
        init();
    }

    /**
     * (re)initialize - What should really go in here???
     */
    public void init() {
        if (log.isDebugEnabled()) {
            log.debug("Enter: AxisEngine::init");
        }

        // The SOAP/XSD stuff is in the default TypeMapping of the TypeMappingRegistry.
        //getTypeMappingRegistry().setParent(SOAPTypeMappingRegistry.getSingleton());

        try {
            config.configureEngine(this);
        } catch (Exception e) {
            throw new InternalException(e);
        }

        /*Set the default attachment implementation */
        setOptionDefault(PROP_ATTACHMENT_IMPLEMENTATION,
                         AxisProperties.getProperty("axis." + PROP_ATTACHMENT_IMPLEMENTATION  ));

        setOptionDefault(PROP_ATTACHMENT_IMPLEMENTATION, DEFAULT_ATTACHMENT_IMPL);

        if (log.isDebugEnabled()) {
            log.debug("Exit: AxisEngine::init");
        }

    }

    /**
     * cleanup routine removes application scoped objects
     * There is a small risk of this being called more than once
     * so the cleanup should be designed to resist that event
     */
    public void cleanup() {
        super.cleanup();

        // Let any application-scoped service objects know that we're going
        // away...
        Enumeration keys = session.getKeys();
        if (keys != null) {
            while (keys.hasMoreElements()) {
                String key = (String)keys.nextElement();
                Object obj = session.get(key);
                if (obj != null && obj instanceof ServiceLifecycle) {
                    ((ServiceLifecycle)obj).destroy();
                }
                session.remove(key);
            }
        }
    }

    /** Write out our engine configuration.
     */
    public void saveConfiguration()
    {
        if (!shouldSaveConfig)
            return;

        try {
            config.writeEngineConfig(this);
        } catch (Exception e) {
            log.error(JavaUtils.getMessage("saveConfigFail00"), e);
        }
    }

    public EngineConfiguration getConfig() {
        return config;
    }

    public boolean hasSafePassword()
    {
        return _hasSafePassword;
    }

    public void setAdminPassword(String pw)
    {
        setOption(PROP_PASSWORD, pw);
        _hasSafePassword = true;
        saveConfiguration();
    }

    public void setShouldSaveConfig(boolean shouldSaveConfig)
    {
        this.shouldSaveConfig = shouldSaveConfig;
    }

    public Handler getHandler(String name) throws AxisFault
    {
        try {
            return config.getHandler(new QName(null, name));
        } catch (ConfigurationException e) {
            throw new AxisFault(e);
        }
    }

    public SOAPService getService(String name) throws AxisFault
    {
        try {
            return config.getService(new QName(null, name));
        } catch (ConfigurationException e) {
            throw new AxisFault(e);
        }
    }

    public Handler getTransport(String name) throws AxisFault
    {
        try {
            return config.getTransport(new QName(null, name));
        } catch (ConfigurationException e) {
            throw new AxisFault(e);
        }
    }

    public TypeMappingRegistry getTypeMappingRegistry()
    {
        TypeMappingRegistry tmr = null;
        try {
            tmr = config.getTypeMappingRegistry();
        } catch (ConfigurationException e) {
            log.error(JavaUtils.getMessage("axisConfigurationException00"), e);
        }

        return tmr;
    }

    public Handler getGlobalRequest()
        throws ConfigurationException
    {
        return config.getGlobalRequest();
    }

    public Handler getGlobalResponse()
        throws ConfigurationException
    {
        return config.getGlobalResponse();
    }

    public ArrayList getActorURIs()
    {
        return actorURIs;
    }

    public void addActorURI(String uri)
    {
        actorURIs.add(uri);
    }

    public void removeActorURI(String uri)
    {
        actorURIs.remove(uri);
    }

    /*********************************************************************
     * Client engine access
     *
     * An AxisEngine may define another specific AxisEngine to be used
     * by newly created Clients.  For instance, a server may
     * create an AxisClient and allow deployment to it.  Then
     * the server's services may access the AxisClient's deployed
     * handlers and transports.
     *********************************************************************
     */

    public abstract AxisEngine getClientEngine ();

    /*********************************************************************
    * Administration and management APIs
    *
    * These can get called by various admin adapters, such as JMX MBeans,
    * our own Admin client, web applications, etc...
    *
    *********************************************************************
    */

    /**
     * List of options which should be converted from Strings to Booleans
     * automatically. Note that these options are common to all XML
     * web services.
     */
    private static final String [] BOOLEAN_OPTIONS = new String [] {
        PROP_DOMULTIREFS, PROP_SEND_XSI, PROP_XML_DECL
    };

    /**
     * Normalise the engine's options.
     * <p>
     * Convert boolean options from String to Boolean and default
     * any ommitted boolean options to TRUE. Default the admin.
     * password.
     */
    public static void normaliseOptions(Handler handler) {
        // Convert boolean options to Booleans so we don't need to use
        // string comparisons.  Default is "true".

        for (int i = 0; i < BOOLEAN_OPTIONS.length; i++) {
            Object val = handler.getOption(BOOLEAN_OPTIONS[i]);
            if (val != null) {
                if (val instanceof Boolean)
                    continue;
                if (JavaUtils.isFalse(val)) {
                    handler.setOption(BOOLEAN_OPTIONS[i], Boolean.FALSE);
                    continue;
                }
            } else {
                if (!(handler instanceof AxisEngine))
                    continue;
            }
            // If it was null or not "false"...
            handler.setOption(BOOLEAN_OPTIONS[i], Boolean.TRUE);
        }

        // Deal with admin password's default value.
        if (handler instanceof AxisEngine) {
            AxisEngine engine = (AxisEngine)handler;
            if (!engine.setOptionDefault(PROP_PASSWORD,
                                         DEFAULT_ADMIN_PASSWORD)) {
                engine.setAdminPassword(
                        (String)engine.getOption(PROP_PASSWORD));
            }
        }
    }

    /**
     * (Re-)load the global options from the registry.
     */
    public void refreshGlobalOptions() throws ConfigurationException {
        Hashtable globalOptions = config.getGlobalOptions();
        if (globalOptions != null)
            setOptions(globalOptions);

        normaliseOptions(this);
    }

    /**
     * accessor only, for application session
     * (could call it "engine session" instead, but named with reference
     * to Apache SOAP's notion of "application scope")
     */
    public Session getApplicationSession () {
        return session;
    }

    public ClassCache getClassCache() {
        return classCache;
    }

    protected void invokeJAXRPCHandlers(MessageContext context){
        Service service
            = (Service)context.getProperty(Call.WSDL_SERVICE);
        if(service == null)
            return;

        QName portName = (QName) context.getProperty(Call.WSDL_PORT_NAME);
        if(portName == null)
            return;

        javax.xml.rpc.handler.HandlerRegistry registry = service.getHandlerRegistry();
        if(registry == null)
            return;

        java.util.List chain = registry.getHandlerChain(portName);

        if(chain == null || chain.isEmpty())
            return;

        HandlerChainImpl impl
            = new HandlerChainImpl(chain);

        if(!context.getPastPivot())
            impl.handleRequest(context);
        else
            impl.handleResponse(context);
        impl.destroy();
    }
}
