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

import org.apache.axis.Constants;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerImpl;
import org.apache.axis.encoding.DeserializerTarget;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.utils.JavaUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;

import java.util.HashMap;
import java.util.Map;

/*
 * A <code>MapSerializer</code> is be used to deserialize
 * deserialize Maps using the <code>SOAP-ENC</code>
 * encoding style.<p>
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
 * Modified by @author Rich scheuerle <scheu@us.ibm.com>
 */
public class MapDeserializer extends DeserializerImpl {

    protected static Log log =
        LogFactory.getLog(MapDeserializer.class.getName());

    // Fixed objects to act as hints to the set() callback
    public static final Object KEYHINT = new Object();
    public static final Object VALHINT = new Object();
    public static final Object NILHINT = new Object();        


    /**
     * This method is invoked after startElement when the element requires
     * deserialization (i.e. the element is not an href and the value is not nil.)
     * 
     * Simply creates map.
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
            log.debug("Enter MapDeserializer::startElement()");
        }
        
        if (attributes.getValue(Constants.URI_DEFAULT_SCHEMA_XSI,  "nil") != null) {
            return;
        }
        
        // Create a hashmap to hold the deserialized values.
        setValue(new HashMap());
        
        if (log.isDebugEnabled()) {
            log.debug("Exit: MapDeserializer::startElement()");
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
            log.debug("Enter: MapDeserializer::onStartChild()");
        }

        SOAPHandler sh = new ItemHandler(this);
        
        if (log.isDebugEnabled()) {
            log.debug("Exit: MapDeserializer::onStartChild()");
        }

        return sh;
    }
    
    /**
     * The registerValueTarget code above causes this set function to be invoked when
     * each value is known.
     * @param value is the value of an element
     * @param hint is the key
     */
    public void setValue(Object value, Object hint) throws SAXException
    {
        if (log.isDebugEnabled()) {
            log.debug(JavaUtils.getMessage("gotValue00", "MapDeserializer", "" + value));
        }
        ((Map)this.value).put(hint, value);
    }
    
    /**
     * A deserializer for an <item>.  Handles getting the key and
     * value objects from their own deserializers, and then putting
     * the values into the HashMap we're building.
     * 
     */
    class ItemHandler extends DeserializerImpl {
        Object key;
        Object myValue;
        int numSet = 0;
        MapDeserializer md = null;

        ItemHandler(MapDeserializer md) {
            this.md = md;
        }
        /** 
         * Callback from our deserializers.  The hint indicates
         * whether the passed "val" argument is the key or the value
         * for this mapping.
         */
        public void setValue(Object val, Object hint) throws SAXException 
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
                md.setValue(myValue, key);
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
            Deserializer dser = context.getDeserializerForType(typeQName);

            // If no deserializer, use the base DeserializerImpl.
            if (dser == null)
                dser = new DeserializerImpl();

            // When the child value is ready, we
            // want our set method to be invoked.
            // To do this register a DeserializeTarget on the
            // new Deserializer.
            DeserializerTarget dt = null;
            if (context.isNil(attributes)) {
                dt = new DeserializerTarget(this, NILHINT);
            } else if (localName.equals("key")) {
                dt = new DeserializerTarget(this, KEYHINT);
            } else if (localName.equals("value")) {
                dt = new DeserializerTarget(this, VALHINT);
            } else {
                // Do nothing
            }
            if (dt != null) {
                dser.registerValueTarget(dt);
            }
            return (SOAPHandler)dser;
        }
    }
}
