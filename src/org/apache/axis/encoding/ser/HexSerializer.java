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

import org.apache.axis.Constants;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.SimpleValueSerializer;
import org.apache.axis.types.HexBinary;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import javax.xml.namespace.QName;
import java.io.IOException;
/**
 * Serializer for hexBinary.
 *
 * @author Davanum Srinivas <dims@yahoo.com>
 * Modified by @author Rich scheuerle <scheu@us.ibm.com>
 * @see <a href="http://www.w3.org/TR/xmlschema-2/#hexBinary">XML Schema 3.2.16</a>
 */
public class HexSerializer implements SimpleValueSerializer {

    public QName xmlType;
    public Class javaType;
    public HexSerializer(Class javaType, QName xmlType) {
        this.xmlType = xmlType;
        this.javaType = javaType;
    }

    /**
     * Serialize a HexBinary quantity.
     */
    public void serialize(QName name, Attributes attributes,
                          Object value, SerializationContext context)
        throws IOException
    {
        context.startElement(name, attributes);
        context.writeString(getValueAsString(value, context));
        context.endElement();
    }

    public String getValueAsString(Object value, SerializationContext context) {
        value = JavaUtils.convert(value, javaType);
        if (javaType == HexBinary.class) {
            return value.toString();
        } else {
            return HexBinary.encode((byte[]) value);
        }
    }

    public String getMechanismType() { return Constants.AXIS_SAX; }

    /**
     * Return XML schema for the specified type, suitable for insertion into
     * the &lt;types&gt; element of a WSDL document, or underneath an
     * &lt;element&gt; or &lt;attribute&gt; declaration.
     *
     * @param javaType the Java Class we're writing out schema for
     * @param types the Java2WSDL Types object which holds the context
     *              for the WSDL being generated.
     * @return a type element containing a schema simpleType/complexType
     * @see org.apache.axis.wsdl.fromJava.Types
     */
    public Element writeSchema(Class javaType, Types types) throws Exception {
        return null;
    }
}
