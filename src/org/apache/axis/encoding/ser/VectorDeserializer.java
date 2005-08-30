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

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerImpl;
import org.apache.axis.encoding.DeserializerTarget;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import java.util.Vector;

/**
 * Deserializer for SOAP Vectors for compatibility with SOAP 2.2.
 *
 * @author Carsten Ziegeler (cziegeler@apache.org)
 * Modified by @author Rich scheuerle <scheu@us.ibm.com>
 */
public class VectorDeserializer extends DeserializerImpl
{
    protected static Log log =
        LogFactory.getLog(VectorDeserializer.class.getName());

    public int curIndex = 0;

    /**
     * This method is invoked after startElement when the element requires
     * deserialization (i.e. the element is not an href and the value is not nil.)
     * 
     * Simply creates 
     * @param namespace is the namespace of the element
     * @param localName is the name of the element
     * @param prefix is the prefix of the element
     * @param attributes are the attributes on the element...used to get the type
     * @param context is the DeserializationContext
     */
    public void onStartElement(String namespace, String localName,
                               String prefix, Attributes attributes,
                               DeserializationContext context)
        throws SAXException {
        if (log.isDebugEnabled()) {
            log.debug("Enter: VectorDeserializer::startElement()");
        }
        
        if (context.isNil(attributes)) { 
            return;
        }
        
        // Create a vector to hold the deserialized values.
        setValue(new java.util.Vector());
        
        if (log.isDebugEnabled()) {
            log.debug("Exit: VectorDeserializer::startElement()");
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
            log.debug("Enter: VectorDeserializer::onStartChild()");
        }
        
        if (attributes == null)
            throw new SAXException(Messages.getMessage("noType01"));

        // If the xsi:nil attribute, set the value to null and return since
        // there is nothing to deserialize.
        if (context.isNil(attributes)) {
            setChildValue(null, new Integer(curIndex++));
            return null;
        }

        // Get the type
        QName itemType = context.getTypeFromAttributes(namespace,
                                                       localName,
                                                       attributes);
        // Get the deserializer
        Deserializer dSer = null;
        if (itemType != null) {
           dSer = context.getDeserializerForType(itemType);
        }
        if (dSer == null) {
            dSer = new DeserializerImpl();
        }

        // When the value is deserialized, inform us.
        // Need to pass the index because multi-ref stuff may 
        // result in the values being deserialized in a different order.
        dSer.registerValueTarget(new DeserializerTarget(this, new Integer(curIndex)));
        curIndex++;

        if (log.isDebugEnabled()) {
            log.debug("Exit: VectorDeserializer::onStartChild()");
        }
        
        // Let the framework know that we aren't complete until this guy
        // is complete.
        addChildDeserializer(dSer);
        
        return (SOAPHandler)dSer;
    }
    
    /**
     * The registerValueTarget code above causes this set function to be invoked when
     * each value is known.
     * @param value is the value of an element
     * @param hint is an Integer containing the index
     */
    public void setChildValue(Object value, Object hint) throws SAXException
    {
        if (log.isDebugEnabled()) {
            log.debug(Messages.getMessage("gotValue00", "VectorDeserializer", "" + value));
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
