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

package org.apache.axis;

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Messages;
import org.apache.commons.discovery.ResourceClassIterator;
import org.apache.commons.discovery.ResourceNameDiscover;
import org.apache.commons.discovery.ResourceNameIterator;
import org.apache.commons.discovery.resource.ClassLoaders;
import org.apache.commons.discovery.resource.classes.DiscoverClasses;
import org.apache.commons.discovery.resource.names.DiscoverMappedNames;
import org.apache.commons.discovery.resource.names.DiscoverNamesInAlternateManagedProperties;
import org.apache.commons.discovery.resource.names.DiscoverNamesInManagedProperties;
import org.apache.commons.discovery.resource.names.DiscoverServiceNames;
import org.apache.commons.discovery.resource.names.NameDiscoverers;
import org.apache.commons.discovery.tools.ClassUtils;
import org.apache.commons.discovery.tools.DefaultClassHolder;
import org.apache.commons.discovery.tools.DiscoverClass;
import org.apache.commons.discovery.tools.ManagedProperties;
import org.apache.commons.discovery.tools.PropertiesHolder;
import org.apache.commons.discovery.tools.SPInterface;
import org.apache.commons.logging.Log;

import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;


/**
 * <p>Configuration properties for AXIS.
 * </p>
 *
 * <p>Manage configuration properties according to a secure
 * scheme similar to that used by classloaders:
 * <ul>
 *   <li><code>ClassLoader</code>s are organized in a tree hierarchy.</li>
 *   <li>each <code>ClassLoader</code> has a reference
 *       to a parent <code>ClassLoader</code>.</li>
 *   <li>the root of the tree is the bootstrap <code>ClassLoader</code>er.</li>
 *   <li>the youngest decendent is the thread context class loader.</li>
 *   <li>properties are bound to a <code>ClassLoader</code> instance
 *   <ul>
 *     <li><i>non-default</i> properties bound to a parent <code>ClassLoader</code>
 *         instance take precedence over all properties of the same name bound
 *         to any decendent.
 *         Just to confuse the issue, this is the default case.</li>
 *     <li><i>default</i> properties bound to a parent <code>ClassLoader</code>
 *         instance may be overriden by (default or non-default) properties of
 *         the same name bound to any decendent.
 *         </li>
 *   </ul>
 *   </li>
 *   <li>System properties take precedence over all other properties</li>
 * </ul>
 * </p>
 *
 * @author Richard A. Sitze
 */
public class AxisProperties {
    /** The <code>Log</code> for all interesting events in this class. */
    protected static Log log =
        LogFactory.getLog(AxisProperties.class.getName());

    private static DiscoverNamesInAlternateManagedProperties altNameDiscoverer;
    private static DiscoverMappedNames mappedNames;
    private static NameDiscoverers nameDiscoverer;
    private static ClassLoaders loaders;

    public static void setClassOverrideProperty(Class clazz, String propertyName) {
        getAlternatePropertyNameDiscoverer()
            .addClassToPropertyNameMapping(clazz.getName(), propertyName);
    }

    public static void setClassDefault(Class clazz, String defaultName) {
        getMappedNames().map(clazz.getName(), defaultName);
    }

    public static void setClassDefaults(Class clazz, String[] defaultNames) {
        getMappedNames().map(clazz.getName(), defaultNames);
    }

    public static synchronized ResourceNameDiscover getNameDiscoverer() {
        if (nameDiscoverer == null) {
            nameDiscoverer = new NameDiscoverers();
            nameDiscoverer.addResourceNameDiscover(getAlternatePropertyNameDiscoverer());
            nameDiscoverer.addResourceNameDiscover(new DiscoverNamesInManagedProperties());
            nameDiscoverer.addResourceNameDiscover(new DiscoverServiceNames(getClassLoaders()));
            nameDiscoverer.addResourceNameDiscover(getMappedNames());
        }
        return nameDiscoverer;
    }

    public static ResourceClassIterator getResourceClassIterator(Class spi) {
        ResourceNameIterator it = getNameDiscoverer().findResourceNames(spi.getName());
        return new DiscoverClasses(loaders).findResourceClasses(it);
    }

    private static ClassLoaders getClassLoaders() {
        if (loaders == null) {
            loaders = ClassLoaders.getAppLoaders(AxisProperties.class, null, true);
        }
        return loaders;
    }

    private static DiscoverMappedNames getMappedNames() {
        if (mappedNames == null) {
            mappedNames = new DiscoverMappedNames();
        }
        return mappedNames;
    }

    private static DiscoverNamesInAlternateManagedProperties getAlternatePropertyNameDiscoverer() {
        if (altNameDiscoverer == null) {
            altNameDiscoverer = new DiscoverNamesInAlternateManagedProperties();
        }

        return altNameDiscoverer;
    }

    /**
     * Create a new instance of a service provider class.
     *
     * !WARNING!
     * SECURITY issue.
     *
     * See bug 11874
     *
     * The solution to both is to move doPrivilege UP within AXIS to a
     * class that is either private (cannot be reached by code outside
     * AXIS) or that represents a secure public interface...
     *
     * This is going to require analysis and (probably) rearchitecting.
     * So, I'm taking taking the easy way out until we are at a point
     * where we can reasonably rearchitect for security.
     *
     * @param spiClass  the service provider class to instantiate
     * @return a new instance of this class
     */

