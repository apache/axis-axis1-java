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

package org.apache.axis.components.compiler;

import org.apache.axis.AxisProperties;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;


/**
 * This class implements a factory to instantiate a Compiler.
 * @author <a href="mailto:dims@yahoo.com">Davanum Srinivas</a>
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @version $Revision: 1.10 $ $Date: 2002/07/02 18:07:35 $
 * @since 2.0
 */
public class CompilerFactory {
    protected static Log log =
        LogFactory.getLog(CompilerFactory.class.getName());

    static {
        AxisProperties.setClassOverrideProperty(Compiler.class, "axis.Compiler");

        AxisProperties.setClassDefault(Compiler.class,
                                       "org.apache.axis.components.compiler.Javac");
    }

    public static Compiler getCompiler() {
        Compiler compiler = (Compiler)AxisProperties.newInstance(Compiler.class);
        
        /**
         * This shouldn't be needed, but seems to be a common feel-good:
         */
        if (compiler == null) {
            log.debug(Messages.getMessage("defaultCompiler"));
            compiler = new Javac();
        }

        log.debug("axis.Compiler:" + compiler.getClass().getName());

        return compiler;
    }
}
