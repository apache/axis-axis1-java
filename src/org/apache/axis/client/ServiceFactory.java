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
import org.apache.axis.AxisFault;
import org.apache.axis.configuration.DefaultEngineConfigurationFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Map;

/**
 * Helper class for obtaining Services from JNDI.
 *
 * !!! WORK IN PROGRESS
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
 */ 

public class ServiceFactory {
    private static EngineConfiguration defaultEngineConfig =
        (new DefaultEngineConfigurationFactory()).getClientEngineConfig();
    private static ThreadLocal threadDefaultConfig = new ThreadLocal();

    public static void setThreadDefaultConfig(EngineConfiguration config)
    {
        threadDefaultConfig.set(config);
    }

    /**
     * Obtain an AxisClient reference, using JNDI if possible, otherwise
     * creating one using the standard Axis configuration pattern.  If we
     * end up creating one and do have JNDI access, bind it to the passed
     * name so we find it next time.
     *
     * @param name the JNDI name we're interested in
     * @param engineConfig a EngineConfiguration which should be used
     *                     to configure any engine we end up creating, or
     *                     null to use the default configuration pattern.
     */
    static public Service getService(Map environment)
    {
        Service service = null;
        InitialContext context = null;

        EngineConfiguration configProvider = (EngineConfiguration)environment.
            get(EngineConfiguration.PROPERTY_NAME);
        if (configProvider == null)
            configProvider = (EngineConfiguration)threadDefaultConfig.get();

        if (configProvider == null)
            configProvider = defaultEngineConfig;

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
}
