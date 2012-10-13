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

import org.apache.axis.AxisEngine;
import org.apache.axis.ConfigurationException;

import java.io.ByteArrayInputStream;

/**
 * A simple ConfigurationProvider that uses the Admin class to
 * configure the engine from a String containing XML.
 *
 * This provider does not write configuration to persistent storage.
 *
 * Example of usage:
 *    new XMLStringProvider("<engineConfig><handlers><handler name=" +
 *        "\"MsgDispatcher\" class=\"org.apache.axis.providers.java" +
 *        ".MsgProvider\"/></handlers><services><service name=\"Adm" +
 *        "inService\" pivot=\"MsgDispatcher\"><option name=\"class" +
 *        "Name\" value=\"org.apache.axis.utils.Admin\"/><option na" +
 *        "me=\"allowedMethods\" value=\"AdminService\"/><option na" +
 *        "me=\"enableRemoteAdmin\" value=\"false\"/></service></se" +
 *        "rvices></engineConfig>");
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class XMLStringProvider extends FileProvider
{
    String xmlConfiguration;

    /**
     * Constructor
     *
     * @param xmlConfiguration a String containing an engine configuration
     *        in XML.
     */
    public XMLStringProvider(String xmlConfiguration)
    {
        super(new ByteArrayInputStream(xmlConfiguration.getBytes()));
        this.xmlConfiguration = xmlConfiguration;
    }

    public void writeEngineConfig(AxisEngine engine)
            throws ConfigurationException {
        // NOOP
    }

    public void configureEngine(AxisEngine engine) throws ConfigurationException {
        setInputStream(new ByteArrayInputStream(xmlConfiguration.getBytes()));
        super.configureEngine(engine);
    }
}
