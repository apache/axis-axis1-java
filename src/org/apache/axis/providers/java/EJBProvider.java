/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

import java.lang.reflect.Method;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Handler;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

/**
 * A basic EJB Provider
 *
 * @author Carl Woolf (cwoolf@macromedia.com)
 * @author Tom Jordahl (tomj@macromedia.com)
 * @author C?dric Chabanois (cchabanois@ifrance.com)
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
    public static final String OPTION_LOCALHOMEINTERFACENAME = "localHomeInterfaceName";
    public static final String OPTION_LOCALINTERFACENAME = "localInterfaceName";
    
    
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
        String remoteHomeName = getStrOption(OPTION_HOMEINTERFACENAME, 
                                                msgContext.getService());
        String localHomeName = getStrOption(OPTION_LOCALHOMEINTERFACENAME, 
                                                msgContext.getService());
        String homeName = (remoteHomeName != null ? remoteHomeName:localHomeName);

        if (homeName == null) {
            // cannot find both remote home and local home  
            throw new AxisFault(
                Messages.getMessage("noOption00", 
                                    OPTION_HOMEINTERFACENAME, 
                                    msgContext.getTargetService()));
        }
                                                        
        // Load the Home class name given in the config file
        Class homeClass = ClassUtils.forName(homeName, true, msgContext.getClassLoader());

        // we create either the ejb using either the RemoteHome or LocalHome object
        if (remoteHomeName != null)
            return createRemoteEJB(msgContext, clsName, homeClass);
        else 
            return createLocalEJB(msgContext, clsName, homeClass);
    }

    /**
     * Create an EJB using a remote home object
     * 
     * @param msgContext the message context
     * @param beanJndiName The JNDI name of the EJB remote home class
     * @param homeClass the class of the home interface
     * @return an EJB
     */
    private Object createRemoteEJB(MessageContext msgContext, 
                                    String beanJndiName,
                                    Class homeClass)
        throws Exception
    {
        // Get the EJB Home object from JNDI 
        Object ejbHome = getEJBHome(msgContext.getService(),
                                    msgContext, beanJndiName);
        Object ehome = javax.rmi.PortableRemoteObject.narrow(ejbHome, homeClass);

        // Invoke the create method of the ejbHome class without actually
        // touching any EJB classes (i.e. no cast to EJBHome)
        Method createMethod = homeClass.getMethod("create", empty_class_array);
        Object result = createMethod.invoke(ehome, empty_object_array);
        
        return result;        
    }

    /**
     * Create an EJB using a local home object
     * 
     * @param msgContext the message context
     * @param beanJndiName The JNDI name of the EJB local home class
     * @param homeClass the class of the home interface
     * @return an EJB
     */
    private Object createLocalEJB(MessageContext msgContext, 
                                   String beanJndiName,
                                   Class homeClass)
        throws Exception
    {
        // Get the EJB Home object from JNDI 
        Object ejbHome = getEJBHome(msgContext.getService(),
                                    msgContext, beanJndiName);

        // the home object is a local home object
        Object ehome;
        if (homeClass.isInstance(ejbHome))
          ehome = ejbHome;
        else
          throw new ClassCastException(
                  Messages.getMessage("badEjbHomeType"));
          
        // Invoke the create method of the ejbHome class without actually
        // touching any EJB classes (i.e. no cast to EJBLocalHome)
        Method createMethod = homeClass.getMethod("create", empty_class_array);
        Object result = createMethod.invoke(ehome, empty_object_array);
                               
        return result;
    }

    /**
     * Tells if the ejb that will be used to handle this service is a remote
     * one
     */
    private boolean isRemoteEjb(SOAPService service)
    {
        return getStrOption(OPTION_HOMEINTERFACENAME,service) != null; 
    }

    /**
     * Tells if the ejb that will be used to handle this service is a local
     * one
     */
    private boolean isLocalEjb(SOAPService service)
    {
        return (!isRemoteEjb(service)) && 
          (getStrOption(OPTION_LOCALHOMEINTERFACENAME,service) != null);
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
     * Get the remote interface of an ejb from its home class.
     * This function can only be used for remote ejbs
     * 
     * @param beanJndiName the jndi name of the ejb
     * @param service the soap service
     * @param msgContext the message context (can be null)
     */
    private Class getRemoteInterfaceClassFromHome(String beanJndiName,
                                                   SOAPService service,
                                                   MessageContext msgContext)
        throws Exception                                       
    {
        // Get the EJB Home object from JNDI
        Object ejbHome = getEJBHome(service, msgContext, beanJndiName);

        String homeName = getStrOption(OPTION_HOMEINTERFACENAME,
                                       service);
        if (homeName == null)
            throw new AxisFault(
                    Messages.getMessage("noOption00",
                                        OPTION_HOMEINTERFACENAME,
                                        service.getName()));

        // Load the Home class name given in the config file
        ClassLoader cl = (msgContext != null) ?
                msgContext.getClassLoader() :
                Thread.currentThread().getContextClassLoader();
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
        Object metaData = getEJBMetaData.invoke(ehome, empty_object_array);
        Method getRemoteInterfaceClass =
                metaData.getClass().getMethod("getRemoteInterfaceClass",
                                                  empty_class_array);
        return (Class) getRemoteInterfaceClass.invoke(metaData,
                                                       empty_object_array);
    }                                      

    
    /**
     * Get the class description for the EJB Remote or Local Interface, 
     * which is what we are interested in exposing to the world (i.e. in WSDL).
     * 
     * @param msgContext the message context (can be null)
     * @param beanJndiName the JNDI name of the EJB
     * @return the class info of the EJB remote or local interface
     */ 
    protected Class getServiceClass(String beanJndiName,
                                    SOAPService service,
                                    MessageContext msgContext)
        throws AxisFault
    {
        Class interfaceClass = null;
        
        try {
            // First try to get the interface class from the configuation
            // Note that we don't verify that remote remoteInterfaceName is used for
            // remote ejb and localInterfaceName for local ejb. Should we ?
            String remoteInterfaceName = 
                    getStrOption(OPTION_REMOTEINTERFACENAME, service);
            String localInterfaceName = 
                    getStrOption(OPTION_LOCALINTERFACENAME, service);
            String interfaceName = (remoteInterfaceName != null ? remoteInterfaceName : localInterfaceName);

            if(interfaceName != null){
                ClassLoader cl = (msgContext != null) ?
                        msgContext.getClassLoader() :
                        Thread.currentThread().getContextClassLoader();
                interfaceClass = ClassUtils.forName(interfaceName,
                                                    true,
                                                    cl);
            } 
            else
            {
                // cannot get the interface name from the configuration, we get
                // it from the EJB Home (if remote)
                if (isRemoteEjb(service)) {
                    interfaceClass = getRemoteInterfaceClassFromHome(beanJndiName,
                                                                     service,
                                                                     msgContext);
                }
                else 
                if (isLocalEjb(service)) {
                    // we cannot get the local interface from the local ejb home
                    // localInterfaceName is mandatory for local ejbs
                    throw new AxisFault(
                            Messages.getMessage("noOption00", 
                                                OPTION_LOCALINTERFACENAME, 
                                                service.getName()));
                }
                else
                {
                    // neither a local ejb or a remote one ...
                    throw new AxisFault(Messages.getMessage("noOption00", 
                                                OPTION_HOMEINTERFACENAME,
                                                service.getName()));
                }
            }
        } catch (Exception e) {
            throw AxisFault.makeFault(e);
        }

        // got it, return it
       return interfaceClass;
    }

    /**
     * Common routine to do the JNDI lookup on the Home interface object
     * username and password for jndi lookup are got from the configuration or from
     * the messageContext if not found in the configuration
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
            String username = getStrOption(jndiUsername, serviceHandler);
            if ((username == null) && (msgContext != null))
               username = msgContext.getUsername();
            if (username != null) {
                if (properties == null)
                    properties = new Properties();
                properties.setProperty(Context.SECURITY_PRINCIPAL, username);
            }

            // password
            String password = getStrOption(jndiPassword, serviceHandler);
            if ((password == null) && (msgContext != null))
                password = msgContext.getPassword();
            if (password != null) {
                if (properties == null)
                    properties = new Properties();
                properties.setProperty(Context.SECURITY_CREDENTIALS, password);
            }

            // factory class
            String factoryClass = getStrOption(jndiContextClass, serviceHandler);
            if (factoryClass != null) {
                if (properties == null)
                    properties = new Properties();
                properties.setProperty(Context.INITIAL_CONTEXT_FACTORY, factoryClass);
            }

            // contextUrl
            String contextUrl = getStrOption(jndiURL, serviceHandler);
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

    /**
     * Fill in a service description with the correct impl class
     * and typemapping set.  
     */
//    public void initServiceDesc(SOAPService service, MessageContext msgContext)
//            throws AxisFault
//    {
//        // the service class used to fill service description is the EJB Remote/Local Interface
//        // we add EJBObject and EJBLocalObject as stop classes because we
//        // don't want any of their methods in the wsdl ...
//        ServiceDesc serviceDescription = service.getServiceDescription();
//        ArrayList stopClasses = serviceDescription.getStopClasses();
//        if (stopClasses == null)
//            stopClasses = new ArrayList();              	
//        stopClasses.add("javax.ejb.EJBObject");
//        stopClasses.add("javax.ejb.EJBLocalObject");
//        serviceDescription.setStopClasses(stopClasses);
//        super.initServiceDesc(service,msgContext);
//    }

}
