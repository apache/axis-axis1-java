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

package org.apache.axis.configuration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import org.apache.axis.AxisEngine;
import org.apache.axis.ConfigurationException;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDDocument;
import org.apache.axis.utils.Admin;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Document;

/**
 * A simple ConfigurationProvider that uses the Admin class to read +
 * write XML files.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 * @author Glyn Normington (glyn@apache.org)
 */
public class FileProvider extends DelegatingWSDDEngineConfiguration {
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
        if (!dir.exists() || !dir.isDirectory() || !dir.canRead()) {
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
        try {
            readOnly = configFile.canRead() & !configFile.canWrite();
        } catch (SecurityException se){
            readOnly = true;            
        }

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
                    // Ignore and continue
                }
            }
            if (getInputStream() == null && searchClasspath) {
                // Attempt to load the file from the classpath
                setInputStream(ClassUtils.getResourceAsStream(filename, engine.getClass().getClassLoader()));
            }

            if (getInputStream() == null) {
                // Load the default configuration. This piece of code provides compatibility with Axis 1.4,
                // which ends up loading org/apache/axis/(client|server)/(client|server)-config.wsdd if
                // (1) filename is (client|server)-config.wsdd;
                // (2) the runtime type of the engine is AxisClient or AxisServer;
                // (3) the file is not found on the file system or in the classpath.
                String type;
                if (filename.equals(EngineConfigurationFactoryDefault.CLIENT_CONFIG_FILE)) {
                    type = "client";
                } else if (filename.equals(EngineConfigurationFactoryDefault.SERVER_CONFIG_FILE)) {
                    type = "server";
                } else {
                    throw new ConfigurationException(
                            Messages.getMessage("noConfigFile"));
                }
                DefaultConfiguration defaultConfig = new DefaultConfiguration(type);
                defaultConfig.configureEngine(engine);
                deployment = defaultConfig.getDeployment();
            } else {
                WSDDDocument doc = new WSDDDocument(XMLUtils.
                                                    newDocument(getInputStream()));
                deployment = doc.getDeployment();
    
                deployment.configureEngine(engine);
            }
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
                Writer osWriter = new OutputStreamWriter(
                        new FileOutputStream(configFile),XMLUtils.getEncoding());
                PrintWriter writer = new PrintWriter(new BufferedWriter(osWriter));
                XMLUtils.DocumentToWriter(doc, writer);
                writer.println();
                writer.close();
            } catch (Exception e) {
                throw new ConfigurationException(e);
            }
        }
    }
}
