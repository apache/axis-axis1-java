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

package org.apache.axis.encoding;

import org.apache.axis.Constants;
import org.apache.axis.message.SOAPHandler;
import org.apache.log4j.Category;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.rpc.namespace.QName;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A <code>MapSerializer</code> is be used to serialize and
 * deserialize Maps using the <code>SOAP-ENC</code>
 * encoding style.<p>
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class MapSerializer extends Deserializer implements Serializer
{
    static Category category =
            Category.getInstance(MapSerializer.class.getName());

    // QNames we deal with
    private static final QName QNAME_KEY = new QName("","key");
    private static final QName QNAME_ITEM = new QName("", "item");
    private static final QName QNAME_VALUE = new QName("", "value");

    // Fixed objects to act as hints to the valueReady() callback
    public static final Object KEYHINT = new Object();
    public static final Object VALHINT = new Object();
    public static final Object NILHINT = new Object();
    
    // Our static deserializer factory
    public static class Factory implements DeserializerFactory {
        public Deserializer getDeserializer(Class cls) {
            return new MapSerializer();
        }
    }
    public static DeserializerFactory factory = new Factory();

    /** Serialize a Map
     * 
     * Walk the collection of keys, serializing each key/value pair
     * inside an <item> element.
     * 
     * @param name the desired QName for the element
     * @param attributes the desired attributes for the element
     * @param value the Object to serialize
     * @param context the SerializationContext in which to do all this
     * @exception IOException
     */
    public void serialize(QName name, Attributes attributes,
                          Object value, SerializationContext context)
        throws IOException
    {
        if (!(value instanceof Map))
            throw new IOException("MapSerializer: " +
                 value.getClass().getName() + " is not a Map");
        
        Map map = (Map)value;
        
        context.startElement(name, attributes);

        for (Iterator i = map.keySet().iterator(); i.hasNext(); )
        {
            Object key = i.next();
            Object val = map.get(key);

            context.startElement(QNAME_ITEM, null);

            context.serialize(QNAME_KEY, null, key);
            context.serialize(QNAME_VALUE, null, val);

            context.endElement();
        }

        context.endElement();
    }
    
    /** A deserializer for an <item>.  Handles getting the key and
     * value objects from their own deserializers, and then putting
     * the values into the HashMap we're building.
     * 
     */
    class ItemHandler extends Deserializer implements ValueReceiver {
        Object key;
        Object myValue;
        int numSet = 0;
        
        /** Callback from our deserializers.  The hint indicates
         * whether the passed "val" argument is the key or the value
         * for this mapping.
         */
        public void valueReady(Object val, Object hint)
        {
            if (hint == KEYHINT) {
                key = val;
            } else if (hint == VALHINT) {
                myValue = val;
            } else if (hint != NILHINT) {
                return;
            }
            numSet++;
            if (numSet == 2)
                doPut(key, myValue);
        }
        
        public SOAPHandler onStartChild(String namespace,
                                    String localName,
                                    String prefix,
                                    Attributes attributes,
                                    DeserializationContext context)
        throws SAXException
        {
            QName typeQName = context.getTypeFromAttributes(namespace,
                                                            localName,
                                                            attributes);
            Deserializer dser = context.getTypeMappingRegistry().
                                 getDeserializer(typeQName);
            if (dser == null)
                dser = new Deserializer();

            String isNil = attributes.getValue(Constants.URI_2001_SCHEMA_XSI, "nil");
            
            if (isNil != null && isNil.equals("true")) {
                dser.registerCallback(this, NILHINT);
            } else if (localName.equals("key")) {
                dser.registerCallback(this, KEYHINT);
            } else if (localName.equals("value")) {
                dser.registerCallback(this, VALHINT);
            } else {
                // Do nothing
            }
            
            return dser;
        }
    }
    
    public void onStartElement(String namespace, String localName,
                             String qName, Attributes attributes,
                             DeserializationContext context)
        throws SAXException
    {
        if (category.isDebugEnabled()) {
            category.debug("In MapSerializer.startElement()");
        }
        
        value = new HashMap();
        
        if (category.isDebugEnabled()) {
            category.debug("Out MapSerializer.startElement()");
        }
    }
    
    public SOAPHandler onStartChild(String namespace,
                                    String localName,
                                    String prefix,
                                    Attributes attributes,
                                    DeserializationContext context)
        throws SAXException
    {
        if (category.isDebugEnabled()) {
            category.debug("In MapSerializer.onStartChild()");
        }

        if (!localName.equals("item"))
            throw new SAXException("Only 'item' elements are allowed in a Map!");
        
        return new ItemHandler();
    }
        
    protected void doPut(Object key, Object value)
    {
        ((Map)this.value).put(key, value);
    }
}
