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
            if (company != null) {
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
