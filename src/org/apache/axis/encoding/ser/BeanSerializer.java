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
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.wsdl.fromJava.ClassRep;
import org.apache.axis.wsdl.fromJava.FieldRep;
import org.apache.axis.wsdl.fromJava.Types;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.lang.reflect.Method;
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

    private static final Object[] noArgs = new Object[] {};  // For convenience

    // When serializing, the property element names passed over the wire
    // are the names of the properties (format=PROPERTY_NAME).
    // Setting the format to FORCE_UPPER will cause the
    // serializer to uppercase the first letter of the property element name.
    // Setting the format to FORCE_LOWER will cause the
    // serializer to uppercase the first letter of the property element name.
    private short elementPropertyFormat = PROPERTY_NAME;
    public static short PROPERTY_NAME = 0;
    public static short FORCE_UPPER   = 1;
    public static short FORCE_LOWER   = 2;

    QName xmlType;
    Class javaType;

    private BeanPropertyDescriptor[] propertyDescriptor = null;
    private Vector beanAttributeNames = null;
    

    // Construct BeanSerializer for the indicated class/qname
    public BeanSerializer(Class javaType, QName xmlType) {
        this.xmlType = xmlType;
        this.javaType = javaType;
        propertyDescriptor = getPd(javaType);
        beanAttributeNames = getBeanAttributes(javaType);
    }

    // Construct BeanSerializer for the indicated class/qname and format
    public BeanSerializer(Class javaType, QName xmlType, short format) {
        this.xmlType = xmlType;
        this.javaType = javaType;
        setElementPropertyFormat(format);
        propertyDescriptor = getPd(javaType);
        beanAttributeNames = getBeanAttributes(javaType);
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
        Attributes beanAttrs = getObjectAttributes(value, attributes);
        context.startElement(name, beanAttrs);

        try {
            // Serialize each property
            for (int i=0; i<propertyDescriptor.length; i++) {
                String propName = propertyDescriptor[i].getName();
                if (propName.equals("class")) 
                    continue;
                //if (!isSOAP_ENC && beanAttributeNames.contains(propName)) 
                if (beanAttributeNames.contains(propName)) 
                    continue;
                propName = format(propName, elementPropertyFormat);

                Method readMethod = propertyDescriptor[i].getReadMethod();
                if (readMethod != null && readMethod.getParameterTypes().length == 0) {
                    // Normal case: serialize the value
                    Object propValue = propertyDescriptor[i].getReadMethod().invoke(value,noArgs);
                    context.serialize(new QName("", propName), null,
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
                            context.serialize(new QName("", propName), null,
                                              propValue,
                                              propertyDescriptor[i].getReadMethod().getReturnType());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Exception: transforming to IOException: ", e);
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
    static Vector getBeanAttributes(Class javaType) {
        // See if this object defined the 'getAttributeElements' function
        // which returns a Vector of property names that are attributes
        try {
            Method getAttributeElements = 
                    javaType.getMethod("getAttributeElements",
                                       new Class [] {});
            // get string array
            String[] array = (String[])getAttributeElements.invoke(null, noArgs);

            // convert it to a Vector
            Vector v = new Vector(array.length);
            for (int i = 0; i < array.length; i++) {
                v.add(array[i]);
            }
            return v;
        } catch (Exception e) {
            return new Vector();  // empty vector
        }
    }
    
    
    /**
     * Get the format of the elements for the properties
     */
    public short getElementPropertyFormat() {
        return elementPropertyFormat;
    }
    /**
     * Set the format of the elements for the properties
     */
    public void setElementPropertyFormat(short format) {
        if (format > FORCE_LOWER ||
            format < PROPERTY_NAME)
            format = PROPERTY_NAME;
        elementPropertyFormat = format;
    }
    /**
     * Returns the property name string formatted in the specified manner
     * @param name to format
     * @param fmt (PROPERTY_NAME, FORCE_LOWER, FORCE_UPPER)
     * @return formatted name
     */
    static String format(String name, short fmt) {
        if (fmt == PROPERTY_NAME)
            return name;
        String theRest = "";
        if (name.length() > 1)
            theRest = name.substring(1);
        if (fmt == FORCE_UPPER)
            return Character.toUpperCase(name.charAt(0)) + theRest;
        else
            return Character.toLowerCase(name.charAt(0)) + theRest;
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

        // Add fields under all element
        Element all = types.createElement("all");
        e.appendChild(all);

        // Build a ClassRep that represents the bean class.  This
        // allows users to provide their own field mapping.
        ClassRep clsRep = types.getBeanBuilder().build(javaType);

        // Write out fields
        Vector fields = clsRep.getFields();
        for (int i=0; i < fields.size(); i++) {
            FieldRep field = (FieldRep) fields.elementAt(i);

            // if bean fields are attributes, write attribute element
            if (beanAttributeNames.contains(field.getName()))
                writeAttribute(types, field.getName(),
                               field.getType(), 
                               complexType);
            else            
                writeField(types, field.getName(), 
                           field.getType(), 
                           field.getIndexed(), 
                           all);
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
                                           Attributes attributes) {
        
        if (beanAttributeNames.isEmpty())
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
                // skip it if its not in the list
                if (!beanAttributeNames.contains(propName)) continue;
                if (propName.equals("class")) continue;
                propName = format(propName, elementPropertyFormat);
                
                Method readMethod = propertyDescriptor[i].getReadMethod();
                if (readMethod != null && 
                    readMethod.getParameterTypes().length == 0) {
                    // add to our attributes
                    Object propValue = propertyDescriptor[i].
                                        getReadMethod().invoke(value,noArgs);
                    // NOTE: we will always set the attribute here to something, 
                    // which we may not want (i.e. if null, omit it)
                    String propString = propValue != null ? propValue.toString() : "";
                    attrs.addAttribute("", propName, propName, "CDATA", propString);
                }
            }
        } catch (Exception e) {
            // no attributes
            return attrs;
        }
        
        return attrs;
    }
}
