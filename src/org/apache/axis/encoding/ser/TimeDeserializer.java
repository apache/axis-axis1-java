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

package org.apache.axis.encoding.ser;

import javax.xml.namespace.QName;

import org.apache.axis.types.Time;
/**
 * The TimeSerializer deserializes a time. 
 * Rely on Time of types package
 * @author Florent Benoit
 */
public class TimeDeserializer extends SimpleDeserializer {


    /**
     * The Deserializer is constructed with the xmlType and 
     * javaType
     */
    public TimeDeserializer(Class javaType, QName xmlType) {
        super(javaType, xmlType);
    }

    /**
     * The simple deserializer provides most of the stuff.
     * We just need to override makeValue().
     */
    public Object makeValue(String source) { 
        Time t = new Time(source);
        return t.getAsCalendar();
    }
}
