/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2002 The Apache Software Foundation.  All rights
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

import org.apache.axis.Constants;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerImpl;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.message.MessageElement;
import org.apache.axis.utils.BeanPropertyDescriptor;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.wsdl.symbolTable.SchemaUtils;

import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;

import java.io.Serializable;
import java.util.Map;

/**
 * General purpose deserializer for an arbitrary java bean.
 *
 * @author Sam Ruby <rubys@us.ibm.com>
 * @author Rich Scheuerle <scheu@us.ibm.com>
 * @author Tom Jordahl <tomj@macromedia.com>
 */
public class BeanDeserializer extends DeserializerImpl implements Serializable
{
    protected static Log log =
        LogFactory.getLog(BeanDeserializer.class.getName());

    QName xmlType;
    Class javaType;
    protected Map propertyMap = null;
    protected QName prevQName;

    /** Type metadata about this class for XML deserialization */
    protected TypeDesc typeDesc = null;

    // This counter is updated to deal with deserialize collection properties
    protected int collectionIndex = -1;

    protected SimpleDeserializer cacheStringDSer = null;
    protected QName cacheXMLType = null;

    // Construct BeanSerializer for the indicated class/qname
    public BeanDeserializer(Class javaType, QName xmlType) {
        this(javaType, xmlType, TypeDesc.getTypeDescForClass(javaType));
    }

    // Construct BeanDeserializer for the indicated class/qname and meta Data
    public BeanDeserializer(Class javaType, QName xmlType, TypeDesc typeDesc ) {
        this(javaType, xmlType, typeDesc,
             BeanDeserializerFactory.getProperties(javaType, typeDesc));
    }

    // Construct BeanDeserializer for the indicated class/qname and meta Data
    public BeanDeserializer(Class javaType, QName xmlType, TypeDesc typeDesc,
                            Map propertyMap ) {
        this.xmlType = xmlType;
        this.javaType = javaType;
        this.typeDesc = typeDesc;
        this.propertyMap = propertyMap;

        // create a value
        try {
            value=javaType.newInstance();
        } catch (Exception e) {
            // Don't process the exception at this point.
            // This is defered until the call to startElement
            // which will throw the exception.
        }
    }

