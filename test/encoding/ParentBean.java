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
package test.encoding;

import org.apache.axis.description.AttributeDesc;
import org.apache.axis.description.ElementDesc;
import org.apache.axis.description.FieldDesc;
import org.apache.axis.description.TypeDesc;

import javax.xml.namespace.QName;

/**
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class ParentBean {
    private float parentFloat; // attribute
    private String parentStr;  // element

    public float getParentFloat() {
        return parentFloat;
    }

    public void setParentFloat(float parentFloat) {
        this.parentFloat = parentFloat;
    }

    public String getParentStr() {
        return parentStr;
    }

    public void setParentStr(String parentStr) {
        this.parentStr = parentStr;
    }

    // Type metadata
    private static TypeDesc typeDesc;
    
    static {
        typeDesc = new TypeDesc(ParentBean.class);
        FieldDesc field;

        // An attribute with a specified QName
        field = new AttributeDesc();
        field.setFieldName("parentFloat");
        field.setXmlName(new QName("", "parentAttr"));
        typeDesc.addFieldDesc(field);

        // An element with a specified QName
        field = new ElementDesc();
        field.setFieldName("parentStr");
        field.setXmlName(new QName("", "parentElement"));
        typeDesc.addFieldDesc(field);
    }
    
    public static TypeDesc getTypeDesc()
    {
        return typeDesc;
    }
}
