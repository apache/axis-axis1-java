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

import javax.xml.rpc.server.ServletEndpointContext;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.utils.Admin;

/**
 * Handler that looks for the {@link MessageContext#QUIT_REQUESTED} flag set by {@link Admin} and
 * initiates the shutdown procedure if the flag is set.
 * 
 * @author Andreas Veithen
 */
public class QuitHandler extends BasicHandler {
    public static final String QUIT_LISTENER = QuitListener.class.getName();
    
    public void invoke(MessageContext msgContext) throws AxisFault {
        if (msgContext.getProperty(MessageContext.QUIT_REQUESTED) != null) {
            ServletEndpointContext sec = ((ServletEndpointContext)msgContext.getProperty(Constants.MC_SERVLET_ENDPOINT_CONTEXT));
            ((QuitListener)sec.getServletContext().getAttribute(QUIT_LISTENER)).requestQuit();
        }
    }
}