    /**
     * startElement
     * 
     * The ONLY reason that this method is overridden is so that
     * the object value can be set or a reasonable exception is thrown
     * indicating that the object cannot be created.  This is done
     * at this point so that it occurs BEFORE href/id processing.
     * @param namespace is the namespace of the element
     * @param localName is the name of the element
     * @param prefix is the prefix of the element
     * @param attributes are the attributes on the element...used to get the
     *                   type
     * @param context is the DeserializationContext
     */
    public void startElement(String namespace, String localName,
                             String prefix, Attributes attributes,
                             DeserializationContext context)
        throws SAXException
    {
        // Create the bean object if it was not already
        // created in the constructor.
        if (value == null) {
            try {
                value=javaType.newInstance();
            } catch (Exception e) {
                // Failed to create an object.
                throw new SAXException(JavaUtils.getMessage("cantCreateBean00", 
                                                            javaType.getName(), 
                                                            e.toString()));
            }
        }
        // Invoke super.startElement to do the href/id processing.
        super.startElement(namespace, localName, 
                           prefix, attributes, context);
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
        BeanPropertyDescriptor propDesc = null;

        String encodingStyle = context.getMessageContext().getEncodingStyle();
        boolean isEncoded = Constants.isSOAP_ENC(encodingStyle);

        QName elemQName = new QName(namespace, localName);
        // The collectionIndex needs to be reset for Beans with multiple arrays
        if ((prevQName == null) || (!prevQName.equals(elemQName))) {
            collectionIndex = -1;
        }  
        prevQName = elemQName;

        if (typeDesc != null) {       
            // Lookup the name appropriately (assuming an unqualified
            // name for SOAP encoding, using the namespace otherwise)
            String fieldName = typeDesc.getFieldNameForElement(elemQName, 
                                                               isEncoded);
            propDesc = (BeanPropertyDescriptor)propertyMap.get(fieldName);
        }

        if (propDesc == null) {
            // look for a field by this name.
            propDesc = (BeanPropertyDescriptor) propertyMap.get(localName);
        }

        // try and see if this is an xsd:any namespace="##any" element before
        // reporting a problem
        if (propDesc == null) {
            // try to put unknown elements into a SOAPElement property, if
            // appropriate
            propDesc = getAnyPropertyDesc();
            if (propDesc != null) {
                try {
                    MessageElement [] curElements = (MessageElement[])propDesc.get(value);
                    int length = 0;
                    if (curElements != null) {
                        length = curElements.length;
                    }
                    MessageElement [] newElements = new MessageElement[length + 1];
                    if (curElements != null) {
                        System.arraycopy(curElements, 0,
                                         newElements, 0, length);
                    }
                    MessageElement thisEl = context.getCurElement();

                    newElements[length] = thisEl;
                    propDesc.set(value, newElements);

                    return new SOAPHandler();
                } catch (Exception e) {
                    throw new SAXException(e);
                }
            }
        }


        if (propDesc == null) {
            // No such field
            throw new SAXException(
                    JavaUtils.getMessage("badElem00", javaType.getName(), 
                                         localName));
        }

        // Get the child's xsi:type if available
        QName childXMLType = context.getTypeFromXSITypeAttr(namespace, 
                                                            localName,
                                                            attributes);
        
        // Get Deserializer for child, default to using DeserializerImpl
        Deserializer dSer = getDeserializer(childXMLType, propDesc.getType(), 
                                            attributes.getValue("href"),
                                            context);
        // It is an error if the dSer is not found, the base
        // deserializer impl is returned so that it can generate the correct message.
        if (dSer == null) {
            dSer = new DeserializerImpl();
            return (SOAPHandler)dSer;
        }

        // Register value target
        if (propDesc.isWriteable()) {
            // If this is an indexed property, and the deserializer we found
            // was NOT the ArrayDeserializer, this is a non-SOAP array:
            // <bean>
            //   <field>value1</field>
            //   <field>value2</field>
            // ...
            // In this case, we want to use the collectionIndex and make sure
            // the deserialized value for the child element goes into the
            // right place in the collection.
            if (propDesc.isIndexed() && !(dSer instanceof ArrayDeserializer)) {
                    collectionIndex++;
                    dSer.registerValueTarget(new BeanPropertyTarget(value,
                                                    propDesc, collectionIndex));
            } else {
                // If we're here, the element maps to a single field value,
                // whether that be a "basic" type or an array, so use the
                // normal (non-indexed) BeanPropertyTarget form.
                collectionIndex = -1;
                dSer.registerValueTarget(new BeanPropertyTarget(value,
                                                                propDesc));
            }
        }
        return (SOAPHandler)dSer;
    }

    /**
     * Get a BeanPropertyDescriptor which indicates where we should
     * put extensibility elements (i.e. XML which falls under the
     * auspices of an &lt;xsd:any&gt; declaration in the schema)
     *
     * @return an appropriate BeanPropertyDescriptor, or null
     */
    public BeanPropertyDescriptor getAnyPropertyDesc() {
        if (typeDesc == null)
            return null;

        return typeDesc.getAnyDesc();
    }

