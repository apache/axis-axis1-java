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

import org.xml.sax.SAXException;

// Target is a Deserializer.  The set method invokes one of the setValue methods
// of the deserializer depending on whether a hint was given.  The DeserializerTarget
// is used in situations when the Deserializer is expecting multiple values and cannot
// be considered complete until all values are received.
// (example is an ArrayDeserializer).
public class DeserializerTarget implements Target {
    public Deserializer target;
    public Object hint;
    public DeserializerTarget(Deserializer target, Object hint)
    {
        this.target = target;
        this.hint = hint;
    }
    
    public void set(Object value) throws SAXException {
        if (hint != null) {
            target.setChildValue(value, hint);
        } else {
            target.setValue(value);
        }
    }
}
