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

import org.apache.axis.encoding.DeserializationContext;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;

/**
 * The DateSerializer deserializes a Date.  Much of the work is done in the 
 * base class.                                               
 *
 * @author Sam Ruby (rubys@us.ibm.com)
 * Modified for JAX-RPC @author Rich Scheuerle (scheu@us.ibm.com)
 */
public class QNameDeserializer extends SimpleDeserializer {

    private DeserializationContext context = null;

    /**
     * The Deserializer is constructed with the xmlType and 
     * javaType
     */
    public QNameDeserializer(Class javaType, QName xmlType) {
        super(javaType, xmlType);
    } // ctor

    /**
     * The simple deserializer provides most of the stuff.
     * We just need to override makeValue().
     */
    public Object makeValue(String source) {
        int colon = source.lastIndexOf(":");
        String namespace = colon < 0 ? "" :
                context.getNamespaceURI(source.substring(0, colon));
        String localPart = colon < 0 ? source : source.substring(colon + 1);
        return new QName(namespace, localPart);
    } // makeValue

    public void onStartElement(String namespace, String localName,
                               String prefix, Attributes attributes,
                               DeserializationContext context)
            throws SAXException 
    {
        this.context = context;
    } // onStartElement
}
