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

package org.apache.axis;

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.session.Session;
import org.apache.axis.session.SimpleSession;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.cache.ClassCache;
import org.apache.commons.logging.Log;

import javax.xml.namespace.QName;
import javax.xml.rpc.server.ServiceLifecycle;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;


/**
 * An <code>AxisEngine</code> is the base class for AxisClient and
 * AxisServer.  Handles common functionality like dealing with the
 * handler/service registries and loading properties.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 * @author Glyn Normington (glyn@apache.org)
 */
public abstract class AxisEngine extends BasicHandler
{
    /**
     * The <code>Log</code> for all message logging.
     */
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
    public static final String PROP_SOAP_ALLOWED_VERSION = "singleSOAPVersion";
    public static final String PROP_TWOD_ARRAY_ENCODING = "enable2DArrayEncoding";
    public static final String PROP_XML_ENCODING = "axis.xmlEncoding";

    public static final String DEFAULT_ATTACHMENT_IMPL="org.apache.axis.attachments.AttachmentsImpl";

    public static final String ENV_ATTACHMENT_DIR = "axis.attachments.Directory";
    public static final String ENV_SERVLET_REALPATH = "servlet.realpath";
    public static final String ENV_SERVLET_CONTEXT = "servletContext";

    // Default admin. password
    private static final String DEFAULT_ADMIN_PASSWORD = "admin";


    /** Our go-to guy for configuration... */
    protected EngineConfiguration config;

    /** Has the user changed the password yet? True if they have. */
    protected boolean _hasSafePassword = false;

    /**
     * Should we save the engine config each time we modify it? True if we
     * should.
     */
    protected boolean shouldSaveConfig = false;

    /** Java class cache. */
    protected ClassCache classCache = new ClassCache();

    /**
     * This engine's Session.  This Session supports "application scope"
     * in the Apache SOAP sense... if you have a service with "application
     * scope", have it store things in this Session.
     */
    private Session session = new SimpleSession();

    /**
     * What actor URIs hold for the entire engine? Find them here.
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
     * Initialize the engine. Multiple calls will (may?) return the engine to
     * the intialized state.
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
     * Cleanup routine removes application scoped objects.
     *
     * There is a small risk of this being called more than once
     * so the cleanup should be designed to resist that event.
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
            log.error(Messages.getMessage("saveConfigFail00"), e);
        }
    }

    /**
     * Get the <code>EngineConfiguration</code> used throughout this
     * <code>AxisEngine</code> instance.
     *
     * @return the engine configuration instance
     */
    public EngineConfiguration getConfig() {
        return config;
    }

    /**
     * Discover if this <code>AxisEngine</code> has a safe password.
     *
     * @return  true if it is safe, false otherwise
     */
    public boolean hasSafePassword()
    {
        return _hasSafePassword;
    }

    /**
     * Set the administration password.
     *
     * @param pw  the literal value of the password as a <code>String</code>
     */
    public void setAdminPassword(String pw)
    {
        setOption(PROP_PASSWORD, pw);
        _hasSafePassword = true;
        saveConfiguration();
    }

    /**
     * Set the flag that controls if the configuration should be saved.
     *
     * @param shouldSaveConfig  true if the configuration should be changed,
     *              false otherwise
     */
    public void setShouldSaveConfig(boolean shouldSaveConfig)
    {
        this.shouldSaveConfig = shouldSaveConfig;
    }

    // fixme: could someone who knows double-check I've got the semantics of
    //   this right?
    /**
     * Get the <code>Handler</code> for a particular local name.
     *
     * @param name  the local name of the request type
     * @return      the <code>Handler</code> for this request type
     * @throws AxisFault
     */
    public Handler getHandler(String name) throws AxisFault
    {
        try {
            return config.getHandler(new QName(null, name));
        } catch (ConfigurationException e) {
            throw new AxisFault(e);
        }
    }

