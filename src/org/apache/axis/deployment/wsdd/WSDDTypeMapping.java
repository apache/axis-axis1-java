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
package org.apache.axis.deployment.wsdd;

import org.apache.axis.Constants;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.rpc.namespace.QName;
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
        serializer = e.getAttribute("serializer");
        deserializer = e.getAttribute("deserializer");
        Attr attrNode = e.getAttributeNode("encodingStyle");

        if (attrNode == null) {
            encodingStyle = Constants.URI_CURRENT_SOAP_ENC;
        } else {
            encodingStyle = attrNode.getValue();
        }

        String qnameStr = e.getAttribute("qname");
        qname = XMLUtils.getQNameFromString(qnameStr, e);

        // JSR 109 v0.093 indicates that this attribute is named "type"

        String typeStr = e.getAttribute("type");
        typeQName = XMLUtils.getQNameFromString(typeStr, e);
        if (typeStr == null || typeStr.equals("")) {
            typeStr = e.getAttribute("languageSpecificType");
            typeQName = XMLUtils.getQNameFromString(typeStr, e);
        }
    }

    /**
     * Write this element out to a SerializationContext
     */
    public void writeToContext(SerializationContext context)
            throws IOException {
        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("", "encodingStyle", "encodingStyle", "CDATA", encodingStyle);
        attrs.addAttribute("", "serializer", "serializer", "CDATA", serializer);
        attrs.addAttribute("", "deserializer", "deserializer", "CDATA", deserializer);

        String typeStr = context.qName2String(typeQName);
        // JSR 109 indicates that the name of this field is type
        attrs.addAttribute("", "type", 
                           "type", "CDATA", typeStr);
        
        String qnameStr = context.qName2String(qname);
        attrs.addAttribute("", "qname", "qname", "CDATA", qnameStr);
        
        context.startElement(WSDDConstants.TYPE_QNAME, attrs);
        context.endElement();
    }

    protected QName getElementName() {
        return WSDDConstants.TYPE_QNAME;
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
            if (!WSDDConstants.WSDD_JAVA.equals(typeQName.getNamespaceURI())) {
                throw new ClassNotFoundException(JavaUtils.
                             getMessage("badTypeNamespace00",
                                        typeQName.getNamespaceURI(),
                                        WSDDConstants.WSDD_JAVA));
            }
            String loadName = JavaUtils.getLoadableClassName(typeQName.getLocalPart());
            if (JavaUtils.getWrapper(loadName) != null) {
                // We're
            }
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            return Class.forName(loadName, true, cl);
        }
        
        throw new ClassNotFoundException(JavaUtils.getMessage("noTypeQName00"));
    }

    /**
     * Set javaType (type= attribute or languageSpecificType= attribute)
     * @param javaType the class of the javaType
     */
    public void setLanguageSpecificType(Class javaType)
    {
        String type = javaType.getName();
        typeQName = new QName(WSDDConstants.WSDD_JAVA, type);
    }

    /**
     * Set javaType (type= attribute or languageSpecificType= attribute)
     * @param javaType is the name of the class.  (For arrays this
     * could be the form my.Foo[] or could be in the form [Lmy.Foo;
     */
    public void setLanguageSpecificType(String javaType)
    {
        typeQName = new QName(WSDDConstants.WSDD_JAVA, javaType);
    }

    /**
     *
     * @return XXX
     * @throws ClassNotFoundException XXX
     */
    public Class getSerializer()
        throws ClassNotFoundException
    {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return Class.forName(serializer, true, cl);
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
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return Class.forName(deserializer, true, cl);
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



