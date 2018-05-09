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
package org.apache.axis.management;

import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.modeler.Registry;

/**
 * class to act as a dynamic loading registrar to commons-modeler, so
 * as to autoregister stuff
 * <p>
 * <a href="http://www.webweavertech.com/costin/archives/000168.html#000168">http://www.webweavertech.com/costin/archives/000168.html#000168</a>
 */
public class Registrar {
    /**
     * our log
     */
            protected static Log log = LogFactory.getLog(Registrar.class.getName());

    /**
     * register an MBean
     *
     * @param objectToRegister
     * @param name
     * @param context
     */
    public static boolean register(Object objectToRegister,
                                   String name, String context) {
        if (log.isDebugEnabled()) {
            log.debug("Registering " + objectToRegister + " as "
                    + name);
        }
        try {
            Registry.getRegistry(null, null).registerComponent(objectToRegister, name, context);
            return true;
        } catch (Exception ex) {
            log.warn(ex);
            return false;
        }
    }
}
