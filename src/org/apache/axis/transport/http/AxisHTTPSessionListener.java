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

import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSession;
import javax.xml.rpc.server.ServiceLifecycle;
import java.util.Enumeration;

/**
 * A simple listener for Servlet 2.3 session lifecycle events.
 * @web.listener
 * @author Glen Daniels (gdaniels@apache.org)
 */ 
public class AxisHTTPSessionListener implements HttpSessionListener {
    protected static Log log =
            LogFactory.getLog(AxisHTTPSessionListener.class.getName());
    
    /**
     * Static method to destroy all ServiceLifecycle objects within an
     * Axis session.
     */ 
    static void destroySession(HttpSession session)
    {
        // Check for our marker so as not to do unneeded work
        if (session.getAttribute(AxisHttpSession.AXIS_SESSION_MARKER) == null)
            return;
        
        if (log.isDebugEnabled()) {
            log.debug("Got destroySession event : " + session);
        }
        
        Enumeration e = session.getAttributeNames();
        while (e.hasMoreElements()) {
            Object next = e.nextElement();
            if (next instanceof ServiceLifecycle) {
                ((ServiceLifecycle)next).destroy();
            }
        }
    }        
    
    /** No-op for now */
    public void sessionCreated(HttpSessionEvent event) {
    }

    /**
     * Called when a session is destroyed by the servlet engine.  We use
     * the relevant HttpSession to look up an AxisHttpSession, and destroy
     * all the appropriate objects stored therein.
     *  
     * @param event the event descriptor passed in by the servlet engine
     */ 
    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        destroySession(session);
    }
}
