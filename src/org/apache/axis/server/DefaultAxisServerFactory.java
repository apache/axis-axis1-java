/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.axis.server;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.AxisFault;
import org.apache.axis.AxisEngine;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Map;
import java.io.File;

/**
 * Helper class for obtaining AxisServers.  Default implementation.
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
 */ 

public class DefaultAxisServerFactory implements AxisServerFactory {

    /**
     * Get an AxisServer.  This factory looks for an "engineConfig" in the
     * environment Map, and if one is found, uses that.  Otherwise it
     * uses the default initialization.
     * 
     */
    public AxisServer getServer(Map environment)
        throws AxisFault
    {
        EngineConfiguration config = null;
        try {
            config = (EngineConfiguration)environment.
                get(EngineConfiguration.PROPERTY_NAME);
        } catch (ClassCastException e) {
            // Just in case, fall through here.
        }

        
        AxisServer ret= createNewServer(config);

        String attachmentsdirservlet=  null;
        if( null != ret &&  environment != null ){
          if( null !=  (attachmentsdirservlet= (String) 
                environment.get("axis.attachments.Directory"))){
              ret.setOption(AxisEngine.PROP_ATTACHMENT_DIR,
               attachmentsdirservlet);
          }
          if(null == (attachmentsdirservlet= (String)
                ret.getOption(AxisEngine.PROP_ATTACHMENT_DIR))){
              if( null !=  (attachmentsdirservlet= (String) 
                  environment.get("servlet.realpath"))){
                 ret.setOption(AxisEngine.PROP_ATTACHMENT_DIR, attachmentsdirservlet);
              }
          }    
        }
        if(ret != null){
            attachmentsdirservlet= (String) ret.getOption(AxisEngine.PROP_ATTACHMENT_DIR);
            File attdirFile= new File(attachmentsdirservlet);
            if( !attdirFile.isDirectory()){
                      attdirFile.mkdirs();
            }
        }

        return ret;
    }

    /**
     * Do the actual work of creating a new AxisServer, using the passed
     * configuration provider, or going through the default configuration
     * steps if null is passed.
     *
     * @return a shiny new AxisServer, ready for use.
     */
    static private AxisServer createNewServer(EngineConfiguration config)
    {
        if (config == null) {
            // A default engine configuration class may be set in a system
            // property. If so, try creating an engine configuration.
            String configClass = System.getProperty("axis.engineConfigClass");
            if (configClass != null) {
                // Got one - so try to make it (which means it had better have
                // a default constructor - may make it possible later to pass
                // in some kind of environmental parameters...)
                try {
                    Class cls = Class.forName(configClass);
                    config = (EngineConfiguration)cls.newInstance();
                } catch (ClassNotFoundException e) {
                    // Fall through???
                } catch (InstantiationException e) {
                    // Fall through???
                } catch (IllegalAccessException e) {
                    // Fall through???
                }
            }
        }

        // Create an AxisServer using the appropriate config
        if (config == null) {
            return new AxisServer();
        } else {
            return new AxisServer(config);
        }
    }
}
