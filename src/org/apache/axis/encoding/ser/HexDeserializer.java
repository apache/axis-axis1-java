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

import org.apache.axis.types.HexBinary;

import javax.xml.namespace.QName;

/**
 * Deserializer for hexBinary.
 *
 * @author Davanum Srinivas <dims@yahoo.com>
 * Modified by @author Rich scheuerle <scheu@us.ibm.com>
 * @see <a href="http://www.w3.org/TR/xmlschema-2/#hexBinary">XML Schema 3.2.16</a>
 */
public class HexDeserializer extends SimpleDeserializer {

    public HexDeserializer(Class javaType, QName xmlType) {
        super(javaType, xmlType);
    }

    /**
     * Convert the string that has been accumulated into an Object.  Subclasses
     * may override this.  Note that if the javaType is a primitive, the returned
     * object is a wrapper class.
     * @param source the serialized value to be deserialized
     * @throws Exception any exception thrown by this method will be wrapped
     */
    public Object makeValue(String source) throws Exception {
        Object result;
        if (javaType == byte[].class) {
            result = HexBinary.decode(source);
        } else {
            result = new HexBinary(source);
        }
        if (result == null) result = new HexBinary("");
        return result;
    }
}
