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
package org.apache.axis.deployment.wsdd;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.deployment.wsdd.providers.WSDDHandlerProvider;
import org.apache.axis.deployment.wsdd.providers.WSDDJavaEJBProvider;
import org.apache.axis.deployment.wsdd.providers.WSDDJavaMsgProvider;
import org.apache.axis.deployment.wsdd.providers.WSDDJavaRPCProvider;
import org.apache.axis.deployment.wsdd.providers.WSDDJavaCORBAProvider;
import org.apache.axis.deployment.wsdd.providers.WSDDJavaRMIProvider;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;

import javax.xml.namespace.QName;
import java.util.Hashtable;

import org.apache.commons.discovery.resource.names.DiscoverServiceNames;
import org.apache.commons.discovery.ResourceNameIterator;
import org.apache.commons.discovery.resource.ClassLoaders;
import org.apache.commons.logging.Log;


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
            String className = (String) iter.nextResourceName();
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
     * @param _class XXX
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
