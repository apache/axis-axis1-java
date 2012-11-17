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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Hashtable;

import org.apache.axis.AxisEngine;
import org.apache.axis.ConfigurationException;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDDocument;
import org.apache.axis.deployment.wsdd.WSDDGlobalConfiguration;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

public class DirProvider extends DelegatingWSDDEngineConfiguration {

    protected static Log log =
        LogFactory.getLog(DirProvider.class.getName());

    private WSDDDeployment deployment = null;
    private String configFile;
    private File dir;

    private static final String SERVER_CONFIG_FILE = 
        "server-config.wsdd";

    public DirProvider(String basepath)
        throws ConfigurationException {
        this(basepath, SERVER_CONFIG_FILE);
    }

    public DirProvider(String basepath, String configFile)
        throws ConfigurationException {
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

        this.dir = dir;
        this.configFile = configFile;
    }

    public WSDDDeployment getDeployment() {
        return this.deployment;
    }

    private static class DirFilter implements FileFilter {
        public boolean accept(File path) {
            return path.isDirectory();
        }
    }

    public void configureEngine(AxisEngine engine)
        throws ConfigurationException {
        this.deployment = new WSDDDeployment();
        WSDDGlobalConfiguration config = new WSDDGlobalConfiguration();
        config.setOptionsHashtable(new Hashtable());
        this.deployment.setGlobalConfiguration(config);
        File [] dirs = this.dir.listFiles(new DirFilter());
        for (int i = 0; i < dirs.length; i++) {
            processWSDD(dirs[i]);
        }
        this.deployment.configureEngine(engine);
        engine.refreshGlobalOptions();
    }

    private void processWSDD(File dir) 
        throws ConfigurationException {
        File file = new File(dir, this.configFile);
        if (!file.exists()) {
            return;
        }
        log.debug("Loading service configuration from file: " + file);
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            WSDDDocument doc = new WSDDDocument(XMLUtils.newDocument(in));
            doc.deploy(this.deployment);
        } catch (Exception e) {
            throw new ConfigurationException(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {}
            }
        }
    }

    /**
     * Save the engine configuration.  In case there's a problem, we
     * write it to a string before saving it out to the actual file so
     * we don't screw up the file.
     */
    public void writeEngineConfig(AxisEngine engine)
        throws ConfigurationException {
        // this is not implemented
    }
}
