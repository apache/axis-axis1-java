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
import org.apache.axis.wsdl.fromJava.ClassRep;
import org.apache.axis.wsdl.fromJava.FieldRep;
import org.apache.axis.wsdl.fromJava.Types;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.beans.IntrospectionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ObjectStreamField;
import java.io.Serializable;

import java.util.HashMap;
import java.util.Vector;

/**
 * General purpose serializer/deserializerFactory for an arbitrary java bean.
 *
 * @author Sam Ruby <rubys@us.ibm.com>
 * @author Rich Scheuerle <scheu@us.ibm.com>
 */
public class BeanSerializer implements Serializer, Serializable {

    static Log log =
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

    // Static Table to store pd[] keyed by class
    private static HashMap pdMap = new HashMap();

    // Construct BeanSerializer for the indicated class/qname
    public BeanSerializer(Class javaType, QName xmlType) {
        this.xmlType = xmlType;
        this.javaType = javaType;
    }

    // Construct BeanSerializer for the indicated class/qname and format
    public BeanSerializer(Class javaType, QName xmlType, short format) {
        this.xmlType = xmlType;
        this.javaType = javaType;
        if (format > FORCE_LOWER ||
            format < PROPERTY_NAME)
            format = PROPERTY_NAME;
        this.elementPropertyFormat = format;
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
        // Get the property descriptors describing the bean properties
        BeanPropertyDescriptor[] pd = getPd(javaType);

        context.startElement(name, attributes);

        try {
            // Serialize each property
            for (int i=0; i<pd.length; i++) {
                String propName = pd[i].getName();
                if (propName.equals("class")) continue;
                propName = format(propName, elementPropertyFormat);

                Method readMethod = pd[i].getReadMethod();
                if (readMethod != null && readMethod.getParameterTypes().length == 0) {
                    // Normal case: serialize the value
                    Object propValue = pd[i].getReadMethod().invoke(value,noArgs);
                    context.serialize(new QName("", propName), null,
                                      propValue,
                                      pd[i].getReadMethod().getReturnType());
                } else {
                    // Collection of properties: serialize each one
                    int j=0;
                    while(j >= 0) {
                        Object propValue = null;
                        try {
                            propValue = pd[i].getReadMethod().invoke(value,
                                                                     new Object[] { new Integer(j) });
                            j++;
                        } catch (Exception e) {
                            j = -1;
                        }
                        if (j >= 0) {
                            context.serialize(new QName("", propName), null,
                                              propValue,
                                              pd[i].getReadMethod().getReturnType());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException(e.toString());
        }

        context.endElement();
    }

    /**
     * Get/Create a BeanPropertyDescriptor array for the indicated class.
     */
    static BeanPropertyDescriptor[] getPd(Class javaType) {
        BeanPropertyDescriptor[] pd = (BeanPropertyDescriptor[]) pdMap.get(javaType);
        if (pd == null) {
            try {
                PropertyDescriptor[] rawPd = Introspector.getBeanInfo(javaType).getPropertyDescriptors();
                pd = BeanPropertyDescriptor.processPropertyDescriptors(rawPd,javaType);
            } catch (Exception e) {
                // this should never happen
                throw new InternalException(e);
            }
        }
        return pd;
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
        types.writeBeanClassType(types.getWsdlQName(xmlType), javaType);
        return true;
    }

}
