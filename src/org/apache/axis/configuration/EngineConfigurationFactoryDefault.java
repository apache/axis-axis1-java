/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
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

package org.apache.axis.configuration;

import org.apache.axis.AxisProperties;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.EngineConfigurationFactory;
import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;


/**
 * This is a default implementation of EngineConfigurationFactory.
 * It is user-overrideable by a system property without affecting
 * the caller. If you decide to override it, use delegation if
 * you want to inherit the behaviour of this class as using
 * class extension will result in tight loops. That is, your
 * class should implement EngineConfigurationFactory and keep
 * an instance of this class in a member field and delegate
 * methods to that instance when the default behaviour is
 * required.
 *
 * @author Richard A. Sitze
 * @author Glyn Normington (glyn@apache.org)
 */
public class EngineConfigurationFactoryDefault
    implements EngineConfigurationFactory
{
    protected static Log log =
        LogFactory.getLog(EngineConfigurationFactoryDefault.class.getName());

    public static final String OPTION_CLIENT_CONFIG_FILE = "axis.ClientConfigFile";
    public static final String OPTION_SERVER_CONFIG_FILE = "axis.ServerConfigFile";

    protected static final String CLIENT_CONFIG_FILE = "client-config.wsdd";
    protected static final String SERVER_CONFIG_FILE = "server-config.wsdd";

    private String clientConfigFile;

    private String serverConfigFile;

    /**
     * Creates and returns a new EngineConfigurationFactory.
     * If a factory cannot be created, return 'null'.
     * 
     * The factory may return non-NULL only if:
     *   - it knows what to do with the param (param == null)
     *   - it can find it's configuration information
     * 
     * @see org.apache.axis.configuration.EngineConfigurationFactoryFinder
     */
    public static EngineConfigurationFactory newFactory(Object param) {
        if (param != null)
            return null;  // not for us.

        /**
         * Default, let this one go through.
         * 
         * The REAL reason we are not trying to make any
         * decision here is because it's impossible
         * (without refactoring FileProvider) to determine
         * if a *.wsdd file is present or not until the configuration
         * is bound to an engine.
         * 
         * FileProvider/EngineConfiguration pretend to be independent,
         * but they are tightly bound to an engine instance...
         */
        return new EngineConfigurationFactoryDefault();
    }

    /**
     * Create the default engine configuration and detect whether the user
     * has overridden this with their own.
     */
    protected EngineConfigurationFactoryDefault() {
        clientConfigFile =
            AxisProperties.getProperty(OPTION_CLIENT_CONFIG_FILE,
                                       CLIENT_CONFIG_FILE);

        serverConfigFile =
            AxisProperties.getProperty(OPTION_SERVER_CONFIG_FILE,
                                       SERVER_CONFIG_FILE);
    }

     /**
      * Get a default client engine configuration.
      *
      * @return a client EngineConfiguration
      */
    public EngineConfiguration getClientEngineConfig() {
        return new FileProvider(clientConfigFile);
    }

    /**
     * Get a default server engine configuration.
     *
     * @return a server EngineConfiguration
     */
    public EngineConfiguration getServerEngineConfig() {
        return new FileProvider(serverConfigFile);
    }
}
