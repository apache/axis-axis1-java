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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.apache.axis.utils.XMLUtils;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.encoding.SerializationContext;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.rpc.namespace.QName;
import java.io.IOException;


/**
 *
 */
public class WSDDTypeMapping
    extends WSDDElement
{
    private QName qname;
    private String serializer;
    private String deserializer;
    private QName typeQName;
    private String ref;
    private String encodingStyle;
    
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
        super(e);
        
        serializer = e.getAttribute("serializer");
        deserializer = e.getAttribute("deserializer");
        
        String qnameStr = e.getAttribute("qname");
        qname = XMLUtils.getQNameFromString(qnameStr, e);
        
        String typeStr = e.getAttribute("languageSpecificType");
        typeQName = XMLUtils.getQNameFromString(typeStr, e);        
    }

    /**
     * Write this element out to a SerializationContext
     */
    public void writeToContext(SerializationContext context)
            throws IOException {
        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("", "serializer", "serializer", "CDATA", serializer);
        attrs.addAttribute("", "deserializer", "deserializer", "CDATA", deserializer);

        String typeStr = context.qName2String(typeQName);
        attrs.addAttribute("", "languageSpecificType", 
                           "languageSpecificType", "CDATA", typeStr);
        
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
            
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            return cl.loadClass(typeQName.getLocalPart());
        }
        
        throw new ClassNotFoundException(JavaUtils.getMessage("noTypeQName00"));
    }

    /**
     *
     * @param lsType XXX
     */
    public void setLanguageSpecificType(Class lsType)
    {
        String type = lsType.getName();
        typeQName = new QName(WSDDConstants.WSDD_JAVA, type);
    }

    /**
     *
     * @return XXX
     * @throws ClassNotFoundException XXX
     */
    public Class getSerializer()
        throws ClassNotFoundException
    {
        return Class.forName(serializer);
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
     *
     * @return XXX
     * @throws ClassNotFoundException XXX
     */
    public Class getDeserializer()
        throws ClassNotFoundException
    {
        return Class.forName(deserializer);
    }

    /**
     *
     * @param deser XXX
     */
    public void setDeserializer(Class deser)
    {
        deserializer = deser.getName();
    }
}



