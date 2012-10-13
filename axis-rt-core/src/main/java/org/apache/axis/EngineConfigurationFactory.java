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

package org.apache.axis;

/**
 * EngineConfigurationFactory is an interface used to construct
 * concrete EngineConfiguration instances.
 * 
 * Each EngineConfigurationFactory must also (directly) implement
 * the following static method:
 *
 *     //Creates and returns a new EngineConfigurationFactory.
 *     //If a factory cannot be created, return 'null'.
 *     //
 *     //The factory may return non-NULL only if:
 *     //  - it knows what to do with the param (check type & process value)
 *     //  - it can find it's configuration information
 *     //
 *     //@see org.apache.axis.configuration.EngineConfigurationFactoryFinder
 * 
 *     public static EngineConfigurationFactory newFactory(Object param);
 *
 * This is checked at runtime and a warning generated for
 * factories found that do NOT implement this.
 *
 * @author Richard A. Sitze
 * @author Glyn Normington (glyn@apache.org)
 */
public interface EngineConfigurationFactory {
    /**
     * Property name used for setting an EngineConfiguration to be used
     * in creating engines.
     */
    public static final String SYSTEM_PROPERTY_NAME = "axis.EngineConfigFactory";
    
     /**
      * Get a default client engine configuration.
      *
      * @return a client EngineConfiguration
      */
    public EngineConfiguration getClientEngineConfig();

    /**
     * Get a default server engine configuration.
     *
     * @return a server EngineConfiguration
     */
    public EngineConfiguration getServerEngineConfig();
}
