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

import org.apache.commons.logging.Log;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Handler;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;

import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * A basic EJB Provider
 *
 * @author Carl Woolf (cwoolf@macromedia.com)
 * @author Tom Jordahl (tomj@macromedia.com)
 */
public class EJBProvider extends RPCProvider
{
    protected static Log log =
        LogFactory.getLog(EJBProvider.class.getName());

    // The enterprise category is for stuff that an enterprise product might
    // want to track, but in a simple environment (like the AXIS build) would
    // be nothing more than a nuisance.
    protected static Log entLog =
        LogFactory.getLog(Constants.ENTERPRISE_LOG_CATEGORY);

    public static final String OPTION_BEANNAME = "beanJndiName";
    public static final String OPTION_HOMEINTERFACENAME = "homeInterfaceName";
    public static final String OPTION_REMOTEINTERFACENAME = "remoteInterfaceName";
    
    public static final String jndiContextClass = "jndiContextClass";
    public static final String jndiURL = "jndiURL";
    public static final String jndiUsername = "jndiUser";
    public static final String jndiPassword = "jndiPassword";
    
    protected static final Class[] empty_class_array = new Class[0];
    protected static final Object[] empty_object_array = new Object[0];
    
    private static InitialContext cached_context = null;

    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
    /////// Default methods from JavaProvider ancestor, overridden
    ///////   for ejbeans
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Return a object which implements the service.
     * 
     * @param msgContext the message context
     * @param clsName The JNDI name of the EJB home class
     * @return an object that implements the service
     */
    protected Object makeNewServiceObject(MessageContext msgContext,
                                          String clsName)
        throws Exception
    {
        // Get the EJB Home object from JNDI
        Object ejbHome = getEJBHome(msgContext.getService(),
                                    msgContext, clsName);

        // Get the Home class name from the configuration file
        // NOTE: Do we really need to have this in the config file
        // since we can get it from ejbHome.getClass()???
        String homeName = getStrOption(OPTION_HOMEINTERFACENAME, 
                                                msgContext.getService());
        if (homeName == null) 
            throw new AxisFault(
                    Messages.getMessage("noOption00", 
                                         OPTION_HOMEINTERFACENAME, 
                                         msgContext.getTargetService()));

        // Load the Home class name given in the config file
        Class homeClass = ClassUtils.forName(homeName, true, msgContext.getClassLoader());

        // Make sure the object we got back from JNDI is the same type
        // as the what is specified in the config file
        Object ehome = javax.rmi.PortableRemoteObject.narrow(ejbHome, homeClass);

        // Invoke the create method of the ejbHome class without actually
        // touching any EJB classes (i.e. no cast to EJBHome)
        Method createMethod = homeClass.getMethod("create", empty_class_array);
        Object result = createMethod.invoke(ehome, empty_object_array);

        return result;
    }

    /**
     * Return the option in the configuration that contains the service class
     * name.  In the EJB case, it is the JNDI name of the bean.
     */
    protected String getServiceClassNameOptionName()
    {
        return OPTION_BEANNAME;
    }

    /**
     * Get a String option by looking first in the service options,
     * and then at the Handler's options.  This allows defaults to be
     * specified at the provider level, and then overriden for particular
     * services.
     *
     * @param optionName the option to retrieve
     * @return String the value of the option or null if not found in
     *                either scope
     */
    protected String getStrOption(String optionName, Handler service)
    {
        String value = null;
        if (service != null)
            value = (String)service.getOption(optionName);
        if (value == null)
            value = (String)getOption(optionName);
        return value;
    }
    
