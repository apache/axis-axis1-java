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

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.description.FieldDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.AttributeSerializationContextImpl;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.utils.BeanPropertyDescriptor;
import org.apache.axis.utils.BeanUtils;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.wsdl.fromJava.Types;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.namespace.QName;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;

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

    QName xmlType;
    Class javaType;

    protected BeanPropertyDescriptor[] propertyDescriptor = null;
    protected TypeDesc typeDesc = null;


    // Construct BeanSerializer for the indicated class/qname
    public BeanSerializer(Class javaType, QName xmlType) {
        this(javaType, xmlType, TypeDesc.getTypeDescForClass(javaType));
    }

    // Construct BeanSerializer for the indicated class/qname
    public BeanSerializer(Class javaType, QName xmlType, TypeDesc typeDesc) {
        this.xmlType = xmlType;
        this.javaType = javaType;
        this.typeDesc = typeDesc;
        propertyDescriptor = BeanUtils.getPd(javaType, typeDesc);
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
        // Check for meta-data in the bean that will tell us if any of the
        // properties are actually attributes, add those to the element
        // attribute list
        Attributes beanAttrs = getObjectAttributes(value, attributes, context);

        // Get the encoding style
        String encodingStyle = context.getMessageContext().getEncodingStyle();
        
        // check whether we have and xsd:any namespace="##any" type
        boolean suppressElement = !context.getMessageContext().isEncoded() &&
                                  name.getNamespaceURI().equals("") && 
                                  name.getLocalPart().equals("any");

        if (!suppressElement) 
            context.startElement(name, beanAttrs);

        try {
            // Serialize each property
            for (int i=0; i<propertyDescriptor.length; i++) {
                String propName = propertyDescriptor[i].getName();
                if (propName.equals("class"))
                    continue;
                QName qname = null;
                boolean isOmittable = false;

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
                        isOmittable = field.isMinOccursIs0();
                    }
                }

                if (qname == null) {
                    // Use the default...
                    propName = propName;
                    qname = new QName("", propName);
                }

                // Read the value from the property
                if(propertyDescriptor[i].isReadable()) {
                    Class baseJavaType = propertyDescriptor[i].getType();
                    Class javaType;
                    if (!propertyDescriptor[i].isIndexed()) {
                        // Normal case: serialize the value
                        Object propValue = 
                            propertyDescriptor[i].get(value);
                        // if meta data says minOccurs=0, then we can skip
                        // it if its value is null and we aren't doing SOAP
                        // encoding.
                        if (propValue == null && 
                                isOmittable &&
                                !Constants.isSOAP_ENC(encodingStyle))
                            continue;
                        
                        javaType = (propValue == null || 
                                    baseJavaType.isPrimitive())
                            ? baseJavaType : propValue.getClass();
                        context.serialize(qname,
                                          null,
                                          propValue, javaType);
                    } else {
                        // Collection of properties: serialize each one
                        int j=0;
                        while(j >= 0) {
                            Object propValue = null;
                            try {
                                propValue = 
                                    propertyDescriptor[i].get(value, j);
                                j++;
                            } catch (Exception e) {
                                j = -1;
                            }
                            if (j >= 0) {
                                javaType = (propValue == null || 
                                            baseJavaType.isPrimitive())
                                    ? baseJavaType : propValue.getClass();
                                context.serialize(qname, null,
                                                  propValue, javaType);
                            }
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

        if (!suppressElement) 
            context.endElement();
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

        // ComplexType representation of bean class
        Element complexType = types.createElement("complexType");
        types.writeSchemaElement(xmlType, complexType);
        complexType.setAttribute("name", xmlType.getLocalPart());

        // See if there is a super class, stop if we hit a stop class
        Element e = null;
        Class superClass = javaType.getSuperclass();
        BeanPropertyDescriptor[] superPd = null;
        List stopClasses = types.getStopClasses();
        if (superClass != null &&
                superClass != java.lang.Object.class &&
                superClass != java.lang.Exception.class &&
                superClass != org.apache.axis.AxisFault.class &&
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
            // Get the property descriptors for the super class
            superPd =
                BeanUtils.getPd(superClass,
                                TypeDesc.getTypeDescForClass(superClass));
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

        if (Modifier.isAbstract(javaType.getModifiers())) {
            complexType.setAttribute("abstract", "true");
        }
        // Serialize each property
        for (int i=0; i<propertyDescriptor.length; i++) {
            String propName = propertyDescriptor[i].getName();

            // Don't serializer properties named class
            boolean writeProperty = true;
            if (propName.equals("class")) {
                writeProperty = false;
            }

            // Don't serialize the property if it is present
            // in the super class property list
            if (superPd != null && writeProperty) {
                for (int j=0; j<superPd.length && writeProperty; j++) {
                    if (propName.equals(superPd[j].getName())) {
                        writeProperty = false;
                    }
                }
            }            
            if (!writeProperty) {
                continue;
            }                

            // If we have type metadata, check to see what we're doing
            // with this field.  If it's an attribute, skip it.  If it's
            // an element, use whatever qname is in there.  If we can't
            // find any of this info, use the default.

            if (typeDesc != null) {
                FieldDesc field = typeDesc.getFieldByName(propName);
                if (field != null) {
                    QName qname = field.getXmlName();
                    if (qname != null) {
                        // FIXME!
                        // Check to see if this is in the right namespace -
                        // if it's not, we need to use an <element ref="">
                        // to represent it!!!

                        // Use the default...
                        propName = qname.getLocalPart();
                    }
                    if (!field.isElement()) {
                        writeAttribute(types,
                                       propName,
                                       propertyDescriptor[i].getType(),
                                       complexType);
                    } else {
                        writeField(types,
                                   propName,
                                   propertyDescriptor[i].getType(),
                                   propertyDescriptor[i].isIndexed(), 
                                   field.isMinOccursIs0(), 
                                   all);
                    }
                } else {
                    writeField(types,
                               propName,
                               propertyDescriptor[i].getType(),
                               propertyDescriptor[i].isIndexed(), false, all);
                }
            } else {
                writeField(types,
                           propName,
                           propertyDescriptor[i].getType(),
                           propertyDescriptor[i].isIndexed(), false, all);
            }
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
    protected void writeField(Types types, String fieldName,
                            Class fieldType,
                            boolean isUnbounded,
                            boolean isOmittable, Element where) throws Exception {
        String elementType = types.writeType(fieldType);
        Element elem = types.createElement(fieldName,
                                           elementType,
                                           types.isNullable(fieldType),
                                           isOmittable,
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
    protected void writeAttribute(Types types,
                                String fieldName,
                                Class fieldType,
                                Element where) throws Exception {

        // Attribute must be a simple type.
        if (!types.isAcceptableAsAttribute(fieldType)) {
            throw new AxisFault(JavaUtils.getMessage("AttrNotSimpleType00",
                                                     fieldName,
                                                     fieldType.getName()));
        }
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
     * @return attributes for this element, null if none
     */
    protected Attributes getObjectAttributes(Object value,
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

                if (propertyDescriptor[i].isReadable() && 
                    !propertyDescriptor[i].isIndexed()) {
                    // add to our attributes
                    Object propValue = propertyDescriptor[i].get(value);
                    // If the property value does not exist, don't serialize
                    // the attribute.  In the future, the decision to serializer
                    // the attribute may be more sophisticated.  For example, don't
                    // serialize if the attribute matches the default value.
                    if (propValue != null) {
                        setAttributeProperty(propValue, qname, attrs, context);
                    } 
                }
            }
        } catch (Exception e) {
            // no attributes
            return attrs;
        }

        return attrs;
    }

    private void setAttributeProperty(Object propValue, 
                                      QName qname, 
                                      AttributesImpl attrs, 
                                      SerializationContext context) throws Exception {
        StringWriter writer = new StringWriter();
        SerializationContext attributeContext = new AttributeSerializationContextImpl(writer, context);
        attributeContext.serialize(qname,
                                   null,
                                   propValue, propValue.getClass());
        writer.close();
        String propString = writer.getBuffer().toString();
        String namespace = qname.getNamespaceURI();
        String localName = qname.getLocalPart();

        attrs.addAttribute(namespace,
                           localName,
                           context.attributeQName2String(qname),
                           "CDATA",
                           propString);
    } 
}
