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

import org.apache.axis.MessageContext;
import org.apache.axis.Constants;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerImpl;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.utils.BeanPropertyDescriptor;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.BeanUtils;

import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
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

    public static final String DESERIALIZE_ANY = "DeserializeAny";
    
    /** Type metadata about this class for XML deserialization */
    protected TypeDesc typeDesc = null;

    // This counter is updated to deal with deserialize collection properties
    protected int collectionIndex = -1;

    // Construct BeanSerializer for the indicated class/qname
    public BeanDeserializer(Class javaType, QName xmlType) {
        this(javaType, xmlType, TypeDesc.getTypeDescForClass(javaType));
    }

    // Construct BeanDeserializer for the indicated class/qname and meta Data
    public BeanDeserializer(Class javaType, QName xmlType, TypeDesc typeDesc ) {
        this.xmlType = xmlType;
        this.javaType = javaType;
        this.typeDesc = typeDesc;
        // Get a list of the bean properties
        BeanPropertyDescriptor[] pd = null;
        if (typeDesc != null) {
            propertyMap = typeDesc.getPropertyDescriptorMap();
        } else {
            pd = BeanUtils.getPd(javaType, null);
            propertyMap = new HashMap();
            // loop through properties and grab the names for later
            for (int i = 0; i < pd.length; i++) {
                BeanPropertyDescriptor descriptor = pd[i];
                propertyMap.put(descriptor.getName(), descriptor);
            }
        }

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
     * @param qName is the prefixed qName of the element
     * @param attributes are the attributes on the element...used to get the type
     * @param context is the DeserializationContext
     */
    public void startElement(String namespace, String localName,
                             String qName, Attributes attributes,
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
                           qName, attributes, context);
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
        
        QName elemQName = new QName(namespace, localName);
        // The collectionIndex needs to be reset for Beans with multiple arrays
        if ((prevQName == null) || (!prevQName.equals(elemQName))) {
            prevQName = elemQName;
            collectionIndex = -1;
        }  
        prevQName = elemQName;

        if (typeDesc != null) {       
            
            // First lookup the field using the target namespace context
            // and local name.  If this fails and the incoming element
            // name is not prefixed, lookup the name assuming an unqualified
            // name.
            String fieldName = typeDesc.getFieldNameForElement(elemQName, 
                                                               false);
            if (fieldName == null && (prefix == null || prefix.equals(""))) {
                fieldName = 
                    typeDesc.getFieldNameForElement(
                      new QName("", elemQName.getLocalPart()), false);
            }

            propDesc = (BeanPropertyDescriptor)propertyMap.get(fieldName);
        }

        if (propDesc == null) {
            // look for a field by this name.
            propDesc = (BeanPropertyDescriptor) propertyMap.get(localName);
        }

        // try and see if this is an xsd:any namespace="##any" element before reporting a problem
        QName qn = null;
        Deserializer dSer = null;
        MessageContext messageContext = context.getMessageContext();
        if (propDesc == null && !messageContext.isEncoded()) {
            // try to put unknown elements into an Object property
            propDesc = getObjectPropertyDesc(elemQName, context);
            if (propDesc != null) {
                //Boolean deserializeAny =  (Boolean) messageContext.getProperty(DESERIALIZE_ANY);
                Boolean deserializeAny = Boolean.TRUE;
                if ((deserializeAny != null) && (deserializeAny.booleanValue())) {
                  dSer = context.getDeserializerForType(elemQName);
                }
                if (dSer == null)  {
                    qn = Constants.XSD_ANY;
                    // make sure that the Element Deserializer deserializes the current element and not the child
                    messageContext.setProperty(ElementDeserializer.DESERIALIZE_CURRENT_ELEMENT, Boolean.TRUE);
                } else {
                    qn = elemQName;
                }
            }
        }


        if (propDesc == null) {
            // No such field
            throw new SAXException(
                    JavaUtils.getMessage("badElem00", javaType.getName(), 
                                         localName));
        }

        // Determine the QName for this child element.
        // Look at the type attribute specified.  If this fails,
        // use the javaType of the property to get the type qname.
        if (qn == null) {
            qn = context.getTypeFromAttributes(namespace, localName, attributes);
        }
        
        // get the deserializer
        if (dSer == null) {
            dSer = context.getDeserializerForType(qn);
        }
        
        // If no deserializer, use the base DeserializerImpl.
        // There may not be enough information yet to choose the
        // specific deserializer.
        if (dSer == null) {
            dSer = new DeserializerImpl();
            // determine a default type for this child element
            TypeMapping tm = context.getTypeMapping();
            Class type = propDesc.getType();
            dSer.setDefaultType(tm.getTypeQName(type));
        }
                
        if (propDesc.isWriteable()) {
            if (!propDesc.isIndexed()) {
                // Success!  Register the target and deserializer.
                collectionIndex = -1;
                dSer.registerValueTarget(
                                         new BeanPropertyTarget(value, propDesc));
            } else {
                // Success! This is a collection of properties so use the index
                collectionIndex++;
                dSer.registerValueTarget(
                                         new BeanPropertyTarget(value, 
                                            propDesc, 
                                            collectionIndex));
            }
        }
        return (SOAPHandler)dSer;
    }

     public BeanPropertyDescriptor getObjectPropertyDesc(QName qname, DeserializationContext context) {
        for (Iterator iterator = propertyMap.values().iterator(); iterator.hasNext();) {
            BeanPropertyDescriptor propertyDesc = (BeanPropertyDescriptor) iterator.next();
            // try to find xsd:any namespace="##any" property
            if (propertyDesc.getName().equals("any") && propertyDesc.getType().getName().equals("java.lang.Object")) {
                return propertyDesc;
            }
        }
        return null;
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
                
                // Success!  Create an object from the string and set
                // it in the bean
                try {
                    dSer.onStartElement(namespace, localName, qName, attributes, context);
                    Object val = ((SimpleDeserializer)dSer).
                        makeValue(attributes.getValue(i));
                    bpd.set(value, val);
                } catch (Exception e) {
                    throw new SAXException(e);
                }
                
            } // if
        } // attribute loop
    }
}
