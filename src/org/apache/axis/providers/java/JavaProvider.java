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

package org.apache.axis.providers.java;

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.AxisServiceConfig;
import org.apache.axis.Handler;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.providers.BasicProvider;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.cache.JavaClass;
import org.apache.axis.utils.cache.ClassCache;
import org.apache.axis.wsdl.fromJava.Emitter;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.enum.Style;
import org.apache.axis.enum.Scope;
import org.apache.axis.Constants;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.apache.axis.deployment.wsdd.WSDDService;

import javax.xml.rpc.server.ServiceLifecycle;
import javax.xml.rpc.holders.IntHolder;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

/**
 * Base class for Java dispatching.  Fetches various fields out of envelope,
 * looks up service object (possibly using session state), and delegates
 * envelope body processing to subclass via abstract processMessage method.
 *
 * @author Doug Davis (dug@us.ibm.com)
 * @author Carl Woolf (cwoolf@macromedia.com)
 */
public abstract class JavaProvider extends BasicProvider
{
    protected static Log log =
        LogFactory.getLog(JavaProvider.class.getName());

    public static final String OPTION_CLASSNAME = "className";
    public static final String OPTION_ALLOWEDMETHODS = "allowedMethods";
    public static final String OPTION_IS_STATIC = "isStatic";
    public static final String OPTION_CLASSPATH = "classPath";
    
    public static final String OPTION_SCOPE = "scope";

    /**
     * Get the service object whose method actually provides the service.
     * May look up in session table.
     */
    private Object getServiceObject (MessageContext msgContext,
                                    Handler service,
                                    String clsName,
                                    IntHolder scopeHolder)
        throws Exception
    {
        String serviceName = msgContext.getService().getName();

        // scope can be "Request", "Session", "Application"
        // (as with Apache SOAP)
        Scope scope = Scope.getScope((String)service.getOption(OPTION_SCOPE), Scope.DEFAULT);
        
        scopeHolder.value = scope.getValue();

        if (scope == Scope.REQUEST) {
            // make a one-off
            return getNewServiceObject(msgContext, clsName);
        } else if (scope == Scope.SESSION) {
            // What do we do if serviceName is null at this point???
            if (serviceName == null)
                serviceName = msgContext.getService().toString();

            // look in incoming session
            if (msgContext.getSession() != null) {
                // This part isn't thread safe...
                synchronized (this) {
                    // store service objects in session, indexed by class name
                    Object obj = msgContext.getSession().get(serviceName);
                    if (obj == null) {
                        obj = getNewServiceObject(msgContext, clsName);
                        msgContext.getSession().set(serviceName, obj);
                    }
                    return obj;
                }
            } else {
                // was no incoming session, sigh, treat as request scope
                scopeHolder.value = Scope.DEFAULT.getValue();
                return getNewServiceObject(msgContext, clsName);
            }
        } else if (scope == Scope.APPLICATION) {
            // MUST be AxisEngine here!
            AxisEngine engine = msgContext.getAxisEngine();
            if (engine.getApplicationSession() != null) {
                // This part isn't thread safe
                synchronized (this) {
                    // store service objects in session, indexed by class name
                    Object obj =
                            engine.getApplicationSession().get(serviceName);
                    if (obj == null) {
                        obj = getNewServiceObject(msgContext, clsName);
                        engine.getApplicationSession().set(serviceName, obj);
                    }
                    return obj;
                }
            } else {
                // was no application session, sigh, treat as request scope
                // FIXME : Should we bomb in this case?
                scopeHolder.value = Scope.DEFAULT.getValue();
                return getNewServiceObject(msgContext, clsName);
            }
        } else {
            // NOTREACHED
            return null;
        }
    }

    /**
     * Return a new service object which, if it implements the ServiceLifecycle
     * interface, has been init()ed.  Right now we pass "null" as the context
     * argument to init() - this might want to be the MessageContext?
     *
     * @param msgContext the MessageContext
     * @param clsName the name of the class to instantiate
     * @return an initialized service object
     */
    private Object getNewServiceObject(MessageContext msgContext,
                                       String clsName) throws Exception
    {
        Object serviceObject = makeNewServiceObject(msgContext, clsName);
        if (serviceObject != null &&
                serviceObject instanceof ServiceLifecycle) {
            ((ServiceLifecycle)serviceObject).init(null);
        }
        return serviceObject;
    }

    /**
     * Process the current message.  Side-effect resEnv to create return value.
     *
     * @param msgContext self-explanatory
     * @param serviceName the class name of the ServiceHandler
     * @param allowedMethods the 'method name' of ditto
     * @param reqEnv the request envelope
     * @param resEnv the response envelope
     * @param jc the JavaClass of the service object
     * @param obj the service object itself
     */
    public abstract void processMessage (MessageContext msgContext,
                                         String serviceName,
                                         String allowedMethods,
                                         SOAPEnvelope reqEnv,
                                         SOAPEnvelope resEnv,
                                         JavaClass jc,
                                         Object obj)
        throws Exception;


