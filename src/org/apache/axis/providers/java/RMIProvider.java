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
