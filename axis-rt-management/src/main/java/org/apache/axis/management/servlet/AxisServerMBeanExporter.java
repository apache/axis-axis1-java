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
package org.apache.axis.management.servlet;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;

import org.apache.axis.management.ServiceAdmin;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.http.AxisServlet;

/**
 * Listener that registers the MBeans for the {@link AxisServer} created by {@link AxisServlet}. To
 * enable MBean registration in your Web application, add the following configuration to
 * <tt>web.xml</tt>:
 * 
 * <pre>
 * &lt;listener&gt;
 *     &lt;listener-class&gt;org.apache.axis.management.servlet.AxisServerMBeanExporter&lt;/listener-class&gt;
 * &lt;/listener&gt;
 * </pre>
 * 
 * @author Andreas Veithen
 */
public class AxisServerMBeanExporter implements ServletContextAttributeListener {
    public void attributeAdded(ServletContextAttributeEvent event) {
        Object value = event.getValue();
        if (value instanceof AxisServer) {
            ServiceAdmin.setEngine((AxisServer)value, event.getServletContext().getServerInfo());
        }
    }

    public void attributeRemoved(ServletContextAttributeEvent event) {
        // TODO: we currently never unregister the MBeans, but this was also the case in Axis 1.4
    }

    public void attributeReplaced(ServletContextAttributeEvent event) {
        attributeRemoved(event);
        attributeAdded(event);
    }
}
