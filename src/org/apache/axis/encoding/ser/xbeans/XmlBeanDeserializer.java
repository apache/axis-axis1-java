/*
 * XmlBeanDeserializer.java
 * 
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
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
 * 
 * 
 * Original author: Jonathan Colwell
 */
package org.apache.axis.encoding.ser.xbeans;

import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.DeserializerImpl;
import org.apache.axis.message.MessageElement;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;

/**
 * Class XmlBeanDeserializer
 * @author Jonathan Colwell
 */
public class XmlBeanDeserializer extends DeserializerImpl {

    private Class mJavaType;
    private QName mXmlType;

    public XmlBeanDeserializer(Class javaType, QName xmlType) {
        mJavaType = javaType;
        mXmlType = xmlType;
    }

    public void onStartElement(String namespace, String localName,
                               String prefix, Attributes attributes,
                               DeserializationContext context)
            throws SAXException {
        try {
            MessageElement me = context.getCurElement();
            XmlOptions opts = new XmlOptions()
                    .setLoadReplaceDocumentElement(null);
            XmlObject xObj = XmlObject.Factory.parse(me, opts);
            SchemaType st = xObj.schemaType();
            SchemaType jt = (SchemaType) mJavaType.getField("type").get(null);
            XmlObject converted = xObj.changeType(jt);
            if (converted != null) {
                setValue(converted);
            } else {
                XmlObject[] children = xObj.selectChildren(QNameSet.ALL);
                for (int j = 0; j < children.length; j++) {
                    st = children[j].schemaType();
                    converted = xObj.changeType(jt);
                    if (converted != null) {
                        setValue(converted);
                        break;
                    }
                }
            }
        } catch (Exception xe) {
            throw new SAXException(xe);
        }
    }
}
