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
import org.apache.axis.Constants;
import org.apache.axis.Handler;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.enum.Scope;
import org.apache.axis.enum.Style;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.providers.BasicProvider;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.cache.ClassCache;
import org.apache.axis.utils.cache.JavaClass;
import org.apache.axis.wsdl.fromJava.Emitter;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.enum.Style;
import org.apache.axis.enum.Scope;
import org.apache.axis.Constants;
import org.apache.axis.session.Session;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.handlers.soap.SOAPService;

import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;
import org.w3c.dom.Document;

import javax.xml.rpc.holders.IntHolder;
import javax.xml.rpc.server.ServiceLifecycle;
import java.util.ArrayList;
import java.util.StringTokenizer;

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

    // The enterprise category is for stuff that an enterprise product might
    // want to track, but in a simple environment (like the AXIS build) would
    // be nothing more than a nuisance.
    protected static Log entLog =
        LogFactory.getLog(Constants.ENTERPRISE_LOG_CATEGORY);

    public static final String OPTION_CLASSNAME = "className";
    public static final String OPTION_ALLOWEDMETHODS = "allowedMethods";
    public static final String OPTION_IS_STATIC = "isStatic";
    public static final String OPTION_CLASSPATH = "classPath";
    public static final String OPTION_WSDL_PORTTYPE="wsdlPortType";
    public static final String OPTION_WSDL_SERVICEELEMENT="wsdlServiceElement";
    public static final String OPTION_WSDL_SERVICEPORT="wsdlServicePort";
    public static final String OPTION_WSDL_TARGETNAMESPACE="wsdlTargetNamespace";

    public static final String OPTION_SCOPE = "scope";

    /**
     * Get the service object whose method actually provides the service.
     * May look up in session table.
     */
    public Object getServiceObject (MessageContext msgContext,
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
            Session session = msgContext.getSession();
            if (session != null) {
                // This part isn't thread safe...
                synchronized (session) {
                    // store service objects in session, indexed by class name
                    Object obj = session.get(serviceName);
                    if (obj == null) {
                        obj = getNewServiceObject(msgContext, clsName);
                        session.set(serviceName, obj);
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
            Session appSession = engine.getApplicationSession();
            if (appSession != null) {
                // This part isn't thread safe
                synchronized (appSession) {
                    // store service objects in session, indexed by class name
                    Object obj = appSession.get(serviceName);
                    if (obj == null) {
                        obj = getNewServiceObject(msgContext, clsName);
                        appSession.set(serviceName, obj);
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
     * interface, has been init()ed.
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
            ((ServiceLifecycle)serviceObject).init(
                  msgContext.getProperty(Constants.MC_SERVLET_ENDPOINT_CONTEXT));
        }
        return serviceObject;
    }

    /**
     * Process the current message.  Side-effect resEnv to create return value.
     *
     * @param msgContext self-explanatory
     * @param reqEnv the request envelope
     * @param resEnv the response envelope
     * @param obj the service object itself
     */
    public abstract void processMessage (MessageContext msgContext,
                                         SOAPEnvelope reqEnv,
                                         SOAPEnvelope resEnv,
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

        if ((clsName == null) || clsName.equals("")) {
            throw new AxisFault("Server.NoClassForService",
                JavaUtils.getMessage("noOption00", getServiceClassNameOptionName(), serviceName),
                null, null);
        }

        try {
            IntHolder scope   = new IntHolder();
            Object obj        = getServiceObject(msgContext,
                                                 service,
                                                 clsName,
                                                 scope);

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

            try {
                processMessage(msgContext, reqEnv,
                               resEnv, obj);
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
            entLog.debug( JavaUtils.getMessage("toAxisFault00"), exp);
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
        SOAPService service = msgContext.getService();
        ServiceDesc serviceDesc = service.getInitializedServiceDesc(msgContext);

        // Calculate the appropriate namespaces for the WSDL we're going
        // to put out.
        //
        // If we've been explicitly told which namespaces to use, respect
        // that.  If not:
        //
        // The "interface namespace" should be either:
        // 1) The namespace of the ServiceDesc
        // 2) The transport URL (if there's no ServiceDesc ns)

        try {
            // Location URL is whatever is explicitly set in the MC
            String locationUrl =
                    msgContext.getStrProp(MessageContext.WSDLGEN_SERV_LOC_URL);

            if (locationUrl == null) {
                // If nothing, try what's explicitly set in the ServiceDesc
                locationUrl = serviceDesc.getEndpointURL();
            }

            if (locationUrl == null) {
                // If nothing, use the actual transport URL
                locationUrl = msgContext.getStrProp(MessageContext.TRANS_URL);
            }

            // Interface namespace is whatever is explicitly set
            String interfaceNamespace =
                    msgContext.getStrProp(MessageContext.WSDLGEN_INTFNAMESPACE);

            if (interfaceNamespace == null) {
                // If nothing, use the default namespace of the ServiceDesc
                interfaceNamespace = serviceDesc.getDefaultNamespace();
            }

            if (interfaceNamespace == null) {
                // If nothing still, use the location URL determined above
                interfaceNamespace = locationUrl;
            }

//  Do we want to do this?
//
//            if (locationUrl == null) {
//                locationUrl = url;
//            } else {
//                try {
//                    URL urlURL = new URL(url);
//                    URL locationURL = new URL(locationUrl);
//                    URL urlTemp = new URL(urlURL.getProtocol(),
//                            locationURL.getHost(),
//                            locationURL.getPort(),
//                            urlURL.getFile());
//                    interfaceNamespace += urlURL.getFile();
//                    locationUrl = urlTemp.toString();
//                } catch (Exception e) {
//                    locationUrl = url;
//                    interfaceNamespace = url;
//                }
//            }

            Emitter emitter = new Emitter();

            // service alias may be provided if exact naming is required,
            // otherwise Axis will name it according to the implementing class name
            String alias = (String)service.getOption("alias");
            if(alias != null) emitter.setServiceElementName(alias);

            emitter.setMode( (service.getStyle() == Style.RPC)
                             ? Emitter.MODE_RPC
                             : Emitter.MODE_DOCUMENT);

            emitter.setClsSmart(serviceDesc.getImplClass(), locationUrl);

            // If a wsdl target namespace was provided, use the targetNamespace.
            // Otherwise use the interfaceNamespace constructed above.
            String targetNamespace = (String) service.getOption(OPTION_WSDL_TARGETNAMESPACE);
            if (targetNamespace == null ||
                targetNamespace.length() == 0) {
                targetNamespace = interfaceNamespace;
            }
            emitter.setIntfNamespace(targetNamespace);

            emitter.setLocationUrl(locationUrl);
            emitter.setServiceDesc(serviceDesc);
            emitter.setTypeMapping((TypeMapping)msgContext.getTypeMappingRegistry().
                                   getTypeMapping(Constants.URI_DEFAULT_SOAP_ENC));
            emitter.setDefaultTypeMapping((TypeMapping)msgContext.getTypeMappingRegistry().
                                          getDefaultTypeMapping());

            String wsdlPortType = (String) service.getOption(OPTION_WSDL_PORTTYPE);
            String wsdlServiceElement = (String) service.getOption(OPTION_WSDL_SERVICEELEMENT);
            String wsdlServicePort = (String) service.getOption(OPTION_WSDL_SERVICEPORT);

            if (wsdlPortType != null && wsdlPortType.length() > 0) {
                emitter.setPortTypeName(wsdlPortType);
            }
            if (wsdlServiceElement != null && wsdlServiceElement.length() > 0) {
                emitter.setServiceElementName(wsdlServiceElement);
            }
            if (wsdlServicePort != null && wsdlServicePort.length() > 0) {
                emitter.setServicePortName(wsdlServicePort);
            }

            Document  doc = emitter.emit(Emitter.MODE_ALL);

            msgContext.setProperty("WSDL", doc);
        } catch (NoClassDefFoundError e) {
            entLog.info( JavaUtils.getMessage("toAxisFault00"), e );
            throw new AxisFault(e.toString(), e);
        } catch (Exception e) {
            entLog.info( JavaUtils.getMessage("toAxisFault00"), e );
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
    protected Class getServiceClass(String clsName,
                                    SOAPService service,
                                    MessageContext msgContext)
            throws AxisFault {
        ClassLoader cl = null;
        Class serviceClass = null;
        AxisEngine engine = service.getEngine();

        // If we have a message context, use that to get classloader and engine
        // otherwise get the current threads classloader
        cl = Thread.currentThread().getContextClassLoader();

        // If we have an engine, use its class cache
        if (engine != null) {
            ClassCache cache     = engine.getClassCache();
            try {
                JavaClass jc = cache.lookup(clsName, cl);
                serviceClass = jc.getJavaClass();
            } catch (ClassNotFoundException e) {
                log.error(JavaUtils.getMessage("exception00"), e);
                throw new AxisFault(JavaUtils.getMessage("noClassForService00", clsName), e);
            }
        } else {
            // if no engine, we don't have a cache, use Class.forName instead.
            try {
                serviceClass = ClassUtils.forName(clsName, true, cl);
            } catch (ClassNotFoundException e) {
                log.error(JavaUtils.getMessage("exception00"), e);
                throw new AxisFault(JavaUtils.getMessage("noClassForService00", clsName), e);
            }
        }
        return serviceClass;
    }

    /**
     * Fill in a service description with the correct impl class
     * and typemapping set.  This uses methods that can be overridden by
     * other providers (like the EJBProvider) to get the class from the
     * right place.
     */
    public void initServiceDesc(SOAPService service, MessageContext msgContext)
            throws AxisFault
    {
        // Set up the Implementation class for the service

        String clsName = getServiceClassName(service);
        if (clsName == null) {
            throw new AxisFault(JavaUtils.getMessage("noServiceClass"));
        }
        Class cls = getServiceClass(clsName, service, msgContext);
        ServiceDesc serviceDescription = service.getServiceDescription();

        // And the allowed methods, if necessary
        if (serviceDescription.getAllowedMethods() == null && service != null) {
            String allowedMethods = getAllowedMethods(service);
            if (allowedMethods != null && !"*".equals(allowedMethods)) {
                ArrayList methodList = new ArrayList();
                StringTokenizer tokenizer = new StringTokenizer(allowedMethods, " ,");
                while (tokenizer.hasMoreTokens()) {
                    methodList.add(tokenizer.nextToken());
                }
                serviceDescription.setAllowedMethods(methodList);
            }
        }

        serviceDescription.loadServiceDescByIntrospection(cls);
    }

}
