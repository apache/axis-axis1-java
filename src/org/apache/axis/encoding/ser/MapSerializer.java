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

import org.apache.axis.Constants;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.fromJava.Types;
import org.apache.commons.logging.Log;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * A <code>MapSerializer</code> is be used to serialize and
 * deserialize Maps using the <code>SOAP-ENC</code>
 * encoding style.<p>
 *
 * @author Glen Daniels (gdaniels@apache.org)
 * Modified by @author Rich Scheuerle (scheu@us.ibm.com)
 */

public class MapSerializer implements Serializer
{
    protected static Log log =
        LogFactory.getLog(MapSerializer.class.getName());

    // QNames we deal with
    private static final QName QNAME_KEY = new QName("","key");
    private static final QName QNAME_ITEM = new QName("", "item");
    private static final QName QNAME_VALUE = new QName("", "value");
    private static final QName QNAME_ITEMTYPE = new QName(Constants.NS_URI_XMLSOAP, "item");    

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
            throw new IOException(
                Messages.getMessage("noMap00", "MapSerializer", value.getClass().getName()));

        Map map = (Map)value;

        context.startElement(name, attributes);
        
        AttributesImpl itemsAttributes = new AttributesImpl();
        String encodingURI = context.getMessageContext().getSOAPConstants().getEncodingURI();
        String encodingPrefix = context.getPrefixForURI(encodingURI);
        String soapPrefix = context.getPrefixForURI(Constants.SOAP_MAP.getNamespaceURI());
        itemsAttributes.addAttribute(encodingURI, "type", encodingPrefix + ":type",
                                   "CDATA", encodingPrefix + ":Array");        
        itemsAttributes.addAttribute(encodingURI, "arrayType", encodingPrefix + ":arrayType",
                                   "CDATA", soapPrefix + ":item["+map.size()+"]");        

        for (Iterator i = map.entrySet().iterator(); i.hasNext(); )
        {
            Map.Entry entry = (Map.Entry) i.next();
            Object key = entry.getKey();
            Object val = entry.getValue();

            context.startElement(QNAME_ITEM, null);

            context.serialize(QNAME_KEY,   null, key);
            context.serialize(QNAME_VALUE, null, val);

            context.endElement();
        }

        context.endElement();
    }

    public String getMechanismType() { return Constants.AXIS_SAX; }

    /**
     * Return XML schema for the specified type, suitable for insertion into
     * the &lt;types&gt; element of a WSDL document, or underneath an
     * &lt;element&gt; or &lt;attribute&gt; declaration.
     *
     * @param javaType the Java Class we're writing out schema for
     * @param types the Java2WSDL Types object which holds the context
     *              for the WSDL being generated.
     * @return a type element containing a schema simpleType/complexType
     * @see org.apache.axis.wsdl.fromJava.Types
     */
    public Element writeSchema(Class javaType, Types types) throws Exception {
        Element complexType = types.createElement("complexType");
        complexType.setAttribute("name", "Map");
        Element seq = types.createElement("sequence");
        complexType.appendChild(seq);
        Element element = types.createElement("element");
        element.setAttribute("name", "item");
        element.setAttribute("minOccurs", "0");
        element.setAttribute("maxOccurs", "unbounded");
        element.setAttribute("type", types.getQNameString(new QName(Constants.NS_URI_XMLSOAP,"mapItem")));
        seq.appendChild(element);
    
        Element itemType = types.createElement("complexType");
        itemType.setAttribute("name", "mapItem");
        Element seq2 = types.createElement("sequence");
        itemType.appendChild(seq2);
        Element element2 = types.createElement("element");
        element2.setAttribute("name", "key");
        element2.setAttribute("nillable", "true");
        element2.setAttribute("type", "xsd:anyType");
        seq2.appendChild(element2);
        Element element3 = types.createElement("element");
        element3.setAttribute("name", "value");
        element3.setAttribute("nillable", "true");
        element3.setAttribute("type", "xsd:anyType");
        seq2.appendChild(element3);
        types.writeSchemaTypeDecl(QNAME_ITEMTYPE, itemType);

        return complexType;
    }
}
