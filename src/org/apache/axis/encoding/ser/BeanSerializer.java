/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.axis.encoding.ser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.rpc.namespace.QName;
import java.io.IOException;

import org.apache.axis.Constants;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.SerializerFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.DeserializerImpl;
import org.apache.axis.InternalException;
import org.apache.axis.AxisFault;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.description.FieldDesc;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.wsdl.fromJava.ClassRep;
import org.apache.axis.wsdl.fromJava.FieldRep;
import org.apache.axis.wsdl.fromJava.Types;
import org.apache.axis.wsdl.toJava.Utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.beans.IntrospectionException;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ObjectStreamField;
import java.io.Serializable;

import java.util.HashMap;
import java.util.Vector;
import java.util.Iterator;

/**
 * General purpose serializer/deserializerFactory for an arbitrary java bean.
 *
 * @author Sam Ruby <rubys@us.ibm.com>
 * @author Rich Scheuerle <scheu@us.ibm.com>
 * @author Tom Jordahl <tomj@macromedia.com>
 */
public class BeanSerializer implements Serializer, Serializable {

    protected static Log log =
        LogFactory.getLog(BeanSerializer.class.getName());

    public static final Object[] noArgs = new Object[] {};  // For convenience

    QName xmlType;
    Class javaType;

    private BeanPropertyDescriptor[] propertyDescriptor = null;
    private TypeDesc typeDesc = null;


    // Construct BeanSerializer for the indicated class/qname
    public BeanSerializer(Class javaType, QName xmlType) {
        this.xmlType = xmlType;
        this.javaType = javaType;
        propertyDescriptor = getPd(javaType);

        typeDesc = TypeDesc.getTypeDescForClass(javaType);
    }

    /**
     * Serialize a bean.  Done simply by serializing each bean property.
     * @param name is the element name
     * @param attributes are the attributes...serialize is free to add more.
     * @param value is the value
     * @param context is the SerializationContext
     */
    public void serialize(QName name, Attributes attributes,
                          Object value, SerializationContext context)
        throws IOException
    {
        boolean isSOAP_ENC = Constants.
                isSOAP_ENC(context.getMessageContext().getEncodingStyle());

        // Check for meta-data in the bean that will tell us if any of the
        // properties are actually attributes, add those to the element
        // attribute list
        Attributes beanAttrs = getObjectAttributes(value, attributes, context);
        context.startElement(name, beanAttrs);

        try {
            // Serialize each property
            for (int i=0; i<propertyDescriptor.length; i++) {
                String propName = propertyDescriptor[i].getName();
                if (propName.equals("class"))
                    continue;

                QName qname = null;

                // If we have type metadata, check to see what we're doing
                // with this field.  If it's an attribute, skip it.  If it's
                // an element, use whatever qname is in there.  If we can't
                // find any of this info, use the default.

                if (typeDesc != null) {
                    FieldDesc field = typeDesc.getFieldByName(propName);
                    if (field != null) {
                        if (!field.isElement())
                            continue;

                        qname = field.getXmlName();
                    }
                }

                if (qname == null) {
                    // Use the default...
                    propName = propName;
                    qname = new QName("", propName);
                }

                Method readMethod = propertyDescriptor[i].getReadMethod();
                if (readMethod != null && readMethod.getParameterTypes().length == 0) {
                    // Normal case: serialize the value
                    Object propValue = propertyDescriptor[i].getReadMethod().invoke(value,noArgs);
                    context.serialize(qname,
                                      null,
                                      propValue,
                                      propertyDescriptor[i].getReadMethod().getReturnType());
                } else {
                    // Collection of properties: serialize each one
                    int j=0;
                    while(j >= 0) {
                        Object propValue = null;
                        try {
                            propValue = propertyDescriptor[i].getReadMethod().invoke(value,
                                                                     new Object[] { new Integer(j) });
                            j++;
                        } catch (Exception e) {
                            j = -1;
                        }
                        if (j >= 0) {
                            context.serialize(qname, null,
                                              propValue,
                                              propertyDescriptor[i].getReadMethod().getReturnType());
                        }
                    }
                }
            }
        } catch (InvocationTargetException ite) {
            Throwable target = ite.getTargetException();
            log.error(JavaUtils.getMessage("exception00"), target);
            throw new IOException(target.toString());
        } catch (Exception e) {
            log.error(JavaUtils.getMessage("exception00"), e);
            throw new IOException(e.toString());
        }

        context.endElement();
    }

