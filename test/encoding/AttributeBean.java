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
 * Simple Java Bean with fields that should be serialized as attributes
 */ 
public class AttributeBean extends ParentBean {
    private int age;
    private float iD;
    public  String company;         // element without getter/setter 
    private java.lang.String name;  // attribute
    private boolean male;           // attribute
    
    public AttributeBean() {}
    
    public AttributeBean(int age, float iD, String name, String company, boolean male) {
        this.age = age;
        this.iD = iD;
        this.name = name;
        this.male = male;
        this.company = company;
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    public float getID() {
        return iD;
    }
    
    public void setID(float iD) {
        this.iD = iD;
    }
    
    public java.lang.String getName() {
        return name;
    }
    
    public void setName(java.lang.String name) {
        this.name = name;
    }
    
    public boolean getMale() {
        return male;
    }
    
    public void setMale(boolean male) {
        this.male = male;
    }

    public boolean equals(Object obj)
    {
        if (obj == null || !(obj instanceof AttributeBean))
            return false;
        AttributeBean other = (AttributeBean)obj;
        if (other.getAge() != age)
            return false;
        if (other.getID() != iD)
            return false;
        if (other.getMale() != male)
            return false;
        if (name == null) {
            if (other.getName() != null) {
                return false;
            }
        }else if (!name.equals(other.getName())) {
            return false;
        }
        if (company == null) {
            if (other.company != null) {
                return false;
            }
        } else if (!company.equals(other.company)) {
            return false;
        }
        if (getParentFloat() != other.getParentFloat())
            return false;
        if (getParentStr() != null) {
            return getParentStr().equals(other.getParentStr());
        }
        return other.getParentStr() == null;
    }

    // Type metadata
    private static TypeDesc typeDesc;
    
    static {
        typeDesc = new TypeDesc(AttributeBean.class);
        FieldDesc field;

        // An attribute with a specified QName
        field = new AttributeDesc();
        field.setFieldName("name");
        field.setXmlName(new QName("foo", "nameAttr"));
        typeDesc.addFieldDesc(field);

        // An attribute with a default QName
        field = new AttributeDesc();
        field.setFieldName("male");
        typeDesc.addFieldDesc(field);

        // An element with a specified QName
        field = new ElementDesc();
        field.setFieldName("age");
        field.setXmlName(new QName("foo", "ageElement"));
        typeDesc.addFieldDesc(field);
    }
    
    public static TypeDesc getTypeDesc()
    {
        return typeDesc;
    }
}
