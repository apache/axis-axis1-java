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


/**
 * A deserializer constructs a value from the xml passed over the wire and
 * sets a target.  The value is set on the target in a number of ways:
 * setting a field, calling a method, setting an indexed property.
 * The Target interface hides the complexity.  The set method is simply
 * invoked with the value.  A class that implements the Target interface
 * needs to supply enough information in the constructor to properly do the
 * set (for example see MethodTarget)
 */
public interface Target         
{
    public void set(Object value) throws SAXException;
}