    /**
     * Create a BeanPropertyDescriptor array for the indicated class.
     */
    static BeanPropertyDescriptor[] getPd(Class javaType) {
        BeanPropertyDescriptor[] pd;
        try {
            PropertyDescriptor[] rawPd = Introspector.getBeanInfo(javaType).getPropertyDescriptors();
            pd = BeanPropertyDescriptor.processPropertyDescriptors(rawPd,javaType);
        } catch (Exception e) {
            // this should never happen
            throw new InternalException(e);
        }
        return pd;
    }

    /**
     * Return a list of properties in the bean which should be attributes
     */
    static Vector getBeanAttributes(Class javaType, TypeDesc typeDesc) {
        Vector ret = new Vector();

        if (typeDesc == null) {
            // !!! Support old-style beanAttributeNames for now

            // See if this object defined the 'getAttributeElements' function
            // which returns a Vector of property names that are attributes
            try {
                Method getAttributeElements =
                        javaType.getMethod("getAttributeElements",
                                           new Class [] {});
                // get string array
                String[] array = (String[])getAttributeElements.invoke(null, noArgs);

                // convert it to a Vector
                ret = new Vector(array.length);
                for (int i = 0; i < array.length; i++) {
                    ret.add(array[i]);
                }
            } catch (Exception e) {
                ret.clear();
            }
        } else {
            FieldDesc [] fields = typeDesc.getFields();
            if (fields != null) {
                for (int i = 0; i < fields.length; i++) {
                    FieldDesc field = fields[i];
                    if (!field.isElement()) {
                        ret.add(field.getFieldName());
                    }
                }
            }
        }

        return ret;
    }


    public String getMechanismType() { return Constants.AXIS_SAX; }

    /**
     * Return XML schema for the specified type, suitable for insertion into
     * the <types> element of a WSDL document.
     *
     * @param types the Java2WSDL Types object which holds the context
     *              for the WSDL being generated.
     * @return true if we wrote a schema, false if we didn't.
     * @see org.apache.axis.wsdl.fromJava.Types
     */
    public boolean writeSchema(Types types) throws Exception {

        javax.wsdl.QName qName = types.getWsdlQName(xmlType);

        // ComplexType representation of bean class
        Element complexType = types.createElement("complexType");
        types.writeSchemaElement(qName, complexType);
        complexType.setAttribute("name", qName.getLocalPart());

        // See if there is a super class, stop if we hit a stop class
        Element e = null;
        Class superClass = javaType.getSuperclass();
        Vector stopClasses = types.getStopClasses();
        if (superClass != null &&
                superClass != java.lang.Object.class &&
                (stopClasses == null ||
                !(stopClasses.contains(superClass.getName()))) ) {
            // Write out the super class
            String base = types.writeType(superClass);
            Element complexContent = types.createElement("complexContent");
            complexType.appendChild(complexContent);
            Element extension = types.createElement("extension");
            complexContent.appendChild(extension);
            extension.setAttribute("base", base);
            e = extension;
        } else {
            e = complexType;
        }

        // Add fields under sequence element.
        // Note: In most situations it would be okay
        // to put the fields under an all element.
        // However it is illegal schema to put an
        // element with minOccurs=0 or maxOccurs>1 underneath
        // an all element.  This is the reason why a sequence
        // element is used.
        Element all = types.createElement("sequence");
        e.appendChild(all);

        // Build a ClassRep that represents the bean class.  This
        // allows users to provide their own field mapping.
        ClassRep clsRep = types.getBeanBuilder().build(javaType);

        // Map abstract classes to abstract attribute on complexType
        if (Modifier.isAbstract(clsRep.getModifiers())) {
            complexType.setAttribute("abstract", "true");
        }

        // Write out fields
        Vector fields = clsRep.getFields();
        for (int i=0; i < fields.size(); i++) {
            FieldRep field = (FieldRep) fields.elementAt(i);

            String name = field.getName();

            if (typeDesc != null) {
                FieldDesc fieldDesc = typeDesc.getFieldByName(field.getName());
                if (fieldDesc != null) {
                    if (!fieldDesc.isElement()) {
                        QName attrName = typeDesc.getAttributeNameForField(
                                                    field.getName());
                        writeAttribute(types, attrName.getLocalPart(),
                                       field.getType(),
                                       complexType);
                        continue;
                    } else {
                        QName xmlName = typeDesc.getElementNameForField(
                                field.getName());
                        if (xmlName != null) {
                            if (xmlName.getNamespaceURI() != "") {
                                // Throw an exception until we can emit
                                // schema for this correctly?
                            }
                            name = xmlName.getLocalPart();
                            writeField(types, name, field.getType(),
                                       field.getIndexed(), all);
                            continue;
                        }
                    }
                }
            }

            writeField(types, name, field.getType(), field.getIndexed(), all);
        }
        // done
        return true;
    }

