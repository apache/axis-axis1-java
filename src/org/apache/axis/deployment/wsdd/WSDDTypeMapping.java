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
package org.apache.axis.deployment.wsdd;

import org.apache.axis.Constants;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.namespace.QName;
import java.io.IOException;


/**
 *
 */
public class WSDDTypeMapping
    extends WSDDElement
{
    protected QName qname = null;
    protected String serializer = null;
    protected String deserializer = null;
    protected QName typeQName = null;
    protected String ref = null;
    protected String encodingStyle = null;
    
    /**
     * Default constructor
     * 
     */ 
    public WSDDTypeMapping()
    {
    }
    
    /**
     *
     * @param e (Element) XXX
     * @throws WSDDException XXX
     */
    public WSDDTypeMapping(Element e)
        throws WSDDException
    {
        serializer = e.getAttribute(ATTR_SERIALIZER);
        deserializer = e.getAttribute(ATTR_DESERIALIZER);
        Attr attrNode = e.getAttributeNode(ATTR_ENCSTYLE);

        if (attrNode == null) {
            encodingStyle = Constants.URI_DEFAULT_SOAP_ENC;
        } else {
            encodingStyle = attrNode.getValue();
        }

        String qnameStr = e.getAttribute(ATTR_QNAME);
        qname = XMLUtils.getQNameFromString(qnameStr, e);

        // JSR 109 v0.093 indicates that this attribute is named "type"

        String typeStr = e.getAttribute(ATTR_TYPE);
        typeQName = XMLUtils.getQNameFromString(typeStr, e);
        if (typeStr == null || typeStr.equals("")) {
            typeStr = e.getAttribute(ATTR_LANG_SPEC_TYPE);
            typeQName = XMLUtils.getQNameFromString(typeStr, e);
        }
    }

    /**
     * Write this element out to a SerializationContext
     */
    public void writeToContext(SerializationContext context)
            throws IOException {
        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("", ATTR_ENCSTYLE, ATTR_ENCSTYLE, "CDATA", encodingStyle);
        attrs.addAttribute("", ATTR_SERIALIZER, ATTR_SERIALIZER, "CDATA", serializer);
        attrs.addAttribute("", ATTR_DESERIALIZER, ATTR_DESERIALIZER, "CDATA", deserializer);

        String typeStr = context.qName2String(typeQName);
        // JSR 109 indicates that the name of this field is type
        attrs.addAttribute("", ATTR_TYPE, ATTR_TYPE,
                           "CDATA", typeStr);
        
        String qnameStr = context.qName2String(qname);
        attrs.addAttribute("", ATTR_QNAME, ATTR_QNAME, "CDATA", qnameStr);
        
        context.startElement(QNAME_TYPEMAPPING, attrs);
        context.endElement();
    }

    protected QName getElementName() {
        return QNAME_TYPEMAPPING;
    }

    /**
     *
     * @return XXX
     */
    public String getRef()
    {
        return ref;
    }

    /**
     *
     * @param ref XXX
     */
    public void setRef(String ref)
    {
        this.ref = ref;
    }

    /**
     *
     * @return XXX
     */
    public String getEncodingStyle()
    {
        return encodingStyle;
    }

    /**
     *
     * @param es XXX
     */
    public void setEncodingStyle(String es)
    {
        encodingStyle = es;
    }

    /**
     *
     * @return XXX
     */
    public QName getQName()
    {
        return qname;
    }

    /**
     *
     * @param name XXX
     */
    public void setQName(QName name)
    {
        qname = name;
    }

    /**
     *
     * @return XXX
     * @throws ClassNotFoundException XXX
     */
    public Class getLanguageSpecificType()
        throws ClassNotFoundException
    {
        if (typeQName != null) {
            if (!URI_WSDD_JAVA.equals(typeQName.getNamespaceURI())) {
                throw new ClassNotFoundException(Messages.getMessage("badTypeNamespace00",
                                        typeQName.getNamespaceURI(),
                                        URI_WSDD_JAVA));
            }
            String loadName = JavaUtils.getLoadableClassName(typeQName.getLocalPart());
            if (JavaUtils.getWrapper(loadName) != null) {
                // in case of a primitive type by use its corresponding wrapper class. 
                loadName = "java.lang." + JavaUtils.getWrapper(loadName);
            }
            return ClassUtils.forName(loadName);
        }
        
        throw new ClassNotFoundException(Messages.getMessage("noTypeQName00"));
    }

    /**
     * Set javaType (type= attribute or languageSpecificType= attribute)
     * @param javaType the class of the javaType
     */
    public void setLanguageSpecificType(Class javaType)
    {
        String type = javaType.getName();
        typeQName = new QName(URI_WSDD_JAVA, type);
    }

    /**
     * Set javaType (type= attribute or languageSpecificType= attribute)
     * @param javaType is the name of the class.  (For arrays this
     * could be the form my.Foo[] or could be in the form [Lmy.Foo;
     */
    public void setLanguageSpecificType(String javaType)
    {
        typeQName = new QName(URI_WSDD_JAVA, javaType);
    }

    /**
     *
     * @return XXX
     * @throws ClassNotFoundException XXX
     */
    public Class getSerializer()
        throws ClassNotFoundException
    {
        return ClassUtils.forName(serializer);
    }

    /**
     *
     * @return serializer factory name
     */
    public String getSerializerName()
    {
        return serializer;
    }
    /**
     *
     * @param ser XXX
     */
    public void setSerializer(Class ser)
    {
        serializer = ser.getName();
    }

    /**
     * Set the serializer factory name
     * @param ser name of the serializer factory class
     */
    public void setSerializer(String ser)
    {
        serializer = ser;
    }

    /**
     *
     * @return XXX
     * @throws ClassNotFoundException XXX
     */
    public Class getDeserializer()
        throws ClassNotFoundException
    {
        return ClassUtils.forName(deserializer);
    }

    /**
     *
     * @return deserializer factory name
     */
    public String getDeserializerName()
    {
        return deserializer;
    }

    /**
     *
     * @param deser XXX
     */
    public void setDeserializer(Class deser)
    {
        deserializer = deser.getName();
    }

    /**
     * Set the deserializer factory name
     * @param deser name of the deserializer factory class
     */
    public void setDeserializer(String deser)
    {
        deserializer = deser;
    }
}



