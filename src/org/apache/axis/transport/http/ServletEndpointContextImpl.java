/*
 * Copyright 2002-2004 The Apache Software Foundation.
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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.server.ServletEndpointContext;
import java.security.Principal;

public class ServletEndpointContextImpl implements ServletEndpointContext {
    
    public HttpSession getHttpSession() {
        HttpServletRequest srvreq = (HttpServletRequest) 
                getMessageContext().getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);
        return (srvreq == null)  ? null : srvreq.getSession();
    }

    public MessageContext getMessageContext() {
        return org.apache.axis.MessageContext.getCurrentContext();
    }

    public ServletContext getServletContext() {
        HttpServlet srv = (HttpServlet)
                getMessageContext().getProperty(HTTPConstants.MC_HTTP_SERVLET);
        return (srv == null) ? null : srv.getServletContext();
    }

    public Principal getUserPrincipal() {
        HttpServletRequest srvreq = (HttpServletRequest)
                getMessageContext().getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);

        return (srvreq == null) ? null : srvreq.getUserPrincipal();
    }
}
