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

package org.apache.axis.providers.java;

import org.apache.axis.Constants;
import org.apache.axis.Handler;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.ClassUtils;
import org.apache.commons.logging.Log;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;

import java.lang.reflect.Method;
import java.util.Properties;

/**
 * A basic CORBA Provider
 *
 * @author Davanum Srinivas (dims@yahoo.com)
 */
public class CORBAProvider extends RPCProvider
{
    protected static Log log =
        LogFactory.getLog(CORBAProvider.class.getName());

    private static final String DEFAULT_ORB_INITIAL_HOST = "localhost";
    private static final String DEFAULT_ORB_INITIAL_PORT = "900";
    
    // The enterprise category is for stuff that an enterprise product might
    // want to track, but in a simple environment (like the AXIS build) would
    // be nothing more than a nuisance.
    protected static Log entLog =
        LogFactory.getLog(Constants.ENTERPRISE_LOG_CATEGORY);

    public static final String OPTION_ORB_INITIAL_HOST = "ORBInitialHost";
    public static final String OPTION_ORB_INITIAL_PORT = "ORBInitialPort";
    public static final String OPTION_NAME_ID = "NameID";
    public static final String OPTION_NAME_KIND = "NameKind";
    public static final String OPTION_INTERFACE_CLASSNAME = "InterfaceClassName";
    public static final String OPTION_HELPER_CLASSNAME = "HelperClassName";

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
        // Read deployment descriptor options
        String orbInitialHost = getStrOption(OPTION_ORB_INITIAL_HOST,msgContext.getService()); 
        if (orbInitialHost == null)
          orbInitialHost = DEFAULT_ORB_INITIAL_HOST;
        String orbInitialPort = getStrOption(OPTION_ORB_INITIAL_PORT,msgContext.getService());
        if (orbInitialPort == null)
          orbInitialPort = DEFAULT_ORB_INITIAL_PORT;
        String nameId = getStrOption(OPTION_NAME_ID,msgContext.getService());
        String nameKind = getStrOption(OPTION_NAME_KIND,msgContext.getService());
        String helperClassName = getStrOption(OPTION_HELPER_CLASSNAME,msgContext.getService());

        // Initialize ORB
        Properties orbProps = new Properties();
        orbProps.put("org.omg.CORBA.ORBInitialHost", orbInitialHost);
        orbProps.put("org.omg.CORBA.ORBInitialPort", orbInitialPort);
        ORB orb = ORB.init(new String[0], orbProps);

        // Find the object
        NamingContext root = NamingContextHelper.narrow(orb.resolve_initial_references("NameService"));
        NameComponent nc = new NameComponent(nameId, nameKind);
        NameComponent[] ncs = {nc};
        org.omg.CORBA.Object corbaObject = root.resolve(ncs);

        Class helperClass = ClassUtils.forName(helperClassName);
        // Narrow the object reference
        Method narrowMethod = helperClass.getMethod("narrow", CORBA_OBJECT_CLASS);
        Object targetObject = narrowMethod.invoke(null, new Object[] {corbaObject});

        return targetObject;
    }

    private static final Class[] CORBA_OBJECT_CLASS = new Class[] {org.omg.CORBA.Object.class};

    /**
     * Return the option in the configuration that contains the service class
     * name.  
     */
    protected String getServiceClassNameOptionName()
    {
        return OPTION_INTERFACE_CLASSNAME;
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
 }
