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
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.CosNaming.NameComponent;
import org.omg.CORBA.ORB;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.Hashtable;

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
        Method narrowMethod = helperClass.getMethod("narrow", new Class[] {org.omg.CORBA.Object.class});
        Object targetObject = narrowMethod.invoke(null, new Object[] {corbaObject});

        return targetObject;
    }

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
