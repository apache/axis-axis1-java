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
import java.util.Vector;
import java.lang.reflect.Method;

import org.apache.axis.Constants;
import org.apache.axis.wsdl.fromJava.Types;
import org.apache.axis.wsdl.toJava.Utils;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.XMLUtils;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.SerializerFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.DeserializerImpl;
import org.apache.axis.encoding.SimpleType;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
/**
 * Serializer for primitives and anything simple whose value is obtained with toString()
 *
 * @author Rich Scheuerle <dims@yahoo.com>
 */
public class SimpleSerializer implements Serializer {

    public QName xmlType;
    public Class javaType;
    public SimpleSerializer(Class javaType, QName xmlType) {
        this.xmlType = xmlType;
        this.javaType = javaType;
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
            throw new IOException(JavaUtils.getMessage("cantSerialize02"));
        }

        // get any attributes
        if (value instanceof SimpleType)
            attributes = getObjectAttributes(value, attributes);
        
        context.startElement(name, attributes);
        if (value != null) {
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
                if (data == Double.NaN) {
                    context.writeString("NaN");
                } else if (data == Double.POSITIVE_INFINITY) {
                    context.writeString("INF");
                } else if (data == Double.NEGATIVE_INFINITY) {
                    context.writeString("-INF");
                } else {
                    context.writeString(value.toString());
                }
            } else if (value instanceof String) {
                context.writeString(
                                    XMLUtils.xmlEncodeString(value.toString()));
            } else if (value instanceof SimpleType) {
                context.writeString(value.toString());
            } else {
                context.writeString(value.toString());
            }
        }
        context.endElement();
    }

    private Attributes getObjectAttributes(Object value, Attributes attributes) {
        
        // get the list of attributes from the bean
        Vector beanAttributeNames = 
                BeanSerializer.getBeanAttributes(value.getClass());

        // if nothing, return
        if (beanAttributeNames.isEmpty())
            return attributes;
        
        AttributesImpl attrs;
        if (attributes != null)
            attrs = new AttributesImpl(attributes);
        else
            attrs = new AttributesImpl();
        
        BeanPropertyDescriptor propertyDescriptor[] = 
                BeanSerializer.getPd(value.getClass());
        
        try {
            // Find each property that is an attribute 
            // and add it to our attribute list
            for (int i=0; i<propertyDescriptor.length; i++) {
                String propName = propertyDescriptor[i].getName();
                if (propName.equals("class"))
                    continue;
                // skip it if its not in the list
                if (!beanAttributeNames.contains(Utils.xmlNameToJava(propName)))
                    continue;
                
                Method readMethod = propertyDescriptor[i].getReadMethod();
                if (readMethod != null && 
                        readMethod.getParameterTypes().length == 0) {
                    // add to our attributes
                    Object propValue = propertyDescriptor[i].
                            getReadMethod().invoke(value, new Object[]{});
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
        return false;
    }
}
