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
package org.apache.axis.components.net;

import org.apache.axis.AxisProperties;
import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

import java.util.Hashtable;

/**
 * Class SocketFactoryFactory
 *
 * @author
 * @version %I%, %G%
 */
public class SocketFactoryFactory {

    /** Field log           */
    protected static Log log =
            LogFactory.getLog(SocketFactoryFactory.class.getName());

    /** socket factory */
    private static Hashtable factories = new Hashtable();

    private static final Class classes[] = new Class[] { Hashtable.class };


    static {
        AxisProperties.setClassOverrideProperty(SocketFactory.class,
                                       "axis.socketFactory");

        AxisProperties.setClassDefault(SocketFactory.class,
                                       "org.apache.axis.components.net.DefaultSocketFactory");

        AxisProperties.setClassOverrideProperty(SecureSocketFactory.class,
                                       "axis.socketSecureFactory");

        AxisProperties.setClassDefault(SecureSocketFactory.class,
                                       "org.apache.axis.components.net.JSSESocketFactory");
    }
    
    /**
     * Returns a copy of the environment's default socket factory.
     * 
     * @param protocol Today this only supports "http" & "https".
     * @param attributes
     *
     * @return
     */
    public static synchronized SocketFactory getFactory(String protocol,
                                                        Hashtable attributes) {
        SocketFactory theFactory = (SocketFactory)factories.get(protocol);

        if (theFactory == null) {
            Object objects[] = new Object[] { attributes };
    
            if (protocol.equalsIgnoreCase("http")) {
                theFactory = (SocketFactory)
                    AxisProperties.newInstance(SocketFactory.class, classes, objects);
            } else if (protocol.equalsIgnoreCase("https")) {
                theFactory = (SecureSocketFactory)
                    AxisProperties.newInstance(SecureSocketFactory.class, classes, objects);
            }
            
            if (theFactory != null) {
                factories.put(protocol, theFactory);
            }
        }
        return theFactory;
    }
}
