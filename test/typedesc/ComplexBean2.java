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
package test.typedesc;

import org.apache.axis.description.AttributeDesc;
import org.apache.axis.description.FieldDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.SimpleType;

import javax.xml.namespace.QName;

public class ComplexBean2 extends SimpleBean2 implements java.io.Serializable {

    public String c;
    public String d;
    public String e;

    private static TypeDesc typeDesc = new TypeDesc(ComplexBean2.class);
    static {
        FieldDesc fd = new AttributeDesc();
        fd.setFieldName("c");
        fd.setXmlName(new QName("foo", "c"));
        typeDesc.addFieldDesc(fd);

        fd = new AttributeDesc();
        fd.setFieldName("d");
        fd.setXmlName(new QName("foo", "d"));
        typeDesc.addFieldDesc(fd);

        fd = new AttributeDesc();
        fd.setFieldName("e");
        ((AttributeDesc)fd).setAttributeName("ffffff");
        fd.setXmlName(new QName("foo", "e"));
        typeDesc.addFieldDesc(fd);
    }
    public static TypeDesc getTypeDesc() { return typeDesc; }

    public ComplexBean2() {}

    public void setC(String value) {
    }

    public String getC() {
        return null;
    }

    public void setD(String value) {
    }

    public String getD() {
        return null;
    }

    public void setE(String value) {
    }

    public String getE() {
        return null;
    }

}
