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
import org.apache.commons.logging.Log;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;

/**
 * A basic RMI Provider
 *
 * @author Davanum Srinivas (dims@yahoo.com)
 */
public class RMIProvider extends RPCProvider {
    protected static Log log =
            LogFactory.getLog(RMIProvider.class.getName());

    // The enterprise category is for stuff that an enterprise product might
    // want to track, but in a simple environment (like the AXIS build) would
    // be nothing more than a nuisance.
    protected static Log entLog =
            LogFactory.getLog(Constants.ENTERPRISE_LOG_CATEGORY);

    public static final String OPTION_NAMING_LOOKUP = "NamingLookup";
    public static final String OPTION_INTERFACE_CLASSNAME = "InterfaceClassName";

    /**
     * Return a object which implements the service.
     * 
     * @param msgContext the message context
     * @param clsName The JNDI name of the EJB home class
     * @return an object that implements the service
     */
    protected Object makeNewServiceObject(MessageContext msgContext,
                                          String clsName)
            throws Exception {
        // Read deployment descriptor options
        String namingLookup = getStrOption(OPTION_NAMING_LOOKUP, msgContext.getService());
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
        Object targetObject = Naming.lookup(namingLookup);
        return targetObject;
    }

    /**
     * Return the option in the configuration that contains the service class
     * name.  
     */
    protected String getServiceClassNameOptionName() {
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
    protected String getStrOption(String optionName, Handler service) {
        String value = null;
        if (service != null)
            value = (String) service.getOption(optionName);
        if (value == null)
            value = (String) getOption(optionName);
        return value;
    }
}
