/*
 * Copyright 2002-2004 The Apache Software Foundation.
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
