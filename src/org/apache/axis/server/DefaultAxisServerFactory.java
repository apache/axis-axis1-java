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
 *    Apache Software Foundation (http://www.apache.org/)."
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
 * @author Glen Daniels (gdaniels@macromedia.com)
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
