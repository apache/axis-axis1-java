/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

import org.apache.axis.AxisEngine;
import org.apache.axis.ConfigurationException;
import org.apache.axis.Handler;
import org.apache.axis.WSDDEngineConfiguration;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDDocument;
import org.apache.axis.deployment.wsdd.WSDDGlobalConfiguration;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.utils.Admin;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Document;

import javax.xml.namespace.QName;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * A simple ConfigurationProvider that uses the Admin class to read +
 * write XML files.
 *
 * @author Glen Daniels (gdaniels@macromedia.com)
 * @author Glyn Normington (glyn@apache.org)
 */
public class FileProvider implements WSDDEngineConfiguration {
    protected static Log log =
        LogFactory.getLog(FileProvider.class.getName());

    private WSDDDeployment deployment = null;

    private String filename;
    private File configFile = null;

    private InputStream myInputStream = null;

    private boolean readOnly = true;

    // Should we search the classpath for the file if we don't find it in
    // the specified location?
    private boolean searchClasspath = true;

    /**
     * Constructor which accesses a file in the current directory of the
     * engine or at an absolute path.
     */
    public FileProvider(String filename) {
        this.filename = filename;
        configFile = new File(filename);
        check();
    }

    /**
     * Constructor which accesses a file relative to a specific base
     * path.
     */
    public FileProvider(String basepath, String filename) 
        throws ConfigurationException {
        this.filename = filename;

        File dir = new File(basepath);

        /*
         * If the basepath is not a readable directory, throw an internal
         * exception to make it easier to debug setup problems.
         */
        if (!dir.isDirectory() || !dir.canRead()) {
            throw new ConfigurationException(Messages.getMessage
                                             ("invalidConfigFilePath",
                                              basepath));
        }

        configFile = new File(basepath, filename);
        check();
    }

    /**
     * Check the configuration file attributes and remember whether
     * or not the file is read-only.
     */
    private void check() {
        readOnly = configFile.canRead() & !configFile.canWrite();

        /*
         * If file is read-only, log informational message
         * as configuration changes will not persist.
         */
        if (readOnly) {
            log.info(Messages.getMessage("readOnlyConfigFile"));
        }
    }

    /**
     * Constructor which takes an input stream directly.
     * Note: The configuration will be read-only in this case!
     */
    public FileProvider(InputStream is) {
        setInputStream(is);
    }
    
    public void setInputStream(InputStream is) {
        myInputStream = is;
    }
    
    private InputStream getInputStream() {
        return myInputStream;
    }

    public WSDDDeployment getDeployment() {
        return deployment;
    }

    public void setDeployment(WSDDDeployment deployment) {
        this.deployment = deployment;
    }

    /**
     * Determine whether or not we will look for a "*-config.wsdd" file
     * on the classpath if we don't find it in the specified location.
     *
     * @param searchClasspath true if we should search the classpath
     */
    public void setSearchClasspath(boolean searchClasspath) {
        this.searchClasspath = searchClasspath;
    }

    public void configureEngine(AxisEngine engine)
        throws ConfigurationException {
        try {
            if (getInputStream() == null) {
                try {
                    setInputStream(new FileInputStream(configFile));
                } catch (Exception e) {
                    if (searchClasspath)
                        setInputStream(ClassUtils.getResourceAsStream(engine.getClass(), filename));
                }
            }

            if (getInputStream() == null) {
                throw new ConfigurationException(
                        Messages.getMessage("noConfigFile"));
            }

            WSDDDocument doc = new WSDDDocument(XMLUtils.
                                                newDocument(getInputStream()));
            deployment = doc.getDeployment();

            deployment.configureEngine(engine);
            engine.refreshGlobalOptions();

            setInputStream(null);
        } catch (Exception e) {
            throw new ConfigurationException(e);
        }
    }

    /**
     * Save the engine configuration.  In case there's a problem, we
     * write it to a string before saving it out to the actual file so
     * we don't screw up the file.
     */
    public void writeEngineConfig(AxisEngine engine)
        throws ConfigurationException {
        if (!readOnly) {
            try {
                Document doc = Admin.listConfig(engine);
                StringWriter writer = new StringWriter();
                XMLUtils.DocumentToWriter(doc, writer);
                writer.close();
                FileOutputStream fos = new FileOutputStream(configFile);
                fos.write(writer.getBuffer().toString().getBytes());
                fos.close();
            } catch (Exception e) {
                throw new ConfigurationException(e);
            }
        }
    }

    /**
     * retrieve an instance of the named handler
     * @param qname XXX
     * @return XXX
     * @throws ConfigurationException XXX
     */
    public Handler getHandler(QName qname) throws ConfigurationException {
        return deployment.getHandler(qname);
    }

    /**
     * retrieve an instance of the named service
     * @param qname XXX
     * @return XXX
     * @throws ConfigurationException XXX
     */
    public SOAPService getService(QName qname) throws ConfigurationException {
        SOAPService service = deployment.getService(qname);
        if (service == null) {
            throw new ConfigurationException(Messages.getMessage("noService06"));
        }
        return service;
    }

    /**
     * Get a service which has been mapped to a particular namespace
     * 
     * @param namespace a namespace URI
     * @return an instance of the appropriate Service, or null
     */
    public SOAPService getServiceByNamespaceURI(String namespace)
            throws ConfigurationException {
        return deployment.getServiceByNamespaceURI(namespace);
    }

    /**
     * retrieve an instance of the named transport
     * @param qname XXX
     * @return XXX
     * @throws ConfigurationException XXX
     */
    public Handler getTransport(QName qname) throws ConfigurationException {
        return deployment.getTransport(qname);
    }

    public TypeMappingRegistry getTypeMappingRegistry()
        throws ConfigurationException {
        return deployment.getTypeMappingRegistry();
    }

    /**
     * Returns a global request handler.
     */
    public Handler getGlobalRequest() throws ConfigurationException {
        return deployment.getGlobalRequest();
    }

    /**
     * Returns a global response handler.
     */
    public Handler getGlobalResponse() throws ConfigurationException {
        return deployment.getGlobalResponse();
    }

    /**
     * Returns the global configuration options.
     */
    public Hashtable getGlobalOptions() throws ConfigurationException {
        WSDDGlobalConfiguration globalConfig
            = deployment.getGlobalConfiguration();
            
        if (globalConfig != null)
            return globalConfig.getParametersTable();

        return null;
    }

    /**
     * Get an enumeration of the services deployed to this engine
     */
    public Iterator getDeployedServices() throws ConfigurationException {
        return deployment.getDeployedServices();
    }
}