    /**
     * Set the bean properties that correspond to element attributes.
     * 
     * This method is invoked after startElement when the element requires
     * deserialization (i.e. the element is not an href and the value is not
     * nil.)
     * @param namespace is the namespace of the element
     * @param localName is the name of the element
     * @param prefix is the prefix of the element
     * @param attributes are the attributes on the element...used to get the
     *                   type
     * @param context is the DeserializationContext
     */
    public void onStartElement(String namespace, String localName,
                               String prefix, Attributes attributes,
                               DeserializationContext context)
            throws SAXException {

        // The value should have been created or assigned already.
        // This code may no longer be needed.
        if (value == null) {
            // create a value
            try {
                value=javaType.newInstance();
            } catch (Exception e) {
                throw new SAXException(JavaUtils.getMessage("cantCreateBean00", 
                                                            javaType.getName(), 
                                                            e.toString()));
            }
        }

        // If no type description meta data, there are no attributes,
        // so we are done.
        if (typeDesc == null)
            return;

        // loop through the attributes and set bean properties that 
        // correspond to attributes
        for (int i=0; i < attributes.getLength(); i++) {
            QName attrQName = new QName(attributes.getURI(i),
                                        attributes.getLocalName(i));
            String fieldName = typeDesc.getFieldNameForAttribute(attrQName);
            if (fieldName == null)
                continue;

            // look for the attribute property
            BeanPropertyDescriptor bpd =
                    (BeanPropertyDescriptor) propertyMap.get(fieldName);
            if (bpd != null) {
                if (!bpd.isWriteable() || bpd.isIndexed() ) continue ;
                
                // Get the Deserializer for the attribute
                Deserializer dSer = getDeserializer(null, bpd.getType(), 
                                                    null, context);

                if (dSer == null)
                    throw new SAXException(
                            JavaUtils.getMessage("unregistered00",
                                                 bpd.getType().toString()));
                
                if (! (dSer instanceof SimpleDeserializer))
                    throw new SAXException(
                            JavaUtils.getMessage("AttrNotSimpleType00", 
                                                 bpd.getName(), 
                                                 bpd.getType().toString()));
                
                // Success!  Create an object from the string and set
                // it in the bean
                try {
                    dSer.onStartElement(namespace, localName, prefix,
                                        attributes, context);
                    Object val = ((SimpleDeserializer)dSer).
                        makeValue(attributes.getValue(i));
                    bpd.set(value, val);
                } catch (Exception e) {
                    throw new SAXException(e);
                }
                
            } // if
        } // attribute loop
    }

    /**
     * Get the Deserializer for the attribute or child element.
     * @param xmlType QName of the attribute/child element or null if not known.
     * @param javaType Class of the corresponding property
     * @param href String is the value of the href attribute, which is used
     *             to determine whether the child element is complete or an 
     *             href to another element.
     * @param context DeserializationContext
     * @return Deserializer or null if not found.
    */
    protected Deserializer getDeserializer(QName xmlType, 
                                           Class javaType, 
                                           String href,
                                           DeserializationContext context) {
        // See if we have a cached deserializer
        if (cacheStringDSer != null) {
            if (String.class.equals(javaType) &&
                (cacheXMLType == null && xmlType == null ||
                 cacheXMLType != null && cacheXMLType.equals(xmlType))) {
                cacheStringDSer.reset();
                return cacheStringDSer;
            }
        }
        
        Deserializer dSer = null;

        if (xmlType != null) {
            // Use the xmlType to get the deserializer.
            dSer = context.getDeserializerForType(xmlType);
        } else {
            // If the xmlType is not set, get a default xmlType
            TypeMapping tm = context.getTypeMapping();
            QName defaultXMLType = tm.getTypeQName(javaType);
            // If there is not href, then get the deserializer
            // using the javaType and default XMLType,
            // If there is an href, the create the generic
            // DeserializerImpl and set its default type (the
            // default type is used if the href'd element does 
            // not have an xsi:type.
            if (href == null) {
                dSer = context.getDeserializer(javaType, defaultXMLType);
            } else {
                dSer = new DeserializerImpl();
                dSer.setDefaultType(defaultXMLType);
            }
        }
        if (javaType.equals(String.class) &&
            dSer instanceof SimpleDeserializer) {
            cacheStringDSer = (SimpleDeserializer) dSer;
            cacheXMLType = xmlType;
        }
        return dSer;
    }
}
