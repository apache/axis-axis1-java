/*
 * Copyright 2003,2004 The Apache Software Foundation.
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

package org.apache.axis.transport.http;

import org.apache.commons.logging.Log;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.deployment.wsdd.WSDDDocument;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.utils.XMLUtils;
import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.ConfigurationException;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.WSDDEngineConfiguration;
import org.apache.axis.i18n.Messages;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

/**
 * Servlet that autoregisters
 * @author Steve Loughran
 * xdoclet tags are not active yet; keep web.xml in sync
 * @web.servlet name="AutoRegisterServlet"  display-name="Axis Autoregister Servlet"  load-on-startup="30"
 */
public class AutoRegisterServlet extends AxisServletBase {

    private static Log log =
            LogFactory.getLog(AutoRegisterServlet.class.getName());

    /**
     * init by registering
     */
    public void init() throws javax.servlet.ServletException {
        log.debug(Messages.getMessage("autoRegServletInit00"));
        autoRegister();
    }

    /**
     * register an open stream, which we close afterwards
     * @param instream
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     */
    public void registerStream(InputStream instream) throws SAXException, ParserConfigurationException, IOException {
        try {
            Document doc=XMLUtils.newDocument(instream);
            WSDDDocument wsddDoc = new WSDDDocument(doc);
            WSDDDeployment deployment;
            deployment = getDeployment();
            if(deployment!=null) {
                wsddDoc.deploy(deployment);
            }
        } finally {
            instream.close();
        }
    }

    /**
     * register a resource
     * @param resourcename
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     */
    public void registerResource(String resourcename)
            throws SAXException, ParserConfigurationException, IOException {
        InputStream in=getServletContext().getResourceAsStream(resourcename);
        if(in==null) {
            throw new FileNotFoundException(resourcename);
        }
        registerStream(in);
    }

    /**
     * register a file
     * @param file
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public void registerFile(File file) throws IOException, SAXException, ParserConfigurationException {
        InputStream in=new BufferedInputStream(new FileInputStream(file));
        registerStream(in);
    }

    /**
     * subclass this to return an array of resource names.
     * @return array of resource names of wsdd files, or null
     */
    public String[] getResourcesToRegister() {
        return null;
    }



    /**
     * get deployment
     * @return
     * @throws AxisFault
     */
    private WSDDDeployment getDeployment() throws AxisFault {
        WSDDDeployment deployment;
        AxisEngine engine = getEngine();
        EngineConfiguration config = engine.getConfig();
        if (config instanceof WSDDEngineConfiguration) {
            deployment = ((WSDDEngineConfiguration) config).getDeployment();
        } else {
            deployment=null;
        }
        return deployment;
    }

    /**
     * handler for logging success, defaults to handing off to logging
     * at debug level
     * @param item what were we loading?
     */
    protected void logSuccess(String item) {
        log.debug(Messages.getMessage("autoRegServletLoaded01",item));
    }

    /**
     * register classes, log exceptions
     */
    protected void autoRegister() {
        String[] resources=getResourcesToRegister();
        if(resources==null || resources.length==0) {
            return;
        }
        for(int i=0;i<resources.length;i++) {
            final String resource = resources[i];
            registerAndLogResource(resource);
        }
        registerAnythingElse();
        try {
            applyAndSaveSettings();
        } catch (Exception e) {
            log.error(Messages.getMessage("autoRegServletApplyAndSaveSettings00"), e);
        }
    }

    /**
     * override point for subclasses to add other registration stuff
     */
    protected void registerAnythingElse() {
    }

    /**
     * register a single resource; log trouble and success.
     * @param resource
     */
    public void registerAndLogResource(final String resource) {
        try {
            registerResource(resource);
            logSuccess(resource);
        } catch (Exception e) {
            log.error(Messages.getMessage("autoRegServletLoadFailed01",resource),e);
        }
    }

    /**
     * actually update the engine and save the settings
     * @throws AxisFault
     * @throws ConfigurationException
     */
    protected void applyAndSaveSettings()
            throws AxisFault, ConfigurationException {
        AxisEngine engine = getEngine();
        engine.refreshGlobalOptions();
        engine.saveConfiguration();
    }
}
