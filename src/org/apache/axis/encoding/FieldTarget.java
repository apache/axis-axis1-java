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

import java.lang.reflect.Field;


// Target is a field.  The set method places the value in the field.
public class FieldTarget implements Target
{
    protected static Log log =
        LogFactory.getLog(FieldTarget.class.getName());

    private Object targetObject;
    private Field targetField;
    
    public FieldTarget(Object targetObject, Field targetField)
    {
        this.targetObject = targetObject;
        this.targetField = targetField;
    }
    
    public FieldTarget(Object targetObject, String fieldName)
        throws NoSuchFieldException
    {
        Class cls = targetObject.getClass();
        targetField = cls.getField(fieldName);
        this.targetObject = targetObject;
    }
    
    public void set(Object value) throws SAXException {
        try {
            targetField.set(targetObject, value);
        } catch (IllegalAccessException accEx) {
            log.error(Messages.getMessage("illegalAccessException00"),
                      accEx);
            throw new SAXException(accEx);
        } catch (IllegalArgumentException argEx) {
            log.error(Messages.getMessage("illegalArgumentException00"),
                      argEx);
            throw new SAXException(argEx);
        }
    }
}
