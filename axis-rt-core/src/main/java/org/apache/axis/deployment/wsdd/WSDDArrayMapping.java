/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import java.io.IOException;
import javax.xml.namespace.QName;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.xml.sax.helpers.AttributesImpl;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.XMLUtils;


/**
 * A <code>WSDDArrayMapping</code> is simply a <code>WSDDTypeMapping</code>
 * which has preset values for the serializer and deserializer attributes.
 *
 * This enables the following slightly simplified syntax when expressing
 * an array mapping:
 *
 * &lt;arrayMapping qname="prefix:local" languageSpecificType="java:class" innerType="prefix:local"/&gt;
 *
 */
public class WSDDArrayMapping extends WSDDTypeMapping {

    /** array item type */
    private QName innerType = null;

    /**
     * Default constructor
     */
    public WSDDArrayMapping() {
    }

    public WSDDArrayMapping(Element e) throws WSDDException {
        super(e);
        Attr innerTypeAttr = e.getAttributeNode(ATTR_INNER_TYPE);
        if (innerTypeAttr != null) {
            String qnameStr = innerTypeAttr.getValue();
            innerType = XMLUtils.getQNameFromString(qnameStr, e);
        }
        serializer = ARRAY_SERIALIZER_FACTORY;
        deserializer = ARRAY_DESERIALIZER_FACTORY;
    }

    protected QName getElementName() {
        return QNAME_ARRAYMAPPING;
    }

    /**
     * @return Returns the innerType.
     */
    public QName getInnerType() {
        return innerType;
    }

    public void writeToContext(SerializationContext context) throws IOException {
        AttributesImpl attrs = new AttributesImpl();

        String typeStr = context.qName2String(typeQName);
        attrs.addAttribute("", ATTR_LANG_SPEC_TYPE, ATTR_LANG_SPEC_TYPE, "CDATA", typeStr);

        String qnameStr = context.qName2String(qname);
        attrs.addAttribute("", ATTR_QNAME, ATTR_QNAME, "CDATA", qnameStr);

        String innerTypeStr = context.qName2String(innerType);
        attrs.addAttribute("", ATTR_INNER_TYPE, ATTR_INNER_TYPE, "CDATA", innerTypeStr);

        context.startElement(QNAME_ARRAYMAPPING, attrs);
        context.endElement();
    }
}