    /**
     * Invoke the message by obtaining various common fields, looking up
     * the service object (via getServiceObject), and actually processing
     * the message (via processMessage).
     */
    public void invoke(MessageContext msgContext) throws AxisFault {
        if (log.isDebugEnabled())
            log.debug("Enter: JavaProvider::invoke (" + this + ")");

        /* Find the service we're invoking so we can grab it's options */
        /***************************************************************/
        String serviceName = msgContext.getTargetService();
        Handler service = msgContext.getService();

        /* Now get the service (RPC) specific info  */
        /********************************************/
        String  clsName    = getServiceClassName(service);
        String  allowedMethods = getAllowedMethods(service);

        if ((clsName == null) || clsName.equals("")) {
            throw new AxisFault("Server.NoClassForService",
                JavaUtils.getMessage("noOption00", getServiceClassNameOptionName(), serviceName),
                null, null);
        }

        if ((allowedMethods == null) || allowedMethods.equals("")) {
            throw new AxisFault("Server.NoMethodConfig",
                JavaUtils.getMessage("noOption00",
                                     OPTION_ALLOWEDMETHODS, serviceName),
                null, null);
        }

        if (allowedMethods.equals("*"))
            allowedMethods = null;

        try {
            IntHolder scope   = new IntHolder();
            Object obj        = getServiceObject(msgContext,
                                                 service,
                                                 clsName,
                                                 scope);
            JavaClass jc	  = JavaClass.find(obj.getClass());

            Message        reqMsg  = msgContext.getRequestMessage();
            SOAPEnvelope   reqEnv  = (SOAPEnvelope)reqMsg.getSOAPEnvelope();
            Message        resMsg  = msgContext.getResponseMessage();
            SOAPEnvelope   resEnv  = (resMsg == null) ?
                                     new SOAPEnvelope(msgContext.
                                                        getSOAPConstants()) :
                                     (SOAPEnvelope)resMsg.getSOAPEnvelope();

            // If we didn't have a response message, make sure we set one up
            if (resMsg == null) {
                resMsg = new Message(resEnv);
                msgContext.setResponseMessage( resMsg );
            }

            /** If the class knows what it should be exporting,
            * respect its wishes.
            */
            AxisServiceConfig axisConfig = getConfiguration(obj);
            if (axisConfig != null) {
                allowedMethods = axisConfig.getAllowedMethods();
            }

            try {
                processMessage(msgContext, clsName, allowedMethods, reqEnv,
                               resEnv, jc, obj);
            } catch (Exception exp) {
                throw exp;
            } finally {
                // If this is a request scoped service object which implements
                // ServiceLifecycle, let it know that it's being destroyed now.
                if (scope.value == Scope.REQUEST.getValue() &&
                        obj instanceof ServiceLifecycle) {
                    ((ServiceLifecycle)obj).destroy();
                }
            }
        }
        catch( Exception exp ) {
            log.debug( JavaUtils.getMessage("toAxisFault00"), exp);
            throw AxisFault.makeFault(exp);
        }

        if (log.isDebugEnabled())
            log.debug("Exit: JavaProvider::invoke (" + this + ")");
    }

