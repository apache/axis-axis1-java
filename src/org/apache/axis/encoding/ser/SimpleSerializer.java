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

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.description.FieldDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.SimpleType;
import org.apache.axis.encoding.SimpleValueSerializer;
import org.apache.axis.utils.BeanPropertyDescriptor;
import org.apache.axis.utils.BeanUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.namespace.QName;
import java.io.IOException;

/**
 * Serializer for primitives and anything simple whose value is obtained with toString()
 *
 * @author Rich Scheuerle <dims@yahoo.com>
 */
public class SimpleSerializer implements SimpleValueSerializer {
    public QName xmlType;
    public Class javaType;

    private BeanPropertyDescriptor[] propertyDescriptor = null;
    private TypeDesc typeDesc = null;

    public SimpleSerializer(Class javaType, QName xmlType) {
        this.xmlType = xmlType;
        this.javaType = javaType;
        init();
    }
    public SimpleSerializer(Class javaType, QName xmlType, TypeDesc typeDesc) {
        this.xmlType = xmlType;
        this.javaType = javaType;
        this.typeDesc = typeDesc;
        init();
    }

   /**
    * Initialize the typeDesc and propertyDescriptor array.
    */
    private void init() {
        // The typeDesc and propertyDescriptor array are only necessary
        // if this class extends SimpleType.
        if (SimpleType.class.isAssignableFrom(javaType)) {
            // Set the typeDesc if not already set
            if (typeDesc == null) {
                typeDesc = TypeDesc.getTypeDescForClass(javaType);
            }
            // Get the cached propertyDescriptor from the type or
            // generate a fresh one.
            if (typeDesc != null) {
                propertyDescriptor = typeDesc.getPropertyDescriptors();
            } else {
                propertyDescriptor = BeanUtils.getPd(javaType, null);
            }
        }
    }

    /**
     * Serialize a primitive or simple value.
     * If the object to serialize is a primitive, the Object value below
     * is the associated java.lang class.
     * To determine if the original value is a java.lang class or a primitive, consult
     * the javaType class.
     */
    public void serialize(QName name, Attributes attributes,
                          Object value, SerializationContext context)
        throws IOException
    {
        if (value != null && value.getClass() == java.lang.Object.class) {
            throw new IOException(Messages.getMessage("cantSerialize02"));
        }

        // get any attributes
        if (value instanceof SimpleType)
            attributes = getObjectAttributes(value, attributes, context);

        String valueStr = null;
        if (value != null) {
            valueStr = getValueAsString(value, context);
        }
        context.startElement(name, attributes);
        if (valueStr != null) {
            context.writeSafeString(valueStr);
        }
        context.endElement();
    }

    public String getValueAsString(Object value, SerializationContext context) {
        // We could have separate serializers/deserializers to take
        // care of Float/Double cases, but it makes more sence to
        // put them here with the rest of the java lang primitives.
        if (value instanceof Float ||
            value instanceof Double) {
            double data = 0.0;
            if (value instanceof Float) {
                data = ((Float) value).doubleValue();
            } else {
                data = ((Double) value).doubleValue();
            }
            if (Double.isNaN(data)) {
                return "NaN";
            } else if (data == Double.POSITIVE_INFINITY) {
                return "INF";
            } else if (data == Double.NEGATIVE_INFINITY) {
                return "-INF";
            }
        } else if (value instanceof QName) {
            return context.qName2String((QName)value);
        }

        return value.toString();
    }

    private Attributes getObjectAttributes(Object value,
                                           Attributes attributes,
                                           SerializationContext context) {
        if (typeDesc == null || !typeDesc.hasAttributes())
            return attributes;

        AttributesImpl attrs;
        if (attributes == null) {
            attrs = new AttributesImpl();
        } else if (attributes instanceof AttributesImpl) {
            attrs = (AttributesImpl)attributes;
        } else {
            attrs = new AttributesImpl(attributes);
        }

        try {
            // Find each property that is an attribute
            // and add it to our attribute list
            for (int i=0; i<propertyDescriptor.length; i++) {
                String propName = propertyDescriptor[i].getName();
                if (propName.equals("class"))
                    continue;

                FieldDesc field = typeDesc.getFieldByName(propName);
                // skip it if its not an attribute
                if (field == null || field.isElement())
                    continue;

                QName qname = field.getXmlName();
                if (qname == null) {
                    qname = new QName("", propName);
                }

                if (propertyDescriptor[i].isReadable() &&
                    !propertyDescriptor[i].isIndexed()) {
                    // add to our attributes
                    Object propValue = propertyDescriptor[i].get(value);
                    // If the property value does not exist, don't serialize
                    // the attribute.  In the future, the decision to serializer
                    // the attribute may be more sophisticated.  For example, don't
                    // serialize if the attribute matches the default value.
                    if (propValue != null) {
                        String propString = getValueAsString(propValue, context);

                        String namespace = qname.getNamespaceURI();
                        String localName = qname.getLocalPart();

                        attrs.addAttribute(namespace,
                                           localName,
                                           context.qName2String(qname),
                                           "CDATA",
                                           propString);
                    }
                }
            }
        } catch (Exception e) {
            // no attributes
            return attrs;
        }

        return attrs;
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
        // Let the caller generate WSDL if this is not a SimpleType
        if (!SimpleType.class.isAssignableFrom(javaType))
            return null;

        // ComplexType representation of SimpleType bean class
        Element complexType = types.createElement("complexType");
        types.writeSchemaTypeDecl(xmlType, complexType);
        complexType.setAttribute("name", xmlType.getLocalPart());

        // Produce simpleContent extending base type.
        Element simpleContent = types.createElement("simpleContent");
        complexType.appendChild(simpleContent);
        Element extension = types.createElement("extension");
        simpleContent.appendChild(extension);

        // Get the base type from the "value" element of the bean
        String base = "string";
        for (int i=0; i<propertyDescriptor.length; i++) {
            String propName = propertyDescriptor[i].getName();
            if (!propName.equals("value")) {
                if (typeDesc != null) {
                    FieldDesc field = typeDesc.getFieldByName(propName);
                    if (field != null) {
                        if (field.isElement()) {
                            // throw?
                        }
                        QName qname = field.getXmlName();
                        if (qname == null) {
                            // Use the default...
                            qname = new QName("", propName);
                        }

                        //  write attribute element
                        Class fieldType = propertyDescriptor[i].getType();

                        // Attribute must be a simple type, enum or SimpleType
                        if (!types.isAcceptableAsAttribute(fieldType)) {
                            throw new AxisFault(Messages.getMessage("AttrNotSimpleType00",
                                    propName,
                                    fieldType.getName()));
                        }

                        // write attribute element
                        // TODO the attribute name needs to be preserved from the XML
                        Element elem = types.createAttributeElement(propName,
                                fieldType,
                                field.getXmlType(),
                                false,
                                extension.getOwnerDocument());
                        extension.appendChild(elem);
                    }
                }
                continue;
            }

            BeanPropertyDescriptor bpd = propertyDescriptor[i];
            Class type = bpd.getType();
            // Attribute must extend a simple type, enum or SimpleType
            if (!types.isAcceptableAsAttribute(type)) {
                throw new AxisFault(Messages.getMessage("AttrNotSimpleType01",
                        type.getName()));
            }
            base = types.writeType(type);
            extension.setAttribute("base", base);
        }

        // done
        return complexType;

    }
}
