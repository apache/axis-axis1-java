package org.apache.axis.encoding;

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

import org.apache.axis.*;
import org.apache.axis.message.*;
import org.apache.axis.utils.*;
import org.xml.sax.*;
import org.xml.sax.helpers.AttributesImpl;
import java.lang.reflect.Array;
import java.io.IOException;

/** An ArraySerializer handles serializing and deserializing SOAP
 * arrays.
 * 
 * Some code borrowed from ApacheSOAP - thanks to Matt Duftler!
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
 */

/** !!! FIRST CUT - INCOMPLETE (doesn't yet do serialization)
 */
public class ArraySerializer extends DeserializerBase
    implements ValueReceiver, Serializer
{
    public static class Factory implements DeserializerFactory {
        public DeserializerBase getDeserializer() {
            return new ArraySerializer();
        }
    }
    public static DeserializerFactory factory = new Factory();
                                                     
    public QName arrayType = null;
    public int curIndex = 0;
    QName arrayItemType;
    
    public void startElement(String namespace, String localName,
                             String qName, Attributes attributes)
        throws SAXException
    {
        super.startElement(namespace, localName, qName, attributes);
        
        QName arrayTypeValue = context.getQNameFromString(
                 attributes.getValue(Constants.URI_SOAP_ENC,
                                     Constants.ATTR_ARRAY_TYPE));
        if (arrayTypeValue == null)
            throw new SAXException("No arrayType attribute for array!");
        
        String arrayTypeValueNamespaceURI = arrayTypeValue.getNamespaceURI();
        String arrayTypeValueLocalPart = arrayTypeValue.getLocalPart();
        int leftBracketIndex = arrayTypeValueLocalPart.lastIndexOf('[');
        int rightBracketIndex = arrayTypeValueLocalPart.lastIndexOf(']');

        if (leftBracketIndex == -1
            || rightBracketIndex == -1
            || rightBracketIndex < leftBracketIndex)
        {
            throw new IllegalArgumentException("Malformed arrayTypeValue '" +
                arrayTypeValue + "'.");
        }

        String componentTypeName =
                    arrayTypeValueLocalPart.substring(0, leftBracketIndex);

        if (componentTypeName.endsWith("]"))
        {
            throw new IllegalArgumentException("Arrays of arrays are not " +
                "supported '" + arrayTypeValue +
                "'.");
        }
        
        arrayItemType = new QName(arrayTypeValueNamespaceURI,
                                  componentTypeName);

        String lengthStr =
                 arrayTypeValueLocalPart.substring(leftBracketIndex + 1,
                                                   rightBracketIndex);
        
        if (lengthStr.length() > 0)
        {
            if (lengthStr.indexOf(',') != -1)
            {
                throw new IllegalArgumentException("Multi-dimensional arrays are " +
                    "not supported '" +
                    lengthStr + "'.");
            }

            try
            {
                int length = Integer.parseInt(lengthStr);
                Class componentType = context.getTypeMappingRegistry().getClassForQName(arrayItemType);
        
                if (componentType == null)
                    throw new SAXException("No component type for " + arrayItemType);
                
                value = Array.newInstance(componentType, length);

            }
            catch (NumberFormatException e)
            {
                throw new IllegalArgumentException("Explicit array length is not a " +
                    "valid integer '" + lengthStr +
                    "'.");
            }
        }
    }
    
    public void onStartChild(String namespace, String localName,
                             String qName, Attributes attributes)
        throws SAXException
    {
        // !!! Check position attribute, type attribute....
        QName itemType = context.getTypeFromAttributes(attributes);
        
        if (itemType != null) {
            if (!arrayItemType.equals(itemType))
                throw new SAXException("Item type (" + itemType + ") didn't match ArrayType (" +
                                        itemType + ")");
        }
        
        DeserializerBase dSer = context.getDeserializer(arrayItemType);
        dSer.registerCallback(this, new Integer(curIndex++));
        context.pushElementHandler(dSer);
    }
    
    public void onEndChild(String localName, DeserializerBase deserializer)
        throws SAXException
    {
    }
    
    public void valueReady(Object value, Object hint)
    {
        Array.set(this.value, ((Integer)hint).intValue(), value);
    }

    public void serialize(QName name, Attributes attributes,
                          Object value, SerializationContext context)
        throws IOException
    {
        if (value == null)
            throw new IOException("Can't serialize null Arrays just yet...");
    
        Class cls = value.getClass();
        if (!cls.isArray())
            throw new IOException("Can't seialize a " + cls.getName() +
                                  " with the ArraySerializer!");
        
        Class componentType = cls.getComponentType();
        QName componentQName = context.getQNameForClass(componentType);
        if (componentQName == null)
            throw new IOException("No mapped schema type for " + componentType.getName());
        String prefix = context.getPrefixForURI(componentQName.getNamespaceURI());
        String arrayType = prefix + ":" + componentQName.getLocalPart();
        int len = Array.getLength(value);
        
        arrayType += "[" + len + "]";
        
        Attributes attrs = attributes;
        
        if (attributes != null &&
            attributes.getIndex(Constants.URI_SOAP_ENC,
                                Constants.ATTR_ARRAY_TYPE) == -1) {
            context.registerPrefixForURI("SOAP-ENC", Constants.URI_SOAP_ENC);
            AttributesImpl attrImpl = new AttributesImpl(attributes);
            attrImpl.addAttribute(Constants.URI_SOAP_ENC, 
                                  Constants.ATTR_ARRAY_TYPE,
                                  "SOAP-ENC:arrayType",
                                  "CDATA",
                                  arrayType);
            attrs = attrImpl;
        }
        
        context.startElement(name, attrs);
        
        for (int index = 0; index < len; index++)
            context.serialize(new QName("","item"), null, Array.get(value, index));
        
        context.endElement();
    }
}