    // fixme: could someone who knows double-check I've got the semantics of
    //   this right?
    /**
     * Get the <code>SOAPService</code> for a particular local name.
     *
     * @param name  the local name of the request type
     * @return      the <code>SOAPService</code> for this request type
     * @throws AxisFault
     */
    public SOAPService getService(String name) throws AxisFault
    {
        try {
            return config.getService(new QName(null, name));
        } catch (ConfigurationException e) {
            try {
                return config.getServiceByNamespaceURI(name);
            } catch (ConfigurationException e1) {
                throw new AxisFault(e);
            }
        }
    }

    /**
     * Get the <code>Handler</code> that implements the transport for a local
     * name.
     *
     * @param name  the local name to fetch the transport for
     * @return  a <code>Handler</code> for this local name
     * @throws AxisFault
     */
    public Handler getTransport(String name) throws AxisFault
    {
        try {
            return config.getTransport(new QName(null, name));
        } catch (ConfigurationException e) {
            throw new AxisFault(e);
        }
    }

    /**
     * Get the <code>TypeMappingRegistry</code> for this axis engine.
     *
     * @return the <code>TypeMappingRegistry</code> if possible, or null if
     *              there is any error resolving it
     */
    public TypeMappingRegistry getTypeMappingRegistry()
    {
        TypeMappingRegistry tmr = null;
        try {
            tmr = config.getTypeMappingRegistry();
        } catch (ConfigurationException e) {
            log.error(Messages.getMessage("axisConfigurationException00"), e);
        }

        return tmr;
    }

    /**
     * Get the global request <code>Handler</code>.
     *
     * @return the <code>Handler</code> used for global requests
     * @throws ConfigurationException
     */
    public Handler getGlobalRequest()
        throws ConfigurationException
    {
        return config.getGlobalRequest();
    }

    /**
     * Get the global respones <code>Handler</code>.
     *
     * @return the <code>Handler</code> used for global responses
     * @throws ConfigurationException
     */
    public Handler getGlobalResponse()
        throws ConfigurationException
    {
        return config.getGlobalResponse();
    }

    // fixme: this exposes the /actual/ actorsURIs array - do we intend this?
    //   if so, then we need to document this so that people don't change this
    //   by accident but have the chance to change a copy
    // fixme: publishing this as ArrayList prevents us moving to another
    //   List impl later
    /**
     * Get a list of actor URIs that hold for the entire engine.
     *
     * @return an <code>ArrayList</code> of all actor URIs as
     *              <code>Strings</code>
     */
    public ArrayList getActorURIs()
    {
        return actorURIs;
    }

    /**
     * Add an actor by uri that will hold for the entire engine.
     *
     * @param uri  a <code>String</code> giving the uri of the actor to add
     */
    public void addActorURI(String uri)
    {
        actorURIs.add(uri);
    }

    /**
     * Remove an actor by uri that will hold for the entire engine.
     *
     * @param uri  a <code>String</code> giving the uri of the actor to remove
     */
    public void removeActorURI(String uri)
    {
        actorURIs.remove(uri);
    }

    /**
     * Client engine access.
     * <p>
     * An AxisEngine may define another specific AxisEngine to be used
     * by newly created Clients.  For instance, a server may
     * create an AxisClient and allow deployment to it.  Then
     * the server's services may access the AxisClient's deployed
     * handlers and transports.
     *
     * @return an <code>AxisEngine</code> that is the client engine
     */

    public abstract AxisEngine getClientEngine ();

   /**
    * Administration and management APIs
    *
    * These can get called by various admin adapters, such as JMX MBeans,
    * our own Admin client, web applications, etc...
    *
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
     *
     * @param handler  the <code>Handler</code> to normalise; instances of
     *              <code>AxisEngine</code> get extra data normalised
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
     *
     * @throws ConfigurationException
     */
    public void refreshGlobalOptions() throws ConfigurationException {
        Hashtable globalOptions = config.getGlobalOptions();
        if (globalOptions != null)
            setOptions(globalOptions);

        normaliseOptions(this);
    }

    /**
     * Get the <code>Session</code> object associated with the application
     * session.
     *
     * @return a <code>Session</code> scoped to the application
     */
    public Session getApplicationSession () {
        return session;
    }

    /**
     * Get the <code>ClassCache</code> associated with this engine.
     *
     * @return the class cache
     */
    public ClassCache getClassCache() {
        return classCache;
    }

}
