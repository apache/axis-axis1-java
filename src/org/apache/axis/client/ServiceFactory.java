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

package org.apache.axis.client;

import org.apache.axis.EngineConfiguration;

import org.apache.axis.configuration.EngineConfigurationFactoryFinder;
import org.apache.axis.utils.ClassUtils;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;

import javax.naming.spi.ObjectFactory;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;


import java.lang.reflect.Constructor;

import java.net.URL;

import java.util.Hashtable;
import java.util.Map;

/**
 * Helper class for obtaining Services from JNDI.
 *
 * !!! WORK IN PROGRESS
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
 */ 

public class ServiceFactory extends javax.xml.rpc.ServiceFactory
        implements ObjectFactory
{
    // Constants for RefAddrs in the Reference.
    public static final String SERVICE_CLASSNAME  = "service classname";
    public static final String WSDL_LOCATION      = "WSDL location";
    public static final String MAINTAIN_SESSION   = "maintain session";
    public static final String SERVICE_NAMESPACE  = "service namespace";
    public static final String SERVICE_LOCAL_PART = "service local part";

    private static EngineConfiguration _defaultEngineConfig = null;

    private static ThreadLocal threadDefaultConfig = new ThreadLocal();

    public static void setThreadDefaultConfig(EngineConfiguration config)
    {
        threadDefaultConfig.set(config);
    }
    
    private static EngineConfiguration getDefaultEngineConfig() {
        if (_defaultEngineConfig == null) {
            _defaultEngineConfig =
                EngineConfigurationFactoryFinder.newFactory().getClientEngineConfig();
        }
        return _defaultEngineConfig;
    }

    /**
     * Obtain an AxisClient reference, using JNDI if possible, otherwise
     * creating one using the standard Axis configuration pattern.  If we
     * end up creating one and do have JNDI access, bind it to the passed
     * name so we find it next time.
     *
     * @param environment
     */
    static public Service getService(Map environment)
    {
        Service service = null;
        InitialContext context = null;

        EngineConfiguration configProvider =
            (EngineConfiguration)environment.get(EngineConfiguration.PROPERTY_NAME);

        if (configProvider == null)
            configProvider = (EngineConfiguration)threadDefaultConfig.get();

        if (configProvider == null)
            configProvider = getDefaultEngineConfig();

        // First check to see if JNDI works
        // !!! Might we need to set up context parameters here?
        try {
            context = new InitialContext();
        } catch (NamingException e) {
        }
        
        if (context != null) {
            String name = (String)environment.get("jndiName");
            if (name == null) {
                name = "axisServiceName";
            }

            // We've got JNDI, so try to find an AxisClient at the
            // specified name.
            try {
                service = (Service)context.lookup(name);
            } catch (NamingException e) {
                service = new Service(configProvider);
                try {
                    context.bind(name, service);
                } catch (NamingException e1) {
                    // !!! Couldn't do it, what should we do here?
                }
            }
        } else {
            service = new Service(configProvider);
        }

        return service;
    }

    public Object getObjectInstance(Object refObject, Name name,
            Context nameCtx, Hashtable environment) throws Exception
    {
        Object instance = null;
        if (refObject instanceof Reference) {
            Reference ref = (Reference) refObject;

            RefAddr addr = ref.get(SERVICE_CLASSNAME);
            Object obj = null;
            // If an explicit service classname is provided, then this is a
            // generated Service class.  Just use its default constructor.
            if (addr != null && (obj = addr.getContent()) instanceof String) {
                instance = ClassUtils.forName((String) obj).newInstance();
            }
            // else this is an instance of the Service class, so grab the
            // reference data...
            else {
                // Get the WSDL location...
                addr = ref.get(WSDL_LOCATION);
                if (addr != null && (obj = addr.getContent()) instanceof String) {
                    URL wsdlLocation = new URL((String) obj);

                    // Build the service qname...
                    addr = ref.get(SERVICE_NAMESPACE);
                    if (addr != null
                        && (obj = addr.getContent()) instanceof String) {
                        String namespace = (String) obj;
                        addr = ref.get(SERVICE_LOCAL_PART);
                        if (addr != null
                            && (obj = addr.getContent()) instanceof String) {
                            String localPart = (String) obj;
                            QName serviceName = new QName(namespace, localPart);

                            // Construct an instance of the service
                            Class[] formalArgs = new Class[]
                                    {URL.class, QName.class};
                            Object[] actualArgs = new Object[]
                                    {wsdlLocation, serviceName};
                            Constructor ctor =
                                    Service.class.getDeclaredConstructor(
                                    formalArgs);
                            instance = ctor.newInstance(actualArgs);
                        }
                    }
                }
            }
            // If maintainSession should be set to true, there will be an
            // addr for it.
            addr = ref.get(MAINTAIN_SESSION);
            if (addr != null && instance instanceof Service) {
                ((Service) instance).setMaintainSession(true);
            }
        }
        return instance;
    } // getObjectInstance

    /**
     *  Create a Service instance.
     *  @param   wsdlDocumentLocation URL for the WSDL document location
                              for the service
     *  @param   serviceName  QName for the service.
     *  @return  Service.
     *  @throws  ServiceException If any error in creation of the specified service
     */
    public javax.xml.rpc.Service createService(URL wsdlDocumentLocation,
            QName serviceName) throws ServiceException {
        return new Service(wsdlDocumentLocation, serviceName);
    } // createService

    /**
     * Create a Service instance.  Since the WSDL file is not provided
     * here, the Service object returned is quite simpleminded.
     * Likewise, the Call object that service.createCall will return
     * will also be simpleminded.  The caller must explicitly fill in
     * all the info on the Call object (ie., endpoint address, etc.).
     *
     *  @param   serviceName QName for the service
     *  @return  Service.
     *  @throws  ServiceException If any error in creation of the specified service
     */
    public javax.xml.rpc.Service createService(QName serviceName)
            throws ServiceException {
        return new Service(serviceName);
    } // createService
}
