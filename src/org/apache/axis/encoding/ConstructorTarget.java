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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXException;
import org.apache.axis.i18n.Messages;


/**
 * Used when the class need a specific Constructor (not default one)
 * @author Florent Benoit
 */
public class ConstructorTarget implements Target {

    /**
     * Constructor to use
     */
    private Constructor constructor = null;
    
    /**
     * Deserializer on which set value
     */
    private Deserializer deSerializer = null;
    
    
    /**
     * List of values
     */
    private List values = null;
    
    public ConstructorTarget(Constructor constructor, Deserializer deSerializer) {
        this.deSerializer = deSerializer;
        this.constructor = constructor;
        values = new ArrayList();
    }
    
    
    /**
     * Instantiate a new class with right constructor
     * @param value value to use on Constructor
     * @throws SAXException on error
     */
    public void set(Object value) throws SAXException {
        try {
            // store received value
            values.add(value);

            // got right parameter length
            if (constructor.getParameterTypes().length == values.size()) {
                // type of parameters
                Class[] classes = constructor.getParameterTypes();
                
                // args array
                Object[] args = new Object[constructor.getParameterTypes().length];
                
                // Get arg for the type of the class
                for (int c = 0; c < classes.length; c++) {
                    boolean found = false;
                    int i = 0;
                    while (!found && i < values.size()) {
                         // got right class arg
                        if (values.get(i).getClass().getName().toLowerCase().indexOf(classes[c].getName().toLowerCase()) != -1) {
                            found = true;
                            args[c] = values.get(i);
                        }
                        i++;

                    }
                    // no suitable object for class required
                    if (!found) {
                        throw new SAXException(Messages.getMessage("cannotFindObjectForClass00", classes[c].toString()));
                    }
                }
                
                // then build object
                Object o = constructor.newInstance(args);
            	deSerializer.setValue(o);
            }
        } catch (Exception e) {
            throw new SAXException(e);
        }

    }

}
