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

/**
 * @author Eric Friedman
 */
public class CommonsHTTPClientPropertiesFactory {
    protected static Log log =
            LogFactory.getLog(CommonsHTTPClientPropertiesFactory.class.getName());
    
    private static CommonsHTTPClientProperties properties;

    public static synchronized CommonsHTTPClientProperties create() {
        if (properties == null) {
            properties = (CommonsHTTPClientProperties)
                AxisProperties.newInstance(CommonsHTTPClientProperties.class,
                                           DefaultCommonsHTTPClientProperties.class);
        }
        return properties;
    }
}
