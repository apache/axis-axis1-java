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
package org.apache.axis.deployment.wsdd;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.deployment.wsdd.providers.WSDDBsfProvider;
import org.apache.axis.deployment.wsdd.providers.WSDDComProvider;
import org.apache.axis.deployment.wsdd.providers.WSDDHandlerProvider;
import org.apache.axis.deployment.wsdd.providers.WSDDJavaCORBAProvider;
import org.apache.axis.deployment.wsdd.providers.WSDDJavaEJBProvider;
import org.apache.axis.deployment.wsdd.providers.WSDDJavaMsgProvider;
import org.apache.axis.deployment.wsdd.providers.WSDDJavaRMIProvider;
import org.apache.axis.deployment.wsdd.providers.WSDDJavaRPCProvider;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.commons.discovery.ResourceNameIterator;
import org.apache.commons.discovery.resource.ClassLoaders;
import org.apache.commons.discovery.resource.names.DiscoverServiceNames;
import org.apache.commons.logging.Log;

import javax.xml.namespace.QName;
import java.util.Hashtable;


/**
 * WSDD provider element
 *
 * Represents the liason to the application being exposed
 * as a Web Service.
 *
 * Specific provider extension classes must be registered
 * by namespace URI.
 *
 * @author James Snell
 * @author Vishy Kasar
 */
public abstract class WSDDProvider
{
    protected static Log log =
        LogFactory.getLog(WSDDProvider.class.getName());

// ** STATIC PROVIDER REGISTRY ** //
    private static final String PLUGABLE_PROVIDER_FILENAME = 
       "org.apache.axis.deployment.wsdd.Provider";

    /** XXX */
    private static Hashtable providers = new Hashtable();

    static {
        providers.put(WSDDConstants.QNAME_JAVARPC_PROVIDER, new WSDDJavaRPCProvider());
        providers.put(WSDDConstants.QNAME_JAVAMSG_PROVIDER, new WSDDJavaMsgProvider());
        providers.put(WSDDConstants.QNAME_HANDLER_PROVIDER, new WSDDHandlerProvider());
        providers.put(WSDDConstants.QNAME_EJB_PROVIDER, new WSDDJavaEJBProvider());
        providers.put(WSDDConstants.QNAME_COM_PROVIDER, new WSDDComProvider());
        providers.put(WSDDConstants.QNAME_BSF_PROVIDER, new WSDDBsfProvider());
        providers.put(WSDDConstants.QNAME_CORBA_PROVIDER, new WSDDJavaCORBAProvider());
        providers.put(WSDDConstants.QNAME_RMI_PROVIDER, new WSDDJavaRMIProvider());
        try {
            loadPluggableProviders();
        } catch (Throwable t){
            String msg=t + JavaUtils.LS + JavaUtils.stackToString(t);
            log.info(Messages.getMessage("exception01",msg));
        }
    }

    /**
       Look for file META-INF/services/org.apache.axis.deployment.wsdd.Provider
       in all the JARS, get the classes listed in those files and add them to 
       providers list if they are valid providers. 

       Here is how the scheme would work.

       A company providing a new provider will jar up their provider related
       classes in a JAR file. The following file containing the name of the new 
       provider class is also made part of this JAR file. 

       META-INF/services/org.apache.axis.deployment.wsdd.Provider

       By making this JAR part of the webapp, the new provider will be 
       automatically discovered. 
    */
    private static void loadPluggableProviders() {
        ClassLoader clzLoader = WSDDProvider.class.getClassLoader();
        ClassLoaders loaders = new ClassLoaders();
        loaders.put(clzLoader);
        DiscoverServiceNames dsn = new DiscoverServiceNames(loaders);
        ResourceNameIterator iter = dsn.findResourceNames(PLUGABLE_PROVIDER_FILENAME);
        while (iter.hasNext()) {
            String className = iter.nextResourceName();
            try {
                Object o = Class.forName(className).newInstance();
                if (o instanceof WSDDProvider) {
                    WSDDProvider provider = (WSDDProvider) o;
                    String providerName = provider.getName();
                    QName q = new QName(WSDDConstants.URI_WSDD_JAVA, providerName);
                    providers.put(q, provider);
                }
            } catch (Exception e) {
                String msg=e + JavaUtils.LS + JavaUtils.stackToString(e);
                log.info(Messages.getMessage("exception01",msg));
                continue;
            }
        }
    }

    /**
     *
     * @param uri XXX
     * @param prov XXX
     */
    public static void registerProvider(QName uri, WSDDProvider prov)
    {
        providers.put(uri, prov);
    }

    /**
     *
     * @return XXX
     */
    public WSDDOperation[] getOperations()
    {
        return null;
    }

    /**
     *
     * @param name XXX
     * @return XXX
     */
    public WSDDOperation getOperation(String name)
    {
        return null;
    }

    /**
     *
     * @param registry XXX
     * @return XXX
     * @throws Exception XXX
     */
    public static Handler getInstance(QName providerType,
                               WSDDService service,
                               EngineConfiguration registry)
        throws Exception
    {
        if (providerType == null)
            throw new WSDDException(Messages.getMessage("nullProvider00"));
        
        WSDDProvider provider = (WSDDProvider)providers.get(providerType);
        if (provider == null) {
            throw new WSDDException(Messages.getMessage("noMatchingProvider00",
                                    providerType.toString()));
        }
        
        return provider.newProviderInstance(service, registry);
    }

    /**
     *
     * @param registry XXX
     * @return XXX
     * @throws Exception XXX
     */
    public abstract Handler newProviderInstance(WSDDService service,
                                                EngineConfiguration registry)
        throws Exception;

    public abstract String getName();
}
