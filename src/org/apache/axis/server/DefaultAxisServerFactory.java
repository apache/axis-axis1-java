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

package org.apache.axis.server;

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.AxisProperties;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

import java.io.File;
import java.util.Map;

/**
 * Helper class for obtaining AxisServers.  Default implementation.
 * 
 * @author Glen Daniels (gdaniels@apache.org)
 */

public class DefaultAxisServerFactory implements AxisServerFactory {
    protected static Log log =
        LogFactory.getLog(DefaultAxisServerFactory.class.getName());

    /**
     * Get an AxisServer.
     * <p>
     * Factory obtains EngineConfiguration as first found of the following:
     * a) EngineConfiguration instance, keyed to
     *    EngineConfiguration.PROPERTY_NAME in 'environment', or
     * b) EngineConfiguration class name, keyed to
     *    AxisEngine.PROP_DEFAULT_CONFIG_CLASS in AxisProperties.
     *    Class is instantiated if found.
     * <p>
     * If an EngineConfiguration cannot be located, the default
     * AxisServer constructor is used.
     * <p>
     * The AxisServer's option AxisEngine.PROP_ATTACHMENT_DIR is set to
     * the (first found) value of either AxisEngine.ENV_ATTACHMENT_DIR
     * or AxisEngine.ENV_SERVLET_REALPATH.
     *
     * @param environment The following keys are used:
     *        AxisEngine.ENV_ATTACHMENT_DIR
     *                   - Set as default value for Axis option
     *                     AxisEngine.PROP_ATTACHMENT_DIR
     *        AxisEngine.ENV_SERVLET_REALPATH
     *                   - Set as alternate default value for Axis option
     *                     AxisEngine.PROP_ATTACHMENT_DIR
     *        EngineConfiguration.PROPERTY_NAME
     *                   - Instance of EngineConfiguration,
     *                     if not set then an attempt is made to retreive
     *                     a class name from AxisEngine.PROP_CONFIG_CLASS
     */
    public AxisServer getServer(Map environment) throws AxisFault {
        log.debug("Enter: DefaultAxisServerFactory::getServer");

        AxisServer ret = createServer(environment);

        if (ret != null) {
            if (environment != null) {
                ret.setOptionDefault(AxisEngine.PROP_ATTACHMENT_DIR,
                    (String)environment.get(AxisEngine.ENV_ATTACHMENT_DIR));

                ret.setOptionDefault(AxisEngine.PROP_ATTACHMENT_DIR,
                    (String)environment.get(AxisEngine.ENV_SERVLET_REALPATH));
            }

            String attachmentsdir = (String)ret.getOption(AxisEngine.PROP_ATTACHMENT_DIR);

            if (attachmentsdir != null) {
                File attdirFile = new File(attachmentsdir);
                if (!attdirFile.isDirectory()) {
                    attdirFile.mkdirs();
                }
            }
        }

        log.debug("Exit: DefaultAxisServerFactory::getServer");

        return ret;
    }

    /**
     * Do the actual work of creating a new AxisServer, using the
     * configuration, or using the default constructor if null is passed.
     *
     * @return a shiny new AxisServer, ready for use.
     */
    private static AxisServer createServer(Map environment) {
        EngineConfiguration config = getEngineConfiguration(environment);

        // Return new AxisServer using the appropriate config
        return (config == null) ? new AxisServer() : new AxisServer(config);
    }

    /**
     * Look for EngineConfiguration, it is first of:
     * a) EngineConfiguration instance, keyed to
     *    EngineConfiguration.PROPERTY_NAME in 'environment', or
     * b) EngineConfiguration class name, keyed to
     *    AxisEngine.PROP_DEFAULT_CONFIG_CLASS in AxisProperties.
     *    Class is instantiated if found.
     */
    private static EngineConfiguration getEngineConfiguration(Map environment)
    {
        log.debug("Enter: DefaultAxisServerFactory::getEngineConfiguration");

        EngineConfiguration config = null;

        if (environment != null) {
            try {
                config = (EngineConfiguration)environment.get(EngineConfiguration.PROPERTY_NAME);
            } catch (ClassCastException e) {
                log.warn(Messages.getMessage("engineConfigWrongClass00"), e);
                // Fall through
            }
        }

        if (config == null) {
            // A default engine configuration class may be set in a system
            // property. If so, try creating an engine configuration.
            String configClass = AxisProperties.getProperty(AxisEngine.PROP_DEFAULT_CONFIG_CLASS);
            if (configClass != null) {
                try {
                    // Got one - so try to make it (which means it had better have
                    // a default constructor - may make it possible later to pass
                    // in some kind of environmental parameters...)
                    Class cls = ClassUtils.forName(configClass);
                    config = (EngineConfiguration)cls.newInstance();
                } catch (ClassNotFoundException e) {
                    log.warn(Messages.getMessage("engineConfigNoClass00", configClass), e);
                    // Fall through
                } catch (InstantiationException e) {
                    log.warn(Messages.getMessage("engineConfigNoInstance00", configClass), e);
                    // Fall through
                } catch (IllegalAccessException e) {
                    log.warn(Messages.getMessage("engineConfigIllegalAccess00", configClass), e);
                    // Fall through
                } catch (ClassCastException e) {
                    log.warn(Messages.getMessage("engineConfigWrongClass01", configClass), e);
                    // Fall through
                }
            }
        }

        log.debug("Exit: DefaultAxisServerFactory::getEngineConfiguration");

        return config;
    }
}
