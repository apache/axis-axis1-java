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
import org.apache.axis.message.SOAPHandler;

import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.SerializerFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.DeserializerImpl;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.Constants;

import java.beans.IntrospectionException;

import java.lang.reflect.Method;
import java.beans.IntrospectionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.Vector;
import java.util.HashMap;

/**
 * General purpose deserializer for an arbitrary java bean.
 *
 * @author Sam Ruby <rubys@us.ibm.com>
 * @author Rich Scheuerle <scheu@us.ibm.com>
 * @author Tom Jordahl <tomj@macromedia.com>
 */
public class BeanDeserializer extends DeserializerImpl implements Deserializer, Serializable
{
    protected static Log log =
        LogFactory.getLog(BeanDeserializer.class.getName());

    QName xmlType;
    Class javaType;
    private BeanPropertyDescriptor[] pd = null;
    private HashMap propertyMap = new HashMap();

    // This counter is updated to deal with deserialize collection properties
    protected int collectionIndex = -1;

    // Construct BeanSerializer for the indicated class/qname
    public BeanDeserializer(Class javaType, QName xmlType) {
        this.xmlType = xmlType;
        this.javaType = javaType;
        // Get a list of the bean properties
        this.pd = BeanSerializer.getPd(javaType);
        // loop through properties and grab the names for later
        for (int i = 0; i < pd.length; i++) {
            BeanPropertyDescriptor descriptor = pd[i];
            propertyMap.put(descriptor.getName(), descriptor);
        }
        // create a value
        try {
            value=javaType.newInstance();
        } catch (Exception e) {
            //throw new SAXException(e.toString());
        }
        
    }

    /**
     * Deserializer interface called on each child element encountered in
     * the XML stream.
     * @param namespace is the namespace of the child element
     * @param localName is the local name of the child element
     * @param prefix is the prefix used on the name of the child element
     * @param attributes are the attributes of the child element
     * @param context is the deserialization context.
     * @return is a Deserializer to use to deserialize a child (must be
     * a derived class of SOAPHandler) or null if no deserialization should
     * be performed.
     */
    public SOAPHandler onStartChild(String namespace,
                                    String localName,
                                    String prefix,
                                    Attributes attributes,
                                    DeserializationContext context)
        throws SAXException
    {

        // look for a field by this name.  Assumes the the number of
        // properties in a bean is (relatively) small, so uses a linear
        // search.  Accept a property if it differs only by the 
        // capitalization of the first character.
        String localNameUp = BeanSerializer.format(localName, BeanSerializer.FORCE_UPPER);
        String localNameLo = BeanSerializer.format(localName, BeanSerializer.FORCE_LOWER);
        String mangledName = JavaUtils.xmlNameToJava(localName);
        for (int i=0; i<pd.length; i++) {
            if (pd[i].getWriteMethod() == null ) continue ;
            if (pd[i].getName().equals(localNameUp) ||
                pd[i].getName().equals(localNameLo) ||
                pd[i].getName().equals(mangledName)) {

                // determine the QName for this child element
                TypeMapping tm = context.getTypeMapping();
                Class type = pd[i].getType();
                QName qn = tm.getTypeQName(type);
                if (qn == null)
                    throw new SAXException(
                            JavaUtils.getMessage("unregistered00", "" + type));

                // get the deserializer
                Deserializer dSer = context.getDeserializerForType(qn);
                if (dSer == null)
                    throw new SAXException(
                            JavaUtils.getMessage("noDeser00", "" + type));

                if (pd[i].getWriteMethod().getParameterTypes().length == 1) {
                    // Success!  Register the target and deserializer.
                    collectionIndex = -1;
                    dSer.registerValueTarget(new BeanPropertyTarget(value, pd[i]));
                    return (SOAPHandler) dSer;
                } else {
                    // Success! This is a collection of properties so use the index
                    collectionIndex++;
                    dSer.registerValueTarget(new BeanPropertyTarget(value, pd[i], collectionIndex));
                    return (SOAPHandler) dSer;
                }
                    
            }
        }

        // No such field
        throw new SAXException(
                JavaUtils.getMessage("badElem00", javaType.getName(), localName));
    }

    /**
     * Set the bean properties that correspond to element attributes.
     * 
     * This method is invoked after startElement when the element requires
     * deserialization (i.e. the element is not an href and the value is not nil.)
     * @param namespace is the namespace of the element
     * @param localName is the name of the element
     * @param qName is the prefixed qName of the element
     * @param attributes are the attributes on the element...used to get the type
     * @param context is the DeserializationContext
     */
    public void onStartElement(String namespace, String localName,
                               String qName, Attributes attributes,
                               DeserializationContext context)
            throws SAXException {

        // get list of properties that are really attributes
        Vector beanAttributeNames = BeanSerializer.getBeanAttributes(javaType);
        
        // loop through the attributes and set bean properties that 
        // correspond to attributes
        if (beanAttributeNames != null && 
            beanAttributeNames.size() > 0) {
            for (int i=0; i < attributes.getLength(); i++) {
                String attrName = attributes.getLocalName(i);
                String attrNameUp = BeanSerializer.format(attrName, BeanSerializer.FORCE_UPPER);
                String attrNameLo = BeanSerializer.format(attrName, BeanSerializer.FORCE_LOWER);
                String mangledName = JavaUtils.xmlNameToJava(attrName);

                // See if the attribute is a beanAttribute name
                if (!beanAttributeNames.contains(attrName) &&
                    !beanAttributeNames.contains(attrNameUp) &&
                    !beanAttributeNames.contains(attrNameLo))
                    continue;

                // look for the attribute property
                BeanPropertyDescriptor bpd = 
                    (BeanPropertyDescriptor) propertyMap.get(attrNameUp);
                if (bpd == null)
                    bpd = (BeanPropertyDescriptor) propertyMap.get(attrNameLo);
                if (bpd == null)
                    bpd = (BeanPropertyDescriptor) propertyMap.get(mangledName);
                if (bpd != null) {
                    if (bpd.getWriteMethod() == null ) continue ;
                    
                    // determine the QName for this child element
                    TypeMapping tm = context.getTypeMapping();
                    Class type = bpd.getType();
                    QName qn = tm.getTypeQName(type);
                    if (qn == null)
                        throw new SAXException(
                            JavaUtils.getMessage("unregistered00", type.toString()));
                
                    // get the deserializer
                    Deserializer dSer = context.getDeserializerForType(qn);
                    if (dSer == null)
                        throw new SAXException(
                            JavaUtils.getMessage("noDeser00", type.toString()));
                    if (! (dSer instanceof SimpleDeserializer))
                        throw new SAXException(
                            JavaUtils.getMessage("AttrNotSimpleType00", 
                                                 bpd.getName(), 
                                                 type.toString()));
                
                    if (bpd.getWriteMethod().getParameterTypes().length == 1) {
                        // Success!  Create an object from the string and set
                        // it in the bean
                        try {
                            Object val = ((SimpleDeserializer)dSer).
                                makeValue(attributes.getValue(i));
                            bpd.getWriteMethod().invoke(value, new Object[] {val} );
                        } catch (Exception e) {
                            throw new SAXException(e);
                        }
                    }
                
                } // if
            } // attribute loop
        } // if attributes exist
    } // onStartElement

}
