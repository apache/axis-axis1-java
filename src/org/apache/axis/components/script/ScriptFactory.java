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

package org.apache.axis.components.script;

import org.apache.axis.AxisProperties;
import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

/**
 * This class implements a factory to instantiate an Script component.
 * @author <a href="mailto:dims@yahoo.com">Davanum Srinivas</a>
 * @version $Revision$ $Date$
 * @since 2.0
 */
public class ScriptFactory {
    protected static Log log =
            LogFactory.getLog(ScriptFactory.class.getName());

    static {
        AxisProperties.setClassOverrideProperty(Script.class, "axis.Script");

        AxisProperties.setClassDefaults(Script.class,
                new String[]{
                    "org.apache.axis.components.script.BSF",
                });
    }

    /**
     * Get the Script implementation. 
     */
    public static Script getScript() {
        Script script = (Script) AxisProperties.newInstance(Script.class);
        log.debug("axis.Script: " + script.getClass().getName());
        return script;
    }
}


