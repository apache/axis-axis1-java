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
package test.encoding;

import org.apache.axis.description.AttributeDesc;
import org.apache.axis.description.FieldDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.SimpleType;

import javax.xml.namespace.QName;

/**
 * A simple type with attributes for testing
 */
public class SimpleBean implements SimpleType {
    public String value;  // Our "actual" value
    public float temp;   // An attribute

    private static TypeDesc typeDesc = new TypeDesc(SimpleBean.class);
    static {
        FieldDesc fd = new AttributeDesc();
        fd.setFieldName("temp");
        fd.setXmlName(new QName("foo", "temp"));
        typeDesc.addFieldDesc(fd);
    }
    public static TypeDesc getTypeDesc() { return typeDesc; }

    /**
     * String constructor
     */
    public SimpleBean(String val)
    {
        value = val;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public String toString() {
        return value;
    }

    public boolean equals(Object obj)
    {
        if (obj == null || !(obj instanceof SimpleBean))
            return false;
        SimpleBean other = (SimpleBean)obj;
        if (other.getTemp() != temp) {
            return false;
        }
        if (value == null) {
            return other.getValue() == null;
        }
        return value.equals(other.getValue());
    }
}
