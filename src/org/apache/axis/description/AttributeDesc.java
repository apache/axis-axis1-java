/*
 * Copyright 2002-2004 The Apache Software Foundation.
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
package org.apache.axis.description;

import javax.xml.namespace.QName;

/**
 * An AttributeDesc is a FieldDesc for an Java field mapping to an
 * XML attribute
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class AttributeDesc extends FieldDesc {
    public AttributeDesc() {
        super(false);
    }

    /**
     * Set the XML attribute's name, without giving it a namespace.
     *
     * This is the most common usage for AttributeDescs.
     */
    public void setAttributeName(String name)
    {
        setXmlName(new QName("", name));
    }
}
