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

import java.util.HashMap;


/**
 * @author Richard A. Sitze
 */
public class TransportClientPropertiesFactory {
    protected static Log log =
            LogFactory.getLog(SocketFactoryFactory.class.getName());
    
    private static HashMap cache = new HashMap();
    private static HashMap defaults = new HashMap();
    
    static {
        defaults.put("http", DefaultHTTPTransportClientProperties.class);
        defaults.put("https", DefaultHTTPSTransportClientProperties.class);
    }

    public static TransportClientProperties create(String protocol)
    {
        TransportClientProperties tcp =
            (TransportClientProperties)cache.get(protocol);
        
        if (tcp == null) {
            tcp = (TransportClientProperties)
                AxisProperties.newInstance(TransportClientProperties.class,
                                           (Class)defaults.get(protocol));

            if (tcp != null) {
                cache.put(protocol, tcp);
            }
        }
        
        return tcp;
    }
}