    /**
     * Generate the WSDL for this service.
     *
     * Put in the "WSDL" property of the message context
     * as a org.w3c.dom.Document
     */
    public void generateWSDL(MessageContext msgContext) throws AxisFault {
        if (log.isDebugEnabled())
            log.debug("Enter: JavaProvider::generateWSDL (" + this + ")");

        /* Find the service we're invoking so we can grab it's options */
        /***************************************************************/
        String serviceName = msgContext.getTargetService();
        SOAPService service = msgContext.getService();

        /* Now get the service (RPC) specific info  */
        /********************************************/
        String  allowedMethods = getAllowedMethods(service);

        /** ??? Should we enforce setting methodName?  As it was,
         * if it's null, we allowed any method.  This seems like it might
         * be considered somewhat insecure (it's an easy mistake to
         * make).  Tossing an Exception if it's not set, and using "*"
         * to explicitly indicate "any method" is probably better.
         */
        if ((allowedMethods == null) || allowedMethods.equals(""))
          throw new AxisFault("Server.NoMethodConfig",
            JavaUtils.getMessage("noOption00", getServiceClassNameOptionName(), serviceName),
            null, null);

        if (allowedMethods.equals("*"))
          allowedMethods = null;

        try {
            String url = msgContext.getStrProp(MessageContext.TRANS_URL);
            String interfaceNamespace = 
                    msgContext.getStrProp(MessageContext.WSDLGEN_INTFNAMESPACE);
            if (interfaceNamespace == null) 
                interfaceNamespace = url;
            String locationUrl = 
                    msgContext.getStrProp(MessageContext.WSDLGEN_SERV_LOC_URL);
            
            if (locationUrl == null) {
                locationUrl = url;
            } else {
                try {
                    URL urlURL = new URL(url);
                    URL locationURL = new URL(locationUrl);
                    URL urlTemp = new URL(urlURL.getProtocol(),
                            locationURL.getHost(),
                            locationURL.getPort(),
                            urlURL.getFile());
                    interfaceNamespace += urlURL.getFile();
                    locationUrl = urlTemp.toString();
                } catch (Exception e) {
                    locationUrl = url;
                    interfaceNamespace = url;
                }
            }


            Class cls = getServiceClass(msgContext, getServiceClassName(service));

            // If the class knows what it should be exporting, respect it's
            // wishes.
            AxisServiceConfig axisConfig = getConfiguration(cls);
            if (axisConfig != null) {
                allowedMethods = axisConfig.getAllowedMethods();
            }

            Emitter emitter = new Emitter();

            // service alias may be provided if exact naming is required,
            // otherwise Axis will name it according to the implementing class name
            String alias = (String)service.getOption("alias");
            if(alias != null) emitter.setServiceElementName(alias);

            emitter.setMode( (service.getStyle() == Style.RPC)
                             ? Emitter.MODE_RPC
                             : Emitter.MODE_DOCUMENT);

            emitter.setClsSmart(cls,url);
            emitter.setAllowedMethods(allowedMethods);
            emitter.setIntfNamespace(interfaceNamespace);
            emitter.setLocationUrl(locationUrl);
            emitter.setServiceDesc(msgContext.getService().getInitializedServiceDesc(msgContext));
            emitter.setTypeMapping((TypeMapping)msgContext.getTypeMappingRegistry().
                                   getTypeMapping(Constants.URI_DEFAULT_SOAP_ENC));
            emitter.setDefaultTypeMapping((TypeMapping)msgContext.getTypeMappingRegistry().
                                          getDefaultTypeMapping());
            Document  doc = emitter.emit(Emitter.MODE_ALL);

            msgContext.setProperty("WSDL", doc);
        } catch (NoClassDefFoundError e) {
            log.info( JavaUtils.getMessage("toAxisFault00"), e );
            throw new AxisFault(e.toString(), e);
        } catch (Exception e) {
            log.info( JavaUtils.getMessage("toAxisFault00"), e );
            throw AxisFault.makeFault(e);
        }

        if (log.isDebugEnabled())
            log.debug("Exit: JavaProvider::generateWSDL (" + this + ")");
    }

    private String getAllowedMethods(Handler service)
    {
        String val = (String)service.getOption(OPTION_ALLOWEDMETHODS);
        if (val == null || val.length() == 0) {
            // Try the old option for backwards-compatibility
            val = (String)service.getOption("methodName");
        }
        return val;
    }

    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
    /////// Default methods for java classes. Override, eg, for
    ///////   ejbeans
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Default java service object comes from simply instantiating the
     * class wrapped in jc
     *
     */
    protected Object makeNewServiceObject(MessageContext msgContext,
                                             String clsName)
        throws Exception
    {
        ClassLoader cl     = msgContext.getClassLoader();
        ClassCache cache   = msgContext.getAxisEngine().getClassCache();
        JavaClass  jc      = cache.lookup(clsName, cl);

        return jc.getJavaClass().newInstance();
    }

    /**
     * Return the class name of the service
     */
    protected String getServiceClassName(Handler service)
    {
        return (String) service.getOption( getServiceClassNameOptionName() );
    }

    /**
     * Return the option in the configuration that contains the service class
     * name
     */
    protected String getServiceClassNameOptionName()
    {
        return OPTION_CLASSNAME;
    }

    /**
     * Returns the Class info about the service class.
     */
    protected Class getServiceClass(MessageContext msgContext, String clsName)
            throws Exception {
        ClassLoader cl     = msgContext.getClassLoader();
        ClassCache cache   = msgContext.getAxisEngine().getClassCache();
        JavaClass  jc      = cache.lookup(clsName, cl);
        return jc.getJavaClass();
    }

    /**
     * For a given object or class, if there is a static method called
     * "getAxisServiceConfig()", we call it and return the value as an
     * AxisServiceConfig object.  This allows us to obtain metadata about
     * a class' configuration without instantiating an object of that class.
     *
     * @param obj an object, which may be a Class
     */
    public AxisServiceConfig getConfiguration(Object obj)
    {
        Class cls;
        if (obj instanceof Class) {
            cls = (Class)obj;
        } else {
            cls = obj.getClass();
        }

        try {
            Method method =
                    cls.getDeclaredMethod("getAxisServiceConfig", new Class [] {});
            if (method != null && Modifier.isStatic(method.getModifiers())) {
                return (AxisServiceConfig)method.invoke(null, null);
            }
        } catch (NoSuchMethodException e) {
        } catch (SecurityException e) {
        } catch (IllegalAccessException e) {
        } catch (IllegalArgumentException e) {
        } catch (InvocationTargetException e) {
        }

        return null;
    }
}