    /**
     * Get the class description for the EJB Remote Interface, which is what
     * we are interested in exposing to the world (i.e. in WSDL).
     * 
     * @param msgContext the message context
     * @param beanJndiName the JNDI name of the EJB
     * @return the class info of the EJB remote interface
     */ 
    protected Class getServiceClass(String beanJndiName,
                                    SOAPService service,
                                    MessageContext msgContext)
        throws AxisFault
    {
        Class interfaceClass = null;
        
        // First try to get the interface class from the configuation
        String remoteName = 
                (String) getStrOption(OPTION_REMOTEINTERFACENAME, service);
        try {
            ClassLoader cl = (msgContext != null) ?
                    msgContext.getClassLoader() :
                    Thread.currentThread().getContextClassLoader();

            if(remoteName != null){
                interfaceClass = ClassUtils.forName(remoteName,
                                                    true,
                                                    cl);
            }
            else
            {
                // Get the EJB Home object from JNDI
                Object ejbHome = getEJBHome(service, msgContext, beanJndiName);

                String homeName = (String)getStrOption(OPTION_HOMEINTERFACENAME,
                                                        service);
                if (homeName == null)
                    throw new AxisFault(
                            Messages.getMessage("noOption00",
                                                 OPTION_HOMEINTERFACENAME,
                                                 service.getName()));

                // Load the Home class name given in the config file
                Class homeClass = ClassUtils.forName(homeName, true, cl);

                // Make sure the object we got back from JNDI is the same type
                // as the what is specified in the config file
                Object ehome = javax.rmi.PortableRemoteObject.narrow(ejbHome, homeClass);

                // This code requires the use of ejb.jar, so we do the stuff below
                //   EJBHome ejbHome = (EJBHome) ehome;
                //   EJBMetaData meta = ejbHome.getEJBMetaData();
                //   Class interfaceClass = meta.getRemoteInterfaceClass();

                // Invoke the getEJBMetaData method of the ejbHome class without
                // actually touching any EJB classes (i.e. no cast to EJBHome)
                Method getEJBMetaData =
                        homeClass.getMethod("getEJBMetaData", empty_class_array);
                Object metaData =
                        getEJBMetaData.invoke(ehome, empty_object_array);
                Method getRemoteInterfaceClass =
                        metaData.getClass().getMethod("getRemoteInterfaceClass",
                                                      empty_class_array);
                interfaceClass =
                        (Class) getRemoteInterfaceClass.invoke(metaData,
                                                               empty_object_array);
            }
        } catch (Exception e) {
            throw AxisFault.makeFault(e);
        }

        // got it, return it
       return interfaceClass;
        
    }

    /**
     * Common routine to do the JNDI lookup on the Home interface object
     */ 
    private Object getEJBHome(SOAPService serviceHandler,
                              MessageContext msgContext,
                              String beanJndiName)
        throws AxisFault
    {
        Object ejbHome = null;
        
        // Set up an InitialContext and use it get the beanJndiName from JNDI
        try {
            Properties properties = null;

            // collect all the properties we need to access JNDI:
            // username, password, factoryclass, contextUrl

            // username
            String username = (String)getStrOption(jndiUsername, serviceHandler);
            if ((username == null) && (msgContext != null))
               username = msgContext.getUsername();
            if (username != null) {
                if (properties == null)
                    properties = new Properties();
                properties.setProperty(Context.SECURITY_PRINCIPAL, username);
            }

            // password
            String password = (String)getStrOption(jndiPassword, serviceHandler);
            if ((password == null) && (msgContext != null))
                password = msgContext.getPassword();
            if (password != null) {
                if (properties == null)
                    properties = new Properties();
                properties.setProperty(Context.SECURITY_CREDENTIALS, password);
            }

            // factory class
            String factoryClass = (String)getStrOption(jndiContextClass, serviceHandler);
            if (factoryClass != null) {
                if (properties == null)
                    properties = new Properties();
                properties.setProperty(Context.INITIAL_CONTEXT_FACTORY, factoryClass);
            }

            // contextUrl
            String contextUrl = (String)getStrOption(jndiURL, serviceHandler);
            if (contextUrl != null) {
                if (properties == null)
                    properties = new Properties();
                properties.setProperty(Context.PROVIDER_URL, contextUrl);
            }

            // get context using these properties 
            InitialContext context = getContext(properties);

            // if we didn't get a context, fail
            if (context == null)
                throw new AxisFault( Messages.getMessage("cannotCreateInitialContext00"));
            
            ejbHome = getEJBHome(context, beanJndiName);

            if (ejbHome == null)
                throw new AxisFault( Messages.getMessage("cannotFindJNDIHome00",beanJndiName));
        }
        // Should probably catch javax.naming.NameNotFoundException here 
        catch (Exception exception) {
            entLog.info(Messages.getMessage("toAxisFault00"), exception);
            throw AxisFault.makeFault(exception);
        }

        return ejbHome;
    }
    
    protected InitialContext getCachedContext()
        throws javax.naming.NamingException
    {
        if (cached_context == null)
            cached_context = new InitialContext();
        return cached_context;
    }
        

    protected InitialContext getContext(Properties properties)
        throws AxisFault, javax.naming.NamingException
    {
        // if we got any stuff from the configuration file
        // create a new context using these properties 
        // otherwise, we get a default context and cache it for next time
        return ((properties == null)
                ? getCachedContext()
                : new InitialContext(properties));
    }

    protected Object getEJBHome(InitialContext context, String beanJndiName)
        throws AxisFault, javax.naming.NamingException
    {
        // Do the JNDI lookup
        return context.lookup(beanJndiName);
    }

}
