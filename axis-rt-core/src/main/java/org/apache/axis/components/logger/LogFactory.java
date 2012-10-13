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

package org.apache.axis.components.logger;

import org.apache.commons.discovery.tools.DiscoverSingleton;
import org.apache.commons.logging.Log;

import java.security.AccessController;
import java.security.PrivilegedAction;


/**
 * @author Richard A. Sitze
 */
public class LogFactory {
    /**
     * Override group context..
     */
    private static final org.apache.commons.logging.LogFactory logFactory =
        getLogFactory();

    public static Log getLog(String name) {
        return org.apache.commons.logging.LogFactory.getLog(name);
    }
    
    private static final org.apache.commons.logging.LogFactory getLogFactory() {
        return (org.apache.commons.logging.LogFactory)
            AccessController.doPrivileged(
                new PrivilegedAction() {
                    public Object run() {
                        return DiscoverSingleton.find(org.apache.commons.logging.LogFactory.class,
                                       org.apache.commons.logging.LogFactory.FACTORY_PROPERTIES,
                                       org.apache.commons.logging.LogFactory.FACTORY_DEFAULT);
                    }
                });
    }
}
