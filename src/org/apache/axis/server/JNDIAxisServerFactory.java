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

package org.apache.axis.server;

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.utils.Messages;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import java.util.Map;

/**
 * Helper class for obtaining AxisServers, which hides the complexity
 * of JNDI accesses, etc.
 *
 * !!! QUESTION : Does this class need to play any ClassLoader tricks?
 * 
 * @author Glen Daniels (gdaniels@apache.org)
 */ 

public class JNDIAxisServerFactory extends DefaultAxisServerFactory {

    /**
     * Obtain an AxisServer reference, using JNDI if possible, otherwise
     * creating one using the standard Axis configuration pattern.  If we
     * end up creating one and do have JNDI access, bind it to the passed
     * name so we find it next time.
     * 
     * NOTE : REQUIRES SERVLET 2.3 FOR THE GetServletContextName() CALL!
     *
     * @param environment The following is used, in addition to
     *                    the keys used by the parent class:
     *        AxisEngine.ENV_SERVLET_CONTEXT
     *                   [required, else default/parent behavior]
     *                   - Instance of ServletContext
     */
    public AxisServer getServer(Map environment)
        throws AxisFault
    {
        log.debug("Enter: JNDIAxisServerFactory::getServer");

        InitialContext context = null;

        // First check to see if JNDI works
        // !!! Might we need to set up context parameters here?
        try {
            context = new InitialContext();
        } catch (NamingException e) {
            log.warn(Messages.getMessage("jndiNotFound00"), e);
        }
        
        ServletContext servletContext = null;
        try {
            servletContext =
                (ServletContext)environment.get(AxisEngine.ENV_SERVLET_CONTEXT);
        } catch (ClassCastException e) {
            log.warn(Messages.getMessage("servletContextWrongClass00"), e);
            // Fall through
        }

        AxisServer server = null;
        if (context != null  &&  servletContext != null) {
            // Figure out the name by looking in the servlet context (for now)
                
            /**
             * !!! WARNING - THIS CLASS NEEDS TO FIGURE OUT THE CORRECT
             * NAMING SCHEME FOR GETTING/PUTTING SERVERS FROM/TO JNDI!
             * 
             */
                
            // For servlet 2.3....?
            // String name = servletContext.getServletContextName();
                
            // THIS IS NOT ACCEPTABLE JNDI NAMING...
            String name = servletContext.getRealPath("/WEB-INF/Server");

// The following was submitted as a patch, but I don't believe this
// is going to produce a valid JNDI name of ANY sort... yuck.
// This would produce a URL, not a path name.
//
// Since it appears, from comments above, that this entire scheme is
// broken, then for now I'll simply check for a null-name to prevent
// possible NPE on WebLogic.
//
// What ARE we doing here?!?!
//            
//            if (name == null) {
//                try {
//                    name = servletContext.getResource("/WEB-INF/Server").toString();
//                } catch (Exception e) {
//                    // ignore
//                }
//            }
                
            // We've got JNDI, so try to find an AxisServer at the
            // specified name.
            if (name != null) {
                try {
                    server = (AxisServer)context.lookup(name);
                } catch (NamingException e) {
                    // Didn't find it.
                    server = super.getServer(environment);
                    try {
                        context.bind(name, server);
                    } catch (NamingException e1) {
                        // !!! Couldn't do it, what should we do here?
                    }
                }
            }
        }
        
        if (server == null) {
            server = super.getServer(environment);
        }

        log.debug("Exit: JNDIAxisServerFactory::getServer");

        return server;
    }
}
