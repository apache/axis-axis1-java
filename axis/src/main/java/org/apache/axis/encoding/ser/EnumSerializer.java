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

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.fromJava.Types;
import org.apache.commons.logging.Log;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import javax.xml.namespace.QName;
import java.io.IOException;

/**
 * Serializer for a JAX-RPC enum.
 *
 * @author Rich Scheuerle <scheu@us.ibm.com>
 * @author Sam Ruby <rubys@us.ibm.com>
 */
public class EnumSerializer extends SimpleSerializer
{
    protected static Log log =
        LogFactory.getLog(EnumSerializer.class.getName());

    private java.lang.reflect.Method toStringMethod = null;

    public EnumSerializer(Class javaType, QName xmlType) {
        super(javaType, xmlType);
    }

    /** 
     * Serialize an enumeration
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
        // Invoke the toString method on the enumeration class and
        // write out the result as a string.
        try {
            if (toStringMethod == null) {
                toStringMethod = javaType.getMethod("toString", null);
            }
            return (String) toStringMethod.invoke(value, null);
        } catch (Exception e) {
            log.error(Messages.getMessage("exception00"), e);
        }
        return null;
    }

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
        // Use Types helper method.
        return types.writeEnumType(xmlType, javaType);
    }
}
