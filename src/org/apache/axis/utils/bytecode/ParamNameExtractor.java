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
package org.apache.axis.utils.bytecode;

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 *  This class retieves function parameter names from bytecode built with
 * debugging symbols.  Used as a last resort when creating WSDL.
 *
 * @author <a href="mailto:tomj@macromedia.com">Tom Jordahl</a>
 */
public class ParamNameExtractor {

    protected static Log log =
            LogFactory.getLog(ParamNameExtractor.class.getName());

    /**
     * Retrieve a list of function parameter names from a method
     * Returns null if unable to read parameter names (i.e. bytecode not
     * built with debug).
     */
    public static String[] getParameterNamesFromDebugInfo(Method method) {
        // Don't worry about it if there are no params.
        int numParams = method.getParameterTypes().length;
        if (numParams == 0)
            return null;

        // get declaring class
        Class c = method.getDeclaringClass();
        
        // Don't worry about it if the class is a Java dynamic proxy 
        if(Proxy.isProxyClass(c)) {
            return null;
        }
        
        try {
            // get a parameter reader
            ParamReader pr = new ParamReader(c);
            // get the paramter names
            String[] names = pr.getParameterNames(method);
            return names;
        } catch (IOException e) {
            // log it and leave
            log.info(Messages.getMessage("error00") + ":" + e);
            return null;
        }
    }
}