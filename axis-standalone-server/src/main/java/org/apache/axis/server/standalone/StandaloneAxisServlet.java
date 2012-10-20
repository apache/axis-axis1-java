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
package org.apache.axis.server.standalone;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.axis.WSDDEngineConfiguration;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDDocument;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.http.AxisServlet;
import org.apache.axis.utils.XMLUtils;

/**
 * Extends {@link AxisServlet} to register the {@link QuitHandler}.
 * 
 * @author Andreas Veithen
 */
public class StandaloneAxisServlet extends AxisServlet {
    private final List/*<URL>*/ wsddUrls;
    
    public StandaloneAxisServlet() {
        wsddUrls = new ArrayList();
        wsddUrls.add(StandaloneAxisServlet.class.getResource("quit-handler-deploy.wsdd"));
    }
    
    public void enableJWS() {
        wsddUrls.add(StandaloneAxisServlet.class.getResource("jws-handler.wsdd"));
    }
    
    public void init() throws ServletException {
        super.init();
        try {
            AxisServer engine = getEngine();
            WSDDDeployment registry = ((WSDDEngineConfiguration)engine.getConfig()).getDeployment();
            for (Iterator it = wsddUrls.iterator(); it.hasNext(); ) {
                URL url = (URL)it.next();
                WSDDDocument wsdd = new WSDDDocument(XMLUtils.newDocument(url.toExternalForm()));
                wsdd.deploy(registry);
            }
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }
}
