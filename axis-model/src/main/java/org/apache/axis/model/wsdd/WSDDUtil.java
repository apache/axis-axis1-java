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
package org.apache.axis.model.wsdd;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.configuration.DefaultConfiguration;
import org.apache.axis.model.util.AxisXMLResource;
import org.apache.axis.model.wsdd.impl.WSDDPackageImpl;
import org.apache.commons.logging.Log;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.util.XMLProcessor;
import org.xml.sax.InputSource;

public final class WSDDUtil {
    private static final Log log = LogFactory.getLog(WSDDUtil.class.getName());
    
    private WSDDUtil() {}

    public static Deployment load(InputSource is) throws IOException {
        WSDDPackageImpl.eINSTANCE.eClass();
        AxisXMLResource resource = new AxisXMLResource();
        Map options = new HashMap();
        options.put(XMLResource.OPTION_EXTENDED_META_DATA, new WSDDExtendedMetaData());
        options.put(AxisXMLResource.OPTION_IGNORE_NAMESPACE_FOR_UNQUALIFIED_QNAME, WSDDPackageImpl.eNS_URI);
        resource.load(is, options);
        return (Deployment)resource.getContents().get(0);        
    }
    
    /**
     * Load the default Axis configuration. This method implements the same algorithm as
     * {@link DefaultConfiguration}.
     * 
     * @param cl
     *            the class loader to load the configuration from
     * @param type
     *            the type of configuration (<tt>client</tt> or <tt>server</tt>)
     * @return the default configuration
     * @throws IOException
     */
    public static Deployment buildDefaultConfiguration(ClassLoader cl, String type) throws IOException {
        // Load the base configuration
        String resourceName = "org/apache/axis/" + type + "/" + type + "-config.wsdd";
        InputStream in = cl.getResourceAsStream(resourceName);
        if (in == null) {
            throw new IOException("Resource " + resourceName + " not found");
        }
        if (log.isDebugEnabled()) {
            log.debug("Loading resource " + resourceName);
        }
        Deployment deployment;
        try {
            deployment = WSDDUtil.load(new InputSource(in));
        } finally {
            in.close();
        }
        
        // Discover and load additional default configuration fragments
        resourceName = "META-INF/axis/default-" + type + "-config.wsdd";
        Enumeration resources = cl.getResources(resourceName);
        while (resources.hasMoreElements()) {
            URL url = (URL)resources.nextElement();
            if (log.isDebugEnabled()) {
                log.debug("Loading " + url);
            }
            in = url.openStream();
            try {
                deployment.merge(WSDDUtil.load(new InputSource(in)));
            } finally {
                in.close();
            }
        }
        
        return deployment;
    }
    
    public static void save(Deployment deployment, OutputStream out) throws IOException {
        AxisXMLResource resource = new AxisXMLResource();
        XMLProcessor processor = new XMLProcessor();
        resource.getContents().add(deployment);
        Map options = new HashMap();
        options.put(XMLResource.OPTION_ENCODING, "UTF-8");
        processor.save(out, resource, options);        
    }
    
    public static void save(Deployment deployment, Writer writer) throws IOException {
        AxisXMLResource resource = new AxisXMLResource();
        XMLProcessor processor = new XMLProcessor();
        resource.getContents().add(deployment);
        Map options = new HashMap();
        options.put(XMLResource.OPTION_DECLARE_XML, Boolean.FALSE);
        processor.save(writer, resource, options);        
    }
}
