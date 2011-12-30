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

import org.apache.axis.deployment.wsdd.WSDDGlobalConfiguration;
import org.apache.axis.deployment.wsdd.WSDDHandler;

public interface DeploymentAdministratorMBean {
    public void saveConfiguration();

    public void configureGlobalConfig(WSDDGlobalConfiguration config);

    public void deployHandler(WSDDHandler handler);

    public void deployService(WSDDServiceWrapper service);

    public void deployTransport(WSDDTransportWrapper transport);

    public void undeployHandler(String qname);

    public void undeployService(String qname);

    public void undeployTransport(String qname);
}