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
package org.apache.axis.management.jmx;

import org.apache.axis.AxisFault;
import org.apache.axis.ConfigurationException;

/**
 * The ServiceAdministrator MBean exposes the
 * org.apache.axis.management.ServiceAdmin object
 *
 * @author bdillon
 * @version 1.0
 */
public interface ServiceAdministratorMBean {
    /**
     * get the axis version
     *
     * @return
     */
    public String getVersion();

    /**
     * Start the server
     */
    public void start();

    /**
     * stop the server
     */
    public void stop();

    /**
     * restart the server
     */
    public void restart();

    /**
     * Start the Service
     *
     * @param serviceName
     * @throws AxisFault ConfigurationException
     */
    public void startService(String serviceName) throws AxisFault,
            ConfigurationException;

    /**
     * Stop the Service
     *
     * @param serviceName
     * @throws AxisFault ConfigurationException
     */
    public void stopService(String serviceName) throws AxisFault,
            ConfigurationException;
}