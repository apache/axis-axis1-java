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

package org.apache.axis.encoding;

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.xml.sax.SAXException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


// Target is set via a method call.  The set method places the value in the field.
public class MethodTarget implements Target
{
    protected static Log log =
        LogFactory.getLog(MethodTarget.class.getName());

    private Object targetObject;
    private Method targetMethod;
    private static final Class [] objArg = new Class [] { Object.class };

    /**
     * Construct a target whose value is set via a method
     * @param targetObject is the object containing the value to be set
     * @param targetMethod is the Method used to set the value
     */
    public MethodTarget(Object targetObject, Method targetMethod)
    {
        this.targetObject = targetObject;
        this.targetMethod = targetMethod;
    }

    /**
     * Construct a target whose value is set via a method
     * @param targetObject is the object containing the value to be set
     * @param methodName is the name of the Method
     */
    public MethodTarget(Object targetObject, String methodName)
        throws NoSuchMethodException
    {
        this.targetObject = targetObject;
        Class cls = targetObject.getClass();
        targetMethod = cls.getMethod(methodName, objArg);
    }
    
    /**
     * Set the target's value by invoking the targetMethod.
     * @param value is the new Object value
     */
    public void set(Object value) throws SAXException {
        try {
            targetMethod.invoke(targetObject, new Object [] { value });
        } catch (IllegalAccessException accEx) {
            log.error(Messages.getMessage("illegalAccessException00"),
                      accEx);
            throw new SAXException(accEx);
        } catch (IllegalArgumentException argEx) {
            log.error(Messages.getMessage("illegalArgumentException00"),
                      argEx);
            throw new SAXException(argEx);
        } catch (InvocationTargetException targetEx) {
            log.error(Messages.getMessage("invocationTargetException00"),
                      targetEx);
            throw new SAXException(targetEx);
        }
    }
}
