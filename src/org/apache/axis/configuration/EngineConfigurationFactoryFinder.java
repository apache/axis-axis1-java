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
import org.apache.axis.EngineConfigurationFactory;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Messages;
import org.apache.commons.discovery.ResourceClassIterator;
import org.apache.commons.discovery.tools.ClassUtils;
import org.apache.commons.logging.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;


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
 */
public class EngineConfigurationFactoryFinder
{
    protected static Log log =
        LogFactory.getLog(EngineConfigurationFactoryFinder.class.getName());

    private static final Class mySpi = EngineConfigurationFactory.class;

    private static final Class[] newFactoryParamTypes =
        new Class[] { Object.class };

    private static final String requiredMethod =
        "public static EngineConfigurationFactory newFactory(Object)";

    static {
        AxisProperties.setClassOverrideProperty(
                EngineConfigurationFactory.class,
                EngineConfigurationFactory.SYSTEM_PROPERTY_NAME);

        AxisProperties.setClassDefaults(EngineConfigurationFactory.class,
                            new String[] {
                                "org.apache.axis.configuration.EngineConfigurationFactoryServlet",
                                "org.apache.axis.configuration.EngineConfigurationFactoryDefault",
                                });
    }

    private EngineConfigurationFactoryFinder() {
    }


    /**
     * Create the default engine configuration and detect whether the user
     * has overridden this with their own.
     *
     * The discovery mechanism will use the following logic:
     *
     * - discover all available EngineConfigurationFactories
     *   - find all META-INF/services/org.apache.axis.EngineConfigurationFactory
     *     files available through class loaders.
     *   - read files (see Discovery) to obtain implementation(s) of that
     *     interface
     * - For each impl, call 'newFactory(Object param)'
     * - Each impl should examine the 'param' and return a new factory ONLY
     *   - if it knows what to do with it
     *     (i.e. it knows what to do with the 'real' type)
     *   - it can find it's configuration information
     * - Return first non-null factory found.
     * - Try EngineConfigurationFactoryServlet.newFactory(obj)
     * - Try EngineConfigurationFactoryDefault.newFactory(obj)
     * - If zero found (all return null), throw exception
     *
     * ***
     * This needs more work: System.properties, etc.
     * Discovery will have more tools to help with that
     * (in the manner of use below) in the near future.
     * ***
     *
     */
    public static EngineConfigurationFactory newFactory(final Object obj) {
        /**
         * recreate on each call is critical to gaining
         * the right class loaders.  Do not cache.
         */
        final Object[] params = new Object[] { obj };

        /**
         * Find and examine each service
         */
        return (EngineConfigurationFactory)AccessController.doPrivileged(
                new PrivilegedAction() {
                    public Object run() {
                        ResourceClassIterator services = AxisProperties.getResourceClassIterator(mySpi);

                        EngineConfigurationFactory factory = null;

                        while (factory == null  &&  services.hasNext()) {
                            Class service = services.nextResourceClass().loadClass();
                
                            /* service == null
                             * if class resource wasn't loadable
                             */
                            if (service != null) {
                                factory = newFactory(service, newFactoryParamTypes, params);
                            }
                        }
                
                        if (factory != null) {
                            if(log.isDebugEnabled()) {
                                log.debug(Messages.getMessage("engineFactory", factory.getClass().getName()));
                            }
                        } else {
                            log.error(Messages.getMessage("engineConfigFactoryMissing"));
                            // we should be throwing an exception here,
                            //
                            // but again, requires more refactoring than we want to swallow
                            // at this point in time.  Ifthis DOES occur, it's a coding error:
                            // factory should NEVER be null.
                            // Testing will find this, as NullPointerExceptions will be generated
                            // elsewhere.
                        }
                
                        return factory;
                    }
                });
    }

    public static EngineConfigurationFactory newFactory() {
        return newFactory(null);
    }

    private static EngineConfigurationFactory newFactory(Class service,
                                                         Class[] paramTypes,
                                                         Object[] param) {
        /**
         * Some JDK's may link on method resolution (findPublicStaticMethod)
         * and others on method call (method.invoke).
         * 
         * Either way, catch class load/resolve problems and return null.
         */
        
        try {
            /**
             * Verify that service implements:
             *  public static EngineConfigurationFactory newFactory(Object);
             */
            Method method = ClassUtils.findPublicStaticMethod(service,
                                                  EngineConfigurationFactory.class,
                                                  "newFactory",
                                                  paramTypes);
    
            if (method == null) {
                log.warn(Messages.getMessage("engineConfigMissingNewFactory",
                                              service.getName(),
                                              requiredMethod));
            } else {
                try {
                    return (EngineConfigurationFactory)method.invoke(null, param);
                } catch (InvocationTargetException e) {
                    if (e.getTargetException() instanceof NoClassDefFoundError) {
                        log.debug(Messages.getMessage("engineConfigLoadFactory",
                                                      service.getName()));
                    } else {
                        log.warn(Messages.getMessage("engineConfigInvokeNewFactory",
                                                      service.getName(),
                                                      requiredMethod), e);
                    }
                } catch (Exception e) {
                    log.warn(Messages.getMessage("engineConfigInvokeNewFactory",
                                                  service.getName(),
                                                  requiredMethod), e);
                }
            }
        } catch (NoClassDefFoundError e) {
            log.debug(Messages.getMessage("engineConfigLoadFactory",
                                          service.getName()));
        }

        return null;
    }
}
