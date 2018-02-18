/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.axis.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import org.apache.axis.AxisEngine;
import org.apache.axis.ConfigurationException;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDDocument;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;

/**
 * Configuration provider that loads the default Axis configuration. It first loads the
 * <tt>org/apache/axis/&lt;type&gt;/&lt;type&gt;-config.wsdd</tt> resource and then searches for resources
 * with name <tt>META-INF/axis/default-&lt;type&gt;-config.wsdd</tt>. All the discovered WSDD documents
 * are merged into a single configuration. <tt>&lt;type&gt;</tt> identifies the engine type for which
 * the configuration is to be built; it is either <tt>client</tt> or <tt>server</tt>.
 * <p>
 * This class looks up the resources using the thread context class loader, except if it determines
 * that the context class loader is not set correctly, in which case it falls back to the class
 * loader that loaded the {@link DefaultConfiguration} class. To determine if the context class
 * loader is set correctly, the code checks that the {@link DefaultConfiguration} class is visible
 * to the context class loader.
 * <p>
 * The algorithm implemented by this class is designed to support the modularized artifacts
 * introduced in Axis 1.4.1. It allows individual JARs to contribute items (transports, handlers,
 * etc.) to the default configuration. The naming convention for the base configuration file
 * (<tt>org/apache/axis/&lt;type&gt;/&lt;type&gt;-config.wsdd</tt>) was chosen for consistency with Axis
 * 1.4, while <tt>META-INF/axis/default-&lt;type&gt;-config.wsdd</tt> is new in Axis 1.4.1.
 * <p>
 * {@link DefaultConfiguration} is also used by {@link FileProvider} to build the configuration if
 * no existing configuration file is found.
 * 
 * @author Andreas Veithen
 */
public class DefaultConfiguration extends DelegatingWSDDEngineConfiguration {
    private static final Log log = LogFactory.getLog(DefaultConfiguration.class.getName());
    
    private final String type;
    private WSDDDeployment deployment;
    
    /**
     * Constructor.
     * 
     * @param type
     *            the engine type to load the default configuration for; this should be
     *            <code>client</code> or <code>server</code> (although any value is supported)
     */
    public DefaultConfiguration(String type) {
        this.type = type;
    }

    public void configureEngine(AxisEngine engine) throws ConfigurationException {
        ClassLoader classLoader;
        try {
            classLoader = Thread.currentThread().getContextClassLoader();
        } catch (SecurityException ex) {
            // We can only get a SecurityException if "the caller's class loader is not the same as
            // or an ancestor of the context class loader". In this case we are not interested in
            // the class loader anyway.
            classLoader = null;
        }
        if (classLoader != null) {
            // Check if we are visible to the thread context class loader. If this is not the case,
            // then the context class loader is likely not set correctly and we ignore it.
            try {
                classLoader.loadClass(DefaultConfiguration.class.getName());
            } catch (ClassNotFoundException ex) {
                log.debug(DefaultConfiguration.class.getName() + " not visible to thread context class loader");
                classLoader = null;
            }
        }
        if (classLoader == null) {
            log.debug("Not using thread context class loader");
            classLoader = DefaultConfiguration.class.getClassLoader();
        } else {
            log.debug("Using thread context class loader");
        }
        
        // Load the base configuration
        String resourceName = "org/apache/axis/" + type + "/" + type + "-config.wsdd";
        if (log.isDebugEnabled()) {
            log.debug("Loading resource " + resourceName);
        }
        InputStream in = classLoader.getResourceAsStream(resourceName);
        if (in == null) {
            throw new ConfigurationException("Resource " + resourceName + " not found");
        }
        try {
            try {
                deployment = new WSDDDocument(XMLUtils.newDocument(in)).getDeployment();
            } finally {
                in.close();
            }
        } catch (Exception ex) {
            // TODO: refactor ConfigurationException to support exception chaining
            throw new ConfigurationException(/*"Failed to process resource " + baseConfigResource,*/ ex);
        }
        
        // Discover and load additional default configuration fragments
        resourceName = "META-INF/axis/default-" + type + "-config.wsdd";
        Enumeration resources;
        try {
            resources = classLoader.getResources(resourceName);
        } catch (IOException ex) {
            // TODO: refactor ConfigurationException to support exception chaining
            throw new ConfigurationException(/*"Failed to discover resources with name " + resourceName,*/ ex);
        }
        while (resources.hasMoreElements()) {
            URL url = (URL)resources.nextElement();
            if (log.isDebugEnabled()) {
                log.debug("Loading " + url);
            }
            try {
                in = url.openStream();
                try {
                    new WSDDDocument(XMLUtils.newDocument(in)).deploy(deployment);
                } finally {
                    in.close();
                }
            } catch (Exception ex) {
                // TODO: refactor ConfigurationException to support exception chaining
                throw new ConfigurationException(/*"Failed to process " + url,*/ ex);
            }
        }
        deployment.configureEngine(engine);
    }

    public WSDDDeployment getDeployment() {
        return deployment;
    }

    public void writeEngineConfig(AxisEngine engine) throws ConfigurationException {
        // Default configuration is read-only
    }
}
