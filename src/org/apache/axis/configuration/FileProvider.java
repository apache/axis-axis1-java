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

package org.apache.axis.configuration;

import org.apache.axis.AxisEngine;
import org.apache.axis.ConfigurationException;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDDocument;
import org.apache.axis.deployment.wsdd.WSDDGlobalConfiguration;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.utils.Admin;
import org.apache.axis.utils.XMLUtils;
import org.apache.axis.utils.JavaUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import javax.xml.rpc.namespace.QName;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Hashtable;

/**
 * A simple ConfigurationProvider that uses the Admin class to read +
 * write XML files.
 *
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class FileProvider implements EngineConfiguration {
    protected static Log log =
        LogFactory.getLog(FileProvider.class.getName());

    protected String sep = System.getProperty("file.separator");

    protected WSDDDeployment deployment = null;

    private static final String CURRENT_DIR = ".";
    protected String basepath;
    protected String filename;

    protected InputStream myInputStream = null;

    protected boolean readOnly = true;

    // Should we search the classpath for the file if we don't find it in
    // the specified location?
    boolean searchClasspath = true;

    /**
     * Constructor which accesses a file in the current directory of the
     * engine.
     */
    public FileProvider(String filename) {
        this(CURRENT_DIR, filename);
    }

    /**
     * Constructor which accesses a file relative to a specific base
     * path.
     */
    public FileProvider(String basepath, String filename) {
        this.basepath = basepath;
        this.filename = filename;
        File file = new File(basepath, filename);
        readOnly = file.canRead() & !file.canWrite();

        /*
         * If file is read-only, log informational message
         * as configuration changes will not persist.
         */
        if (readOnly) {
            log.info(JavaUtils.getMessage("readOnlyConfigFile"));
        }
    }

    /**
     * Constructor which takes an input stream directly.
     * Note: The configuration will be read-only in this case!
     */
    public FileProvider(InputStream is) {
        myInputStream = is;
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
            if (myInputStream == null) {
                try {
                    myInputStream = new FileInputStream(basepath + sep +
                                                        filename);
                } catch (Exception e) {
                    if (searchClasspath) {
                        myInputStream = engine.getClass().
                            getResourceAsStream(filename);
                    }
                }
            }

            if (myInputStream == null) {
                throw new ConfigurationException("No engine configuration " +
                                                 "file - aborting!");
            }

            WSDDDocument doc = new WSDDDocument(XMLUtils.
                                                newDocument(myInputStream));
            deployment = doc.getDeployment();

            deployment.configureEngine(engine);
            engine.refreshGlobalOptions();

            myInputStream = null;
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
                FileOutputStream fos = new FileOutputStream(basepath + sep +
                                                            filename);
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
        return deployment.getService(qname);
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
        WSDDGlobalConfiguration globalConfig = deployment.
            getGlobalConfiguration();
        if (globalConfig != null)
            return globalConfig.getParametersTable();

        return null;
    }

}