    public static Object newInstance(Class spiClass)
    {
        return newInstance(spiClass, null, null);
    }

    public static Object newInstance(final Class spiClass,
                                     final Class constructorParamTypes[],
                                     final Object constructorParams[]) {
        return AccessController.doPrivileged(
            new PrivilegedAction() {
                public Object run() {
                    ResourceClassIterator services = getResourceClassIterator(spiClass);

                    Object obj = null;
                    while (obj == null  &&  services.hasNext()) {
                        Class service = services.nextResourceClass().loadClass();

                        /* service == null
                         * if class resource wasn't loadable
                         */
                        if (service != null) {
                            /* OK, class loaded.. attempt to instantiate it.
                             */
                            try {
                                ClassUtils.verifyAncestory(spiClass, service);
                                obj = ClassUtils.newInstance(service, constructorParamTypes, constructorParams);
                            } catch (InvocationTargetException e) {
                                if (e.getTargetException() instanceof java.lang.NoClassDefFoundError) {
                                    log.debug(Messages.getMessage("exception00"), e);
                                } else {
                                    log.warn(Messages.getMessage("exception00"), e);
                                }
                            } catch (Exception e) {
                                log.warn(Messages.getMessage("exception00"), e);
                            }
                        }
                    }

                    return obj;
                }
            });
    }


    /**
     * Get value for property bound to the current thread context class loader.
     *
     * @param propertyName property name.
     * @return property value if found, otherwise default.
     */
    public static String getProperty(String propertyName) {
        return ManagedProperties.getProperty(propertyName);
    }

    /**
     * Get value for property bound to the current thread context class loader.
     * If not found, then return default.
     *
     * @param propertyName property name.
     * @param dephault default value.
     * @return property value if found, otherwise default.
     */
    public static String getProperty(String propertyName, String dephault) {
        return ManagedProperties.getProperty(propertyName, dephault);
    }

    /**
     * Set value for property bound to the current thread context class loader.
     * @param propertyName property name
     * @param value property value (non-default)  If null, remove the property.
     */
    public static void setProperty(String propertyName, String value) {
        ManagedProperties.setProperty(propertyName, value);
    }

    /**
     * Set value for property bound to the current thread context class loader.
     * @param propertyName property name
     * @param value property value.  If null, remove the property.
     * @param isDefault determines if property is default or not.
     *        A non-default property cannot be overriden.
     *        A default property can be overriden by a property
     *        (default or non-default) of the same name bound to
     *        a decendent class loader.
     */
    public static void setProperty(String propertyName, String value, boolean isDefault) {
        ManagedProperties.setProperty(propertyName, value, isDefault);
    }

    /**
     * Set property values for <code>Properties</code> bound to the
     * current thread context class loader.
     *
     * @param newProperties name/value pairs to be bound
     */
    public static void setProperties(Map newProperties) {
        ManagedProperties.setProperties(newProperties);
    }


    /**
     * Set property values for <code>Properties</code> bound to the
     * current thread context class loader.
     *
     * @param newProperties name/value pairs to be bound
     * @param isDefault determines if properties are default or not.
     *        A non-default property cannot be overriden.
     *        A default property can be overriden by a property
     *        (default or non-default) of the same name bound to
     *        a decendent class loader.
     */
    public static void setProperties(Map newProperties, boolean isDefault) {
        ManagedProperties.setProperties(newProperties, isDefault);
    }


    public static Enumeration propertyNames() {
        return ManagedProperties.propertyNames();
    }

    /**
     * This is an expensive operation.
     *
     * @return Returns a <code>java.util.Properties</code> instance
     * that is equivalent to the current state of the scoped
     * properties, in that getProperty() will return the same value.
     * However, this is a copy, so setProperty on the
     * returned value will not effect the scoped properties.
     */
    public static Properties getProperties() {
        return ManagedProperties.getProperties();
    }


    public static Object newInstance(Class spiClass, Class defaultClass)
    {
        return newInstance(new SPInterface(spiClass), new DefaultClassHolder(defaultClass));
    }

    /**
     * !WARNING!
     * SECURITY issue.
     *
     * See bug 11874
     *
     * The solution to both is to move doPrivilege UP within AXIS to a
     * class that is either private (cannot be reached by code outside
     * AXIS) or that represents a secure public interface...
     *
     * This is going to require analysis and (probably) rearchitecting.
     * So, I'm taking taking the easy way out until we are at a point
     * where we can reasonably rearchitect for security.
     */
    private static Object newInstance(final SPInterface spi,
                                      final DefaultClassHolder defaultClass)
    {
        return AccessController.doPrivileged(
            new PrivilegedAction() {
                public Object run() {
                    try {
                        return DiscoverClass.newInstance(null,
                                                         spi,
                                                         (PropertiesHolder)null,
                                                         defaultClass);
                    } catch (Exception e) {
                        log.error(Messages.getMessage("exception00"), e);
                    }
                    return null;
                }
            });
    }
}
