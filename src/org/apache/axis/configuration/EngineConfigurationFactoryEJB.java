/*
 * Copyright 2002-2005 The Apache Software Foundation.
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

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.EngineConfigurationFactory;
import org.apache.axis.EngineConfiguration;
import org.apache.commons.logging.Log;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;


/**
 * This is an implementation of {@link EngineConfigurationFactory} intended
 * for use when Axis is run from within an EJB container. It enables to find
 * a client configuration <tt>.wsdd</tt> file when it is packaged as part of an
 * ejb-jar file and the Application server uses distinct class loaders
 * per ejb-jar of a J2EE application.
 *
 * @author Andrei Iltchenko (andrei.iltchenko@nl.compuware.com)
 * @author Paulo Moreira    (paulo.moreira@nl.compuware.com)
 */
public class  EngineConfigurationFactoryEJB
    extends EngineConfigurationFactoryDefault
{
    protected static Log log =
        LogFactory.getLog(EngineConfigurationFactoryEJB.class.getName());

    /**
     * Creates and returns a new EngineConfigurationFactory.
     * If a factory cannot be created, returns the <code>null</code> reference.
     *
     * @param p   currently the constuctor argument is used only by the
     *            {@link EngineConfigurationFactoryServlet#newFactory(Object)}
     *            method where it must refer to
     *            a {@link javax.servlet.ServletConfig} object.
     *            In all other contexts (including calling this static factory
     *            method) the agrument is meaningless and shall hold the
     *            <code>null</code> reference.
     * @see org.apache.axis.configuration.EngineConfigurationFactoryFinder
     */
    public static EngineConfigurationFactory  newFactory(Object p) {
        return  p != null  ?  null : new EngineConfigurationFactoryEJB();
    }

    /**
     * Calls {@link EngineConfigurationFactoryDefault#EngineConfigurationFactoryDefault()}.
     * The constructor is defined merely to ensure that it is protected.
     */
    protected  EngineConfigurationFactoryEJB() {
    }

    public EngineConfiguration getClientEngineConfig() {
        InputStream   is;
        try {
            // Try to obtain directly from the file system.
            is = new FileInputStream(clientConfigFile);
        } catch (FileNotFoundException e) {
            // Try to obtain through the context class loader.
            is = Thread.currentThread().getContextClassLoader().
                    getResourceAsStream(clientConfigFile);
        }
        return  new FileProvider(is);
    }

}  /*  For  Class   EngineConfigurationFactoryEJB   */
