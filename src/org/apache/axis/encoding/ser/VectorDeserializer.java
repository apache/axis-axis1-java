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

import java.util.Vector;

import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.SerializerFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerTarget;
import org.apache.axis.encoding.DeserializerFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.DeserializerImpl;

import org.apache.axis.utils.JavaUtils;
import org.apache.axis.Constants;
import org.apache.axis.message.SOAPHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Deserializer for SOAP Vectors for compatibility with SOAP 2.2.
 *
 * @author Carsten Ziegeler (cziegeler@apache.org)
 * Modified by @author Rich scheuerle <scheu@us.ibm.com>
 */
public class VectorDeserializer extends DeserializerImpl implements Deserializer  {

    static Log log =
            LogFactory.getLog(VectorDeserializer.class.getName());

    public int curIndex = 0;

    /**
     * This method is invoked after startElement when the element requires
     * deserialization (i.e. the element is not an href and the value is not nil.)
     * 
     * Simply creates 
     * @param namespace is the namespace of the element
     * @param localName is the name of the element
     * @param qName is the prefixed qname of the element
     * @param attributes are the attributes on the element...used to get the type
     * @param context is the DeserializationContext
     */
    public void onStartElement(String namespace, String localName,
                               String qName, Attributes attributes,
                               DeserializationContext context)
        throws SAXException {
        if (log.isDebugEnabled()) {
            log.debug(JavaUtils.getMessage("enter00", "VectorDeserializer.startElement()"));
        }
        
        if (attributes.getValue(Constants.URI_CURRENT_SCHEMA_XSI,  "nil") != null) {
            return;
        }
        
        // Create a vector to hold the deserialized values.
        setValue(new java.util.Vector());
        
        if (log.isDebugEnabled()) {
            log.debug(JavaUtils.getMessage("exit00", "VectorDeserializer.startElement()"));
        }
    }
    
    /**
     * onStartChild is called on each child element.
     *
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
        throws SAXException {
        if (log.isDebugEnabled()) {
            log.debug(JavaUtils.getMessage("enter00", "VectorDeserializer.onStartChild()"));
        }
        
        if (attributes == null)
            throw new SAXException(JavaUtils.getMessage("noType01"));

        // If the xsi:nil attribute, set the value to null and return since
        // there is nothing to deserialize.
        if (context.isNil(attributes)) {
            setValue(null, new Integer(curIndex++));
            return null;
        }

        // Get the type
        QName itemType = context.getTypeFromAttributes(namespace,
                                                       localName,
                                                       attributes);
        if (itemType == null)
            throw new SAXException(JavaUtils.getMessage("noType01"));
        
        // Get the deserializer
        Deserializer dSer = context.getDeserializerForType(itemType);
        if (dSer == null)
            throw new SAXException(JavaUtils.getMessage("noType01"));

        // When the value is deserialized, inform us.
        // Need to pass the index because multi-ref stuff may 
        // result in the values being deserialized in a different order.
        dSer.registerValueTarget(new DeserializerTarget(this, new Integer(curIndex)));
        curIndex++;

        if (log.isDebugEnabled()) {
            log.debug(JavaUtils.getMessage("exit00", "VectorDeserializer.onStartChild()"));
        }
        return (SOAPHandler) dSer;
    }
    
    /**
     * The registerValueTarget code above causes this set function to be invoked when
     * each value is known.
     * @param value is the value of an element
     * @param hint is an Integer containing the index
     */
    public void setValue(Object value, Object hint) throws SAXException
    {
        if (log.isDebugEnabled()) {
            log.debug(JavaUtils.getMessage("gotValue00", "VectorDeserializer", "" + value));
        }
        int offset = ((Integer)hint).intValue();
        Vector v = (Vector)this.value;
        
        // If the vector is too small, grow it 
        if (offset >= v.size()) {
            v.setSize(offset+1);
        }
        v.setElementAt(value, offset);
    }
}
