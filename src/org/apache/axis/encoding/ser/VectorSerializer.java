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
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.utils.IdentityHashMap;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.fromJava.Types;
import org.apache.commons.logging.Log;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

/**
 * A <code>VectorSerializer</code> is be used to serialize and
 * deserialize Vectors using the <code>SOAP-ENC</code>
 * encoding style.<p>
 *
 *  @author Rich Scheuerle (scheu@us.ibm.com)
 */

public class VectorSerializer implements Serializer
{
    protected static Log log =
        LogFactory.getLog(VectorSerializer.class.getName());

    /** Serialize a Vector
     *
     * Walk the collection of keys, serializing each key/value pair
     * inside an <item> element.
     *
     * @param name the desired QName for the element
     * @param attributes the desired attributes for the element
     * @param value the Object to serialize
     * @param context the SerializationContext in which to do all this
     * @exception IOException
     */
    public void serialize(QName name, Attributes attributes,
                          Object value, SerializationContext context)
        throws IOException
    {
        if (!(value instanceof Vector))
            throw new IOException(
                Messages.getMessage("noVector00", "VectorSerializer", 
                                     value.getClass().getName()));

        Vector vector = (Vector)value;
        
        // Check for circular references. 
        if(isRecursive(new IdentityHashMap(), vector)){
            throw new IOException(Messages.getMessage("badVector00"));
        }
        
        context.startElement(name, attributes);
        for (Iterator i = vector.iterator(); i.hasNext(); )
        {
            Object item = i.next();
            context.serialize(Constants.QNAME_LITERAL_ITEM,  null, item);
        }
        context.endElement();
    }

    public boolean isRecursive(IdentityHashMap map, Vector vector) 
    {
        map.add(vector);
        boolean recursive = false;
        for(int i=0;i<vector.size() && !recursive;i++)
        {
            Object o = vector.get(i);
            if(o instanceof Vector) {
                if(map.containsKey(o)) {
                    return true;
                } else { 
                    recursive = isRecursive(map, (Vector)o);
                }
            }
        }
        return recursive;
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
        Element complexType = types.createElement("complexType");
        complexType.setAttribute("name", "Vector");
        types.writeSchemaTypeDecl(Constants.SOAP_VECTOR, complexType);
        Element seq = types.createElement("sequence");
        complexType.appendChild(seq);

        Element element = types.createElement("element");
        element.setAttribute("name", "item");
        element.setAttribute("minOccurs", "0");
        element.setAttribute("maxOccurs", "unbounded");
        element.setAttribute("type", "xsd:anyType");
        seq.appendChild(element);

        return complexType;
    }
}
