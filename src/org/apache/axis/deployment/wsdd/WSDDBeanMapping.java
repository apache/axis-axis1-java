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

import org.apache.axis.encoding.SerializationContext;
import org.w3c.dom.Element;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.namespace.QName;
import java.io.IOException;


/**
 * A <code>WSDDBeanMapping</code> is simply a <code>WSDDTypeMapping</code>
 * which has preset values for the serializer and deserializer attributes.
 *
 * This enables the following slightly simplified syntax when expressing
 * a bean mapping:
 *
 * &lt;beanMapping qname="prefix:local" languageSpecificType="java:class"/&gt;
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class WSDDBeanMapping
    extends WSDDTypeMapping
{
    /**
     * Default constructor
     * 
     */ 
    public WSDDBeanMapping()
    {
    }
    
    public WSDDBeanMapping(Element e)
        throws WSDDException
    {
        super(e);
        
        serializer = BEAN_SERIALIZER_FACTORY;
        deserializer = BEAN_DESERIALIZER_FACTORY;
        encodingStyle = null;
    }

    protected QName getElementName() {
        return QNAME_BEANMAPPING;
    }

    public void writeToContext(SerializationContext context) throws IOException {
        AttributesImpl attrs = new AttributesImpl();

        String typeStr = context.qName2String(typeQName);
        attrs.addAttribute("", ATTR_LANG_SPEC_TYPE, 
                           ATTR_LANG_SPEC_TYPE, "CDATA", typeStr);

        String qnameStr = context.qName2String(qname);
        attrs.addAttribute("", ATTR_QNAME, ATTR_QNAME, "CDATA", qnameStr);

        context.startElement(WSDDConstants.QNAME_BEANMAPPING, attrs);
        context.endElement();
    }
}