    /**
     * write a schema representation of the given Class field and append it to
     * the where Node, recurse on complex types
     * @param fieldName name of the field
     * @param fieldType type of the field
     * @param isUnbounded causes maxOccurs="unbounded" if set
     * @param where location for the generated schema node
     * @throws Exception
     */
    private void writeField(Types types, String fieldName,
                            Class fieldType,
                            boolean isUnbounded,
                            Element where) throws Exception {
        String elementType = types.writeType(fieldType);
        Element elem = types.createElement(fieldName,
                                           elementType,
                                           types.isNullable(fieldType),
                                           where.getOwnerDocument());
        if (isUnbounded) {
            elem.setAttribute("maxOccurs", "unbounded");
        }
        where.appendChild(elem);
    }

    /**
     * write aa attribute element and append it to the 'where' Node
     * @param fieldName name of the field
     * @param fieldType type of the field
     * @param where location for the generated schema node
     * @throws Exception
     */
    private void writeAttribute(Types types,
                                String fieldName,
                                Class fieldType,
                                Element where) throws Exception {

        // Attribute must be a simple type.
        if (!types.isSimpleSchemaType(fieldType))
            throw new AxisFault(JavaUtils.getMessage("AttrNotSimpleType00",
                                                     fieldName,
                                                     fieldType.getName()));

        String elementType = types.writeType(fieldType);
        Element elem = types.createAttributeElement(fieldName,
                                           elementType,
                                           false,
                                           where.getOwnerDocument());
        where.appendChild(elem);
    }

    /**
     * Check for meta-data in the bean that will tell us if any of the
     * properties are actually attributes, add those to the element
     * attribute list
     *
     * @param value the object we are serializing
     * @param pd the properties of this class
     * @return attributes for this element, null if none
     */
    private Attributes getObjectAttributes(Object value,
                                           Attributes attributes,
                                           SerializationContext context) {

        if (typeDesc == null || !typeDesc.hasAttributes())
            return attributes;

        AttributesImpl attrs;
        if (attributes != null)
            attrs = new AttributesImpl(attributes);
        else
            attrs = new AttributesImpl();

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

                Method readMethod = propertyDescriptor[i].getReadMethod();
                if (readMethod != null &&
                    readMethod.getParameterTypes().length == 0) {
                    // add to our attributes
                    Object propValue = readMethod.invoke(value,noArgs);
                    // If the property value does not exist, don't serialize
                    // the attribute.  In the future, the decision to serializer
                    // the attribute may be more sophisticated.  For example, don't
                    // serialize if the attribute matches the default value.
                    if (propValue != null) {
                        String propString = propValue.toString();
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
}
